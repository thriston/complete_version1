package com.example.bookster;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Seller_info extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ImageView profileImageV;
    android.support.v7.widget.Toolbar toolbar;
    private TextView fullNameView, emailView, contactView;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_info);
        user = FirebaseAuth.getInstance().getCurrentUser();

        fullNameView=findViewById(R.id.fullNameField);
        emailView=findViewById(R.id.emailField);
        contactView=findViewById(R.id.contactField);
        profileImageV = findViewById(R.id.profilePicture);

        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(user.getUid());
        mDatabase.keepSynced(true);
        Intent intent = getIntent();
        final Product product = (Product) intent.getSerializableExtra("productObj");
        final String myUserName = intent.getStringExtra("myUserName");
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(product.getSeller().getFullname());



        System.out.println("THIS IS THE PRODUCT NAME: " + product.getName());
        fullNameView.setText(product.getSeller().getFullname());
        contactView.setText(product.getSeller().getContact());
        emailView.setText(product.getSeller().getEmail());
        Glide.with(getApplicationContext()).load(product.getSeller().profilePicURL).apply(new RequestOptions().placeholder(R.drawable.img_placeholder)).error(R.drawable.image_placeholder).fitCenter().into(profileImageV);



        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        //For Call Button
        ImageButton btn = (ImageButton) findViewById(R.id.callbtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null)
                {
                    if(product.getSeller().getContact().equals("Hidden"))
                    {
                        Toast.makeText(Seller_info.this, "Seller's Contact Number is not Available", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + product.getSeller().getContact()));
                        startActivity(intent);
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please Login To Continue", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //Gets Current User's name if logged in that will be used to sent to other activity
        ImageButton messageBtn = findViewById(R.id.msgbtn);
        messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null)
                {
                    Intent myintent = new Intent(Seller_info.this, MessageActivity.class);
                    myintent.putExtra("productObj", product);
                    myintent.putExtra("myUserName", myUserName);
                    myintent.putExtra("myUserProfile",product.getSeller());
                    //System.out.println("CATEGORY: "+categoryList.get(position).getName());
                    startActivityForResult(myintent, 0);
                }
                else
                {
//                    Intent myintent = new Intent(ProductDetailsActivity.this, Login.class);
                    Toast.makeText(getApplicationContext(), "Please Login To Continue", Toast.LENGTH_SHORT).show();
//                    startActivityForResult(myintent, REQUEST_CODE);
                }

            }
        });
    }
}
