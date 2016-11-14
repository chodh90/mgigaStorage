package com.kt.gigastorage.mobile.vo;

/**
 * Created by a-raise on 2016-09-23.
 */
public class FileEmailVO {

    /* 파일ID */
    private String fileId;

    /* eamil TO */
    private String emailTo;

    /* eamil CC */
    private String emailCc;

    /* eamil FROM */
    private String emailFrom;

    /* eamil REPLY TO */
    private String emailReplyTo;

    /* eamil SUBJECT */
    private String emailSbjt;

    /* 보낸시간 */
    private String sendDate;

    /* eamil ID */
    private String emailId;

    private String newFileId;

    private String inOut;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getEmailTo() {
        return emailTo;
    }

    public void setEmailTo(String emailTo) {
        this.emailTo = emailTo;
    }

    public String getEmailCc() {
        return emailCc;
    }

    public void setEmailCc(String emailCc) {
        this.emailCc = emailCc;
    }

    public String getEmailFrom() {
        return emailFrom;
    }

    public void setEmailFrom(String emailFrom) {
        this.emailFrom = emailFrom;
    }

    public String getEmailReplyTo() {
        return emailReplyTo;
    }

    public void setEmailReplyTo(String emailReplyTo) {
        this.emailReplyTo = emailReplyTo;
    }

    public String getEmailSbjt() {
        return emailSbjt;
    }

    public void setEmailSbjt(String emailSbjt) {
        this.emailSbjt = emailSbjt;
    }

    public String getSendDate() {
        return sendDate;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getNewFileId() {
        return newFileId;
    }

    public void setNewFileId(String newFileId) {
        this.newFileId = newFileId;
    }

    public String getInOut() {
        return inOut;
    }

    public void setInOut(String inOut) {
        this.inOut = inOut;
    }
}
