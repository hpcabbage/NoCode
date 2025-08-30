package com.yuaicodemother.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface ProjectDownloadService {
    void downloadProject(String projectPath, String downloadFilePath, HttpServletResponse response);
}
