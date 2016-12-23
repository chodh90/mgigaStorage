package com.kt.gigastorage.mobile.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kt.gigastorage.mobile.activity.DrawerLayoutViewActivity;
import com.kt.gigastorage.mobile.activity.MainActivity;
import com.kt.gigastorage.mobile.activity.R;
import com.kt.gigastorage.mobile.service.AlertDialogService;
import com.kt.gigastorage.mobile.service.FileDownloadThread;
import com.kt.gigastorage.mobile.service.FileService;
import com.kt.gigastorage.mobile.service.FileViewService;
import com.kt.gigastorage.mobile.service.ResponseFailCode;
import com.kt.gigastorage.mobile.service.TimerService;
import com.kt.gigastorage.mobile.utils.DeviceUtil;
import com.kt.gigastorage.mobile.utils.FileUtil;
import com.kt.gigastorage.mobile.utils.SharedPreferenceUtil;
import com.kt.gigastorage.mobile.vo.ComndQueueVO;
import com.kt.gigastorage.mobile.vo.FileBasVO;
import com.kt.gigastorage.mobile.vo.NoteBasVO;
import com.kt.gigastorage.mobile.vo.NoteBmarkVO;
import com.kt.gigastorage.mobile.vo.NoteListVO;
import com.kt.gigastorage.mobile.webservice.impl.RestServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kt.gigastorage.mobile.activity.R.id.contextIcon;
import static com.kt.gigastorage.mobile.activity.R.id.end;

/**
 * Created by zeroeun on 2016-10-14.
 */

public class BizNoteListFragment extends Fragment {

    private List<Map<String, String>> mListData = new ArrayList<>();

    private AppAdapter mAdapter;
    private SwipeMenuListView mListView;

    private NoteBasVO mNoteBasVO = new NoteBasVO();
    private NoteListVO mNoteListVO = new NoteListVO();
    private NoteBmarkVO noteBmarkVO = new NoteBmarkVO();
    private TextView dirNavi = null;
    private String userId;
    private boolean bookMarkFlag;
    private AlertDialog.Builder alert;
    private TextView dirUpNavi;
    private TextView toolbarTitle;

    public static Context context;

    private ArrayList<String> rootFolderNms = new ArrayList<>();
    private boolean[] swipeStateList;
    private Map<String, String> item;
    private SwipeMenu swipeMenu;
    private int mIndex;
    private String myDevUuid;
    private Map<String, String> itemArea;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(getArguments() != null) {
            mNoteListVO.setNoteId(getArguments().getString("noteId"));
        }

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = DrawerLayoutViewActivity.context;
        View view = inflater.inflate(R.layout.content_swipe_listview, container, false);
        setHasOptionsMenu(true);

        userId = SharedPreferenceUtil.getSharedPreference(context,"userId");
        bookMarkFlag = false;
        alert = AlertDialogService.alert(context);
        myDevUuid = DeviceUtil.getDevicesUUID(context);


        swipeStateList = new boolean[0];

        toolbarTitle = (TextView) ((DrawerLayoutViewActivity)DrawerLayoutViewActivity.context).findViewById(R.id.toobar_title);
        mListView = (SwipeMenuListView) view.findViewById(R.id.sWlistView);
        dirNavi = (TextView) view.findViewById(R.id.dirNavi);
        dirUpNavi = (TextView) view.findViewById(R.id.dirUpNavi);
        dirUpNavi.setVisibility(View.GONE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        float dp = context.getResources().getDisplayMetrics().density;
        int leftGoneDp = (int)(26 * dp);
        params.leftMargin = leftGoneDp;
        dirNavi.setLayoutParams(params);
        dirNavi.setText("> BizNote");
        rootFolderNms.add(dirNavi.getText().toString());

        mAdapter = new AppAdapter();
        mListView.setAdapter(mAdapter); // swipeMenuListView에 어댑터 연결

        mNoteBasVO.setUserId(SharedPreferenceUtil.getSharedPreference(getActivity(),"userId"));
        noteBmarkVO.setUserId(SharedPreferenceUtil.getSharedPreference(getActivity(),"userId"));
        mNoteListVO.setUserId(SharedPreferenceUtil.getSharedPreference(getActivity(),"userId"));

        if(mNoteListVO.getNoteId() == null){
            getNoteMenuListWebservice();
        }else{
            getNoteListWebservice(mNoteListVO);
        }

        toolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                DrawerLayoutViewActivity.activity.changeBizFragment("bizNote",args);
            }
        });


        //Toolbar toolbar = (Toolbar) ((DrawerLayoutViewActivity)DrawerLayoutViewActivity.context).findViewById(R.id.toolbar);
        //toolbar.setOverflowIcon(ContextCompat.getDrawable(DrawerLayoutViewActivity.context, R.drawable.ico_24dp_top_align));

        // 스와이프 메뉴 생성
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                SwipeMenuItem downloadItem = new SwipeMenuItem(getActivity());
                SwipeMenuItem gigaNasItem = new SwipeMenuItem(getActivity());
                SwipeMenuItem appPlayItem = new SwipeMenuItem(getActivity());
                SwipeMenuItem detailItem = new SwipeMenuItem(getActivity());
                SwipeMenuItem bMarkItem = new SwipeMenuItem(getActivity());
                SwipeMenuItem blankItem = new SwipeMenuItem(getActivity());

                switch (menu.getViewType()) {
                    case 0: // bizNote 폴더
                        bMarkItem = new SwipeMenuItem(getActivity());
                        bMarkItem.setBackground(R.color.baseColor);
                        bMarkItem.setWidth(dp2px(80));
                        bMarkItem.setIcon(R.drawable.ico_18dp_bookmark);
                        bMarkItem.setTitle("북마크 추가");
                        bMarkItem.setTitleSize(12);
                        bMarkItem.setTitleColor(R.color.darkGray);

                        blankItem = new SwipeMenuItem(getActivity());
                        blankItem.setBackground(R.color.baseColor);
                        blankItem.setWidth(dp2px(235));

                        menu.addMenuItem(bMarkItem);
                        menu.addMenuItem(blankItem);
                        break;
                    case 1: //내 디바이스 파일
                        detailItem = new SwipeMenuItem(getActivity());
                        detailItem.setBackground(R.color.baseColor);
                        detailItem.setWidth(dp2px(80));
                        detailItem.setIcon(R.drawable.ico_18dp_contextmenu_info);
                        detailItem.setTitle("속성보기");
                        detailItem.setTitleSize(12);
                        detailItem.setTitleColor(R.color.darkGray);

                        appPlayItem = new SwipeMenuItem(getActivity());
                        appPlayItem.setBackground(R.color.baseColor);
                        appPlayItem.setWidth(dp2px(80));
                        appPlayItem.setIcon(R.drawable.ico_18dp_contextmenu_app);
                        appPlayItem.setTitle("앱 실행");
                        appPlayItem.setTitleSize(12);
                        appPlayItem.setTitleColor(R.color.darkGray);

                        gigaNasItem = new SwipeMenuItem(getActivity());
                        gigaNasItem.setBackground(R.color.baseColor);
                        gigaNasItem.setWidth(dp2px(80));
                        gigaNasItem.setIcon(R.drawable.ico_18dp_contextmenu_send);
                        gigaNasItem.setTitle("GiGA NAS로\n 보내기");
                        gigaNasItem.setTitleSize(10);
                        gigaNasItem.setTitleColor(R.color.darkGray);

                        blankItem = new SwipeMenuItem(getActivity());
                        blankItem.setBackground(R.color.baseColor);
                        blankItem.setWidth(dp2px(75));

                        menu.addMenuItem(detailItem);
                        menu.addMenuItem(appPlayItem);
                        menu.addMenuItem(gigaNasItem);
                        menu.addMenuItem(blankItem);
                        break;
                    case 2: // root 책갈피
                        break;
                    case 3: // .. 폴더
                        break;
                    case 4: // 책갈피 삭제
                        bMarkItem = new SwipeMenuItem(getActivity());
                        bMarkItem.setBackground(R.color.baseColor);
                        bMarkItem.setWidth(dp2px(80));
                        bMarkItem.setIcon(R.drawable.ico_18dp_bookmark_del);
                        bMarkItem.setTitle("북마크 삭제");
                        bMarkItem.setTitleSize(12);
                        bMarkItem.setTitleColor(R.color.darkGray);

                        blankItem = new SwipeMenuItem(getActivity());
                        blankItem.setBackground(R.color.baseColor);
                        blankItem.setWidth(dp2px(235));

                        menu.addMenuItem(bMarkItem);
                        menu.addMenuItem(blankItem);
                        break;
                    case 5: //원격지 PC,Android 파일
                        detailItem = new SwipeMenuItem(getActivity());
                        detailItem.setBackground(R.color.baseColor);
                        detailItem.setWidth(dp2px(80));
                        detailItem.setIcon(R.drawable.ico_18dp_contextmenu_info);
                        detailItem.setTitle("속성보기");
                        detailItem.setTitleSize(12);
                        detailItem.setTitleColor(R.color.darkGray);

                        downloadItem = new SwipeMenuItem(getActivity());
                        downloadItem.setBackground(R.color.baseColor);
                        downloadItem.setWidth(dp2px(80));
                        downloadItem.setIcon(R.drawable.ico_18dp_contextmenu_dwld);
                        downloadItem.setTitle("다운로드");
                        downloadItem.setTitleSize(12);
                        downloadItem.setTitleColor(R.color.darkGray);

                        gigaNasItem = new SwipeMenuItem(getActivity());
                        gigaNasItem.setBackground(R.color.baseColor);
                        gigaNasItem.setWidth(dp2px(80));
                        gigaNasItem.setIcon(R.drawable.ico_18dp_contextmenu_send);
                        gigaNasItem.setTitle("GiGA NAS로\n 보내기");
                        gigaNasItem.setTitleSize(10);
                        gigaNasItem.setTitleColor(R.color.darkGray);

                        blankItem = new SwipeMenuItem(getActivity());
                        blankItem.setBackground(R.color.baseColor);
                        blankItem.setWidth(dp2px(75));

                        menu.addMenuItem(detailItem);
                        menu.addMenuItem(downloadItem);
                        menu.addMenuItem(gigaNasItem);
                        menu.addMenuItem(blankItem);
                        break;
                    case 6: //NAS 파일
                        detailItem = new SwipeMenuItem(getActivity());
                        detailItem.setBackground(R.color.baseColor);
                        detailItem.setWidth(dp2px(80));
                        detailItem.setIcon(R.drawable.ico_18dp_contextmenu_info);
                        detailItem.setTitle("속성보기");
                        detailItem.setTitleSize(12);
                        detailItem.setTitleColor(R.color.darkGray);

                        downloadItem = new SwipeMenuItem(getActivity());
                        downloadItem.setBackground(R.color.baseColor);
                        downloadItem.setWidth(dp2px(80));
                        downloadItem.setIcon(R.drawable.ico_18dp_contextmenu_dwld);
                        downloadItem.setTitle("다운로드");
                        downloadItem.setTitleSize(12);
                        downloadItem.setTitleColor(R.color.darkGray);

                        gigaNasItem = new SwipeMenuItem(getActivity());
                        gigaNasItem.setBackground(R.color.baseColor);
                        gigaNasItem.setWidth(dp2px(80));
                        gigaNasItem.setIcon(R.drawable.ico_18dp_contextmenu_send);
                        gigaNasItem.setTitle("GiGA NAS로\n 보내기");
                        gigaNasItem.setTitleSize(10);
                        gigaNasItem.setTitleColor(R.color.darkGray);

                        blankItem = new SwipeMenuItem(getActivity());
                        blankItem.setBackground(R.color.baseColor);
                        blankItem.setWidth(dp2px(75));

                        menu.addMenuItem(detailItem);
                        menu.addMenuItem(downloadItem);
                        menu.addMenuItem(gigaNasItem);
                        menu.addMenuItem(blankItem);
                        break;
                }
            }
        };
        // 생성한 스와이프메뉴 set
        mListView.setMenuCreator(creator);

        // 스와이프메뉴 클릭 리스너
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                item = mListData.get(position);
                String command = "fagMent";
                swipeMenu = menu;
                mIndex = position;
                View view = mAdapter.getViewByPosition(position,mListView);
                ((ImageView)view.findViewById(contextIcon)).setImageResource(R.drawable.ico_36dp_context_open);
                try {
                    if (index != -1) {
                        switch (menu.getViewType()) {
                            case 0: // 폴더
                                switch (index) {
                                    case 0: // 북마크 추가
                                        Object obj = (Object) item.get("noteId");
                                        noteBmarkVO.setNoteId(obj.toString());
                                        mergNoteBmark(noteBmarkVO);
                                        break;
                                }
                                break;
                            case 1: // 내 디바이스
                                switch (index) {
                                    case 0: // 파일속성
                                        DrawerLayoutViewActivity dlv = (DrawerLayoutViewActivity) getActivity();
                                        dlv.intentNoteFileAttrViewActivity(item);
                                        break;
                                    case 1: // 앱 실행
                                        FileViewService.viewFile(context, item.get("foldrWholePathNm"), item.get("fileNm"));
                                        break;
                                    case 2: //GIGA NAS로 내보내기
                                        ((DrawerLayoutViewActivity) context).intentToActivity(item, item.get("osCd"), item.get("devUuid"), command);
                                        break;
                                }
                                break;
                            case 4: // 북마크 삭제
                                switch (index) {
                                    case 0: // 북마크 삭제
                                        Object obj = (Object) item.get("noteId");
                                        noteBmarkVO.setNoteId(obj.toString());
                                        delNoteBmark(noteBmarkVO);
                                        alert.setMessage("북마크가 삭제 되었습니다.");
                                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();     //닫기
                                                mListData.remove(mIndex);
                                                mAdapter.notifyDataSetChanged();
                                            }
                                        });
                                        alert.show();
                                        break;
                                }
                                break;
                            case 5: // 원격지 PC/Android
                                switch (index) {
                                    case 0: // 속성보기
                                        DrawerLayoutViewActivity dlv = (DrawerLayoutViewActivity) getActivity();
                                        dlv.intentNoteFileAttrViewActivity(item);
                                        break;
                                    case 1: // 다운로드

                                        if (item.get("nasSynchYn").equals("Y")) {
                                            new FileDownloadThread(context).execute(item.get("foldrWholePathNm"), item.get("fileNm"), item.get("devUuid"), "N"); // params = 폴더명,파일이름,디바이스Uuid,app실행여부
                                            break;
                                        } else {
                                            ComndQueueVO comndQueueVO = new ComndQueueVO();
                                            if (item.get("osCd").equals("A")) { // osCd = W, A 분기 처리
                                                comndQueueVO.setComnd("RALA");
                                                comndQueueVO.setFromOsCd("A");
                                            }
                                            if (item.get("osCd").equals("W")) {
                                                comndQueueVO.setComnd("RWLA");
                                                comndQueueVO.setFromOsCd("W");
                                            }
                                            comndQueueVO.setFromUserId(userId);
                                            comndQueueVO.setFromFoldr(item.get("foldrWholePathNm"));
                                            comndQueueVO.setFromFileNm(item.get("fileNm"));
                                            Object objFileId = item.get("fileId");
                                            comndQueueVO.setFromFileId(objFileId.toString());
                                            comndQueueVO.setFromDevUuid(item.get("devUuid"));
                                            comndQueueVO.setToFoldr("/Mobile");
                                            comndQueueVO.setToOsCd("A");
                                            comndQueueVO.setToDevUuid(myDevUuid);
                                            comndQueueVO.setComndOsCd("A");
                                            comndQueueVO.setComndDevUuid(myDevUuid);

                                            FileService.fileDownloadWebservice(comndQueueVO, context, "N");

                                            break;
                                        }
                                    case 2: // nas로 보내기
                                        ((DrawerLayoutViewActivity) getActivity()).intentToActivity(item, item.get("osCd"), item.get("devUuid"), command);
                                        break;
                                }
                                break;
                            case 6: // NAS 파일
                                switch (index) {
                                    case 0: // 파일속성
                                        DrawerLayoutViewActivity dlv = (DrawerLayoutViewActivity) getActivity();
                                        dlv.intentNoteFileAttrViewActivity(item);
                                        break;
                                    case 1: // 다운로드
                                        new FileDownloadThread(context).execute(item.get("foldrWholePathNm"), item.get("fileNm"), "", "N"); // params = 폴더명,파일이름,디바이스Uuid,app실행여부
                                        break;
                                    case 2: //GIGA NAS로 내보내기
                                        ((DrawerLayoutViewActivity) context).intentToActivity(item, item.get("osCd"), item.get("devUuid"), command);
                                        break;
                                }
                                break;
                        }
                    }
                    return false;
                }catch (Exception e){

                }
                return false;
            }
        });

        mListView.setOnMenuStateChangeListener(new SwipeMenuListView.OnMenuStateChangeListener() {
            @Override
            public void onMenuOpen(int position) {

                View view = mAdapter.getViewByPosition(position,mListView);
                ((ImageView)view.findViewById(contextIcon)).setImageResource(R.drawable.ico_36dp_context_close);
            }

            @Override
            public void onMenuClose(int position) {
                View view = mAdapter.getViewByPosition(position,mListView);
                ((ImageView)view.findViewById(contextIcon)).setImageResource(R.drawable.ico_36dp_context_open);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ImageView contextIcon = (ImageView)view.findViewById(R.id.contextIcon);

                if(!swipeStateList[position]) { // 닫혀있는상태
                    contextIcon.setImageResource(R.drawable.ico_36dp_context_close);
                    mListView.smoothOpenMenu(position);
                } else { // 열려있는 상태
                    contextIcon.setImageResource(R.drawable.ico_36dp_context_open);
                    mListView.smoothCloseMenu();
                }

                swipeStateList[position] = !swipeStateList[position];
            }
        });

        return view;
    }

    private void getNoteMenuListWebservice() {
        Call<JsonObject> listFoldrCall = RestServiceImpl.getInstance(null).listNoteMenu(mNoteBasVO);
        listFoldrCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response) {
                if (response.isSuccess()) {
                    Gson gson = new Gson();
                    int statusCode = gson.fromJson(response.body().get("statusCode"), Integer.class);
                    String message = new ResponseFailCode().responseFail(statusCode);
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    if(statusCode == 100) {
                        bookMarkFlag = false;
                        mListData = new ArrayList<Map<String, String>>();
                        mListData = gson.fromJson(response.body().get("listData"), List.class);
                        dirNavi.setText("> BizNote");
                        Map<String, String> bookMark = new ArrayMap<String, String>();
                        bookMark.put("userId", userId);
                        bookMark.put("noteNm", "책갈피");
                        bookMark.put("bookMark","bookMark");
                        mListData.add(bookMark);
                        mAdapter.notifyDataSetChanged();
                        swipeStateList = new boolean[mListData.size()];
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
                    }
                });
            }
        });
    }

    private void mergNoteBmark(NoteBmarkVO noteBmarkVO) {
        Call<JsonObject> mergNoteBmarkCall = RestServiceImpl.getInstance(null).mergNoteBmark(noteBmarkVO);
        mergNoteBmarkCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response) {
                if (response.isSuccess()) {
                    Gson gson = new Gson();
                    int statusCode = gson.fromJson(response.body().get("statusCode"), Integer.class);
                    String message = new ResponseFailCode().responseFail(statusCode);
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    if(statusCode == 100) {

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
                    }
                });
            }
        });
    }
    private void delNoteBmark(NoteBmarkVO bmarkVO) {
        Call<JsonObject> delNoteBmarkCall = RestServiceImpl.getInstance(null).delNoteBmark(bmarkVO);
        delNoteBmarkCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response) {
                if (response.isSuccess()) {
                    Gson gson = new Gson();
                    int statusCode = gson.fromJson(response.body().get("statusCode"), Integer.class);
                    String message = new ResponseFailCode().responseFail(statusCode);
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    if(statusCode == 100) {

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
                    }
                });
            }
        });
    }

    private void getNoteBmarkListWebservice(NoteBmarkVO noteBmarkVO) {
        Call<JsonObject> listNoteBmarkCall = RestServiceImpl.getInstance(null).listNoteBmark(noteBmarkVO);
        listNoteBmarkCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response) {
                if (response.isSuccess()) {
                    Gson gson = new Gson();
                    int statusCode = gson.fromJson(response.body().get("statusCode"), Integer.class);
                    String message = new ResponseFailCode().responseFail(statusCode);
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    if(statusCode == 100) {
                        mListData = new ArrayList<Map<String, String>>();
                        bookMarkFlag = true;
                        Map<String, String> rootFoldr = new ArrayMap<String, String>();
                        rootFoldr.put("noteNm", "..");
                        mListData.add(rootFoldr);
                        mListData.addAll(gson.fromJson(response.body().get("listData"), List.class));
                        dirNavi.setText("> 책갈피");
                        mAdapter.notifyDataSetChanged();
                        swipeStateList = new boolean[mListData.size()];
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
                    }
                });
            }
        });
    }

    public void getNoteListWebservice(NoteListVO mNoteListVO) {

        Call<JsonObject> listFileCall = RestServiceImpl.getInstance(null).listNote(mNoteListVO);
        listFileCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response) {
                if(response.isSuccess()) {
                    Gson gson = new Gson();
                    int statusCode = gson.fromJson(response.body().get("statusCode"), Integer.class);
                    String message = new ResponseFailCode().responseFail(statusCode);
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    if(statusCode == 100){
                        mListData = new ArrayList<Map<String, String>>();
                        Map<String, String> rootMap = new ArrayMap<String, String>();
                        rootMap.put("noteNm", "..");
                        mListData.add(rootMap);
                        mListData.addAll(gson.fromJson(response.body().get("listData"), List.class));
                        mAdapter.notifyDataSetChanged();
                        swipeStateList = new boolean[mListData.size()];
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
                    }
                });
            }
        });
    }

    public void listBmarkFile(NoteBmarkVO noteBmarkVO) {

        Call<JsonObject> listNoteBmarkFileCall = RestServiceImpl.getInstance(null).listNoteBmarkFile(noteBmarkVO);
        listNoteBmarkFileCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response) {
                if(response.isSuccess()) {
                    Gson gson = new Gson();
                    int statusCode = gson.fromJson(response.body().get("statusCode"), Integer.class);
                    String message = new ResponseFailCode().responseFail(statusCode);
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    if(statusCode == 100){
                        mListData = new ArrayList<Map<String, String>>();
                        Map<String, String> rootMap = new ArrayMap<String, String>();
                        rootMap.put("noteNm", "..");
                        mListData.add(rootMap);
                        mListData.addAll(gson.fromJson(response.body().get("listData"), List.class));
                        mAdapter.notifyDataSetChanged();
                        swipeStateList = new boolean[mListData.size()];
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
                    }
                });
            }
        });
    }

    class AppAdapter extends BaseAdapter {

        class ViewHolder {

            ImageView iv_icon; //폴더 및 파일 아이콘
            TextView tv_name; // 폴더 및 파일 명
            LinearLayout additionArea; // 생성일자, 파일사이즈 영역
            ImageView contextIcon; // 컨텍스트메뉴 아이콘
            TextView cretDate; // 생성일자
            ImageView dev_icon; // 해당기기 아이콘
            TextView devNm; // 해당기기명

            public ViewHolder(View view) {

                iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                tv_name = (TextView) view.findViewById(R.id.tv_name);
                additionArea = (LinearLayout) view.findViewById(R.id.additionArea);
                contextIcon = (ImageView) view.findViewById(R.id.contextIcon);
                cretDate = (TextView) view.findViewById(R.id.item_date);
                dev_icon = (ImageView) view.findViewById(R.id.dev_icon);
                devNm = (TextView) view.findViewById(R.id.devNm);

                view.setTag(this);
            }
        }

        public View getViewByPosition(int pos, ListView listView) {

            final int firstListItemPosition = listView.getFirstVisiblePosition();
            final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

            if (pos < firstListItemPosition || pos > lastListItemPosition ) {
                return listView.getAdapter().getView(pos, null, listView);
            } else {
                final int childIndex = pos - firstListItemPosition;
                return listView.getChildAt(childIndex);
            }
        }

        // 현재 아이템의 수를 리턴
        @Override
        public int getCount() {
            return mListData.size();
        }

        // 현재 아이템의 object를 리턴
        @Override
        public Map<String,String> getItem(int position) {
            try{
                if(position != -1){
                    return mListData.get(position);
                }else{
                    throw new Exception();
                }
            }catch (Exception e){
                Log.d("/////////////////에러:","index 에러");
            }
            return mListData.get(0);
        }

        // 아이템 포지션의 id값 리턴
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 9;
        }

        @Override
        public int getItemViewType(int position) {

            int returnPos = 0;

            if(getItem(position).get("fileId") != null) {

                if (getItem(position).get("devUuid").equals(SharedPreferenceUtil.getSharedPreference(context, "devUuid").toString())) { // 접속기기의 파일
                    returnPos = 1;
                } else {
                    if (getItem(position).get("osCd").equals("W") || getItem(position).get("osCd").equals("A")) {  //접속기기 외 PC
                        returnPos = 5;
                    }else if (getItem(position).get("osCd").equals("G")) { //GIGANAS
                        returnPos = 6;
                    }
                }


            }else if(getItem(position).get("bookMark") != null){
                if(getItem(position).get("noteNm").equals("..")){
                    returnPos = 2;
                }else{
                    returnPos = 3;
                }
            }else{
                if(bookMarkFlag == true){
                    if(!getItem(position).get("noteNm").equals("..")){
                        returnPos = 4;
                    }else{
                        returnPos = 2;
                    }
                }else{
                    if(!getItem(position).get("noteNm").equals("..")){
                        returnPos = 0;
                    }else{
                        returnPos = 2;
                    }
                }
            }
            return returnPos;
        }

        // 출력될 아이템 관리
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            try {
                // 리스트가 길 때, 화면에 보이지 않는 아이템은 convertView가 null인 상태임(고로 여기를 탐)
                if (convertView == null) {
                    convertView = View.inflate(getActivity(),
                            R.layout.item_list_dir_search, null);
                    new AppAdapter.ViewHolder(convertView);
                }

                final AppAdapter.ViewHolder holder = (AppAdapter.ViewHolder) convertView.getTag();

                final Map<String, String> mData = getItem(position);

                holder.contextIcon.setImageResource(R.drawable.ico_36dp_context_open);

                if (mData.get("fileId") == null) {

                    holder.iv_icon.setImageResource(R.drawable.ico_36dp_folder);
                    holder.tv_name.setText(mData.get("noteNm"));
                    if (mData.get("noteNm").equals("..")) {
                        holder.additionArea.setVisibility(View.GONE);
                        holder.contextIcon.setVisibility(View.GONE);
                    } else if (mData.get("noteNm").equals("책갈피")) {
                        holder.additionArea.setVisibility(View.GONE);
                        holder.contextIcon.setVisibility(View.GONE);
                    } else {
                        holder.additionArea.setVisibility(View.VISIBLE);
                        holder.contextIcon.setVisibility(View.VISIBLE);
                    }
                    Object obj = mData.get("noteId");

                    if (obj == null && !mData.get("noteNm").equals("책갈피")) {
                        convertView.findViewById(R.id.dir_item_area).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (bookMarkFlag == false) {
                                    getNoteMenuListWebservice();
                                } else if (dirNavi.getText().equals("> 책갈피")) {
                                    getNoteMenuListWebservice();
                                } else {
                                    getNoteBmarkListWebservice(noteBmarkVO);
                                }

                            }
                        });
                    } else if (mData.get("noteNm").equals("책갈피")) {
                        convertView.findViewById(R.id.dir_item_area).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mData.get("noteNm").equals("..")) {
                                    getNoteMenuListWebservice();
                                } else {
                                    getNoteBmarkListWebservice(noteBmarkVO);
                                }
                            }
                        });
                    } else {
                        convertView.findViewById(R.id.dir_item_area).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Map<String, String> itemMap = getItem(position);
                                if(bookMarkFlag == true) {
                                    noteBmarkVO.setNoteId(((Object) itemMap.get("noteId")).toString());
                                    listBmarkFile(noteBmarkVO);
                                }else{
                                    mNoteListVO.setNoteId(((Object) itemMap.get("noteId")).toString());
                                    getNoteListWebservice(mNoteListVO);
                                }
                                dirNavi.setText("> " + itemMap.get("noteNm"));
                                float dp = context.getResources().getDisplayMetrics().density;
                                int widthDp = (int) (320 * dp);
                                dirNavi.setWidth(widthDp);
                                dirNavi.setSingleLine(true);
                                dirNavi.setEllipsize(TextUtils.TruncateAt.END);
                            }
                        });
                    }
                } else {
                    holder.additionArea.setVisibility(View.VISIBLE);
                    holder.contextIcon.setVisibility(View.VISIBLE);
                    String etsionNm = mData.get("etsionNm");
                    int resourceInt = FileUtil.getIconByEtsion(etsionNm);
                    holder.iv_icon.setImageResource(resourceInt);
                    holder.tv_name.setText(mData.get("fileNm"));
                    holder.cretDate.setText(mData.get("cretDate"));
                    holder.devNm.setText(mData.get("devNm"));
                    Object obj = mData.get("fileId");
                }

                convertView.findViewById(R.id.dir_item_area).setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        itemArea = getItem(position);

                        if (itemArea.get("fileId") != null) {

                            if (itemArea.get("osCd").equals("G")) {

                                new FileDownloadThread(context).execute(itemArea.get("foldrWholePathNm"), itemArea.get("fileNm"), itemArea.get("devUuid"), "Y"); // params = 폴더명,파일이름,디바이스Uuid,app실행여부

                            } else if (itemArea.get("devUuid").equals(myDevUuid)) {

                                FileViewService.viewFile(context, itemArea.get("foldrWholePathNm"), itemArea.get("fileNm"));

                            } else if (itemArea.get("nasSynchYn").equals("Y")) {

                                new FileDownloadThread(context).execute(itemArea.get("foldrWholePathNm"), itemArea.get("fileNm"), itemArea.get("devUuid"), "Y"); // params = 폴더명,파일이름,디바이스Uuid,app실행여부

                            } else {
                                ComndQueueVO comndQueueVO = new ComndQueueVO();
                                if (itemArea.get("osCd").equals("A")) { // osCd = W, A 분기 처리
                                    comndQueueVO.setComnd("RALA");
                                    comndQueueVO.setFromOsCd("A");
                                }
                                if (itemArea.get("osCd").equals("W")) {
                                    comndQueueVO.setComnd("RWLA");
                                    comndQueueVO.setFromOsCd("W");
                                }
                                comndQueueVO.setFromUserId(userId);
                                comndQueueVO.setFromFoldr(itemArea.get("foldrWholePathNm"));
                                comndQueueVO.setFromFileNm(itemArea.get("fileNm"));
                                Object objFileId = itemArea.get("fileId");
                                comndQueueVO.setFromFileId(objFileId.toString());
                                comndQueueVO.setFromDevUuid(itemArea.get("devUuid"));
                                comndQueueVO.setToFoldr("/Mobile");
                                comndQueueVO.setToOsCd("A");
                                comndQueueVO.setToDevUuid(myDevUuid);
                                comndQueueVO.setComndOsCd("A");
                                comndQueueVO.setComndDevUuid(myDevUuid);

                                FileService.fileDownloadWebservice(comndQueueVO, context, "Y");
                                TimerService.timerStart(itemArea.get("fileNm"), context);
                            }
                        }

                        return false;
                    }
                });

                return convertView;
            } catch (Exception e) {
                Log.d("////////////에러", "Error");
            }
            return convertView;
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}