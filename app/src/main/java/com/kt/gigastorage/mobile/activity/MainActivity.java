package com.kt.gigastorage.mobile.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kt.gigastorage.mobile.gcm.GigaRegistrationIntentService;
import com.kt.gigastorage.mobile.service.AlertDialogService;
import com.kt.gigastorage.mobile.service.FileService;
import com.kt.gigastorage.mobile.service.ProgressService;
import com.kt.gigastorage.mobile.service.ResponseFailCode;
import com.kt.gigastorage.mobile.utils.DeviceUtil;
import com.kt.gigastorage.mobile.utils.SharedPreferenceUtil;
import com.kt.gigastorage.mobile.vo.DevBasVO;
import com.kt.gigastorage.mobile.webservice.impl.RestServiceImpl;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends Activity {

    public static Context context;
    public static DrawerLayoutViewActivity activity;
    public static Log log;

    private EditText userId;
    private EditText password;
    private Button loginBtn;
    private ToggleButton toggleButton;
    private LinearLayout background;
    private TextView errorMsg;

    public static ProgressDialog mProgDlg;
    public static AlertDialog.Builder alert;
    private Message loginMsg = new Message();

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;
        alert = AlertDialogService.alert(context);
        userId = (EditText) findViewById(R.id.input_id);
        password = (EditText) findViewById(R.id.input_pwd);
        loginBtn = (Button) findViewById(R.id.btn_login);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton1);
        background = (LinearLayout) findViewById(R.id.background);
        errorMsg = (TextView) findViewById(R.id.errorMsg);

        String id = SharedPreferenceUtil.getSharedPreference(context, getString(R.string.userId));
        String pwd = SharedPreferenceUtil.getSharedPreference(context, getString(R.string.password));
        Object obj = SharedPreferenceUtil.getCheckedSharedPreference(context, getString(R.string.isChecked));

        boolean objChecked = false;

        if (obj != null) {
            objChecked = (boolean) obj;
        }

        if (objChecked == true && (!userId.equals("") && !pwd.equals(""))) {
            toggleButton.setChecked(objChecked);
            if (id != null && !id.equals("")) {
                userId.setText(id);
            }
            if (pwd != null && !pwd.equals("")) {
                password.setText(pwd);
            }
            loginRetrofit();
        } else {
            userId.setText("");
            password.setText("");
        }

        // keypad 없애기
        background.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(userId.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(password.getWindowToken(), 0);

                return false;
            }
        });

        //자동로그인 체크 이벤트
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferenceUtil.putSharedPreference(context, getString(R.string.isChecked), isChecked);

            }
        });

        //아이디 포커스 이벤트
        userId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundResource(R.drawable.focus_border_style);
                } else {
                    v.setBackgroundResource(R.drawable.lost_focus_border_style);
                }
            }
        });

        //패스워드 포커스 이벤트
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundResource(R.drawable.focus_border_style);
                } else {
                    v.setBackgroundResource(R.drawable.lost_focus_border_style);
                }
            }
        });

        //패스워드 키보드 완료클릭 이벤트
        password.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String check = spaceCheck(userId.getText().toString(),password.getText().toString());
                if(userId.getText().toString().equals("") || password.getText().toString().equals("")){
                    errorMsg.setText("로그인 정보를 확인해 주세요.");
                }else{
                    if(check.equals("Y")){
                        loginRetrofit();
                    }else{
                        errorMsg.setText("로그인 정보를 확인해 주세요.");
                    }
                }
                return false;
            }
        });

        //로그인 클릭 이벤트
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String check = spaceCheck(userId.getText().toString(),password.getText().toString());
                if(userId.getText().toString().equals("") || password.getText().toString().equals("")){
                    errorMsg.setText("로그인 정보를 확인해 주세요.");
                }else{
                    if(check.equals("Y")){
                        loginRetrofit();
                    }else{
                        errorMsg.setText("로그인 정보를 확인해 주세요.");
                    }
                }
            }
        });

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, GigaRegistrationIntentService.class);
            startService(intent);
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS) {
            if(apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 9000).show();
            } else {
                finish();
            }

            return false;
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    // deviceAuth
    public void deviceAuth() {
        BluetoothAdapter DeviceNm = BluetoothAdapter.getDefaultAdapter();
        DevBasVO devBasVO = new DevBasVO();
        devBasVO.setDevUuid(DeviceUtil.getDevicesUUID(context));
        SharedPreferenceUtil.putSharedPreference(context, "devUuid", DeviceUtil.getDevicesUUID(context));
        devBasVO.setToken(SharedPreferenceUtil.getSharedPreference(context, getString(R.string.gcmToken)));
        devBasVO.setUserId(userId.getText().toString());
        devBasVO.setMkngVndrNm(Build.BRAND);
        devBasVO.setLastUpdtId(userId.getText().toString());
        if(DeviceNm.getName() == null) {
            devBasVO.setDevNm(Build.MODEL);
        } else {
            devBasVO.setDevNm(DeviceNm.getName());
        }
        devBasVO.setOsCd(getString(R.string.androidCd));
        devBasVO.setOsDesc(getString(R.string.android));

        Call<JsonObject> devBasVOCall = RestServiceImpl.getInstance(null).devBasAuth(devBasVO);
        devBasVOCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response) {
                Gson gson = new Gson();
                int statusCode = gson.fromJson(response.body().get("statusCode"), Integer.class);
                String message = new ResponseFailCode().responseFail(statusCode);
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                if(statusCode == 100){
                    FileService.syncFoldrInfo();
                    mProgDlg.dismiss();
                    Intent intent = new Intent(MainActivity.this, DrawerLayoutViewActivity.class);
                    startActivity(intent);
                    finish();
                }else if(statusCode != 100 && statusCode != 400){
                    errorMsg.setText(message);
                }else {
                    errorMsg.setText("로그인 정보가 올바르지 않습니다.");
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
            }
        });
    }

    public void loginRetrofit() {

        mProgDlg = ProgressService.progress(MainActivity.this);
        mProgDlg.setMessage("로그인 중입니다...");
        mProgDlg.show();

        SharedPreferenceUtil.putSharedPreference(context, getString(R.string.xAuthToken), "");
        SharedPreferenceUtil.putSharedPreference(context, getString(R.string.cookie), "");

        Call<JsonObject> LoginCall = RestServiceImpl.getInstance(null).login(userId.getText().toString(), password.getText().toString());
        LoginCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response) {

                int responseCode = response.code();
                mProgDlg.dismiss();

                if(responseCode == 200) {
                    Gson gson = new Gson();
                    int statusCode = gson.fromJson(response.body().get("statusCode"), Integer.class);
                    String message = new ResponseFailCode().responseFail(statusCode);
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    if(statusCode == 100) {
                        SharedPreferenceUtil.putSharedPreference(context, getString(R.string.xAuthToken), response.raw().header(getString(R.string.xAuthToken)));
                        SharedPreferenceUtil.putSharedPreference(context, getString(R.string.userId), userId.getText().toString());
                        SharedPreferenceUtil.putSharedPreference(context, getString(R.string.password), password.getText().toString());
                        SharedPreferenceUtil.putSharedPreference(context, getString(R.string.cookie), response.raw().header(getString(R.string.cookie)));
                        errorMsg.setText("");
                        deviceAuth();
                    }
                } else if(responseCode == 410) {
                    errorMsg.setText("아이디와 비밀번호를 확인해 주세요");
                }
            }

            @Override
            public void onFailure(Throwable t) { //
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
                    }
                });
            }
        });
    }
    public String spaceCheck(String spaceId ,String spacePwd) {
        boolean idCheck = false;
        boolean pwdCheck = false;
        String check = "N";

        for(int i = 0 ; i < spaceId.length() ; i++) {
            if(spaceId.charAt(i) == ' '){
                idCheck = true;
            }

        }

        for(int j = 0; j < spacePwd.length() ; j++){
            if(spacePwd.charAt(j) == ' '){
                pwdCheck = true;
            }
        }
        if(idCheck == false && pwdCheck == false){
            check = "Y";
        }else{
            return check;
        }

        return check;
    }

};
