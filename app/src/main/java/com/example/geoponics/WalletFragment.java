package com.example.geoponics;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.HashMap;
import java.util.Map;


public class WalletFragment extends Fragment {



    public WalletFragment() {
        // Required empty public constructor

    }


    Button openQR;
    OpenQRDialog openQRDialog;
    TextView point;

    FirebaseFirestore db;
    DocumentReference ref;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userID, user;

    private int CAMERA_PERMISSION_CODE=1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        openQR=view.findViewById(R.id.showqr);
        openQRDialog=new OpenQRDialog(getActivity());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = sharedPreferences.edit();
        user = sharedPreferences.getString(getString(R.string.uid), "");
        userID = "User_ID_" + user;

        db=FirebaseFirestore.getInstance();
        ref=db.collection("Users").document(userID).collection("Wallet").document("Wallet_"+userID);
        point=view.findViewById(R.id.walletpoints);
        getpoints();

        openQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
                    startActivity(new Intent(getContext(),ScannerActivity.class));
                }else{
                    requestCamPermission();
                }


            }
        });

        return view;
    }

    public void getpoints(){
        ref.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        point.setText(documentSnapshot.getString("Points").toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                StyleableToast.makeText(getContext(), "Error Fetching Points..", Toast.LENGTH_SHORT,R.style.errortoast).show();
            }
        });
    }
    private void requestCamPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.CAMERA)){
                new AlertDialog.Builder(getContext())
                        .setTitle("Permission Needed")
                        .setMessage("This Permission is require to scan QR code..")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();

        }else{
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getpoints();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==CAMERA_PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                StyleableToast.makeText(getContext(), "Permission Granted..", Toast.LENGTH_SHORT,R.style.customtoast).show();
            }else{
                StyleableToast.makeText(getContext(), "Permission Denied !", Toast.LENGTH_SHORT,R.style.errortoast).show();
            }
        }
    }
}