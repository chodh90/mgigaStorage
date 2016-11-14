package com.kt.gigastorage.mobile.vo;

import android.content.Context;

import com.kt.gigastorage.mobile.activity.MainActivity;

/**
 * Created by a-raise on 2016-09-28.
 */
public class FileAtribVO {

    private static Context context = MainActivity.context;

    /* 사용자ID */
    private String userId;

    /* 디바이스UUID */
    private String devUuid;

    /* 파일ID */
    private String fileId;

    /* 파일속성명 */
    private String fileAtrib;

    /* 파일속성값 */
    private String fileAtribVal;

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

    public String getFileAtrib() {
        return fileAtrib;
    }

    public void setFileAtrib(String fileAtrib) {
        this.fileAtrib = fileAtrib;
    }

    public String getFileAtribVal() {
        return fileAtribVal;
    }

    public void setFileAtribVal(String fileAtribVal) {
        this.fileAtribVal = fileAtribVal;
    }

}
