package com.example.bookster;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Login extends AppCompatActivity {
    EditText emailInput,passwordInput;
    TextView textView, textView2;
    ViewPager viewPager;
    ProductPictureSlider adapter;
    Toolbar toolbar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textView=findViewById(R.id.signUpLinkTextView);
        textView2=findViewById(R.id.resetPassword);
        mAuth = FirebaseAuth.getInstance();
        emailInput=findViewById(R.id.emailAddressEditText2);
        passwordInput=findViewById(R.id.passwordEditText2);
        textView2.setTextColor(Color.parseColor("#411FC7"));
        textView2.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textView.setTextColor(Color.parseColor("#411FC7"));
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Bookster - Login");
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
                Intent i= new Intent(getApplicationContext(),SignUp.class);
                startActivity(i);
                finish();
            }
        });
        /** Organizes the toolbar   **/
    }
    public void signIn(View view) {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        if(email.isEmpty()){
            Toast.makeText(getApplicationContext(), "Email Address field is Empty", Toast.LENGTH_SHORT).show();
        }

        if(password.isEmpty()){
            Toast.makeText(getApplicationContext(), "Password field is empty", Toast.LENGTH_SHORT).show();
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            /**Sign in success, update UI with the signed-in user's information   **/
                            Toast.makeText(getApplicationContext(), "Logged In", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            setResult(  RESULT_OK, null);
                            finish();

                        } else {
                            /**If sign in fails, display a message to the user.   **/
                            Toast.makeText(getApplicationContext(), "Sign in Failed - Username or Password Incorrect", Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }

    public void sendPasswordReset(View view) {
        /** [START send_password_reset]  **/
        final String emailAddress = emailInput.getText().toString();
        if(emailAddress.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please enter your email address to being password reset", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "An email was sent to " + emailAddress + " to reset your password", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "This email is not registered to an existing account", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        /**[END send_password_reset]   **/
    }
}
