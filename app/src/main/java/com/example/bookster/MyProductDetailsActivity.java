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

    Toolbar toolbar;

    //Get current user username
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK )
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
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_product_page);
        secondaryImages = new ArrayList<>();
        ImageButton button = (ImageButton) findViewById(R.id.viewinfo);
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
        TextView stockQty = findViewById(R.id.stockQuantity);
        TextView views = findViewById(R.id.nViews);
        TextView nTransactions = findViewById(R.id.nTransactions);
        TextView dateCreated = findViewById(R.id.dateCreated);

        productName.setText(product.getName());
        price.setText("$"+product.getPrice());
        category.setText("Category: "+product.getCategory());
        details.setText(product.getDetails());
        stockQty.setText(product.getQuantity());
        views.setText(product.getViews()+"");
        nTransactions.setText(product.getNTransactions()+"");
        dateCreated.setText(DateFormat.format("d-M-yyyy", product.getDateCreated())+" (dd-mm-yyyyy)");

        ImageButton editProductBtn = findViewById(R.id.editProductInfo);
        ImageButton deleteProductBtn = findViewById(R.id.deleteProduct);
        ImageButton viewProfileBtn = findViewById(R.id.my_view_info);


        editProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(getApplication(), EditProductActivity.class);
                myintent.putExtra("productObj",product);
                startActivityForResult(myintent, 0);
            }
        });

        viewProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(getApplicationContext(), Profile.class);
                //i.putExtra("receiverUID", receiverUID);
                //startActivity(i);
                //finish();
                Intent i=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Products").child(product.getID());
        //mDatabase.keepSynced(true);

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

//                                        System.out.println("NUMBER ITEMS: "+dataSnapshot.child("Description").getValue());
//
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });






    }


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
