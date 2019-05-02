package com.example.bookster;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ChatMessageListAdapter extends ArrayAdapter<ChatMessage> {
    private Context mContext;
    int mResource;
    ArrayList<ChatMessage> chatMessages = null;
    public static final int BY_SENDER = 1;
    public static final int BY_RECEIVER = 0;


    public ChatMessageListAdapter(Context context, int resource, ArrayList<ChatMessage> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
        this.chatMessages = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String message = getItem(position).getMessageText();
        long messageTime = getItem(position).getMessageTime();

        if(convertView == null)
        {
            if(getItemViewType(position) == BY_SENDER)
            {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.adapter_view_layout1, parent, false);

            }
            else if(getItemViewType(position) == BY_RECEIVER)
            {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.adapter_view_layout2, parent, false);
            }
        }


        TextView tvMessage = (TextView) convertView.findViewById(R.id.message);
        TextView tvTime = (TextView) convertView.findViewById(R.id.time);
        tvMessage.setText(message);
        tvTime.setText(DateFormat.format("h:mma   dd/MM/yyyy", messageTime));
        /**Casts XML Objects to Chat Message with Chat View **/
        return convertView;

    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(chatMessage.getSenderUID().equals(user.getUid()))
        {
            return BY_SENDER;
        }
        else
        {
            return BY_RECEIVER;
        }

    }
}
