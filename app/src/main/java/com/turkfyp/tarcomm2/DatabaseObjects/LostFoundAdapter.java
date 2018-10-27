package com.turkfyp.tarcomm2.DatabaseObjects;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.turkfyp.tarcomm2.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class LostFoundAdapter extends ArrayAdapter<LostFound> {

    public LostFoundAdapter(Activity context, int resource, List<LostFound> list) {
        super(context, resource, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LostFound lostFound = getItem(position);

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.lostfound_records, parent, false);

        TextView tvLostItemName, tvLostItemDate, tvLostItemOwner;
        ImageView ivLostItemImage;

        tvLostItemName = (TextView) rowView.findViewById(R.id.tvLostItemName);
        tvLostItemDate = (TextView) rowView.findViewById(R.id.tvLostItemDate);
        tvLostItemOwner = (TextView)rowView.findViewById(R.id.tvLostItemOwner);
        ivLostItemImage = (ImageView) rowView.findViewById(R.id.ivLostItemImage);

        tvLostItemName.setText(lostFound.getLostItemName());
        tvLostItemDate.setText(lostFound.getLostDate());
        tvLostItemOwner.setText(lostFound.getContactName());
        getImage(lostFound.getLostItemURL(), ivLostItemImage);
        return rowView;
    }

    private void getImage(String urlToImage, final ImageView ivImage) {
        class GetImage extends AsyncTask<String, Void, Bitmap> {
            ProgressDialog loading;

            @Override
            protected Bitmap doInBackground(String... params) {
                URL url = null;
                Bitmap image = null;

                String urlToImage = params[0];
                try {
                    url = new URL(urlToImage);
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return image;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(getContext(), "Downloading Image...", "Please wait...", true, true);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                //loading.dismiss();
                ivImage.setImageBitmap(bitmap);
            }
        }
        GetImage gi = new GetImage();
        gi.execute(urlToImage);
    }
}
