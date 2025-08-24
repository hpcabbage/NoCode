package com.yuaicodemother.core;


import com.yuaicodemother.Exception.BusinessException;
import com.yuaicodemother.Exception.ErrorCode;
import com.yuaicodemother.ai.AiCodeGeneratorService;
import com.yuaicodemother.ai.enums.CodeGenTypeEnum;
import com.yuaicodemother.ai.model.HtmlCodeResult;
import com.yuaicodemother.ai.model.MultiFileCodeResult;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class AiCodeGeneratorFacade {
    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;

    /**
     * 根绝类型生成代码的统一入口
     * @param message    用户提示词
     * @param codeGenType 生成模式
     * @return 保存目录
     */
    public File GenerateAndSaveCode(String message, CodeGenTypeEnum codeGenType) {
        if(codeGenType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择生成模式");
        }
        return switch (codeGenType) {
            case MULTI_FILE -> generateAndSavaMultiFileCode(message);
            case HTML -> generateAndSavaHtmlCode(message);
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的生成模式");
        };
    }


    private File generateAndSavaMultiFileCode(String userMessage) {
        MultiFileCodeResult result = aiCodeGeneratorService.generateMultiFileCode(userMessage);
        return CodeFileSaver.saveMulFileCodeResult(result);
    }

    private File generateAndSavaHtmlCode(String userMessage) {
        HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode(userMessage);
        return CodeFileSaver.saveHtmlCodeResult(result);
    }
}
