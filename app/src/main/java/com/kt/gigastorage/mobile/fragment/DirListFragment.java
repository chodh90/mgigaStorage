package com.kt.gigastorage.mobile.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
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
import com.kt.gigastorage.mobile.activity.FileSearchViewActivity;
import com.kt.gigastorage.mobile.activity.MainActivity;
import com.kt.gigastorage.mobile.activity.R;
import com.kt.gigastorage.mobile.service.AlertDialogService;
import com.kt.gigastorage.mobile.service.FileDownloadThread;
import com.kt.gigastorage.mobile.service.FileService;
import com.kt.gigastorage.mobile.service.FileViewService;
import com.kt.gigastorage.mobile.service.KbConverter;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kt.gigastorage.mobile.activity.R.id.center_vertical;
import static com.kt.gigastorage.mobile.activity.R.id.contextIcon;
import static com.kt.gigastorage.mobile.activity.R.id.dir_item_area;
import static com.kt.gigastorage.mobile.activity.R.id.item_area;

/**
 * Created by zeroeun on 2016-10-14.
 */

public class DirListFragment extends Fragment {

    private List<Map<String, String>> mListData = new ArrayList<>();

    private AppAdapter mAdapter;
    private SwipeMenuListView mListView;
    private ImageView toolbarSort;

    private FoldrBasVO foldrBasVO = new FoldrBasVO();
    private FileBasVO fileBasVO = new FileBasVO();
    private TextView dirNavi = null;
    private TextView dirUpNavi;
    private FrameLayout frameLayout;

    public static Context context;
    public static Activity activity;
    private String devNm = ""; // 해당기기명
    private String devUuid = ""; // 해당기기uuid
    private String osCd = ""; // 해당기기 os

    private String myDevUuid; //내 기기uuid
    private String userId;
    private TextView toolbarTitle;


    private ArrayList<String> rootFolders = new ArrayList<>();
    private ArrayList<String> rootFolderNms = new ArrayList<>();
    private ProgressDialog mProgDlg;
    private boolean[] swipeStateList;
    private AlertDialog.Builder alert;
    private Map<String, String> item;
    private Map<String, String> itemArea;
    private SwipeMenu swipeMenu;
    private int mIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(getArguments() != null) {
            context = DrawerLayoutViewActivity.context;
            activity = DrawerLayoutViewActivity.activity;
            userId = SharedPreferenceUtil.getSharedPreference(context,context.getString(R.string.userId));
            myDevUuid = DeviceUtil.getDevicesUUID(context);
            mProgDlg = ProgressService.progress(context);
            alert = AlertDialogService.alert(context);
            devNm = getArguments().getString("devNm");
            devUuid = getArguments().getString(context.getString(R.string.devUuid));
            osCd = getArguments().getString("osCd");
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.content_swipe_listview, container, false);

        setHasOptionsMenu(true);
        swipeStateList = new boolean[0];

        mListView = (SwipeMenuListView) view.findViewById(R.id.sWlistView);
        dirNavi = (TextView) view.findViewById(R.id.dirNavi);
        dirUpNavi = (TextView) view.findViewById(R.id.dirUpNavi);
        dirNavi.setText(" > " + devNm);
        rootFolderNms.add(dirNavi.getText().toString());

        dirUpNavi.setVisibility(View.GONE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        float dp = context.getResources().getDisplayMetrics().density;
        int leftGoneDp = (int)(26 * dp);
        params.leftMargin = leftGoneDp;
        dirNavi.setLayoutParams(params);


        mAdapter = new AppAdapter();
        mListView.setAdapter(mAdapter); // swipeMenuListView에 어댑터 연결
        foldrBasVO.setUserId(SharedPreferenceUtil.getSharedPreference(getActivity(),"userId"));
        foldrBasVO.setDevUuid(devUuid);
        fileBasVO.setUserId(SharedPreferenceUtil.getSharedPreference(getActivity(),"userId"));
        fileBasVO.setDevUuid(devUuid);
        getFoldrListWebservice();

        frameLayout = (FrameLayout)(DrawerLayoutViewActivity.activity.findViewById(R.id.content_fragment));
        final Toolbar toolbar = (Toolbar) ((DrawerLayoutViewActivity)DrawerLayoutViewActivity.context).findViewById(R.id.toolbar);
        toolbarSort = (ImageView) toolbar.findViewById(R.id.toolbar_sort);
        toolbarSort.setVisibility(View.VISIBLE);
        toolbarTitle = (TextView) ((DrawerLayoutViewActivity)DrawerLayoutViewActivity.context).findViewById(R.id.toobar_title);

        Button temp = (Button)((DrawerLayoutViewActivity)DrawerLayoutViewActivity.context).findViewById(R.id.btn_temp);
        Context wrapper = new ContextThemeWrapper(DrawerLayoutViewActivity.context, R.style.AppTheme_Popup_menu);

        final PopupMenu popupMenu = new PopupMenu(wrapper, temp, Gravity.CENTER);
        popupMenu.inflate(R.menu.option_menu);

        toolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString("devNm", devNm);
                args.putString("devUuid", devUuid);
                args.putString("osCd", osCd);
                DrawerLayoutViewActivity.activity.changeFragment(args);
            }
        });

        toolbarSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.show();

                if(popupMenu.getDragToOpenListener() instanceof ListPopupWindow.ForwardingListener) {
                    ListPopupWindow.ForwardingListener listener = (ListPopupWindow.ForwardingListener) popupMenu.getDragToOpenListener();

                    Display display = DrawerLayoutViewActivity.activity.getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int width = size.x;
                    int height = size.y;

                    frameLayout.setAlpha(0.6f);
                    toolbar.setAlpha(0.6f);

                    listener.getPopup().setHeight(height/3);
                    listener.getPopup().setWidth(width);
                    listener.getPopup().show();
                }
            }
        });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                frameLayout.setAlpha(1);
                toolbar.setAlpha(1);
            }
        });

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.new_date) {
                    sortAdapter("new");
                } else if (id == R.id.old_date) {
                    sortAdapter("old");
                } else if (id == R.id.asc_name) {
                    sortAdapter("asc");
                } else if (id == R.id.desc_name) {
                    sortAdapter("desc");
                } else if (id == R.id.kind) {
                    sortAdapter("kind");
                }

                item.setChecked(true);

                return false;
            }
        });

        // 스와이프 메뉴 생성
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // 0:접속기기 외 폴더, 1:접속기기 폴더, 2:나스파일, 3:pc,접속기기가아닌android 파일, 4: 접속기기 파일
                SwipeMenuItem detailItem = new SwipeMenuItem(getActivity());
                SwipeMenuItem appPlayItem = new SwipeMenuItem(getActivity());
                SwipeMenuItem downloadItem = new SwipeMenuItem(getActivity());
                SwipeMenuItem gigaNasItem = new SwipeMenuItem(getActivity());
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
                SwipeMenuItem blankItem = new SwipeMenuItem(getActivity());

                switch (menu.getViewType()) {
                    case 0: // 접속기기 외 폴더(context menu x)
                        break;
                    case 1: // 접속기기 폴더(삭제)
                        deleteItem = new SwipeMenuItem(getActivity());
                        deleteItem.setBackground(R.color.baseColor);
                        deleteItem.setWidth(dp2px(80));
                        deleteItem.setIcon(R.drawable.ico_18dp_contextmenu_del);
                        deleteItem.setTitle("삭제");
                        deleteItem.setTitleSize(12);
                        deleteItem.setTitleColor(R.color.darkGray);
                        blankItem = new SwipeMenuItem(getActivity());
                        blankItem.setBackground(R.color.baseColor);
                        blankItem.setWidth(dp2px(235));

                        menu.addMenuItem(deleteItem);
                        menu.addMenuItem(blankItem);
                        break;
                    case 2: // Nas 파일(속성보기, 다운로드, 나스로보내기, 삭제)
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

                        deleteItem = new SwipeMenuItem(getActivity());
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
                    case 3: // pc, 접속기기 외 파일(속성보기, 다운로드, 나스로보내기)
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
                    case 4: // 접속기기 파일(속성보기, 앱 실행, 나스로보내기, 삭제)
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

                        deleteItem = new SwipeMenuItem(getActivity());
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
                swipeMenu = menu;
                mIndex = position;
                String command = "fagMent";
                View view = mAdapter.getViewByPosition(position,mListView);
                ((ImageView)view.findViewById(contextIcon)).setImageResource(R.drawable.ico_36dp_context_open);
                try {
                    if (index != -1) {
                        switch (menu.getViewType()) {
                            case 0: // 접속기기외의 폴더
                                break;
                            case 1: // 접속기기 폴더
                                switch (index) {
                                    case 0: // 폴더삭제
                                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                                        dialog.setTitle("해당 폴더를 삭제 하시겠습니까?");
                                        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                mProgDlg.setMessage("폴더 삭제중입니다...");
                                                mProgDlg.show();
                                                swipeMenu.getMenuItem(0).setBackground(R.drawable.ico_18dp_contextmenu_del_r);
                                                FileUtil.removeFoldr(item.get("foldrWholePathNm"));
                                                FileService.syncFoldrInfo();
                                                mListData.remove(mIndex);
                                                mProgDlg.dismiss();
                                                alert.setMessage("폴더 삭제가 완료 되었습니다.");
                                                alert.show();
                                                mAdapter.notifyDataSetChanged();
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
                            case 2: // nas 파일
                                switch (index) {
                                    case 0: // 속성보기
                                        DrawerLayoutViewActivity dlv = (DrawerLayoutViewActivity) getActivity();
                                        dlv.intentFileAttrViewActivity(item);
                                        break;
                                    case 1: // 다운로드

                                        new FileDownloadThread(context).execute(item.get("foldrWholePathNm"), item.get("fileNm"), "", "N"); // params = 폴더명,파일이름,디바이스Uuid,app실행여부

                                        break;
                                    case 2: // nas로 보내기
                                        ((DrawerLayoutViewActivity) getActivity()).intentToActivity(item, osCd, devUuid, command);
                                        break;
                                    case 3: // 삭제
                                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
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
                                                FileService.nasFileDel(fileBasVO, context);
                                                FileService.syncFoldrInfo();
                                                mProgDlg.dismiss();
                                                mListData.remove(mIndex);
                                                mAdapter.notifyDataSetChanged();
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
                            case 3: // pc, 접속기기외의 파일
                                switch (index) {
                                    case 0: // 속성보기
                                        DrawerLayoutViewActivity dlv = (DrawerLayoutViewActivity) getActivity();
                                        dlv.intentFileAttrViewActivity(item);
                                        break;
                                    case 1: // 다운로드

                                        if (item.get("nasSynchYn").equals("Y")) {
                                            new FileDownloadThread(context).execute(item.get("foldrWholePathNm"), item.get("fileNm"), devUuid, "N"); // params = 폴더명,파일이름,디바이스Uuid,app실행여부
                                            break;
                                        } else {
                                            ComndQueueVO comndQueueVO = new ComndQueueVO();
                                            if (osCd.equals("A")) { // osCd = W, A 분기 처리
                                                comndQueueVO.setComnd("RALA");
                                                comndQueueVO.setFromOsCd("A");
                                            }
                                            if (osCd.equals("W")) {
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

                                            FileService.fileDownloadWebservice(comndQueueVO, context, "N");

                                            break;
                                        }
                                    case 2: // nas로 보내기
                                        ((DrawerLayoutViewActivity) getActivity()).intentToActivity(item, osCd, devUuid, command);
                                        break;
                                }
                                break;
                            case 4: // 접속기기 파일
                                switch (index) {
                                    case 0: // 속성보기
                                        DrawerLayoutViewActivity dlv = (DrawerLayoutViewActivity) getActivity();
                                        dlv.intentFileAttrViewActivity(item);
                                        break;
                                    case 1: // 앱실행
                                        FileViewService.viewFile(context, item.get("foldrWholePathNm"), item.get("fileNm"));
                                        break;
                                    case 2: // nas로보내기
                                        ((DrawerLayoutViewActivity) getActivity()).intentToActivity(item, osCd, devUuid, command);
                                        break;
                                    case 3: // 삭제
                                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                                        dialog.setTitle("해당 파일을 삭제 하시겠습니까?");
                                        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                mProgDlg.setMessage("파일 삭제중입니다...");
                                                mProgDlg.show();
                                                FileUtil.removeFile(item.get("foldrWholePathNm"), item.get("fileNm"));
                                                FileService.syncFoldrInfo();
                                                mListData.remove(mIndex);
                                                mProgDlg.dismiss();
                                                alert.setMessage("파일 삭제가 완료 되었습니다.");
                                                alert.show();
                                                mAdapter.notifyDataSetChanged();
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
                        }
                    }

                    return false;
                }catch (Exception e){

                }
                return false;
            }
        });

        // contextmenu 열리고 닫힐 때!!!!!!!!!!아오!!!
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

        return view;
    }

    private List<Map<String, String>> tempListData = new ArrayList<>();

    private void getFoldrListWebservice() { // 폴더 list
        Call<JsonObject> listFoldrCall = RestServiceImpl.getInstance(null).listFoldr(foldrBasVO);
        listFoldrCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response) {
                if (response.isSuccess()) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    float dp = context.getResources().getDisplayMetrics().density;
                    int leftGoneDp = (int)(26 * dp);
                    int leftDp = (int)(0 * dp);
                    Gson gson = new Gson();
                    int statusCode = gson.fromJson(response.body().get("statusCode"), Integer.class);
                    String message = new ResponseFailCode().responseFail(statusCode);
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    if(statusCode == 100) {
                        tempListData = gson.fromJson(response.body().get("listData"), List.class);
                        mListData = new ArrayList<Map<String, String>>();
                        if (tempListData.size() > 0) { // 폴더리스트가 있을 때!
                            if (tempListData.get(0).get("upFoldrId") == null) { //root인경우 다시 웹서비스호출
                                if (tempListData.get(0).get("osCd").equals("W")) {
                                    dirUpNavi.setVisibility(View.GONE);
                                    params.leftMargin = leftGoneDp;
                                    dirNavi.setText("> " + devNm);
                                    dirNavi.setLayoutParams(params);
                                    mListData.addAll(tempListData);
                                    getFileListWebservice();
                                } else {
                                    Object foldrIdObj = tempListData.get(0).get("foldrId");
                                    foldrBasVO.setFoldrId(foldrIdObj.toString());
                                    fileBasVO.setFoldrId(foldrBasVO.getFoldrId());
                                    getFoldrListWebservice();
                                }
                            } else { //root가 아닌경우
                                if (rootFolders.size() > 0) {
                                    Map<String, String> rootMap = new ArrayMap<String, String>();
                                    rootMap.put("foldrNm", "..");
                                    mListData.add(rootMap);
                                    dirUpNavi.setVisibility(View.VISIBLE);
                                    params.leftMargin = leftDp;
                                    dirNavi.setLayoutParams(params);
                                }else{
                                    dirUpNavi.setVisibility(View.GONE);
                                    dirNavi.setText("> " + devNm);
                                    params.leftMargin = leftGoneDp;
                                    dirNavi.setLayoutParams(params);
                                }

                                mListData.addAll(tempListData);
                                getFileListWebservice();
                            }
                        } else { // 폴더리스트가 없음
                            if (rootFolders.size() > 0) {
                                dirUpNavi.setVisibility(View.VISIBLE);
                                params.leftMargin = leftDp;
                                dirNavi.setLayoutParams(params);
                                Map<String, String> rootMap = new ArrayMap<String, String>();
                                rootMap.put("foldrNm", "..");
                                mListData.add(rootMap);
                            }

                            mListData.addAll(tempListData);
                            getFileListWebservice();
                        }

                    }else if(statusCode == 400) {
                        alert.setMessage(message);
                        alert.show();
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();     //닫기
                                Intent intent = new Intent(activity, MainActivity.class);
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
                    }
                });
                alert.show();
            }
        });
    }

    private void getFileListWebservice() { // 파일 list

        Call<JsonObject> lisfFileCall = RestServiceImpl.getInstance(null).listFile(fileBasVO);
        lisfFileCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response) {
                if(response.isSuccess()) {
                    Gson gson = new Gson();
                    int statusCode = gson.fromJson(response.body().get("statusCode"), Integer.class);
                    String message = new ResponseFailCode().responseFail(statusCode);
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    if(statusCode == 100){
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
                        DrawerLayoutViewActivity.activity.finish();
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
            TextView upFoldrId; // 해당 폴더 및 파일의 상단폴더아이디
            TextView cretDate; // 생성일자
            TextView fileSize; // 파일사이즈
            ImageView contextIcon; // 컨텍스트메뉴 아이콘

            public ViewHolder(View view) {

                iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                tv_name = (TextView) view.findViewById(R.id.tv_name);
                foldrFileId = (TextView) view.findViewById(R.id.foldrFileId);
                upFoldrId = (TextView) view.findViewById(R.id.upFoldrId) ;
                cretDate = (TextView) view.findViewById(R.id.item_date);
                fileSize = (TextView) view.findViewById(R.id.item_size);
                contextIcon = (ImageView) view.findViewById(R.id.contextIcon);

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

            if (fileId == null) { // 폴더
                if (devUuid.equals(SharedPreferenceUtil.getSharedPreference(getActivity(), "devUuid").toString())) { // 접속기기와 들어온 기기가 같을 때
                    returnPos = 1;
                }

                if (getItem(position).get("foldrNm").equals("..")) {
                    returnPos = 0;
                }
            } else { //파일
                if (osCd.equals("G")) { // NAS
                    returnPos = 2;
                } else if (osCd.equals("W")) { // Windows
                    returnPos = 3;
                } else if (osCd.equals("A")) { // Android
                    returnPos = 3;
                    if (devUuid.equals(SharedPreferenceUtil.getSharedPreference(getActivity(), "devUuid").toString())) {
                        returnPos = 4;
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
                            R.layout.item_list_dir, null);
                    new AppAdapter.ViewHolder(convertView);
                }

                final AppAdapter.ViewHolder holder = (AppAdapter.ViewHolder) convertView.getTag();

                final Map<String, String> mData = getItem(position);

                holder.contextIcon.setImageResource(R.drawable.ico_36dp_context_open);

                if (mData.get("fileId") == null) { // 폴더

                    holder.iv_icon.setImageResource(R.drawable.ico_36dp_folder);

                    holder.tv_name.setText(mData.get("foldrNm"));
                    holder.fileSize.setText("");
                    holder.cretDate.setText(mData.get("cretDate"));
                    if (!devUuid.equals(SharedPreferenceUtil.getSharedPreference(getActivity(), "devUuid"))
                            || mData.get("foldrNm").equals("..")) {
                        holder.contextIcon.setVisibility(View.GONE);
                    } else {
                        holder.contextIcon.setVisibility(View.VISIBLE);
                    }

                    Object obj = (Object) mData.get("foldrId");
                    if (obj != null) {
                        holder.foldrFileId.setText(obj.toString());
                    }
                    obj = (Object) mData.get("upFoldrId");
                    if (obj != null) {
                        holder.upFoldrId.setText(obj.toString());
                    }
                } else { // 파일
                    Object obj = new Object();
                    String etsionNm = mData.get("etsionNm");
                    int resourceInt = FileUtil.getIconByEtsion(etsionNm);
                    holder.iv_icon.setImageResource(resourceInt);

                    holder.tv_name.setText(mData.get("fileNm"));
                    obj = (Object) mData.get("fileSize");
                    long value = Long.parseLong(obj.toString());
                    String size = KbConverter.convertBytesToSuitableUnit(value);
                    holder.fileSize.setText(size);
                    holder.cretDate.setText(mData.get("cretDate"));

                    obj = (Object) mData.get("fileId");
                    holder.foldrFileId.setText(obj.toString());
                    obj = (Object) mData.get("foldrId");
                    if (obj != null) {
                        holder.upFoldrId.setText(obj.toString());
                    }
                }

                convertView.findViewById(R.id.dir_item_area).setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        itemArea = getItem(position);

                        if (itemArea.get("fileId") != null) {

                            if (osCd.equals("G")) {

                                new FileDownloadThread(context).execute(itemArea.get("foldrWholePathNm"), itemArea.get("fileNm"), devUuid, "Y"); // params = 폴더명,파일이름,디바이스Uuid,app실행여부

                            } else if (devUuid.equals(myDevUuid)) {
                                FileViewService.viewFile(context, itemArea.get("foldrWholePathNm"), itemArea.get("fileNm"));
                            } else {
                                if (itemArea.get("nasSynchYn").equals("Y")) {
                                    new FileDownloadThread(context).execute(itemArea.get("foldrWholePathNm"), itemArea.get("fileNm"), devUuid, "Y"); // params = 폴더명,파일이름,디바이스Uuid,app실행여부
                                } else {
                                    ComndQueueVO comndQueueVO = new ComndQueueVO();
                                    if (osCd.equals("A")) { // osCd = W, A 분기 처리
                                        comndQueueVO.setComnd("RALA");
                                        comndQueueVO.setFromOsCd("A");
                                    }
                                    if (osCd.equals("W")) {
                                        comndQueueVO.setComnd("RWLA");
                                        comndQueueVO.setFromOsCd("W");
                                    }
                                    comndQueueVO.setFromUserId(userId);
                                    comndQueueVO.setFromFoldr(itemArea.get("foldrWholePathNm"));
                                    comndQueueVO.setFromFileNm(itemArea.get("fileNm"));
                                    Object objFileId = itemArea.get("fileId");
                                    comndQueueVO.setFromFileId(objFileId.toString());
                                    comndQueueVO.setFromDevUuid(devUuid);
                                    comndQueueVO.setToFoldr("/Mobile");
                                    comndQueueVO.setToOsCd("A");
                                    comndQueueVO.setToDevUuid(myDevUuid);
                                    comndQueueVO.setComndOsCd("A");
                                    comndQueueVO.setComndDevUuid(myDevUuid);

                                    FileService.fileDownloadWebservice(comndQueueVO, context, "Y");
                                    TimerService.timerStart(itemArea.get("fileNm"), context);

                                }
                            }
                        }

                        return false;
                    }
                });
                convertView.findViewById(R.id.dir_item_area).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Map<String, String> itemMap = getItem(position);

                        if (itemMap.get("fileId") == null) { // 폴더

                            Object obj = (Object) itemMap.get("foldrId");

                            if (obj == null) { // ..을 누르면
                                foldrBasVO.setFoldrId(rootFolders.get(rootFolders.size() - 1));
                                fileBasVO.setFoldrId(rootFolders.get(rootFolders.size() - 1));
                            } else { //.. 외의 폴더를 누르면
                                foldrBasVO.setFoldrId(obj.toString()); // 해당 폴더id를 vo에 set
                                fileBasVO.setFoldrId(obj.toString()); // 해당 폴더id를 vo에 set
                                obj = (Object) itemMap.get("upFoldrId");
                                if (obj == null) {
                                    rootFolders.add(null); // 해당 폴더의 상위폴더id를 add(PC)
                                    dirUpNavi.setVisibility(View.GONE);
                                } else {
                                    rootFolders.add(obj.toString()); // 해당 폴더의 상위폴더id를 add
                                }
                                rootFolderNms.add(itemMap.get("foldrNm"));
                            }

                            obj = mData.get(("foldrNm"));
                            dirUpNavi.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    foldrBasVO.setFoldrId(rootFolders.get(rootFolders.size() - 1));
                                    fileBasVO.setFoldrId(rootFolders.get(rootFolders.size() - 1));
                                    rootFolders.remove(rootFolders.size() - 1); // 마지막 root foldrId 지움
                                    rootFolderNms.remove(rootFolderNms.size() - 1);
                                    dirNavi.setText(rootFolderNms.get(rootFolders.size()));
                                    getFoldrListWebservice();
                                }
                            });
                            if (obj.equals("..")) {
                                rootFolders.remove(rootFolders.size() - 1); // 마지막 root foldrId 지움
                                rootFolderNms.remove(rootFolderNms.size() - 1);
                                dirNavi.setText(rootFolderNms.get(rootFolders.size()));
                            } else {
                                dirNavi.setText(mData.get(("foldrNm")));
                                float dp = context.getResources().getDisplayMetrics().density;
                                int widthDp = (int) (280 * dp);
                                dirNavi.setWidth(widthDp);
                                dirNavi.setSingleLine(true);
                                dirNavi.setEllipsize(TextUtils.TruncateAt.END);
                            }
                            getFoldrListWebservice();
                        } else { // 파일

                        }
                    }
                });
                return convertView;
            }catch (Exception e){
                Log.d("//////////////에러 :","Error");
            }
            return convertView;
        }

    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    public void sortAdapter(String sortBy) {

        foldrBasVO.setSortBy(sortBy);
        fileBasVO.setSortBy(sortBy);
        getFoldrListWebservice();
    }

    public void intentsearch() {
        Intent intent = new Intent(context, FileSearchViewActivity.class);
        intent.putExtra("devUuid", devUuid);
        startActivity(intent);
    }
}