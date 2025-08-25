package com.yuaicodemother.ai.save;

import com.yuaicodemother.ai.enums.CodeGenTypeEnum;
import com.yuaicodemother.ai.model.HtmlCodeResult;
import com.yuaicodemother.ai.model.MultiFileCodeResult;

import java.io.File;

public class CodeFileSaveExecutor {
    private static final HtmlCodeFileSaveTemplate htmlCodeFileSaveTemplate = new HtmlCodeFileSaveTemplate();
    private static final MultiFileCodeFileSaverTemplate multiFileCodeFileSaverTemplate = new MultiFileCodeFileSaverTemplate();

    public static File executeSave(Object result, CodeGenTypeEnum codeGenType,Long appId) {
        return switch(codeGenType)
        {
            case HTML -> htmlCodeFileSaveTemplate.saveCode((HtmlCodeResult) result,appId);
            case MULTI_FILE -> multiFileCodeFileSaverTemplate.saveCode((MultiFileCodeResult) result,appId);
            default -> throw new RuntimeException("不支持的生成模式");
        };
    }
}
