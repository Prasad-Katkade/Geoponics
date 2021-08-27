package com.example.geoponics;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.automl.FirebaseAutoMLLocalModel;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions;


import java.io.IOException;
import java.util.List;

public class PlantHealthFragment extends Fragment {



    public PlantHealthFragment() {
        // Required empty public constructor
    }

    ImageView vimage;
    private static int RESULT_LOAD_IMAGE = 1;
    Uri selectedImage;
    TextView textView,details;

    private FirebaseAutoMLLocalModel localModel;
    FirebaseVisionImageLabeler labeler;
    FirebaseVisionImage image;
    float max;
    String name;
    Switch aSwitch;
    private int STORAGE_PERMISSION_CODE=1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_plant_health, container, false);
        vimage=view.findViewById(R.id.showimg);
        textView=view.findViewById(R.id.res);
        details=view.findViewById(R.id.details);
        aSwitch=view.findViewById(R.id.switch1);


        vimage.setOnClickListener(new View.OnClickListener() {
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

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    details.setVisibility(View.VISIBLE);
                }else{
                    details.setVisibility(View.GONE);
                }
            }
        });



        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data !=null && data.getData()!=null){
            selectedImage = data.getData();
            vimage.setImageURI(selectedImage);
            setLabelerFromLocalModel(selectedImage);
        }
    }

    private void setLabelerFromLocalModel(Uri uri) {
        localModel = new FirebaseAutoMLLocalModel.Builder()
                .setAssetFilePath("model/manifest.json")
                .build();
        try {
            FirebaseVisionOnDeviceAutoMLImageLabelerOptions options =
                    new FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder(localModel)
                            .setConfidenceThreshold(0.0f)
                            .build();
            labeler = FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(options);
            image = FirebaseVisionImage.fromFilePath(getContext(), uri);
            processImageLabeler(labeler, image);
        } catch (FirebaseMLException | IOException e) {
            // ...
        }
    }

    private void processImageLabeler(FirebaseVisionImageLabeler labeler, FirebaseVisionImage image) {
        details.setText("\nDetails :\n\n");
        max=0.0f;
        labeler.processImage(image).addOnCompleteListener(new OnCompleteListener<List<FirebaseVisionImageLabel>>() {
            @Override
            public void onComplete(@NonNull Task<List<FirebaseVisionImageLabel>> task) {
                for (FirebaseVisionImageLabel label : task.getResult()) {
                    String eachlabel = label.getText().toUpperCase();
                    float confidence = label.getConfidence();
                    eachlabel=eachlabel.replace("_"," ");
                    eachlabel=eachlabel.replace("  "," ");
                    details.append(eachlabel + " - " + ("" + confidence * 100).subSequence(0, 4) + "%" + "\n\n");
                    if(max<confidence){
                        max=confidence;
                        name=eachlabel;
                    }
                }
                name=name.replace("_"," ");
                name=name.replace("  "," ");
                //Toast.makeText(getContext(), ""+max+name, Toast.LENGTH_SHORT).show();
                textView.setText(name);
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("https://www.google.com/search?q=" + task.getResult().get(0).getText()));
//                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("OnFail", "" + e);
                Toast.makeText(getContext(), "Something went wrong! " + e, Toast.LENGTH_SHORT).show();
            }
        });
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