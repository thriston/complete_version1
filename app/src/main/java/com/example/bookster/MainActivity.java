package com.example.bookster;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button button;
    Toolbar toolbar;
    User myUserProfile;
    String receiverName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseUser auth;


        ListView mListView = (ListView) findViewById(R.id.listView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Bookster");
        setSupportActionBar(toolbar);
        fadeIn(toolbar);

        //Create Category objects
        Category school = new Category("School", "Books, Calculators, map","15 ads", "drawable://" + R.drawable.book);
        Category electronics = new Category("Electronics", "phones, laptops, watches, etc.", "7 ads", "drawable://" + R.drawable.electronics);
        Category clothing = new Category("Clothing", "Shoes, Jeans, Dresses, etc.", "32 ads", "drawable://" + R.drawable.clothing);
        Category gaming = new Category("Gaming","Consoles, Games, Controllers, etc.", "3 ads", "drawable://" + R.drawable.gaming);
        Category food = new Category("Food", "KFC, Subway, Marios, Pita Pit, Rituals", "10 ads", "drawable://" + R.drawable.food);

        //Add the categories objects to an ArrayList
        final ArrayList<Category> categoryList = new ArrayList<>();
        categoryList.add(school);
//        categoryList.add(electronics);
//        categoryList.add(clothing);
//        categoryList.add(gaming);
//        categoryList.add(food);

        CategoryListAdapter adapter = new CategoryListAdapter(this, R.layout.adapter_view_layout, categoryList);
        mListView.setAdapter(adapter);


//        DatabaseReference myDatabase = FirebaseDatabase.getInstance().getReference().child("users");
//        myDatabase.child("Conversations").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                ListView mListView = findViewById(R.id.chatListView);
//                final ArrayList<Conversation> conversationsModel= new ArrayList<>();
//                for(DataSnapshot ds : dataSnapshot.getChildren())
//                {
//
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError)
//            {
//            }
//
//
//        });

//        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users").child(receiverUID).child("fullName");
//
//        db.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                receiverName = (String) dataSnapshot.getValue();
//
//                //firebaseCallback.onCallBack(receiverName);
////                Conversation conversation = new Conversation(key,time, receiverName);
////                conversationsModel.add(conversation);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });







        //LIST ONCLICK LISTENER

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //if(position==0){
                    Intent myintent = new Intent(view.getContext(), ProductListActivity.class);
                    myintent.putExtra("category", categoryList.get(position).getName() );
                    //System.out.println("CATEGORY: "+categoryList.get(position).getName());
                    startActivityForResult(myintent, 0);
                //}
            }
        });

    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public void checkCurrentUser() {
        // [START check_current_user]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {
            Toast.makeText(getApplicationContext(),"User Signed In",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(),"User Not Signed in",Toast.LENGTH_SHORT).show();
        }
        // [END check_current_user]
    }


    // Respond to menu item clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.miProfile)
        {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                //Uri photoUrl = user.getPhotoUrl();

                // Check if user's email is verified
                boolean emailVerified = user.isEmailVerified();
                System.out.println("USER: " + user.getEmail());
                String uid = user.getUid();
                //Toast.makeText(getApplicationContext(),email+" Signed in",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), Profile.class);
                //i.putExtra("receiverUID", receiverUID);
                startActivity(i);

            }
            else {
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
            }
            return true;
        }


        if(id == R.id.chat)
        {
            //Toast.makeText(MainActivity.this, "Chat clicked", Toast.LENGTH_LONG).show();
            Intent i = new Intent(getApplicationContext(), ConversationChatActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void fadeIn(View view) {
        // Create an AlphaAnimation variable
        // 0.0f makes the view invisible
        // 1.0f makes the view fully visible
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        // Set out long you want the animation to be. * Measured in milliseconds *
        // 1000 milliseconds = 1 second
        anim.setDuration(1500);
        // Start the animation on our passed in view
        view.startAnimation(anim);
        /*  After the animation is complete we want to make sure we set the visibility of the view
            to VISIBLE. Otherwise it will go back to being INVISIBLE due to our previous lines
            that set the view to INVISIBLE */
        view.setVisibility(View.VISIBLE);
    }
}
