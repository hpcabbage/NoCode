package com.yuaicodemother.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.yuaicodemother.model.entity.AppFrontendVersion;
import com.yuaicodemother.model.vo.AppFrontendVersionDiffVO;
import com.yuaicodemother.model.vo.AppFrontendVersionFileDiffVO;
import com.yuaicodemother.model.vo.AppFrontendVersionVO;

import java.util.List;

public interface AppFrontendVersionService extends IService<AppFrontendVersion> {

    Integer getNextVersionNo(Long appId);

    AppFrontendVersion createVersionRecord(AppFrontendVersion versionRecord);

    List<AppFrontendVersion> listByAppId(Long appId);

    Page<AppFrontendVersion> listByAppId(Long appId, long pageNum, long pageSize);

    AppFrontendVersionVO getAppFrontendVersionVO(AppFrontendVersion version);

    List<AppFrontendVersionVO> getAppFrontendVersionVOList(List<AppFrontendVersion> versionList);

    Page<AppFrontendVersionVO> listVersionVOByAppId(Long appId, long pageNum, long pageSize);

    AppFrontendVersionVO getVersionVOById(Long versionId);

    AppFrontendVersion setVersionStable(Long versionId, Integer isStable);

    List<AppFrontendVersion> listVersionsAfter(Long appId, Integer versionNo);

    boolean removeVersionsAfter(Long appId, Integer versionNo);

    AppFrontendVersionDiffVO getVersionDiff(Long leftVersionId, Long rightVersionId);

    AppFrontendVersionFileDiffVO getFileDiff(Long leftVersionId, Long rightVersionId, String filePath);
}
