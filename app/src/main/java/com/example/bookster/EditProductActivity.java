package com.example.bookster;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditProductActivity extends AppCompatActivity {
    String category;
    FloatingActionButton fabAdd;
    FloatingActionButton fabCancel;
    User myUserProfile;


    private TextInputLayout textInputProductName;
    private TextInputLayout textInputDescription;
    private TextInputLayout textInputPrice;
    private TextInputLayout textInputQuantity;


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
    private boolean changesMadeMain = false;
    private boolean changesMadeSec = false;
    private FirebaseUser user;
    /**Local variables used     **/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.my_product_add_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mainImageBtn = findViewById(R.id.mainImageUpload);
        imagesUrl = new ArrayList<>();
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Edit Product");
        /**Organises the toolbar     **/

        product = (Product) getIntent().getSerializableExtra("productObj");
        category = product.getCategory();
        myStorage = FirebaseStorage.getInstance().getReference();

        fabAdd = findViewById(R.id.fabAdd);
        fabCancel = findViewById(R.id.fabCancel);

        textInputProductName = findViewById(R.id.text_input_product_name);
        textInputDescription = findViewById(R.id.text_input_description);
        textInputPrice = findViewById(R.id.text_input_price);
        textInputQuantity = findViewById(R.id.text_input_quantity);

        allowCalls = findViewById(R.id.allowCallsSwitch);
        myDatabase = FirebaseDatabase.getInstance().getReference().child("Products");
        CardView cardView = findViewById(R.id.cardView);
        CardView cardView1 = findViewById(R.id.cardView1);
        CardView cardView2 = findViewById(R.id.cardView2);
        CardView cardView3 = findViewById(R.id.cardView3);
        CardView cardView4 = findViewById(R.id.cardView4);

        ImageView mainImage = findViewById(R.id.mainImage);

        textInputProductName.getEditText().setText(product.getName());
        textInputDescription.getEditText().setText(product.getDetails());
        textInputPrice.getEditText().setText(product.getPrice());
        textInputQuantity.getEditText().setText(product.getQuantity());

        if(product.getSeller().getContact().equals("Hidden"))
        {
            allowCalls.setChecked(false);
        }
        else {
            allowCalls.setChecked(true);
        }

        Glide.with(this).load(product.getMainImage()).into(mainImage);

        final ArrayList<Integer> resources = new ArrayList<>();
        resources.add(R.id.secImage1);
        resources.add(R.id.secImage2);
        resources.add(R.id.secImage3);
        resources.add(R.id.secImage4);

        count = 0;
        if(product.getSecondaryImages() != null)
        {
            for(String path: product.getSecondaryImages())
            {

                pathRef = FirebaseStorage.getInstance().getReference().child(path);
                pathRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri)
                    {
                        ImageView secondaryImage = findViewById(resources.get(count));
                        count++;
                        Glide.with(getApplicationContext()).load(uri.toString()).into(secondaryImage);


                    }
                });
            }
        }



        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changesMadeMain = true;
                chooseImage(PICK_MAIN_IMAGE_REQUEST);

            }
        });

        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changesMadeSec = true;
                chooseImage(PICK_SEC1_IMAGE_REQUEST);

            }
        });

        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changesMadeSec = true;
                chooseImage(PICK_SEC2_IMAGE_REQUEST);

            }
        });

        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changesMadeSec = true;
                chooseImage(PICK_SEC3_IMAGE_REQUEST);

            }
        });

        cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changesMadeSec = true;
                chooseImage(PICK_SEC4_IMAGE_REQUEST);

            }
        });
        /**Updates images     **/

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null)
        {
            DatabaseReference db;
            db = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            db.keepSynced(false);
            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    myUserProfile = dataSnapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }/**Pulls existing info from firebase   **/


        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(confirmInput())
                {
                    ProgressBar progressBar = findViewById(R.id.progress_bar);
                    progressBar.setVisibility(View.VISIBLE);
                    productID = product.getID();
                    Switch simpleSwitch = (Switch) findViewById(R.id.allowCallsSwitch);

                    /** check current state of a Switch (true or false). **/
                    Boolean switchState = simpleSwitch.isChecked();
                    if(!switchState)
                    {
                        myUserProfile.setContact("Hidden");
                    }

                    final String mainImg = product.getMainImage();
                    final ArrayList<String> secImg = product.getSecondaryImages();

                    if(changesMadeMain && changesMadeSec)
                    {
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
                                                textInputProductName.getEditText().getText().toString(),
                                                textInputPrice.getEditText().getText().toString(),
                                                textInputQuantity.getEditText().getText().toString(),
                                                textInputDescription.getEditText().getText().toString(),
                                                category,
                                                uri.toString(),
                                                secondaryImages,
                                                myUserProfile);
                                        myDatabase.child(productID).setValue(product);
                                        mainUploaded = true;

                                        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Products").child(productID);
                                        db.child("id").setValue(productID);
                                        db.child("name").setValue(textInputProductName.getEditText().getText().toString());
                                        db.child("details").setValue(textInputDescription.getEditText().getText().toString());
                                        db.child("price").setValue(textInputPrice.getEditText().getText().toString());
                                        db.child("quantity").setValue(textInputQuantity.getEditText().getText().toString());
                                        db.child("category").setValue(category);
                                        db.child("mainImage").setValue(uri.toString());
                                        db.child("secondaryImages").setValue(secondaryImages);
                                        db.child("nTransactions").setValue(0);
                                        db.child("views").setValue(0);
                                        db.child("dateCreated").setValue(System.currentTimeMillis());

                                        Toast.makeText(getApplicationContext(),"Changes saved", Toast.LENGTH_SHORT).show();
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                });
                            }
                        });
                        /**Updates New Info on Firebase     **/

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
                                            setResult(RESULT_OK);
                                            secondaryImagesUploaded = true;
                                            if(mainUploaded && secondaryImagesUploaded)
                                                finish();
                                        }
                                    }
                                });
                            }
                        }

                    }
                    /**Stores new photos on Firebase     **/


                    /** Main Image and Secondary image is unchanged    **/
                    else if(!changesMadeMain && !changesMadeSec)
                    {
                        Product product = new Product(
                                productID,
                                textInputProductName.getEditText().getText().toString(),
                                textInputPrice.getEditText().getText().toString(),
                                textInputQuantity.getEditText().getText().toString(),
                                textInputDescription.getEditText().getText().toString(),
                                category,
                                mainImg,
                                secImg,
                                myUserProfile);
                        myDatabase.child(productID).setValue(product);
                        mainUploaded = true;

                        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Products").child(productID);
                        db.child("id").setValue(productID);
                        db.child("name").setValue(textInputProductName.getEditText().getText().toString());
                        db.child("details").setValue(textInputDescription.getEditText().getText().toString());
                        db.child("price").setValue(textInputPrice.getEditText().getText().toString());
                        db.child("quantity").setValue(textInputQuantity.getEditText().getText().toString());
                        db.child("category").setValue(category);
                        db.child("mainImage").setValue(mainImg);
                        db.child("secondaryImages").setValue(secImg);
                        db.child("nTransactions").setValue(0);
                        db.child("views").setValue(0);
                        db.child("dateCreated").setValue(System.currentTimeMillis());

                        Toast.makeText(getApplicationContext(),"Changes made", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();

                    }


                    /**  If MainImage Changed Alone   **/
                    else if(changesMadeMain && !changesMadeSec)
                    {
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
                                                textInputProductName.getEditText().getText().toString(),
                                                textInputPrice.getEditText().getText().toString(),
                                                textInputQuantity.getEditText().getText().toString(),
                                                textInputDescription.getEditText().getText().toString(),
                                                category,
                                                uri.toString(),
                                                secImg,
                                                myUserProfile);
                                        myDatabase.child(productID).setValue(product);
                                        mainUploaded = true;

                                        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Products").child(productID);
                                        db.child("id").setValue(productID);
                                        db.child("name").setValue(textInputProductName.getEditText().getText().toString());
                                        db.child("details").setValue(textInputDescription.getEditText().getText().toString());
                                        db.child("price").setValue(textInputPrice.getEditText().getText().toString());
                                        db.child("quantity").setValue(textInputQuantity.getEditText().getText().toString());
                                        db.child("category").setValue(category);
                                        db.child("mainImage").setValue(uri.toString());
                                        db.child("secondaryImages").setValue(secImg);
                                        db.child("nTransactions").setValue(0);
                                        db.child("views").setValue(0);
                                        db.child("dateCreated").setValue(System.currentTimeMillis());

                                        Toast.makeText(getApplicationContext(),"Changes saved", Toast.LENGTH_SHORT).show();
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                });
                            }
                        });

                        //end if
                    }



                    /**If Secondary Images Changed ALone     **/
                    else if(changesMadeSec && !changesMadeMain)
                    {

                        count = 0;
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
                                        System.out.println("Count: "+count+"    Size: "+productImages.size());
                                        if(count == productImages.size())
                                        {
                                            Product product = new Product(
                                                    productID,
                                                    textInputProductName.getEditText().getText().toString(),
                                                    textInputPrice.getEditText().getText().toString(),
                                                    textInputQuantity.getEditText().getText().toString(),
                                                    textInputDescription.getEditText().getText().toString(),
                                                    category,
                                                    mainImg,
                                                    secondaryImages,
                                                    myUserProfile);
                                            myDatabase.child(productID).setValue(product);
                                            mainUploaded = true;

                                            DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users")
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Products").child(productID);
                                            db.child("id").setValue(productID);
                                            db.child("name").setValue(textInputProductName.getEditText().getText().toString());
                                            db.child("details").setValue(textInputDescription.getEditText().getText().toString());
                                            db.child("price").setValue(textInputPrice.getEditText().getText().toString());
                                            db.child("quantity").setValue(textInputQuantity.getEditText().getText().toString());
                                            db.child("category").setValue(category);
                                            db.child("mainImage").setValue(mainImg);
                                            db.child("secondaryImages").setValue(secondaryImages);
                                            db.child("nTransactions").setValue(0);
                                            db.child("views").setValue(0);
                                            db.child("dateCreated").setValue(System.currentTimeMillis());

                                            Toast.makeText(getApplicationContext(),"Changes Saved", Toast.LENGTH_SHORT).show();
                                            setResult(RESULT_OK);
                                            finish();
                                        }
                                    }
                                });
                            }
                        }

                        //end if
                    }
                }

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


    /**All methods are the same as the Add Product Activity**/
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

    }


    private boolean validateProductName()
    {
        String productNameInput = textInputProductName.getEditText().getText().toString().trim();
        if(productNameInput.isEmpty())
        {
            textInputProductName.setError("Name can't be empty");
            return false;
        }
        else
        if(productNameInput.length() > 40)
        {
            textInputProductName.setError("Name too large");
            return false;
        }
        else
        {
            textInputProductName.setError(null);
            return true;
        }
    }

    private boolean validateDescription()
    {
        String descriptionInput = textInputDescription.getEditText().getText().toString().trim();
        if(descriptionInput.isEmpty())
        {
            textInputDescription.setError("Description can't be empty");
            return false;
        }
        else
        if(descriptionInput.length() > 150)
        {
            textInputDescription.setError("Description too large");
            return false;
        }
        else
        {
            textInputDescription.setError(null);
            return true;
        }
    }

    private boolean validatePrice()
    {
        String priceInput = textInputPrice.getEditText().getText().toString().trim();
        if(priceInput.isEmpty())
        {
            textInputPrice.setError("Price is empty");
            return true;
        }
        else
        if(priceInput.length() > 6)
        {
            textInputPrice.setError("Number too large");
            return false;
        }
        else
        {
            textInputPrice.setError(null);
            return true;
        }
    }

    private boolean validateQuantity()
    {
        String quantityInput = textInputQuantity.getEditText().getText().toString().trim();
        if(quantityInput.isEmpty())
        {
            textInputQuantity.setError("Quantity cant be empty");
            return false;
        }
        else
        if(quantityInput.length() > 4)
        {
            textInputQuantity.setError("Number too large");
            return false;
        }
        else
        {
            textInputQuantity.setError(null);
            return true;
        }
    }

    public boolean confirmInput()
    {
        if(!validateProductName() | !validateDescription() | !validatePrice() | !validateQuantity())
        {
            return false;
        }
        return true;
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


    }
}
