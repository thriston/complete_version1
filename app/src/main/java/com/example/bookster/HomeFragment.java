package com.example.bookster;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class HomeFragment extends Fragment  {

    Button button;
    Toolbar toolbar;
    private ArrayList<Category> categoryList = new ArrayList<>();
    private ListView mListView;
    private CategoryListAdapter adapter;
    private DrawerLayout drawer;
    User myUserProfile;
    String receiverName;
    NavigationView navigationView;
    private DatabaseReference myDatabase;

    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);






        FirebaseUser auth;
        mListView  = (ListView) v.findViewById(R.id.listView);
//        drawer= v.findViewById(R.id.drawer_layout);
//
//        navigationView = v.findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//        navigationView.setCheckedItem(R.id.home);
        //navigationView.setNavigationItemSelectedListener(this);


//        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
//        toolbar.setTitleTextColor(Color.WHITE);
//
//        toolbar.setTitle("Bookster");
//        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);




//        fadeIn(toolbar);

        //Options Bar
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();



        DatabaseReference myDatabase = FirebaseDatabase.getInstance().getReference().child("Category");
        myDatabase.keepSynced(true);
        myDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoryList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {


                    //System.out.println("CAT HERE: "+ds.child("").getValue());
                    String description, name, imageUrl;
                    String nItems;
                    description =(String) ds.child("Description").getValue();
                    name =(String) ds.child("Name").getValue();
                    imageUrl = (String) ds.child("imageUrl").getValue();
                    nItems =(String) ds.child("nItems").getValue();

                    Category category = new Category(name, description, ""+nItems, imageUrl);//"drawable://" + R.drawable.book
                    categoryList.add(category);

                }
                if (getActivity()!=null){
                    adapter = new CategoryListAdapter(getActivity(), R.layout.adapter_view_layout, categoryList);
                    mListView.setAdapter(adapter);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



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


        return v;


    }


//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//        switch (menuItem.getItemId())
//        {
//            case R.id.home:
//                getActivity().getFragmentManager().popBackStack();
//                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
//                navigationView.setCheckedItem(R.id.home);
//                break;
//            case R.id.nav_login:
//                getActivity().getFragmentManager().popBackStack();
//                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoginFragment()).commit();
//                navigationView.setCheckedItem(R.id.nav_login);
//                break;
//
//
//        }
//
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }

//    @Override
//    public void onBackPressed() {
//        if (drawer.isDrawerOpen(GravityCompat.START))
//        {
//            drawer.closeDrawer(GravityCompat.START);
//        }
//        else{
//            super.onBackPressed();
//        }
//
//
//    }




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
