package com.kt.gigastorage.mobile.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import com.kt.gigastorage.mobile.service.AlertDialogService;
import com.kt.gigastorage.mobile.service.FileDownloadThread;
import com.kt.gigastorage.mobile.service.FileService;
import com.kt.gigastorage.mobile.service.FileViewService;
import com.kt.gigastorage.mobile.service.ProgressService;
import com.kt.gigastorage.mobile.service.ResponseFailCode;
import com.kt.gigastorage.mobile.service.TimerService;
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

import static com.kt.gigastorage.mobile.activity.R.id.contextIcon;
import static com.kt.gigastorage.mobile.activity.R.id.sWlistView;

/**
 * Created by araise on 2016-10-19.
 */

public class FileSearchViewActivity extends Activity {

    private List<Map<String, String>> mListData = new ArrayList<>();

    private AppAdapter mAdapter;
    private SwipeMenuListView mListView;
    private EditText searchKeyword;
    private LinearLayout totalLine;
    private TextView searchTotal;
    private CheckBox isChecked;

    private ProgressDialog mProgDlg;
    private AlertDialog.Builder alert;
    private static String devUuid;
    private static String userId;
    private static String myDevUuid;
    private static String fileShar;
    private static Map<String, String> item;
    private static int mIndex;

    private FoldrBasVO foldrBasVO = new FoldrBasVO();

    public static Context context; // DrawerLayoutViewActivity
    public static Context mContext; // FileSearchViewActivity
    public static FileSearchViewActivity activity;
    private Map<String, String> itemArea;
    private boolean[] swipeStateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_search_layout);

        context = DrawerLayoutViewActivity.context;
        mContext = FileSearchViewActivity.this;
        activity = FileSearchViewActivity.this;

        Intent intent = getIntent();

        fileShar = "N";
        devUuid = intent.getStringExtra(mContext.getString(R.string.devUuid));
        userId = SharedPreferenceUtil.getSharedPreference(mContext,mContext.getString(R.string.userId));
        myDevUuid = DeviceUtil.getDevicesUUID(mContext);
        alert = AlertDialogService.alert(mContext);
        mProgDlg = ProgressService.progress(mContext);

        swipeStateList = new boolean[0];

        foldrBasVO.setUserId(SharedPreferenceUtil.getSharedPreference(context, getString(R.string.userId)));
        if(devUuid != null) {
            foldrBasVO.setDevUuid(devUuid);
        }

        findViewById(R.id.topBack).setOnClickListener(closeActivity); // x버튼
        mListView = (SwipeMenuListView) findViewById(sWlistView); // swipeMenu
        searchKeyword = (EditText) findViewById(R.id.searchKeyword); // 검색어
        totalLine = (LinearLayout)findViewById(R.id.totalLine); // total 줄
        searchTotal = (TextView)findViewById(R.id.searchTotal); // 검색 total
        isChecked = (CheckBox)findViewById(R.id.checkBox); // 사내공유 checkbox

        isChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (buttonView.getId() == R.id.checkBox) {
                    if (isChecked) {
                        fileShar = "Y";
                    } else {
                        fileShar = "N";
                    }
                }
            }
        });

        mAdapter = new AppAdapter();
        mListView.setAdapter(mAdapter); // swipeMenuListView에 어댑터 연결

        findViewById(R.id.searchBtn).setOnClickListener(searchClick);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                SwipeMenuItem detailItem = new SwipeMenuItem(mContext);
                SwipeMenuItem downloadItem = new SwipeMenuItem(mContext);
                SwipeMenuItem gigaNasItem = new SwipeMenuItem(mContext);
                SwipeMenuItem appPlayItem = new SwipeMenuItem(mContext);
                SwipeMenuItem deleteItem = new SwipeMenuItem(mContext);
                SwipeMenuItem blankItem = new SwipeMenuItem(mContext);


                switch (menu.getViewType()) {
                    case 0:
                        break;
                    case 1: //접속 기기

                        detailItem = new SwipeMenuItem(mContext);
                        detailItem.setBackground(R.color.baseColor);
                        detailItem.setWidth(dp2px(80));
                        detailItem.setIcon(R.drawable.ico_18dp_contextmenu_info);
                        detailItem.setTitle("속성보기");
                        detailItem.setTitleSize(12);
                        detailItem.setTitleColor(R.color.darkGray);

                        appPlayItem = new SwipeMenuItem(mContext);
                        appPlayItem.setBackground(R.color.baseColor);
                        appPlayItem.setWidth(dp2px(80));
                        appPlayItem.setIcon(R.drawable.ico_18dp_contextmenu_app);
                        appPlayItem.setTitle("앱 실행");
                        appPlayItem.setTitleSize(12);
                        appPlayItem.setTitleColor(R.color.darkGray);

                        gigaNasItem = new SwipeMenuItem(mContext);
                        gigaNasItem.setBackground(R.color.baseColor);
                        gigaNasItem.setWidth(dp2px(80));
                        gigaNasItem.setIcon(R.drawable.ico_18dp_contextmenu_send);
                        gigaNasItem.setTitle("GiGA NAS로\n 보내기");
                        gigaNasItem.setTitleSize(10);
                        gigaNasItem.setTitleColor(R.color.darkGray);

                        deleteItem = new SwipeMenuItem(mContext);
                        deleteItem.setBackground(R.color.baseColor);
                        deleteItem.setWidth(dp2px(80));
                        deleteItem.setIcon(R.drawable.ico_18dp_contextmenu_del);
                        deleteItem.setTitle("삭제");
                        deleteItem.setTitleSize(12);
                        deleteItem.setTitleColor(R.color.darkGray);

                        menu.addMenuItem(detailItem);
                        menu.addMenuItem(appPlayItem);
                        menu.addMenuItem(gigaNasItem);
                        menu.addMenuItem(deleteItem);

                        break;
                    case 2: //접속기기 외 PC,모바일

                        detailItem = new SwipeMenuItem(mContext);
                        detailItem.setBackground(R.color.baseColor);
                        detailItem.setWidth(dp2px(80));
                        detailItem.setIcon(R.drawable.ico_18dp_contextmenu_info);
                        detailItem.setTitle("속성보기");
                        detailItem.setTitleSize(12);
                        detailItem.setTitleColor(R.color.darkGray);

                        downloadItem = new SwipeMenuItem(mContext);
                        downloadItem.setBackground(R.color.baseColor);
                        downloadItem.setWidth(dp2px(80));
                        downloadItem.setIcon(R.drawable.ico_18dp_contextmenu_dwld);
                        downloadItem.setTitle("다운로드");
                        downloadItem.setTitleSize(12);
                        downloadItem.setTitleColor(R.color.darkGray);

                        gigaNasItem = new SwipeMenuItem(mContext);
                        gigaNasItem.setBackground(R.color.baseColor);
                        gigaNasItem.setWidth(dp2px(80));
                        gigaNasItem.setIcon(R.drawable.ico_18dp_contextmenu_send);
                        gigaNasItem.setTitle("GiGA NAS로\n 보내기");
                        gigaNasItem.setTitleSize(10);
                        gigaNasItem.setTitleColor(R.color.darkGray);

                        blankItem = new SwipeMenuItem(mContext);
                        blankItem.setBackground(R.color.baseColor);
                        blankItem.setWidth(dp2px(75));

                        menu.addMenuItem(detailItem);
                        menu.addMenuItem(downloadItem);
                        menu.addMenuItem(gigaNasItem);
                        menu.addMenuItem(blankItem);

                        break;

                    case 3: //NAS

                        detailItem = new SwipeMenuItem(mContext);
                        detailItem.setBackground(R.color.baseColor);
                        detailItem.setWidth(dp2px(80));
                        detailItem.setIcon(R.drawable.ico_18dp_contextmenu_info);
                        detailItem.setTitle("속성보기");
                        detailItem.setTitleSize(12);
                        detailItem.setTitleColor(R.color.darkGray);

                        downloadItem = new SwipeMenuItem(mContext);
                        downloadItem.setBackground(R.color.baseColor);
                        downloadItem.setWidth(dp2px(80));
                        downloadItem.setIcon(R.drawable.ico_18dp_contextmenu_dwld);
                        downloadItem.setTitle("다운로드");
                        downloadItem.setTitleSize(12);
                        downloadItem.setTitleColor(R.color.darkGray);

                        gigaNasItem = new SwipeMenuItem(mContext);
                        gigaNasItem.setBackground(R.color.baseColor);
                        gigaNasItem.setWidth(dp2px(80));
                        gigaNasItem.setIcon(R.drawable.ico_18dp_contextmenu_send);
                        gigaNasItem.setTitle("GiGA NAS로\n 보내기");
                        gigaNasItem.setTitleSize(10);
                        gigaNasItem.setTitleColor(R.color.darkGray);

                        deleteItem = new SwipeMenuItem(mContext);
                        deleteItem.setBackground(R.color.baseColor);
                        deleteItem.setWidth(dp2px(75));
                        deleteItem.setIcon(R.drawable.ico_18dp_contextmenu_del);
                        deleteItem.setTitle("삭제");
                        deleteItem.setTitleSize(12);
                        deleteItem.setTitleColor(R.color.darkGray);

                        menu.addMenuItem(detailItem);
                        menu.addMenuItem(downloadItem);
                        menu.addMenuItem(gigaNasItem);
                        menu.addMenuItem(deleteItem);

                        break;
                    case 4:
                        detailItem = new SwipeMenuItem(mContext);
                        detailItem.setBackground(R.color.baseColor);
                        detailItem.setWidth(dp2px(80));
                        detailItem.setIcon(R.drawable.ico_18dp_contextmenu_info);
                        detailItem.setTitle("속성보기");
                        detailItem.setTitleSize(12);
                        detailItem.setTitleColor(R.color.darkGray);

                        blankItem.setBackground(R.color.baseColor);
                        blankItem.setWidth(dp2px(235));

                        menu.addMenuItem(detailItem);
                        menu.addMenuItem(blankItem);
                        break;
                }
            }
        };
        // 생성한 스와이프메뉴 set
        mListView.setMenuCreator(creator);

        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, final int index) {
                mIndex = position;
                item = mListData.get(position);
                devUuid = item.get("devUuid");
                String command = "search";

                View view = mAdapter.getViewByPosition(position,mListView);
                ((ImageView)view.findViewById(contextIcon)).setImageResource(R.drawable.ico_36dp_context_open);
                try {
                    if (index != -1) {
                        switch (menu.getViewType()) {

                            case 0: // 폴더
                                break;
                            case 1: // 접속기기 파일
                                switch (index) {
                                    case 0: // 파일속성
                                        intentFileAttrViewActivity(item);
                                        break;
                                    case 1: // 앱 실행
                                        FileViewService.viewFile(mContext, item.get("foldrWholePathNm"), item.get("fileNm"));
                                        break;
                                    case 2: //GIGA NAS로 내보내기
                                        ((DrawerLayoutViewActivity) context).intentToActivity(item, item.get("osCd"), devUuid, command);
                                        break;
                                    case 3: //파일 삭제
                                        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                                        dialog.setTitle("해당 파일을 삭제 하시겠습니까?");
                                        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                mProgDlg.setMessage("파일 삭제중입니다...");
                                                mProgDlg.show();
                                                FileUtil.removeFile(item.get("foldrWholePathNm"), item.get("fileNm"));
                                                FileService.syncFoldrInfo();
                                                mProgDlg.dismiss();
                                                alert.setMessage("파일 삭제가 완료 되었습니다.");
                                                alert.show();
                                                getFoldrFileList();
                                            }
                                        });
                                        // Cancel 버튼 이벤트
                                        dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                                        dialog.show();
                                        break;
                                }
                                break;
                            case 2: // 접속기기 외 PC 파일
                                switch (index) {
                                    case 0: // 파일속성
                                        intentFileAttrViewActivity(item);
                                        break;
                                    case 1: // 다운로드
                                        if (item.get("nasSynchYn").equals("Y")) {
                                            new FileDownloadThread(mContext).execute(item.get("foldrWholePathNm"), item.get("fileNm"), item.get("devUuid"), "N"); // params = 폴더명,파일이름,디바이스Uuid,app실행여부
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
                                            comndQueueVO.setFromDevUuid(devUuid);
                                            comndQueueVO.setToFoldr("/Mobile");
                                            comndQueueVO.setToOsCd("A");
                                            comndQueueVO.setToDevUuid(myDevUuid);
                                            comndQueueVO.setComndOsCd("A");
                                            comndQueueVO.setComndDevUuid(myDevUuid);

                                            FileService.fileDownloadWebservice(comndQueueVO, mContext, "N");
                                            break;
                                        }
                                    case 2: // GigaNas로 내보내기
                                        ((DrawerLayoutViewActivity) context).intentToActivity(item, item.get("osCd"), devUuid, command);
                                        break;
                                }

                                break;
                            case 3: // NAS 파일
                                switch (index) {
                                    case 0: // 파일속성
                                        intentFileAttrViewActivity(item);
                                        break;
                                    case 1: // 다운로드
                                        new FileDownloadThread(mContext).execute(item.get("foldrWholePathNm"), item.get("fileNm"), "", "N"); // params = 폴더명,파일이름,디바이스Uuid,app실행여부
                                        break;
                                    case 2: //GIGA NAS로 내보내기
                                        ((DrawerLayoutViewActivity) context).intentToActivity(item, item.get("osCd"), devUuid, command);
                                        break;
                                    case 3:
                                        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                                        dialog.setTitle("해당 파일을 삭제 하시겠습니까?");
                                        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                mProgDlg.setMessage("파일 삭제중입니다...");
                                                mProgDlg.show();
                                                FileBasVO fileBasVO = new FileBasVO();
                                                fileBasVO.setUserId(userId);
                                                fileBasVO.setDevUuid(myDevUuid);
                                                fileBasVO.setFoldrWholePathNm(item.get("foldrWholePathNm"));
                                                fileBasVO.setFileNm(item.get("fileNm"));
                                                Object fileIdObj = item.get("fileId");
                                                fileBasVO.setFileId(fileIdObj.toString());
                                                FileService.nasFileDel(fileBasVO, mContext);
                                                FileService.syncFoldrInfo();
                                                mProgDlg.dismiss();
                                                getFoldrFileList();
                                            }
                                        });
                                        dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                                        dialog.show();
                                        break;
                                }
                                break;
                            case 4: // 사내공유 검색
                                intentFileAttrViewActivity(item);
                                break;

                        }
                    }
                    return false;
                }catch (Exception e){

                }
                return false;
            }
        });

        //키보드 완료클릭 이벤트
        searchKeyword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                AlertDialog.Builder alert = AlertDialogService.alert(mContext);
                String check = spaceCheck(searchKeyword.getText().toString());
                if(searchKeyword.getText() == null || searchKeyword.getText().toString().length() < 2) {
                    alert.setMessage("2자 이상의 검색어를 입력해주세요.");
                    alert.show();
                    return false;
                }else if(check.equals("N")) {
                    alert.setMessage("검색어에 공백을 제거해 주세요.");
                    alert.show();
                }else{
                    foldrBasVO.setSearchKeyword(searchKeyword.getText().toString());
                    if(fileShar.equals("Y")){
                        getFileSharList();
                    }else if(fileShar.equals("N") || fileShar == null){
                        getFoldrFileList();
                    }
                }
                searchKeyword.clearFocus();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchKeyword.getWindowToken(), 0);
                return false;
            }
        });

        // contextmenu 열리고 닫힐 때
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

        // contextmenu 눌렀을떄!~!!!!!!!!!!!!!!!!
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
    }

    public void intentFileAttrViewActivity(Map<String, String> item){
        Intent intent = new Intent(mContext, FileAttrViewActivity.class);

        Object fileId = (Object) item.get("fileId");

        intent.putExtra("fileId", fileId.toString());
        intent.putExtra("foldrWholePathNm", item.get("foldrWholePathNm"));

        mContext.startActivity(intent);
    }

    Button.OnClickListener searchClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder alert = AlertDialogService.alert(mContext);
            String check = spaceCheck(searchKeyword.getText().toString());
            if(searchKeyword.getText() == null || searchKeyword.getText().toString().length() < 2) {
                alert.setMessage("2자 이상의 검색어를 입력해주세요.");
                alert.show();
            }else if(check.equals("N")) {
                alert.setMessage("검색어에 공백을 제거해 주세요.");
                alert.show();
            }else{
                foldrBasVO.setSearchKeyword(searchKeyword.getText().toString());
                if(fileShar.equals("Y")){
                    getFileSharList();
                }else if(fileShar.equals("N") || fileShar == null){
                    getFoldrFileList();
                }
            }
            searchKeyword.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchKeyword.getWindowToken(), 0);
        }
    };


    Button.OnClickListener closeActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private void getFoldrFileList() { // 폴더 list

        Call<JsonObject> listFoldrCall = RestServiceImpl.getInstance(null).listSearchFoldrFile(foldrBasVO);
        listFoldrCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response) {
                if (response.isSuccess()) {
                    Gson gson = new Gson();
                    int statusCode = gson.fromJson(response.body().get("statusCode"), Integer.class);
                    String message = new ResponseFailCode().responseFail(statusCode);
                    AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                    if(statusCode == 100){
                        List<Map<String, String>> tempData = new ArrayList<>();
                        List<Map<String, String>> fileData = new ArrayList<>();
                        tempData = gson.fromJson(response.body().get("listData"), List.class);
                        for(int i=0; i<tempData.size(); i++){
                            if(tempData.get(i).get("foldrYn").equals("N")){
                                fileData.add(tempData.get(i));
                            }
                        }
                        if(fileData != null) {
                            mListData = fileData;
                            searchTotal.setText(String.valueOf(mAdapter.getCount()));
                            mAdapter.notifyDataSetChanged();
                            swipeStateList = new boolean[mListData.size()];
                        } else {
                            searchTotal.setText("0");
                            swipeStateList = new boolean[0];
                        }
                    }else if(statusCode == 400){
                        alert.setMessage(message);
                        alert.show();
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();     //닫기
                                Intent intent = new Intent(mContext, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                DrawerLayoutViewActivity.activity.finish();
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
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
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
                alert.show();
            }
        });
    }

    private void getFileSharList() { // 폴더 list

        Call<JsonObject> FileSharListCall = RestServiceImpl.getInstance(null).FileSharList(foldrBasVO);
        FileSharListCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response) {
                if (response.isSuccess()) {
                    Gson gson = new Gson();
                    int statusCode = gson.fromJson(response.body().get("statusCode"), Integer.class);
                    String message = new ResponseFailCode().responseFail(statusCode);
                    AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                    if(statusCode == 100){
                        List<Map<String, String>> tempData = new ArrayList<>();
                        tempData = gson.fromJson(response.body().get("listData"), List.class);

                        if(tempData != null) {
                            mListData = tempData;
                            searchTotal.setText(String.valueOf(mAdapter.getCount()));
                            mAdapter.notifyDataSetChanged();
                            swipeStateList = new boolean[mListData.size()];
                        } else {
                            searchTotal.setText("0");
                            swipeStateList = new boolean[0];
                        }
                    }else if(statusCode == 400){
                        alert.setMessage(message);
                        alert.show();
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();     //닫기
                                Intent intent = new Intent(mContext, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                DrawerLayoutViewActivity.activity.finish();
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
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
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
                alert.show();
            }
        });
    }


    class AppAdapter extends BaseAdapter {

        class ViewHolder {

            ImageView iv_icon; //폴더 및 파일 아이콘
            TextView tv_name; // 폴더 및 파일 명
            TextView foldrFileId; // 폴더 및 파일 아이디
            TextView cretDate; // 생성일자
            ImageView contextIcon; // 컨텍스트메뉴 아이콘
            TextView devNm; // 해당기기

            public ViewHolder(View view) {

                iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                tv_name = (TextView) view.findViewById(R.id.tv_name);
                foldrFileId = (TextView) view.findViewById(R.id.foldrFileId);
                cretDate = (TextView) view.findViewById(R.id.item_date);
                contextIcon = (ImageView) view.findViewById(R.id.contextIcon);
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
            return 5;
        }

        @Override
        public int getItemViewType(int position) {

            int returnPos = 0;

            Object fileId = getItem(position).get("fileId");

            if(fileShar.equals("N")){
                if(fileId == "") { // 폴더

                    if (getItem(position).get("foldrNm").equals("..")) {
                        returnPos = 0;
                    }

                }else {
                    if (getItem(position).get("devUuid").equals(SharedPreferenceUtil.getSharedPreference(mContext, "devUuid").toString())) { // 접속기기의 파일
                        returnPos = 1;
                    } else {
                        if (getItem(position).get("osCd").equals("W") || getItem(position).get("osCd").equals("A")) {  //접속기기 외 PC
                            returnPos = 2;
                        }else if (getItem(position).get("osCd").equals("G")) { //GIGANAS
                            returnPos = 3;
                        }
                    }
                }
            }else{
                returnPos = 4;
            }
            return returnPos;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            try {
                if (convertView == null) {
                    convertView = View.inflate(mContext,
                            R.layout.item_list_dir_search, null);
                    new AppAdapter.ViewHolder(convertView);
                }

                AppAdapter.ViewHolder holder = (AppAdapter.ViewHolder) convertView.getTag();
                final Map<String, String> mData = getItem(position);

                holder.contextIcon.setImageResource(R.drawable.ico_36dp_context_open);

                if (mData.get("foldrYn").equals("Y")) { // 폴더
                    holder.iv_icon.setImageResource(R.drawable.ico_24dp_folder); // icon
                    holder.tv_name.setText(mData.get("foldrNm")); // name
                    holder.devNm.setText(mData.get("devNm")); // cret date
                    holder.contextIcon.setVisibility(View.GONE);
                } else { // 파일
                    Object obj = new Object();
                    String etsionNm = mData.get("etsionNm");
                    int resourceInt = FileUtil.getIconByEtsion(etsionNm);
                    holder.iv_icon.setImageResource(resourceInt); // icon
                    holder.tv_name.setText(mData.get("fileNm")); // name

                    obj = (Object) mData.get("fileSize");
                    holder.devNm.setText(mData.get("devNm"));
                    holder.contextIcon.setVisibility(View.VISIBLE);
                }

                holder.cretDate.setVisibility(View.VISIBLE);
                holder.cretDate.setText(mData.get("cretDate"));

                /*if(mData.get("osCd") != null) {
                    if(mData.get("osCd").equals("W")) {
                        holder.dev_icon.setImageResource(R.drawable.ico_18dp_device_pc_off);
                    } else if(mData.get("osCd").equals("A")) {
                        holder.dev_icon.setImageResource(R.drawable.ico_18dp_device_mobile_off);
                    } else {
                        holder.dev_icon.setImageResource(R.drawable.ico_18dp_device_giganas_off);
                    }
                }*/

                convertView.findViewById(R.id.dir_item_area).setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        itemArea = getItem(position);

                        if (itemArea.get("fileId") != null) {
                            if (fileShar.equals("N")) {

                                if (itemArea.get("osCd").equals("G")) {

                                    new FileDownloadThread(mContext).execute(itemArea.get("foldrWholePathNm"), itemArea.get("fileNm"), itemArea.get("devUuid"), "Y"); // params = 폴더명,파일이름,디바이스Uuid,app실행여부

                                } else if (itemArea.get("devUuid").equals(myDevUuid)) {

                                    FileViewService.viewFile(mContext, itemArea.get("foldrWholePathNm"), itemArea.get("fileNm"));

                                } else if (itemArea.get("nasSynchYn").equals("Y")) {

                                    new FileDownloadThread(mContext).execute(itemArea.get("foldrWholePathNm"), itemArea.get("fileNm"), itemArea.get("devUuid"), "Y"); // params = 폴더명,파일이름,디바이스Uuid,app실행여부

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

                                    FileService.fileDownloadWebservice(comndQueueVO, mContext, "Y");
                                    TimerService.timerStart(itemArea.get("fileNm"), mContext);
                                }
                            }
                        }

                        return false;
                    }
                });

                convertView.findViewById(R.id.dir_item_area).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //open
                    }
                });

                return convertView;
            } catch (Exception e) {
                Log.d("////////////에러 : ", "Error");
            }
            return convertView;
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    public String spaceCheck(String searchText) {
        boolean searchCheck = false;

        String check = "N";

        for(int i = 0 ; i < searchText.length() ; i++) {
            if(searchText.charAt(i) == ' '){
                searchCheck = true;
            }

        }

        if(searchCheck == false){
            check = "Y";
        }else{
            return check;
        }

        return check;
    }

}