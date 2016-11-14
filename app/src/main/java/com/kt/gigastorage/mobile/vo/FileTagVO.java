package com.kt.gigastorage.mobile.vo;

/**
 * Created by araise on 2016-11-07.
 */

public class FileTagVO {

    /* 파일ID */
    private String fileId;
    /* 파일속성명 */
    private String fileTag;

    private String newFileId;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileTag() {
        return fileTag;
    }

    public void setFileTag(String fileTag) {
        this.fileTag = fileTag;
    }

    public String getNewFileId() {
        return newFileId;
    }

    public void setNewFileId(String newFileId) {
        this.newFileId = newFileId;
    }

}
