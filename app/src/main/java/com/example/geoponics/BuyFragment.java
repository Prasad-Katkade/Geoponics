package com.example.geoponics;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.muddzdev.styleabletoast.StyleableToast;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BuyFragment extends Fragment {


    public BuyFragment() {
        // Required empty public constructor
    }

    Spinner products_spinner;
    TextView notavailable;

    RecyclerView recyclerView;
    FirebaseFirestore db;
    FirestoreRecyclerAdapter productAdapter;
    FirestoreRecyclerOptions<ProductModel> options;

    String producttype;
    String docID;

    CollectionReference reference;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userID,user;

    FloatingActionButton cart;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_buy, container, false);
        cart=view.findViewById(R.id.cart);
        products_spinner = view.findViewById(R.id.products_spin);
        notavailable=view.findViewById(R.id.notavailable);
        spinnerMethod();
        db = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getContext());
        editor=sharedPreferences.edit();
        user = sharedPreferences.getString(getString(R.string.uid),"");
        userID="User_ID_"+user;
        reference=db.collection("Users");
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),CartActivity.class));
            }
        });

        return view;
    }

    private class ProductsViewHolder extends RecyclerView.ViewHolder {

        public TextView product_title, product_descr;
        public Button addToCart;
        public ImageView productImg;
        public View mview;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            product_title = itemView.findViewById(R.id.productTitle);
            product_descr = itemView.findViewById(R.id.productDesc);
            addToCart = itemView.findViewById(R.id.btn_AddToCart);
            productImg = itemView.findViewById(R.id.product_Img);
            mview=itemView;

        }
    }

    public void loadProducts(){
        Query query = db.collection("Products").whereEqualTo("product_type",producttype);
        options = new FirestoreRecyclerOptions.Builder<ProductModel>()
                .setQuery(query, ProductModel.class)
                .build();

        productAdapter = new FirestoreRecyclerAdapter<ProductModel, ProductsViewHolder>(options) {
            @NonNull
            @Override
            public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_view, parent, false);
                return new ProductsViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductsViewHolder holder, final int position, @NonNull final ProductModel model) {
                holder.product_title.setText(model.getProduct_name());
                holder.product_descr.setText("₹ "+model.getProduct_price()+"/-"+"\n"+model.getProduct_desc());
                docID=getSnapshots().getSnapshot(position).getId().toString();

                if(getSnapshots().getSnapshot(position).getString("product_type").equals("Vegetables")){
                    holder.product_descr.setText("₹ "+model.getProduct_price()+"/kg"+"\n"+model.getProduct_desc());
                }
                    Picasso.get().load(model.getProduct_url())
                            .fit()
                            .placeholder(R.drawable.progress_animation)
                            .error(R.drawable.try_later)
                            .into(holder.productImg);
                holder.addToCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String, String> product = new HashMap<>();
                            product.put("product_id", docID);
                            product.put("product_name", model.getProduct_name());
                            product.put("product_price", model.getProduct_price());
                            product.put("product_url", model.getProduct_url());
                        reference.document(userID).collection("Cart")
                                .add(product)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        StyleableToast.makeText(getContext(), "Item Added to cart !", Toast.LENGTH_SHORT,R.style.customtoast).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                StyleableToast.makeText(getContext(), "error occurred please try again ", Toast.LENGTH_SHORT,R.style.errortoast).show();
                            }
                        });
                    }
                });

                holder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        docID=getSnapshots().getSnapshot(position).getId().toString();
                        Intent intent=new Intent(getContext(),DescriptionActivity.class);
                        intent.putExtra("docID",docID);
                        startActivity(intent);
                        
                    }
                });
            }
        };
        productAdapter.startListening();
        recyclerView.setAdapter(productAdapter);
    }

    public void spinnerMethod(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.products, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        products_spinner.setAdapter(adapter);
        notavailable.setText("Products Not Available");
        products_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                producttype=adapterView.getItemAtPosition(i).toString();
                loadProducts();
                notavailable.setText(" ");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


}