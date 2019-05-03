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
import android.widget.RatingBar;
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

    private AlertDialog safeZoneDisclaimer, safeZoneDisclaimerBarter, purchaseRequestDialog, barterRequestDialog ;
    private ViewPager viewPager;
    private ProductPictureSlider adapter;
    private  int REQUEST_CODE =1;
    private User myUserProfile;
    private String myUserName;
    private DatabaseReference mDatabase;
    private Product product, requestProduct;
    private int count;
    private FirebaseUser user;
    private ArrayList<String> secondaryImages;
    private int REQUEST_NAME = 150;
    private int REQUEST_PRODUCT = 160;
    private RatingBar rating;
    private TextView nRatingC;
    private android.support.v7.widget.Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_page);
        secondaryImages = new ArrayList<>();
        ImageButton infoButton = (ImageButton) findViewById(R.id.viewinfo);
        user = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference mStorage = FirebaseStorage.getInstance().getReference();
        StorageReference pathRef;

        //Receive data from parent activity
        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra("productObj");
        myUserProfile = (User) intent.getSerializableExtra("myUserProfile");

        //Configure toolbar
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

        //Adds secondary images to image slider
        count = 0;
        secondaryImages.add(product.getMainImage());
        if(product.getSecondaryImages() != null)
        {
            for(String path: product.getSecondaryImages())
            {
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

        //set Texts
        TextView productName = findViewById(R.id.productNameTextView);
        TextView price = findViewById(R.id.productPricetextView);
        TextView category = findViewById(R.id.productCatagoryTextView);
        TextView details = findViewById(R.id.productDetailstextView);
        TextView sellerName = findViewById(R.id.sellerNameTextView);
        TextView stockQty = findViewById(R.id.stockQuantity);
        TextView sellerContact = findViewById(R.id.sellerContactNumTextView);
        TextView sellerEmail = findViewById(R.id.sellerEmailTextView);
        rating = findViewById(R.id.rating);
        nRatingC = findViewById(R.id.nRating);
        productName.setText(product.getName());
        price.setText("$"+product.getPrice());
        category.setText("Category: "+product.getCategory());
        details.setText(product.getDetails());
        stockQty.setText(product.getQuantity()+" left");

        final User userProfile = product.getSeller();

        //To set the ratings on the product page
        DatabaseReference rateDB = FirebaseDatabase.getInstance().getReference().child("users").child(product.getSeller().getMyUID());
        rateDB.keepSynced(false);
        rateDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int  ratingCount, nRating;
                nRating = 0;
                float sumRating = 0;
                ratingCount = Integer.parseInt(dataSnapshot.child("ratingCount").getValue().toString());
                if(ratingCount > 0)
                {
                    float average;
                    for(DataSnapshot ds: dataSnapshot.child("ratings").getChildren())
                    {
                        nRating++;
                        Rating rating = (Rating) ds.getValue(Rating.class);
                        sumRating = sumRating + Float.parseFloat(rating.getRating());
                    }
                    average = sumRating / nRating;
                    rating.setRating(average);
                    nRatingC.setText(nRating+"");
                }
                else
                if(ratingCount == 0)
                {
                    rating.setRating(0);
                    nRatingC.setText("none");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

        //If the profile info button is clicked on product page
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null){
                    Intent myintent = new Intent(ProductDetailsActivity.this, Seller_info.class);
                    myintent.putExtra("productObj", product);
                    myintent.putExtra("myUserName", myUserName);
                    startActivityForResult(myintent, 0);

                }
                else{
                    Toast.makeText(getApplicationContext(), "Please Login To Continue", Toast.LENGTH_SHORT).show();
                }

            }
        });


        //Set texts
        sellerName.setText(product.getSeller().getFullname());
        sellerContact.setText(product.getSeller().getContact());
        sellerEmail.setText(product.getSeller().getEmail());


        //If call button is clicked then open dialer
        ImageButton btn = (ImageButton) findViewById(R.id.callbtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null)
                {
                    if(product.getSeller().getContact().equals("Hidden"))
                    {
                        Toast.makeText(ProductDetailsActivity.this, "Seller's Contact Number is not Available", Toast.LENGTH_SHORT).show();
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
                    Intent myintent = new Intent(ProductDetailsActivity.this, MessageActivity.class);
                    myintent.putExtra("productObj", product);
                    myintent.putExtra("myUserName", myUserName);
                    myintent.putExtra("myUserProfile",product.getSeller());
                    startActivityForResult(myintent, REQUEST_NAME);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please Login To Continue", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //Display confirmations if purchase button is clicked
        Button purchaseBtn = findViewById(R.id.purchaseRequestBtn);
        purchaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = FirebaseAuth.getInstance().getCurrentUser();

                if(user != null)
                {
                    if(Integer.parseInt(product.getQuantity()) <= 0)
                    {
                        Toast.makeText(ProductDetailsActivity.this, "OUT OF STOCK", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        safeZoneDisclaimer.show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please Login To Purchase", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //For Purchase request disclaimer
        safeZoneDisclaimer = new AlertDialog.Builder(ProductDetailsActivity.this).create();
        safeZoneDisclaimer.setTitle("Safe Zones");
        safeZoneDisclaimer.setMessage("Your personal safety is important to us at Bookster and as such we have identified places in and around the St Augustine UWI campus which are well known, public and suitable for conducting trade. These areas are called Safe Zones and are there to help you conduct your trades in a safe environment.\n" +
                "\nDisclaimer\n" +
                "While we are careful in identifying Safe Zones, they are merely suggestions, therefore you must always take care to ensure personal safety. Bookster accepts no liability in any event whether or not conducting trades in or out of Safe Zones.\n");
        safeZoneDisclaimer.setButton(DialogInterface.BUTTON_POSITIVE, "I Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                purchaseRequestDialog.show();
                dialog.dismiss();
            }
        });
        safeZoneDisclaimer.setButton(DialogInterface.BUTTON_NEGATIVE, "Reject", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ProductDetailsActivity.this, "You must accept to continue.", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        //If accepts purchase request disclaimer then show purchaseRequestDialog and add the request to firebase
        purchaseRequestDialog = new AlertDialog.Builder(ProductDetailsActivity.this).create();
        purchaseRequestDialog.setTitle("Purchase Request");
        purchaseRequestDialog.setMessage("Do you want to buy this product?");
        purchaseRequestDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Send Purchase Request", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ProductDetailsActivity.this, "Purchase Request Sent", Toast.LENGTH_LONG).show();
                String requestID = FirebaseDatabase.getInstance().getReference().child("Requests").push().getKey();
                mDatabase = FirebaseDatabase.getInstance().getReference().child("Requests").child(requestID);
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
                        .child("Requests").child(requestID);
                mDatabase.setValue(purchaseRequest);
                mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(product.getSeller().myUID)
                        .child("Requests").child(requestID);
                mDatabase.setValue(purchaseRequest);
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


        //IF barter Button CLicked then basically do the same as the purchase button above with the exception of choosing a product to barter for
        Button barterBtn = findViewById(R.id.barterRequestBtn);
        barterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null)
                {
                    if(Integer.parseInt(product.getQuantity()) <= 0)
                    {
                        Toast.makeText(ProductDetailsActivity.this, "OUT OF STOCK", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        safeZoneDisclaimerBarter.show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please Login To Barter", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Shown when the user clicks the accept button
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

        //If safe zone disclaimer is accepted then allow the user to choose a product that they own
        barterRequestDialog = new AlertDialog.Builder(ProductDetailsActivity.this).create();
        barterRequestDialog .setTitle("Barter Request");
        barterRequestDialog .setMessage("Do you want to barter this product?");
        barterRequestDialog .setButton(DialogInterface.BUTTON_POSITIVE, "Select Product to Barter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

    }

    //Add a "view" to the product if the the viewer is not the owner him/herself
    public void updateViews()
    {
        DatabaseReference db1,db2;
        db1 = FirebaseDatabase.getInstance().getReference().child("Products").child(product.getID()).child("views");
        db1.setValue(product.getViews());
        db2 = FirebaseDatabase.getInstance().getReference().child("users").child(product.getSeller().myUID).child("Products").child(product.getID()).child("views");
        db2.setValue(product.getViews());
    }

    //Hide the Image buttons if its the owner of the product
    public void hideButton(){
        final ImageButton messageBtn = findViewById(R.id.msgbtn);
        final ImageButton callBtn = findViewById(R.id.callbtn);
        final ImageButton viewBtn = findViewById(R.id.viewinfo);
        messageBtn.setVisibility(View.INVISIBLE);
        callBtn.setVisibility(View.INVISIBLE);
        viewBtn.setVisibility(View.INVISIBLE);
    }

    //Returns the selected barter product if the barter request button is clicked and also returns the current user's username
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
            String requestID = FirebaseDatabase.getInstance().getReference().child("Requests").push().getKey();
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Requests").child(requestID);
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
                    .child("Requests").child(requestID);
            mDatabase.setValue(barterRequest);
            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(product.getSeller().myUID)
                    .child("Requests").child(requestID);
            mDatabase.setValue(barterRequest);
            Toast.makeText(ProductDetailsActivity.this, "Barter Request Sent", Toast.LENGTH_LONG).show();
        }
        else
        {
            System.out.println("Activity Canceled");
        }
    }
}
