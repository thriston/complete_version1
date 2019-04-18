package com.example.bookster;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RequestActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private ArrayList<PurchaseRequest> requestList;
    private RequestListAdapter adapter;
    private ListView mListView;
    android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Purchase Requests");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);






        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        requestList = new ArrayList<PurchaseRequest>();
        mListView = findViewById(R.id.listView);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .child("Requests").child("Purchase").child("received");

        mDatabase.keepSynced(true);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                requestList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    if(ds.child("status").getValue().equals("pending"))
                    {
                        //PurchaseRequest purchaseRequest = ds.getValue(PurchaseRequest.class);

                        Product product = ds.child("product").getValue(Product.class);
                        PurchaseRequest purchaseRequest = new PurchaseRequest(
                                ds.child("id").getValue().toString(),
                                ds.child("message").getValue().toString(),
                                ds.child("location").getValue().toString(),
                                ds.child("status").getValue().toString(),
                                ds.child("senderUID").getValue().toString(),
                                ds.child("senderName").getValue().toString(),
                                product,
                                (long) ds.child("date").getValue()
                        );



                        requestList.add(purchaseRequest);
                    }
                }

                adapter = new RequestListAdapter(RequestActivity.this, R.layout.adapter_view_purchase_request, requestList);


                mListView.setAdapter(adapter);





            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }
}
