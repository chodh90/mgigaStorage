package com.kt.gigastorage.mobile.vo;


/**
 * 명령어 큐 관리를 위한 VO 클래스
 * @author hyi
 * @since 2016.10.11
 * @version 1.0
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *     수정일     		 수정자				수정내용
 *  ----------    --------    ------------------
 *  2016.10.11  	hyi             최초생성
 *
 *  </pre>
 */

public class ComndQueueVO {

    /* 명령ID */
    private String queId;

    /* 사용자ID */
    private String fromUserId;

    /* 디바이스UUID */
    private String fromDevUuid;

    /* OS코드 */
    private String fromOsCd;

    /* 폴더전체경로명 */
    private String fromFoldr;

    /* 파일명 */
    private String fromFileNm;

    /* 파일ID */
    private String fromFileId;

    /* 타겟 디바이스UUID */
    private String toDevUuid;

    /* 타겟 OS코드 */
    private String toOsCd;

    /* 타겟 폴더전체경로명 */
    private String toFoldr;

    /* 타겟 파일명 */
    private String toFileNm;

    /* 명령어CODE ( W: windows, A: android, G: Giga NAS ) */
    private String comnd;

    /* 상태코드 */
    private String sttus;

    /* 최초등록자ID */
    private String frstRegistId;

    /* 최초등록IP주소 */
    private String frstRegistIp;

    /* 최초등록일시 */
    private String frstRegistDt;

    /* 최초수정자ID */
    private String lastUpdtId;

    /* 최종수정IP주소 */
    private String lastUpdtIp;

    /* 최종수정일시 */
    private String lastUpdtDt;

    private String reqData;
    private String fileId;

    private String command;
    private String userId;
    private String devUuid;

    private String foldrId;

    /* OS코드 CODE_GROUP_ID 0001  */
    private String comndOsCd;

    /* 명령요청디바이스UUID  */
    private String comndDevUuid;

    public String getQueId() {
        return queId;
    }

    public void setQueId(String queId) {
        this.queId = queId;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromDevUuid() {
        return fromDevUuid;
    }

    public void setFromDevUuid(String fromDevUuid) {
        this.fromDevUuid = fromDevUuid;
    }

    public String getFromOsCd() {
        return fromOsCd;
    }

    public void setFromOsCd(String fromOsCd) {
        this.fromOsCd = fromOsCd;
    }

    public String getFromFoldr() {
        return fromFoldr;
    }

    public void setFromFoldr(String fromFoldr) {
        this.fromFoldr = fromFoldr;
    }

    public String getFromFileNm() {
        return fromFileNm;
    }

    public void setFromFileNm(String fromFileNm) {
        this.fromFileNm = fromFileNm;
    }

    public String getFromFileId() {
        return fromFileId;
    }

    public void setFromFileId(String fromFileId) {
        this.fromFileId = fromFileId;
    }

    public String getToDevUuid() {
        return toDevUuid;
    }

    public void setToDevUuid(String toDevUuid) {
        this.toDevUuid = toDevUuid;
    }

    public String getToOsCd() {
        return toOsCd;
    }

    public void setToOsCd(String toOsCd) {
        this.toOsCd = toOsCd;
    }

    public String getToFoldr() {
        return toFoldr;
    }

    public void setToFoldr(String toFoldr) {
        this.toFoldr = toFoldr;
    }

    public String getToFileNm() {
        return toFileNm;
    }

    public void setToFileNm(String toFileNm) {
        this.toFileNm = toFileNm;
    }

    public String getComnd() {
        return comnd;
    }

    public void setComnd(String comnd) {
        this.comnd = comnd;
    }

    public String getSttus() {
        return sttus;
    }

    public void setSttus(String sttus) {
        this.sttus = sttus;
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

    public String getReqData() {
        return reqData;
    }

    public void setReqData(String reqData) {
        this.reqData = reqData;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
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

    public String getComndOsCd() {
        return comndOsCd;
    }

    public void setComndOsCd(String comndOsCd) {
        this.comndOsCd = comndOsCd;
    }

    public String getComndDevUuid() {
        return comndDevUuid;
    }

    public void setComndDevUuid(String comndDevUuid) {
        this.comndDevUuid = comndDevUuid;
    }

    public String getFoldrId() {
        return foldrId;
    }

    public void setFoldrId(String foldrId) {
        this.foldrId = foldrId;
    }
}
