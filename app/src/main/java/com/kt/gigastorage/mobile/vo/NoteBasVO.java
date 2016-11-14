package com.kt.gigastorage.mobile.vo;

/**
 * Created by a-raise on 2016-09-23.
 */
public class NoteBasVO {

    /** 사용자 ID **/
    private String userId;

    /** 노트 ID **/
    private String noteId;

    /** 노트명 **/
    private String noteNm;

    /** 폴더여부 **/
    private String foldrYn;

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

    public String getNoteNm() {
        return noteNm;
    }

    public void setNoteNm(String noteNm) {
        this.noteNm = noteNm;
    }

    public String getFoldrYn() {
        return foldrYn;
    }

    public void setFoldrYn(String foldrYn) {
        this.foldrYn = foldrYn;
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
}
