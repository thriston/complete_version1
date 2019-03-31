package com.example.bookster;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.UUID;

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
    private int count = 0;
    private ArrayList<String> images;
    private StorageReference myStorage;
    private Button mainImageBtn;
    private int PICK_IMAGE_REQUEST = 10;
    private Uri productImage;
    private ArrayList<Uri> productImages = new ArrayList<Uri>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_add_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mainImageBtn = findViewById(R.id.mainImageUpload);
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




        mainImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();

                //myStorage.child(path);
            }
        });


        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ProgressBar progressBar = findViewById(R.id.progress_bar);
                progressBar.setVisibility(View.VISIBLE);
                productID = FirebaseDatabase.getInstance().getReference().child("Products").push().getKey();



                String path = "ProductImages/"+UUID.randomUUID();
                myStorage = FirebaseStorage.getInstance().getReference(path);
                myStorage.putFile(productImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        myStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                Product product = new Product(
                                        productID,
                                        productName.getText().toString(),
                                        price.getText().toString(),
                                        quantity.getText().toString(),
                                        description.getText().toString(),
                                        category,
                                        imageUrl,
                                        myUserProfile);
                                myDatabase.child(productID).setValue(product);
                                //Send information to User's Profile
                                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users")
                                        .child(product.getSeller().myUID).child("Products").child(productID);
                                db.child("id").setValue(productID);
                                db.child("name").setValue(productName.getText().toString());
                                db.child("details").setValue(description.getText().toString());
                                db.child("price").setValue(price.getText().toString());
                                db.child("category").setValue(category);
                                db.child("mainImage").setValue(imageUrl);


                                Toast.makeText(getApplicationContext(),"Product Added", Toast.LENGTH_SHORT).show();
                                finish();


                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("ERROR: "+e.toString());
                    }
                });


//                for(Uri uri: productImages)
//                {
//
//                }



                //productID = FirebaseDatabase.getInstance().getReference().child("Products").push().getKey();

//                Product product = new Product(
//                        productID,
//                        productName.getText().toString(),
//                        price.getText().toString(),
//                        quantity.getText().toString(),
//                        description.getText().toString(),
//                        category,
//                        images,
//                        myUserProfile);
//
//                //Send information to User's Profile
//                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users")
//                        .child(product.getSeller().myUID).child("Products").child(productID);
//                db.child("id").setValue(productID);
//                db.child("name").setValue(productName.getText().toString());
//                db.child("details").setValue(description.getText().toString());
//                db.child("price").setValue(price.getText().toString());
//                db.child("category").setValue(category);
//                db.child("images").setValue(images);
//
//
//                myDatabase.child(productID).setValue(product);
////
////
////                Toast.makeText(getApplicationContext(),"Product Added", Toast.LENGTH_SHORT).show();
////                finish();


            }
        });


        fabCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });





    }

    private void chooseImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK)
        {
            //productImages.add(data.getData());
            Button mainImageBtn = findViewById(R.id.mainImageUpload);
            mainImageBtn.setVisibility(View.GONE);

            productImage = data.getData();
            ImageView mainImage = findViewById(R.id.mainImage);
            mainImage.setImageURI(productImage);

            CardView cardView = findViewById(R.id.cardView);
            cardView.setVisibility(View.VISIBLE);


            TextView tv = findViewById(R.id.mainImageTV);
            tv.setVisibility(View.VISIBLE);

            //mainImage.setLa
            Toast.makeText(getApplicationContext(), "Image Selected", Toast.LENGTH_SHORT).show();
        }
    }
}
