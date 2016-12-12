package com.kt.gigastorage.mobile.service;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by araise on 2016-11-03.
 */

public class ProgressService {
    public static ProgressDialog mProgDlg;

    public static ProgressDialog progress(Context context){
        mProgDlg = new ProgressDialog(context);
        mProgDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return mProgDlg;
    }

}
