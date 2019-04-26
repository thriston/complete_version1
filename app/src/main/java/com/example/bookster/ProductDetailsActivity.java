package com.example.bookster;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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

public class ProductDetailsActivity extends AppCompatActivity {

    AlertDialog safeZoneDisclaimer, safeZoneDisclaimerBarter, purchaseRequestDialog, barterRequestDialog ;
    ViewPager viewPager;
    ProductPictureSlider adapter;
    private  int REQUEST_CODE =1;
    User myUserProfile;
    String myUserName;
    private DatabaseReference mDatabase;
    private Product product, requestProduct;
    private int count;
    private FirebaseUser user;
    private ArrayList<String> secondaryImages;
    private int REQUEST_NAME = 150;
    private int REQUEST_PRODUCT = 160;


    android.support.v7.widget.Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_page);
        secondaryImages = new ArrayList<>();
        ImageButton infoButton = (ImageButton) findViewById(R.id.viewinfo);
        user = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference mStorage = FirebaseStorage.getInstance().getReference();
        StorageReference pathRef;




        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra("productObj");

        count = 0;
        secondaryImages.add(product.getMainImage());
        if(product.getSecondaryImages() != null)
        {
            for(String path: product.getSecondaryImages())
            {
                //System.out.println("URL: "+mStorage.toString()+path);
                pathRef = mStorage.child(path);
                pathRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri)
                    {
                        count++;
                        System.out.println("URL: "+uri.toString());
                        secondaryImages.add(uri.toString());

                        if(count == product.getSecondaryImages().size())
                        {
                            viewPager = findViewById(R.id.product_img_slider);
                            adapter = new ProductPictureSlider(getApplication(), secondaryImages);
                            viewPager.setAdapter(adapter);
                        }


                    }
                });
            }
        }
        else
        {
            viewPager = findViewById(R.id.product_img_slider);
            adapter = new ProductPictureSlider(getApplication(), secondaryImages);
            viewPager.setAdapter(adapter);
        }

        //System.out.println("MAIN URL: "+product.getMainImage());




        //if viewer is not the owner, add to number of views
        if(user!=null)
        {
            if(!user.getUid().equals(product.getSeller().getMyUID()))
            {
                product.addView();
                updateViews();
            }
        }

        if(user!=null){
            if(user.getUid().equals(product.getSeller().getMyUID())) {
            hideButton();
            }
        }


        myUserProfile = (User) intent.getSerializableExtra("myUserProfile");

        //System.out.println("PRODUCT NAME: "+narnia.getName());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Bookster - "+product.getName());
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //set Texts
        TextView productName = findViewById(R.id.productNameTextView);
        TextView price = findViewById(R.id.productPricetextView);
        TextView category = findViewById(R.id.productCatagoryTextView);
        TextView details = findViewById(R.id.productDetailstextView);
        TextView sellerName = findViewById(R.id.sellerNameTextView);
        TextView stockQty = findViewById(R.id.stockQuantity);
        TextView sellerContact = findViewById(R.id.sellerContactNumTextView);
        TextView sellerEmail = findViewById(R.id.sellerEmailTextView);

        productName.setText(product.getName());
        price.setText("$"+product.getPrice());
        category.setText("Category: "+product.getCategory());
        details.setText(product.getDetails());
        stockQty.setText(product.getQuantity()+" left");
        User userProfile = product.getSeller();
        //System.out.println("SELLER INFO DETAILS: "+product.getDetails());



        //If User is logged in, get username
        if(user != null)
        {
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
        }
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null){
                    Intent myintent = new Intent(ProductDetailsActivity.this, Seller_info.class);
                    myintent.putExtra("productObj", product);
                    startActivityForResult(myintent, 0);

                }
                else{
//                    Intent myintent = new Intent(ProductDetailsActivity.this, Login.class);
                    Toast.makeText(getApplicationContext(), "Please Login To Continue", Toast.LENGTH_SHORT).show();
//                    startActivityForResult(myintent, REQUEST_CODE);
                }

            }
        });



        sellerName.setText(product.getSeller().getFullname());
        sellerContact.setText(product.getSeller().getContact());
        sellerEmail.setText(product.getSeller().getEmail());


        //For Call Button
        ImageButton btn = (ImageButton) findViewById(R.id.callbtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null)
                {
                    Intent intent = new Intent(Intent.ACTION_DIAL);

                    TextView num = findViewById(R.id.sellerContactNumTextView);
                    intent.setData(Uri.parse("tel:" + num.getText() ));
                    startActivity(intent);
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
                    Intent myintent = new Intent(ProductDetailsActivity.this, MessageActivity.class);
                    myintent.putExtra("productObj", product);
                    myintent.putExtra("myUserName", myUserName);
                    myintent.putExtra("myUserProfile",product.getSeller());
                    //System.out.println("CATEGORY: "+categoryList.get(position).getName());
                    startActivityForResult(myintent, REQUEST_NAME);
                }
                else
                {
//                    Intent myintent = new Intent(ProductDetailsActivity.this, Login.class);
                    Toast.makeText(getApplicationContext(), "Please Login To Continue", Toast.LENGTH_SHORT).show();
//                    startActivityForResult(myintent, REQUEST_CODE);
                }

            }
        });




        //Purchase Button Clicked

        Button purchaseBtn = findViewById(R.id.purchaseRequestBtn);
        purchaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchaseRequestDialog.show();
            }
        });

        purchaseRequestDialog = new AlertDialog.Builder(ProductDetailsActivity.this).create();
        purchaseRequestDialog.setTitle("Purchase Request");
        purchaseRequestDialog.setMessage("Do you want to buy this product?");
        purchaseRequestDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Send Purchase Request", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(ProductDetailsActivity.this, "Sending Purchase Request...", Toast.LENGTH_LONG).show();
                // TO PURCHASE REQUESTS
                safeZoneDisclaimer.show();
                //
                dialog.dismiss();;
            }
        });
        purchaseRequestDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ProductDetailsActivity.this, "Canceled", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });





        //Barter Button CLicked

        Button barterBtn = findViewById(R.id.barterRequestBtn);
        barterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                safeZoneDisclaimerBarter.show();
            }
        });

        barterRequestDialog = new AlertDialog.Builder(ProductDetailsActivity.this).create();
        barterRequestDialog .setTitle("Barter Request");
        barterRequestDialog .setMessage("Do you want to barter this product?");
        barterRequestDialog .setButton(DialogInterface.BUTTON_POSITIVE, "Select Product to Barter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(ProductDetailsActivity.this, "Sending Purchase Request...", Toast.LENGTH_LONG).show();
                // TO PURCHASE REQUESTS
                //safeZoneDisclaimer.show();
                //

                Intent myintent = new Intent(getApplicationContext(), MyBarterProductListActivity.class);
                startActivityForResult(myintent, REQUEST_PRODUCT);


                dialog.dismiss();;
            }
        });
        barterRequestDialog .setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ProductDetailsActivity.this, "Canceled", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });








        safeZoneDisclaimer = new AlertDialog.Builder(ProductDetailsActivity.this).create();
        safeZoneDisclaimer.setTitle("Safe Zones");
        safeZoneDisclaimer.setMessage("Your personal safety is important to us at Bookster and as such we have identified places in and around the St Augustine UWI campus which are well known, public and suitable for conducting trade. These areas are called Safe Zones and are there to help you conduct your trades in a safe environment.\n" +
                "\nDisclaimer\n" +
                "While we are careful in identifying Safe Zones, they are merely suggestions, therefore you must always take care to ensure personal safety. Bookster accepts no liability in any event whether or not conducting trades in or out of Safe Zones.\n");
        safeZoneDisclaimer.setButton(DialogInterface.BUTTON_POSITIVE, "I Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ProductDetailsActivity.this, "Accepted...Purchase Request Sent.", Toast.LENGTH_LONG).show();
                // TO PURCHASE REQUESTS

                //Code to send request here
                String requestID = FirebaseDatabase.getInstance().getReference().child("Requests").child("Purchase").push().getKey();
                mDatabase = FirebaseDatabase.getInstance().getReference().child("Requests").child("Purchase").child(requestID);

                PurchaseRequest purchaseRequest = new PurchaseRequest(
                        requestID,
                        "Purchase Request",
                        "Campus Security",
                        "pending",
                        FirebaseAuth.getInstance().getCurrentUser().getUid(),
                        myUserName,
                        product,
                        System.currentTimeMillis()
                );

                mDatabase.setValue(purchaseRequest);

                mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("Requests").child("Purchase").child("sent").child(requestID);
                mDatabase.setValue(purchaseRequest);

                mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(product.getSeller().myUID)
                        .child("Requests").child("Purchase").child("received").child(requestID);
                mDatabase.setValue(purchaseRequest);

                purchaseRequestDialog.show();
                Intent i = new Intent(ProductDetailsActivity.this, MainActivity.class);
                dialog.dismiss();
                startActivity(i);
            }
        });
        safeZoneDisclaimer.setButton(DialogInterface.BUTTON_NEGATIVE, "Reject", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ProductDetailsActivity.this, "You must accept to continue.", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });




        safeZoneDisclaimerBarter = new AlertDialog.Builder(ProductDetailsActivity.this).create();
        safeZoneDisclaimerBarter.setTitle("Safe Zones");
        safeZoneDisclaimerBarter.setMessage("Your personal safety is important to us at Bookster and as such we have identified places in and around the St Augustine UWI campus which are well known, public and suitable for conducting trade. These areas are called Safe Zones and are there to help you conduct your trades in a safe environment.\n" +
                "\nDisclaimer\n" +
                "While we are careful in identifying Safe Zones, they are merely suggestions, therefore you must always take care to ensure personal safety. Bookster accepts no liability in any event whether or not conducting trades in or out of Safe Zones.\n");
        safeZoneDisclaimerBarter.setButton(DialogInterface.BUTTON_POSITIVE, "I Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                barterRequestDialog.show();

                dialog.dismiss();
            }
        });
        safeZoneDisclaimerBarter.setButton(DialogInterface.BUTTON_NEGATIVE, "Reject", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ProductDetailsActivity.this, "You must accept to continue.", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

    }

    public void updateViews()
    {
        DatabaseReference db1,db2;
        db1 = FirebaseDatabase.getInstance().getReference().child("Products").child(product.getID()).child("views");
        db1.setValue(product.getViews());
        db2 = FirebaseDatabase.getInstance().getReference().child("users").child(product.getSeller().myUID).child("Products").child(product.getID()).child("views");
        db2.setValue(product.getViews());
    }

    public void hideButton(){
        final ImageButton messageBtn = findViewById(R.id.msgbtn);
        final ImageButton callBtn = findViewById(R.id.callbtn);
        final ImageButton viewBtn = findViewById(R.id.viewinfo);
        messageBtn.setVisibility(View.INVISIBLE);
        callBtn.setVisibility(View.INVISIBLE);
        viewBtn.setVisibility(View.INVISIBLE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == REQUEST_NAME)
        {
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
        }
        else if(resultCode == RESULT_OK && requestCode == REQUEST_PRODUCT)
        {
            requestProduct = (Product) data.getSerializableExtra("productObj");


            String requestID = FirebaseDatabase.getInstance().getReference().child("Requests").child("Barter").push().getKey();
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Requests").child("Barter").child(requestID);

            BarterRequest barterRequest = new BarterRequest(
                    requestID,
                    "Purchase Request",
                    "Campus Security",
                    "pending",
                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    myUserName,
                    requestProduct,
                    product,
                    System.currentTimeMillis()
            );



            mDatabase.setValue(barterRequest);

            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("Requests").child("Barter").child("sent").child(requestID);
            mDatabase.setValue(barterRequest);

            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(product.getSeller().myUID)
                    .child("Requests").child("Barter").child("received").child(requestID);
            mDatabase.setValue(barterRequest);

            Toast.makeText(ProductDetailsActivity.this, "Barter Request Sent", Toast.LENGTH_LONG).show();




        }
        else
        {
            System.out.println("Activity Canceled");
        }
    }




}
