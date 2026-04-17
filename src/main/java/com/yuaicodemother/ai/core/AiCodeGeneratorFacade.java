package com.yuaicodemother.ai.core;


import cn.hutool.json.JSONUtil;
import com.yuaicodemother.ai.AiCodeGeneratorServiceFactory;
import com.yuaicodemother.ai.model.message.AiResponseMessage;
import com.yuaicodemother.ai.model.message.ToolExecutedMessage;
import com.yuaicodemother.ai.model.message.ToolRequestMessage;
import com.yuaicodemother.constant.AppConstant;
import com.yuaicodemother.exception.BusinessException;
import com.yuaicodemother.exception.ErrorCode;
import com.yuaicodemother.ai.AiCodeGeneratorService;
import com.yuaicodemother.ai.enums.CodeGenTypeEnum;
import com.yuaicodemother.ai.model.HtmlCodeResult;
import com.yuaicodemother.ai.model.MultiFileCodeResult;
import com.yuaicodemother.ai.parser.CodeParserExecutor;
import com.yuaicodemother.ai.save.CodeFileSaveExecutor;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecution;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class AiCodeGeneratorFacade {
    private static final String HTML_PROMPT_PATH = "prompt/codegen-html-system-prompt.txt";
    private static final String MULTI_FILE_PROMPT_PATH = "prompt/codegen-multi-file-system-prompt.txt";

    @Resource
    private AiCodeGeneratorServiceFactory aiCodeGeneratorServiceFactory;
    @Resource
    private VueProjectBuilder vueProjectBuilder;
    /**
     * 将 TokenStream 转换为 Flux<String>，并传递工具调用信息
     *
     * @param tokenStream TokenStream 对象
     * @return Flux<String> 流式响应
     */
    private Flux<String> processTokenStream(TokenStream tokenStream,Long appId) {
        return Flux.create(sink -> {
            tokenStream.onPartialResponse((String partialResponse) -> {
                        AiResponseMessage aiResponseMessage = new AiResponseMessage(partialResponse);
                        sink.next(JSONUtil.toJsonStr(aiResponseMessage));
                    })
                    .onPartialToolExecutionRequest((index, toolExecutionRequest) -> {
                        ToolRequestMessage toolRequestMessage = new ToolRequestMessage(toolExecutionRequest);
                        sink.next(JSONUtil.toJsonStr(toolRequestMessage));
                    })
                    .onToolExecuted((ToolExecution toolExecution) -> {
                        ToolExecutedMessage toolExecutedMessage = new ToolExecutedMessage(toolExecution);
                        sink.next(JSONUtil.toJsonStr(toolExecutedMessage));
                    })
                    .onCompleteResponse((ChatResponse response) -> {
                        String projectPath = AppConstant.CODE_OUTPUT_ROOT_DIR + "/vue_project_" + appId;
                        vueProjectBuilder.buildProject(projectPath);
                        sink.complete();
                    })
                    .onError((Throwable error) -> {
                        error.printStackTrace();
                        sink.error(error);
                    })
                    .start();
        });
    }



    /**
     * 统一入口：根据类型生成并保存代码
     * @param userMessage
     * @param codeGenTypeEnum
     * @return
     */
    public File generateAndSaveCode(String userMessage,CodeGenTypeEnum codeGenTypeEnum,Long appId) {
        AiCodeGeneratorService aiCodeGeneratorService =
                aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId,codeGenTypeEnum);
        if(codeGenTypeEnum == null) throw new BusinessException(ErrorCode.PARAMS_ERROR,"生成类型不能为空");
        return switch(codeGenTypeEnum) {
            case HTML-> {
                HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode(userMessage);
                yield CodeFileSaveExecutor.executeSave(result.getHtmlCode(),codeGenTypeEnum,appId);
            }
            case MULTI_FILE-> {
                MultiFileCodeResult result = aiCodeGeneratorService.generateMultiFileCode(userMessage);
                yield CodeFileSaveExecutor.executeSave(result.getHtmlCode(),codeGenTypeEnum,appId);
            }
            default -> {
                String errmessage = "不支持的生成类型:" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.PARAMS_ERROR,errmessage);
            }
        };
    }

    /**
     * 通用流式代码处理方法
     *
     * @param codeStream  代码流
     * @param codeGenType 代码生成类型
     * @return 流式响应
     */
    private Flux<String> processCodeStream(Flux<String> codeStream, CodeGenTypeEnum codeGenType,Long appId) {
        StringBuilder codeBuilder = new StringBuilder();
        return codeStream.doOnNext(chunk -> {
            // 实时收集代码片段
            codeBuilder.append(chunk);
        }).doOnComplete(() -> saveGeneratedCode(codeBuilder.toString(), codeGenType, appId));
    }

    private void saveGeneratedCode(String completeCode, CodeGenTypeEnum codeGenType, Long appId) {
        try {
            Object parsedResult = CodeParserExecutor.executeParser(completeCode, codeGenType);
            File savedDir = CodeFileSaveExecutor.executeSave(parsedResult, codeGenType, appId);
            log.info("保存成功，路径为：{}", savedDir.getAbsolutePath());
        } catch (Exception e) {
            log.error("保存失败", e);
            throw new RuntimeException(e);
        }
    }

    private String loadPrompt(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            byte[] bytes = resource.getInputStream().readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("加载提示词失败: " + path, e);
        }
    }

    private Flux<String> processManualStreamingChat(String userMessage, CodeGenTypeEnum codeGenType, Long appId) {
        String systemPrompt = switch (codeGenType) {
            case HTML -> loadPrompt(HTML_PROMPT_PATH);
            case MULTI_FILE -> loadPrompt(MULTI_FILE_PROMPT_PATH);
            default -> throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的流式生成类型");
        };

        StreamingChatModel streamingChatModel = aiCodeGeneratorServiceFactory.getStreamingChatModel();
        ChatRequest chatRequest = ChatRequest.builder()
                .messages(
                        SystemMessage.from(systemPrompt),
                        UserMessage.from(userMessage)
                )
                .build();

        return Flux.create(sink -> {
            StringBuilder codeBuilder = new StringBuilder();
            streamingChatModel.chat(chatRequest, new StreamingChatResponseHandler() {
                @Override
                public void onPartialResponse(String partialResponse) {
                    codeBuilder.append(partialResponse);
                    sink.next(JSONUtil.toJsonStr(new AiResponseMessage(partialResponse)));
                }

                @Override
                public void onCompleteResponse(ChatResponse completeResponse) {
                    try {
                        saveGeneratedCode(codeBuilder.toString(), codeGenType, appId);
                        sink.complete();
                    } catch (Exception e) {
                        sink.error(e);
                    }
                }

                @Override
                public void onError(Throwable error) {
                    sink.error(error);
                }
            });
        });
    }


    public Flux<String> generateAndSaveCodeStream(String userMessage,CodeGenTypeEnum codeGenTypeEnum,Long appId) {
        AiCodeGeneratorService aiCodeGeneratorService =
                aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId,codeGenTypeEnum);
        if(codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"生成类型不能为空");
        }
        return switch(codeGenTypeEnum) {
            case VUE -> {
                TokenStream tokenStream = aiCodeGeneratorService.generateVueProjectCodeStream(appId,userMessage);
                yield processTokenStream(tokenStream,appId);
            }
            case HTML, MULTI_FILE -> processManualStreamingChat(userMessage, codeGenTypeEnum, appId);
            default -> {
                String errmessage = "不支持的生成类型:" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.PARAMS_ERROR,errmessage);
            }
        };
     }
}
