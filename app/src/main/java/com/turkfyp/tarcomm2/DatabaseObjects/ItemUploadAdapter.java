package com.turkfyp.tarcomm2.DatabaseObjects;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.turkfyp.tarcomm2.R;

import java.util.HashMap;
import java.util.List;

public class ItemUploadAdapter extends BaseExpandableListAdapter {

    RequestOptions options;
    private Context _context;

    //header titles
    private List<String> _listDataHeader;

    // child data in format of header title, child title
    private HashMap<String, List<Item>> _listDataChild;

    public ItemUploadAdapter(Context context, List<String> listDataHeader,
                                  HashMap<String, List<Item>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;

        //For Glide image
        options = new RequestOptions()
                .centerCrop()
//                .signature(new MediaStoreSignature("","",0))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
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

        Item item = (Item) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_records, null);
        }

        TextView tvItemName, tvItemPrice,tvItemSeller;
        ImageView ivTradingImage,ivPrice;

        tvItemName = (TextView) convertView.findViewById(R.id.tvItemName);
        tvItemPrice = (TextView) convertView.findViewById(R.id.tvItemPrice);
        tvItemSeller= (TextView) convertView.findViewById(R.id.tvItemSeller);
        ivTradingImage = (ImageView) convertView.findViewById(R.id.ivItemImage);
        ivPrice = (ImageView) convertView.findViewById(R.id.ivPrice);

        tvItemName.setText( item.getItemName());
        tvItemSeller.setText(item.getSellerName());
        if(!item.getItemCategory().equals("WTT")){
            tvItemPrice.setText(String.format("RM %.2f", Double.parseDouble(item.getItemPrice())));

        }else{
            tvItemPrice.setText(null);
            ivPrice.setVisibility(View.GONE);
        }

        //Load image into imageViewer with Glide
        Glide.with(_context).load(item.getImageURL()).apply(options).into(ivTradingImage);

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
            convertView = infalInflater.inflate(R.layout.item_upload_header, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.tvItemUploadHeader);
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
