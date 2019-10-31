package com.example.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.pools.R;

public class FullSizeAdapter extends PagerAdapter {

    Context context;
    String[] images;
    LayoutInflater layoutInflater;

    public FullSizeAdapter(Context context,String[] images){
        this.context = context;
        this.images = images;

    }


    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
       layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       View v = layoutInflater.inflate(R.layout.full_screen_item,null);

        ImageView imageView = (ImageView)v.findViewById(R.id.img);

        Glide.with(context).load(images[position]).apply(new RequestOptions().centerInside())
                .into(imageView);

        ViewPager vp = (ViewPager)container;
        vp.addView(v,0);
        return  v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //super.destroyItem(container, position, object);

        ViewPager viewPager = (ViewPager)container;
        View v = (View) object;
        viewPager.removeView(v);
    }
}
