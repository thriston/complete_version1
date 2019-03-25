package com.example.bookster;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class Bookster extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
