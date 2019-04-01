package com.example.bookster;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUp extends AppCompatActivity{
    EditText fullNameInput,emailInput,passwordInput, retypePassword, contactInput;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    TextView textView;
    Toolbar toolbar;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        textView=findViewById(R.id.loginLinkTextView);
        mAuth = FirebaseAuth.getInstance();
        fullNameInput=findViewById(R.id.fullNameEditText);
        contactInput=findViewById(R.id.phoneEditText);
        emailInput=findViewById(R.id.emailAddressEditText);
        passwordInput=findViewById(R.id.passwordEditText);
        retypePassword=findViewById(R.id.retypePasswordEditText);
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.keepSynced(true);

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


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(getApplicationContext(),Login.class);
                startActivity(i);
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
        String password=passwordInput.getText().toString();

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

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //Store Additionl data in firebase database
                                User user = new User(
                                    fullname,
                                    email,
                                    contact,
                                    FirebaseAuth.getInstance().getCurrentUser().getUid()

                            );
//                            UserProfile user = new UserProfile(
//                                    fullname,
//                                    email,
//                                    contact,
//                                    FirebaseAuth.getInstance().getCurrentUser().getUid()
//                            );

                            mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(),"User Created",Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Log.d("Testing Daatabase Stuff", task.toString());
                                        Toast.makeText(getApplicationContext(),"Error!!!... Cannot Create User Account" ,Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                            //FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(),"An account already exists with that email",Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


}

