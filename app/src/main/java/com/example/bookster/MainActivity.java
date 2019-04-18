package com.example.bookster;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    Button button;
    android.support.v7.widget.Toolbar toolbar;
    private ArrayList<Category> categoryList = new ArrayList<>();
    private ListView mListView;
    private CategoryListAdapter adapter;
    private DrawerLayout drawer;
    User myUserProfile;
    String receiverName;
    NavigationView navigationView;
    private DatabaseReference myDatabase;

    //If logged in
    //private static final int nav_chat = 941;
    private static final int nav_chat = 2;
    private static final int nav_profile = 3;
    private static final int nav_my_products = 4;
    private static final int nav_notifications = 5;
    private static final int nav_logout = 6;

    //If logged out
    private static final int nav_login = 7;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // FirebaseUser auth;
       // mListView  = (ListView) findViewById(R.id.listView);
        drawer= findViewById(R.id.drawer_layout);
//
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //navigationView.setCheckedItem(R.id.nav_home);
        addMenuItems();

//        Menu menu = navigationView.getMenu();
//        menu.add(R.id.group0, menu.FIRST, menu.NONE , "TEST").setIcon(R.drawable.ic_login);

////
////
        toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle("Bookster");
        setSupportActionBar(toolbar);
        fadeIn(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
//
//
//
        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).addToBackStack("fragBack").commit();
            navigationView.setCheckedItem(R.id.home);
        }

//
//        DatabaseReference myDatabase = FirebaseDatabase.getInstance().getReference().child("Category");
//        myDatabase.keepSynced(true);
//        myDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                categoryList.clear();
//                for(DataSnapshot ds: dataSnapshot.getChildren())
//                {
//
//
//                    //System.out.println("CAT HERE: "+ds.child("").getValue());
//                    String description, name, imageUrl;
//                    String nItems;
//                    description =(String) ds.child("Description").getValue();
//                    name =(String) ds.child("Name").getValue();
//                    imageUrl = (String) ds.child("imageUrl").getValue();
//                    nItems =(String) ds.child("nItems").getValue();
//
//                    Category category = new Category(name, description, ""+nItems, imageUrl);//"drawable://" + R.drawable.book
//                    categoryList.add(category);
//
//                }
//                adapter = new CategoryListAdapter(MainActivity.this, R.layout.adapter_view_layout, categoryList);
//                mListView.setAdapter(adapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//     //LIST ONCLICK LISTENER
//
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                //if(position==0){
//                    Intent myintent = new Intent(view.getContext(), ProductListActivity.class);
//                    myintent.putExtra("category", categoryList.get(position).getName() );
//                    //System.out.println("CATEGORY: "+categoryList.get(position).getName());
//                    startActivityForResult(myintent, 0);
//                //}
//            }
//        });

    }

//    // Menu icons are inflated just as they were with actionbar
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menu.clear();
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }


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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if(id == R.id.miProfile)
//        {
//
//            if (user != null) {
//                //Uri photoUrl = user.getPhotoUrl();
//
//                // Check if user's email is verified
//                boolean emailVerified = user.isEmailVerified();
//                System.out.println("USER: " + user.getEmail());
//                String uid = user.getUid();
//                //Toast.makeText(getApplicationContext(),email+" Signed in",Toast.LENGTH_SHORT).show();
//                Intent i = new Intent(getApplicationContext(), Profile.class);
//                //i.putExtra("receiverUID", receiverUID);
//                startActivity(i);
//
//            }
//            else {
//                Intent i = new Intent(getApplicationContext(), Login.class);
//                startActivity(i);
//            }
//            return true;
//        }


//        if(user != null && id == R.id.chat)
//        {
//            //Toast.makeText(MainActivity.this, "Chat clicked", Toast.LENGTH_LONG).show();
//            Intent i = new Intent(getApplicationContext(), ConversationChatActivity.class);
//            startActivity(i);
//            return true;
//        }
//        else{
//            Toast.makeText(getApplicationContext(), "Please Login To Continue", Toast.LENGTH_SHORT).show();
//            Intent i = new Intent(getApplicationContext(), Login.class);
//            startActivity(i);
//        }
        return super.onOptionsItemSelected(item);
    }




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemID = menuItem.getItemId();
        if(itemID == nav_login)
        {
            getFragmentManager().popBackStack();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoginFragment()).addToBackStack("fragBack").commit();
        }
        else
        if (itemID == R.id.nav_home)
        {
            getFragmentManager().popBackStack();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).addToBackStack("fragBack").commit();
        }
        else
        if(itemID == nav_chat)
        {
            getFragmentManager().popBackStack();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChatFragment()).addToBackStack("fragBack").commit();
        }
        else
        if(itemID == nav_profile)
        {
            getFragmentManager().popBackStack();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).addToBackStack("fragBack").commit();
        }
        else
        if(itemID == nav_logout)
        {

            AlertDialog.Builder altdial = new AlertDialog.Builder(MainActivity.this);
            altdial.setMessage("Are you sure you want to log out?").setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent= new Intent(getApplicationContext(), MainActivity.class);
                            finish();
                            startActivity(intent);
                            Toast.makeText(MainActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
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
        else
        if(itemID == nav_my_products)
        {
            Intent i= new Intent(getApplicationContext(),MyProductListActivity.class);
            startActivity(i);
        }
        else
        if(itemID == nav_notifications)
        {
            Intent i= new Intent(getApplicationContext(),RequestActivity.class);
            startActivity(i);
        }




        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag("fragBack") != null) {

        }
        else {
            super.onBackPressed();
            return;
        }
        if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            //Toast.makeText(getApplicationContext(), "Test", Toast.LENGTH_LONG).show();
            Fragment frag = getSupportFragmentManager().findFragmentByTag("fragBack");
            FragmentTransaction transac = getSupportFragmentManager().beginTransaction().remove(frag);
            transac.commit();
        }
    }

    public void addMenuItems()
    {
        FirebaseUser user;
        user = FirebaseAuth.getInstance().getCurrentUser();
        Menu menu = navigationView.getMenu();
        if(user!=null)
        {
            menu.add(R.id.group0, nav_chat, 2 , "Chat").setIcon(R.drawable.chat_icon);
            menu.add(R.id.group0, nav_profile, 3 , "Profile").setIcon(R.drawable.ic_profile);
            menu.add(R.id.group0, nav_my_products, 4 , "My products").setIcon(R.drawable.ic_product);
            menu.add(R.id.group0, nav_notifications, 5 , "Notifications").setIcon(R.drawable.ic_notifications);
            menu.add(R.id.group0, nav_logout, 6 , "Logout").setIcon(R.drawable.ic_logout);
        }
        else
        {
            menu.add(R.id.group0, nav_login, 2 , "Login").setIcon(R.drawable.ic_login);
        }

    }



}
