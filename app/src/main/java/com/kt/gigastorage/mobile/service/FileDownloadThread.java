package com.kt.gigastorage.mobile.service;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Message;
import android.util.Base64;

import com.kt.gigastorage.mobile.activity.MainActivity;
import com.kt.gigastorage.mobile.activity.R;
import com.kt.gigastorage.mobile.utils.DeviceUtil;
import com.kt.gigastorage.mobile.utils.SharedPreferenceUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by araise on 2016-10-26.
 */

public class FileDownloadThread extends AsyncTask<String, Integer, String> {

        public static Message message;
        public static ProgressDialog mProgDlg;

        private final int 	SUCCESS = 0;		//다운로드 성공
        private final int 	FAILED = 1;			//다운로드 실패
        public static Context context;

        public static String userId = SharedPreferenceUtil.getSharedPreference(MainActivity.context,"userId");
        public static String password = SharedPreferenceUtil.getSharedPreference(MainActivity.context,"password");
        public static String fileNm;
        public static String appPlay;
        public static  String devUuid;

        private Context mContext;

        public FileDownloadThread(Context mContext) {
            context = mContext;
        }

        @Override
        protected void onPreExecute() {
            devUuid = DeviceUtil.getDevicesUUID(context);
            message = new Message();
            mProgDlg = new ProgressDialog(context);
            mProgDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgDlg.setMessage("다운로드 중입니다...");
            mProgDlg.show();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try{
                fileNm = params[1];
                appPlay = params[3];

                String[] foldr = params[0].split("/");
                String encodePath = "";
                String encodeFile = URLEncoder.encode(params[1], "UTF-8");
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + context.getString(R.string.rootFoldrNm);
                for (int i = 0; i < foldr.length; i++) {
                    if (i == 0) {
                        encodePath = URLEncoder.encode(foldr[i], "UTF-8");
                    } else {
                        encodePath += "/" + URLEncoder.encode(foldr[i], "UTF-8");
                    }
                }
                encodeFile = encodeFile.replaceAll("\\+", "%20");
                encodePath = encodePath.replaceAll("\\+", "%20");

                String adress = "";
                Resources res = context.getResources();
                String hostIp = String.format(res.getString(R.string.nasUrl), SharedPreferenceUtil.getSharedPreference(context,"hostIp"));
                if(params[2] == null || params[2].equals("")){
                    adress = String.format("%s/gs-%s/%s-gs/%s/%s", hostIp, userId, userId, encodePath, encodeFile);
                }else{
                    adress = String.format("%s/gs-%s/%s/%s/%s", hostIp, userId, params[2], encodePath, encodeFile);
                }


                URL url = new URL(adress);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                String authStr = "gs-" + userId + ":" + password;
                byte[] encodeData = authStr.getBytes("UTF-8");
                String encoded = Base64.encodeToString(encodeData,Base64.DEFAULT);

                //헤더
                connection.setRequestProperty("Authorization", "Basic " + encoded);

                File file = new File(filePath+ "/" + params[1]);

                inputStream = connection.getInputStream();
                outputStream = new FileOutputStream(file);

                int downloadSize = 0;

                byte[] fileReader = new byte[4096];
                int read = 0;

                while ((read = inputStream.read(fileReader)) > -1) {
                    outputStream.write(fileReader, 0, read);
                    downloadSize += read;
                }

                int responseCode = connection.getResponseCode();

                if(responseCode == 200) {
                    message.what = SUCCESS;
                }
                inputStream.close();
                outputStream.close();

                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                message.what = FAILED;
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            mProgDlg.dismiss();
            AlertDialog.Builder alert = AlertDialogService.alert(context);
            switch (message.what) {
                case SUCCESS:
                    FileService.syncFoldrInfo();
                    if(appPlay.equals("Y")){
                        FileViewService.viewFile(context,"/Mobile",fileNm);
                    }else{
                        alert.setMessage("파일 다운로드를 성공하였습니다.");
                        alert.show();
                    }
                    break;
                case FAILED:
                    alert.setMessage("파일 다운로드 실패하였습니다.");
                    alert.show();
                    break;
            }

            super.onPostExecute(s);
        }
}



