package com.example.bookster;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReceivedRequestActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView listView;
    private ArrayList<Object> requestList = new ArrayList<>();
    private ArrayList<BarterRequest> barterList;
    private ArrayList<PurchaseRequest> purchaseList;
    private RequestPageListAdapter adapter;
    private DatabaseReference db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received);
        listView = findViewById(R.id.receivedListView);

        //Configure toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Requests Received");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //To receive purchase and barter requests from firebase and populate list view
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("Requests");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                requestList.clear();
                barterList = new ArrayList<>();
                purchaseList = new ArrayList<>();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    if(ds.child("type").getValue().toString().equals("Barter") && !ds.child("senderUID").getValue().toString().equals(user.getUid()))
                    {
                        barterList.add(ds.getValue(BarterRequest.class));
                    }
                    else
                    if(ds.child("type").getValue().toString().equals("Purchase") && !ds.child("senderUID").getValue().toString().equals(user.getUid()))
                    {
                        purchaseList.add(ds.getValue(PurchaseRequest.class));
                    }
                }
                requestList.addAll(barterList);
                requestList.addAll(purchaseList);

                //Populate list view with received requests
                adapter = new RequestPageListAdapter(getApplicationContext(), R.layout.barter_request_item, requestList);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        //Click on product list item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //If list item clicked is a barter request
                if(requestList.get(position) instanceof BarterRequest)
                {
                    BarterRequest barterRequest = (BarterRequest) requestList.get(position);
                    Intent intent = new Intent(getApplicationContext(), PurchaseRequestPage.class);
                    intent.putExtra("barterRequest", barterRequest);
                    startActivity(intent);
                }
                //If list item clicked is a purchase request
                else
                if(requestList.get(position) instanceof PurchaseRequest)
                {
                    PurchaseRequest purchaseRequest = (PurchaseRequest) requestList.get(position);
                    Intent intent = new Intent(getApplicationContext(), PurchaseRequestPage.class);
                    intent.putExtra("purchaseRequest", purchaseRequest);
                    startActivity(intent);
                }
            }
        });
    }
}
