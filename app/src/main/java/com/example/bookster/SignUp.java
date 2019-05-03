package com.example.bookster;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;


public class SignUp extends AppCompatActivity{
    AlertDialog termsAndConditions;
    EditText fullNameInput,emailInput,passwordInput, retypePassword, contactInput;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private int PICK_PROFILE_PICTURE_REQUEST = 300;
    private StorageReference myStorage;
    private Uri profilePictureUri = null;
    TextView textView;
    Toolbar toolbar;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        textView=findViewById(R.id.loginLinkTextView);
        textView.setTextColor(Color.parseColor("#411FC7"));
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mAuth = FirebaseAuth.getInstance();
        fullNameInput=findViewById(R.id.fullNameEditText);
        contactInput=findViewById(R.id.phoneEditText);
        emailInput=findViewById(R.id.emailAddressEditText);
        passwordInput=findViewById(R.id.passwordEditText);
        retypePassword=findViewById(R.id.retypePasswordEditText);
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.keepSynced(false);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Bookster - SignUp");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //to return to the previous activity (login activity) by finishing this activity
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Cardview button to select or pick a profile picture
        CardView cardView = findViewById(R.id.cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseImage(PICK_PROFILE_PICTURE_REQUEST);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser()!=null){
            //handle logged in user
        }
    }

    public void createUser(View view){
        final String fullname = fullNameInput.getText().toString();
        String retypedPassword = retypePassword.getText().toString();
        final String email=emailInput.getText().toString();
        final String contact =contactInput.getText().toString();
        final String password=passwordInput.getText().toString();
        EditText fullNameET = findViewById(R.id.fullNameEditText);

        //To verify signup fields
        if(fullname.isEmpty()){
            Toast.makeText(getApplicationContext(),"Name is required",Toast.LENGTH_SHORT).show();
            return;
        }

        if(email.isEmpty()){
            Toast.makeText(getApplicationContext(),"Email Address is required",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!isValidEmail(email)){
            Toast.makeText(getApplicationContext(),"Invalid email",Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.isEmpty()){
            Toast.makeText(getApplicationContext(),"Password is required",Toast.LENGTH_SHORT).show();
            return;
        }

        if(retypedPassword.isEmpty()){
            Toast.makeText(getApplicationContext(),"Please re-enter your password",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!password.equals(retypedPassword)){
            Toast.makeText(getApplicationContext(),"Your Passwords Do Not Match",Toast.LENGTH_SHORT).show();
            return;
        }

        if(contact.isEmpty()){
            Toast.makeText(getApplicationContext(),"Contact Number is required",Toast.LENGTH_SHORT).show();
            return;
        }

        if(profilePictureUri==null)
        {
            Toast.makeText(getApplicationContext(),"Select a Profile picture",Toast.LENGTH_SHORT).show();
            return;
        }

        //For Terms And Conditions
        termsAndConditions = new AlertDialog.Builder(SignUp.this).create();
        termsAndConditions.setTitle("TERMS AND CONDITIONS");
        termsAndConditions.setMessage("Welcome to Bookster\n\n" +
                "These terms and conditions outline the rules and regulations for the use of Bookster.\n\n\n" +
                "Product/Item Postings\n\n" +
                "Drugs/illegal substances including any and all related paraphernalia are not allowed.\n\n" +
                "Sexual/inappropriate items and images are not allowed.\n\n" +
                "Product pictures must be accurate and be of the same or an identical product.\n\n" +
                "The creation of multiple postings for the same item is not allowed.\n\n" +
                "Spamming of any kind is not allowed.\n\n" +
                "The use of abusive/hateful/inappropriate language in the chat is not allowed.\n\n\n" +
                "Reservation of Rights\n\n" +
                "We reserve the right at any time and in our sole discretion to remove or to request that you remove all item/product postings or any particular posting on our application. You agree to immediately remove all item/product postings upon such request. We also reserve the right to amend these terms and conditions and its item posting policy at any time. By continuing to post items, you agree to be bound to and abide by these posting terms and conditions.\n" +
                "\n\nDisclaimer\n\n" +
                "Bookster provides a service to be used as is, without guarantee, as such to the maximum extent permitted by applicable law, we exclude all liabilities, representations, warranties and conditions relating to our application and the use of this application (including, without limitation, any warranties implied by law in respect of satisfactory quality). \n" +
                "\n\n" +
                "Safe Zones Disclaimer\n" +
                "\nBookster accepts no liability in any event whether conducting trades in or out of Safe Zones.\n\n" +
                "In order to create an account and fully use Bookster you must agree to the preceding terms and conditions.\n");

        //If the user accept the "terms and conditions", then attempt to create the account and store the info on firebase
        termsAndConditions.setButton(DialogInterface.BUTTON_POSITIVE, "I Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final Button signUpBtn = findViewById(R.id.signUpButton);
                signUpBtn.setVisibility(View.INVISIBLE);

                final ProgressBar progressBar = findViewById(R.id.progress_bar);
                progressBar.setVisibility(View.VISIBLE);
                Toast.makeText(SignUp.this, "Accepted...", Toast.LENGTH_SHORT).show();
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    final String profileImagePath = "ProfilePictures/"+ UUID.randomUUID();
                                    myStorage = FirebaseStorage.getInstance().getReference();
                                    myStorage.child(profileImagePath).putFile(profilePictureUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            myStorage.child(profileImagePath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    //If profile picture has successfully been uploaded, then add the other data to firebase
                                                    User user = new User(
                                                            fullname,
                                                            email,
                                                            contact,
                                                            FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                                            uri.toString()

                                                    );
                                                    mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()){
                                                                        Toast.makeText(getApplicationContext(),"User Created",Toast.LENGTH_SHORT).show();
                                                                        //mAuth.signInWithEmailAndPassword(email, password);
                                                                        finish();
                                                                    }
                                                                    else{
                                                                        Toast.makeText(getApplicationContext(),"Error!!!... Cannot Create User Account" ,Toast.LENGTH_SHORT).show();
                                                                    }

                                                                }
                                                            });
                                                }
                                            });
                                        }
                                    });

                                } else {
                                    // If sign up fails, display a message to the user.
                                    progressBar.setVisibility(View.INVISIBLE);
                                    signUpBtn.setVisibility(View.VISIBLE);
                                    Toast.makeText(getApplicationContext(),"An account already exists with that email",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                dialog.dismiss();
            }
        });

        //If rejected the "terms and conditions"
        termsAndConditions.setButton(DialogInterface.BUTTON_NEGATIVE, "Reject", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(SignUp.this, "You must accept to continue.", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        termsAndConditions.show();

    }

    //To check if email is valid
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    //To start an activity to select or pick a profile picture
    private void chooseImage(int PICK_REQUEST)
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        if(PICK_REQUEST == PICK_PROFILE_PICTURE_REQUEST)
        {
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_PROFILE_PICTURE_REQUEST);
        }
    }

    //To receive the profile picture URI from the activity that selected it
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_PROFILE_PICTURE_REQUEST && resultCode == RESULT_OK && data != null)
        {
            Uri mainImageUrl = data.getData();
            profilePictureUri = mainImageUrl;
            ImageView mainImage = findViewById(R.id.profilePicture);
            mainImage.setImageURI(mainImageUrl);
            TextView cardTV;
            cardTV = findViewById(R.id.cardViewTV);
            cardTV.setText("");
            ScrollView signUpScrollView = findViewById(R.id.signUpScrollV);
            signUpScrollView.fullScroll(View.FOCUS_DOWN);
            Toast.makeText(getApplicationContext(), "Profile Picture Selected", Toast.LENGTH_SHORT).show();
        }
    }
}

