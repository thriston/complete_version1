package com.example.bookster;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProductDetailsActivity extends AppCompatActivity {

    ViewPager viewPager;
    ProductPictureSlider adapter;
    User myUserProfile;
    String myUserName;

    android.support.v7.widget.Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_page);


        Intent intent = getIntent();
        final Product product = (Product) intent.getSerializableExtra("productObj");
        myUserProfile = (User) intent.getSerializableExtra("myUserProfile");
        //System.out.println("PRODUCT NAME: "+narnia.getName());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Bookster - "+product.getName());



        viewPager = findViewById(R.id.product_img_slider);
        adapter = new ProductPictureSlider(this);
        viewPager.setAdapter(adapter);

        //set Texts
        TextView productName = findViewById(R.id.productNameTextView);
        TextView price = findViewById(R.id.productPricetextView);
        TextView category = findViewById(R.id.productCatagoryTextView);
        TextView details = findViewById(R.id.productDetailstextView);
        TextView sellerName = findViewById(R.id.sellerNameTextView);
        TextView sellerContact = findViewById(R.id.sellerContactNumTextView);
        TextView sellerEmail = findViewById(R.id.sellerEmailTextView);

        productName.setText(product.getName());
        price.setText("$"+product.getPrice());
        category.setText("Category: "+product.getCategory());
        details.setText(product.getDetails());
        User userProfile = product.getSeller();
        //System.out.println("SELLER INFO DETAILS: "+product.getDetails());

        DatabaseReference db;
        db = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myUserName =  dataSnapshot.child("fullname").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






        sellerName.setText(product.getSeller().getFullname());
        sellerContact.setText(product.getSeller().getContact());
        sellerEmail.setText(product.getSeller().getEmail());


        //For Call Button
        Button btn = (Button) findViewById(R.id.callbtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);

                TextView num = findViewById(R.id.sellerContactNumTextView);
                intent.setData(Uri.parse("tel:" + num.getText() ));
                startActivity(intent);
            }
        });

        Button messageBtn = findViewById(R.id.msgbtn);
        messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(ProductDetailsActivity.this, MessageActivity.class);
                myintent.putExtra("productObj", product);
                myintent.putExtra("myUserName", myUserName);
                myintent.putExtra("myUserProfile",product.getSeller());
                //System.out.println("CATEGORY: "+categoryList.get(position).getName());
                startActivityForResult(myintent, 0);
            }
        });

    }



}
