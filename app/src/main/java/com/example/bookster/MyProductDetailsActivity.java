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
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

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

public class MyProductDetailsActivity extends AppCompatActivity {

    ViewPager viewPager;
    ProductPictureSlider adapter;
    private  int REQUEST_CODE =1;
    User myUserProfile;
    String myUserName;
    DatabaseReference catRef;
    private DatabaseReference mDatabase;
    private Product product;
    private int count;
    private FirebaseUser user;
    private ArrayList<String> secondaryImages;
    private RatingBar rating;
    private TextView nRatingC;
    private int REQUEST_EDIT = 150;

    Toolbar toolbar;

    //Get current user username
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode != REQUEST_EDIT)
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
        if(resultCode == RESULT_OK && requestCode == REQUEST_EDIT)
        {
            finish();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_product_page);
        secondaryImages = new ArrayList<>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference mStorage = FirebaseStorage.getInstance().getReference();
        StorageReference pathRef;

        //Configure toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        //toolbar.setTitle("Bookster - "+product.getName());
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
        product = (Product) intent.getSerializableExtra("productObj");
        myUserProfile = (User) intent.getSerializableExtra("myUserProfile");

        //Loads image slider with images from product
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


        //set Texts
        TextView productName = findViewById(R.id.productNameTextView);
        TextView price = findViewById(R.id.productPricetextView);
        TextView category = findViewById(R.id.productCatagoryTextView);
        TextView details = findViewById(R.id.productDetailstextView);
        TextView stockQty = findViewById(R.id.stockQuantity);
        TextView views = findViewById(R.id.nViews);
        TextView nTransactions = findViewById(R.id.nTransactions);
        TextView dateCreated = findViewById(R.id.dateCreated);
        rating = findViewById(R.id.rating);
        nRatingC = findViewById(R.id.nRating);

        //To set the ratings on the owner's product page
        DatabaseReference rateDB = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        rateDB.keepSynced(false);
        rateDB.addListenerForSingleValueEvent(new ValueEventListener() {
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


        //Set texts
        productName.setText(product.getName());
        price.setText("$"+product.getPrice());
        category.setText("Category: "+product.getCategory());
        details.setText(product.getDetails());
        stockQty.setText(product.getQuantity());
        views.setText(product.getViews()+"");
        nTransactions.setText(product.getNTransactions()+"");
        dateCreated.setText(DateFormat.format("d-M-yyyy", product.getDateCreated())+" (dd-mm-yyyyy)");

        //Get button references
        ImageButton editProductBtn = findViewById(R.id.editProductInfo);
        ImageButton deleteProductBtn = findViewById(R.id.deleteProduct);

        //Opens activity to edit product if the user clicks on the editProduct button
        editProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(getApplication(), EditProductActivity.class);
                myintent.putExtra("productObj",product);
                startActivityForResult(myintent, REQUEST_EDIT);
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Products").child(product.getID());


        //Updates the number of items if the product is deleted and makes that product inactive
        deleteProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogevent();
                catRef = FirebaseDatabase.getInstance().getReference();
                catRef = catRef.child("Category");
                catRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren())
                        {
                            if(ds.getKey().equals(product.getCategory()))
                            {
                                String val = ds.child("nItems").getValue().toString();
                                int valInt = Integer.parseInt(val);
                                FirebaseDatabase.getInstance().getReference().child("Category").child(product.getCategory()).child("nItems").setValue((valInt-1)+"");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
    }

    //called if the user clicks the delete button
    public void dialogevent(){
        AlertDialog.Builder altdial = new AlertDialog.Builder(MyProductDetailsActivity.this);
        altdial.setMessage("Are you sure you want to delete this product?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDatabase.child("active").setValue(false);
                        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("Products").child(product.getID());
                        mDatabase.child("active").setValue(false);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = altdial.create();
        alert.setTitle("Delete");
        alert.show();
    }
}
