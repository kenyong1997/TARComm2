package com.turkfyp.tarcomm2.DatabaseObjects;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.turkfyp.tarcomm2.R;

public class LostFoundUploadAdapter extends BaseExpandableListAdapter {

    RequestOptions options;
    private Context _context;

    // header titles
    private List<String> _listDataHeader;

    // child data in format of header title, child title
    private HashMap<String, List<LostFound>> _listDataChild;

    public LostFoundUploadAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<LostFound>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;

        //For Glide image
        options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.background_white)
                .error(R.drawable.background_white);
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        LostFound lostFound = (LostFound) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.lostfound_records, null);
        }

        TextView tvLostItemName, tvLostItemDate, tvLostItemOwner;
        ImageView ivLostItemImage;

        tvLostItemName = (TextView) convertView.findViewById(R.id.tvLostItemName);
        tvLostItemDate = (TextView) convertView.findViewById(R.id.tvLostItemDate);
        tvLostItemOwner = (TextView) convertView.findViewById(R.id.tvLostItemOwner);
        ivLostItemImage = (ImageView) convertView.findViewById(R.id.imageViewLostItemImage);

        tvLostItemName.setText(lostFound.getLostItemName());
        tvLostItemDate.setText(lostFound.getLostDate());
        tvLostItemOwner.setText(lostFound.getContactName());

        //Load image into imageViewer with Glide
        Glide.with(_context).load(lostFound.getLostItemURL()).apply(options).into(ivLostItemImage);


        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.lostfound_upload_header, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.tvLostFoundUploadHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
