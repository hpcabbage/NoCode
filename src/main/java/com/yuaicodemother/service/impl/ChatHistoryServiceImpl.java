package com.yuaicodemother.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.yuaicodemother.exception.ErrorCode;
import com.yuaicodemother.exception.ThrowUtils;
import com.yuaicodemother.model.entity.ChatHistory;
import com.yuaicodemother.mapper.ChatHistoryMapper;
import com.yuaicodemother.service.ChatHistoryService;
import org.springframework.stereotype.Service;

/**
 * 对话历史 服务层实现。
 *
 */
@Service
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory>  implements ChatHistoryService{

    @Override
    public boolean addChatHistory(Long appId, String message, String messageType, Long userId) {
        ThrowUtils.throwIf(appId == null || appId <= 0 , ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        ThrowUtils.throwIf(message == null || message.length() == 0, ErrorCode.PARAMS_ERROR, "消息不能为空");
        ThrowUtils.throwIf(messageType == null || messageType.length() == 0, ErrorCode.PARAMS_ERROR, "消息类型不能为空");
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        ChatHistory chatHistory = ChatHistory.builder()
                .appId(appId)
                .message(message)
                .messageType(messageType)
                .userId(userId)
                .build();
        return this.save(chatHistory);
    }

    @Override
    public boolean deleteyAppId(Long appId) {
        ThrowUtils.throwIf(appId == null || appId <= 0 , ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        return this.remove(new QueryWrapper().eq("appId", appId));
    }
}
