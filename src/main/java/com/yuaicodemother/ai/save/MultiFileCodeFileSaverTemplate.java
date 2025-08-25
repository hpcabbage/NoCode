package com.yuaicodemother.ai.save;

import cn.hutool.core.util.StrUtil;
import com.yuaicodemother.exception.BusinessException;
import com.yuaicodemother.exception.ErrorCode;
import com.yuaicodemother.ai.enums.CodeGenTypeEnum;
import com.yuaicodemother.ai.model.MultiFileCodeResult;

public class MultiFileCodeFileSaverTemplate extends CodeFileSaveTemplate<MultiFileCodeResult>{

    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.MULTI_FILE;
    }

    @Override
    protected void saveFiles(MultiFileCodeResult restult, String baseDirPath) {
        writeToFile(baseDirPath,"index.html",restult.getHtmlCode());
        writeToFile(baseDirPath,"style.css",restult.getCssCode());
        writeToFile(baseDirPath,"script.js",restult.getJsCode());
    }

    @Override
    protected void validateResult(MultiFileCodeResult result) {
        super.validateResult(result);
        if(StrUtil.isBlank(result.getHtmlCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "HTML代码不能为空");
        }
    }
}
