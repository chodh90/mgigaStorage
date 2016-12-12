package com.kt.gigastorage.mobile.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import com.kt.gigastorage.mobile.service.KbConverter;
import com.kt.gigastorage.mobile.utils.DeviceUtil;
import com.kt.gigastorage.mobile.utils.FileUtil;
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

import static android.view.View.GONE;

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
    public static Button btnSameTimeFile;

    /* email */
    public static LinearLayout email;
    public static LinearLayout emailInfo;
    public static ImageView emailImg;
    public static LinearLayout firstEmail;
    public static ImageView emailBtn;
    public static TextView viewEmailTitle;
    public static TextView viewEmailDate;
    public static TextView viewEmailNm;
    public static TextView viewFileSize;
    public static LinearLayout parentsLayout;
    public static LinearLayout childLayout;
    public static boolean emailCheck;

    /* 관련 BizNote */
    public static LinearLayout relationBiznote;
    public static ImageView relationBtn;
    public static LinearLayout relationImg;
    public static ImageView firstRelationImg;
    public static LinearLayout ascNote1;
    public static LinearLayout ascNote2;
    public static ImageView ascNoteImg1;
    public static ImageView ascNoteImg2;
    public static TextView ascNoteTxt1;
    public static TextView ascNoteTxt2;
    public static boolean relationBizChk;

    public static String userId;
    public static String devUuid;
    public static String intentFileId;
    public static String fileSize;
    public static String foldrWholePathNm;
    public static String intentAscNoteId1;
    public static String intentAscNoteId2;
    public static String ascNoteId1;
    public static String ascNoteId2;
    public static String amdDate;
    public static String emailFrom;
    public static String emailId;

    public static Drawable drawable;

    public static FileBasVO fileBasVO = new FileBasVO();
    public static NoteListVO noteListVO = new NoteListVO();

    List<Map<String,String>> fileEmailData = new ArrayList<>();
    List<Map<String, String>> ascNoteList = new ArrayList<>();
    Map<String,String> fileData = new HashMap<>();

    public static Context context;
    public static NoteFileAttrViewActivity activity;
    public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_file_attr_layout);
        mContext = DrawerLayoutViewActivity.context;
        context = NoteFileAttrViewActivity.this;
        activity = NoteFileAttrViewActivity.this;

        userId = SharedPreferenceUtil.getSharedPreference(context,"userId");
        devUuid = DeviceUtil.getDevicesUUID(context);
        firstEmail = (LinearLayout) findViewById(R.id.firstEmail);
        email = (LinearLayout) findViewById(R.id.email);
        emailInfo = (LinearLayout) findViewById(R.id.emailInfo);
        emailBtn = (ImageView)findViewById(R.id.emailBtn);
        emailCheck = false;
        btnSameTimeFile = (Button)findViewById(R.id.btnSameTimeFile);
        viewEmailTitle = (TextView) findViewById(R.id.emailTitle);
        viewEmailDate = (TextView) findViewById(R.id.emailDate);
        viewEmailNm = (TextView) findViewById(R.id.emailNm);
        viewFileNm = (TextView) findViewById(R.id.fileNm);
        viewFileSize = (TextView) findViewById(R.id.fileSize);
        viewFoldrWholePath = (TextView) findViewById(R.id.foldrWholePathNm);
        viewCretDate = (TextView) findViewById(R.id.cretDate);
        viewAmdDate = (TextView) findViewById(R.id.amdDate);
        imgView = (ImageView)findViewById(R.id.fileThum);
        relationBizChk = false;
        firstRelationImg = (ImageView)findViewById(R.id.firstRelationImg);
        relationBiznote = (LinearLayout)findViewById(R.id.relationBiznote);
        relationImg = (LinearLayout)findViewById(R.id.relationImg);
        relationBtn = (ImageView)findViewById(R.id.relationBtn);
        ascNoteImg1 = (ImageView)findViewById(R.id.ascNoteImg1);
        ascNoteImg2 = (ImageView)findViewById(R.id.ascNoteImg2);
        ascNoteTxt1 = (TextView)findViewById(R.id.ascNoteTxt1);
        ascNoteTxt2 = (TextView)findViewById(R.id.ascNoteTxt2);
        ascNote1 = (LinearLayout)findViewById(R.id.ascNote1);
        ascNote2 = (LinearLayout)findViewById(R.id.ascNote2);

        findViewById(R.id.topBack).setOnClickListener(closeActivity);
        Intent intent = getIntent();

        intentFileId = intent.getExtras().getString("fileId");
        intentAscNoteId1 = intent.getExtras().getString("ascNoteId1");
        intentAscNoteId2 = intent.getExtras().getString("ascNoteId2");
        foldrWholePathNm = intent.getExtras().getString("foldrWholePathNm");

        fileBasVO.setFileId(intentFileId);

        noteListVO.setAscNoteId1(intentAscNoteId1);
        noteListVO.setAscNoteId2(intentAscNoteId2);

        fileAttrList();

        new DownloadImagesTask().execute("http://222.106.202.145:8080/GIGA_Storage/imgFileThum.do?fileId="+intentFileId, "0", "", "");

        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(emailCheck == false){
                    emailInfo.setVisibility(View.GONE);
                    drawable = getResources().getDrawable(R.drawable.ico_24dp_email_open);
                }else{
                    emailInfo.setVisibility(View.VISIBLE);
                    drawable = getResources().getDrawable(R.drawable.ico_24dp_email_close);
                }
                emailBtn.setImageDrawable(drawable);
                emailCheck = !emailCheck;
            }
        });

        relationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(relationBizChk == false){
                    relationImg.setVisibility(View.GONE);
                    drawable = getResources().getDrawable(R.drawable.ico_24dp_email_open);
                }else{
                    relationImg.setVisibility(View.VISIBLE);
                    drawable = getResources().getDrawable(R.drawable.ico_24dp_email_close);
                }
                relationBtn.setImageDrawable(drawable);
                relationBizChk = !relationBizChk;
            }
        });

        firstRelationImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoteFileAttrViewActivity.activity, TempActivity.class);
                intent.putExtra("emailFrom", emailFrom);

                startActivity(intent);
            }
        });

        btnSameTimeFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoteFileAttrViewActivity.activity, TempActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("amdDate", amdDate);

                startActivity(intent);
            }
        });

        ascNoteImg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString("noteId",ascNoteId1);
                DrawerLayoutViewActivity.activity.changeBizFragment("attribute",args);
                activity.finish();
            }
        });

        ascNoteImg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString("noteId",ascNoteId2);
                DrawerLayoutViewActivity.activity.changeBizFragment("attribute",args);
                activity.finish();
            }
        });
    }

    /*Button.OnClickListener intentSameTimeFile = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(NoteFileAttrViewActivity.activity, TempActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("amdDate", amdDate);

            startActivity(intent);
        }
    };*/

    /*Button.OnClickListener intentSendFile = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(NoteFileAttrViewActivity.activity, TempActivity.class);
            intent.putExtra("emailFrom", emailFrom);

            startActivity(intent);
        }
    };*/

    /*Button.OnClickListener intentTogetherPerson = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(NoteFileAttrViewActivity.activity, PopupCcActivity.class);
            intent.putExtra("emailId", emailId);

            startActivity(intent);
        }
    };*/

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
                        long value = Long.parseLong(fileSize);
                        String size = KbConverter.convertBytesToSuitableUnit(value);
                        viewFileSize.setText(size);
                        viewFoldrWholePath.setText(foldrWholePathNm);
                        viewCretDate.setText(fileData.get("cretDate"));
                        viewAmdDate.setText(fileData.get("amdDate"));

                        amdDate = fileData.get("amdDate");
                    }

                    if (fileEmailData != null && fileEmailData.size() != 0) {
                        for (int i = 0; i < fileEmailData.size(); i++) {
                            if(i == 0){
                                viewEmailTitle.setText(fileEmailData.get(0).get("emailSbjt").toString());
                                viewEmailDate.setText(fileEmailData.get(0).get("sendDate").toString());
                                viewEmailNm.setText(fileEmailData.get(0).get("emailFrom").toString());
                                firstEmail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(NoteFileAttrViewActivity.activity, PopupCcActivity.class);
                                        intent.putExtra("emailId", emailId);
                                        startActivity(intent);
                                    }
                                });
                            }else{
                                TextView emailTitle = new TextView(NoteFileAttrViewActivity.this);
                                TextView emailDate = new TextView(NoteFileAttrViewActivity.this);
                                TextView emailNm = new TextView(NoteFileAttrViewActivity.this);
                                TextView emailBottom = new TextView(NoteFileAttrViewActivity.this);

                                parentsLayout = new LinearLayout(NoteFileAttrViewActivity.this);
                                childLayout = new LinearLayout(NoteFileAttrViewActivity.this);

                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);


                                float dp = NoteFileAttrViewActivity.this.getResources().getDisplayMetrics().density;

                                parentsLayout.setLayoutParams(layoutParams);
                                parentsLayout.setOrientation(LinearLayout.HORIZONTAL);

                                int image = 20;
                                int width = 250;
                                int left = 35;
                                int top = 10;
                                int imageDp = Math.round(image * dp);
                                int leftDp = Math.round(left * dp);
                                int topDp = Math.round(top * dp);

                                emailImg = new ImageView(NoteFileAttrViewActivity.this);

                                drawable = getResources().getDrawable(R.drawable.ico_18dp_biznote_more);
                                LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(imageDp,imageDp);

                                imgParams.leftMargin = leftDp;
                                imgParams.topMargin = topDp;

                                emailImg.setId(i);
                                emailImg.setImageDrawable(drawable);
                                emailImg.setLayoutParams(imgParams);

                                emailImg.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(NoteFileAttrViewActivity.activity, TempActivity.class);
                                        intent.putExtra("emailFrom", emailFrom);

                                        startActivity(intent);
                                    }
                                });

                                parentsLayout.addView(emailImg);
                                emailInfo.addView(parentsLayout);

                                childLayout.setLayoutParams(layoutParams);
                                childLayout.setOrientation(LinearLayout.VERTICAL);
                                childLayout.setId(i);

                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

                                top = 10;
                                left = 15;


                                topDp = Math.round(top * dp);
                                leftDp = Math.round(left * dp);
                                int widthDp = Math.round(width * dp);


                                params.leftMargin = leftDp;
                                emailTitle.setLayoutParams(params);
                                emailTitle.setText(fileEmailData.get(i).get("emailSbjt").toString());
                                emailTitle.setTextColor(getResources().getColor(R.color.darkGray));
                                emailTitle.setWidth(widthDp);
                                childLayout.addView(emailTitle);

                                leftDp = Math.round(left * dp);
                                params.leftMargin = leftDp;
                                emailDate.setLayoutParams(params);
                                emailDate.setText(fileEmailData.get(i).get("sendDate").toString());
                                emailDate.setTextColor(getResources().getColor(R.color.disabledGrayToNavi));
                                emailDate.setTextSize(12);
                                emailDate.setWidth(widthDp);
                                childLayout.addView(emailDate);

                                topDp = Math.round(4 * dp);
                                leftDp = Math.round(left * dp);
                                params.topMargin = topDp;
                                params.leftMargin = leftDp;

                                emailNm.setLayoutParams(params);
                                emailNm.setText(fileEmailData.get(i).get("emailFrom").toString());
                                emailNm.setTextColor(getResources().getColor(R.color.disabledGrayToNavi));
                                emailNm.setTextSize(12);
                                emailNm.setWidth(widthDp);

                                childLayout.addView(emailNm);

                                emailBottom.setText("\n");
                                childLayout.addView(emailBottom);

                                parentsLayout.addView(childLayout);

                                childLayout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Object position = view.getId();
                                        int index = (int)position;
                                        Intent intent = new Intent(NoteFileAttrViewActivity.activity, PopupCcActivity.class);
                                        intent.putExtra("emailId", emailId);
                                        startActivity(intent);
                                    }
                                });

                                emailInfo.setVisibility(GONE);

                            }
                        }
                    }else{
                        email.setVisibility(View.GONE);
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
                    ascNoteList = new ArrayList<Map<String, String>>();
                    Gson gson = new Gson();
                    ascNoteList = gson.fromJson(response.body().get("listData"), List.class);

                    if(ascNoteList != null && ascNoteList.size() != 0) {
                        for(int i=0; i<ascNoteList.size(); i++) {
                            Object ascNoteFileId = ascNoteList.get(i).get("fileId");
                            Object ascNote1 = ascNoteList.get(0).get("noteId");
                            Object ascNote2 = ascNoteList.get(1).get("noteId");
                            ascNoteId1 = ascNote1.toString();
                            ascNoteId2 = ascNote2.toString();
                            ascNoteTxt1.setText(ascNoteList.get(0).get("noteNm"));
                            ascNoteTxt2.setText(ascNoteList.get(1).get("noteNm"));
                            new DownloadImagesTask().execute("http://222.106.202.145:8080/GIGA_Storage/imgFileThum.do?fileId="+ascNoteFileId.toString(), ""+(i+1), ascNoteList.get(i).get("noteNm"), ascNoteList.get(i).get("etsionNm"));
                        }
                    }else{
                        relationBiznote.setVisibility(GONE);
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
        String etsionNm = "";

        @Override
        protected Bitmap doInBackground(String... params) {
            gubun = params[1];
            noteNm = params[2];
            etsionNm = params[3];
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
                } else if(gubun.equals("2")) {
                    ascNote2.setVisibility(View.VISIBLE);
                    ascNoteImg2.setImageBitmap(result);
                }
            }else {
                if(gubun.equals("1")) {
                    ascNote1.setVisibility(View.VISIBLE);
                    ascNoteImg1.setImageResource(FileUtil.getIconByEtsion(etsionNm));
                } else if(gubun.equals("2")) {
                    ascNote2.setVisibility(View.VISIBLE);
                    ascNoteImg2.setImageResource(FileUtil.getIconByEtsion(etsionNm));
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
