package com.example.geoponics;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.muddzdev.styleabletoast.StyleableToast;

import java.io.IOException;


public class SellFragment extends Fragment {



    public SellFragment() {
        // Required empty public constructor
    }

    Spinner products_spinner;
    EditText productName,productDescription,productprice,productCount;
    Button sell_btn;
    ImageView productImg;
    ProgressBar uploadprogressbar;

    Uri selectedImage;

    private static int RESULT_LOAD_IMAGE = 1;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    FirebaseFirestore db=FirebaseFirestore.getInstance();
    DocumentReference ref;

    String userID,user,username,address,phnno,isFarmer,product_name,product_desc,product_count,product_price,product_type,product_url;

    StorageReference storageReference;
    private int STORAGE_PERMISSION_CODE=1;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sell, container, false);

        products_spinner=view.findViewById(R.id.products_spin);
        productName=view.findViewById(R.id.product_name);
        productDescription=view.findViewById(R.id.product_description);
        productprice=view.findViewById(R.id.product_price);
        productCount=view.findViewById(R.id.product_count);
        sell_btn=view.findViewById(R.id.sell_btn);
        productImg=view.findViewById(R.id.product_image);
        uploadprogressbar=view.findViewById(R.id.progressUplaodBar);
        spinnerMethod();
        storageReference=FirebaseStorage.getInstance().getReference("Product_Images");

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getContext());
        editor=sharedPreferences.edit();

        userID = sharedPreferences.getString(getString(R.string.uid),"");
        user="User_ID_"+userID;

        ref=db.collection("Users").document(user);



        productImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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


        getUserData();
        sell_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                product_name=productName.getText().toString();
                product_price=productprice.getText().toString();
                product_desc=productDescription.getText().toString();
                product_count=productCount.getText().toString();

                if(!TextUtils.isEmpty(product_name) && !TextUtils.isEmpty(product_price) && !TextUtils.isEmpty(product_desc) && !TextUtils.isEmpty(product_count)){
                    uploadData();
                }else{
                    StyleableToast.makeText(getContext(), "Please Enter All Data", Toast.LENGTH_SHORT,R.style.errortoast).show();
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
            productImg.setImageURI(selectedImage);
        }
    }

    public void spinnerMethod(){
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(getContext(),R.array.products_sell,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        products_spinner.setAdapter(adapter);
        products_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                if( parent.getItemAtPosition(position).toString().equals("Vegetables")){
                    productName.setHint("Enter Vegetable Name..");
                    productDescription.setHint("Enter Vegetable Description..");
                    productCount.setHint("Enter Total Kg's..");
                    product_type="Vegetables";
                    productprice.setVisibility(View.VISIBLE);
                    sell_btn.setText("Sell !");
                }
                if( parent.getItemAtPosition(position).toString().equals("Plants")){
                    productName.setHint("Enter Plant Name..");
                    productDescription.setHint("Enter Plant Description..");
                    productCount.setHint("Enter Total Count..");
                    product_type="Plants";
                    productprice.setVisibility(View.VISIBLE);
                    sell_btn.setText("Sell !");
                }
                if( parent.getItemAtPosition(position).toString().equals("Fertilizers")){
                    productName.setHint("Enter Fertilizers Name..");
                    productDescription.setHint("Enter Fertilizers Description..");
                    productCount.setHint("Enter Total Count..");
                    product_type="Fertilizers";
                    productprice.setVisibility(View.VISIBLE);
                    sell_btn.setText("Sell !");

                }
                if( parent.getItemAtPosition(position).toString().equals("Pots")){
                    productName.setHint("Enter Pot Name/Type..");
                    productDescription.setHint("Enter Pot Description..");
                    productCount.setHint("Enter Total Count..");
                    product_type="Pots";
                    productprice.setVisibility(View.VISIBLE);
                    sell_btn.setText("Sell !");
                }
                if( parent.getItemAtPosition(position).toString().equals("Gardening Tools")){
                    productName.setHint("Enter Name/Type of Tool..");
                    productDescription.setHint("Enter Tool Description..");
                    productCount.setHint("Enter Total Count..");
                    product_type="Gardening Tools";
                    productprice.setVisibility(View.VISIBLE);
                    sell_btn.setText("Sell !");

                }
                if( parent.getItemAtPosition(position).toString().equals("Rotten Vegetables")){
                    productName.setHint("Enter Name/Type eg..Fruits/Vegetables/Plants");
                    productDescription.setHint("Enter  Description..");
                    productCount.setHint("Enter Total Count..");
                    productprice.setText("Not Picked Up");
                    productprice.setVisibility(View.INVISIBLE);
                    product_type="Rotten Vegetables";
                    sell_btn.setText("Place Order To collect !");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    public void getUserData(){
        ref.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if(documentSnapshot.exists()){
                            username=documentSnapshot.getString("username");
                            address=documentSnapshot.getString("address");
                            phnno=documentSnapshot.getString("phno");
                            isFarmer=documentSnapshot.getString("isFarmer");
                        }else{
                            StyleableToast.makeText(getContext(), "User Data Not Found", Toast.LENGTH_SHORT,R.style.errortoast).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                StyleableToast.makeText(getContext(), "Some Error Occurred Please Try Again..", Toast.LENGTH_SHORT,R.style.errortoast).show();
            }
        });
    }

    private String getFileExtension(Uri uri){
        ContentResolver cr = getActivity().getApplicationContext().getContentResolver();;
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadData(){
            uploadprogressbar.setVisibility(View.VISIBLE);
            if(selectedImage !=null){
                final StorageReference filepath=storageReference.child(System.currentTimeMillis()+"."+getFileExtension(selectedImage));
                filepath.putFile(selectedImage)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Handler handler=new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                uploadprogressbar.setProgress(0);
                                            }
                                        },(500));
                                        //Toast.makeText(getContext(), ""+username+uri, Toast.LENGTH_SHORT).show();
                                        //Toast.makeText(getContext(), ""+product_name+product_price+product_desc+product_count, Toast.LENGTH_SHORT).show();
                                        try{
                                            product_url=uri.toString();
                                            ProductModel productData=new ProductModel(
                                                    product_name,
                                                    product_desc,
                                                    product_count,
                                                    product_price,
                                                    username,
                                                    address,
                                                    phnno,
                                                    isFarmer,
                                                    userID,
                                                    product_url,
                                                    product_type
                                            );
                                            db.collection("Products").document().set(productData)
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            StyleableToast.makeText(getContext(), "Some Error Occurred Please Try Again..", Toast.LENGTH_SHORT,R.style.errortoast).show();
                                                        }
                                                    });
                                        }catch (Exception e){
                                            StyleableToast.makeText(getContext(), "Some Error Occurred Please Try Again..", Toast.LENGTH_SHORT,R.style.errortoast).show();
                                        }

                                    }
                                });
                                StyleableToast.makeText(getContext(), "Upload Successful !", Toast.LENGTH_SHORT,R.style.customtoast).show();
                                uploadprogressbar.setVisibility(View.INVISIBLE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        StyleableToast.makeText(getContext(), "Some Error Occurred Please Try Again..", Toast.LENGTH_SHORT,R.style.errortoast).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress=(100.0*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                        uploadprogressbar.setProgress((int)progress);
                    }
                });
            }else{
                StyleableToast.makeText(getContext(), "Please Select Image !", Toast.LENGTH_SHORT,R.style.errortoast).show();
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