package com.yuaicodemother.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.yuaicodemother.ai.enums.CodeGenTypeEnum;
import com.yuaicodemother.ai.model.HtmlCodeResult;
import com.yuaicodemother.ai.model.MultiFileCodeResult;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class CodeFileSaver {
    //保存文件根目录
    private static final String FILE_SAVE_ROOT_PATH = System.getProperty("user.dir") + "/tmp/code_output";

    public static File saveHtmlCodeResult(HtmlCodeResult htmlCodeResult) {
        String baseDirPath = buildUnqiueDir(CodeGenTypeEnum.HTML.getValue());
        writeToFile(baseDirPath,"index.html",htmlCodeResult.getHtmlCode());
        return new File(baseDirPath);
    }

    public static File saveMulFileCodeResult(MultiFileCodeResult result) {
        String baseDirPath = buildUnqiueDir(CodeGenTypeEnum.MULTI_FILE.getValue());
        writeToFile(baseDirPath,"index.html",result.getHtmlCode());
        writeToFile(baseDirPath,"style.css",result.getCssCode());
        writeToFile(baseDirPath,"script.js",result.getJsCode());
        return new File(baseDirPath);
    }

    /**
     * 构建临时目录
     */

    private static String buildUnqiueDir(String bizType) {
        String uniqueDirName = StrUtil.format("{}_{}",bizType, IdUtil.getSnowflake());
        String dirPath = FILE_SAVE_ROOT_PATH + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    /**
     * 写入单个文件
     */
    private static void writeToFile(String dirPath,String filename,String content) {
        String path = dirPath + File.separator + filename;
        FileUtil.writeString(content,path, StandardCharsets.UTF_8);
    }
}
