package com.kt.gigastorage.mobile.service;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kt.gigastorage.mobile.activity.BizNoteEmailRefFileViewActivity;
import com.kt.gigastorage.mobile.activity.DrawerLayoutViewActivity;
import com.kt.gigastorage.mobile.activity.MainActivity;
import com.kt.gigastorage.mobile.activity.R;
import com.kt.gigastorage.mobile.activity.SendNasViewActivity;
import com.kt.gigastorage.mobile.utils.DeviceUtil;
import com.kt.gigastorage.mobile.utils.FileUtil;
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
 * Created by araise on 2016-10-25.
 */

public class FileService {

    public static List<Map<String,String>> fileCreateList = new ArrayList<Map<String,String>>();
    public static List<Map<String,String>> foldrCreateList = new ArrayList<Map<String,String>>();
    public static List<Map<String,String>> fileDeleteList = new ArrayList<Map<String,String>>();
    public static List<Map<String,String>> foldrDeleteList = new ArrayList<Map<String,String>>();
    public static List<String> clientFoldrPath = new ArrayList<String>();

    public static Context context = MainActivity.context;
    public static Context mContext;
    public static String userId;
    public static String appPlay;

    // FOLDR&FILE_LIST_SELECT
    public static void syncFoldrInfo() {
        userId = SharedPreferenceUtil.getSharedPreference(context,context.getString(R.string.userId));

        final FoldrBasVO foldrBasVO = new FoldrBasVO();
        foldrBasVO.setUserId(userId);
        foldrBasVO.setDevUuid(DeviceUtil.getDevicesUUID(context));

        Call<JsonObject> syncFoldrInfoCall = RestServiceImpl. getInstance(null).syncFoldrInfo(foldrBasVO);
        syncFoldrInfoCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response) {
                if(response.isSuccess()) {
                    Gson gson = new Gson();
                    //서버 파일,폴더 정보
                    List<Map<String,String>> serverList = new ArrayList<Map<String,String>>();
                    serverList = gson.fromJson(response.body().get("listData"), List.class );
                    foldrCreateList = new ArrayList<Map<String, String>>();
                    foldrDeleteList = new ArrayList<Map<String, String>>();
                    clientFoldrPath = new ArrayList<String>();
                    //클라이언트 파일,폴더 정보
                    List<Map<String,String>>  clientList = new ArrayList<Map<String,String>>();
                    clientList = FileUtil.matching();

                    List<Map<String,String>> clientListTemp = new ArrayList<Map<String,String>>();
                    List<Map<String,String>> serverListTemp = new ArrayList<Map<String,String>>();

                    clientListTemp = clientList;
                    serverListTemp = serverList;

                    //클라이언트 파일,폴더 정보와 서버 파일,폴더 정보를 비교
                    for(int i=0; i<clientListTemp.size(); i++){
                        boolean createIsEqual = false;

                        for(int j=0; j<serverListTemp.size(); j++){
                            //폴더 비교 create
                            if (clientList.get(i).containsKey("foldrNm")) {
                                if (clientListTemp.get(i).containsValue(serverListTemp.get(j).get("foldrWholePathNm"))) {
                                    createIsEqual = true;
                                    break;
                                }
                            }
                        }

                        if(createIsEqual == false){
                            foldrCreateList.add(clientListTemp.get(i));
                        }
                    }

                    clientListTemp = clientList;
                    serverListTemp = serverList;


                    //Foldr패스 쪼개서 저장
                    for(int i=0; i<clientListTemp.size(); i++){
                        if (clientList.get(i).containsKey("foldrNm")) {

                            String[] arrayPath = clientListTemp.get(i).get("foldrWholePathNm").split("/");
                            int lastIndex = arrayPath.length;
                            String pathNm = "";

                            for (int k = 0; k < lastIndex; k++) {
                                if (k == 0) {
                                    pathNm = arrayPath[k];
                                } else {
                                    pathNm += "/" + arrayPath[k];
                                }
                                clientFoldrPath.add(pathNm);
                            }
                        }
                    }

                    //서버리스트
                    for(int i=0; i<serverListTemp.size(); i++){

                        boolean deleteIsEqual = false;
                        for(int j=0; j<clientListTemp.size(); j++) {

                            for (int l = 0; l < clientFoldrPath.size(); l++) {
                                //폴더 비교 delete
                                if (serverListTemp.get(i).containsValue(clientFoldrPath.get(l))) {
                                    deleteIsEqual = true;
                                    break;
                                }
                            }
                        }
                        if (deleteIsEqual == false) {
                            foldrDeleteList.add(serverListTemp.get(i));
                        }
                    }

                    //폴더 동기화 Start
                    foldrMetaInsert();
                } else {
                    Log.d("No Success Message :", response.message());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Fail Message", t.getMessage());
            }
        });

    }


    // Sync_File_Info
    public static void syncFileInfo() {
        FileBasVO fileBasVO = new FileBasVO();
        fileBasVO.setUserId(userId);
        fileBasVO.setDevUuid(DeviceUtil.getDevicesUUID(context));
        Call<JsonObject> syncFileInfoCall = RestServiceImpl.getInstance(null).syncFileInfo(fileBasVO);
        syncFileInfoCall.enqueue( new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response) {
                if(response.isSuccess()) {
                    Gson gson = new Gson();
                    List<Map<String,String>> serverList = new ArrayList<Map<String,String>>();
                    serverList = gson.fromJson(response.body().get("listData"), List.class );
                    fileCreateList = new ArrayList<Map<String, String>>();
                    fileDeleteList = new ArrayList<Map<String, String>>();
                    clientFoldrPath = new ArrayList<String>();
                    //클라이언트 파일,폴더 정보
                    List<Map<String,String>>  clientList = new ArrayList<Map<String,String>>();
                    clientList = FileUtil.matching();

                    List<Map<String,String>> clientListTemp = new ArrayList<Map<String,String>>();
                    List<Map<String,String>> serverListTemp = new ArrayList<Map<String,String>>();

                    clientListTemp = clientList;
                    serverListTemp = serverList;

                    //File일시에 fileWholePathNm 추가
                    for(int i=0; i<serverListTemp.size(); i++){
                        if (serverListTemp.get(i).containsKey("fileNm")){
                            serverListTemp.get(i).put("fileWholePathNm",serverListTemp.get(i).get("foldrWholePathNm")+"/"+serverList.get(i).get("fileNm"));
                        }
                    }

                    //클라이언트 파일,폴더 정보와 서버 파일,폴더 정보를 비교

                    /**
                     * 클라이언트에 있는 파일정보와 서버의 파일 정보를 비교하여
                     * 클라이언트에만 있으면 fileCreateList에 데이터를 담는다.
                     */
                    for(int i=0; i<clientListTemp.size(); i++){

                        boolean createIsEqual = false;
                        for(int j=0; j<serverListTemp.size(); j++){
                            //파일 비교 create
                            if (clientList.get(i).containsKey("fileNm")) {

                                if (clientListTemp.get(i).containsValue(serverListTemp.get(j).get("foldrWholePathNm") + "/" + serverList.get(j).get("fileNm"))) {
                                    createIsEqual = true;
                                    break;
                                }
                            }
                        }

                        if (createIsEqual == false) {
                            if (clientList.get(i).containsKey("fileNm")) {  //
                                fileCreateList.add(clientListTemp.get(i));
                            }
                        }

                    }

                    //서버리스트
                    /**
                     * 서버에 있는 파일정보와 클라이언트의 파일 정보를 비교하여
                     * 서버에만 있으면 fileDeleteList 데이터를 담는다.
                     */
                    for(int i=0; i<serverListTemp.size(); i++){

                        boolean deleteIsEqual = false;
                        for(int j=0; j<clientListTemp.size(); j++) {
                            //파일 비교 delete
                            if(clientListTemp.get(j).containsKey("foldrNm")){   //클라이언트 정보중 폴더정보는 무시한다.
                                continue;
                            }
                            if (serverListTemp.get(i).containsValue(clientListTemp.get(j).get("fileWholePathNm"))) {
                                deleteIsEqual = true;
                                break;
                            }

                        }
                        if (deleteIsEqual == false) {
                            fileDeleteList.add(serverListTemp.get(i));
                        }
                    }


                    //폴더 Delete
                    /*fileMetaInsert();*/
                    foldrMetaDelete();

                } else {
                    Log.d("No Success Message :", response.message());
                }
            }
            @Override
            public void onFailure(Throwable t) {
                Log.e("Fail Message", t.getMessage());
            }
        });
    }

    // FOLDR_META_INSERT
    public static void foldrMetaInsert() {

        List<FoldrBasVO> list = new ArrayList<FoldrBasVO>();

        for(int i=0; i<foldrCreateList.size(); i++) {
            FoldrBasVO foldrBasVO = new FoldrBasVO();
            if (foldrCreateList.get(i).containsKey("foldrNm")) {
                foldrBasVO.setUserId(userId);
                foldrBasVO.setDevUuid(DeviceUtil.getDevicesUUID(context));
                foldrBasVO.setCmd("C");
                foldrBasVO.setFoldrNm(foldrCreateList.get(i).get("foldrNm"));
                foldrBasVO.setFoldrWholePathNm(foldrCreateList.get(i).get("foldrWholePathNm"));
                foldrBasVO.setCretDate(foldrCreateList.get(i).get("foldrAmdDate"));
                foldrBasVO.setAmdDate(foldrCreateList.get(i).get("foldrAmdDate"));
                list.add(foldrBasVO);
            }
        }

        Call<JsonObject> foldrMetaInsertCall = RestServiceImpl. getInstance(null).foldrMetaInsert(list);
        foldrMetaInsertCall.enqueue( new Callback<JsonObject>() {

            @Override
            public void onResponse(Response<JsonObject> response) {
                if(response.isSuccess()) {
                    Log.d("Success action","success");
                    syncFileInfo();
                } else {
                    Log.d("No Success Message :", response.message());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Fail Message", t.getMessage());
            }
        });
    }

    // File_META_INSERT
    public static void fileMetaInsert() {

        List<FileBasVO> list = new ArrayList<FileBasVO>();
        for(int i=0; i<fileCreateList.size(); i++) {
            //파일 create
            FileBasVO fileBasVO = new FileBasVO();
            if (fileCreateList.get(i).containsKey("fileNm")) {
                int pos = fileCreateList.get(i).get("fileNm").lastIndexOf( "." );
                String etsionNm = fileCreateList.get(i).get("fileNm").substring( pos + 1 );
                fileBasVO.setUserId(userId);
                fileBasVO.setDevUuid(DeviceUtil.getDevicesUUID(context));
                fileBasVO.setCmd("C");
                fileBasVO.setFileNm(fileCreateList.get(i).get("fileNm"));
                fileBasVO.setFileSize(fileCreateList.get(i).get("fileSize"));
                fileBasVO.setFoldrWholePathNm(fileCreateList.get(i).get("foldrWholePathNm"));
                fileBasVO.setCretDate(fileCreateList.get(i).get("fileAmdDate"));
                fileBasVO.setAmdDate(fileCreateList.get(i).get("fileAmdDate"));
                fileBasVO.setEtsionNm(etsionNm);
                list.add(fileBasVO);
            }
        }

        Call<JsonObject> fileMetaInsertCall = RestServiceImpl. getInstance(null).fileMetaInsert(list);
        fileMetaInsertCall.enqueue( new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response) {
                if(response.isSuccess()) {
                    Log.d("Success action","success");
                    //폴더 삭제(폴더,파일 동기화)
                    /*fileMetaDelete();*/
                } else {
                    Log.d("No Success Message :", response.message());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Fail Message", t.getMessage());
            }
        });
    }

    // File_META_Delete
    public static void fileMetaDelete() {
        List<FileBasVO> list = new ArrayList<FileBasVO>();
        //폴더,파일 delete

        for(int i=0; i<fileDeleteList.size(); i++){
            if (fileDeleteList.get(i).containsKey("fileNm")) {
                FileBasVO fileBasVO = new FileBasVO();
                fileBasVO.setUserId(userId);
                fileBasVO.setDevUuid(DeviceUtil.getDevicesUUID(context));
                fileBasVO.setCmd("D");
                fileBasVO.setFoldrWholePathNm(fileDeleteList.get(i).get("foldrWholePathNm"));
                fileBasVO.setDevUuid(DeviceUtil.getDevicesUUID(context));
                fileBasVO.setFileNm(fileDeleteList.get(i).get("fileNm"));
                list.add(fileBasVO);
            }
        }

        Call<JsonObject> fileMetaDeleteCall = RestServiceImpl. getInstance(null).deleteFileMeta(list);

        fileMetaDeleteCall.enqueue( new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response) {
                if(response.isSuccess()) {
                    /*foldrMetaDelete();*/
                    fileMetaInsert();
                    Log.d("Success action","success");
                } else {
                    Log.d("No Success Message :", response.message());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Fail Message", t.getMessage());
            }
        });
    }

    // Foldr_META_Delete
    public static void foldrMetaDelete() {

        List<FoldrBasVO> list = new ArrayList<FoldrBasVO>();
        //폴더,파일 delete
        for(int i=0; i<foldrDeleteList.size(); i++){
            if (!foldrDeleteList.get(i).containsKey("fileNm")) {
                FoldrBasVO foldrBasVO = new FoldrBasVO();
                foldrBasVO.setUserId(userId);
                foldrBasVO.setDevUuid(DeviceUtil.getDevicesUUID(context));
                foldrBasVO.setCmd("D");
                foldrBasVO.setDevUuid(DeviceUtil.getDevicesUUID(context));
                foldrBasVO.setFoldrWholePathNm(foldrDeleteList.get(i).get("foldrWholePathNm"));
                list.add(foldrBasVO);
            }
        }

        Call<JsonObject> foldrMetaDeleteCall = RestServiceImpl.getInstance(null).deleteFoldrMeta(list);
        foldrMetaDeleteCall.enqueue( new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response) {
                if(response.isSuccess()) {
                    //파일 정보 삭제
                    fileMetaDelete();

                    Log.d("Success action","success");
                } else {
                    Log.d("No Success Message :", response.message());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Fail Message", t.getMessage());
            }
        });
    }


    public static void fileDownloadWebservice(ComndQueueVO comndQueueVO,Context context,String appPlayYn) {
        appPlay = appPlayYn;
        mContext = context;
        Call<JsonObject> reqFileDownCall = RestServiceImpl.getInstance(null).nasFileDownload(comndQueueVO);
        reqFileDownCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response) {
                if (response.isSuccess()) { // Code 200
                    if(appPlay.equals("N")){
                        Gson gson = new Gson();
                        int statusCode = gson.fromJson(response.body().get("statusCode"), Integer.class);
                        if(statusCode == 501){
                            AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();     //닫기
                                    /*if(mContext == DrawerLayoutViewActivity.context){
                                        DrawerLayoutViewActivity.refresh();
                                    }*/
                                }
                            });
                            alert.setMessage("원격지 PC가 오프라인 상태입니다.");
                            alert.show();
                        }else{
                            String message = new ResponseFailCode().responseFail(statusCode);
                            AlertDialog.Builder alert = AlertDialogService.alert(mContext);
                            alert.setMessage(message);
                            alert.show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                alert.setMessage(SendNasViewActivity.context.getString(R.string.serverOut));
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                        Intent intent = new Intent(mContext, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mContext.startActivity(intent);
                    }
                });
                alert.show();
            }
        });
    }

    public static void nasFileCopy(FileBasVO fileBasVO,Context context) {
        mContext = context;
        Call<JsonObject> nasFileCopyCall = RestServiceImpl.getInstance(null).nasFileCopy(fileBasVO);
        nasFileCopyCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response) {
                if (response.isSuccess()) { // Code 200
                    Gson gson = new Gson();
                    int statusCode = gson.fromJson(response.body().get("statusCode"), Integer.class);
                    String message = new ResponseFailCode().responseFail(statusCode);
                    AlertDialog.Builder alert = AlertDialogService.alert(mContext);
                    if(statusCode == 100){
                        alert.setMessage("NAS로 내보내기 성공");
                    }else if(statusCode != 100 && statusCode != 400){
                        alert.setMessage(message);
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();     //닫기
                            }
                        });
                    }
                    alert.show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                alert.setMessage(DrawerLayoutViewActivity.context.getString(R.string.serverOut));
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                        Intent intent = new Intent(mContext, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        DrawerLayoutViewActivity.context.startActivity(intent);
                    }
                });
                alert.show();
            }
        });
    }

    public static void nasFileDel(FileBasVO fileBasVO,Context context) {
        mContext = context;
        Call<JsonObject> nasFileDelCall = RestServiceImpl.getInstance(null).nasFileDel(fileBasVO);
        nasFileDelCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response) {
                if (response.isSuccess()) { // Code 200
                    Gson gson = new Gson();
                    int statusCode = gson.fromJson(response.body().get("statusCode"), Integer.class);
                    String message = new ResponseFailCode().responseFail(statusCode);
                    AlertDialog.Builder alert = AlertDialogService.alert(mContext);
                    if(statusCode == 100){
                        FileService.syncFoldrInfo();
                        alert.setMessage("파일 삭제가 완료 되었습니다.");
                    }else if(statusCode != 100 && statusCode != 400){
                        alert.setMessage(message);
                    }
                    alert.show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                alert.setMessage(DrawerLayoutViewActivity.context.getString(R.string.serverOut));
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                        Intent intent = new Intent(mContext, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mContext.startActivity(intent);
                    }
                });
                alert.show();
            }
        });
    }
}