package com.example.geoponics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.squareup.picasso.Picasso;

public class ClusterActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    FirebaseFirestore db;
    FirestoreRecyclerOptions<UserModel> options;
    FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cluster);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView=findViewById(R.id.userslist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db=FirebaseFirestore.getInstance();
        Query query= db.collection("Users");
        options=new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query,UserModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<UserModel, UserViewHolder>(options) {
            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_to_cart_item_view, parent, false);
                return new ClusterActivity.UserViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull UserModel model) {
                holder.item_name.setText("Name : "+model.getUsername());
                holder.item_price.setText("Phone No : "+model.getPhno()+"\nAddress : "+model.getAddress()+"\n"+"Is Farmer : "+model.getIsFarmer()+"\n");
                Picasso.get()
                        .load(getSnapshots().getSnapshot(position).getString("profile_url"))
                        .placeholder(R.drawable.progress_animation)
                        .error(R.drawable.ic_profile)
                        .into(holder.pro_img);

                holder.removeItem.setVisibility(View.INVISIBLE);
            }
        };

        recyclerView.setAdapter(adapter);


    }


    private class UserViewHolder extends RecyclerView.ViewHolder {

        TextView item_name, item_price;
        ImageView pro_img;
        Button removeItem;
        public View mview;

        public UserViewHolder(@NonNull View itemView) {
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