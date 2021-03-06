package com.example.bookster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
//This class is the same as "Product List Adapter" but just receives different input
public class MyProductListAdapter extends ArrayAdapter<Product> {
    private Context mContext;
    int mResource;
    private int lastPosition = -1;

    //ViewHolder for displaying the product in a list
    private static class ViewHolder {
        TextView name;
        TextView details;
        TextView views;
        TextView bids;
        TextView quantity;
        ImageView image;
    }

    public MyProductListAdapter(Context context, int resource, ArrayList<Product> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    //Inflates an individual product item
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String name = getItem(position).getName();
        String details = getItem(position).getDetails();
        int views = getItem(position).getViews();
        int transactions = getItem(position).getNTransactions();
        String quantity = getItem(position).getQuantity();
        String mainImage = getItem(position).getMainImage();
        final View result;

        //ViewHolder object
        MyProductListAdapter.ViewHolder holder;


        if(convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent,false);
            holder= new MyProductListAdapter.ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.my_product_name);
            holder.details = (TextView) convertView.findViewById(R.id.my_product_description);
            holder.views = (TextView) convertView.findViewById(R.id.my_product_views);
            holder.bids = (TextView) convertView.findViewById(R.id.nSold);
            holder.quantity = (TextView) convertView.findViewById(R.id.my_product_quantity);
            holder.image = (ImageView) convertView.findViewById(R.id.my_product_image);
            result = convertView;
            convertView.setTag(holder);

        }
        else{
            holder = (MyProductListAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }
        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        result.startAnimation(animation);
        lastPosition = position;

        holder.name.setText(name);
        holder.details.setText(details);
        holder.views.setText(views+" Views");
        holder.bids.setText(transactions+" Transactions");
        holder.quantity.setText(quantity+" in stock");

        ImageLoader imageLoader = ImageLoader.getInstance();
        int defaultImage = mContext.getResources().getIdentifier("@drawable/image_failed",null,mContext.getPackageName());

        //create display options
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .showImageOnLoading(defaultImage).build();

        //download and display image from url
        imageLoader.displayImage(mainImage, holder.image, options);

        return convertView;
    }
    private void setupImageLoader(){
        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                mContext)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);
        // END - UNIVERSAL IMAGE LOADER SETUP
    }
}
