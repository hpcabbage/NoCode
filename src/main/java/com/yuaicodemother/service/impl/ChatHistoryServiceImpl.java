package com.yuaicodemother.service.impl;

import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.yuaicodemother.constant.UserConstant;
import com.yuaicodemother.exception.BusinessException;
import com.yuaicodemother.exception.ErrorCode;
import com.yuaicodemother.exception.ThrowUtils;
import com.yuaicodemother.model.dto.chathistory.ChatHistoryQueryRequest;
import com.yuaicodemother.model.entity.App;
import com.yuaicodemother.model.entity.ChatHistory;
import com.yuaicodemother.mapper.ChatHistoryMapper;
import com.yuaicodemother.model.entity.User;
import com.yuaicodemother.service.AppService;
import com.yuaicodemother.service.ChatHistoryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import javax.management.Query;
import java.time.LocalDateTime;

/**
 * 对话历史 服务层实现。
 *
 */
@Service
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory>  implements ChatHistoryService{
    @Resource
    private AppService appService;

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

    @Override
    public QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest) {
        if (chatHistoryQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        LocalDateTime lastCreateTime = chatHistoryQueryRequest.getLastCreateTime();
        String sortField = chatHistoryQueryRequest.getSortField();
        String sortOrder = chatHistoryQueryRequest.getSortOrder();

        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("id", chatHistoryQueryRequest.getId())
                .eq("appId", chatHistoryQueryRequest.getAppId())
                .like("message", chatHistoryQueryRequest.getMessage())
                .eq("messageType", chatHistoryQueryRequest.getMessageType())
                .eq("userId", chatHistoryQueryRequest.getUserId());
        if(lastCreateTime != null) {
            queryWrapper.lt("createTime", lastCreateTime);
        }
        if(StrUtil.isNotBlank(sortField)) {
            queryWrapper.orderBy(sortField, "ascend".equals(sortOrder));
        } else {
            queryWrapper.orderBy("createTime",false);
        }
        return queryWrapper;
    }

    @Override
    public Page<ChatHistory> listAppChatHistoryByPage(Long appId, int PageSize, LocalDateTime lastCreateTime, User loginUser) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        ThrowUtils.throwIf(PageSize <= 0 || PageSize > 50, ErrorCode.PARAMS_ERROR, "分页大小必须在1-50之间");
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 只有管理员和应用创建者才能查看应用对话历史
        boolean isAdmin = UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole());
        boolean isCreator = app.getUserId().equals(loginUser.getId());
        ThrowUtils.throwIf(!isAdmin && !isCreator, ErrorCode.NO_AUTH_ERROR, "无权限查看应用对话历史");
        ChatHistoryQueryRequest chatHistoryQueryRequest = new ChatHistoryQueryRequest();
        chatHistoryQueryRequest.setAppId(appId);
        chatHistoryQueryRequest.setLastCreateTime(lastCreateTime);
        QueryWrapper queryWrapper = this.getQueryWrapper(chatHistoryQueryRequest);
        return this.page(Page.of(1, PageSize), queryWrapper);
    }
}