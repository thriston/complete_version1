package com.example.bookster;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyProductListActivity extends AppCompatActivity {
    private String category;
    private User myUserProfile;
    private ArrayList<Product> productList;
    private FirebaseUser user;
    private ListView mListView;
    private DatabaseReference mDatabase;
    private int REQUEST_CODE = 1;
    private FirebaseAuth mAuth;
    private String myUID;
    Toolbar toolbar;
    FloatingActionButton fab;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_product_list_layout);

        //Toolbar configurations
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("My items");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Gets current user User ID
        myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        productList = new ArrayList<>();

        //Gets the user's products from firebase and inflate the list
        mListView = findViewById(R.id.my_product_list_view);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(myUID).child("Products");
        mDatabase.keepSynced(false);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    Product product = ds.getValue(Product.class);
                    if(product.isActive())
                        productList.add(product);
                }
                MyProductListAdapter adapter = new MyProductListAdapter(MyProductListActivity.this, R.layout.my_product_list_item_layout, productList);
                mListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Add on click listener to list iteams
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myintent = new Intent(view.getContext(), MyProductDetailsActivity.class);
                myintent.putExtra("productObj", productList.get(position));
                startActivityForResult(myintent, 0);
            }
        });

    }

}
