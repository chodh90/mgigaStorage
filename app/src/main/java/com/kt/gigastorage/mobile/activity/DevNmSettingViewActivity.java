package com.kt.gigastorage.mobile.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kt.gigastorage.mobile.service.ResponseFailCode;
import com.kt.gigastorage.mobile.utils.SharedPreferenceUtil;
import com.kt.gigastorage.mobile.vo.DevBasVO;
import com.kt.gigastorage.mobile.webservice.impl.RestServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by araise on 2016-10-19.
 */

public class DevNmSettingViewActivity extends Activity {

    private List<Map<String, String>> mListData = new ArrayList<>();

    private ListViewAdapter mAdapter;
    private ListView mListView;

    private DevBasVO devBasVO = new DevBasVO();
    public static Context sContext;
    public static DevNmSettingViewActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_name_setting_layout);

        sContext = DevNmSettingViewActivity.this;
        activity = DevNmSettingViewActivity.this;

        devBasVO.setUserId(SharedPreferenceUtil.getSharedPreference(sContext, sContext.getString(R.string.userId)));

        mAdapter = new ListViewAdapter();
        mListView = (ListView)findViewById(R.id.devNmSetting);
        mListView.setAdapter(mAdapter);

        findViewById(R.id.topBack).setOnClickListener(closeActivity);

        getDevListWebservice();
    }

    Button.OnClickListener closeActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
            DrawerLayoutViewActivity.refresh();
        }
    };

    public void getDevListWebservice() {

        Call<JsonObject> listDevBasCall = RestServiceImpl.getInstance(null).listOneview(devBasVO);
        listDevBasCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response) {
                if (response.isSuccess()) { // Code 200
                    Gson gson = new Gson();
                    int statusCode = gson.fromJson(response.body().get("statusCode"), Integer.class);
                    String message = new ResponseFailCode().responseFail(statusCode);
                    AlertDialog.Builder alert = new AlertDialog.Builder(sContext);
                    if(statusCode == 100){
                        mListData = gson.fromJson(response.body().get("listData"), List.class);
                        mAdapter.notifyDataSetChanged();
                    }else if(statusCode == 400){
                        alert.setMessage(message);
                        alert.show();
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();     //닫기
                                Intent intent = new Intent(sContext, MainActivity.class);
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
                AlertDialog.Builder alert = new AlertDialog.Builder(sContext);
                alert.setMessage(sContext.getString(R.string.serverOut));
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        activity.finish();
                    }
                });
            }
        });
    }

    private class ViewHolder {
        public TextView devNmTxt;
        public EditText devNmEdt;
        public ImageView devImg;
    }

    private class ListViewAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final int pos = position;
            ViewHolder holder;

            if(convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_list_dev_setting,null);

                holder.devNmTxt = (TextView) convertView.findViewById(R.id.devNmTxt);
                holder.devNmEdt = (EditText) convertView.findViewById(R.id.devNmEdt);
                holder.devImg = (ImageView) convertView.findViewById(R.id.devImg);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Map<String, String> mData = mListData.get(position);

            holder.devNmTxt.setText(mData.get("devNm"));
            holder.devNmEdt.setHint(mData.get("devNm"));
            if(mData.get("osCd").equals("W")) {
                holder.devImg.setImageResource(R.drawable.ico_35dp_device_pc_on);
            } else if(mData.get("osCd").equals("A")) {
                holder.devImg.setImageResource(R.drawable.ico_35dp_device_mobile_on);
            } else {
                holder.devImg.setImageResource(R.drawable.ico_35dp_device_giganas_on);
            }

            convertView.findViewById(R.id.devNmEdt).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(DevNmSettingViewActivity.this, PopupDevNmActivity.class);
                    intent.putExtra("devNm", mListData.get(pos).get("devNm"));
                    intent.putExtra("devUuid", mListData.get(pos).get("devUuid"));
                    startActivity(intent);

                }
            });

            return convertView;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        DrawerLayoutViewActivity.refresh();
        super.onBackPressed();
    }

}
