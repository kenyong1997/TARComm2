package com.turkfyp.tarcomm2.DatabaseObjects;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.turkfyp.tarcomm2.DatabaseObjects.ViewPagerModel;
import com.turkfyp.tarcomm2.R;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ViewPagerAdapter extends PagerAdapter{

    private List<ViewPagerModel> contents;
    private Context context;
    RequestOptions options;

    @Override
    public int getCount() {
        return contents.size();
    }

    public ViewPagerAdapter(List<ViewPagerModel> contents, Context context) {
        this.contents = contents;
        this.context = context;

        //For Glide image
        options = new RequestOptions()
                .override(1500,2000)
                .fitCenter()
                .placeholder(R.drawable.background_white)
                .error(R.drawable.background_white);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (LinearLayout)object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_contents,container,false);
        container.addView(view);

        ImageView imageView = (ImageView) view.findViewById(R.id.highlight_event);
//        getImage(contents.get(position).getImage(),imageView);

        //Load image into imageViewer with Glide
        Glide.with(context).load(contents.get(position).getImage()).apply(options).into(imageView);

        TextView name,desc,location;

        name = (TextView) view.findViewById(R.id.event_name);
        name.setText(contents.get(position).getName());

        desc = (TextView) view.findViewById(R.id.desc);
        desc.setText(contents.get(position).getName());

        location = (TextView) view.findViewById(R.id.venue);
        location.setText(contents.get(position).getLocation());
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }
}