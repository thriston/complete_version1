package com.example.bookster;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class SentRequestPage extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ImageView profileImageV, productImageV;
    android.support.v7.widget.Toolbar toolbar;
    private TextView fullNameView, emailView, contactView, productNameView, priceView, statusView, dateView, locationView, sellerTV, contactTV;
    private FirebaseUser user;
    private PurchaseRequest purchaseRequest = null;
    private BarterRequest barterRequest = null;
    private ImageButton call, msg;
    private String myUserName;
    private boolean isBarter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sent_page_screen);


        Intent intent = getIntent();
        purchaseRequest = (PurchaseRequest) intent.getSerializableExtra("purchaseRequest");
        barterRequest = (BarterRequest) intent.getSerializableExtra("barterRequest");

        if(barterRequest != null)
        {
            isBarter = true;
        }
        else
        if(purchaseRequest != null)
        {
            isBarter = false;
        }


        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Request");

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        user = FirebaseAuth.getInstance().getCurrentUser();


        sellerTV = findViewById(R.id.sellerName);
        contactTV = findViewById(R.id.contact);
        productNameView=findViewById(R.id.my_product);
        priceView=findViewById(R.id.price);
        statusView=findViewById(R.id.status);
        dateView=findViewById(R.id.date);
        locationView=findViewById(R.id.location);


        productImageV = findViewById(R.id.product_image);
        call = findViewById(R.id.callbtn);
        msg = findViewById(R.id.msgbtn);




        if(!isBarter)
        {
            TextView my_productTV = findViewById(R.id.my_productTV);
            my_productTV.setText("Product: ");
            contactTV.setText(purchaseRequest.getProduct().getSeller().getContact());
            sellerTV.setText(purchaseRequest.getProduct().getSeller().getFullname());
            productNameView.setText(purchaseRequest.getProduct().getName());
            priceView.setText("$"+purchaseRequest.getProduct().getPrice());
            statusView.setText(purchaseRequest.getStatus());
            dateView.setText(DateFormat.format("h:mma   dd/MM/yyyy", purchaseRequest.getDate()));
            locationView.setText(purchaseRequest.getLocation());


            Glide.with(getApplicationContext()).load(purchaseRequest.getProduct().getMainImage()).apply(new RequestOptions().placeholder(R.drawable.img_placeholder)).error(R.drawable.image_placeholder).fitCenter().into(productImageV);



            if(purchaseRequest.getStatus().equals("Accepted"))
            {
                statusView.setTextColor(Color.rgb(0, 255, 0));
            }
            else if(purchaseRequest.getStatus().equals("Rejected"))
            {
                statusView.setTextColor(Color.rgb(255, 0, 0));
            }
            else
            {
                statusView.setTextColor(Color.rgb(204, 204, 0));
            }

            //For Call Button
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + purchaseRequest.getProduct().getSeller().getContact()));
                    startActivity(intent);
                }
            });

            //Gets Current User's name if logged in that will be used to sent to other activity
            msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent myintent = new Intent(getApplicationContext(), RequestMessageActivity.class);
                    //myintent.putExtra("productObj", barterProduct);
                    myintent.putExtra("myUserName", purchaseRequest.getSenderName());
                    myintent.putExtra("receiverUID", purchaseRequest.getProduct().getSeller().myUID);
                    startActivity(myintent);
                }
            });


        }
        else //If it is a Barter Request then set the text view fields
        {

            TextView myProductTV, barterProductTV, requestTV;
            myProductTV = findViewById(R.id.my_productTV);
            barterProductTV = findViewById(R.id.barter_productTV);
            requestTV = findViewById(R.id.requestTV);

            requestTV.setText("Barter Request");
            myProductTV.setText("My product:");
            barterProductTV.setText("Barter product:");


            contactTV.setText(barterRequest.getMyProduct().getSeller().getContact());
            sellerTV.setText(barterRequest.getMyProduct().getSeller().getFullname());
            productNameView.setText(barterRequest.getSellerProduct().getName());
            priceView.setText(barterRequest.getMyProduct().getName());
            statusView.setText(barterRequest.getStatus());
            dateView.setText(DateFormat.format("h:mma   dd/MM/yyyy", barterRequest.getDate()));
            locationView.setText(barterRequest.getLocation());


            Glide.with(getApplicationContext()).load(barterRequest.getMyProduct().getMainImage()).apply(new RequestOptions().placeholder(R.drawable.img_placeholder)).error(R.drawable.image_placeholder).fitCenter().into(productImageV);



            //If request is already accepted, then cant click
            if(barterRequest.getStatus().equals("Accepted"))
            {
                statusView.setTextColor(Color.rgb(0, 255, 0));
            }
            //If request is already rejected, then cant click
            else if(barterRequest.getStatus().equals("Rejected"))
            {
                statusView.setTextColor(Color.rgb(255, 0, 0));
            }
            //If request is already default, then can click
            else
            {
                statusView.setTextColor(Color.rgb(204, 204, 0));
            }




            //Open dialer if clicked
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + barterRequest.getMyProduct().getSeller().getContact()));
                    startActivity(intent);
                }
            });

            //If Message button is clicked, open chat
            msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent myintent = new Intent(getApplicationContext(), RequestMessageActivity.class);
                    //myintent.putExtra("productObj", barterProduct);
                    myintent.putExtra("myUserName", barterRequest.getSenderName());
                    myintent.putExtra("receiverUID", barterRequest.getMyProduct().getSeller().myUID);
                    startActivity(myintent);
                }
            });

        }


    }
}
