package com.kt.gigastorage.mobile.vo;

/**
 * Created by a-raise on 2016-09-23.
 */
public class NoteListVO {

    /** 사용자 ID **/
    private String userId;

    /** 노트 ID **/
    private String noteId;

    /** 파일 ID **/
    private String fileId;

    /** 최초등록자ID **/
    private String frstRegistId;

    /** 최초등록IP **/
    private String frstRegistIp;

    /** 최초등록일 **/
    private String frstRegistDt;

    /** 최종수정자 ID **/
    private String lastUpdtId;

    /** 최종수정 IP **/
    private String lastUpdtIp;

    /** 최종수정일 **/
    private String lastUpdtDt;

    /** 관련노트 ID 1 **/
    private String ascNoteId1;

    /** 관련노트 ID 2 **/
    private String ascNoteId2;

    /** 정렬순서 **/
    private String sortOdrg;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
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

    public String getAscNoteId1() {
        return ascNoteId1;
    }

    public void setAscNoteId1(String ascNoteId1) {
        this.ascNoteId1 = ascNoteId1;
    }

    public String getAscNoteId2() {
        return ascNoteId2;
    }

    public void setAscNoteId2(String ascNoteId2) {
        this.ascNoteId2 = ascNoteId2;
    }

    public String getSortOdrg() {
        return sortOdrg;
    }

    public void setSortOdrg(String sortOdrg) {
        this.sortOdrg = sortOdrg;
    }
}