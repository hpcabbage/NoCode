package com.yuaicodemother.service;

import com.mybatisflex.core.service.IService;
import com.yuaicodemother.model.entity.ChatHistory;

/**
 * 对话历史 服务层。
 *
 */
public interface ChatHistoryService extends IService<ChatHistory> {
    boolean addChatHistory(Long appId, String message, String messageType, Long userId);

    boolean deleteyAppId(Long appId);
}
