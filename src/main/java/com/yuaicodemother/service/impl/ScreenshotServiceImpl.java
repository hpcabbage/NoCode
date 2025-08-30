package com.yuaicodemother.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.yuaicodemother.exception.ErrorCode;
import com.yuaicodemother.exception.ThrowUtils;
import com.yuaicodemother.service.ScreenshotService;
import com.yuaicodemother.utils.MinioUtils;
import com.yuaicodemother.utils.WebScreenshotUtils;
import dev.langchain4j.agent.tool.P;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
public class ScreenshotServiceImpl implements ScreenshotService {
    @Resource
    private MinioUtils minioUtils;

    /**
     * 生成网页截图并上传到MinIO
     * @param webUrl
     * @return
     */
    @Override
    public String generateAndUploadScreenshot(String webUrl) {
        ThrowUtils.throwIf(webUrl == null || webUrl.length() == 0, ErrorCode.PARAMS_ERROR, "网页地址不能为空");
        log.info("开始截取网页图片,URL:{}", webUrl);
        //开始生成本地截图
        String localScreenshotPath = WebScreenshotUtils.saveWebPageScreenshot(webUrl);
        ThrowUtils.throwIf(localScreenshotPath == null, ErrorCode.SYSTEM_ERROR, "生成网页截图失败");
        try {
            String url = uploadScreenshotToMinio(localScreenshotPath);
            ThrowUtils.throwIf(url == null, ErrorCode.SYSTEM_ERROR, "上传截图到MinIO失败");
            log.info("截图上传成功,URL:{}", url);
            return url;
        } catch (Exception e) {
            log.error("上传截图到MinIO失败: {}", e.getMessage());
        } finally {
            //删除本地截图
            cleanLocalFile(localScreenshotPath);
        }
        return null;
    }

    /**
     * 上传截图到MinIO
     * @param localScreenshotPath
     * @return
     * @throws Exception
     */
    private String uploadScreenshotToMinio(String localScreenshotPath) throws Exception {
        if(StrUtil.isBlank(localScreenshotPath)){
            return null;
        }
        File screenshotFile = new File(localScreenshotPath);
        if(!screenshotFile.exists()) {
            log.error("截图文件不存在: {}", localScreenshotPath);
            return null;
        }
        return minioUtils.uploadFile(screenshotFile);
    }

    /**
     * 删除本地文件
     * @param localFilePath
     */
    private void cleanLocalFile(String localFilePath) {
        File file = new File(localFilePath);
        if(file.exists()) {
            File parentFile = file.getParentFile();
            FileUtil.del(parentFile);
            log.info("删除本地文件成功: {}", parentFile.getAbsolutePath());
        }
    }
}
