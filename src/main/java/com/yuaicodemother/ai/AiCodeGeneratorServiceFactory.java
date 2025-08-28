package com.yuaicodemother.ai;

import cn.hutool.core.bean.BeanUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.yuaicodemother.ai.enums.CodeGenTypeEnum;
import com.yuaicodemother.ai.tool.FileWriteTool;
import com.yuaicodemother.exception.BusinessException;
import com.yuaicodemother.exception.ErrorCode;
import com.yuaicodemother.service.ChatHistoryService;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.time.Duration;

@Configuration
@Slf4j
public class AiCodeGeneratorServiceFactory {
    @Resource
    private ChatModel chatModel;
    @Resource
    private StreamingChatModel openAiStreamingChatModel;
    @Resource
    private StreamingChatModel reasoningStreamingChatModel;
    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;
    @Resource
    private ChatHistoryService chatHistoryService;

    private final Cache<String, AiCodeGeneratorService> serviceCache = Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(Duration.ofMinutes(30))
            .expireAfterAccess(Duration.ofMinutes(10))
            .removalListener(((key, value, cause) -> {
                log.info("AI 服务实例被移除,appId:{},原因：{}",key,value);
            }))
            .build();

    /**
     * 兼容之前的模式
     * @param appId
     * @return
     */
    public AiCodeGeneratorService getAiCodeGeneratorService(Long appId) {
        return getAiCodeGeneratorService(appId, CodeGenTypeEnum.MULTI_FILE);
    }

    /**
     * 根据appId 和代码生成类型获取服务(带缓存)
     */
    public AiCodeGeneratorService getAiCodeGeneratorService(Long appId, CodeGenTypeEnum codeGenType) {
        String cacheKey = buildCacheKey(appId, codeGenType);
        return serviceCache.get(cacheKey, key -> createAiCodeGeneratorService(appId, codeGenType));
    }

    /**
     * 构建缓存键
     */
    private String buildCacheKey(Long appId, CodeGenTypeEnum codeGenType) {
        return appId + "_" + codeGenType.getValue();
    }


    /**
     * 根据 appId 获取服务
     * @return
     */

    /**
     * 创建新的 AI 服务实例
     */
    private AiCodeGeneratorService createAiCodeGeneratorService(long appId, CodeGenTypeEnum codeGenType) {
        // 根据 appId 构建独立的对话记忆
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory
                .builder()
                .id(appId)
                .chatMemoryStore(redisChatMemoryStore)
                .maxMessages(20)
                .build();
        // 从数据库加载历史对话到记忆中
        chatHistoryService.loadChatHistoryCountToMemory(appId, chatMemory, 20);
        // 根据代码生成类型选择不同的模型配置
        return switch (codeGenType) {
            // Vue 项目生成使用推理模型
            case VUE -> AiServices.builder(AiCodeGeneratorService.class)
                    .streamingChatModel(reasoningStreamingChatModel)
                    .chatMemoryProvider(memoryId -> chatMemory)
                    .tools(new FileWriteTool())
                    .hallucinatedToolNameStrategy(toolExecutionRequest -> ToolExecutionResultMessage.from(
                            toolExecutionRequest, "Error: there is no tool called " + toolExecutionRequest.name()
                    ))
                    .build();
            // HTML 和多文件生成使用默认模型
            case HTML, MULTI_FILE -> AiServices.builder(AiCodeGeneratorService.class)
                    .chatModel(chatModel)
                    .streamingChatModel(openAiStreamingChatModel)
                    .chatMemory(chatMemory)
                    .build();
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR,
                    "不支持的代码生成类型: " + codeGenType.getValue());
        };
    }

    @Bean
    public AiCodeGeneratorService createAiCodeGeneratorService() {
        return getAiCodeGeneratorService(0L);
    }
}
