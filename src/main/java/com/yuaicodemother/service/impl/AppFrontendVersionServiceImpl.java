package com.yuaicodemother.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.yuaicodemother.exception.ErrorCode;
import com.yuaicodemother.exception.ThrowUtils;
import com.yuaicodemother.mapper.AppFrontendVersionMapper;
import com.yuaicodemother.mapper.AppMapper;
import com.yuaicodemother.model.entity.App;
import com.yuaicodemother.model.entity.AppFrontendVersion;
import com.yuaicodemother.model.vo.AppFrontendVersionDiffVO;
import com.yuaicodemother.model.vo.AppFrontendVersionFileDiffVO;
import com.yuaicodemother.model.vo.AppFrontendVersionVO;
import com.yuaicodemother.service.AppFrontendVersionService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AppFrontendVersionServiceImpl extends ServiceImpl<AppFrontendVersionMapper, AppFrontendVersion>
        implements AppFrontendVersionService {

    @jakarta.annotation.Resource
    private AppMapper appMapper;

    @Override
    public Integer getNextVersionNo(Long appId) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 非法");
        List<AppFrontendVersion> versionList = this.list(QueryWrapper.create()
                .eq("appId", appId)
                .orderBy("versionNo", false)
                .limit(1));
        Integer currentMax = versionList.isEmpty() ? null : versionList.get(0).getVersionNo();
        return currentMax == null ? 1 : currentMax + 1;
    }

    @Override
    public AppFrontendVersion createVersionRecord(AppFrontendVersion versionRecord) {
        ThrowUtils.throwIf(versionRecord == null, ErrorCode.PARAMS_ERROR, "版本记录不能为空");
        ThrowUtils.throwIf(versionRecord.getAppId() == null || versionRecord.getAppId() <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 非法");
        ThrowUtils.throwIf(versionRecord.getCreatedBy() == null || versionRecord.getCreatedBy() <= 0, ErrorCode.PARAMS_ERROR, "创建人非法");
        if (versionRecord.getVersionNo() == null || versionRecord.getVersionNo() <= 0) {
            versionRecord.setVersionNo(getNextVersionNo(versionRecord.getAppId()));
        }
        if (versionRecord.getVersionStatus() == null) {
            versionRecord.setVersionStatus("PENDING");
        }
        if (versionRecord.getSourceType() == null) {
            versionRecord.setSourceType("AI_CHAT");
        }
        versionRecord.setCreateTime(LocalDateTime.now());
        versionRecord.setUpdateTime(LocalDateTime.now());
        boolean result = this.save(versionRecord);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "创建前端版本记录失败");
        return versionRecord;
    }

    @Override
    public List<AppFrontendVersion> listByAppId(Long appId) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 非法");
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("appId", appId)
                .orderBy("versionNo", false)
                .orderBy("id", false);
        return this.list(queryWrapper);
    }

    @Override
    public Page<AppFrontendVersion> listByAppId(Long appId, long pageNum, long pageSize) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 非法");
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("appId", appId)
                .orderBy("versionNo", false)
                .orderBy("id", false);
        return this.page(Page.of(pageNum, pageSize), queryWrapper);
    }

    @Override
    public AppFrontendVersionVO getAppFrontendVersionVO(AppFrontendVersion version) {
        if (version == null) {
            return null;
        }
        AppFrontendVersionVO vo = new AppFrontendVersionVO();
        BeanUtil.copyProperties(version, vo);
        App app = appMapper.selectOneById(version.getAppId());
        vo.setCurrentVersion(app != null && app.getCurrentVersionId() != null
                && app.getCurrentVersionId().equals(version.getId()));
        vo.setMetaInfo(parseMetaInfo(version.getMetaPath()));
        return vo;
    }

    @Override
    public List<AppFrontendVersionVO> getAppFrontendVersionVOList(List<AppFrontendVersion> versionList) {
        return versionList.stream().map(this::getAppFrontendVersionVO).collect(Collectors.toList());
    }

    @Override
    public Page<AppFrontendVersionVO> listVersionVOByAppId(Long appId, long pageNum, long pageSize) {
        Page<AppFrontendVersion> versionPage = this.listByAppId(appId, pageNum, pageSize);
        Page<AppFrontendVersionVO> voPage = new Page<>(pageNum, pageSize, versionPage.getTotalRow());
        voPage.setRecords(this.getAppFrontendVersionVOList(versionPage.getRecords()));
        return voPage;
    }

    @Override
    public AppFrontendVersionVO getVersionVOById(Long versionId) {
        ThrowUtils.throwIf(versionId == null || versionId <= 0, ErrorCode.PARAMS_ERROR, "版本 ID 非法");
        AppFrontendVersion version = this.getById(versionId);
        ThrowUtils.throwIf(version == null, ErrorCode.NOT_FOUND_ERROR, "版本不存在");
        return this.getAppFrontendVersionVO(version);
    }

    @Override
    public AppFrontendVersion setVersionStable(Long versionId, Integer isStable) {
        ThrowUtils.throwIf(versionId == null || versionId <= 0, ErrorCode.PARAMS_ERROR, "版本 ID 非法");
        ThrowUtils.throwIf(isStable == null || (isStable != 0 && isStable != 1), ErrorCode.PARAMS_ERROR, "稳定版本标记非法");
        AppFrontendVersion version = this.getById(versionId);
        ThrowUtils.throwIf(version == null, ErrorCode.NOT_FOUND_ERROR, "版本不存在");
        version.setIsStable(isStable);
        version.setUpdateTime(LocalDateTime.now());
        boolean updated = this.updateById(version);
        ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "更新稳定版本标记失败");
        return version;
    }

    @Override
    public AppFrontendVersionDiffVO getVersionDiff(Long leftVersionId, Long rightVersionId) {
        AppFrontendVersion leftVersion = validateComparableVersions(leftVersionId, rightVersionId)[0];
        AppFrontendVersion rightVersion = validateComparableVersions(leftVersionId, rightVersionId)[1];

        Set<String> leftFiles = listRelativeFiles(leftVersion.getVersionPath());
        Set<String> rightFiles = listRelativeFiles(rightVersion.getVersionPath());

        List<String> addedFiles = new ArrayList<>();
        List<String> removedFiles = new ArrayList<>();
        List<String> changedFiles = new ArrayList<>();

        for (String file : rightFiles) {
            if (!leftFiles.contains(file)) {
                addedFiles.add(file);
                continue;
            }
            File leftFile = new File(leftVersion.getVersionPath(), file);
            File rightFile = new File(rightVersion.getVersionPath(), file);
            if (leftFile.isFile() && rightFile.isFile()) {
                String leftContent = FileUtil.readString(leftFile, "UTF-8");
                String rightContent = FileUtil.readString(rightFile, "UTF-8");
                if (!StrUtil.equals(leftContent, rightContent)) {
                    changedFiles.add(file);
                }
            }
        }

        for (String file : leftFiles) {
            if (!rightFiles.contains(file)) {
                removedFiles.add(file);
            }
        }

        AppFrontendVersionDiffVO diffVO = new AppFrontendVersionDiffVO();
        diffVO.setLeftVersionId(leftVersionId);
        diffVO.setRightVersionId(rightVersionId);
        diffVO.setLeftVersionNo(leftVersion.getVersionNo());
        diffVO.setRightVersionNo(rightVersion.getVersionNo());
        diffVO.setAddedFiles(addedFiles);
        diffVO.setRemovedFiles(removedFiles);
        diffVO.setChangedFiles(changedFiles);
        return diffVO;
    }

    @Override
    public AppFrontendVersionFileDiffVO getFileDiff(Long leftVersionId, Long rightVersionId, String filePath) {
        ThrowUtils.throwIf(StrUtil.isBlank(filePath), ErrorCode.PARAMS_ERROR, "文件路径不能为空");
        AppFrontendVersion[] versions = validateComparableVersions(leftVersionId, rightVersionId);
        AppFrontendVersion leftVersion = versions[0];
        AppFrontendVersion rightVersion = versions[1];

        File leftFile = new File(leftVersion.getVersionPath(), filePath);
        File rightFile = new File(rightVersion.getVersionPath(), filePath);

        AppFrontendVersionFileDiffVO fileDiffVO = new AppFrontendVersionFileDiffVO();
        fileDiffVO.setLeftVersionId(leftVersionId);
        fileDiffVO.setRightVersionId(rightVersionId);
        fileDiffVO.setFilePath(filePath);
        fileDiffVO.setLeftContent(readFileIfExists(leftFile));
        fileDiffVO.setRightContent(readFileIfExists(rightFile));
        return fileDiffVO;
    }

    private Map<String, String> parseMetaInfo(String metaPath) {
        Map<String, String> metaInfo = new LinkedHashMap<>();
        if (StrUtil.isBlank(metaPath)) {
            return metaInfo;
        }
        File metaFile = new File(metaPath);
        if (!metaFile.exists() || !metaFile.isFile()) {
            return metaInfo;
        }
        List<String> lines = FileUtil.readLines(metaFile, "UTF-8");
        for (String line : lines) {
            if (StrUtil.isBlank(line) || !line.contains("=")) {
                continue;
            }
            String[] parts = line.split("=", 2);
            if (parts.length == 2) {
                metaInfo.put(parts[0], parts[1]);
            }
        }
        return metaInfo;
    }

    private Set<String> listRelativeFiles(String versionPath) {
        Set<String> files = new LinkedHashSet<>();
        if (StrUtil.isBlank(versionPath)) {
            return files;
        }
        File versionDir = new File(versionPath);
        if (!versionDir.exists() || !versionDir.isDirectory()) {
            return files;
        }
        List<File> fileList = FileUtil.loopFiles(versionDir);
        for (File file : fileList) {
            if (!file.isFile()) {
                continue;
            }
            String relativePath = FileUtil.subPath(versionDir.getAbsolutePath(), file.getAbsolutePath());
            if (StrUtil.equals(relativePath, "meta.txt")) {
                continue;
            }
            files.add(relativePath);
        }
        return files;
    }

    private AppFrontendVersion[] validateComparableVersions(Long leftVersionId, Long rightVersionId) {
        ThrowUtils.throwIf(leftVersionId == null || leftVersionId <= 0, ErrorCode.PARAMS_ERROR, "左侧版本 ID 非法");
        ThrowUtils.throwIf(rightVersionId == null || rightVersionId <= 0, ErrorCode.PARAMS_ERROR, "右侧版本 ID 非法");
        AppFrontendVersion leftVersion = this.getById(leftVersionId);
        AppFrontendVersion rightVersion = this.getById(rightVersionId);
        ThrowUtils.throwIf(leftVersion == null || rightVersion == null, ErrorCode.NOT_FOUND_ERROR, "版本不存在");
        ThrowUtils.throwIf(!leftVersion.getAppId().equals(rightVersion.getAppId()), ErrorCode.PARAMS_ERROR, "只能比较同一应用下的版本");
        return new AppFrontendVersion[]{leftVersion, rightVersion};
    }

    private String readFileIfExists(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return "";
        }
        return FileUtil.readString(file, "UTF-8");
    }
}
