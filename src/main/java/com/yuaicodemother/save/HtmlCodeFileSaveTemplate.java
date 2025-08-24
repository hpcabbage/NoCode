package com.yuaicodemother.save;

import com.yuaicodemother.ai.enums.CodeGenTypeEnum;
import com.yuaicodemother.ai.model.HtmlCodeResult;

public class HtmlCodeFileSaveTemplate extends CodeFileSaveTemplate<HtmlCodeResult>{
    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.HTML;
    }

    @Override
    protected void saveFiles(HtmlCodeResult restult, String baseDirPath) {
        //保存HTML文件
        writeToFile(baseDirPath,"index.html",restult.getHtmlCode());
    }

    @Override
    protected void validateResult(HtmlCodeResult result) {
        super.validateResult(result);
        if(result == null || result.getHtmlCode() == null){
            throw new RuntimeException("生成HTML代码文件结果为空");
        }
    }
}
