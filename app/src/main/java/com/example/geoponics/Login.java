package com.example.geoponics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.muddzdev.styleabletoast.StyleableToast;

public class Login extends AppCompatActivity {

    TextView createacc, forgetpass;
    EditText UserEmail, UserPass;
    Button submit_btn;


    FirebaseAuth firebaseAuth;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    CheckBox rmcheckbox;

   int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        forgetpass = findViewById(R.id.forgetpass);
        UserEmail = findViewById(R.id.Uemail);
        UserPass = findViewById(R.id.Upass);
        submit_btn = findViewById(R.id.submit);
        createacc = findViewById(R.id.createacc);
        rmcheckbox = findViewById(R.id.rememberme);



        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        checkpreferences();
        if(flag==1){
            startActivity(new Intent(Login.this,Home.class));
        }

        createacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Registration.class));
            }
        });

        final PleaseWaitDialog pleaseWaitDialog = new PleaseWaitDialog(Login.this);


        firebaseAuth = FirebaseAuth.getInstance();
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (rmcheckbox.isChecked()) {
                    editor.putString(getString(R.string.rmbox), "true");
                    editor.commit();
                    editor.putString(getString(R.string.uemail), UserEmail.getText().toString());
                    editor.commit();
                    editor.putString(getString(R.string.upwd), UserPass.getText().toString());
                    editor.commit();
                } else {
                    editor.putString(getString(R.string.rmbox), "false");
                    editor.commit();
                    editor.putString(getString(R.string.uemail), "");
                    editor.commit();
                    editor.putString(getString(R.string.upwd), "");
                    editor.commit();

                }

                pleaseWaitDialog.startLoadingDialog();
                try {
                    firebaseAuth.signInWithEmailAndPassword(UserEmail.getText().toString(), UserPass.getText().toString())
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {
                                        StyleableToast.makeText(Login.this, "Login Succesful", Toast.LENGTH_SHORT, R.style.customtoast).show();
                                        editor.putString(getString(R.string.uid), firebaseAuth.getUid().toString());
                                        editor.commit();
                                        pleaseWaitDialog.dismissDialog();
                                        Intent intent = new Intent(Login.this, Home.class);
                                        startActivity(intent);
                                    } else {
                                        StyleableToast.makeText(Login.this, "Enter Valid Credentials !", Toast.LENGTH_SHORT, R.style.errortoast).show();
                                        pleaseWaitDialog.dismissDialog();
                                    }


                                }
                            });
                } catch (Exception e) {
                    StyleableToast.makeText(Login.this, "Please Enter Credentials ! ", Toast.LENGTH_SHORT, R.style.errortoast).show();
                    pleaseWaitDialog.dismissDialog();
                }


            }
        });

        forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetPass(v);
            }
        });

    }

    private void checkpreferences() {
        String checkbox = sharedPreferences.getString(getString(R.string.rmbox), "False");
        String email = sharedPreferences.getString(getString(R.string.uemail), "");
        String pwd = sharedPreferences.getString(getString(R.string.upwd), "");
        UserEmail.setText(email);
        UserPass.setText(pwd);
        if (checkbox.equals("true")) {
            rmcheckbox.setChecked(true);
            flag=1;
        } else {
            rmcheckbox.setChecked(false);
        }
    }
    public void forgetPass(View v){
        final EditText resetmail = new EditText(v.getContext());
        AlertDialog.Builder passwordreset = new AlertDialog.Builder(v.getContext());
        passwordreset.setTitle("Reset Password ?");
        passwordreset.setMessage("Enter Your Verified Email to Reset Password");
        passwordreset.setView(resetmail);
        passwordreset.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    firebaseAuth.sendPasswordResetEmail(resetmail.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            StyleableToast.makeText(Login.this, "Reset Link sent to E-Mail..", Toast.LENGTH_SHORT, R.style.customtoast).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            StyleableToast.makeText(Login.this, "Reset Link Not Sent Try Again..", Toast.LENGTH_SHORT, R.style.errortoast).show();
                        }
                    });
                }catch (Exception e){
                    StyleableToast.makeText(Login.this, "Enter Valid E-mail Address !", Toast.LENGTH_SHORT, R.style.errortoast).show();
                }
            }
        });
        passwordreset.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        passwordreset.create().show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkpreferences();
        if(flag==1){
            startActivity(new Intent(Login.this,Home.class));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Login.this,Login.class));
        finish();
    }
}