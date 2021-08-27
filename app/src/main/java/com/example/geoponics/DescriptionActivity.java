package com.example.geoponics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.muddzdev.styleabletoast.StyleableToast;
import com.squareup.picasso.Picasso;

public class DescriptionActivity extends AppCompatActivity {

    String docID;
    FirebaseFirestore db;
    DocumentReference reference;

    TextView pname, pdesc, pprice, pcnt, sName, sAddr, sphno, sIsFarm, sID, quantity;
    ImageView productImg;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        docID = intent.getStringExtra("docID");
        pname = findViewById(R.id.name);
        pdesc = findViewById(R.id.desc);
        pprice = findViewById(R.id.price);
        pcnt = findViewById(R.id.count);
        sName = findViewById(R.id.sname);
        sAddr = findViewById(R.id.saddress);
        sphno = findViewById(R.id.sphnno);
        sIsFarm = findViewById(R.id.sisfarmer);
        sID = findViewById(R.id.sid);
        quantity = findViewById(R.id.quantity);
        productImg = findViewById(R.id.pimage);

        db = FirebaseFirestore.getInstance();
        reference = db.collection("Products").document(docID);

        reference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Picasso.get().load(documentSnapshot.getString("product_url"))
                                .placeholder(R.drawable.progress_animation)
                                .error(R.drawable.try_later)
                                .fit()
                                .centerCrop()
                                .into(productImg);

                        if (documentSnapshot.getString("product_type").equals("Vegetables")) {
                            quantity.setText("Total Kg's Available :");
                        }

                        pname.setText(documentSnapshot.getString("product_name"));
                        pdesc.setText(documentSnapshot.getString("product_desc"));
                        pprice.setText(documentSnapshot.getString("product_price"));
                        pcnt.setText(documentSnapshot.getString("product_count"));
                        sName.setText(documentSnapshot.getString("seller_name"));
                        sAddr.setText(documentSnapshot.getString("seller_addr"));
                        sphno.setText(documentSnapshot.getString("seller_phno"));
                        sIsFarm.setText(documentSnapshot.getString("seller_farmer"));
                        sID.setText(documentSnapshot.getString("seller_ID"));


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                StyleableToast.makeText(DescriptionActivity.this, "Cannot Retrieve Data", Toast.LENGTH_SHORT,R.style.errortoast).show();
            }
        });


    }


}