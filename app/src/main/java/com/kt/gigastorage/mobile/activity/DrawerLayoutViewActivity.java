package com.kt.gigastorage.mobile.activity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.kt.gigastorage.mobile.fragment.BizNoteListFragment;
import com.kt.gigastorage.mobile.fragment.DevListFragment;
import com.kt.gigastorage.mobile.fragment.DirListFragment;
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

public class DrawerLayoutViewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private List<Map<String, String>> mListData = new ArrayList<>();
    private NavigationView navigationView = null;
    private TextView toolbarTitle = null;
    private ImageView toolbarSort;
    private DrawerLayout drawer = null;
    private Toolbar toolbar = null;
    private ImageView search;

    private final long FINSH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    private ListViewAdapter mAdapter = null;
    private ListView mListView = null;

    private DevBasVO devBasVO = new DevBasVO();

    public static Context context;
    public static DrawerLayoutViewActivity activity;

    private int clickPos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_layout);

        context = DrawerLayoutViewActivity.this;
        activity = DrawerLayoutViewActivity.this;

        devBasVO.setUserId(SharedPreferenceUtil.getSharedPreference(context, context.getString(R.string.userId)));

        /*framelayout에 기기 리스트 fragment로*/
        Fragment fragment = new DevListFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.replace(R.id.content_fragment, fragment);
        transaction.addToBackStack(null);

        transaction.commit();

        /* toolbar */
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbarSort = (ImageView)findViewById(R.id.toolbar_sort);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.app_name, R.string.app_name);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        toolbarTitle = (TextView) findViewById(R.id.toobar_title);
        navigationView = (NavigationView) findViewById(R.id.naviView);
        navigationView.getHeaderView(0).setBackgroundColor(getResources().getColor(R.color.naviHeader));

        ImageView setting = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.setting);
        TextView home = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_title);

        mListView = (ListView) navigationView.findViewById(R.id.navList);
        mListView.setBackgroundColor(getResources().getColor(R.color.naviHeader));

        mAdapter = new ListViewAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        search = (ImageView) findViewById(R.id.toolbar_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentsearch();
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentSetting();
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPos = -1;
                getDevListWebservice();
                mAdapter.notifyDataSetChanged();
                changeHome();
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        getDevListWebservice();

        navigationView.setNavigationItemSelectedListener(this);
        mListView.setOnItemClickListener(mItemClickListener);
    }

    public void getDevListWebservice() {
        Call<JsonObject> listDevBasCall = RestServiceImpl.getInstance(null).listOneview(devBasVO);
        listDevBasCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response) {

                if (response.isSuccess()) { // Code 200
                    Gson gson = new Gson();
                    int statusCode = gson.fromJson(response.body().get("statusCode"), Integer.class);
                    String message = new ResponseFailCode().responseFail(statusCode);
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    if(statusCode == 100){
                        mListData = gson.fromJson(response.body().get("listData"), List.class);
                        /* bizNot 하드코딩 */
                        Map<String, String> bizNote = new ArrayMap<String, String>();
                        bizNote.put("userId", mListData.get(0).get("userId"));
                        bizNote.put("userName", mListData.get(0).get("userName"));
                        bizNote.put("devUuid", "");
                        bizNote.put("devNm", "BizNote");
                        bizNote.put("osCd", "B");
                        /* 로그아웃 하드코딩 */
                        Map<String, String> logOut = new ArrayMap<String, String>();
                        logOut.put("userId",mListData.get(0).get("userId"));
                        logOut.put("userName", mListData.get(0).get("userName"));
                        logOut.put("devUuid", "");
                        logOut.put("devNm", "로그아웃");
                        logOut.put("osCd", "L");

                        mListData.add(bizNote);
                        mListData.add(logOut);
                        mAdapter.notifyDataSetChanged();
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
                        activity.finish();
                    }
                });
                alert.show();
            }
        });
    }

    public void setToolbarTitle(String titleNm) {
        toolbarTitle = (TextView) findViewById(R.id.toobar_title);
        toolbarTitle.setText(titleNm);
    }

    public void changeBizFragment(String bizNote,Bundle args) {

        search.setVisibility(View.GONE);
        toolbarSort.setVisibility(View.GONE);
        Fragment fragment = new BizNoteListFragment();
        fragment.setArguments(args);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.replace(R.id.content_fragment, fragment);
        if(bizNote.equals("bizNote")){
            transaction.commit();
        }else{
            transaction.commitAllowingStateLoss();
        }

    }

    public void changeFragment(Bundle args) {

        search.setVisibility(View.VISIBLE);

        Fragment fragment = new DirListFragment();
        fragment.setArguments(args);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.replace(R.id.content_fragment, fragment);

        transaction.commit();
    }

    public void changeHome() {

        search.setVisibility(View.VISIBLE);
        Fragment fragment = new DevListFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.replace(R.id.content_fragment, fragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void intentToActivity(Map<String, String> item,String osCd,String devUuid,String command) {
        Intent intent = new Intent(this, SendNasViewActivity.class);

        Object fileId = (Object) item.get("fileId");
        intent.putExtra("fileId", fileId.toString());
        intent.putExtra("osCd", osCd);
        intent.putExtra("devUuid", devUuid);
        intent.putExtra("command",command);
        intent.putExtra("foldrWholePathNm", item.get("foldrWholePathNm"));
        intent.putExtra("fileNm", item.get("fileNm"));


        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        long tempTime        = System.currentTimeMillis();
        long intervalTime    = tempTime - backPressedTime;
        Toast toast = Toast.makeText(activity, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);

        if ( 0 <= intervalTime && FINSH_INTERVAL_TIME >= intervalTime ) {
            activity.finish();
            toast.cancel();
        }else {
            backPressedTime = tempTime;
            toast.show();
        }
    }

    Menu tempMenu = null;

    public void menuSelect(int position) {
        clickPos = position;
        mAdapter.notifyDataSetChanged();
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Map<String, String> data = (Map<String,String>) mAdapter.getItem(position);

            clickPos = position;
            mAdapter.notifyDataSetChanged();

            if(!data.get("osCd").equals("B") && !data.get("osCd").equals("L")) { // bizNote가 아니면
                setToolbarTitle(data.get("devNm"));
                Bundle args = new Bundle();
                args.putString("devNm", data.get("devNm"));
                args.putString("devUuid", data.get("devUuid"));
                args.putString("osCd", data.get("osCd"));
                changeFragment(args);
            }else if(data.get("osCd").equals("B")) {
                Bundle args = new Bundle();
                setToolbarTitle(data.get("devNm"));
                changeBizFragment("bizNote",args);
            }else{//로그아웃
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle("로그아웃 하시겠습니까?");
                dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SettingViewActivity.logout(context);
                    }
                });
                // Cancel 버튼 이벤트
                dialog.setNegativeButton("취소",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
    };

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        return true;
    }

    public void intentFileAttrViewActivity(Map<String, String> item){
        Intent intent = new Intent(this, FileAttrViewActivity.class);

        Object fileId = (Object) item.get("fileId");

        intent.putExtra("fileId", fileId.toString());
        intent.putExtra("foldrWholePathNm", item.get("foldrWholePathNm"));

        startActivity(intent);
    }

    public void intentNoteFileAttrViewActivity(Map<String, String> item){

        Intent intent = new Intent(this, NoteFileAttrViewActivity.class);

        Object fileId = (Object) item.get("fileId");
        Object ascNoteId1 = (Object) item.get("ascNoteId1");
        Object ascNoteId2 = (Object) item.get("ascNoteId2");

        intent.putExtra("fileId", fileId.toString());
        intent.putExtra("foldrWholePathNm", item.get("foldrWholePathNm"));
        intent.putExtra("ascNoteId1", ascNoteId1.toString());
        intent.putExtra("ascNoteId2", ascNoteId2.toString());

        startActivity(intent);
    }

    public void intentSetting(){
        Intent intent = new Intent(this.getApplicationContext(), SettingViewActivity.class);
        this.startActivity(intent);
    }

    public static void refresh(){
        Intent intent = new Intent(activity, DrawerLayoutViewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish();
    }

    public void intentsearch(){

        Fragment fragment = getFragmentManager().findFragmentById(R.id.content_fragment);

        if(fragment instanceof DirListFragment) {
            fragment = (DirListFragment) getFragmentManager().findFragmentById(R.id.content_fragment);
            ((DirListFragment) fragment).intentsearch();
        } else {
            Intent intent = new Intent(this, FileSearchViewActivity.class);
            startActivity(intent);
        }
    }

    private class ViewHolder {
        public ImageView mIcon;
        public TextView mText;
        public ImageView mStatusIcon;
        public TextView mDevUuid;
    }

    private class ListViewAdapter extends BaseAdapter {

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
            final ViewHolder holder;

            if(convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_list_dev_nav, null);

                holder.mIcon = (ImageView) convertView.findViewById(R.id.dev_icon);
                holder.mText = (TextView) convertView.findViewById(R.id.dev_nm);
                holder.mStatusIcon = (ImageView) convertView.findViewById(R.id.onOffIcon);
                holder.mDevUuid = (TextView) convertView.findViewById(R.id.devUuid);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Map<String, String> mData = mListData.get(position);

            holder.mIcon.setVisibility(View.VISIBLE);
            holder.mStatusIcon.setVisibility(View.GONE);
            if(clickPos != -1) {
                if(position == clickPos) {
                    holder.mStatusIcon.setVisibility(View.VISIBLE);
                }
            }

            if(mData.get("osCd").equals("W")) { // PC

                if(mData.get("onoff").equals("Y")){
                    holder.mIcon.setImageResource(R.drawable.ico_24dp_device_pc);
                    holder.mText.setTextColor(getResources().getColor(R.color.windowBackground));
                }else{
                    holder.mIcon.setImageResource(R.drawable.ico_24dp_device_pc_off);
                    holder.mText.setTextColor(getResources().getColor(R.color.disabledGrayToNavi));
                }
            } else if(mData.get("osCd").equals("A")) { // Android

                //안드로이드폰은 다 ON
                holder.mIcon.setImageResource(R.drawable.ico_24dp_device_mobile);
                holder.mText.setTextColor(getResources().getColor(R.color.windowBackground));

            } else if(mData.get("osCd").equals("G")) { // GiGA NAS
                holder.mIcon.setImageResource(R.drawable.ico_24dp_device_giganas);
                holder.mText.setTextColor(getResources().getColor(R.color.windowBackground));
            } else if(mData.get("osCd").equals("B")){ // bizNote
                holder.mIcon.setImageResource(R.drawable.ico_24dp_biznote_wh);
                holder.mText.setTextColor(getResources().getColor(R.color.windowBackground));
            } else{
                holder.mIcon.setImageResource(R.drawable.ico_24dp_logout);
                holder.mText.setTextColor(getResources().getColor(R.color.disabledGrayToNavi));
                holder.mStatusIcon.setVisibility(View.GONE);
            }

            holder.mText.setTypeface(Typeface.DEFAULT_BOLD);
            holder.mText.setText(mData.get("devNm"));
            holder.mDevUuid.setText(mData.get("devUuid"));

            return convertView;
        }
    }

}
