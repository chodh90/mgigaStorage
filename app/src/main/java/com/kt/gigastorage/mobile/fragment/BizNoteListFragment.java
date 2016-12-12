package com.kt.gigastorage.mobile.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
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
import com.kt.gigastorage.mobile.service.ResponseFailCode;
import com.kt.gigastorage.mobile.utils.FileUtil;
import com.kt.gigastorage.mobile.utils.SharedPreferenceUtil;
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

    public static Context context;

    private ArrayList<String> rootFolderNms = new ArrayList<>();
    private boolean[] swipeStateList;
    private Map<String, String> item;
    private SwipeMenu swipeMenu;
    private int mIndex;

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


        swipeStateList = new boolean[0];

        mListView = (SwipeMenuListView) view.findViewById(R.id.sWlistView);
        dirNavi = (TextView) view.findViewById(R.id.dirNavi);
        dirNavi.setText(" > BizNote");
        rootFolderNms.add(dirNavi.getText().toString());

        mAdapter = new AppAdapter();
        mListView.setAdapter(mAdapter); // swipeMenuListView에 어댑터 연결

        mNoteBasVO.setUserId(SharedPreferenceUtil.getSharedPreference(getActivity(),"userId"));
        noteBmarkVO.setUserId(SharedPreferenceUtil.getSharedPreference(getActivity(),"userId"));
        if(mNoteListVO.getNoteId() == null){
            getNoteMenuListWebservice();
        }else{
            getNoteListWebservice(mNoteListVO);
        }


        //Toolbar toolbar = (Toolbar) ((DrawerLayoutViewActivity)DrawerLayoutViewActivity.context).findViewById(R.id.toolbar);
        //toolbar.setOverflowIcon(ContextCompat.getDrawable(DrawerLayoutViewActivity.context, R.drawable.ico_24dp_top_align));

        // 스와이프 메뉴 생성
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

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
                    case 1: //파일
                        detailItem = new SwipeMenuItem(getActivity());
                        detailItem.setBackground(R.color.baseColor);
                        detailItem.setWidth(dp2px(80));
                        detailItem.setIcon(R.drawable.ico_18dp_contextmenu_info);
                        detailItem.setTitle("속성보기");
                        detailItem.setTitleSize(12);
                        detailItem.setTitleColor(R.color.darkGray);

                        blankItem = new SwipeMenuItem(getActivity());
                        blankItem.setBackground(R.color.baseColor);
                        blankItem.setWidth(dp2px(235));

                        menu.addMenuItem(detailItem);
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
                View view = mAdapter.getViewByPosition(position,mListView);
                ((ImageView)view.findViewById(contextIcon)).setImageResource(R.drawable.ico_36dp_context_open);
                    switch (menu.getViewType()) {
                        case 0: // 폴더
                            switch (index) {
                                case 0: // 북마크 추가
                                    Object obj = (Object)item.get("noteId");
                                    noteBmarkVO.setNoteId(obj.toString());
                                    mergNoteBmark(noteBmarkVO);
                                    break;
                            }
                            break;
                        case 1: // 파일
                            switch (index) {
                                case 0: // 속성보기
                                    DrawerLayoutViewActivity dlv = (DrawerLayoutViewActivity)getActivity();
                                    dlv.intentNoteFileAttrViewActivity(item);
                                    break;
                            }
                            break;
                        case 4: // 북마크 삭제
                            switch (index) {
                                case 0: // 북마크 삭제
                                    Object obj = (Object)item.get("noteId");
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

        Call<JsonObject> lisfFileCall = RestServiceImpl.getInstance(null).listNote(mNoteListVO);
        lisfFileCall.enqueue(new Callback<JsonObject>() {
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
            return mListData.get(position);
        }

        // 아이템 포지션의 id값 리턴
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 6;
        }

        @Override
        public int getItemViewType(int position) {

            int returnPos = 0;

            if(getItem(position).get("fileId") != null) {
                returnPos = 1;
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

            // 리스트가 길 때, 화면에 보이지 않는 아이템은 convertView가 null인 상태임(고로 여기를 탐)
            if (convertView == null) {
                convertView = View.inflate(getActivity(),
                        R.layout.item_list_dir_search, null);
                new AppAdapter.ViewHolder(convertView);
            }

            final AppAdapter.ViewHolder holder = (AppAdapter.ViewHolder) convertView.getTag();

            final Map<String,String> mData = getItem(position);

            holder.contextIcon.setImageResource(R.drawable.ico_36dp_context_open);

            if(mData.get("fileId") == null) {

                holder.iv_icon.setImageResource(R.drawable.ico_36dp_folder);
                holder.tv_name.setText(mData.get("noteNm"));
                if(mData.get("noteNm").equals("..")){
                    holder.additionArea.setVisibility(View.GONE);
                    holder.contextIcon.setVisibility(View.GONE);
                }else if(mData.get("noteNm").equals("책갈피")){
                    holder.additionArea.setVisibility(View.GONE);
                    holder.contextIcon.setVisibility(View.GONE);
                }else{
                    holder.additionArea.setVisibility(View.VISIBLE);
                    holder.contextIcon.setVisibility(View.VISIBLE);
                }
                Object obj = mData.get("noteId");

                if(obj == null && !mData.get("noteNm").equals("책갈피")) {
                    convertView.findViewById(R.id.dir_item_area).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(bookMarkFlag == false){
                                getNoteMenuListWebservice();
                            }else if(dirNavi.getText().equals("> 책갈피")){
                                getNoteMenuListWebservice();
                            }else{
                                getNoteBmarkListWebservice(noteBmarkVO);
                            }

                        }
                    });
                }else if(mData.get("noteNm").equals("책갈피")){
                    convertView.findViewById(R.id.dir_item_area).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(mData.get("noteNm").equals("..")){
                                getNoteMenuListWebservice();
                            }else{
                                getNoteBmarkListWebservice(noteBmarkVO);
                            }
                        }
                    });
                }else {
                    convertView.findViewById(R.id.dir_item_area).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Map<String,String> itemMap = getItem(position);
                            mNoteListVO.setNoteId(((Object)mData.get("noteId")).toString());
                            getNoteListWebservice(mNoteListVO);
                            dirNavi.setText("> " + mData.get("noteNm"));
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

            return convertView;
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

}