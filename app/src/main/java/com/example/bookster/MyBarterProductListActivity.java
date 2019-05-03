package com.example.bookster;

import android.app.Activity;
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

public class MyBarterProductListActivity extends AppCompatActivity {
    private String category;
    private User myUserProfile;
    private ArrayList<Product> productList;
    private FirebaseUser user;
    private ListView mListView;
    private DatabaseReference mDatabase;
    private int REQUEST_CODE = 1;
    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private FloatingActionButton fab;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_product_list_layout);

        //Configure toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Select product to Barter");
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
        String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        productList = new ArrayList<>();
        mListView = findViewById(R.id.my_product_list_view);

        //receives and add all of the user's products to a list from firebase
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
                MyProductListAdapter adapter = new MyProductListAdapter(MyBarterProductListActivity.this, R.layout.my_product_list_item_layout, productList);
                mListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Returns the selected product to parent activity
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myintent = new Intent();
                myintent.putExtra("productObj", productList.get(position));
                setResult(Activity.RESULT_OK, myintent);
                finish();
            }
        });

    }
}
