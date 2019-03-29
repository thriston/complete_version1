package com.example.bookster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MyProductListAdapter extends ArrayAdapter<Product> {
    private Context mContext;
    int mResource;

    public MyProductListAdapter(Context context, int resource, ArrayList<Product> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String name = getItem(position).getName();
        String details = getItem(position).getDetails();
        //String price = getItem(position).getPrice();
        int views = getItem(position).getViews();
        int bids = getItem(position).getBids();
        String quantity = getItem(position).getQuantity();


        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent,false);

        TextView product_name = (TextView) convertView.findViewById(R.id.my_product_name);
        TextView product_details = (TextView) convertView.findViewById(R.id.my_product_description);
        TextView product_views = (TextView) convertView.findViewById(R.id.my_views);
        TextView product_bids = (TextView) convertView.findViewById(R.id.my_bids);
        TextView product_quantity = (TextView) convertView.findViewById(R.id.my_quantity);
        //TextView product_price = (TextView) convertView.findViewById(R.id.product_price);

        product_name.setText(name);
        product_details.setText(details);
//        product_views.setText(views);
//        product_bids.setText(bids);
//        product_quantity.setText(quantity);
        product_views.setText("102 Views");
        product_bids.setText("10 Bids");
        product_quantity.setText("4 in Stock");







        return convertView;
    }
}
