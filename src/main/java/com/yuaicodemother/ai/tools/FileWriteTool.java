package com.yuaicodemother.ai.tools;

import com.yuaicodemother.constant.AppConstant;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Slf4j
public class FileWriteTool {

    @Tool("写入文件到指定路径")
    public String writeFile(
            @P("文件的相对路径")
            String relativeFilePath,
            @P("要写入文件的内容")
            String content,
            @ToolMemoryId Long appId
    ) {
        try {
            Path path = Paths.get(relativeFilePath);
            // 如果是相对路径才处理
            if(!path.isAbsolute()) {
                String projectDirName = "vue_project_" + appId;
                Path projectRoot = Paths.get(AppConstant.CODE_OUTPUT_ROOT_DIR,projectDirName);
                path = projectRoot.resolve(path);
            }
            Path parentDir = path.getParent();
            if(parentDir != null) {
                Files.createDirectories(parentDir);
            }
            Files.write(path,content.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            log.info("写入文件成功: {}",relativeFilePath);
            return "写入文件成功: " + relativeFilePath;
        } catch(Exception e) {
            String errorMsg = "写入文件失败: " + relativeFilePath + ",错误: " + e.getMessage();
            log.error(errorMsg);
            return errorMsg;
        }
    }
}

