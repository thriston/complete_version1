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

public class ProductListActivity extends AppCompatActivity {
    private String category;
    private User myUserProfile;
    private ArrayList<Product> productList;
    private FirebaseUser user;
    private ListView mListView;
    private DatabaseReference mDatabase;
    private int REQUEST_CODE = 1;
    private FirebaseAuth mAuth;
    Toolbar toolbar;
    FloatingActionButton fab;

    @Override
    protected void onActivityResult(int requestCode, int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK )
        {
//            Intent refresh = new Intent(this, ProductListActivity.class);
//            startActivity(refresh);
//            this.finish();
            DatabaseReference db;
            db = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            db.keepSynced(true);
            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    myUserProfile = dataSnapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_list_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Products");
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
        fab = findViewById(R.id.fab);
        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        //System.out.println("CATEGORY IN LIST"+category);
        productList = new ArrayList<>();
        mListView = findViewById(R.id.product_list_view);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Products");
        mDatabase.keepSynced(true);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    if(ds.child("category").getValue().equals(category))
                    {
                        Product product = ds.getValue(Product.class);
                        productList.add(product);
                    }
                }

                ProductListAdapter adapter = new ProductListAdapter(ProductListActivity.this, R.layout.product_list_item_layout, productList);
                mListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //If user is logged in, copy User information to variable
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null)
        {
            DatabaseReference db;
            db = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            db.keepSynced(true);
            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    myUserProfile = dataSnapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }




        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //if(position==0){
                Intent myintent = new Intent(view.getContext(), ProductDetailsActivity.class);
                myintent.putExtra("productObj", productList.get(position));
                //System.out.println("CATEGORY: "+categoryList.get(position).getName());
                startActivityForResult(myintent, 0);
                //}
            }
        });


        //to Add product
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null)
                {
                    Intent myintent = new Intent(ProductListActivity.this, AddProductActivity.class);
                    myintent.putExtra("category",category);
                    myintent.putExtra("myUserProfile",myUserProfile);
                    startActivityForResult(myintent, 0);
                }
                else
                {
                    Intent myintent = new Intent(ProductListActivity.this, Login.class);
                    startActivityForResult(myintent, REQUEST_CODE);
                }

            }
        });


    }


}
