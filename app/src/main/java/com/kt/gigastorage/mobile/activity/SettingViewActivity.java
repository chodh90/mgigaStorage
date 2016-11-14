package com.kt.gigastorage.mobile.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kt.gigastorage.mobile.service.ProgressService;
import com.kt.gigastorage.mobile.service.ResponseFailCode;
import com.kt.gigastorage.mobile.utils.SharedPreferenceUtil;
import com.kt.gigastorage.mobile.webservice.impl.RestServiceImpl;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by araise on 2016-10-19.
 */

public class SettingViewActivity extends Activity {

    private ToggleButton toggleButton;

    private LinearLayout nameSetting;
    private TextView loginId;
    private static Context context;
    private static SettingViewActivity activity;
    private ProgressDialog mProgDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_layout);

        context = SettingViewActivity.this;
        activity = SettingViewActivity.this;
        mProgDlg = ProgressService.progress(context);

        findViewById(R.id.logout).setOnClickListener(logout);
        findViewById(R.id.topBack).setOnClickListener(closeActivity);
        toggleButton = (ToggleButton)findViewById(R.id.toggleButton1);

        Object obj = SharedPreferenceUtil.getCheckedSharedPreference(getApplicationContext(),getString(R.string.isChecked));
        boolean objChecked = false;
        if(obj != null){
            objChecked = (boolean)obj;
        }
        if(objChecked == true){
            toggleButton.setChecked(objChecked);
        }

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferenceUtil.putSharedPreference(getApplicationContext(),getString(R.string.isChecked),isChecked);
            }
        });

        String userId = SharedPreferenceUtil.getSharedPreference(context, getString(R.string.userId));

        loginId = (TextView) findViewById(R.id.loginId);
        loginId.setText(userId);

        nameSetting = (LinearLayout) findViewById(R.id.nameSetting);
        nameSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentNameSetting();
            }
        });
    }

    ImageView.OnClickListener logout = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(SettingViewActivity.this);
            dialog.setTitle("로그아웃 하시겠습니까?");
            dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    logout();
                }
            });
            // Cancel 버튼 이벤트
            dialog.setNegativeButton("취소",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            dialog.show();
        }
    };

    public void logout() {

        mProgDlg.setMessage("로그아웃 중입니다.");
        mProgDlg.onStart();

        Call<JsonObject> logoutCall = RestServiceImpl.getInstance(null).logout();
        logoutCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response) {
                Gson gson = new Gson();
                int statusCode = gson.fromJson(response.body().get("statusCode"), Integer.class);
                String message = new ResponseFailCode().responseFail(statusCode);
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                mProgDlg.dismiss();
                if(statusCode == 100){
                    SharedPreferenceUtil.putSharedPreference(getApplicationContext(), getString(R.string.xAuthToken), "");
                    SharedPreferenceUtil.putSharedPreference(getApplicationContext(), getString(R.string.cookie), "");
                    SharedPreferenceUtil.putSharedPreference(getApplicationContext(), getString(R.string.userId), "");
                    SharedPreferenceUtil.putSharedPreference(getApplicationContext(), getString(R.string.password), "");
                    SharedPreferenceUtil.putSharedPreference(getApplicationContext(), getString(R.string.isChecked), false);
                    DrawerLayoutViewActivity.activity.finish();
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                    activity.finish();


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

            @Override
            public void onFailure(Throwable t) {
                mProgDlg.dismiss();
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
            }
        });
    }

    Button.OnClickListener closeActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    public void intentNameSetting(){
        Intent intent = new Intent(this, DevNmSettingViewActivity.class);
        startActivity(intent);
    }
}
