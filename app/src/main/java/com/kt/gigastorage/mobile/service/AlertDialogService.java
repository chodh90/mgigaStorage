package com.kt.gigastorage.mobile.service;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by araise on 2016-11-04.
 */

public class AlertDialogService {

    public static AlertDialog.Builder alert(Context context){
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
            }
        });
        return alert;
    }

    /*@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context mContext = getActivity() ;
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
            }
        });

        AlertDialog dialog = alert.create();
        return  dialog;
    }*/

}
