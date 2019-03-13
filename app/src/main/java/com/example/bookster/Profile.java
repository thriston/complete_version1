package com.example.bookster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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
        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(user.getUid());

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



}
