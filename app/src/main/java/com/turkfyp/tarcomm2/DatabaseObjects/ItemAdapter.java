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

public class ItemAdapter extends ArrayAdapter<Item> {

    public ItemAdapter(Activity context, int resource, List<Item> list) {
        super(context, resource, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item item = getItem(position);

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.item_records, parent, false);

        TextView tvItemName, tvItemPrice;
        ImageView ivTradingImage;

        tvItemName = (TextView) rowView.findViewById(R.id.tvItemName);
        tvItemPrice = (TextView) rowView.findViewById(R.id.tvItemPrice);
        ivTradingImage = (ImageView) rowView.findViewById(R.id.ivItemImage);

        tvItemName.setText( item.getItemName());
        tvItemPrice.setText(String.format("RM %.2f", Double.parseDouble(item.getItemPrice())));
        getImage(item.getImageURL(), ivTradingImage);
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
