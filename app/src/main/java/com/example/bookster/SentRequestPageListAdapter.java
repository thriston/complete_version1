package com.example.bookster;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class SentRequestPageListAdapter extends ArrayAdapter<Object> {
    private Context mContext;
    int mResource;
    ArrayList<Object> requestList = null;
    public static final int BARTER = 1;
    public static final int PURCHASE = 0;


    public SentRequestPageListAdapter(Context context, int resource, ArrayList<Object> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
        this.requestList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        boolean isBarter;

        //ChatMessage chatMessage = new ChatMessage(message, messageUser, userName, messageTime);

        if(requestList.get(position) instanceof BarterRequest)
            isBarter = true;
        else
            isBarter = false;


        if(convertView == null)
        {
            if(getItemViewType(position) == BARTER)
            {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.barter_request_item, parent, false);

            }
            else if(getItemViewType(position) == PURCHASE)
            {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.purchase_request_item, parent, false);
            }
        }


        TextView fromTV = convertView.findViewById(R.id.fromTV);
        fromTV.setText("To: ");
        if(isBarter)
        {
            //Get TextViews
            ImageView imageView = convertView.findViewById(R.id.image);
            TextView tvType = convertView.findViewById(R.id.type);
            TextView tvFrom = convertView.findViewById(R.id.from);
            TextView tvMyProduct = convertView.findViewById(R.id.my_product);
            TextView tvBarterProduct = convertView.findViewById(R.id.barter_product);
            TextView tvDate = convertView.findViewById(R.id.date);
            TextView tvStatus = convertView.findViewById(R.id.status);

            //Set TextViews
           //System.out.println("WHY CANT? "+requestList.get(position));
            BarterRequest barterRequest = (BarterRequest) requestList.get(position);

            tvType.setText(barterRequest.getType());
            tvFrom.setText(barterRequest.getMyProduct().getSeller().getFullname());
            tvMyProduct.setText(barterRequest.getSellerProduct().getName());
            tvBarterProduct.setText(barterRequest.getMyProduct().getName());
            tvDate.setText(DateFormat.format("h:mma   dd/MM/yyyy", barterRequest.getDate()));
            tvStatus.setText(barterRequest.getStatus());
            if(barterRequest.getStatus().equals("Accepted"))
            {
                tvStatus.setTextColor(Color.rgb(0, 255, 0));
            }
            else if(barterRequest.getStatus().equals("Rejected"))
            {
                tvStatus.setTextColor(Color.rgb(255, 0, 0));
            }
            else
            {
                tvStatus.setTextColor(Color.rgb(204, 204, 0));
            }
            Glide.with(getContext()).load(barterRequest.getMyProduct().getMainImage()).apply(new RequestOptions().placeholder(R.drawable.img_placeholder)).error(R.drawable.image_placeholder).fitCenter().into(imageView);


        }
        else
        {
            //Get TextViews
            ImageView imageView = convertView.findViewById(R.id.image);
            TextView tvType = convertView.findViewById(R.id.type);
            TextView tvFrom = convertView.findViewById(R.id.from);
            TextView tvPrice = convertView.findViewById(R.id.price);
            TextView tvMyProduct = convertView.findViewById(R.id.product_name);
            TextView tvDate = convertView.findViewById(R.id.date);
            TextView tvStatus = convertView.findViewById(R.id.status);

            //Set TextViews
            PurchaseRequest purchaseRequest = (PurchaseRequest) getItem(position);

            tvType.setText(purchaseRequest.getType());
            tvFrom.setText(purchaseRequest.getProduct().getSeller().getFullname());
            tvPrice.setText("$"+purchaseRequest.getProduct().getPrice());
            tvMyProduct.setText(purchaseRequest.getProduct().getName());
            tvDate.setText(DateFormat.format("h:mma   dd/MM/yyyy", purchaseRequest.getDate()));
            tvStatus.setText(purchaseRequest.getStatus());
            if(purchaseRequest.getStatus().equals("Accepted"))
            {
                tvStatus.setTextColor(Color.rgb(0, 255, 0));
            }
            else if(purchaseRequest.getStatus().equals("Rejected"))
            {
                tvStatus.setTextColor(Color.rgb(255, 0, 0));
            }
            else
            {
                tvStatus.setTextColor(Color.rgb(204, 204, 0));
            }
            Glide.with(getContext()).load(purchaseRequest.getProduct().getMainImage()).apply(new RequestOptions().placeholder(R.drawable.img_placeholder)).error(R.drawable.image_placeholder).fitCenter().into(imageView);

        }


        return convertView;

    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        Object object = requestList.get(position);
        if(object instanceof BarterRequest)
        {
            return BARTER;
        }
        else
        {
            return PURCHASE;
        }



    }
}
