package com.yuaicodemother.ai.core;

import cn.hutool.core.util.RuntimeUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Slf4j
public class VueProjectBuilder {
    /**
     * 执行通用命令
     * @param workingDir
     * @param command
     * @param timeoutSeconds
     * @return
     */
    private boolean executeCommand(File workingDir, String command, int timeoutSeconds) {
        try {
            log.info("在目录 {} 中执行命令 {}", workingDir,  command);
            Process process = RuntimeUtil.exec(
                    null,
                    workingDir,
                    command.split("\\s+")
            );
            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            if(!finished) {
                log.error("命令执行超时({}秒)", timeoutSeconds);
                process.destroyForcibly();
                return false;
            }
            int exitCode = process.exitValue();
            if(exitCode == 0) {
                log.info("命令执行成功: {}",command);
                return true;
            } else {
                log.error("{}命令执行失败: 退出码:{}",command, exitCode);
                return false;
            }
        } catch (InterruptedException e) {
            log.error("执行命令失败: {} ,错误信息: {}",command,e.getMessage());
            return false;
        }
    }

    /**
     * 执行npm install命令
     * @param projectDir
     * @return
     */
    private boolean executeNpmInstall(File projectDir) {
        log.info("开始执行npm install...");
        String command = String.format("%s install",buildCommand("npm"));
        return executeCommand(projectDir, command, 300); // 5分钟超时时间
    }

    private boolean executeNpmBuild(File projectDir) {
        log.info("开始执行npm build...");
        String command = String.format("%s run build",buildCommand("npm"));
        return executeCommand(projectDir, command, 180); // 3分钟的超时时间
    }

    /**
     * 检查是否是windows操作系统
     * @return
     */
    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    /**
     * 根据操作系统构造命令
     * @param baseCommand
     * @return
     */
    private String buildCommand(String baseCommand) {
        if (isWindows()) {
            return baseCommand + ".cmd";
        }
        return baseCommand;
    }

    public boolean buildProject(String projectPath) {
        File projectDir = new File(projectPath);
        if(!projectDir.exists() || !projectDir.isDirectory()) {
            log.info("项目路径不存在或者不是目录: {}", projectPath);
            return false;
        }
        File packageJson = new File(projectDir, "package.json");
        if(!packageJson.exists()) {
            log.error("package.json 文件不存在:{}",packageJson.getAbsolutePath());
            return false;
        }
        log.info("开始构建项目: {}", projectPath);
        if(!executeNpmInstall(projectDir)) {
            log.error("npm install 失败");
            return false;
        }
        if(!executeNpmBuild(projectDir)) {
            log.error("npm build 构建失败");
            return false;
        }
        File distDir = new File(projectDir, "dist");
        if(!distDir.exists()) {
            log.error("构建完成 ，dist 目录不存在: {}", distDir.getAbsolutePath());
            return false;
        }
        log.info("VUE项目构建完成，dist 目录已生成: {}", distDir.getAbsolutePath());
        return true;
    }
}
