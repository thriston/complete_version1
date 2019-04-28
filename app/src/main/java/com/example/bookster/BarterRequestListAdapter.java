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

public class BarterRequestListAdapter extends ArrayAdapter<BarterRequest> {
    private Context mContext;
    private int mResource;
    private DatabaseReference mDatabase;
    ViewHolder barterViewHolder = null;

    public BarterRequestListAdapter(Context context, int resource, ArrayList<BarterRequest> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String message = getItem(position).getMessage();
        String senderUserName = getItem(position).getSenderName();
        long time = getItem(position).getDate();
        final Product barterProduct= getItem(position).getMyProduct();
        final Product  myProduct= getItem(position).getSellerProduct();
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
            viewHolder.tvMyProduct = (TextView) convertView.findViewById(R.id.my_product);
            viewHolder.tvBarterProduct = (TextView) convertView.findViewById(R.id.barterProduct);
            viewHolder.tvBarterDescription = (TextView) convertView.findViewById(R.id.description);
            viewHolder.tvBarterRating = (TextView) convertView.findViewById(R.id.rating);
            viewHolder.tvBarterPrice = (TextView) convertView.findViewById(R.id.price);
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
            barterViewHolder = (ViewHolder) convertView.getTag();
            barterViewHolder.tvMessage.setText(message);
            barterViewHolder.tvName.setText(senderUserName);
            barterViewHolder.tvMyProduct.setText(myProduct.getName());
            barterViewHolder.tvBarterProduct.setText(barterProduct.getName());
            barterViewHolder.tvBarterDescription.setText(barterProduct.getDetails());
            barterViewHolder.tvBarterRating.setText("10/10");
            barterViewHolder.tvBarterPrice.setText("$"+barterProduct.getPrice());
            barterViewHolder.tvTime.setText(DateFormat.format("h:mma dd-MM-yyyy", time));
            barterViewHolder.tvSafeZone.setText(location);
            barterViewHolder.tvStatus.setText(status);

            if(status.equals("Accepted"))
            {
                barterViewHolder.tvStatus.setTextColor(Color.rgb(0, 255, 0));
                barterViewHolder.accept.setVisibility(View.GONE);
                barterViewHolder.reject.setVisibility(View.GONE);
            }
            else if(status.equals("Rejected"))
            {
                barterViewHolder.tvStatus.setTextColor(Color.rgb(255, 0, 0));
                barterViewHolder.accept.setVisibility(View.GONE);
                barterViewHolder.reject.setVisibility(View.GONE);
            }
            else
            {
                barterViewHolder.tvStatus.setTextColor(Color.rgb(204, 204, 0));
                barterViewHolder.accept.setVisibility(View.VISIBLE);
                barterViewHolder.reject.setVisibility(View.VISIBLE);
            }





            barterViewHolder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("Requests").child("Barter").child("received").child(requestID).child("status");
                    mDatabase.setValue("Accepted");

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(senderUID)
                            .child("Requests").child("Barter").child("sent").child(requestID).child("status");
                    mDatabase.setValue("Accepted");

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Requests").child("Barter")
                            .child(requestID).child("status");
                    mDatabase.setValue("Accepted");

                    myProduct.addTransaction();
                    DatabaseReference db1,db2;
                    db1 = FirebaseDatabase.getInstance().getReference().child("Products").child(myProduct.getID()).child("ntransactions");
                    db1.setValue(myProduct.getNTransactions());

                    db1 = FirebaseDatabase.getInstance().getReference().child("Products").child(myProduct.getID()).child("quantity");
                    db1.setValue(myProduct.getQuantity());

                    db2 = FirebaseDatabase.getInstance().getReference().child("users").child(myProduct.getSeller().myUID).child("Products").child(myProduct.getID()).child("nTransactions");
                    db2.setValue(myProduct.getNTransactions());

                    db2 = FirebaseDatabase.getInstance().getReference().child("users").child(myProduct.getSeller().myUID).child("Products").child(myProduct.getID()).child("quantity");
                    db2.setValue(myProduct.getQuantity());

                    mDatabase.keepSynced(true);


                    Toast.makeText(mContext, "Accepted Trade of"+barterProduct.getName(), Toast.LENGTH_SHORT).show();

                }
            });

            barterViewHolder.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("Requests").child("Barter").child("received").child(requestID).child("status");
                    mDatabase.setValue("Rejected");

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(senderUID)
                            .child("Requests").child("Barter").child("sent").child(requestID).child("status");
                    mDatabase.setValue("Rejected");

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Requests").child("Barter")
                            .child(requestID).child("status");
                    mDatabase.setValue("Rejected");

                    mDatabase.keepSynced(true);
                }
            });

            barterViewHolder.chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myintent = new Intent(getContext(), RequestMessageActivity.class);
                    //myintent.putExtra("productObj", barterProduct);
                    myintent.putExtra("myUserName", myProduct.getSeller().fullname);
                    myintent.putExtra("receiverUID",senderUID);
                    getContext().startActivity(myintent);
                }
            });

        }


        return convertView;

    }


    public class ViewHolder {

        TextView tvMessage ;
        TextView tvName;
        TextView tvMyProduct ;
        TextView tvBarterProduct ;
        TextView tvBarterDescription ;
        TextView tvBarterRating ;
        TextView tvBarterPrice ;
        TextView tvTime ;
        TextView tvSafeZone;
        TextView tvStatus ;
        Button accept;
        //Button call;
        Button chat;
        Button reject;

    }

}
