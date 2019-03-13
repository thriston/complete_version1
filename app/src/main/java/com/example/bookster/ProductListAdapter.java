package com.example.bookster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ProductListAdapter extends ArrayAdapter<Product> {
    private Context mContext;
    int mResource;

    public ProductListAdapter(Context context, int resource, ArrayList<Product> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String name = getItem(position).getName();
        String details = getItem(position).getDetails();
        String price = getItem(position).getPrice();


        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent,false);

        TextView product_name = (TextView) convertView.findViewById(R.id.product_name);
        TextView product_details = (TextView) convertView.findViewById(R.id.product_description);
        TextView product_price = (TextView) convertView.findViewById(R.id.product_price);

        product_name.setText(name);
        product_details.setText(details);
        product_price.setText("$"+price);






        return convertView;
    }
}
