package com.yuaicodemother.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.yuaicodemother.model.dto.chathistory.ChatHistoryQueryRequest;
import com.yuaicodemother.model.entity.ChatHistory;
import com.yuaicodemother.model.entity.User;

import java.time.LocalDateTime;

/**
 * 对话历史 服务层。
 *
 */
public interface ChatHistoryService extends IService<ChatHistory> {
    boolean addChatHistory(Long appId, String message, String messageType, Long userId);

    boolean deleteyAppId(Long appId);

    QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest);

    Page<ChatHistory> listAppChatHistoryByPage(Long appId, int PageSize,
                                               LocalDateTime lastCreateTime,
                                               User loginUser );
}
