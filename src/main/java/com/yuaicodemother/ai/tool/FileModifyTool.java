package com.yuaicodemother.ai.tool;

import com.yuaicodemother.constant.AppConstant;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Slf4j
public class FileModifyTool {
    @Tool
    public String modifyFile(
            @P("文件的相对路径")
            String relativeFilePath,
            @P("要替换的旧内容")
            String oldcontent,
            @P("替换后的新内容")
            String newContent,
            @ToolMemoryId Long appId
    ) {
        try {
            Path path = Paths.get(relativeFilePath);
            if(!path.isAbsolute()) {
                String projectDirName = "vue_project_" + appId;
                Path projectRoot = Paths.get(AppConstant.CODE_OUTPUT_ROOT_DIR, projectDirName);
                path = projectRoot.resolve(relativeFilePath);
            }
            if(!Files.exists(path) || !Files.isRegularFile(path)) {
                return "警告：文件不存在，或者不是文件，无法修改 - " + relativeFilePath;
            }
            String originalContent = Files.readString(path);
            if(!originalContent.contains(oldcontent)) {
                return "警告：文件内容中不存在旧内容，无法修改 - " + relativeFilePath;
            }
            String modifiedContent = originalContent.replace(oldcontent, newContent);
            if(originalContent.equals(modifiedContent)) {
                return "警告：文件内容未发生改变，无法修改 - " + relativeFilePath;
            }
            Files.writeString(path,modifiedContent, StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);
            log.info("修改文件成功: {}",relativeFilePath);
            return "修改文件成功: " + relativeFilePath;
        } catch (IOException e) {
            String errorMessage = "修改文件失败: " + relativeFilePath + ", 错误: " + e.getMessage();
            log.error(errorMessage, e);
            return errorMessage;
        }
    }

}
