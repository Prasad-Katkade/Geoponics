package com.example.geoponics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {

    EditText uname, uemail, upass, uphnno, uaddress;
    String isFarmer;
    RadioButton farmer_yes, farmer_no;
    Button reg_btn;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        uname = findViewById(R.id.user_name);
        uemail = findViewById(R.id.user_email);
        upass = findViewById(R.id.user_password);
        uphnno = findViewById(R.id.user_phno);
        uaddress = findViewById(R.id.user_address);
        farmer_yes = findViewById(R.id.farmer_yes);
        farmer_no = findViewById(R.id.farmer_no);
        reg_btn = findViewById(R.id.register);

        firebaseAuth = FirebaseAuth.getInstance();
        final PleaseWaitDialog pleaseWaitDialog = new PleaseWaitDialog(Registration.this);

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(uemail.getText().toString()) && TextUtils.isEmpty(upass.getText().toString()) && TextUtils.isEmpty(uname.getText().toString())) {
                    StyleableToast.makeText(Registration.this, "Please Enter All Credentials !", Toast.LENGTH_SHORT,R.style.errortoast).show();
                } else {

                    if (farmer_yes.isChecked()) {
                        isFarmer = "Yes";
                    } else {
                        isFarmer = "No";
                    }

                    pleaseWaitDialog.startLoadingDialog();
                    firebaseAuth.createUserWithEmailAndPassword(uemail.getText().toString(), upass.getText().toString())
                            .addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        UserModel user = new UserModel(
                                                uname.getText().toString(),
                                                uemail.getText().toString(),
                                                uphnno.getText().toString(),
                                                uaddress.getText().toString(),
                                                isFarmer
                                        );
                                        userID="User_ID_" + firebaseAuth.getUid();
                                        db.collection("Users").document("User_ID_" + firebaseAuth.getUid().toString()).set(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        createWallet();
                                                        StyleableToast.makeText(Registration.this, "Registration Succesful !", Toast.LENGTH_SHORT,R.style.customtoast).show();
                                                        pleaseWaitDialog.dismissDialog();
                                                        startActivity(new Intent(Registration.this, Login.class));
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                StyleableToast.makeText(Registration.this, "Something Went Wrong Please Try Again.. ", Toast.LENGTH_SHORT,R.style.errortoast).show();
                                                pleaseWaitDialog.dismissDialog();
                                            }
                                        });


                                    } else {
                                        StyleableToast.makeText(Registration.this, "Something Went Wrong Please Try Again ", Toast.LENGTH_SHORT,R.style.errortoast).show();
                                        pleaseWaitDialog.dismissDialog();
                                    }


                                }
                            });

                }



            }
        });

    }
    public void createWallet(){
        Map<String,String> wallet=new HashMap<>();
        wallet.put("Points","0");
        db.collection("Users").document(userID).collection("Wallet").document("Wallet_"+userID)
                .set(wallet)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        StyleableToast.makeText(Registration.this, "Creating User Wallet..", Toast.LENGTH_SHORT,R.style.customtoast).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                StyleableToast.makeText(Registration.this, "Error Occurred !", Toast.LENGTH_SHORT,R.style.errortoast).show();
            }
        });
    }
}