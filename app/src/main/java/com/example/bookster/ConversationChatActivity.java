package com.example.bookster;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ConversationChatActivity extends AppCompatActivity {

    private String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String myUserName;
    private String receiverUID;
    private String receiverName, key;
    private ListView mListView;
    private ArrayList<Conversation> conversationsModel;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversations_layout);
        mListView = findViewById(R.id.chatListView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Conversations");
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
        /**Organizes The toolbar    **/

        DatabaseReference myDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(myUID);
        myDatabase.keepSynced(false);
        myDatabase.child("Conversations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                conversationsModel= new ArrayList<>();
                for(final DataSnapshot ds : dataSnapshot.getChildren())
                {
                    key = ds.getKey();

                    if (key.startsWith(myUID))
                    {
                        receiverUID =  key.substring(myUID.length()+1,key.length());
                    }
                    else
                    {
                        receiverUID =  key.substring(0,key.indexOf("_"));
                    }


                    final long time = (Long) ds.child("lastActivity").getValue();
                    receiverName =  ds.child("receiverName").getValue().toString();
                    myUserName = ds.child("myUserName").getValue().toString();

                    Conversation conversation = new Conversation(key,receiverUID, time, receiverName, myUserName);
                    conversationsModel.add(conversation);

                }
                /**Looks for existing conversation amoungst the users    **/

                ConversationListAdapter adapter = new ConversationListAdapter(ConversationChatActivity.this, R.layout.conversations_view_layout, conversationsModel);
                mListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }


        });


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //if(position==0){
                Intent myintent = new Intent(view.getContext(), ConversationMessageActivity.class);
                myintent.putExtra("receiverConvoObj", conversationsModel.get(position));
                //System.out.println("CATEGORY: "+categoryList.get(position).getName());
                startActivityForResult(myintent, 0);
                //}
            }
        });
        /**Starts the Conversation Message Activity    **/
    }



}
