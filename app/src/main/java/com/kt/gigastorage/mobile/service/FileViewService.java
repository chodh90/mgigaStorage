package com.kt.gigastorage.mobile.service;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by araise on 2016-11-08.
 */

public class FileViewService {

    public static AlertDialog.Builder alert;

    //파일 확장자 조회
    public static String getExtension(String fileStr) {
        return fileStr.substring(fileStr.lastIndexOf(".") + 1, fileStr.length());
    }

    //파일 뷰어 실행
    public static void viewFile(Context context, String filePath, String fileName) {
        List<ResolveInfo> list = new ArrayList<ResolveInfo>();
        Intent fileLinkIntent = new Intent(Intent.ACTION_VIEW);
        fileLinkIntent.addCategory(Intent.CATEGORY_DEFAULT);
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+filePath, fileName);
        Uri uri = Uri.fromFile(file);
        //확장자 구하기
        String fileExtend = getExtension(file.getAbsolutePath());
        // 파일 확장자 별로 mime type 지정해 준다.
        if (fileExtend.equalsIgnoreCase("mp3")) {
            fileLinkIntent.setDataAndType(Uri.fromFile(file), "audio/*");
        } else if (fileExtend.equalsIgnoreCase("mp4")) {
            fileLinkIntent.setDataAndType(Uri.fromFile(file), "video/*");
        } else if (fileExtend.equalsIgnoreCase("jpg")
                || fileExtend.equalsIgnoreCase("jpeg")
                || fileExtend.equalsIgnoreCase("gif")
                || fileExtend.equalsIgnoreCase("png")
                || fileExtend.equalsIgnoreCase("bmp")) {
            fileLinkIntent.setDataAndType(Uri.fromFile(file), "image/*");
        } else if (fileExtend.equalsIgnoreCase("txt")) {
            fileLinkIntent.setDataAndType(Uri.fromFile(file), "text/*");
        } else if (fileExtend.equalsIgnoreCase("doc")
                || fileExtend.equalsIgnoreCase("docx")) {
            fileLinkIntent.setDataAndType(Uri.fromFile(file), "application/msword");
        } else if (fileExtend.equalsIgnoreCase("xls")
                || fileExtend.equalsIgnoreCase("xlsx")) {
            fileLinkIntent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.ms-excel");
        } else if (fileExtend.equalsIgnoreCase("ppt")
                || fileExtend.equalsIgnoreCase("pptx")) {
            fileLinkIntent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.ms-powerpoint");
        } else if (fileExtend.equalsIgnoreCase("pdf")) {
            fileLinkIntent.setDataAndType(Uri.fromFile(file), "application/pdf");
        } else if (fileExtend.equalsIgnoreCase("hwp")) {
            fileLinkIntent.setDataAndType(Uri.fromFile(file),
                    "application/haansofthwp");
        }else{
            fileLinkIntent.setDataAndType(Uri.fromFile(file),fileExtend);
        }
        PackageManager pm = context.getPackageManager();
        list = pm.queryIntentActivities(fileLinkIntent,
                PackageManager.GET_META_DATA);
        if (list.size() == 0) {
            alert = AlertDialogService.alert(context);
            alert.setMessage(fileName + "을 확인할 수 있는 앱이 설치되지 않았습니다.");
            alert.show();
        } else {
            context.startActivity(fileLinkIntent);
        }
    }
}
