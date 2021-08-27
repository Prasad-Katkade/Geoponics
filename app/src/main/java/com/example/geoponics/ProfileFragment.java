package com.example.geoponics;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


public class ProfileFragment extends Fragment {


    public ProfileFragment() {
        // Required empty public constructor
    }

    ImageView profImg;
    Uri selectedImage;
    private static int RESULT_LOAD_IMAGE = 1;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userID, user;

    FirebaseFirestore db;
    DocumentReference ref;


    TextView uname, email,phn, addr;
    Button btn_update;
    ProgressBar updateprogress;

    String profName, profEmail, profphno, profAddr, profile_uri;

    StorageReference storageReference;

    CardView currentlySelling,Mycluster;
    ProgressBar progressBar;
    private int STORAGE_PERMISSION_CODE=1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = sharedPreferences.edit();
        user = sharedPreferences.getString(getString(R.string.uid), "");
        userID = "User_ID_" + user;

        email = view.findViewById(R.id.profile_email);
        phn = view.findViewById(R.id.profile_phnno);
        addr = view.findViewById(R.id.profile_addr);
        uname = view.findViewById(R.id.profile_name);
        btn_update = view.findViewById(R.id.updateData);
        updateprogress = view.findViewById(R.id.updateprogressbar);
        progressBar=view.findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);

        db = FirebaseFirestore.getInstance();
        ref = db.collection("Users").document(userID);
        getData();

        storageReference = FirebaseStorage.getInstance().getReference("Profile_Images");


        profImg = view.findViewById(R.id.profile_img);
        profImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                }else{
                    requestCamPermission();
                }
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });

        currentlySelling=view.findViewById(R.id.currentlysell);
        currentlySelling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),CurrentlySellingActivity.class));
            }
        });

        Mycluster=view.findViewById(R.id.cluster);
        Mycluster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(getContext(),ClusterActivity.class));
            }
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedImage = data.getData();
            profImg.setImageURI(selectedImage);
        }
    }

    public void getData() {
        ref.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        profName = documentSnapshot.getString("username");
                        uname.setText("\t" + profName);
                        profEmail = documentSnapshot.getString("email");
                        email.setText("\t" + profEmail);
                        profphno = documentSnapshot.getString("phno");
                        phn.setText("\t" + profphno);
                        profAddr = documentSnapshot.getString("address");
                        addr.setText("\t" + profAddr);

                        Picasso.get().load(documentSnapshot.getString("profile_url"))
                                .placeholder(R.drawable.progress_animation)
                                .error(R.drawable.ic_addphoto)
                                .into(profImg);
                        progressBar.setVisibility(View.INVISIBLE);

                    }
                });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getActivity().getApplicationContext().getContentResolver();
        ;
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }


    private void uploadData() {
        updateprogress.setVisibility(View.VISIBLE);
        if (selectedImage != null) {
            final StorageReference filepath = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(selectedImage));
            filepath.putFile(selectedImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            updateprogress.setProgress(0);
                                        }
                                    }, (500));
                                    profile_uri = uri.toString();
//                                    try {
//                                        ref.update("phno", profphno);
//                                        ref.update("address", profAddr);
//                                    }catch (Exception e){
//                                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//                                    }
//                                    Map<String, String> newData = new HashMap<>();
//                                    newData.put("phno", profphno);
//                                    newData.put("address", profAddr);
//                                    ref.set(newData, SetOptions.merge());
                                    ref.update("profile_url", profile_uri);

                                }
                            });
                            Toast.makeText(getContext(), "Upload Succesful", Toast.LENGTH_SHORT).show();
                            updateprogress.setVisibility(View.INVISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    updateprogress.setProgress((int) progress);
                }
            });
        } else {
            Toast.makeText(getContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestCamPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(getContext())
                    .setTitle("Permission Needed")
                    .setMessage("This Permission is require to access gallery")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();

        }else{
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
        }
    }


}