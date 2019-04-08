package com.example.bookster;

import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;

public class MessagingService extends FirebaseMessagingService {


    Intent intent = new Intent(this, MainActivity.class);
    //intent.setFlags(Intent.)
}
