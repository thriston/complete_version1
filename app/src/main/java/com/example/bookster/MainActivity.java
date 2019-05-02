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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private TextView tvName, tvEmail;
    private ImageView imageView;
    private static final int nav_chat = 2;
    private static final int nav_profile = 3;
    private static final int nav_my_products = 4;
    private static final int nav_requests = 5;
    private static final int nav_logout = 6;
    private static final int nav_login = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawer= findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        /**Organizes Navigation Drawer   **/

        View headerLayout = navigationView.getHeaderView(0);
        tvName = headerLayout.findViewById(R.id.name);
        tvEmail = headerLayout.findViewById(R.id.email);
        imageView = headerLayout.findViewById(R.id.profilePictureImage);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null)
        {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    tvName.setText(dataSnapshot.child("fullname").getValue().toString());
                    tvEmail.setText(dataSnapshot.child("email").getValue().toString());
                    Glide.with(getApplicationContext()).load(dataSnapshot.child("profilePicURL").getValue().toString()).apply(new RequestOptions().placeholder(R.drawable.img_placeholder)).error(R.drawable.image_placeholder).fitCenter().into(imageView);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        /** Pulls and sets user info to the nav drawer  **/
        else
        {
            tvName.setText("Login to access full functionality");
            tvEmail.setVisibility(View.GONE);

        }

        addMenuItems();

        toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle("Bookster");
        setSupportActionBar(toolbar);
        fadeIn(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).addToBackStack("fragBack").commit();
            navigationView.setCheckedItem(R.id.home);
        }

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


    /**  Respond to menu item clicks **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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
        if(itemID == nav_requests)
        {
            Intent i= new Intent(getApplicationContext(), RequestActivityPage.class);
            startActivity(i);
        }




        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /**Actions done when icons is clicked**/

    private void fadeIn(View view) {

        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);

        anim.setDuration(1500);
        view.startAnimation(anim);

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
            menu.add(R.id.group0, nav_requests, 5 , "Requests").setIcon(R.drawable.ic_notifications);
            menu.add(R.id.group0, nav_logout, 6 , "Logout").setIcon(R.drawable.ic_logout);
        }
        else
        {
            menu.add(R.id.group0, nav_login, 2 , "Login").setIcon(R.drawable.ic_login);
        }

    }



}
