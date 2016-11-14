package com.kt.gigastorage.mobile.vo;


import android.content.Context;

import com.kt.gigastorage.mobile.activity.MainActivity;

/**
 * Created by a-raise on 2016-09-23.
 */

public class DevBasVO {

    private static Context context = MainActivity.context;

    /** 사용자 ID */
    private String userId;

    /** 장치고유 ID */
    private String devUuid;

    /** 장치명 */
    private String devNm;

    /** OS코드( A: 안드로이드, W: 윈도우) */
    private String osCd;

    /** OS명 */
    private String osNm;

    /** 상세 OS명 */
    private String osDesc;

    /** 디바이스 제조업체 */
    private String mkngVndrNm;

    /** 최초등록자 ID */
    private String frstRegistId;

    /** 최초등록자 IP주소 */
    private String frstRegistIp;

    /** 최초등록일시 */
    private String frstRegistDt;

    /** 최종수정자ID */
    private String lastUpdtId;

    /** 최종수정 IP주소 */
    private String lastUpdtIp;

    /** 최종수정일시 */
    private String lastUpdtDt;

    /** GCM 토큰 */
    private String token;

    /** 정렬기준 */
    private String sortBy;

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

    public String getDevNm() {
        return devNm;
    }

    public void setDevNm(String devNm) {
        this.devNm = devNm;
    }

    public String getOsCd() {
        return osCd;
    }

    public void setOsCd(String osCd) {
        this.osCd = osCd;
    }

    public String getOsNm() {
        return osNm;
    }

    public void setOsNm(String osNm) {
        this.osNm = osNm;
    }

    public String getOsDesc() {
        return osDesc;
    }

    public void setOsDesc(String osDesc) {
        this.osDesc = osDesc;
    }

    public String getMkngVndrNm() {
        return mkngVndrNm;
    }

    public void setMkngVndrNm(String mkngVndrNm) {
        this.mkngVndrNm = mkngVndrNm;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
}
