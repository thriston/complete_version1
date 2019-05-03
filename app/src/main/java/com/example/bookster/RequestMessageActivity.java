package com.example.bookster;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class RequestMessageActivity extends AppCompatActivity {

    private DatabaseReference myDatabase;
    private String receiverUID;
    private FloatingActionButton sendBtn;
    private String key;
    private String myUID;
    private String myUserName;
    private User myUserProfile;
    private String receiverName;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        //Receive data from parent activity
        Intent intent = getIntent();
        myUserName = intent.getStringExtra("myUserName");
        receiverUID = intent.getStringExtra("receiverUID");

        //Get logged in current user
        myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Configure toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        //Set toolbar image and text by pulling the information from firebase
        myDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(receiverUID);
        myDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                toolbar.setTitle("");
                TextView toolbarTV = findViewById(R.id.title);
                toolbarTV.setText(dataSnapshot.child("fullname").getValue().toString()+"'s Chat");
                ImageView imageView = findViewById(R.id.profilePicToolbar);
                Glide.with(getApplicationContext()).load(dataSnapshot.child("profilePicURL").getValue().toString()).apply(new RequestOptions().placeholder(R.drawable.img_placeholder)).error(R.drawable.image_placeholder).fitCenter().into(imageView);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //If send button is clicked call the sendMessage method
        sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        //Checks for an existing conversation and returns messages
        myDatabase = FirebaseDatabase.getInstance().getReference().child("Chats");
        myDatabase.keepSynced(false);
        myDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ListView mListView = findViewById(R.id.listView);
                ArrayList<String> chatIDs = new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    ChatMessage chatMessage = ds.getValue(ChatMessage.class);
                    chatIDs.add(ds.getKey());
                }

                //can have two possble conversation ID, v1 or v2, so checks for both
                final String v1, v2, myUID;
                myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                v1 = receiverUID+"_"+myUID;
                v2 = myUID+"_"+receiverUID;

                if(chatIDs.contains(v1) || chatIDs.contains(v2))
                {
                    if(chatIDs.contains(v1))
                    {
                        myDatabase.child(v1).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ListView mListView = findViewById(R.id.listView);
                                ArrayList<ChatMessage> chatModelList = new ArrayList<>();
                                for(DataSnapshot ds : dataSnapshot.getChildren())
                                {
                                    ChatMessage chatMessage = ds.getValue(ChatMessage.class);
                                    chatModelList.add(chatMessage);
                                }
                                ChatMessageListAdapter adapter = new ChatMessageListAdapter(RequestMessageActivity.this, R.layout.adapter_view_layout1, chatModelList);
                                mListView.setAdapter(adapter);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                        //Conversation ID v1 was used in existing conversation
                        key = v1;
                    }


                    if(chatIDs.contains(v2))
                    {
                        myDatabase.child(v2).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ListView mListView = findViewById(R.id.listView);
                                ArrayList<ChatMessage> chatModelList = new ArrayList<>();
                                for(DataSnapshot ds : dataSnapshot.getChildren())
                                {
                                    ChatMessage chatMessage = ds.getValue(ChatMessage.class);
                                    chatModelList.add(chatMessage);
                                }
                                System.out.println("HERE-HE...IN 2");
                                System.out.println("HERE-HE: "+chatModelList.get(0).getUserName());
                                ChatMessageListAdapter adapter = new ChatMessageListAdapter(RequestMessageActivity.this, R.layout.adapter_view_layout1, chatModelList);

                                mListView.setAdapter(adapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        //Conversation ID v2 was used in existing conversation
                        key = v2;
                    }

                }

                //new conversation
                if(!chatIDs.contains(v1) && !chatIDs.contains(v2))
                {
                    key = v1;

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //To get message receivers name from firebase
        DatabaseReference myDatabase1 = FirebaseDatabase.getInstance().getReference().child("users");
        myDatabase1.keepSynced(false);
        myDatabase1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    if(ds.getKey().equals(receiverUID))
                    {
                        User user= (User) ds.getValue(User.class);
                        receiverName = user.getFullname();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });


    }

    public void sendMessage()
    {
        EditText editText = findViewById(R.id.editText);
        String message = editText.getText().toString();

        //Save copies of message data on firebase
        if(!message.equals("")) {
            Date date = new Date();
            FirebaseDatabase.getInstance().getReference().child("users").child(myUID).child("Conversations").child(key).child("lastActivity").setValue(date.getTime());
            FirebaseDatabase.getInstance().getReference().child("users").child(myUID).child("Conversations").child(key).child("receiverUID").setValue(receiverUID);
            FirebaseDatabase.getInstance().getReference().child("users").child(myUID).child("Conversations").child(key).child("receiverName").setValue(receiverName);
            FirebaseDatabase.getInstance().getReference().child("users").child(myUID).child("Conversations").child(key).child("myUID").setValue(myUID);
            FirebaseDatabase.getInstance().getReference().child("users").child(myUID).child("Conversations").child(key).child("myUserName").setValue(myUserName);

            FirebaseDatabase.getInstance().getReference().child("users").child(receiverUID).child("Conversations").child(key).child("lastActivity").setValue(date.getTime());
            FirebaseDatabase.getInstance().getReference().child("users").child(receiverUID).child("Conversations").child(key).child("receiverUID").setValue(myUID);
            FirebaseDatabase.getInstance().getReference().child("users").child(receiverUID).child("Conversations").child(key).child("receiverName").setValue(myUserName);
            FirebaseDatabase.getInstance().getReference().child("users").child(receiverUID).child("Conversations").child(key).child("myUID").setValue(receiverUID);
            FirebaseDatabase.getInstance().getReference().child("users").child(receiverUID).child("Conversations").child(key).child("myUserName").setValue(receiverName);

            ChatMessage chatMessage = new ChatMessage(
                    message,
                    FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                    myUserName,
                    new Date().getTime()
            );

            //Empties the EDITTEXT if the message is sent
            myDatabase.child(key).push().setValue(chatMessage);
            editText.setText("");
        }
    }

}
