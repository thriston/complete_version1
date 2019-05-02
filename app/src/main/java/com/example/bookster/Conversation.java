package com.example.bookster;

import java.io.Serializable;

public class Conversation implements Serializable {
    private String conversationID;
    private long lastActivityTime;
    private String myUID;
    private String myUserName;
    private String receiverUID;
    private String receiverFullName;
    /** Local Variables Used for Conversation   **/

    public Conversation(String conversationID,String receiverUID, long lastActivityTime, String receiverFullName, String myUserName) {
        this.conversationID = conversationID;
        this.lastActivityTime = lastActivityTime;
        this.receiverFullName = receiverFullName;
        this.receiverUID = receiverUID;
        this.myUserName = myUserName;
    }
    /**Conversation Constructor   **/

    public Conversation(String conversationID,String receiverUID, long lastActivityTime, String receiverFullName) {
        this.conversationID = conversationID;
        this.lastActivityTime = lastActivityTime;
        this.receiverFullName = receiverFullName;
        this.receiverUID = receiverUID;
        this.myUserName = myUserName;
    }
    /** Overloaded Constructor   **/



    public String getConversationID() {
        return conversationID;
    }

    public void setConversationID(String conversationID) {
        this.conversationID = conversationID;
    }

    public long getLastActivityTime() {
        return lastActivityTime;
    }

    public void setLastActivityTime(long lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }

    public String getMyUID() {
        return myUID;
    }

    public void setMyUID(String senderUID) {
        this.myUID = senderUID;
    }

    public String getReceiverUID() {
        return receiverUID;
    }

    public void setReceiverUID(String receiverUID) {
        this.receiverUID = receiverUID;
    }

    public String getReceiverFullName() {

        return receiverFullName;
    }

    public void setReceiverFullName(String receiverFullName) {
        this.receiverFullName = receiverFullName;
    }

    public String getMyUserName() {
        return myUserName;
    }

    public void setMyUserName(String myUserName) {
        this.myUserName = myUserName;
    }
    /**Getters and setters    **/
}
