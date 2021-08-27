package com.example.geoponics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.muddzdev.styleabletoast.StyleableToast;
import com.squareup.picasso.Picasso;

public class CartActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userID, user;

    FirebaseFirestore db;
    FirestoreRecyclerOptions<ProductModel> options;
    FirestoreRecyclerAdapter adapter;
    DocumentReference ref;

    TextView totalcost,point,total;
    int cost=0,walletpt=0,rem=0;
    String docID;
    Button placeorder;
    OrderPlaceDialog orderPlaceDialog=new OrderPlaceDialog(CartActivity.this);




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CartActivity.this);
        editor = sharedPreferences.edit();
        user = sharedPreferences.getString(getString(R.string.uid), "");
        userID = "User_ID_" + user;

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        totalcost=findViewById(R.id.totalcost);
        total=findViewById(R.id.total);
        totalcost.setText(""+cost);
        placeorder=findViewById(R.id.placeorder);
        point=findViewById(R.id.walletpts);


        db=FirebaseFirestore.getInstance();
        ref=db.collection("Users").document(userID).collection("Wallet").document("Wallet_"+userID);
        getpoints();
        Query query = db.collection("Users").document(userID).collection("Cart");
        options = new FirestoreRecyclerOptions.Builder<ProductModel>()
                .setQuery(query, ProductModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<ProductModel, ProductViewHolder>(options) {
            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_to_cart_item_view, parent, false);
                return new ProductViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, final int position, @NonNull final ProductModel model) {
                holder.item_name.setText(model.getProduct_name());
                holder.item_price.setText("₹ "+model.getProduct_price()+"/-");
                docID=getSnapshots().getSnapshot(position).getId().toString();
                
                    cost += Integer.parseInt(model.getProduct_price().toString());
                    totalcost.setText("₹ "+cost+"/-");

                Picasso.get().load(model.getProduct_url())
                        .placeholder(R.drawable.progress_animation)
                        .error(R.drawable.try_later)
                        .into(holder.pro_img);

                holder.removeItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        int rem=cost-Integer.parseInt(getSnapshots().getSnapshot(position).getString("product_price"));
//                        Toast.makeText(CartActivity.this, ""+rem, Toast.LENGTH_SHORT).show();
                        getSnapshots().getSnapshot(position).getReference().delete();
                        notifyDataSetChanged();
                        StyleableToast.makeText(CartActivity.this, "Item Removed !", Toast.LENGTH_SHORT,R.style.errortoast).show();
                        startActivity(new Intent(CartActivity.this,CartActivity.class));
                        finish();
                    }
                });


            }
        };


        recyclerView.setAdapter(adapter);
        adapter.startListening();

        total.setText("..");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int res=cost-walletpt;
                total.setText("₹ "+Math.abs(res)+"/-");
                if(res<0){
                    rem=walletpt-cost;
                    total.setText("₹ "+"0"+"/-");
                }
            }
        },1500);

        placeorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                orderPlaceDialog.startLoadingDialog();
                ref.update("Points",""+Math.abs(rem));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        orderPlaceDialog.dismissDialog();
                        Intent i = new Intent(CartActivity.this, Home.class);
                        startActivity(i);
                    }
                }, 3000);
            }
        });


    }

    private class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView item_name, item_price;
        ImageView pro_img;
        Button removeItem;


        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            item_name = itemView.findViewById(R.id.item_name);
            item_price = itemView.findViewById(R.id.item_price);
            pro_img = itemView.findViewById(R.id.product_pic);
            removeItem=itemView.findViewById(R.id.removeItem);


        }
    }

    public void getpoints(){
        ref.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        point.setText("- "+documentSnapshot.getString("Points").toString());
                        walletpt=Integer.parseInt(documentSnapshot.getString("Points").toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CartActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        getpoints();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        getpoints();
    }




}