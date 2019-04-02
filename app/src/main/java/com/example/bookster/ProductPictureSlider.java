package com.example.bookster;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;


public class ProductPictureSlider extends PagerAdapter {
    private int[] image_resources = {
            R.drawable.book1,
            R.drawable.book2,
            R.drawable.book3,
            R.drawable.book4,
    };
    Bitmap image;
    private ArrayList<String> images;
    private Context ctx;
    private LayoutInflater layoutInflater;

    public ProductPictureSlider(Context ctx, ArrayList<String> images) {
        this.ctx = ctx;
        this.images = images;
    }

    @Override
    public int getCount()
    {
        //return image_resources.length;
        return this.images.size();
    }

    @Override
    public  boolean isViewFromObject(View view, Object object){
        return view == (object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position){
        layoutInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View item_view = layoutInflater.inflate(R.layout.productpicures,container,false);

        ImageView imageView = item_view.findViewById(R.id.slider_image);


        //Glide.with(ctx).load(images.get(position)).into(imageView);
        Glide.with(ctx).load(images.get(position)).apply(new RequestOptions().placeholder(R.drawable.img_placeholder)).error(R.drawable.image_placeholder).fitCenter().into(imageView);

        container.addView(item_view);

        return item_view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object){
        container.removeView((ConstraintLayout) object);
    }



}
