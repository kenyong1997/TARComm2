package com.turkfyp.tarcomm2.activity;

import android.content.Context;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.turkfyp.tarcomm2.DatabaseObjects.Event;
import com.turkfyp.tarcomm2.R;


import java.util.List;

public class ViewPagerAdapter extends PagerAdapter{

    private List<ViewPagerModel> contents;
    private Context context;

    @Override
    public int getCount() {
        return contents.size();
    }

    public ViewPagerAdapter(List<ViewPagerModel> contents, Context context) {
        this.contents = contents;
        this.context = context;
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
        imageView.setImageResource(contents.get(position).getImages());

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
