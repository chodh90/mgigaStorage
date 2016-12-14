package com.kt.gigastorage.mobile.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kt.gigastorage.mobile.service.AlertDialogService;
import com.kt.gigastorage.mobile.service.ResponseFailCode;
import com.kt.gigastorage.mobile.vo.FileTagVO;
import com.kt.gigastorage.mobile.webservice.impl.RestServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by araise on 2016-11-01.
 */

public class TagEditActivity extends Activity {

    private String intentFileId = "";
    private String[] intentTag;
    private static Context context;
    private ImageView imageView;
    private LinearLayout childLayout;
    private EditText tagEdit;
    private ArrayList<String> tagArray;
    private LinearLayout parentsLayout;
    private static AlertDialog.Builder alert;
    private static TagEditActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tag_edit_layout);

        context = TagEditActivity.this;
        activity = TagEditActivity.this;

        alert = AlertDialogService.alert(context);

        findViewById(R.id.tagAdd).setOnClickListener(tagAdd);
        findViewById(R.id.btn_confirm).setOnClickListener(editConfirm);

        tagEdit = (EditText)findViewById(R.id.input_tag);

        Intent intent = getIntent();

        intentFileId = intent.getExtras().getString("fileId");
        intentTag = intent.getExtras().getStringArray("tag");
        tagArray = new ArrayList<String>(Arrays.asList(intentTag));

        findViewById(R.id.topBack).setOnClickListener(closeActivity);

        editTag();

    }

    public  void editTag(){

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int displayWidth = size.x;

        parentsLayout = (LinearLayout)findViewById(R.id.parentsLayout);

        childLayout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int top = 10;
        int left = 25; // margin in dips
        int right = 0;
        int bottom = 10;
        float dp = this.getResources().getDisplayMetrics().density;
        int topDp = (int)(top * dp);
        int leftDp = (int)(left * dp); // margin in pixels
        int rightDp = (int)(right * dp); // margin in pixels
        int bottomDp = (int)(bottom * dp); // margin in pixels
        layoutParams.setMargins(leftDp, bottomDp, rightDp, topDp);
        childLayout.setLayoutParams(layoutParams);
        childLayout.setOrientation(LinearLayout.HORIZONTAL);

        int sumChildWidth = 0;

        for(int i=0; i<tagArray.size(); i++) {

            final LinearLayout addLayout = new LinearLayout(this);
            LinearLayout.LayoutParams addParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            addLayout.setLayoutParams(addParams);
            addLayout.setOrientation(LinearLayout.HORIZONTAL);

            final TextView textView = new TextView(this);
            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            textView.setTextColor(getResources().getColor(R.color.darkGray));
            textView.setTextSize(14);
            textView.setBackgroundColor(getResources().getColor(R.color.backGray));
            if(tagArray.get(i) != null){
                textView.setText("#"+tagArray.get(i).toString());

                int image = 30;
                int imageDp = (int)(image * dp);

                imageView = new ImageView(this);
                Drawable drawable = getResources().getDrawable(R.drawable.ico_18dp_tag_del);

                imageView.setId(i);
                imageView.setMinimumWidth(imageDp);
                imageView.setImageDrawable(drawable);

                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                //params1.setMargins(0,0,rightDp,0);
                params1.setMargins(0,0,leftDp,0);
                imageView.setLayoutParams(params1);


                addLayout.addView(textView);
                addLayout.addView(imageView);

                childLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                addLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

                int childWidth = childLayout.getMeasuredWidth();    //로우가 변하지 않으면 합산 로우가 변하면 0
                int viewWidth = addLayout.getMeasuredWidth();   //항상 일정한 크기
                int layoutWidth = childWidth + viewWidth;

                if (displayWidth < layoutWidth) {
                    parentsLayout.addView(childLayout);
                    childLayout = new LinearLayout(this);
                    addParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    addParams.setMargins(leftDp, 0, right, 20);
                    childLayout.setLayoutParams(addParams);
                    childLayout.setOrientation(LinearLayout.HORIZONTAL);
                    childLayout.addView(addLayout);
                }else{
                    childLayout.addView(addLayout);
                }
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Object position = view.getId();
                        int index = (int)position;
                        tagArray.remove(index);
                        parentsLayout.removeAllViews();
                        editTag();
                    }
                });
            }
        }
        parentsLayout.addView(childLayout);
    }

    Button.OnClickListener tagAdd = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String tagValue = tagEdit.getText().toString();
            String check = spaceCheck(tagValue);
            if(tagValue == null || tagValue.equals("")){
                alert.setMessage("태그 정보를 입력해 주세요.");
                alert.show();
            }else if(check.equals("N")){
                alert.setMessage("태그 정보에 공백을 제거해주세요.");
                alert.show();
            }else{
                tagArray.add(tagValue);
            }
            parentsLayout.removeAllViews();
            tagEdit.setText("");
            editTag();
        }
    };

    Button.OnClickListener editConfirm = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            AlertDialog.Builder confirm = new AlertDialog.Builder(context);
            confirm.setTitle("태그를 저장 하시겠습니까?");
            confirm.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    FileTagVO fileTagVO = new FileTagVO();
                    fileTagVO.setFileId(intentFileId);
                    String fileTag = "";
                    for(int i=0; i<tagArray.size(); i++){
                        String tagName = tagArray.get(i);
                        if(tagName != null){
                            if(tagArray.get(i).equals(tagArray.get(tagArray.size()-1))){
                                fileTag += tagName;
                            }else{
                                fileTag += tagName + ";";
                            }
                        }
                    }
                    fileTagVO.setFileTag(fileTag);
                    mobileFileTagUpdate(fileTagVO);
                }
            });
            // Cancel 버튼 이벤트
            confirm.setNegativeButton("취소",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            confirm.show();
        }
    };

    public static void mobileFileTagUpdate(FileTagVO fileTagVO) {

        Call<JsonObject> mobileFileTagUpdateCall = RestServiceImpl.getInstance(null).mobileFileTagUpdate(fileTagVO);
        mobileFileTagUpdateCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response) {
                if (response.isSuccess()) { // Code 200
                    Gson gson = new Gson();
                    int statusCode = gson.fromJson(response.body().get("statusCode"), Integer.class);
                    String message = new ResponseFailCode().responseFail(statusCode);
                    if(statusCode == 100){
                        AlertDialog.Builder successAlert = new AlertDialog.Builder(context);
                        successAlert.setMessage(message);
                        successAlert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();     //닫기
                                activity.finish();
                                FileAttrViewActivity.refresh();
                            }
                        });
                        successAlert.show();
                    }else if(statusCode != 100 && statusCode != 400){
                        alert.setMessage(message);
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
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(intent);
                        FileAttrViewActivity.activity.finish();
                        DrawerLayoutViewActivity.activity.finish();
                        activity.finish();

                    }
                });
                alert.show();
            }
        });
    }

    Button.OnClickListener closeActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    public String spaceCheck(String tagText) {
        boolean tagCheck = false;

        String check = "N";

        for(int i = 0 ; i < tagText.length() ; i++) {
            if(tagText.charAt(i) == ' '){
                tagCheck = true;
            }

        }

        if(tagCheck == false){
            check = "Y";
        }else{
            return check;
        }

        return check;
    }

}