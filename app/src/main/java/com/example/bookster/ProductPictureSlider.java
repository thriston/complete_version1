package com.example.bookster;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class ProductPictureSlider extends PagerAdapter {
    private int[] image_resources = {
            R.drawable.book1,
            R.drawable.book2,
            R.drawable.book3,
            R.drawable.book4,
    };

    private Context ctx;
    private LayoutInflater layoutInflater;

    public ProductPictureSlider(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return image_resources.length;
    }
    @Override
    public  boolean isViewFromObject(View view, Object object){
        return (view == (ConstraintLayout) object);
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position){
        layoutInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View item_view = layoutInflater.inflate(R.layout.productpicures,container,false);

        ImageView imageView = item_view.findViewById(R.id.slider_image);

        imageView.setImageResource(image_resources[position]);
        container.addView(item_view);

        return item_view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object){
        container.removeView((ConstraintLayout) object);
    }
}
