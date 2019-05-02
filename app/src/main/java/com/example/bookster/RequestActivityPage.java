package com.example.bookster;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

public class RequestActivityPage extends AppCompatActivity {

    private Toolbar toolbar;
    //private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_page);
        //listView = findViewById(R.id.request_page_list_view);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Requests");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });







        LinearLayout receivedLayout = findViewById(R.id.receivedLayout);
        receivedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReceivedRequestActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout sentLayout = findViewById(R.id.sentLayout);
        sentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SentRequestActivity.class);
                startActivity(intent);
            }
        });
//
//        LinearLayout ARLayout = findViewById(R.id.arLayout);
//        ARLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("ACCEPT/REJECT CLICKED");
//            }
//        });


    }
}
