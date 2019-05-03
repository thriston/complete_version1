package com.example.bookster;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PurchaseRequestPage extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ImageView profileImageV, productImageV;
    private android.support.v7.widget.Toolbar toolbar;
    private TextView fullNameView, emailView, contactView, productNameView, priceView, statusView, dateView, locationView;
    private FirebaseUser user;
    private PurchaseRequest purchaseRequest = null;
    private BarterRequest barterRequest = null;
    private ImageButton accept, call, msg, reject;
    private String fullname, email, contact, profilePicURL;
    private String myUserName;
    private boolean isBarter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purchase_page_screen);

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

        //Current user
        user = FirebaseAuth.getInstance().getCurrentUser();

        //Get text views and buttons
        fullNameView=findViewById(R.id.fullNameField);
        emailView=findViewById(R.id.emailField);
        contactView=findViewById(R.id.contactField);

        productNameView=findViewById(R.id.my_product);
        priceView=findViewById(R.id.price);
        statusView=findViewById(R.id.status);
        dateView=findViewById(R.id.date);
        locationView=findViewById(R.id.location);

        profileImageV = findViewById(R.id.profilePicture);
        productImageV = findViewById(R.id.product_image);

        accept = findViewById(R.id.acceptBtn);
        reject = findViewById(R.id.rejectBtn);
        call = findViewById(R.id.callbtn);
        msg = findViewById(R.id.msgbtn);

        //If is a PURCHASE REQUEST
        if(!isBarter)
        {
            mDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(purchaseRequest.getSenderUID());
            mDatabase.keepSynced(true);

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    fullname = dataSnapshot.child("fullname").getValue().toString();
                    email =  dataSnapshot.child("email").getValue().toString();
                    contact = dataSnapshot.child("contact").getValue().toString();
                    profilePicURL = dataSnapshot.child("profilePicURL").getValue().toString();

                    //Set text fields
                    fullNameView.setText(fullname);
                    emailView.setText(email);
                    contactView.setText(contact);
                    productNameView.setText(purchaseRequest.getProduct().getName());
                    priceView.setText(purchaseRequest.getProduct().getPrice());
                    statusView.setText(purchaseRequest.getStatus());
                    dateView.setText(DateFormat.format("h:mma   dd/MM/yyyy", purchaseRequest.getDate()));
                    locationView.setText(purchaseRequest.getLocation());

                    //Set Profile and product pictures
                    Glide.with(getApplicationContext()).load(profilePicURL)
                            .apply(new RequestOptions().placeholder(R.drawable.img_placeholder))
                            .error(R.drawable.image_placeholder).fitCenter().into(profileImageV);
                    Glide.with(getApplicationContext()).load(purchaseRequest.getProduct().getMainImage())
                            .apply(new RequestOptions().placeholder(R.drawable.img_placeholder))
                            .error(R.drawable.image_placeholder)
                            .fitCenter().into(productImageV);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //If request has already been accepted or rejected, then the accept/reject button will no longer be available
            if(purchaseRequest.getStatus().equals("Accepted"))
            {
                statusView.setTextColor(Color.rgb(0, 255, 0));
                accept.setVisibility(View.GONE);
                reject.setVisibility(View.GONE);
            }
            else if(purchaseRequest.getStatus().equals("Rejected"))
            {
                statusView.setTextColor(Color.rgb(255, 0, 0));
                accept.setVisibility(View.GONE);
                reject.setVisibility(View.GONE);
            }
            else
            {
                statusView.setTextColor(Color.rgb(204, 204, 0));
                accept.setVisibility(View.VISIBLE);
                reject.setVisibility(View.VISIBLE);
            }


            //to get my username from firebase
            mDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    myUserName = dataSnapshot.child("fullname").getValue().toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //For Call Button
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + contact));
                    startActivity(intent);
                }
            });

            //Gets Current User's name if logged in that will be used to sent to other activity
            msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent myintent = new Intent(getApplicationContext(), RequestMessageActivity.class);
                    //myintent.putExtra("productObj", barterProduct);
                    myintent.putExtra("myUserName", myUserName);
                    myintent.putExtra("receiverUID", purchaseRequest.getSenderUID());
                    startActivity(myintent);
                }
            });

            //If accept button is clicked, update status of product, the quantity and the number of transactions for that product
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quantity = Integer.parseInt(purchaseRequest.getProduct().getQuantity());
                    if(quantity <= 0)
                    {
                        Toast.makeText(PurchaseRequestPage.this, "Out of Stock", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    purchaseRequest.getProduct().addTransaction();
                    quantity = Integer.parseInt(purchaseRequest.getProduct().getQuantity());
                    if(quantity == 0)
                    {
                        mDatabase = FirebaseDatabase.getInstance().getReference().child("Products").child(purchaseRequest.getProduct().getID()).child("quantity");
                        mDatabase.setValue("OUT OF STOCK");
                    }
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("Requests").child(purchaseRequest.getID()).child("status");
                    mDatabase.setValue("Accepted");
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(purchaseRequest.getSenderUID())
                            .child("Requests").child(purchaseRequest.getID()).child("status");
                    mDatabase.setValue("Accepted");
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Requests")
                            .child(purchaseRequest.getID()).child("status");
                    mDatabase.setValue("Accepted");

                    DatabaseReference db1,db2;
                    db1 = FirebaseDatabase.getInstance().getReference().child("Products").child(purchaseRequest.getProduct().getID()).child("ntransactions");
                    db1.setValue(purchaseRequest.getProduct().getNTransactions());
                    db2 = FirebaseDatabase.getInstance().getReference().child("users").child(purchaseRequest.getProduct().getSeller().myUID).child("Products").child(purchaseRequest.getProduct().getID()).child("nTransactions");
                    db2.setValue(purchaseRequest.getProduct().getNTransactions());

                    db1 = FirebaseDatabase.getInstance().getReference().child("Products").child(purchaseRequest.getProduct().getID()).child("quantity");
                    db1.setValue(purchaseRequest.getProduct().getQuantity());
                    db2 = FirebaseDatabase.getInstance().getReference().child("users").child(purchaseRequest.getProduct().getSeller().myUID).child("Products").child(purchaseRequest.getProduct().getID()).child("quantity");
                    db2.setValue(purchaseRequest.getProduct().getQuantity());

                    mDatabase.keepSynced(true);
                    accept.setVisibility(View.GONE);
                    reject.setVisibility(View.GONE);
                    statusView.setText("Accepted");
                    Toast.makeText(getApplicationContext(), "Accepted Purchase", Toast.LENGTH_SHORT).show();
                }
            });

            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("Requests").child(purchaseRequest.getID()).child("status");
                    mDatabase.setValue("Rejected");

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(purchaseRequest.getSenderUID())
                            .child("Requests").child(purchaseRequest.getID()).child("status");
                    mDatabase.setValue("Rejected");

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Requests")
                            .child(purchaseRequest.getID()).child("status");
                    mDatabase.setValue("Rejected");

                    mDatabase.keepSynced(true);

                    accept.setVisibility(View.GONE);
                    reject.setVisibility(View.GONE);

                    Toast.makeText(getApplicationContext(), "Rejected Purchase", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else //If it is a BARTER REQUEST then set the text view fields
        {
            mDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(barterRequest.getSenderUID());
            mDatabase.keepSynced(true);

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    TextView myProductTV, barterProductTV, requestTV;
                    myProductTV = findViewById(R.id.my_productTV);
                    barterProductTV = findViewById(R.id.barter_productTV);
                    requestTV = findViewById(R.id.requestTV);

                    requestTV.setText("Barter Request");
                    myProductTV.setText("My product:");
                    barterProductTV.setText("Barter product:");


                    fullname = dataSnapshot.child("fullname").getValue().toString();
                    email =  dataSnapshot.child("email").getValue().toString();
                    contact = dataSnapshot.child("contact").getValue().toString();
                    profilePicURL = dataSnapshot.child("profilePicURL").getValue().toString();


                    fullNameView.setText(fullname);
                    emailView.setText(email);
                    contactView.setText(contact);

                    productNameView.setText(barterRequest.getMyProduct().getName());
                    priceView.setText(barterRequest.getSellerProduct().getName());
                    statusView.setText(barterRequest.getStatus());
                    dateView.setText(DateFormat.format("h:mma   dd/MM/yyyy", barterRequest.getDate()));
                    locationView.setText(barterRequest.getLocation());


                    Glide.with(getApplicationContext()).load(profilePicURL).apply(new RequestOptions().placeholder(R.drawable.img_placeholder)).error(R.drawable.image_placeholder).fitCenter().into(profileImageV);
                    Glide.with(getApplicationContext()).load(barterRequest.getSellerProduct().getMainImage()).apply(new RequestOptions().placeholder(R.drawable.img_placeholder)).error(R.drawable.image_placeholder).fitCenter().into(productImageV);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //If request is already accepted, then cant click
            if(barterRequest.getStatus().equals("Accepted"))
            {
                statusView.setTextColor(Color.rgb(0, 255, 0));
                accept.setVisibility(View.GONE);
                reject.setVisibility(View.GONE);
            }
            //If request is already rejected, then cant click
            else if(barterRequest.getStatus().equals("Rejected"))
            {
                statusView.setTextColor(Color.rgb(255, 0, 0));
                accept.setVisibility(View.GONE);
                reject.setVisibility(View.GONE);
            }
            //If request is already default, then can click
            else
            {
                statusView.setTextColor(Color.rgb(204, 204, 0));
                accept.setVisibility(View.VISIBLE);
                reject.setVisibility(View.VISIBLE);
            }


            //get the current user's username from firebase
            mDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    myUserName = dataSnapshot.child("fullname").getValue().toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



            //Open dialer if clicked
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + contact));
                    startActivity(intent);
                }
            });

            //If Message button is clicked, open chat
            msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent myintent = new Intent(getApplicationContext(), RequestMessageActivity.class);
                    //myintent.putExtra("productObj", barterProduct);
                    myintent.putExtra("myUserName", myUserName);
                    myintent.putExtra("receiverUID", barterRequest.getSenderUID());
                    startActivity(myintent);
                }
            });


            //If barter request is accepted, set values on firebase to "accepted", minus from product quantity, and add to nuber of transactions
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("Requests").child(barterRequest.getID()).child("status");
                    mDatabase.setValue("Accepted");

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(barterRequest.getSenderUID())
                            .child("Requests").child(barterRequest.getID()).child("status");
                    mDatabase.setValue("Accepted");

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Requests")
                            .child(barterRequest.getID()).child("status");
                    mDatabase.setValue("Accepted");

                    barterRequest.getMyProduct().addTransaction();
                    barterRequest.getSellerProduct().addTransaction();
                    DatabaseReference db1,db2;

                    //For the Receiver
                    //Requestee's product transaction count increase and quantity decrease
                    db1 = FirebaseDatabase.getInstance().getReference().child("Products").child(barterRequest.getMyProduct().getID()).child("ntransactions");
                    db1.setValue(barterRequest.getMyProduct().getNTransactions());
                    db2 = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Products").child(barterRequest.getMyProduct().getID()).child("nTransactions");
                    db2.setValue(barterRequest.getMyProduct().getNTransactions());


                    db1 = FirebaseDatabase.getInstance().getReference().child("Products").child(barterRequest.getMyProduct().getID()).child("quantity");
                    db1.setValue(barterRequest.getMyProduct().getQuantity());
                    db2 = FirebaseDatabase.getInstance().getReference().child("users").child(barterRequest.getMyProduct().getSeller().myUID).child("Products").child(barterRequest.getMyProduct().getID()).child("quantity");
                    db2.setValue(barterRequest.getMyProduct().getQuantity());


                    //For the requester
                    //Requester's product transaction count increase and quantity decrease
                    db1 = FirebaseDatabase.getInstance().getReference().child("Products").child(barterRequest.getSellerProduct().getID()).child("ntransactions");
                    db1.setValue(barterRequest.getSellerProduct().getNTransactions());
                    db2 = FirebaseDatabase.getInstance().getReference().child("users").child(barterRequest.getSenderUID()).child("Products").child(barterRequest.getSellerProduct().getID()).child("nTransactions");
                    db2.setValue(barterRequest.getSellerProduct().getNTransactions());


                    db1 = FirebaseDatabase.getInstance().getReference().child("Products").child(barterRequest.getSellerProduct().getID()).child("quantity");
                    db1.setValue(barterRequest.getSellerProduct().getQuantity());
                    db2 = FirebaseDatabase.getInstance().getReference().child("users").child(barterRequest.getSenderUID()).child("Products").child(barterRequest.getSellerProduct().getID()).child("quantity");
                    db2.setValue(barterRequest.getSellerProduct().getQuantity());


                    mDatabase.keepSynced(true);

                    accept.setVisibility(View.GONE);
                    reject.setVisibility(View.GONE);

                    statusView.setText("Accepted");
                    Toast.makeText(getApplicationContext(), "Accepted Purchase", Toast.LENGTH_SHORT).show();

                }
            });

            //If barter request is rejected, set value to rejected on the various fields in firebase
            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("Requests").child(barterRequest.getID()).child("status");
                    mDatabase.setValue("Rejected");

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(barterRequest.getSenderUID())
                            .child("Requests").child(barterRequest.getID()).child("status");
                    mDatabase.setValue("Rejected");

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Requests")
                            .child(barterRequest.getID()).child("status");
                    mDatabase.setValue("Rejected");

                    mDatabase.keepSynced(true);

                    accept.setVisibility(View.GONE);
                    reject.setVisibility(View.GONE);

                    Toast.makeText(getApplicationContext(), "Rejected Purchase", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }
}
