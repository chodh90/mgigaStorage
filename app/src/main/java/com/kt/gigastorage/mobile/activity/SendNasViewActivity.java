package com.kt.gigastorage.mobile.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kt.gigastorage.mobile.fragment.FoldrFragment;
import com.kt.gigastorage.mobile.utils.DeviceUtil;
import com.kt.gigastorage.mobile.utils.SharedPreferenceUtil;

import java.util.ArrayList;

/**
 * Created by zeroeun on 2016-10-18.
 */

public class SendNasViewActivity extends Activity {

    public static Context context;
    public static SendNasViewActivity activity;
    public String intentOsCd = "";
    public String intentDevUuid = "";
    public String intentFoldrWholePathNm = "";
    public String intentFileNm = "";
    public String intentFileId = "";
    public String intentCommand = "";
    public String userId;
    public String myDevUuid;

    private ArrayList<String> rootFolderNms = new ArrayList<>();
    private TextView dirNavi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_nas);
        context = SendNasViewActivity.this;
        activity = SendNasViewActivity.this;
        userId = SharedPreferenceUtil.getSharedPreference(context,context.getString(R.string.userId));
        myDevUuid = DeviceUtil.getDevicesUUID(context);
        Intent intent = getIntent();

        intentOsCd = intent.getExtras().getString("osCd");
        intentDevUuid = intent.getExtras().getString(context.getString(R.string.devUuid));
        intentFoldrWholePathNm = intent.getExtras().getString("foldrWholePathNm");
        intentFileNm = intent.getExtras().getString("fileNm");
        intentFileId = intent.getExtras().getString("fileId");
        intentCommand = intent.getExtras().getString("command");

        findViewById(R.id.btn_confirm).setOnClickListener(selecteConfirm);
        findViewById(R.id.topBack).setOnClickListener(closeActivity);
        dirNavi = (TextView)findViewById(R.id.dirNavi);
        rootFolderNms.add(dirNavi.getText().toString());
    }

    public void setNavi(String foldrNm) {

        if(foldrNm == null) { // ..을 눌렀을 때
            dirNavi.setText(rootFolderNms.get(rootFolderNms.size()-1));
            rootFolderNms.remove(rootFolderNms.size()-1);
        } else if(foldrNm.equals("")) { // 더이상 하위폴더가 없을 때 다시 지워줌
            if(rootFolderNms.size() > 1) {
                rootFolderNms.remove(rootFolderNms.size()-1);
                dirNavi.setText(rootFolderNms.get(rootFolderNms.size()-1));
            }
        } else {
            rootFolderNms.add(foldrNm);
            dirNavi.setText(foldrNm);
        }
    }

    Button.OnClickListener selecteConfirm = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            FoldrFragment fragment = (FoldrFragment) getFragmentManager().findFragmentById(R.id.listview_fragment);
            fragment.confirmSelected(intentOsCd,intentDevUuid,intentFoldrWholePathNm,intentFileNm,intentFileId,intentCommand);
        }
    };

    Button.OnClickListener closeActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    public static void offLine(){
        Intent intent = new Intent(activity, DrawerLayoutViewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish();
    }
}
