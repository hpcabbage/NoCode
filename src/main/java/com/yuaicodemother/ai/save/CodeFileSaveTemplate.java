package com.yuaicodemother.ai.save;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.yuaicodemother.constant.AppConstant;
import com.yuaicodemother.exception.BusinessException;
import com.yuaicodemother.exception.ErrorCode;
import com.yuaicodemother.ai.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;

public abstract class CodeFileSaveTemplate<T> {
    // 文件保存目录
    // 文件保存根目录
    protected static final String FILE_SAVE_ROOT_DIR = AppConstant.CODE_OUTPUT_ROOT_DIR;


    /**
     * 模板保存方法
     * @param result
     */
    public final File saveCode(T result, Long appId) {
        //1.验证参数是否合理
        validateResult(result);
        //2.构建文件保存目录
        String baseDirPath = buildUniqueDir(appId);
        //3.保存文件
        saveFiles(result, baseDirPath);
        //4.返回保存的目录
        return new File(baseDirPath);
    }

    /**
     * 验证输入的参数是否合法
     * @param result
     */
    protected void validateResult(T result) {
        if(result == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "代码结果对象不能为空");
        }
    }

    /**
     * 构建一个唯一的目录名称
     * @return 目录路径
     */
    //被final 修饰，不能被继承重写，即这个方法就是最终方法
    protected final String buildUniqueDir(Long appId) {
        if(appId == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "appId不能为空");
        }
        String codeType = getCodeType().getValue();
        String uniqueDirName = StrUtil.format("{}_{}",codeType, appId);
        String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    /**
     * 写入文件
     * @param dirPath
     * @param filename
     * @param content
     */
    protected final void writeToFile(String dirPath,String filename,String content) {
        if(!StrUtil.isBlank(content)) {
            String path = dirPath + File.separator + filename;
            FileUtil.writeString(content,path, StandardCharsets.UTF_8);
        }
    }

    /**
     * 获取代码类型（由子类实现）
     * @return 代码生成的类型
     */
    protected abstract CodeGenTypeEnum getCodeType();

    /**
     * 保存文件的具体实现（由子类实现）
     * @param restult
     * @param baseDirPath
     */
    protected abstract void saveFiles(T restult, String baseDirPath);
}
