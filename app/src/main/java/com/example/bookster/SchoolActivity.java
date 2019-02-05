package com.example.bookster;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class SchoolActivity extends AppCompatActivity {

    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school);

        name = "School";


    }


}
