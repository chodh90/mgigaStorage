package com.kt.gigastorage.mobile.vo;

import android.content.Context;

import com.kt.gigastorage.mobile.activity.MainActivity;

/**
 * Created by a-raise on 2016-09-23.
 */
public class FoldrBasVO {

    private static Context context = MainActivity.context;

    /* 명령어 */
    private String cmd;
    /* 사용자ID */
    private String userId;
    /* 장치ID */
    private String devUuid;
    /* 폴더ID */
    private String foldrId;
    /* 폴더명 */
    private String foldrNm;
    /* 폴더레벨 */
    private String foldrLevel;
    /* 상위폴더ID */
    private String upFoldrId;
    /* 전체경로 */
    private String foldrWholePathNm;
    /* 폴더생성일 */
    private String cretDate;
    /* 폴더수정일 */
    private String amdDate;
    /* PC폴더ID */
    private String syncFoldrId;
    /* 동기화여부 */
    private String syncYn;
    /* 최초등록자ID */
    private String frstRegistId;
    /* 최초등록IP */
    private String frstRegistIp;
    /* 최초등록일 */
    private String frstRegistDt;
    /* 최종수정자ID */
    private String lastUpdtId;
    /* 최종수정IP */
    private String lastUpdtIp;
    /* 최종수정일 */
    private String lastUpdtDt;

    /* 폴더유무 */
    private String foldrYn;
    /* 파일ID */
    private Integer fileId;
    /* 파일명 */
    private String fileNm;
    /* 파일사이즈 */
    private Integer fileSize;
    /* 확장자명 */
    private String etsionNm;

    /* 수정전전체경로 */
    private String oldfoldrWholePathNm;

    /* 수정전전체경로 검색 */
    private String likePathNm;

    /* 정렬기준준 */
    private String sortBy;
    /* 검색어 */
    private String searchKeyword;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDevUuid() {
        return devUuid;
    }

    public void setDevUuid(String devUuid) {
        this.devUuid = devUuid;
    }

    public String getFoldrId() {
        return foldrId;
    }

    public void setFoldrId(String foldrId) {
        this.foldrId = foldrId;
    }

    public String getFoldrNm() {
        return foldrNm;
    }

    public void setFoldrNm(String foldrNm) {
        this.foldrNm = foldrNm;
    }

    public String getFoldrLevel() {
        return foldrLevel;
    }

    public void setFoldrLevel(String foldrLevel) {
        this.foldrLevel = foldrLevel;
    }

    public String getUpFoldrId() {
        return upFoldrId;
    }

    public void setUpFoldrId(String upFoldrId) {
        this.upFoldrId = upFoldrId;
    }

    public String getFoldrWholePathNm() {
        return foldrWholePathNm;
    }

    public void setFoldrWholePathNm(String foldrWholePathNm) {
        this.foldrWholePathNm = foldrWholePathNm;
    }

    public String getCretDate() {
        return cretDate;
    }

    public void setCretDate(String cretDate) {
        this.cretDate = cretDate;
    }

    public String getAmdDate() {
        return amdDate;
    }

    public void setAmdDate(String amdDate) {
        this.amdDate = amdDate;
    }

    public String getSyncFoldrId() {
        return syncFoldrId;
    }

    public void setSyncFoldrId(String syncFoldrId) {
        this.syncFoldrId = syncFoldrId;
    }

    public String getSyncYn() {
        return syncYn;
    }

    public void setSyncYn(String syncYn) {
        this.syncYn = syncYn;
    }

    public String getFrstRegistId() {
        return frstRegistId;
    }

    public void setFrstRegistId(String frstRegistId) {
        this.frstRegistId = frstRegistId;
    }

    public String getFrstRegistIp() {
        return frstRegistIp;
    }

    public void setFrstRegistIp(String frstRegistIp) {
        this.frstRegistIp = frstRegistIp;
    }

    public String getFrstRegistDt() {
        return frstRegistDt;
    }

    public void setFrstRegistDt(String frstRegistDt) {
        this.frstRegistDt = frstRegistDt;
    }

    public String getLastUpdtId() {
        return lastUpdtId;
    }

    public void setLastUpdtId(String lastUpdtId) {
        this.lastUpdtId = lastUpdtId;
    }

    public String getLastUpdtIp() {
        return lastUpdtIp;
    }

    public void setLastUpdtIp(String lastUpdtIp) {
        this.lastUpdtIp = lastUpdtIp;
    }

    public String getLastUpdtDt() {
        return lastUpdtDt;
    }

    public void setLastUpdtDt(String lastUpdtDt) {
        this.lastUpdtDt = lastUpdtDt;
    }

    public String getFoldrYn() {
        return foldrYn;
    }

    public void setFoldrYn(String foldrYn) {
        this.foldrYn = foldrYn;
    }

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public String getFileNm() {
        return fileNm;
    }

    public void setFileNm(String fileNm) {
        this.fileNm = fileNm;
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    public String getEtsionNm() {
        return etsionNm;
    }

    public void setEtsionNm(String etsionNm) {
        this.etsionNm = etsionNm;
    }

    public String getOldfoldrWholePathNm() {
        return oldfoldrWholePathNm;
    }

    public void setOldfoldrWholePathNm(String oldfoldrWholePathNm) {
        this.oldfoldrWholePathNm = oldfoldrWholePathNm;
    }

    public String getLikePathNm() {
        return likePathNm;
    }

    public void setLikePathNm(String likePathNm) {
        this.likePathNm = likePathNm;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }
}
