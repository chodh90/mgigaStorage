package com.kt.gigastorage.mobile.service;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.kt.gigastorage.mobile.activity.R;

import java.io.File;

/**
 * Created by araise on 2016-11-23.
 */

public class TimerService {

    private static int count;

    private static String fileNm;

    private static ProgressDialog mProgDlg;

    private static AlertDialog.Builder alert;

    private static Context context;

    private static Handler handler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            alert = AlertDialogService.alert(context);
            if(msg.what == 0){
                String fileChk = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+context.getString(R.string.rootFoldrNm)+"/" + fileNm;
                File file = new File(fileChk);
                if(file.exists()){
                    mProgDlg.dismiss();
                    FileViewService.viewFile(context,"/"+context.getString(R.string.rootFoldrNm),fileNm);
                    handler.removeMessages(0);
                }else if(count == 5){
                    mProgDlg.dismiss();
                    alert.setMessage("원격지로부터 응답이 없습니다.");
                    alert.show();
                    Log.d("count22",">>>>>>>>>>>>>>"+count);
                    handler.removeMessages(0);
                }else{
                    handler.sendEmptyMessageDelayed(0, 3000);
                    count++;
                }
            }
        }
    };

    public static void timerStart(final String fileName,final Context mContext){
        count = 0;
        fileNm = fileName;
        context = mContext;
        mProgDlg = ProgressService.progress(context);
        mProgDlg.setMessage("파일을 다운로드 중입니다.");
        mProgDlg.show();
        handler.sendEmptyMessage(0);

    }

}