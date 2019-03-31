package com.example.bookster;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Seller_info extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    android.support.v7.widget.Toolbar toolbar;
    private TextView fullNameView, emailView, contactView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_info);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        fullNameView=findViewById(R.id.fullNameField);
        emailView=findViewById(R.id.emailField);
        contactView=findViewById(R.id.contactField);
        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(user.getUid());
        mDatabase.keepSynced(true);
        Intent intent = getIntent();
        final Product product = (Product) intent.getSerializableExtra("productObj");
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(product.getSeller().getFullname());



        System.out.println("THIS IS THE PRODUCT NAME: " + product.getName());
        fullNameView.setText(product.getSeller().getFullname());
        contactView.setText(product.getSeller().getContact());
        emailView.setText(product.getSeller().getEmail());


        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ProductDetailsActivity.class));
            }
        });
    }
}
