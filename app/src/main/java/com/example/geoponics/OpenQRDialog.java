package com.example.geoponics;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class OpenQRDialog {

    Activity activity;
    AlertDialog alertDialog;

    public OpenQRDialog(Activity activity) {
        this.activity = activity;
    }

    void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.qr_code_dialog,null));
        builder.setCancelable(true);
        alertDialog = builder.create();
        alertDialog.show();
    }

    void dismissDialog(){
        alertDialog.dismiss();
    }
}
