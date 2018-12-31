package com.turkfyp.tarcomm2.DatabaseObjects;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.turkfyp.tarcomm2.R;
import com.turkfyp.tarcomm2.activity.LostFoundDetailActivity;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

public class LostFoundRVAdapter extends RecyclerView.Adapter<LostFoundRVAdapter.MyViewHolder> {

    RequestOptions options;
    private Context mContext ;
    private List<LostFound> lostFoundList;

    Bitmap bitmap;

    public LostFoundRVAdapter(Context mContext, List lst) {
        this.mContext = mContext;
        this.lostFoundList = lst;
    }

    @Override
    public LostFoundRVAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.lostfound_records, parent, false);

        final MyViewHolder viewHolder = new MyViewHolder(view);
        viewHolder.lostFound_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent lostFoundDetailIntent = new Intent(mContext,LostFoundDetailActivity.class);

                lostFoundDetailIntent.putExtra("lostItemName",lostFoundList.get(viewHolder.getAdapterPosition()).getLostItemName());
                lostFoundDetailIntent.putExtra("email",lostFoundList.get(viewHolder.getAdapterPosition()).getEmail());
                lostFoundDetailIntent.putExtra("lostItemDesc",lostFoundList.get(viewHolder.getAdapterPosition()).getLostItemDesc());
                lostFoundDetailIntent.putExtra("lostDate", lostFoundList.get(viewHolder.getAdapterPosition()).getLostDate());
                lostFoundDetailIntent.putExtra("lostItemContactName",lostFoundList.get(viewHolder.getAdapterPosition()).getContactName());
                lostFoundDetailIntent.putExtra("lostItemContactNo",lostFoundList.get(viewHolder.getAdapterPosition()).getContactNo());
                lostFoundDetailIntent.putExtra("itemCategory",lostFoundList.get(viewHolder.getAdapterPosition()).getCategory());
                lostFoundDetailIntent.putExtra("lostLastModified",lostFoundList.get(viewHolder.getAdapterPosition()).getLastModified());

                SharedPreferences preferences = mContext.getSharedPreferences("tarcommUser", MODE_PRIVATE);
                String email = preferences.getString("email", "");
                if(lostFoundList.get(viewHolder.getAdapterPosition()).getEmail().equals(email))
                    lostFoundDetailIntent.putExtra("checkYourUpload",true);
                else
                    lostFoundDetailIntent.putExtra("checkYourUpload",false);

                convertImage(lostFoundList.get(viewHolder.getAdapterPosition()).getLostItemURL());
                lostFoundDetailIntent.putExtra("LostImage", bitmap);
                lostFoundDetailIntent.putExtra("LostImageURL", lostFoundList.get(viewHolder.getAdapterPosition()).getLostItemURL());

                mContext.startActivity(lostFoundDetailIntent);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(LostFoundRVAdapter.MyViewHolder holder, int position) {
        holder.tvLostItemName.setText(lostFoundList.get(position).getLostItemName());
        holder.tvLostItemDate.setText(lostFoundList.get(position).getLostDate());
        holder.tvLostItemOwner.setText(lostFoundList.get(position).getContactName());

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mContext);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        //For Glide image
        options = new RequestOptions()
                .centerCrop()
                .signature(new ObjectKey(lostFoundList.get(position).getLastModified()))
                .placeholder(circularProgressDrawable)
                .error(R.drawable.background_white);

        // load image using Glide
        Glide.with(mContext).load(lostFoundList.get(position).getLostItemURL()).apply(options).into(holder.ivLostItemImage);
    }

    @Override
    public int getItemCount() {
        return lostFoundList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvLostItemName, tvLostItemDate, tvLostItemOwner;
        ImageView ivLostItemImage;
        LinearLayout lostFound_container;

        public MyViewHolder(View lostFoundView) {
            super(lostFoundView);
            tvLostItemName = (TextView) lostFoundView.findViewById(R.id.tvLostItemName);
            tvLostItemDate = (TextView) lostFoundView.findViewById(R.id.tvLostItemDate);
            tvLostItemOwner = (TextView)lostFoundView.findViewById(R.id.tvLostItemOwner);
            ivLostItemImage = (ImageView) lostFoundView.findViewById(R.id.imageViewLostItemImage);
            lostFound_container = (LinearLayout)itemView.findViewById(R.id.lostFound_container);
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

