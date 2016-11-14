package com.kt.gigastorage.mobile.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kt.gigastorage.mobile.service.ResponseFailCode;
import com.kt.gigastorage.mobile.vo.FileEmailVO;
import com.kt.gigastorage.mobile.webservice.impl.RestServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PopupCcActivity extends Activity {

    private FileEmailVO fileEmailVO = new FileEmailVO();
    private Context context;
    private Activity activity;
    private String emailId;
    private ListView listView;
    private ArrayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        context = PopupCcActivity.this;
        activity = PopupCcActivity.this;

        emailId = getIntent().getExtras().getString("emailId");
        fileEmailVO.setEmailId(emailId);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup_cc);

        // 팝업창이 뜨면 뒷배경 블러처리
        /*WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.7f;
        getWindow().setAttributes(layoutParams);*/

        mAdapter = new ArrayAdapter(this, R.layout.item_list_cc);

        listView = (ListView)findViewById(R.id.list_view);
        findViewById(R.id.btn_close).setOnClickListener(close);
        listView.setAdapter(mAdapter);
        getRefrEmailWebservice();
    }

    Button.OnClickListener close = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            activity.finish();
        }
    };

    private void getRefrEmailWebservice() {

        Call<JsonObject> listDevBasCall = RestServiceImpl.getInstance(null).listRefrEmail(fileEmailVO);
        listDevBasCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response) {

                Gson gson = new Gson();
                int responseCode = response.code();

                if(responseCode == 200) {
                    int statusCode = gson.fromJson(response.body().get("statusCode"), Integer.class);
                    String message = new ResponseFailCode().responseFail(statusCode);
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    if (statusCode == 100) {
                        List<Map<String,String>> data = new ArrayList<>();
                        data = gson.fromJson(response.body().get("listData"), List.class);
                        for(int i=0; i<data.size(); i++) {
                            mAdapter.add(data.get(i).get("emailTo").toString());
                        }
                        mAdapter.notifyDataSetChanged();
                    } else if (statusCode == 400) {
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
                    } else if (statusCode != 100 && statusCode != 400) {
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
                    }
                });
            }
        });
    }
};
