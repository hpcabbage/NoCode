package com.yuaicodemother.core;


import com.yuaicodemother.Exception.BusinessException;
import com.yuaicodemother.Exception.ErrorCode;
import com.yuaicodemother.ai.AiCodeGeneratorService;
import com.yuaicodemother.ai.enums.CodeGenTypeEnum;
import com.yuaicodemother.ai.model.HtmlCodeResult;
import com.yuaicodemother.ai.model.MultiFileCodeResult;
import com.yuaicodemother.parser.CodeParserExecutor;
import com.yuaicodemother.save.CodeFileSaveExecutor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

@Service
@Slf4j
public class AiCodeGeneratorFacade {
    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;

    /**
     * 统一入口：根据类型生成并保存代码 8ue
     * @param userMessage
     * @param codeGenTypeEnum
     * @return
     */
    public File generateAndSaveCode(String userMessage,CodeGenTypeEnum codeGenTypeEnum) {
        if(codeGenTypeEnum == null) throw new BusinessException(ErrorCode.PARAMS_ERROR,"生成类型不能为空");
        return switch(codeGenTypeEnum) {
            case HTML-> {
                HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode(userMessage);
                yield CodeFileSaveExecutor.executeSave(result.getHtmlCode(),codeGenTypeEnum);
            }
            case MULTI_FILE-> {
                MultiFileCodeResult result = aiCodeGeneratorService.generateMultiFileCode(userMessage);
                yield CodeFileSaveExecutor.executeSave(result.getHtmlCode(),codeGenTypeEnum);
            }
            default -> {
                String errmessage = "不支持的生成类型:" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.PARAMS_ERROR,errmessage);
            }
        };
    }

    public Flux<String> processCodeStream(Flux<String> codeStream,CodeGenTypeEnum codeGenTypeEnum) {
        StringBuilder codeBuilder = new StringBuilder();
        return codeStream.doOnNext(chunk ->{
            // 实时收集代码片段
            codeBuilder.append(chunk);
        }).doOnComplete(() -> {
            //流式返回完成保存代码
            try {
                String completeCode = codeBuilder.toString();
                // 使用执行器解析代码
                Object parseResult = CodeParserExecutor.executeParser(completeCode,codeGenTypeEnum);
                // 使用执行器保存代码
                File savaDir = CodeFileSaveExecutor.executeSave(parseResult,codeGenTypeEnum);
                log.info("代码保存成功，保存路径为：{}",savaDir.getAbsolutePath());
            } catch (Exception e) {
                log.error("代码保存失败：{}",e.getMessage());
            }
        });
    }

     public Flux<String> generateAndSaveCodeStream(String userMessage,CodeGenTypeEnum codeGenTypeEnum) {
        if(codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"生成类型不能为空");
        }
        return switch(codeGenTypeEnum) {
            case HTML -> {
                Flux<String> result = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
                yield processCodeStream(result,codeGenTypeEnum);
            }
            case MULTI_FILE -> {
                Flux<String> result = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
                yield processCodeStream(result,codeGenTypeEnum);
            }
            default -> {
                String errmessage = "不支持的生成类型:" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.PARAMS_ERROR,errmessage);
            }
        };
     }
}
