package com.example.bookster;

import android.content.Context;
import android.support.annotation.NonNull;
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

public class ProductListAdapter extends ArrayAdapter<Product> {
    private Context mContext;
    int mResource;
    private int lastPosition = -1;


    private static class ViewHolder {
        TextView name;
        TextView details;
        TextView price;
        ImageView image;
    }


    public ProductListAdapter(Context context, int resource, ArrayList<Product> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        setupImageLoader();

        String name = getItem(position).getName();
        String details = getItem(position).getDetails();
        String price = getItem(position).getPrice();
        String mainImage = getItem(position).getMainImage();
        //ArrayList<String> images = getItem(position).getImages();

        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        ProductListAdapter.ViewHolder holder;


        if(convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent,false);

            holder= new ProductListAdapter.ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.product_name);
            holder.details = (TextView) convertView.findViewById(R.id.product_description);
            holder.price = (TextView) convertView.findViewById(R.id.product_price);
            holder.image = (ImageView) convertView.findViewById(R.id.product_image);

            result = convertView;

            convertView.setTag(holder);

        }
        else{
            holder = (ProductListAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }


        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        result.startAnimation(animation);
        lastPosition = position;

        holder.name.setText(name);
        holder.details.setText(details);
        holder.price.setText("$"+price);


        ImageLoader imageLoader = ImageLoader.getInstance();
        ;
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







//
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        convertView = inflater.inflate(mResource, parent,false);
//
//        TextView product_name = (TextView) convertView.findViewById(R.id.product_name);
//        TextView product_details = (TextView) convertView.findViewById(R.id.product_description);
//        TextView product_price = (TextView) convertView.findViewById(R.id.product_price);
//        ImageView product_image = (ImageView) convertView.findViewById(R.id.product_image);
//
//        product_name.setText(name);
//        product_details.setText(details);
//        product_price.setText("$"+price);
//
//        //Works
//        Picasso.with(mContext).load(mainImage).into(product_image);
//
//
//
//
//
//
//
//        return convertView;
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
