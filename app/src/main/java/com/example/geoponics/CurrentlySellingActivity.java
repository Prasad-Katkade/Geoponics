package com.example.geoponics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.muddzdev.styleabletoast.StyleableToast;
import com.squareup.picasso.Picasso;

public class CurrentlySellingActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userID, user;

    FirebaseFirestore db;
    FirestoreRecyclerOptions<ProductModel> options;
    FirestoreRecyclerAdapter adapter;
    String docID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currently_selling);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CurrentlySellingActivity.this);
        editor = sharedPreferences.edit();
        user = sharedPreferences.getString(getString(R.string.uid), "");
        userID = "User_ID_" + user;

        recyclerView=findViewById(R.id.cslist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        db= FirebaseFirestore.getInstance();
        Query query =db.collection("Products").whereEqualTo("seller_ID",user);
        options = new FirestoreRecyclerOptions.Builder<ProductModel>()
                .setQuery(query, ProductModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<ProductModel, CurrentlySellingActivity.ProductViewHolder>(options) {
            @NonNull
            @Override
            public CurrentlySellingActivity.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_to_cart_item_view, parent, false);
                return new CurrentlySellingActivity.ProductViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CurrentlySellingActivity.ProductViewHolder holder, final int position, @NonNull ProductModel model) {
                holder.item_name.setText("Name : "+model.getProduct_name());
                holder.item_price.setText("Price : ₹ "+model.getProduct_price()+"/-"+"\nTotal Count/Kg's :"+model.getProduct_count()+"\n");
                docID=getSnapshots().getSnapshot(position).getId().toString();

                if(getSnapshots().getSnapshot(position).getString("product_type").equals("Rotten Vegetables")){
                    holder.item_price.setText("Total Count/Kg's :"+model.getProduct_count()+"\n"+"Status :  "+model.getProduct_price());
                }

                Picasso.get().load(model.getProduct_url())
                        .placeholder(R.drawable.progress_animation)
                        .error(R.drawable.try_later)
                        .into(holder.pro_img);

                holder.removeItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getSnapshots().getSnapshot(position).getReference().delete();
                        notifyDataSetChanged();
                        StyleableToast.makeText(CurrentlySellingActivity.this, "Item Removed !", Toast.LENGTH_SHORT,R.style.errortoast).show();
                    }
                });

                holder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        docID=getSnapshots().getSnapshot(position).getId().toString();
                        Intent intent=new Intent(CurrentlySellingActivity.this,DescriptionActivity.class);
                        intent.putExtra("docID",docID);
                        startActivity(intent);

                    }
                });

            }
        };
        recyclerView.setAdapter(adapter);


    }

    private class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView item_name, item_price;
        ImageView pro_img;
        Button removeItem;
        public View mview;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            item_name = itemView.findViewById(R.id.item_name);
            item_price = itemView.findViewById(R.id.item_price);
            pro_img = itemView.findViewById(R.id.product_pic);
            removeItem=itemView.findViewById(R.id.removeItem);
            mview=itemView;

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}