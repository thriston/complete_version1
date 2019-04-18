package com.example.bookster;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RequestListAdapter extends ArrayAdapter<PurchaseRequest> {
    private Context mContext;
    private int mResource;

    public RequestListAdapter(Context context, int resource, ArrayList<PurchaseRequest> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String message = getItem(position).getMessage();
        String senderUserName = getItem(position).getSenderName();
        long time = getItem(position).getDate();
        Product product = getItem(position).getProduct();
        String requestID = getItem(position).getID();
        String location = getItem(position).getLocation();
        String status = getItem(position).getStatus();
        String senderUID = getItem(position).getSenderUID();



        PurchaseRequest purchaseRequest = new PurchaseRequest(requestID,
                message,
                location,
                status,
                senderUID,
                senderUserName,
                product,
                time
        );

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvMessage = (TextView) convertView.findViewById(R.id.message);
        TextView tvTime = (TextView) convertView.findViewById(R.id.time);
        TextView tvName = (TextView) convertView.findViewById(R.id.name);
        TextView tvProduct = (TextView) convertView.findViewById(R.id.product);
        TextView tvStatus = (TextView) convertView.findViewById(R.id.status);

        tvMessage.setText(message);
        tvTime.setText(DateFormat.format("h:mma dd-MM-yyyy", time));
        tvName.setText(senderUserName);
        tvProduct.setText(product.getName());
        tvStatus.setText(status);
        return convertView;

    }
}
