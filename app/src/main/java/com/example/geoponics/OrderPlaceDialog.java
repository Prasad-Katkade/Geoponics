package com.example.geoponics;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class OrderPlaceDialog {

    Activity activity;
    AlertDialog alertDialog;

    public OrderPlaceDialog(Activity activity) {
        this.activity = activity;
    }

    void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.orderplaceddialog,null));
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }

    void dismissDialog(){
        alertDialog.dismiss();
    }
}
