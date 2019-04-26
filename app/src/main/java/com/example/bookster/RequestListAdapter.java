package com.example.bookster;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RequestListAdapter extends ArrayAdapter<PurchaseRequest> {
    private Context mContext;
    private int mResource;
    private DatabaseReference mDatabase;
    ViewHolder purchaseViewHolder = null;

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
        final Product product = getItem(position).getProduct();
        final String requestID = getItem(position).getID();
        String location = getItem(position).getLocation();
        String status = getItem(position).getStatus();
        final String senderUID = getItem(position).getSenderUID();






        if(convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            ViewHolder viewHolder = new ViewHolder();


            viewHolder.tvMessage = (TextView) convertView.findViewById(R.id.message);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.tvProduct = (TextView) convertView.findViewById(R.id.product);
            viewHolder.tvPrice = (TextView) convertView.findViewById(R.id.price);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.time);
            viewHolder.tvSafeZone = (TextView) convertView.findViewById(R.id.safe_zone);
            viewHolder.tvStatus = (TextView) convertView.findViewById(R.id.status);

            viewHolder.accept = (Button) convertView.findViewById(R.id.accept);
            viewHolder.reject = (Button) convertView.findViewById(R.id.reject);
            //viewHolder.call = (Button) convertView.findViewById(R.id.call);
            viewHolder.chat = (Button) convertView.findViewById(R.id.chat);


            convertView.setTag(viewHolder);
        }
        else
        {
            purchaseViewHolder = (ViewHolder) convertView.getTag();
            purchaseViewHolder.tvMessage.setText(message);
            purchaseViewHolder.tvName.setText(senderUserName);
            purchaseViewHolder.tvProduct.setText(product.getName());
            purchaseViewHolder.tvPrice.setText("$"+product.getPrice());
            purchaseViewHolder.tvTime.setText(DateFormat.format("h:mma dd-MM-yyyy", time));
            purchaseViewHolder.tvSafeZone.setText(location);
            purchaseViewHolder.tvStatus.setText(status);

            if(status.equals("Accepted"))
            {
                purchaseViewHolder.tvStatus.setTextColor(Color.rgb(0, 255, 0));
                purchaseViewHolder.accept.setVisibility(View.GONE);
                purchaseViewHolder.reject.setVisibility(View.GONE);
            }
            else if(status.equals("Rejected"))
            {
                purchaseViewHolder.tvStatus.setTextColor(Color.rgb(255, 0, 0));
                purchaseViewHolder.accept.setVisibility(View.GONE);
                purchaseViewHolder.reject.setVisibility(View.GONE);
            }
            else
            {
                purchaseViewHolder.tvStatus.setTextColor(Color.rgb(204, 204, 0));
                purchaseViewHolder.accept.setVisibility(View.VISIBLE);
                purchaseViewHolder.reject.setVisibility(View.VISIBLE);
            }



            purchaseViewHolder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("Requests").child("Purchase").child("received").child(requestID).child("status");
                    mDatabase.setValue("Accepted");

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(senderUID)
                            .child("Requests").child("Purchase").child("sent").child(requestID).child("status");
                    mDatabase.setValue("Accepted");

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Requests").child("Purchase")
                            .child(requestID).child("status");
                    mDatabase.setValue("Accepted");

                    product.addTransaction();
                    DatabaseReference db1,db2;
                    db1 = FirebaseDatabase.getInstance().getReference().child("Products").child(product.getID()).child("ntransactions");
                    db1.setValue(product.getNTransactions());
                    db2 = FirebaseDatabase.getInstance().getReference().child("users").child(product.getSeller().myUID).child("Products").child(product.getID()).child("nTransactions");
                    db2.setValue(product.getNTransactions());

                    mDatabase.keepSynced(true);


                    Toast.makeText(mContext, "Accepted Purchase", Toast.LENGTH_SHORT).show();

                }
            });

            purchaseViewHolder.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("Requests").child("Purchase").child("received").child(requestID).child("status");
                    mDatabase.setValue("Rejected");

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(senderUID)
                            .child("Requests").child("Purchase").child("sent").child(requestID).child("status");
                    mDatabase.setValue("Rejected");

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Requests").child("Purchase")
                            .child(requestID).child("status");
                    mDatabase.setValue("Rejected");

                    mDatabase.keepSynced(true);

                    Toast.makeText(mContext, "Rejected Purchase", Toast.LENGTH_SHORT).show();
                }
            });


            purchaseViewHolder.chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myintent = new Intent(getContext(), RequestMessageActivity.class);
                    //myintent.putExtra("productObj", barterProduct);
                    myintent.putExtra("myUserName", product.getSeller().fullname);
                    myintent.putExtra("receiverUID",senderUID);
                    getContext().startActivity(myintent);
                }
            });




        }

        return convertView;



//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        convertView = inflater.inflate(mResource, parent, false);
//
//        TextView tvMessage = (TextView) convertView.findViewById(R.id.message);
//        TextView tvTime = (TextView) convertView.findViewById(R.id.time);
//        TextView tvName = (TextView) convertView.findViewById(R.id.name);
//        TextView tvProduct = (TextView) convertView.findViewById(R.id.product);
//        TextView tvStatus = (TextView) convertView.findViewById(R.id.status);
//        TextView tvPrice = (TextView) convertView.findViewById(R.id.price);
//        TextView tvSafeZone = (TextView) convertView.findViewById(R.id.safe_zone);
//
//        tvMessage.setText(message);
//        tvPrice.setText("$"+product.getPrice());
//        tvTime.setText(DateFormat.format("h:mma dd-MM-yyyy", time));
//        tvName.setText(senderUserName);
//        tvProduct.setText(product.getName());
//        tvStatus.setText(status);
//        tvSafeZone.setText(location);
//        return convertView;

    }

    public class ViewHolder {

        TextView tvMessage ;
        TextView tvName;
        TextView tvProduct ;
        TextView tvPrice;
        TextView tvTime ;
        TextView tvSafeZone;
        TextView tvStatus ;
        Button accept;
        //Button call;
        Button chat;
        Button reject;

    }
}
