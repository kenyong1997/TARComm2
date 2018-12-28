package com.turkfyp.tarcomm2.DatabaseObjects;

import android.content.Context;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.CircularProgressDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.turkfyp.tarcomm2.R;
import com.turkfyp.tarcomm2.activity.EventActivity;


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

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        //For Glide image
        options = new RequestOptions()
                .override(1500,2000)
                .fitCenter()
                .placeholder(circularProgressDrawable)
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

        //Load image into imageViewer with Glide
        Glide.with(context).load(contents.get(position).getImage()).apply(options).into(imageView);

        TextView name,desc,location;

        name = (TextView) view.findViewById(R.id.event_name);
        name.setText(contents.get(position).getName());

        desc = (TextView) view.findViewById(R.id.desc);
        desc.setText(contents.get(position).getDesc());

        location = (TextView) view.findViewById(R.id.venue);
        location.setText(contents.get(position).getLocation());

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context,EventActivity.class);

                context.startActivity(i);
            }
        });
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }
}
