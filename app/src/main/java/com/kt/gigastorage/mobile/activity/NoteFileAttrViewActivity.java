package com.kt.gigastorage.mobile.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kt.gigastorage.mobile.utils.DeviceUtil;
import com.kt.gigastorage.mobile.utils.SharedPreferenceUtil;
import com.kt.gigastorage.mobile.vo.FileBasVO;
import com.kt.gigastorage.mobile.vo.NoteListVO;
import com.kt.gigastorage.mobile.webservice.impl.RestServiceImpl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by araise on 2016-10-18.
 */

public class NoteFileAttrViewActivity extends Activity{

    /* thumnail */
    public static ImageView imgView;
    /* file name */
    public static TextView viewFileNm;

    /*  attr */
    public static TextView viewFoldrWholePath;
    public static TextView viewCretDate;
    public static TextView viewAmdDate;

    /* email */
    public static TextView viewEmailTitle;
    public static TextView viewEmailDate;
    public static TextView viewEmailNm;
    public static TextView viewFileSize;

    /* 관련 BizNote */
    public static LinearLayout ascNote1;
    public static LinearLayout ascNote2;
    public static ImageView ascNoteImg1;
    public static ImageView ascNoteImg2;
    public static TextView ascNoteTxt1;
    public static TextView ascNoteTxt2;

    public static String userId;
    public static String devUuid;
    public static String intentFileId;
    public static String fileSize;
    public static String foldrWholePathNm;
    public static String intentAscNoteId1;
    public static String intentAscNoteId2;
    public static String amdDate;
    public static String emailFrom;
    public static String emailId;

    public static FileBasVO fileBasVO = new FileBasVO();
    public static NoteListVO noteListVO = new NoteListVO();

    List<Map<String,String>> fileEmailData = new ArrayList<>();
    List<Map<String, String>> ascNoteList = new ArrayList<>();
    Map<String,String> fileData = new HashMap<>();

    public static Context context;
    public static NoteFileAttrViewActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_file_attr_layout);

        context = NoteFileAttrViewActivity.this;
        activity = NoteFileAttrViewActivity.this;

        userId = SharedPreferenceUtil.getSharedPreference(context,"userId");
        devUuid = DeviceUtil.getDevicesUUID(context);
        viewEmailTitle = (TextView) findViewById(R.id.emailTitle);
        viewEmailDate = (TextView) findViewById(R.id.emailDate);
        viewEmailNm = (TextView) findViewById(R.id.emailNm);
        viewFileNm = (TextView) findViewById(R.id.fileNm);
        viewFileSize = (TextView) findViewById(R.id.fileSize);
        viewFoldrWholePath = (TextView) findViewById(R.id.foldrWholePath);
        viewCretDate = (TextView) findViewById(R.id.cretDate);
        viewAmdDate = (TextView) findViewById(R.id.amdDate);
        imgView = (ImageView)findViewById(R.id.fileThum);
        ascNoteImg1 = (ImageView)findViewById(R.id.ascNoteImg1);
        ascNoteImg2 = (ImageView)findViewById(R.id.ascNoteImg2);
        ascNoteTxt1 = (TextView)findViewById(R.id.ascNoteTxt1);
        ascNoteTxt2 = (TextView)findViewById(R.id.ascNoteTxt2);
        ascNote1 = (LinearLayout)findViewById(R.id.ascNote1);
        ascNote2 = (LinearLayout)findViewById(R.id.ascNote2);

        findViewById(R.id.topBack).setOnClickListener(closeActivity);
        findViewById(R.id.btnSameTimeFile).setOnClickListener(intentSameTimeFile);
        findViewById(R.id.btnSendFile).setOnClickListener(intentSendFile);
        findViewById(R.id.btnTogetherPerson).setOnClickListener(intentTogetherPerson);

        Intent intent = getIntent();

        intentFileId = intent.getExtras().getString("fileId");
        intentAscNoteId1 = intent.getExtras().getString("ascNoteId1");
        intentAscNoteId2 = intent.getExtras().getString("ascNoteId2");

        fileBasVO.setFileId(intentFileId);

        noteListVO.setAscNoteId1(intentAscNoteId1);
        noteListVO.setAscNoteId2(intentAscNoteId2);

        fileAttrList();

        new DownloadImagesTask().execute("http://222.106.202.145:8080/GIGA_Storage/imgFileThum.do?fileId="+intentFileId, "0", "");

    }

    Button.OnClickListener intentSameTimeFile = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(NoteFileAttrViewActivity.activity, TempActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("amdDate", amdDate);

            startActivity(intent);
        }
    };

    Button.OnClickListener intentSendFile = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(NoteFileAttrViewActivity.activity, TempActivity.class);
            intent.putExtra("emailFrom", emailFrom);

            startActivity(intent);
        }
    };

    Button.OnClickListener intentTogetherPerson = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(NoteFileAttrViewActivity.activity, PopupCcActivity.class);
            intent.putExtra("emailId", emailId);

            startActivity(intent);
        }
    };

    Button.OnClickListener closeActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    public void fileAttrList() {

        FileBasVO fileBasVO = new FileBasVO();
        fileBasVO.setUserId(SharedPreferenceUtil.getSharedPreference(context,"userId"));
        fileBasVO.setDevUuid(devUuid);
        fileBasVO.setFileId(intentFileId);

        Call<JsonObject> fileAttrListCall = RestServiceImpl.getInstance(null).fileAttrList(fileBasVO);
        fileAttrListCall.enqueue( new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response) {
                if(response.isSuccess()) {
                    Gson gson = new Gson();
                    fileEmailData = gson.fromJson(response.body().get("listData2"), List.class);
                    fileData = gson.fromJson(response.body().get("fileData"),Map.class);

                    if(fileData != null){
                        Object fileSizeObj = (Object) fileData.get("fileSize");
                        fileSize = fileSizeObj.toString();

                        viewFileNm.setText(fileData.get("fileNm"));
                        viewFileSize.setText(fileSize + "KB");
                        viewFoldrWholePath.setText(fileData.get("foldrWholePathNm"));
                        viewCretDate.setText(fileData.get("cretDate"));
                        viewAmdDate.setText(fileData.get("amdDate"));

                        amdDate = fileData.get("amdDate");
                    }

                    if(fileEmailData != null){
                        for(int i=0; i<fileEmailData.size(); i++){
                            viewEmailTitle.setText(fileEmailData.get(i).get("emailSbjt").toString());
                            viewEmailDate.setText(fileEmailData.get(i).get("sendDate").toString());
                            viewEmailNm.setText(fileEmailData.get(i).get("emailFrom").toString());

                            emailFrom = fileEmailData.get(i).get("emailFrom").toString();
                            emailId = fileEmailData.get(i).get("emailId").toString();
                        }
                    }

                    getNoteAscWebservice();
                } else {
                    Log.d("No Success Message :", response.message());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setMessage(context.getString(R.string.serverOut));
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
                alert.show();
            }
        });
    }

    public void getNoteAscWebservice() {

        Call<JsonObject> fileAttrListCall = RestServiceImpl.getInstance(null).listNoteAsc(noteListVO);
        fileAttrListCall.enqueue( new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response) {
                if(response.isSuccess()) {
                    Gson gson = new Gson();
                    ascNoteList = gson.fromJson(response.body().get("listData"), List.class);

                    if(ascNoteList != null) {
                        for(int i=0; i<ascNoteList.size(); i++) {
                            Object ascNoteFileId = ascNoteList.get(i).get("fileId");
                            new DownloadImagesTask().execute("http://222.106.202.145:8080/GIGA_Storage/imgFileThum.do?fileId="+ascNoteFileId.toString(), ""+(i+1), ascNoteList.get(i).get("noteNm"));
                        }
                    }
                } else {
                    Log.d("No Success Message :", response.message());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setMessage(context.getString(R.string.serverOut));
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
                alert.show();
            }
        });
    }

    public class DownloadImagesTask extends AsyncTask<String, Void, Bitmap> {

        String gubun = "";
        String noteNm = "";

        @Override
        protected Bitmap doInBackground(String... params) {
            gubun = params[1];
            noteNm = params[2];
            return download_Image(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if(result != null) {
                if(gubun.equals("0")) {
                    imgView.setImageBitmap(result);
                } else if(gubun.equals("1")) {
                    ascNote1.setVisibility(View.VISIBLE);
                    ascNoteImg1.setImageBitmap(result);
                    ascNoteTxt1.setText("[" + noteNm + "]");
                } else if(gubun.equals("2")) {
                    ascNote2.setVisibility(View.VISIBLE);
                    ascNoteImg2.setImageBitmap(result);
                    ascNoteTxt1.setText("[" + noteNm + "]");
                }
            }
        }

        private Bitmap download_Image(String params) {
            Bitmap bm = null;
            try {
                URL url = new URL(params);
                URLConnection conn = url.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (IOException e) {
                Log.e("Hub", "Error getting the image from server : " + e.getMessage());
            }
            return bm;
        }
    }
}
