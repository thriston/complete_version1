package com.example.bookster;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ProductListActivity extends AppCompatActivity {
    private String category;
    private User myUserProfile;
    private ArrayList<Product> productList;
    private ListView mListView;
    private FirebaseUser user;
    private ProductListAdapter adapter;
    private EditText searchEdit;
    private Product product;
    private Intent myintent;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private StorageReference pathRef;
    private StorageReference ref;
    private ArrayList<String> productImages;
    private int REQUEST_CODE = 1;
    private int count;
    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private FloatingActionButton fab;


    @Override
    protected void onActivityResult(int requestCode, int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK )
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
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_list_layout);

        //To stop keyboard from auto-opening
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        productImages = new ArrayList<>();
        mStorage = FirebaseStorage.getInstance().getReference();
        searchEdit = findViewById(R.id.search_edit);
        productList = new ArrayList<>();
        fab = findViewById(R.id.fab);

        //Configure toolbar
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

        //Get data from parent activity
        Intent intent = getIntent();
        category = intent.getStringExtra("category");

        //Display products that are active. i.e. Not deleted etc.
        mListView = findViewById(R.id.product_list_view);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Products");
        mDatabase.keepSynced(true);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    if(ds.child("category").getValue().equals(category)  && ds.child("active").getValue().toString().equals("true"))
                    {
                        Product product = ds.getValue(Product.class);
                        productList.add(product);
                    }
                }

                adapter = new ProductListAdapter(ProductListActivity.this, R.layout.product_list_item_layout, productList);
                mListView.setAdapter(adapter);
                mListView.setTextFilterEnabled(true);

                searchEdit.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
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

        //Opens a product page for prospect user to view or a product page for the owner of the product to view nd make changes
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                product = productList.get(position);
                if(FirebaseAuth.getInstance().getCurrentUser()!= null && product.getSeller().getMyUID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) )
                {
                    myintent = new Intent(view.getContext(), MyProductDetailsActivity.class);
                    myintent.putExtra("productObj", product);
                    startActivityForResult(myintent, 0);
                }
                else
                {
                    myintent = new Intent(view.getContext(), ProductDetailsActivity.class);
                    myintent.putExtra("productObj", product);
                    startActivityForResult(myintent, 0);
                }

            }
        });

        //Start add product activity if clicked
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
                    Toast.makeText(ProductListActivity.this, "Please login to add product", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

}
