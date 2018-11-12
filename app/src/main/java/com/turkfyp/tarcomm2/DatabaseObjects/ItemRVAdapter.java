package com.turkfyp.tarcomm2.DatabaseObjects;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.content.MimeTypeFilter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.turkfyp.tarcomm2.R;
import com.turkfyp.tarcomm2.activity.MarketplaceDetailActivity;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ItemRVAdapter extends RecyclerView.Adapter<ItemRVAdapter.MyViewHolder> {

    RequestOptions options;
    private Context mContext ;
    private List<Item> itemList;

    Bitmap bitmap;

    public ItemRVAdapter(Context mContext, List lst) {
        this.mContext = mContext;
        this.itemList = lst;

        //For Glide image
        options = new RequestOptions()
                .centerCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.background_white)
                .error(R.drawable.background_white);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.tvItemName.setText(itemList.get(position).getItemName());
        holder.tvItemSeller.setText(itemList.get(position).getSellerName());
        holder.tvItemPrice.setText(itemList.get(position).getItemPrice());

        // load image using Glide
        Glide.with(mContext).load(itemList.get(position).getImageURL()).apply(options).into(holder.ivTradingImage);

        if(!itemList.get(position).getItemCategory().equals("WTT")){
            holder.tvItemPrice.setText(String.format("RM %.2f", Double.parseDouble(itemList.get(position).getItemPrice())));

        }else{
            holder.tvItemPrice.setText(null);
            holder.ivPrice.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.item_records, parent, false);

        //OnClick Listener for RecyclerView
        final MyViewHolder viewHolder = new MyViewHolder(view);
        viewHolder.item_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                convertImage(viewHolder.getAdapterPosition());
//                ImageView ivImage = (ImageView) view.findViewById(R.id.ivItemImage);
//                ivImage.buildDrawingCache();
//                Bitmap image = ivImage.getDrawingCache();

            }
        });

        return viewHolder;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvItemName, tvItemPrice,tvItemSeller;
        ImageView ivTradingImage,ivPrice;
        LinearLayout item_container;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvItemName = (TextView) itemView.findViewById(R.id.tvItemName);
            tvItemPrice = (TextView) itemView.findViewById(R.id.tvItemPrice);
            tvItemSeller= (TextView) itemView.findViewById(R.id.tvItemSeller);
            ivTradingImage = (ImageView) itemView.findViewById(R.id.ivItemImage);
            ivPrice = (ImageView) itemView.findViewById(R.id.ivPrice);
            item_container = (LinearLayout)itemView.findViewById(R.id.item_container);
        }
    }

    //Get Profile Image for Navigation Menu
    private void convertImage(final int position){
        class ConvertImage extends AsyncTask<String, Void, Bitmap> {

            @Override
            protected Bitmap doInBackground(String... strings) {
                String imageURL = itemList.get(position).getImageURL();

                try {
                    bitmap = Glide.with(mContext)
                            .asBitmap()
                            .load(imageURL)
                            .apply(options)
                            .submit(200,200)
                            .get();

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                Intent itemDetailIntent = new Intent(mContext,MarketplaceDetailActivity.class);

                itemDetailIntent.putExtra("itemName", itemList.get(position).getItemName());
                itemDetailIntent.putExtra("itemPrice",itemList.get(position).getItemPrice());
                itemDetailIntent.putExtra("itemDesc",itemList.get(position).getItemDescription());
                itemDetailIntent.putExtra("itemSeller",itemList.get(position).getSellerName());
                itemDetailIntent.putExtra("sellerContact",itemList.get(position).getSellerContact());
                itemDetailIntent.putExtra("email",itemList.get(position).getEmail());

                if(itemList.get(position).getItemCategory().equals("WTT"))
                    itemDetailIntent.putExtra("checkWTT",true);
                else
                    itemDetailIntent.putExtra("checkYourUpload",false);

                itemDetailIntent.putExtra("Image", bitmap);
                itemDetailIntent.putExtra("ImageURL", itemList.get(position).getImageURL());

                mContext.startActivity(itemDetailIntent);
            }
        }
        ConvertImage convertImage = new ConvertImage();
        convertImage.execute();
    }
}
