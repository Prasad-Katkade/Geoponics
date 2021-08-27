package com.example.geoponics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.Result;
import com.muddzdev.styleabletoast.StyleableToast;

public class ScannerActivity extends AppCompatActivity {

    CodeScanner codeScanner;
    CodeScannerView scannerView;

    FirebaseFirestore db;
    DocumentReference ref;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userID, user;

    String previouspoints,newpoints;
    int totalpoints=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        scannerView =findViewById(R.id.scanner_view);
        codeScanner=new CodeScanner(this,scannerView);


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ScannerActivity.this);
        editor = sharedPreferences.edit();
        user = sharedPreferences.getString(getString(R.string.uid), "");
        userID = "User_ID_" + user;

        db=FirebaseFirestore.getInstance();
        ref=db.collection("Users").document(userID).collection("Wallet").document("Wallet_"+userID);

        getpoints();

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      if(result.getText().equals("Geoponics-Admin-Success")){
                          StyleableToast.makeText(ScannerActivity.this, "Scanned Successfully ! \n Press back to Check Your Wallet..", Toast.LENGTH_SHORT,R.style.customtoast).show();
                          addPoints();
                      }else{
                          StyleableToast.makeText(ScannerActivity.this, "Please Scan Valid QR Code !", Toast.LENGTH_SHORT,R.style.errortoast).show();
                      }
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        codeScanner.startPreview();
    }

    public void addPoints(){
        totalpoints=Integer.parseInt(previouspoints) + 10;
        ref.update("Points",""+totalpoints);
    }

    public void getpoints(){
        ref.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        previouspoints = documentSnapshot.getString("Points").toString();
                        //Toast.makeText(ScannerActivity.this, "points fetched", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                StyleableToast.makeText(ScannerActivity.this, "Error Fetching Points", Toast.LENGTH_SHORT,R.style.errortoast).show();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        getpoints();
    }
}