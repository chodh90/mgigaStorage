package com.kt.gigastorage.mobile.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kt.gigastorage.mobile.activity.DrawerLayoutViewActivity;
import com.kt.gigastorage.mobile.activity.MainActivity;
import com.kt.gigastorage.mobile.activity.R;
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
 * Created by zeroeun on 2016-10-14.
 */

public class DevListFragment extends Fragment {

    private List<Map<String, String>> mListData = new ArrayList<>();
    private ListViewAdapter mAdapter = null;
    private ListView mListView = null;

    private Context mContext;

    private Activity activity;

    private DevBasVO devBasVO = new DevBasVO();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = DrawerLayoutViewActivity.context;
        activity = DrawerLayoutViewActivity.activity;

        View view = inflater.inflate(R.layout.content_listview, container, false);

        mListView = (ListView) view.findViewById(R.id.mList);

        mAdapter = new ListViewAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mItemClickListener);

        devBasVO.setUserId(SharedPreferenceUtil.getSharedPreference(mContext,mContext.getString(R.string.userId)));

        getDevListWebservice();

        return view;
    }

    private void getDevListWebservice() {

        Call<JsonObject> listDevBasCall = RestServiceImpl.getInstance(null).listOneview(devBasVO);
        listDevBasCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response) {
                if (response.isSuccess()) { // Code 200
                    Gson gson = new Gson();
                    int statusCode = gson.fromJson(response.body().get("statusCode"), Integer.class);
                    String message = new ResponseFailCode().responseFail(statusCode);
                    AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                    if(statusCode == 100){
                        mListData = gson.fromJson(response.body().get("listData"), List.class);
                        /* bizNot 하드코딩 */
                        Map<String, String> bizNote = new ArrayMap<String, String>();
                        bizNote.put("userId", mListData.get(0).get("userId"));
                        bizNote.put("userName", mListData.get(0).get("userName"));
                        bizNote.put("devUuid", "");
                        bizNote.put("devNm", "BizNote");
                        bizNote.put("osCd", "B");
                        mListData.add(bizNote);

                        ((TextView)getActivity().findViewById(R.id.toobar_title)).setText(mListData.get(0).get("userName") + "님의 스토리지");
                        ((TextView)((NavigationView)getActivity().findViewById(R.id.naviView)).getHeaderView(0).findViewById(R.id.nav_header_title)).setText(mListData.get(0).get("userName") + "님의 스토리지");
                        mAdapter.notifyDataSetChanged();
                    }else if(statusCode == 400) {
                        alert.setMessage(message);
                        alert.show();
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();     //닫기
                                Intent intent = new Intent(mContext, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
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
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                alert.setMessage(mContext.getString(R.string.serverOut));
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                        Intent intent = new Intent(mContext, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Map<String, String> data = (Map<String,String>) mAdapter.getItem(position);

            ((DrawerLayoutViewActivity)getActivity()).menuSelect(position);

            ImageView devIcon = (ImageView) view.findViewById(R.id.dev_icon);
            TextView devNm = (TextView) view.findViewById(R.id.dev_nm);
            LinearLayout devArea = (LinearLayout) view.findViewById(R.id.dev_item_area);

            if(data.get("osCd").equals("W")) {
                if(data.get("onoff").equals("Y")){
                    devIcon.setImageResource(R.drawable.ico_35dp_device_pc_on);
                }else{
                    devIcon.setImageResource(R.drawable.ico_35dp_device_pc_off);
                }
            } else if(data.get("osCd").equals("A")) {
                devIcon.setImageResource(R.drawable.ico_35dp_device_mobile_on);
            } else if(data.get("osCd").equals("G")) {
                devIcon.setImageResource(R.drawable.ico_35dp_device_giganas_select);
            } else {
                devIcon.setImageResource(R.drawable.ico_35dp_device_giganas_on);
            }

            devArea.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.pink));
            devNm.setTextColor(ContextCompat.getColor(getActivity(), R.color.baseColor));

            ((DrawerLayoutViewActivity)getActivity()).setToolbarTitle(data.get("devNm"));
            if(!data.get("osCd").equals("B")) { // bizNote가 아니면

                Bundle args = new Bundle();
                args.putString("devNm",data.get("devNm"));
                args.putString("devUuid",data.get("devUuid"));
                args.putString("osCd", data.get("osCd"));
                ((DrawerLayoutViewActivity)getActivity()).changeFragment(args);
            } else {
                ((DrawerLayoutViewActivity)getActivity()).changeBizFragment();
            }

        }
    };

    private class ViewHolder {
        public ImageView mIcon;
        public TextView mText;
        /*public ImageView mStatusIcon;*/
        public TextView mDevUuid;
    }

    private class ListViewAdapter extends BaseAdapter {

        public ListViewAdapter(Context context) {
            mContext = context;
        }

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

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_list_dev,null);

                holder.mIcon = (ImageView) convertView.findViewById(R.id.dev_icon);
                holder.mText = (TextView) convertView.findViewById(R.id.dev_nm);
                /*holder.mStatusIcon = (ImageView) convertView.findViewById(R.id.onOffIcon);*/
                holder.mDevUuid = (TextView) convertView.findViewById(R.id.devUuid);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Map<String, String> mData = mListData.get(position);

            holder.mIcon.setVisibility(View.VISIBLE);

            if(mData.get("osCd").equals("W")) { // PC
                if(mData.get("onoff").equals("Y")){
                    holder.mIcon.setImageResource(R.drawable.ico_35dp_device_pc_on);
                    holder.mText.setTextColor(getResources().getColor(R.color.darkGray));
                }else{
                    holder.mIcon.setImageResource(R.drawable.ico_35dp_device_pc_off);
                    holder.mText.setTextColor(getResources().getColor(R.color.disabledGray));
                }
            } else if(mData.get("osCd").equals("A")) { // Android

                //TODO - 안드로이드폰은 우선 다 ON으로 되어있는 것으로
                holder.mIcon.setImageResource(R.drawable.ico_35dp_device_mobile_on);
                holder.mText.setTextColor(getResources().getColor(R.color.darkGray));

                /*if(mData.get("devUuid").equals(SharedPreferenceUtil.getSharedPreference(mContext, "devUuid"))) {
                    holder.mIcon.setImageResource(R.drawable.ico_35dp_device_mobile_on);
                    holder.mText.setTextColor(getResources().getColor(R.color.darkGray));
                } else {
                    holder.mIcon.setImageResource(R.drawable.ico_35dp_device_mobile_off);
                    holder.mText.setTextColor(getResources().getColor(R.color.disabledGray));
                }*/
            } else if(mData.get("osCd").equals("G")) { // GiGA NAS
                holder.mIcon.setImageResource(R.drawable.ico_35dp_device_giganas_on);
                holder.mText.setTextColor(getResources().getColor(R.color.darkGray));
            } else { // bizNote
                holder.mIcon.setImageResource(R.drawable.ico_35dp_device_giganas_on);
                holder.mText.setTextColor(getResources().getColor(R.color.darkGray));
            }

            holder.mText.setTypeface(Typeface.DEFAULT_BOLD);
            holder.mText.setText(mData.get("devNm"));
            holder.mDevUuid.setText(mData.get("devUuid"));

            return convertView;
        }
    }

}
