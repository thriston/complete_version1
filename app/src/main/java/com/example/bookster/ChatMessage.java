package com.example.bookster;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

public class ChatMessage {
    private String messageText;
    private String messageUser;
    private long messageTime;
    private String userName;
    private String senderUID;

    public ChatMessage(String messageText, String messageUser, String userName, long messageTime) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.userName = userName;
        this.messageTime = new Date().getTime();
        this.senderUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public ChatMessage() {
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSenderUID() {
        return senderUID;
    }

    public void setSenderUID(String senderUID) {
        this.senderUID = senderUID;
    }
}
