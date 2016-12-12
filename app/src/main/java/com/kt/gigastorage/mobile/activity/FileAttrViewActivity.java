package com.kt.gigastorage.mobile.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kt.gigastorage.mobile.service.KbConverter;
import com.kt.gigastorage.mobile.service.ResponseFailCode;
import com.kt.gigastorage.mobile.utils.DeviceUtil;
import com.kt.gigastorage.mobile.utils.SharedPreferenceUtil;
import com.kt.gigastorage.mobile.vo.FileBasVO;
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

public class FileAttrViewActivity extends Activity{
    public static TextView viewEmailTitle;
    public static TextView viewEmailDate;
    public static TextView viewEmailNm;
    public static TextView viewFileEtsionNm;
    public static TextView viewFileSize;
    public static TextView viewFoldrWholePathNm;
    public static TextView viewCretDate;
    public static TextView viewAmdDate;
    public static ImageView imgView;
    public static TextView tagInfo;
    public static CheckBox isChecked;
    public static TextView sharText;
    public static String[] tag;
    public static Button editBtn;
    public static boolean emailCheck;
    public static FileAttrViewActivity activity;
    public static LinearLayout emailInfo;
    public static LinearLayout email;
    public static ImageView emailBtn;

    public static String otherUserId;
    public static String userId;
    public static String devUuid;
    public static String intentFileId;
    public static String etsionNm;
    public static String fileSize;
    public static String foldrWholePathNm;
    public static String cretDate;
    public static String amdDate;
    public static String fileShar;
    public static FileBasVO fileBasVO;

    List<Map<String,String>> fileAttrData = new ArrayList<>();
    List<Map<String,String>> fileTagData = new ArrayList<>();
    List<Map<String,String>> fileEmailData = new ArrayList<>();
    Map<String,String> fileData = new HashMap<>();

    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_attr_layout);
        context = FileAttrViewActivity.this;
        activity = FileAttrViewActivity.this;
        userId = SharedPreferenceUtil.getSharedPreference(context,context.getString(R.string.userId));
        devUuid = DeviceUtil.getDevicesUUID(context);
        viewEmailTitle = (TextView) findViewById(R.id.emailTitle);
        viewEmailDate = (TextView) findViewById(R.id.emailDate);
        viewEmailNm = (TextView) findViewById(R.id.emailNm);
        viewFileEtsionNm = (TextView) findViewById(R.id.fileEtsionNm);
        viewFileSize = (TextView) findViewById(R.id.fileSize);
        viewFoldrWholePathNm = (TextView) findViewById(R.id.foldrWholePathNm);
        viewCretDate = (TextView) findViewById(R.id.cretDate);
        viewAmdDate = (TextView) findViewById(R.id.amdDate);
        imgView = (ImageView)findViewById(R.id.fileThum);
        tagInfo = (TextView)findViewById(R.id.tagInfo);
        isChecked = (CheckBox) findViewById(R.id.checkBox);
        sharText = (TextView) findViewById(R.id.sharText);
        editBtn = (Button) findViewById(R.id.btn_edit);
        email = (LinearLayout) findViewById(R.id.email);
        emailInfo = (LinearLayout) findViewById(R.id.emailInfo);
        emailBtn = (ImageView)findViewById(R.id.emailBtn);
        emailCheck = false;

        findViewById(R.id.topBack).setOnClickListener(closeActivity);

        Intent intent = getIntent();

        intentFileId = intent.getExtras().getString("fileId");
        foldrWholePathNm = intent.getExtras().getString("foldrWholePathNm");

        fileBasVO = new FileBasVO();

        fileBasVO.setFileId(intentFileId);

        fileAttrList();

        new DownloadImagesTask().execute("http://222.106.202.145:8080/GIGA_Storage/imgFileThum.do?fileId="+intentFileId);

        editBtn.setOnClickListener(editPage);

        isChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (buttonView.getId() == R.id.checkBox) {
                    if (isChecked) {
                        fileBasVO.setFileShar("Y");
                    } else {
                        fileBasVO.setFileShar("M");
                    }
                    fileSharUpdate(fileBasVO);
                }
            }
        });

        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(emailCheck == false){
                    emailInfo.setVisibility(GONE);
                    Drawable drawable = getResources().getDrawable(R.drawable.ico_24dp_email_open);
                    emailBtn.setImageDrawable(drawable);
                }else{
                    emailInfo.setVisibility(View.VISIBLE);
                    Drawable drawable = getResources().getDrawable(R.drawable.ico_24dp_email_close);
                    emailBtn.setImageDrawable(drawable);
                }
                emailCheck = !emailCheck;
            }
        });

    }

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
                    int statusCode = gson.fromJson(response.body().get("statusCode"), Integer.class);
                    String message = new ResponseFailCode().responseFail(statusCode);
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    if(statusCode == 100) {
                        fileAttrData = gson.fromJson(response.body().get("listData1"), List.class);
                        fileEmailData = gson.fromJson(response.body().get("listData2"), List.class);
                        fileTagData = gson.fromJson(response.body().get("listData3"), List.class);
                        fileData = gson.fromJson(response.body().get("fileData"), Map.class);
                        if (fileData != null) {
                            Object fileSizeObj = (Object) fileData.get("fileSize");
                            otherUserId = fileData.get("userId");
                            fileShar = fileData.get("fileShar");
                            etsionNm = fileData.get("etsionNm");
                            fileSize = fileSizeObj.toString();
                            cretDate = fileData.get("cretDate");
                            amdDate = fileData.get("amdDate");

                            viewFileEtsionNm.setText(etsionNm);
                            long value = Long.parseLong(fileSize);
                            String size = KbConverter.convertBytesToSuitableUnit(value);
                            viewFileSize.setText(size);
                            viewFoldrWholePathNm.setText(foldrWholePathNm);
                            viewCretDate.setText(cretDate);
                            viewAmdDate.setText(amdDate);

                            if (fileShar.equals("Y")) {
                                isChecked.setChecked(true);
                            } else {
                                isChecked.setChecked(false);
                            }

                            if (!userId.equals(otherUserId)) {
                                sharText.setEnabled(false);
                                sharText.setTextColor(Color.LTGRAY);
                                isChecked.setEnabled(false);
                                editBtn.setVisibility(GONE);

                            }
                        }

                        if (fileEmailData != null && fileEmailData.size() != 0) {
                            for (int i = 0; i < fileEmailData.size(); i++) {
                                if(i == 0){
                                    viewEmailTitle.setText(fileEmailData.get(0).get("emailSbjt").toString());
                                    viewEmailDate.setText(fileEmailData.get(0).get("sendDate").toString());
                                    viewEmailNm.setText(fileEmailData.get(0).get("emailFrom").toString());
                                }else{
                                    TextView emailTitle = new TextView(FileAttrViewActivity.this);
                                    TextView emailDate = new TextView(FileAttrViewActivity.this);
                                    TextView emailNm = new TextView(FileAttrViewActivity.this);
                                    TextView emailBottom = new TextView(FileAttrViewActivity.this);

                                    int top = 10;
                                    int left = 70;
                                    int width = 250;

                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

                                    float dp = FileAttrViewActivity.this.getResources().getDisplayMetrics().density;

                                    int topDp = Math.round(top * dp);
                                    int leftDp = Math.round(left * dp);
                                    int widthDp = Math.round(width * dp);

                                    params.leftMargin = leftDp;
                                    emailTitle.setLayoutParams(params);
                                    emailTitle.setText(fileEmailData.get(i).get("emailSbjt").toString());
                                    emailTitle.setTextColor(getResources().getColor(R.color.darkGray));
                                    emailTitle.setWidth(widthDp);
                                    emailInfo.addView(emailTitle);

                                    leftDp = Math.round(left * dp);
                                    params.leftMargin = leftDp;
                                    emailDate.setLayoutParams(params);
                                    emailDate.setText(fileEmailData.get(i).get("sendDate").toString());
                                    emailDate.setTextColor(getResources().getColor(R.color.disabledGrayToNavi));
                                    emailDate.setTextSize(12);
                                    emailDate.setWidth(widthDp);
                                    emailInfo.addView(emailDate);

                                    topDp = Math.round(4 * dp);
                                    leftDp = Math.round(left * dp);
                                    params.topMargin = topDp;
                                    params.leftMargin = leftDp;

                                    emailNm.setLayoutParams(params);
                                    emailNm.setText(fileEmailData.get(i).get("emailFrom").toString());
                                    emailNm.setTextColor(getResources().getColor(R.color.disabledGrayToNavi));
                                    emailNm.setTextSize(12);
                                    emailNm.setWidth(widthDp);

                                    emailInfo.addView(emailNm);

                                    emailBottom.setText("\n");
                                    emailInfo.addView(emailBottom);

                                    emailInfo.setVisibility(GONE);

                                }
                            }
                        }else{
                            email.setVisibility(View.GONE);
                        }

                        String tagSum = "";
                        if (fileTagData != null) {
                            tag = new String[fileTagData.size()];
                            for (int i = 0; i < fileTagData.size(); i++) {
                                String fileTag = fileTagData.get(i).get("fileTag").toString();
                                if (!fileTag.equals("")) {
                                    tag[i] = fileTag;
                                    tagSum += "#" + fileTagData.get(i).get("fileTag") + "  ";
                                }
                                tagInfo.setText(tagSum);
                            }
                        }
                    }else if(statusCode == 400) {
                        alert.setMessage(message);
                        alert.show();
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();     //닫기
                                Intent intent = new Intent(context, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                activity.finish();
                            }
                        });
                    }else if(statusCode != 100 && statusCode != 400){
                        alert.setMessage(message);
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();     //닫기
                            }
                        });
                        alert.show();
                    }
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
                        DrawerLayoutViewActivity.activity.finish();
                        activity.finish();
                    }
                });
                alert.show();
            }
        });
    }
    public void fileSharUpdate(FileBasVO fileBasVO) {

        Call<JsonObject> fileSharUpdateCall = RestServiceImpl.getInstance(null).fileSharUpdate(fileBasVO);
        fileSharUpdateCall.enqueue( new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response) {
                if(response.isSuccess()) {

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
                        DrawerLayoutViewActivity.activity.finish();
                        activity.finish();
                    }
                });
                alert.show();
            }
        });
    }

    public class DownloadImagesTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            return download_Image(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if(result != null) {
                imgView.setImageBitmap(result);
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

    Button.OnClickListener editPage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(FileAttrViewActivity.this, TagEditActivity.class);
            intent.putExtra("fileId",intentFileId);
            intent.putExtra("tag",tag);
            startActivity(intent);
        }
    };

    public static void refresh(){
        Intent intent = new Intent(activity, FileAttrViewActivity.class);

        intent.putExtra("fileId", intentFileId);
        intent.putExtra("foldrWholePathNm",foldrWholePathNm);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish();
    }
}
