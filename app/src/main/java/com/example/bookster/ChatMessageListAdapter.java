package com.example.bookster;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatMessageListAdapter extends ArrayAdapter<ChatMessage> {
    private Context mContext;
    int mResource;

    public ChatMessageListAdapter(Context context, int resource, ArrayList<ChatMessage> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String message = getItem(position).getMessageText();
        String userName = getItem(position).getUserName();
        long messageTime = getItem(position).getMessageTime();
        String messageUser = getItem(position).getMessageUser();

        ChatMessage chatMessage = new ChatMessage(message, messageUser, userName, messageTime);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvMessage = (TextView) convertView.findViewById(R.id.message);
        TextView tvTime = (TextView) convertView.findViewById(R.id.time);
        TextView tvDisplayName = (TextView) convertView.findViewById(R.id.display_name);

        tvMessage.setText(message);
        tvTime.setText(DateFormat.format("h:mma dd-MM-yyyy", messageTime));
        tvDisplayName.setText(userName);

        return convertView;

    }
}
