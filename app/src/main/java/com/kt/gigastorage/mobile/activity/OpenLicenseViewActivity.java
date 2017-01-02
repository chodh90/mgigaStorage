package com.kt.gigastorage.mobile.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by zeroeun on 2016-11-11.
 */

public class OpenLicenseViewActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_license_layout);

        findViewById(R.id.topBack).setOnClickListener(closeActivity);

    }
    Button.OnClickListener closeActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };
}