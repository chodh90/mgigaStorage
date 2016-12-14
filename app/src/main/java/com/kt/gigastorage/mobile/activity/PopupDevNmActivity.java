package com.kt.gigastorage.mobile.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kt.gigastorage.mobile.service.ResponseFailCode;
import com.kt.gigastorage.mobile.utils.SharedPreferenceUtil;
import com.kt.gigastorage.mobile.vo.DevBasVO;
import com.kt.gigastorage.mobile.webservice.impl.RestServiceImpl;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PopupDevNmActivity extends Activity {

    private EditText mEditText;
    private DevBasVO devBasVO = new DevBasVO();
    private Context context;
    private Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = PopupDevNmActivity.this;
        activity = PopupDevNmActivity.this;

        String devNm = getIntent().getExtras().get("devNm").toString();
        String devUuid = getIntent().getExtras().get(context.getString(R.string.devUuid)).toString();

        devBasVO.setUserId(SharedPreferenceUtil.getSharedPreference(context, context.getString(R.string.userId)));
        devBasVO.setDevUuid(devUuid);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 팝업창이 뜨면 뒷배경 블러처리
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.7f;
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.activity_popup_dev_name);

        mEditText = (EditText)findViewById(R.id.devNm);
        mEditText.setText(devNm);

        setContent();
    }

    private void setContent() {
        findViewById(R.id.cancel).setOnClickListener(cancel);
        findViewById(R.id.ok).setOnClickListener(ok);
    }

    Button.OnClickListener cancel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PopupDevNmActivity.this.finish();
        }
    };

    Button.OnClickListener ok = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(mEditText.getText() != null) {
                String devNm = mEditText.getText().toString();
                mEditText.setText(devNm.replaceAll("\n", ""));
            }

            if(mEditText.getText() == null || mEditText.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), "변경할 디바이스 이름을 입력하세요.", Toast.LENGTH_SHORT).show();
            } else {
                devBasVO.setDevNm(mEditText.getText().toString());
                updDevNmWebservice();
            }
        }
    };

    private void updDevNmWebservice() {

        Call<JsonObject> listDevBasCall = RestServiceImpl.getInstance(null).updDevNm(devBasVO);
        listDevBasCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response) {
                Gson gson = new Gson();
                int statusCode = gson.fromJson(response.body().get("statusCode"), Integer.class);
                String message = new ResponseFailCode().responseFail(statusCode);
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                if(statusCode == 100){
                    PopupDevNmActivity.this.finish();
                    ((DevNmSettingViewActivity) DevNmSettingViewActivity.sContext).getDevListWebservice();
                    ((DrawerLayoutViewActivity) DrawerLayoutViewActivity.context).getDevListWebservice();
                }else if(statusCode == 400){
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
};
