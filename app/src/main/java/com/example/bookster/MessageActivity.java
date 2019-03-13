package com.example.bookster;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class MessageActivity extends AppCompatActivity {

    private DatabaseReference myDatabase;
    private String receiverUID;
    private FloatingActionButton sendBtn;
    private String key;
    private String myUID;
    private String myUserName;
    private User myUserProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        Intent intent = getIntent();

        final Product product = (Product) intent.getSerializableExtra("productObj");
        myUserName = intent.getStringExtra("myUserName");
        myUserProfile = (User) intent.getSerializableExtra("myUserProfile");
        //final myUse = (Product) intent.getSerializableExtra("productObj");

        receiverUID = myUserProfile.getMyUID();
        //receiverUID = product.getSeller().getUID();
        myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        myDatabase = FirebaseDatabase.getInstance().getReference().child("Chats");


        //FirebaseDatabase.getInstance().getReference().child()




        sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        //System.out.println("USER PROFILE: "+value);






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
                                System.out.println("HERE-HE...IN 1");
                                System.out.println("HERE-HE: "+chatModelList.get(0).getUserName());
                                ChatMessageListAdapter adapter = new ChatMessageListAdapter(MessageActivity.this, R.layout.adapter_view_layout1, chatModelList);
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
                                ChatMessageListAdapter adapter = new ChatMessageListAdapter(MessageActivity.this, R.layout.adapter_view_layout1, chatModelList);
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
                    //mDatabase.child("users").child(userId).setValue(user);

                    System.out.println("NEW CONVERSATION"); //FirebaseDatabase.getInstance().getReference().child("Users").child(myUID).child("Conversations").child(key);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });






//        String value;
//        DatabaseReference myDatabase2 = FirebaseDatabase.getInstance().getReference().child("Users").child(myUID).child("Conversations");
//
//
//        myDatabase2.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                final String v1, v2, myUID;
//                myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                v1 = receiverUID+"_"+myUID;
//                v2 = myUID+"_"+receiverUID;
//
//
//                ArrayList<Conversation> conversationsModel= new ArrayList<>();
//                for(DataSnapshot ds : dataSnapshot.getChildren())
//                {
//                    Conversation conversation = new Conversation(ds.getKey(),(Long) ds.getValue());
//                    conversation.setMyUID(myUID);
//                    conversationsModel.add(conversation);
//                }
//                //System.out.println("CONVERSATIONS: "+conversationsModel.get(0).getLastActivityTime());
//                //System.out.println("TIMES: "+lastActivityTimes);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });

    }

    public void sendMessage()
    {
        //saves last message time
        Date date = new Date();
        FirebaseDatabase.getInstance().getReference().child("Users").child(myUID).child("Conversations").child(key).setValue(date.getTime());
        FirebaseDatabase.getInstance().getReference().child("Users").child(receiverUID).child("Conversations").child(key).setValue(date.getTime());
        EditText editText = findViewById(R.id.editText);

        ChatMessage chatMessage = new ChatMessage(
                editText.getText().toString(),
                FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                myUserName,
                new Date().getTime()
        );
//        ChatMessage chatMessage = new ChatMessage(
//                editText.getText().toString(),
//                seller.getEmail(),
//                seller.getFullName(),
//                new Date().getTime()
//        );

        myDatabase.child(key).push().setValue(chatMessage);
        editText.setText("");
    }

}
