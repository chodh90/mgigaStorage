package com.kt.gigastorage.mobile.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kt.gigastorage.mobile.activity.BizNoteEmailRefFileViewActivity;
import com.kt.gigastorage.mobile.activity.DrawerLayoutViewActivity;
import com.kt.gigastorage.mobile.activity.FileSearchViewActivity;
import com.kt.gigastorage.mobile.activity.MainActivity;
import com.kt.gigastorage.mobile.activity.R;
import com.kt.gigastorage.mobile.activity.SendNasViewActivity;
import com.kt.gigastorage.mobile.service.AlertDialogService;
import com.kt.gigastorage.mobile.service.FileService;
import com.kt.gigastorage.mobile.service.FileUploadThread;
import com.kt.gigastorage.mobile.service.ResponseFailCode;
import com.kt.gigastorage.mobile.utils.DeviceUtil;
import com.kt.gigastorage.mobile.utils.SharedPreferenceUtil;
import com.kt.gigastorage.mobile.vo.ComndQueueVO;
import com.kt.gigastorage.mobile.vo.FileBasVO;
import com.kt.gigastorage.mobile.vo.FoldrBasVO;
import com.kt.gigastorage.mobile.webservice.impl.RestServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by zeroeun on 2014-07-02.
 */

public class FoldrFragment extends Fragment {

    private List<Map<String, String>> mListData = new ArrayList<>();
    private ListViewAdapter mAdapter = null;
    private ListView mListView = null;
    public static ProgressDialog dialog;
    private FoldrBasVO foldrBasVO = new FoldrBasVO();
    public static Context flagContext;
    private boolean[] isCheckedArray = new boolean[0];

    private ArrayList<String> rootFolders = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.content_listview, container, false);
        mListView = (ListView) view.findViewById(R.id.mList);
        mAdapter = new ListViewAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mItemClickListener);

        foldrBasVO.setUserId(SharedPreferenceUtil.getSharedPreference(getActivity(), "userId"));
        foldrBasVO.setDevUuid(foldrBasVO.getUserId() + "-gs");

        getFoldrList();

        return view;
    }

    public void confirmSelected(String osCd,String devUuid,String foldrWholePathNm, String fileNm, String fileId, String command) {
        AlertDialog.Builder alert = new AlertDialog.Builder(SendNasViewActivity.context);
        alert.setMessage("폴더를 선택하세요.");
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
            }
        });
        String myDevUuid = DeviceUtil.getDevicesUUID(flagContext);
        String userId = SharedPreferenceUtil.getSharedPreference(flagContext,flagContext.getString(R.string.userId));

        Map<String, String> temp = new ArrayMap<>();
        String foldrId = "";
        if(command.equals("search")){
            flagContext = FileSearchViewActivity.mContext;
        }else if(command.equals("bizNote")){
            flagContext = BizNoteEmailRefFileViewActivity.mContext;
        }else{
            flagContext = DrawerLayoutViewActivity.context;
        }

        if(isCheckedArray.length > 0) {
            for(int i=0; i<isCheckedArray.length; i++) {
                if(isCheckedArray[i] == true) {
                    temp = (Map<String, String>)mAdapter.getItem(i);

                    Object obj = temp.get("foldrId");
                    foldrId = obj.toString();
                }
            }
            if(!foldrId.equals("")) {
                if(osCd.equals("A") && devUuid.equals(myDevUuid)){
                    new FileUploadThread(flagContext).execute(temp.get("foldrWholePathNm"),fileNm,fileId,foldrWholePathNm,foldrId);
                }

                if(osCd.equals("W")){
                    ComndQueueVO comndQueueVO = new ComndQueueVO();
                    comndQueueVO.setComnd("RWLG");
                    comndQueueVO.setFromOsCd("W");
                    comndQueueVO.setFromUserId(userId);
                    comndQueueVO.setFromFoldr(foldrWholePathNm);
                    comndQueueVO.setFromFileNm(fileNm);
                    comndQueueVO.setFromFileId(fileId);
                    comndQueueVO.setFromDevUuid(devUuid);
                    comndQueueVO.setToDevUuid(userId + "-gs");
                    comndQueueVO.setComndOsCd("A");
                    comndQueueVO.setComndDevUuid(myDevUuid);
                    comndQueueVO.setToOsCd("G");
                    comndQueueVO.setToFoldr(temp.get("foldrWholePathNm"));
                    comndQueueVO.setToFileNm(fileNm);
                    comndQueueVO.setUserId(userId);

                    FileService.fileDownloadWebservice(comndQueueVO,flagContext,"N");
                }
                if(osCd.equals("A") && !devUuid.equals(myDevUuid)){

                    ComndQueueVO comndQueueVO = new ComndQueueVO();
                    comndQueueVO.setComnd("RALG");
                    comndQueueVO.setFromOsCd("A");
                    comndQueueVO.setFromUserId(userId);
                    comndQueueVO.setFromFoldr(foldrWholePathNm);
                    comndQueueVO.setFromFileNm(fileNm);
                    comndQueueVO.setFromFileId(fileId);
                    comndQueueVO.setFromDevUuid(devUuid);
                    comndQueueVO.setToDevUuid(myDevUuid);
                    comndQueueVO.setComndOsCd("A");
                    comndQueueVO.setComndDevUuid(myDevUuid);
                    comndQueueVO.setToOsCd("G");
                    comndQueueVO.setToFoldr(temp.get("foldrWholePathNm"));
                    comndQueueVO.setToFileNm(fileNm);
                    comndQueueVO.setUserId(userId);

                    FileService.fileDownloadWebservice(comndQueueVO,flagContext,"N");

                }if(osCd.equals("G")){
                    FileBasVO fileBasVO = new FileBasVO();
                    fileBasVO.setUserId(userId);
                    fileBasVO.setFileId(fileId);
                    fileBasVO.setFoldrId(foldrId);
                    fileBasVO.setOldfoldrWholePathNm(foldrWholePathNm);
                    fileBasVO.setFoldrWholePathNm(temp.get("foldrWholePathNm"));
                    fileBasVO.setFileNm(fileNm);

                    FileService.nasFileCopy(fileBasVO,flagContext);
                }

                ((SendNasViewActivity)getActivity()).finish();

            } else {
                alert.show();
            }
        } else {
            alert.show();
        }

    }

    public void getFoldrList() {

        Call<JsonObject> listDevBasCall = RestServiceImpl.getInstance(null).listFoldr(foldrBasVO);
        listDevBasCall.enqueue(new Callback<JsonObject>() {

            List<Map<String, String>> tempList = new ArrayList<Map<String, String>>();

            @Override
            public void onResponse(Response<JsonObject> response) {
                if (response.isSuccess()) { // Code 200
                    Gson gson = new Gson();
                    int statusCode = gson.fromJson(response.body().get("statusCode"), Integer.class);
                    String message = new ResponseFailCode().responseFail(statusCode);
                    AlertDialog.Builder alert = new AlertDialog.Builder(SendNasViewActivity.context);
                    if(statusCode == 100){
                        List<Map<String, String>> tempData = new ArrayList<>();
                        tempData = gson.fromJson(response.body().get("listData"), List.class);
                        if(tempData.size() == 0) {
                            alert.setMessage("더이상 하위폴더가 없습니다.");
                            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();     //닫기
                                }
                            });
                            alert.show();
                            ((SendNasViewActivity)getActivity()).setNavi("");
                        } else {
                            mListData = new ArrayList<Map<String, String>>();
                            tempList = gson.fromJson(response.body().get("listData"), List.class);

                            if(rootFolders.size() > 0) {
                                Map<String, String> rootMap = new ArrayMap<String, String>();
                                rootMap.put("foldrNm", "..");
                                mListData.add(rootMap);
                            }
                            mListData.addAll(tempList);
                            isCheckedArray = new boolean[mListData.size()];

                            mAdapter.notifyDataSetChanged();
                        }
                    }else if(statusCode == 400) {
                        alert.setMessage(message);
                        alert.show();
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();     //닫기
                                Intent intent = new Intent(SendNasViewActivity.context, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                DrawerLayoutViewActivity.activity.finish();
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
                AlertDialog.Builder alert = new AlertDialog.Builder(SendNasViewActivity.context);
                alert.setMessage(flagContext.getString(R.string.serverOut));
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                        Intent intent = new Intent(SendNasViewActivity.context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        DrawerLayoutViewActivity.activity.finish();
                        startActivity(intent);
                    }
                });
            }
        });
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            mAdapter.setChecked(position);
            mAdapter.notifyDataSetChanged();
        }
    };

    private class ViewHolder {
        public ImageView mIcon;
        public TextView mText;
        public TextView mFoldrId;
        public ImageView mCheck;
    }

    public static ProgressDialog dialog(){

        return dialog;
    }

    private class ListViewAdapter extends BaseAdapter {

        public ListViewAdapter(Context context) {
            flagContext = context;
        }

        public void setChecked(int position) {
            for(int i=0; i<getCount(); i++) {
                if(i == position) {
                    isCheckedArray[i] = !isCheckedArray[position];
                } else {
                    isCheckedArray[i] = false;
                }
            }
        }

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Map<String,String> getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final int pos = position;

            final ViewHolder holder;

            if(convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_list_foldr, null);

                holder.mIcon = (ImageView) convertView.findViewById(R.id.foldrIcon);
                holder.mText = (TextView) convertView.findViewById(R.id.foldrNm);
                holder.mFoldrId = (TextView) convertView.findViewById(R.id.foldrId);
                holder.mCheck = (ImageView) convertView.findViewById(R.id.foldrCheck);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Map<String, String> mData = mListData.get(position);

            holder.mIcon.setVisibility(View.VISIBLE);
            holder.mText.setText(mData.get("foldrNm"));
            Object obj = (Object) mData.get("foldrId");
            if(obj != null) {
                holder.mFoldrId.setText(obj.toString());
            }
            if(!isCheckedArray[pos]) {
                holder.mIcon.setImageResource(R.drawable.ico_36dp_folder);
                holder.mText.setTextColor(getResources().getColor(R.color.darkGray));
                if(obj == null) { // ..인 경우 check없음
                    holder.mCheck.setVisibility(View.GONE);
                } else {
                    holder.mCheck.setVisibility(View.VISIBLE);
                    holder.mCheck.setImageResource(R.drawable.ico_24dp_done_disable);
                    holder.mText.setTextColor(getResources().getColor(R.color.darkGray));
                    holder.mIcon.setImageResource(R.drawable.ico_36dp_folder);
                }
            } else {
                holder.mCheck.setVisibility(View.VISIBLE);
                holder.mCheck.setImageResource(R.drawable.ico_24dp_done_r);
                holder.mText.setTextColor(getResources().getColor(R.color.darkGray));
                holder.mIcon.setImageResource(R.drawable.ico_36dp_folder_r);
            }

            convertView.findViewById(R.id.item_area).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Map<String,String> itemMap = getItem(pos);

                    Object obj = (Object) itemMap.get("foldrId");

                    if(obj == null) { // ..을 누르면
                        String upFoldrId = rootFolders.get(rootFolders.size()-1);
                        if(upFoldrId.equals("")) {
                            foldrBasVO.setFoldrId(null);
                        } else {
                            foldrBasVO.setFoldrId(rootFolders.get(rootFolders.size()-1));
                        }
                        rootFolders.remove(rootFolders.size()-1); // 마지막 root foldrId 지움

                        ((SendNasViewActivity)getActivity()).setNavi(null);
                    } else {
                        foldrBasVO.setFoldrId(holder.mFoldrId.getText().toString());
                        obj = (Object) itemMap.get("upFoldrId");
                        if(obj == null) { // root를 눌렀을 때 upFolderId를 공백으로 넣었다가 나중에 null로 바꿔줌
                            rootFolders.add("");
                        } else {
                            rootFolders.add(obj.toString()); // 해당 폴더의 상위폴더id를 add
                        }
                        ((SendNasViewActivity)getActivity()).setNavi(" > " +itemMap.get("foldrNm"));
                    }

                    getFoldrList();
                }
            });

            return convertView;
        }

    }
}