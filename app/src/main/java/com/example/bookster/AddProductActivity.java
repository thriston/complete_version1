package com.example.bookster;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddProductActivity extends AppCompatActivity {
    String category;
    FloatingActionButton fabAdd;
    FloatingActionButton fabCancel;
    User myUserProfile;
    EditText productName;
    EditText description;
    EditText price;
    EditText quantity;
    Switch allowCalls;
    Product product;
    String productID;
    DatabaseReference myDatabase;
    Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_add_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Add Product");
        category = getIntent().getStringExtra("category");
        myUserProfile = (User) getIntent().getSerializableExtra("myUserProfile");

        System.out.println("USER: "+myUserProfile.getFullname());
        System.out.println("USER: "+myUserProfile.getEmail());
        System.out.println("USER: "+myUserProfile.getContact());
        //System.out.println("USER: "+myUserProfile.getUID());

        fabAdd = findViewById(R.id.fabAdd);
        fabCancel = findViewById(R.id.fabCancel);

        productName = findViewById(R.id.productName);
        description = findViewById(R.id.description);
        price = findViewById(R.id.price);
        quantity = findViewById(R.id.quantity);
        allowCalls = findViewById(R.id.allowCallsSwitch);
        myDatabase = FirebaseDatabase.getInstance().getReference().child("Products");



        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                productID = FirebaseDatabase.getInstance().getReference().child("Products").push().getKey();

                Product product = new Product(
                        productID,
                        productName.getText().toString(),
                        price.getText().toString(),
                        quantity.getText().toString(),
                        description.getText().toString(),
                        category,
                        myUserProfile);

                //Send information to User's Profile
                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users")
                        .child(product.getSeller().myUID).child("Products").child(productID);
                db.child("id").setValue(productID);
                db.child("name").setValue(productName.getText().toString());
                db.child("details").setValue(description.getText().toString());
                db.child("price").setValue(price.getText().toString());
                db.child("category").setValue(category);


                myDatabase.child(productID).setValue(product);

                Toast.makeText(getApplicationContext(),"Product Added", Toast.LENGTH_SHORT).show();
                finish();


            }
        });


        fabCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




    }
}
