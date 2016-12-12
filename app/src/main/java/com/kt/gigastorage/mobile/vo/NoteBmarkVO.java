package com.kt.gigastorage.mobile.vo;


/**
 * 비즈노트 책갈피 관리를 위한 VO 클래스
 * @author hyi
 * @since 2016.11.30
 * @version 1.0
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일     		 수정자				수정내용
 *  ----------    --------    ------------------
 *  2016.11.30  	hyi             최초생성
 *
 *  </pre>
 */

public class NoteBmarkVO {

    /** BZ노트 ID **/
    private String noteId;

    /** 사용자 ID **/
    private String userId;

    /** 최종수정일시 **/
    private String lastUpdtDt;

    /** 최종수정IP주소 **/
    private String lastUpdtIp;

    /** 최종수정자ID **/
    private String lastUpdtId;

    /** 최초등록일시 **/
    private String frstRegistDt;

    /** 최초등록IP주소 **/
    private String frstRegistIp;

    /** 최초등록자ID **/
    private String frstRegistId;

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastUpdtDt() {
        return lastUpdtDt;
    }

    public void setLastUpdtDt(String lastUpdtDt) {
        this.lastUpdtDt = lastUpdtDt;
    }

    public String getLastUpdtIp() {
        return lastUpdtIp;
    }

    public void setLastUpdtIp(String lastUpdtIp) {
        this.lastUpdtIp = lastUpdtIp;
    }

    public String getLastUpdtId() {
        return lastUpdtId;
    }

    public void setLastUpdtId(String lastUpdtId) {
        this.lastUpdtId = lastUpdtId;
    }

    public String getFrstRegistDt() {
        return frstRegistDt;
    }

    public void setFrstRegistDt(String frstRegistDt) {
        this.frstRegistDt = frstRegistDt;
    }

    public String getFrstRegistIp() {
        return frstRegistIp;
    }

    public void setFrstRegistIp(String frstRegistIp) {
        this.frstRegistIp = frstRegistIp;
    }

    public String getFrstRegistId() {
        return frstRegistId;
    }

    public void setFrstRegistId(String frstRegistId) {
        this.frstRegistId = frstRegistId;
    }

}
