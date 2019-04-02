package com.example.bookster;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
    String productID;
    DatabaseReference myDatabase;
    Toolbar toolbar;
    private boolean mainImageUploaded = false;
    private boolean secondaryImagesUploaded  = false;
    private Map<String,Uri> productImages = new HashMap<String, Uri>();
    private ArrayList<String> secondaryImages = new ArrayList<>();
    private Product product;
    private int count = 0;
    private ArrayList<String> images;
    private StorageReference myStorage;
    private StorageReference pathRef;
    private Button mainImageBtn;

    private int PICK_MAIN_IMAGE_REQUEST = 10;
    private int PICK_SEC1_IMAGE_REQUEST = 11;
    private int PICK_SEC2_IMAGE_REQUEST = 12;
    private int PICK_SEC3_IMAGE_REQUEST = 13;
    private int PICK_SEC4_IMAGE_REQUEST = 14;


    private String key;
    private int PICK_MULTIPLE_IMAGE_REQUEST = 20;
    private Uri productImage;
    private ArrayList<String> imagesUrl; //the html image url
    private String mainImageUrl;
    private boolean mainUploaded = false;
    private TextView cardTV;
    //private ArrayList<Uri> productImages;
    //private ArrayList<Uri> productImages = new ArrayList<Uri>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.product_add_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mainImageBtn = findViewById(R.id.mainImageUpload);
        imagesUrl = new ArrayList<>();
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Add Product");
        category = getIntent().getStringExtra("category");
        myUserProfile = (User) getIntent().getSerializableExtra("myUserProfile");
        myStorage = FirebaseStorage.getInstance().getReference();

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
        CardView cardView = findViewById(R.id.cardView);
        CardView cardView1 = findViewById(R.id.cardView1);
        CardView cardView2 = findViewById(R.id.cardView2);
        CardView cardView3 = findViewById(R.id.cardView3);
        CardView cardView4 = findViewById(R.id.cardView4);





        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseImage(PICK_MAIN_IMAGE_REQUEST);

            }
        });

        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseImage(PICK_SEC1_IMAGE_REQUEST);

            }
        });

        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseImage(PICK_SEC2_IMAGE_REQUEST);

            }
        });

        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseImage(PICK_SEC3_IMAGE_REQUEST);

            }
        });

        cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseImage(PICK_SEC4_IMAGE_REQUEST);

            }
        });




        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ProgressBar progressBar = findViewById(R.id.progress_bar);
                progressBar.setVisibility(View.VISIBLE);
                productID = FirebaseDatabase.getInstance().getReference().child("Products").push().getKey();




                //System.out.println("MAP SIZE: "+productImages.size());
//                for(int i=0; i < productImages.size(); i++)
//                {
//
//                    System.out.println("IMAGE: "+productImages.get(""+i));
//                }

                productImage = productImages.get("0");
                final String mainImagePath = "ProductImages/"+ UUID.randomUUID();
                myStorage.child(mainImagePath).putFile(productImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        myStorage.child(mainImagePath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Product product = new Product(
                                    productID,
                                    productName.getText().toString(),
                                    price.getText().toString(),
                                    quantity.getText().toString(),
                                    description.getText().toString(),
                                    category,
                                    uri.toString(),
                                    secondaryImages,
                                    myUserProfile);
                                myDatabase.child(productID).setValue(product);
                                mainUploaded = true;

                                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users")
                                        .child(product.getSeller().myUID).child("Products").child(productID);
                                db.child("id").setValue(productID);
                                db.child("name").setValue(productName.getText().toString());
                                db.child("details").setValue(description.getText().toString());
                                db.child("price").setValue(price.getText().toString());
                                db.child("quantity").setValue(quantity.getText().toString());
                                db.child("category").setValue(category);
                                db.child("mainImage").setValue(uri.toString());
                                db.child("secondaryImages").setValue(secondaryImages);
                                db.child("bids").setValue(0);
                                db.child("views").setValue(0);

                                Toast.makeText(getApplicationContext(),"Product Added", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                });



                for(Map.Entry<String, Uri> entry : productImages.entrySet()) {
                    String path = "ProductImages/"+ UUID.randomUUID();
                    key = entry.getKey();
                    Uri url = entry.getValue();
                    if(key != "0")
                    {
                        secondaryImages.add(path);
                        myStorage.child(path).putFile(url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                count++;
                                if(count == productImages.size()-1)
                                {
                                    secondaryImagesUploaded = true;
                                    if(mainUploaded && secondaryImagesUploaded)
                                        finish();
                                }
                            }
                        });
                    }
                }







//                if(productImages.size() >= 1)
//                {
////                    for(Uri secondaryImageUrl: productImages)
////                    {
////                        myStorage.putFile(secondaryImageUrl);
//
//                    myStorage.putFile(productImages.get(0));
//                    myStorage.putFile(productImages.get(1));
//                    myStorage.putFile(productImages.get(2));
//                    myStorage.putFile(productImages.get(3));
//                }





//                for(Uri uri: productImages)
//                {
//
//                }



//                //productID = FirebaseDatabase.getInstance().getReference().child("Products").push().getKey();
//
//                Product product = new Product(
//                        productID,
//                        productName.getText().toString(),
//                        price.getText().toString(),
//                        quantity.getText().toString(),
//                        description.getText().toString(),
//                        category,
//                        mainImageUrl,
//                        imagesUrl,
//                        myUserProfile);
//
//                myDatabase.child(productID).setValue(product);
//                //Send information to User's Profile
//                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users")
//                        .child(product.getSeller().myUID).child("Products").child(productID);
//                db.child("id").setValue(productID);
//                db.child("name").setValue(productName.getText().toString());
//                db.child("details").setValue(description.getText().toString());
//                db.child("price").setValue(price.getText().toString());
//                db.child("category").setValue(category);
                //db.child("images").setValue(images);


                //myDatabase.child(productID).setValue(product);
//
//
//                Toast.makeText(getApplicationContext(),"Product Added", Toast.LENGTH_SHORT).show();
//                finish();


            }
        });


        fabCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });





    }


    private synchronized ArrayList<String> getDownloadUrls(final ArrayList<String> secondaryImages)
    {
        count = 0;
        for(String path: secondaryImages)
        {
            pathRef = myStorage.child(path);
            pathRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    count++;
                    imagesUrl.add(uri.toString());
                }
            });
        }

        return imagesUrl;
    }



    private void chooseImage(int PICK_REQUEST)
    {

        Intent intent = new Intent();
        intent.setType("image/*");
        if(PICK_REQUEST == PICK_MULTIPLE_IMAGE_REQUEST)
        {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_MULTIPLE_IMAGE_REQUEST);
        }
        else if(PICK_REQUEST == PICK_MAIN_IMAGE_REQUEST)
        {
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_MAIN_IMAGE_REQUEST);
        }
        else if(PICK_REQUEST == PICK_SEC1_IMAGE_REQUEST)
        {
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_SEC1_IMAGE_REQUEST);
        }
        else if(PICK_REQUEST == PICK_SEC2_IMAGE_REQUEST)
        {
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_SEC2_IMAGE_REQUEST);
        }
        else if(PICK_REQUEST == PICK_SEC3_IMAGE_REQUEST)
        {
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_SEC3_IMAGE_REQUEST);
        }
        else if(PICK_REQUEST == PICK_SEC4_IMAGE_REQUEST)
        {
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_SEC4_IMAGE_REQUEST);
        }
//        else
//        {
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(intent, PICK_MAIN_IMAGE_REQUEST);
//        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_MAIN_IMAGE_REQUEST && resultCode == RESULT_OK && data != null)
        {
            Uri mainImageUrl = data.getData();
            productImages.put("0", mainImageUrl);

            //productImages.set(0, data.getData());
            ImageView mainImage = findViewById(R.id.mainImage);
            mainImage.setImageURI(mainImageUrl);
            cardTV = findViewById(R.id.cardViewTV);
            cardTV.setText("");

            TextView secImageTV = findViewById(R.id.secImageTV);
            secImageTV.setVisibility(View.VISIBLE);

            LinearLayout L1 = findViewById(R.id.secLinear);
            L1.setVisibility(View.VISIBLE);

            LinearLayout L2 = findViewById(R.id.secLinear2);
            L2.setVisibility(View.VISIBLE);

            ScrollView addProductSV = findViewById(R.id.addProductScrollV);
            addProductSV.fullScroll(View.FOCUS_DOWN);

            Toast.makeText(getApplicationContext(), "Image Selected", Toast.LENGTH_SHORT).show();
        }
        else if(requestCode == PICK_SEC1_IMAGE_REQUEST && resultCode == RESULT_OK && data != null)
        {
            Uri image = data.getData();
            productImages.put("1", image);
            ImageView secImage = findViewById(R.id.secImage1);
            secImage.setImageURI(image);
            cardTV = findViewById(R.id.cardViewTV1);
            cardTV.setText("");
            Toast.makeText(getApplicationContext(), "Image Selected", Toast.LENGTH_SHORT).show();
        }
        else if(requestCode == PICK_SEC2_IMAGE_REQUEST && resultCode == RESULT_OK && data != null)
        {
            Uri image = data.getData();
            productImages.put("2", image);
            ImageView secImage = findViewById(R.id.secImage2);
            secImage.setImageURI(image);
            cardTV = findViewById(R.id.cardViewTV2);
            cardTV.setText("");
            Toast.makeText(getApplicationContext(), "Image Selected", Toast.LENGTH_SHORT).show();
        }
        else if(requestCode == PICK_SEC3_IMAGE_REQUEST && resultCode == RESULT_OK && data != null)
        {
            Uri image = data.getData();
            productImages.put("3", image);
            ImageView secImage = findViewById(R.id.secImage3);
            secImage.setImageURI(image);
            cardTV = findViewById(R.id.cardViewTV3);
            cardTV.setText("");
            Toast.makeText(getApplicationContext(), "Image Selected", Toast.LENGTH_SHORT).show();
        }
        else if(requestCode == PICK_SEC4_IMAGE_REQUEST && resultCode == RESULT_OK && data != null)
        {
            Uri image = data.getData();
            productImages.put("4", image);
            ImageView secImage = findViewById(R.id.secImage4);
            secImage.setImageURI(image);
            cardTV = findViewById(R.id.cardViewTV4);
            cardTV.setText("");
            Toast.makeText(getApplicationContext(), "Image Selected", Toast.LENGTH_SHORT).show();
        }
//        else if(requestCode == PICK_MULTIPLE_IMAGE_REQUEST && resultCode == RESULT_OK && data.getClipData() != null)
//        {
//            ClipData mClipData = data.getClipData();
//            for(int i = 0; i < mClipData.getItemCount(); i++)
//            {
//                ClipData.Item item = mClipData.getItemAt(i);
//                productImages.add(item.getUri());
//            }
//
//            Toast.makeText(getApplicationContext(), "Images Selected", Toast.LENGTH_SHORT).show();
//        }

    }
}
