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

public class ConversationMessageActivity extends AppCompatActivity {

    private DatabaseReference myDatabase;
    private String receiverUID;
    private FloatingActionButton sendBtn;
    private String key;
    private String myUID;
    private String myUserName;
    private String receiverName;
    private Conversation receiverConvo;
    private String profilePicURL;
    Toolbar toolbar;
    /**Local Variables Used     **/

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        Intent intent = getIntent();
        receiverConvo = (Conversation) intent.getSerializableExtra("receiverConvoObj");
        myUserName = receiverConvo.getMyUserName();
        receiverUID = receiverConvo.getReceiverUID();
        myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("");
        TextView textView = findViewById(R.id.title);
        textView.setText(receiverConvo.getReceiverFullName());
        setSupportActionBar(toolbar);
        /**Organizes the toolbar     **/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


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
        /**Pulls and sets user profile picture     **/


        myDatabase = FirebaseDatabase.getInstance().getReference().child("Chats");
        myDatabase.keepSynced(true);

        sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });


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
                                ChatMessageListAdapter adapter = new ChatMessageListAdapter(ConversationMessageActivity.this, R.layout.adapter_view_layout1, chatModelList);
                                mListView.setAdapter(adapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

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
                                ChatMessageListAdapter adapter = new ChatMessageListAdapter(ConversationMessageActivity.this, R.layout.adapter_view_layout1, chatModelList);
                                mListView.setAdapter(adapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

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
        /**Looks for existing messages sent, pulls it, sets the chat interface, and transmit messages     **/


        DatabaseReference myDatabase1 = FirebaseDatabase.getInstance().getReference().child("users");
        myDatabase1.keepSynced(true);
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
        //saves last message time
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

        EditText editText = findViewById(R.id.editText);

        ChatMessage chatMessage = new ChatMessage(
                editText.getText().toString(),
                FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                myUserName,
                new Date().getTime()
        );

        myDatabase.child(key).push().setValue(chatMessage);
        editText.setText("");
    }
    /**Sends all messages i.e updates firebase database     **/
}
