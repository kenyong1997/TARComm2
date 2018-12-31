package com.turkfyp.tarcomm2.DatabaseObjects;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.content.MimeTypeFilter;
import android.support.v4.widget.CircularProgressDrawable;
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
import com.bumptech.glide.signature.ObjectKey;
import com.turkfyp.tarcomm2.R;
import com.turkfyp.tarcomm2.activity.MarketplaceDetailActivity;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

public class ItemRVAdapter extends RecyclerView.Adapter<ItemRVAdapter.MyViewHolder> {

    RequestOptions options;
    private Context mContext ;
    private List<Item> itemList;

    Bitmap bitmap;

    public ItemRVAdapter(Context mContext, List lst) {
        this.mContext = mContext;
        this.itemList = lst;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.tvItemName.setText(itemList.get(position).getItemName());
        holder.tvItemSeller.setText(itemList.get(position).getSellerName());
        holder.tvItemPrice.setText(itemList.get(position).getItemPrice());

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mContext);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        //For Glide image
        options = new RequestOptions()
                .centerCrop()
                .signature(new ObjectKey(itemList.get(position).getItemLastModified()))
                .placeholder(circularProgressDrawable)
                .error(R.drawable.background_white);

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
                Intent itemDetailIntent = new Intent(mContext,MarketplaceDetailActivity.class);

                itemDetailIntent.putExtra("itemName", itemList.get(viewHolder.getAdapterPosition()).getItemName());
                itemDetailIntent.putExtra("itemPrice",itemList.get(viewHolder.getAdapterPosition()).getItemPrice());
                itemDetailIntent.putExtra("itemDesc",itemList.get(viewHolder.getAdapterPosition()).getItemDescription());
                itemDetailIntent.putExtra("itemSeller",itemList.get(viewHolder.getAdapterPosition()).getSellerName());
                itemDetailIntent.putExtra("sellerContact",itemList.get(viewHolder.getAdapterPosition()).getSellerContact());
                itemDetailIntent.putExtra("email",itemList.get(viewHolder.getAdapterPosition()).getEmail());
                itemDetailIntent.putExtra("itemCategory",itemList.get(viewHolder.getAdapterPosition()).getItemCategory());

                if(itemList.get(viewHolder.getAdapterPosition()).getItemCategory().equals("WTT"))
                    itemDetailIntent.putExtra("checkWTT",true);

                SharedPreferences preferences = mContext.getSharedPreferences("tarcommUser", MODE_PRIVATE);
                String email = preferences.getString("email", "");

                if(itemList.get(viewHolder.getAdapterPosition()).getItemCategory().equals("WTT"))
                    itemDetailIntent.putExtra("checkWTT",true);
                if(itemList.get(viewHolder.getAdapterPosition()).getEmail().equals(email))
                    itemDetailIntent.putExtra("checkYourUpload",true);
                else
                    itemDetailIntent.putExtra("checkYourUpload",false);


                convertImage(itemList.get(viewHolder.getAdapterPosition()).getImageURL());
                itemDetailIntent.putExtra("Image", bitmap);
                itemDetailIntent.putExtra("ImageURL", itemList.get(viewHolder.getAdapterPosition()).getImageURL());
                itemDetailIntent.putExtra("itemLastModified", itemList.get(viewHolder.getAdapterPosition()).getItemLastModified());

                mContext.startActivity(itemDetailIntent);
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

    //Convert Glide Image to bitmap
    private void convertImage(String imageURL){
        class ConvertImage extends AsyncTask<String, Void, Bitmap> {

            @Override
            protected Bitmap doInBackground(String... strings) {
                String imageURL = strings[0];

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
            }
        }
        ConvertImage convertImage = new ConvertImage();
        convertImage.execute(imageURL);
    }
}
