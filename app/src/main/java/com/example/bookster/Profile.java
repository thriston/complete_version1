package com.example.bookster;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    Toolbar toolbar;
    private TextView fullNameView, emailView, contactView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();
        fullNameView=findViewById(R.id.fullNameField);
        emailView=findViewById(R.id.emailField);
        contactView=findViewById(R.id.contactField);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Your Profile");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });


        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(user.getUid());
        mDatabase.keepSynced(true);

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                User currUser = dataSnapshot.getValue(User.class);
                // [START_EXCLUDE]
                fullNameView.setText(currUser.fullname);
                emailView.setText(currUser.email);
                contactView.setText(currUser.contact);

                // [END_EXCLUDE]
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.d("loadPost:onCancelled", String.valueOf(databaseError.toException()));
                // [START_EXCLUDE]
                Toast.makeText(getApplicationContext(), "Email Address field is Empty", Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mDatabase.addValueEventListener(userListener);


    }

    public void logout(View view){
         FirebaseAuth.getInstance().signOut();
        Intent i= new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
    }

    public void my_products(View view)
    {
        Intent i= new Intent(getApplicationContext(),MyProductListActivity.class);
        startActivity(i);
    }



}
