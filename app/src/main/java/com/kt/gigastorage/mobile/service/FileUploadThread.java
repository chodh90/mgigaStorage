package com.kt.gigastorage.mobile.service;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kt.gigastorage.mobile.activity.DrawerLayoutViewActivity;
import com.kt.gigastorage.mobile.activity.FileAttrViewActivity;
import com.kt.gigastorage.mobile.activity.FileSearchViewActivity;
import com.kt.gigastorage.mobile.activity.MainActivity;
import com.kt.gigastorage.mobile.activity.R;
import com.kt.gigastorage.mobile.activity.SendNasViewActivity;
import com.kt.gigastorage.mobile.utils.SharedPreferenceUtil;
import com.kt.gigastorage.mobile.vo.ComndQueueVO;
import com.kt.gigastorage.mobile.webservice.impl.RestServiceImpl;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by araise on 2016-10-26.
 */

public class FileUploadThread extends AsyncTask<String, Integer, String> {
    public static Message message;
    private final int 	SUCCESS = 0;		//다운로드 성공
    private final int 	FAILED = 1;			//다운로드 실패

    public static Context context;
    public static String userId = SharedPreferenceUtil.getSharedPreference(MainActivity.context,"userId");
    public static String password = SharedPreferenceUtil.getSharedPreference(MainActivity.context,"password");
    public static String foldrPath = "";
    public static String fileNm = "";
    public static String fileId = "";
    public static String devUuid = "";
    public static String foldrId = "";
    public static ProgressDialog mProgDlg;
    public static Dialog alert;

    public FileUploadThread (Context mContext){
        context = mContext;
    }

    @Override
    protected void onPreExecute() {
        message = new Message();
        mProgDlg = new ProgressDialog(context);
        mProgDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgDlg.setMessage("업로드 중입니다...");
        mProgDlg.show();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        try{

            HttpURLConnection conn = null;
            String adress =  String.format("%s/gs-%s/%s-gs/%s?recursive=true", context.getString(R.string.nasUrl),userId, userId, params[0]);
            URL url = new URL(adress);
            conn = (HttpURLConnection) url.openConnection();

            String authStr = "gs-" + userId + ":" + password;
            byte[] encodeData = authStr.getBytes("UTF-8");
            String encoded = Base64.encodeToString(encodeData,Base64.DEFAULT);

            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Authorization", "Basic " + new String(encoded));
            conn.setRequestProperty("x-isi-ifs-target-type", "container");
            conn.connect();

            int status = conn.getResponseCode();
            if(status == 200 || status == 204){
                try{
                    foldrPath = params[0];
                    fileNm = params[1];
                    fileId = params[2];
                    foldrId = params[4];

                    String[] foldr = foldrPath.split("/");
                    String encodePath = "";
                    String encodeFile = URLEncoder.encode(fileNm,"UTF-8");
                    for(int i=0; i<foldr.length; i++){
                        if(i == 0){
                            encodePath = URLEncoder.encode(foldr[i], "UTF-8");
                        }else{
                            encodePath += "/" + URLEncoder.encode(foldr[i], "UTF-8");
                        }
                    }
                    encodePath = encodePath.replaceAll("\\+", "%20");

                    adress =  String.format("%s/gs-%s/%s-gs/%s/%s?overwrite=true", context.getString(R.string.nasUrl),userId, userId, encodePath, encodeFile);

                    File file = new File(Environment.getExternalStorageDirectory()+"/"+params[3]+"/",fileNm); //params에 filePath 넘겨받음

                    url = new URL(adress);

                    HttpURLConnection connection = (HttpURLConnection)url.openConnection(); //연결

                    // 커넥션 설정(읽기쓰기가능, POST)
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.setRequestMethod("PUT");

                    //헤더
                    connection.setRequestProperty("Authorization", "Basic " + encoded);
                    connection.setRequestProperty("x-isi-ifs-target-type","object");
                    connection.connect();

                    FileInputStream fileInputStream = new FileInputStream(file);
                    DataOutputStream dataStream = new DataOutputStream(connection.getOutputStream());

                    int byteAvaliable = fileInputStream.available();
                    int maxBufferSize = 1024;
                    int bufferSize = Math.min(byteAvaliable, maxBufferSize);
                    byte[] buffer = new byte[bufferSize];

                    int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    while (bytesRead > 0) {
                        try {
                            dataStream.write(buffer, 0, bufferSize);
                        }catch (Exception e){
                            Log.d("TEST",">>> : "+e.getMessage());
                        }
                        byteAvaliable = fileInputStream.available();
                        bufferSize = Math.min(byteAvaliable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    fileInputStream.close();

                    dataStream.flush(); // 써진 버퍼를 stream에 출력.

                    int responseCode = connection.getResponseCode();

                    if(responseCode == 200 || responseCode == 204){
                        message.what = SUCCESS;
                    }
                    dataStream.close();
                }catch (Exception e){
                    message.what = FAILED;
                }
            }
        }catch (Exception e){
            message.what = FAILED;
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        switch (message.what) {
            case SUCCESS:

                if(fileId != null || !fileId.equals("")){

                    ComndQueueVO comndQueueVO = new ComndQueueVO();
                    comndQueueVO.setUserId(userId);
                    comndQueueVO.setToFoldr(foldrPath);
                    comndQueueVO.setFileId(fileId);
                    comndQueueVO.setToFileNm(fileNm);
                    comndQueueVO.setFoldrId(foldrId);

                    Call<JsonObject> nasUpldCmpltCall = RestServiceImpl.getInstance(null).nasUpldCmplt(comndQueueVO);
                    nasUpldCmpltCall.enqueue(new Callback<JsonObject>() {

                        @Override
                        public void onResponse(Response<JsonObject> response) {
                            mProgDlg.dismiss();
                            AlertDialog.Builder alert = AlertDialogService.alert(context);
                            if(response.isSuccess()) {
                                Gson gson = new Gson();
                                int statusCode = gson.fromJson(response.body().get("statusCode"), Integer.class);
                                String message = new ResponseFailCode().responseFail(statusCode);
                                if(statusCode == 100){
                                    SendNasViewActivity.activity.finish();
                                    alert.setMessage("NAS로 전송을 성공하였습니다.");
                                }else{
                                    alert.setMessage(message);
                                }
                                alert.show();
                            }

                        }
                        @Override
                        public void onFailure(Throwable t) {
                            Log.e("error", " >>> " + t.getMessage());
                            Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;

            case FAILED:
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setMessage("웹서버에 파일 업로드 실패");
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                    }
                });
                alert.show();
                break;
        }
        super.onPostExecute(s);
    }
}

