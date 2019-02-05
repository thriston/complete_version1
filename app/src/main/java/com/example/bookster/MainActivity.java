package com.example.bookster;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button button;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView mListView = (ListView) findViewById(R.id.listView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        categoryList.add(electronics);
        categoryList.add(clothing);
        categoryList.add(gaming);
        categoryList.add(food);

        CategoryListAdapter adapter = new CategoryListAdapter(this, R.layout.adapter_view_layout, categoryList);
        mListView.setAdapter(adapter);

    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    // Respond to menu item clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.miProfile:
                Intent i=new Intent(getApplicationContext(),Login.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
