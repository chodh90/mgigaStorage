package com.kt.gigastorage.mobile.vo;

import android.content.Context;

import com.kt.gigastorage.mobile.activity.MainActivity;

import java.util.List;

/**
 * Created by a-raise on 2016-09-28.
 */
public class FileBasVO {

    private static Context context = MainActivity.context;

    /* 명령어 */
    private String cmd;

    /* 사용자 ID */
    private String userId;

    /* 디바이스UUID */
    private String devUuid;

    /* 파일ID */
    private String fileId;

    /* 폴더ID */
    private String foldrId;

    /* 파일 명 */
    private String fileNm;

    /* 이전 파일 명 */
    private String oldfileNm;

    /* PC파일ID */
    private String syncFileId;

    /* 확장자명 */
    private String etsionNm;

    /* 파일크기 */
    private String fileSize;

    /* 만든날짜 */
    private String cretDate;

    /* 수정한날짜 */
    private String amdDate;

    /* 스캔한날짜 */
    private String scanDate;

    /* NAS 동기화 여부 */
    private String nasSynchYn;

    /* 파일 속성 정보 */
    private List<FileAtribVO> listAttr;

    /* 전체경로 */
    private String foldrWholePathNm;

    /* 이전전체경로 */
    private String oldfoldrWholePathNm;

    private Integer page = 0;

    private Integer startIndex = 0;

    private Integer endIndex = 0;

    private String newFileId;

    private String nasUserId;

    private String nasDevUuid;

    private String nasFoldrId;

    private String fileShar;

    /* 정렬기준 */
    private String sortBy;

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

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFoldrId() {
        return foldrId;
    }

    public void setFoldrId(String foldrId) {
        this.foldrId = foldrId;
    }

    public String getFileNm() {
        return fileNm;
    }

    public void setFileNm(String fileNm) {
        this.fileNm = fileNm;
    }

    public String getOldfileNm() {
        return oldfileNm;
    }

    public void setOldfileNm(String oldfileNm) {
        this.oldfileNm = oldfileNm;
    }

    public String getSyncFileId() {
        return syncFileId;
    }

    public void setSyncFileId(String syncFileId) {
        this.syncFileId = syncFileId;
    }

    public String getEtsionNm() {
        return etsionNm;
    }

    public void setEtsionNm(String etsionNm) {
        this.etsionNm = etsionNm;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
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

    public String getScanDate() {
        return scanDate;
    }

    public void setScanDate(String scanDate) {
        this.scanDate = scanDate;
    }

    public String getNasSynchYn() {
        return nasSynchYn;
    }

    public void setNasSynchYn(String nasSynchYn) {
        this.nasSynchYn = nasSynchYn;
    }

    public List<FileAtribVO> getListAttr() {
        return listAttr;
    }

    public void setListAttr(List<FileAtribVO> listAttr) {
        this.listAttr = listAttr;
    }

    public String getFoldrWholePathNm() {
        return foldrWholePathNm;
    }

    public void setFoldrWholePathNm(String foldrWholePathNm) {
        this.foldrWholePathNm = foldrWholePathNm;
    }

    public String getOldfoldrWholePathNm() {
        return oldfoldrWholePathNm;
    }

    public void setOldfoldrWholePathNm(String oldfoldrWholePathNm) {
        this.oldfoldrWholePathNm = oldfoldrWholePathNm;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(Integer endIndex) {
        this.endIndex = endIndex;
    }

    public String getNewFileId() {
        return newFileId;
    }

    public void setNewFileId(String newFileId) {
        this.newFileId = newFileId;
    }

    public String getNasUserId() {
        return nasUserId;
    }

    public void setNasUserId(String nasUserId) {
        this.nasUserId = nasUserId;
    }

    public String getNasDevUuid() {
        return nasDevUuid;
    }

    public void setNasDevUuid(String nasDevUuid) {
        this.nasDevUuid = nasDevUuid;
    }

    public String getNasFoldrId() {
        return nasFoldrId;
    }

    public void setNasFoldrId(String nasFoldrId) {
        this.nasFoldrId = nasFoldrId;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getFileShar() {
        return fileShar;
    }

    public void setFileShar(String fileShar) {
        this.fileShar = fileShar;
    }
}
