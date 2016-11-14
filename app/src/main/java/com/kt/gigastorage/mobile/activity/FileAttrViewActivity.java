package com.kt.gigastorage.mobile.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kt.gigastorage.mobile.service.AlertDialogService;
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
    public static FileAttrViewActivity activity;

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
                            viewFileSize.setText(fileSize);
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
                                editBtn.setVisibility(View.GONE);

                            }
                        }

                        if (fileEmailData != null) {
                            for (int i = 0; i < fileEmailData.size(); i++) {
                                viewEmailTitle.setText(fileEmailData.get(i).get("emailSbjt").toString());
                                viewEmailDate.setText(fileEmailData.get(i).get("sendDate").toString());
                                viewEmailNm.setText(fileEmailData.get(i).get("emailFrom").toString());
                            }
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
