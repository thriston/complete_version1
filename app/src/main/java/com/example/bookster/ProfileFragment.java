package com.example.bookster;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//This class is the fragment that is accessible from the navigation view
public class ProfileFragment extends Fragment {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private TextView fullNameView, emailView, contactView;
    private ImageView profileImageV;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        fullNameView= v.findViewById(R.id.fullNameField);
        emailView= v.findViewById(R.id.emailField);
        contactView= v.findViewById(R.id.contactField);
        profileImageV = v.findViewById(R.id.profilePicture);

        // Configure toolbar
        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.setTitle("Your Profile");
        toolbar.setTitleTextColor(Color.WHITE);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(user.getUid());
        mDatabase.keepSynced(false);

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                if (getActivity() == null) {
                    return;
                }
                User currUser = dataSnapshot.getValue(User.class);
                fullNameView.setText(currUser.fullname);
                emailView.setText(currUser.email);
                contactView.setText(currUser.contact);
                Glide.with(getContext()).load(currUser.profilePicURL)
                        .apply(new RequestOptions().placeholder(R.drawable.img_placeholder))
                        .error(R.drawable.image_placeholder)
                        .fitCenter().into(profileImageV);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Email Address field is Empty", Toast.LENGTH_SHORT).show();
            }
        };
        mDatabase.addValueEventListener(userListener);

        //If logout button is clicked, allow the user to confirm
        Button logOutBtn = v.findViewById(R.id.logOutBtn);
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder altdial = new AlertDialog.Builder(getContext());
                altdial.setMessage("Are you sure you want to log out?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent= new Intent(getContext(), MainActivity.class);
                                //getActivity().finish();
                                startActivity(intent);
                                Toast.makeText(getContext(), "Logged Out", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = altdial.create();
                alert.setTitle("Log Out");
                alert.show();
            }
        });
        return v;
    }

}
