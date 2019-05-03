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

//The main Chat Message activity
public class MessageActivity extends AppCompatActivity {

    private DatabaseReference myDatabase;
    private String receiverUID;
    private FloatingActionButton sendBtn;
    private String key;
    private String myUID;
    private String myUserName;
    private User myUserProfile;
    private String receiverName;
    private String profilePicURL;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        //Get data from parent activity
        Intent intent = getIntent();
        myUserName = intent.getStringExtra("myUserName");
        myUserProfile = (User) intent.getSerializableExtra("myUserProfile");
        receiverUID = myUserProfile.getMyUID();

        //Get Current User ID
        myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Configure toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Get message the receiver's profile picture and name and add it to toolbar
        myDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(receiverUID).child("profilePicURL");
        myDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                profilePicURL = dataSnapshot.getValue().toString();
                ImageView imageView = findViewById(R.id.profilePicToolbar);
                Glide.with(getApplicationContext()).load(profilePicURL).apply(new RequestOptions().placeholder(R.drawable.img_placeholder)).error(R.drawable.image_placeholder).fitCenter().into(imageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Send button clicked
        sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });



        //Checks for a conversation and returns messages if existing conversation exists
        myDatabase = FirebaseDatabase.getInstance().getReference().child("Chats");
        myDatabase.keepSynced(true);
        myDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> chatIDs = new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    chatIDs.add(ds.getKey());
                }
                final String v1, v2, myUID;
                myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                //Conversation ID can either be v1 or v2
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

                                //Adds items (chat messages) to the chat listView
                                ChatMessageListAdapter adapter = new ChatMessageListAdapter(MessageActivity.this, R.layout.adapter_view_layout1, chatModelList);
                                mListView.setAdapter(adapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        // v1 is the conversation ID key
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

                                //Adds items (chat messages) to the chat listView
                                ChatMessageListAdapter adapter = new ChatMessageListAdapter(MessageActivity.this, R.layout.adapter_view_layout1, chatModelList);
                                mListView.setAdapter(adapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        // v2 is the conversation ID key
                        key = v2;
                    }

                }

                //if no conversation already exists then create a new conversation with v1 as the ID
                if(!chatIDs.contains(v1) && !chatIDs.contains(v2))
                {
                    key = v1;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //Uses the receiver User ID to find the receiver's full name on firebase
        DatabaseReference myDatabase1 = FirebaseDatabase.getInstance().getReference().child("users");
        myDatabase1.keepSynced(false);
        myDatabase1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    if(ds.getKey().equals(receiverUID))
                    {
                        User user = ds.getValue(User.class);
                        receiverName = user.getFullname();
                        TextView textView = findViewById(R.id.title);
                        textView.setText(receiverName);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });

    }

    //Called when the user clicks the send message floating action button
    public void sendMessage()
    {
        EditText editText = findViewById(R.id.editText);
        String message = editText.getText().toString();
        if(!message.equals("")) {
            Date date = new Date();
            //store a copy of message/chat information to the current user's profile
            FirebaseDatabase.getInstance().getReference().child("users").child(myUID).child("Conversations").child(key).child("lastActivity").setValue(date.getTime());
            FirebaseDatabase.getInstance().getReference().child("users").child(myUID).child("Conversations").child(key).child("receiverUID").setValue(receiverUID);
            FirebaseDatabase.getInstance().getReference().child("users").child(myUID).child("Conversations").child(key).child("receiverName").setValue(receiverName);
            FirebaseDatabase.getInstance().getReference().child("users").child(myUID).child("Conversations").child(key).child("myUID").setValue(myUID);
            FirebaseDatabase.getInstance().getReference().child("users").child(myUID).child("Conversations").child(key).child("myUserName").setValue(myUserName);

            //store a copy of message/chat information to the current receiver's profile
            FirebaseDatabase.getInstance().getReference().child("users").child(receiverUID).child("Conversations").child(key).child("lastActivity").setValue(date.getTime());
            FirebaseDatabase.getInstance().getReference().child("users").child(receiverUID).child("Conversations").child(key).child("receiverUID").setValue(myUID);
            FirebaseDatabase.getInstance().getReference().child("users").child(receiverUID).child("Conversations").child(key).child("receiverName").setValue(myUserName);
            FirebaseDatabase.getInstance().getReference().child("users").child(receiverUID).child("Conversations").child(key).child("myUID").setValue(receiverUID);
            FirebaseDatabase.getInstance().getReference().child("users").child(receiverUID).child("Conversations").child(key).child("myUserName").setValue(receiverName);

            //store the main copy of message/chat information to the "Chats" node on firebase
            //EditText editText = findViewById(R.id.editText);
            ChatMessage chatMessage = new ChatMessage(
                    message,
                    FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                    myUserName,
                    new Date().getTime()
            );
            myDatabase.child(key).push().setValue(chatMessage);
            editText.setText("");
        }
    }

}
