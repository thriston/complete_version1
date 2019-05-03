package com.example.bookster;

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
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ChatFragment extends Fragment {


    private String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String myUserName;
    private String receiverUID;
    private String receiverName, key;
    private ListView mListView;
    private ArrayList<Conversation> conversationsModel;
    private Toolbar toolbar;


    public ChatFragment() {
        /**Required empty public constructor **/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /** Inflate the layout for this fragment   **/
        View v = inflater.inflate(R.layout.fragment_chat, container, false);

        mListView = v.findViewById(R.id.chatListView);
        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.setTitle("Chat");
        toolbar.setTitleTextColor(Color.WHITE);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity().finish();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });
        /** Organizes the toolbar   **/

        DatabaseReference myDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(myUID);
        myDatabase.keepSynced(false);
        myDatabase.child("Conversations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                conversationsModel= new ArrayList<>();
                for(final DataSnapshot ds : dataSnapshot.getChildren())
                {
                    if(getActivity() == null)
                        return;
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

                if(getActivity()!=null)
                {
                    ConversationListAdapter adapter = new ConversationListAdapter(getContext(), R.layout.conversations_view_layout, conversationsModel);
                    mListView.setAdapter(adapter);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }


        });
/**Constructs a Chat Model for the user chats    **/

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myintent = new Intent(view.getContext(), ConversationMessageActivity.class);
                myintent.putExtra("receiverConvoObj", conversationsModel.get(position));
                //System.out.println("CATEGORY: "+categoryList.get(position).getName());
                startActivityForResult(myintent, 0);
            }
        });

        return v;
    }


}
