package com.kt.gigastorage.mobile.gcm;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.kt.gigastorage.mobile.service.FileService;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import static com.kt.gigastorage.mobile.webservice.impl.RestServiceImpl.CONNECT_TIMEOUT;
import static com.kt.gigastorage.mobile.webservice.impl.RestServiceImpl.READ_TIMEOUT;

/**
 * Created by zeroeun on 2016-09-27.
 */

public class GigaGcmListenerService extends GcmListenerService {

    private static final String TAG = GigaGcmListenerService.class.getName();

    public static final String SERVER_URL = "http://222.106.202.145/GIGA_Storage/webservice/rest";
    public static final String NAS_URL = "http://222.106.202.145/namespace/ifs/home";
    /*public static Context context = DrawerLayoutViewActivity.context;*/


    public static Message message;
    public static File file;

    private final int 	SUCCESS = 0;		//다운로드 성공
    private final int 	FAILED = 1;			//다운로드 실패

    public static String fromFoldr = "";
    public static String fromFileNm = "";
    public static String userId = "";
    public static String queId = "";
    public static String command = "";
    public static String fromDevUuid = "";
    public static String toDevUuid = "";
    public static String toFoldr = "";
    public static String toOsCd = "";
    public static String fromOsCd = "";
    public static String authToken = "";
    public static String token = "";
    public static String nasStatusCode = "";
    /*public static ProgressDialog dialog;*/

    /**
     * 푸시가 수신될 때 호출됨
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {

        String message = data.getString("message");

        // temp파라미터 0eun
        String mapData = data.getString("data");
        authToken = data.getString("authToken");
        byte[] decoded  = Base64.decode(authToken.getBytes(),Base64.DEFAULT);
        token = new String(decoded);

        try{
            JSONObject jsonObj = new JSONObject(mapData);
            fromFoldr =  jsonObj.get("fromFoldr").toString();
            fromFileNm = jsonObj.get("fromFileNm").toString();
            userId = jsonObj.get("fromUserId").toString();
            toOsCd = jsonObj.get("toOsCd").toString();
            queId = jsonObj.get("queId").toString();
            command = jsonObj.get("command").toString();
            fromDevUuid = jsonObj.get("fromDevUuid").toString();
            toDevUuid = jsonObj.get("toDevUuid").toString();
            toFoldr = jsonObj.getString("toFoldr").toString();

        }catch (Exception e){

        }



        Log.d(TAG, "From: " + from + ", Message: " + message);

        // TODO ?
        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        if(command.equals("D")) { //GCM command가 다운로드 일때

            new FileDownLoadTask().execute(fromFoldr,fromFileNm);

        }
        if(command.equals("U") || toOsCd.equals("G")) { //GCM command가 업로드 일때

            if(toOsCd.equals("G")) {
                new FileUpLoadTask().execute(toFoldr,fromFileNm,queId);
            } else {
                new FileUpLoadTask().execute(fromFoldr,fromFileNm,queId);
            }

        }



    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     * 생성 및 수신 된 GCM 메시지를 포함하는 간단한 알림을 표시합니다.
     * /*@param message GCM message received.*/

    private class FileUpLoadTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            message = new Message();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            try{
                HttpURLConnection conn = null;
                String adress = "";
                if(toOsCd.equals("G")){
                    adress =  String.format("%s/gs-%s/%s-gs/%s/?recursive=true", NAS_URL,userId, userId, params[0]);
                }else{
                    adress =  String.format("%s/gs-%s/%s/%s/?recursive=true", NAS_URL, userId, fromDevUuid, params[0]);
                }
                URL url = new URL(adress);
                conn = (HttpURLConnection) url.openConnection();

                String authStr = "gs-" + userId + ":" + token;
                byte[] encodeData = authStr.getBytes("UTF-8");
                String encoded = Base64.encodeToString(encodeData,Base64.DEFAULT);

                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Authorization", "Basic " + new String(encoded));
                conn.setRequestProperty("x-isi-ifs-target-type", "container");
                conn.connect();

                int status = conn.getResponseCode();

                if(status == 200 || status == 204){
                    try{

                        String[] foldr = params[0].split("/");
                        String encodePath = "";
                        String encodeFile = URLEncoder.encode(params[1],"UTF-8");
                        for(int i=0; i<foldr.length; i++){
                            if(i == 0){
                                encodePath = URLEncoder.encode(foldr[i], "UTF-8");
                            }else{
                                encodePath += "/" + URLEncoder.encode(foldr[i], "UTF-8");
                            }
                        }
                        encodePath = encodePath.replaceAll("\\+", "%20");

                        if(toOsCd.equals("G")){
                            adress =  String.format("%s/gs-%s/%s-gs/%s/%s?overwrite=true", NAS_URL, userId, userId, encodePath, encodeFile);
                        }else{
                            adress =  String.format("%s/gs-%s/%s/%s/%s?overwrite=true", NAS_URL, userId, fromDevUuid, encodePath, encodeFile);
                        }

                        File file = new File(Environment.getExternalStorageDirectory()+"/"+fromFoldr+"/",params[1]); //params에 filePath 넘겨받음


                        url = new URL(adress);

                        HttpURLConnection connection = (HttpURLConnection)url.openConnection(); //연결

                        // 커넥션 설정(읽기쓰기가능, POST)
                        connection.setDoInput(true);
                        connection.setDoOutput(true);
                        connection.setUseCaches(false);
                        connection.setRequestMethod("PUT");


                        authStr = "gs-" + userId + ":" + token;
                        encodeData = authStr.getBytes("UTF-8");
                        encoded = Base64.encodeToString(encodeData,Base64.DEFAULT);

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
                            dataStream.write(buffer, 0, bufferSize);
                            byteAvaliable = fileInputStream.available();
                            bufferSize = Math.min(byteAvaliable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                        }

                        fileInputStream.close();

                        dataStream.flush(); // 써진 버퍼를 stream에 출력.

                        int responseCode = connection.getResponseCode();

                        if(responseCode == 200 || responseCode == 204){
                            message.what = SUCCESS;
                            nasStatusCode = "100";
                        }
                        dataStream.close();
                    }catch (Exception e){
                        e.printStackTrace();
                        message.what = FAILED;
                        nasStatusCode = "999";
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
                message.what = FAILED;
                nasStatusCode = "999";
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            switch (message.what) {
                case SUCCESS:

                        new FileUpldCompltTask().execute(nasStatusCode); //업로드 성공 Complete 수행

                    break;

                case FAILED:

                        new FileUpldCompltTask().execute(nasStatusCode); //업로드 실패 Complete 수행

                    break;
            }
            super.onPostExecute(s);
        }
    }

    // AsyncTask 1: doInBackground의 가변인수 매개변수 타입, 2: onProgressUpdate의 가변인수 매개변수 타입, 3:onPostExecute 매개변수 타입
    private class FileDownLoadTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            /*dialog = ProgressService.progress(MainActivity.context);
            dialog.show();*/
            message = new Message();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try{
                String[] foldr = params[0].split("/");
                String encodePath = "";
                String encodeFile = URLEncoder.encode(params[1], "UTF-8");
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "Mobile";
                for (int i = 0; i < foldr.length; i++) {
                    if (i == 0) {
                        encodePath = foldr[i];
                    } else {
                        encodePath += "/" + URLEncoder.encode(foldr[i], "UTF-8");
                    }
                }
                encodeFile = encodeFile.replaceAll("\\+", "%20");
                encodePath = encodePath.replaceAll("\\+", "%20");

                String adress = String.format("%s/gs-%s/%s/%s/%s", NAS_URL, userId, fromDevUuid, encodePath, encodeFile);

                URL url = new URL(adress);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                String authStr = "gs-" + userId + ":" + token;
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

                if(responseCode == 200 || responseCode == 204) {
                    /*dialog.dismiss();*/
                    message.what = SUCCESS;
                }
                inputStream.close();
                outputStream.close();

                connection.disconnect();
            } catch (Exception e) {
                /*dialog.dismiss();*/
                message.what = FAILED;
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            /*AlertDialog.Builder alert = AlertDialogService.alert(MainActivity.context);*/
            switch (message.what) {
                case SUCCESS:

                    FileService.syncFoldrInfo();

                    /*alert.setMessage("파일이 다운로드 되었습니다.");*/

                    break;
                case FAILED:
                    /*alert.setMessage("파일 다운로드가 실패하였습니다.");*/
                    break;
            }
            /*alert.show();*/
            super.onPostExecute(s);
        }
    }

    //파일 업로드 Complete Thread
    private class FileUpldCompltTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {

            if(queId != null || !queId.equals("")){

                HttpURLConnection conn = null;

                OutputStream os = null;
                InputStream is = null;
                ByteArrayOutputStream baos = null;
                String address = SERVER_URL+"/upldCmplt.do";
                try {
                    URL url = new URL(address);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(CONNECT_TIMEOUT * 1000);
                    conn.setReadTimeout(READ_TIMEOUT * 1000);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Cache-Control", "no-cache");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject job = new JSONObject();

                    job.put("queId", queId);
                    job.put("authToken",authToken);
                    job.put("nasStatusCode",nasStatusCode);
                    os = conn.getOutputStream();
                    os.write(job.toString().getBytes());
                    os.flush();
                    String response;

                    int responseCode = conn.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {

                        is = conn.getInputStream();
                        baos = new ByteArrayOutputStream();
                        byte[] byteBuffer = new byte[1024];
                        byte[] byteData = null;
                        int nLength = 0;
                        while ((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                            baos.write(byteBuffer, 0, nLength);
                        }
                        byteData = baos.toByteArray();

                        response = new String(byteData);

                        Log.i(TAG, "DATA response = " + response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    message.what = FAILED;
                }
            }
            return null;
        }

    }

    /*private class FileUpldFailCompltTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {

            if(queId != null || !queId.equals("")){

                HttpURLConnection conn = null;

                OutputStream os = null;
                InputStream is = null;
                ByteArrayOutputStream baos = null;
                String address = SERVER_URL+"/upldCmplt.do";
                try {
                    URL url = new URL(address);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(CONNECT_TIMEOUT * 1000);
                    conn.setReadTimeout(READ_TIMEOUT * 1000);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Cache-Control", "no-cache");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject job = new JSONObject();

                    job.put("queId", queId);
                    job.put("authToken",authToken);
                    job.put("nasStatusCode",999);
                    os = conn.getOutputStream();
                    os.write(job.toString().getBytes());
                    os.flush();
                    String response;

                    int responseCode = conn.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {

                        is = conn.getInputStream();
                        baos = new ByteArrayOutputStream();
                        byte[] byteBuffer = new byte[1024];
                        byte[] byteData = null;
                        int nLength = 0;
                        while ((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                            baos.write(byteBuffer, 0, nLength);
                        }
                        byteData = baos.toByteArray();

                        response = new String(byteData);

                        Log.i(TAG, "DATA response = " + response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    message.what = FAILED;
                }
            }
            return null;
        }


    }*/
}
