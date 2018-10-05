package com.turkfyp.tarcomm2.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.turkfyp.tarcomm2.R;

public class MarketplaceDetailActivity extends AppCompatActivity {

    protected TextView tvDetailItemName, tvDetailItemPrice, tvDetailItemDesc, tvDetailItemSeller;
    protected ImageView imageViewDetailItem;
    protected String sellerContact, itemSeller, itemName, itemDesc;
    protected double itemPrice;

    int itemID;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketplace_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //No Activity Title
        getSupportActionBar().setTitle(null);

        //Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Link code to UI
        tvDetailItemName = (TextView) findViewById(R.id.tvDetailItemName);
        tvDetailItemPrice = (TextView) findViewById(R.id.tvDetailItemPrice);
        tvDetailItemDesc = (TextView) findViewById(R.id.tvDetailItemDesc);
        tvDetailItemSeller = (TextView) findViewById(R.id.tvDetailItemSeller);

        imageViewDetailItem = (ImageView) findViewById(R.id.imageViewDetailItem);

        pDialog = new ProgressDialog(this);


        //get the extras and values
        Bundle extras = getIntent().getExtras();
        itemSeller = extras.getString("itemSeller");
        sellerContact = extras.getString("sellerContact");
        itemName = extras.getString("itemName");
        itemDesc = extras.getString("itemDesc");
        itemPrice = extras.getDouble("itemPrice");


        //(WILL BE ADDED IF NEEDED)
        //if the user logged in is the seller of item
//        String checkItemSeller = itemSeller.toUpperCase();
//        String checkLoginUser = MainActivity.LOGGED_IN_USER.toUpperCase();

        //set the text and image by using extras value
        tvDetailItemName.setText(itemName);
        tvDetailItemPrice.setText(String.format("Item Price: RM %.2f", itemPrice));
        tvDetailItemSeller.setText("Item Seller: " + itemSeller + ", "+ sellerContact);
        tvDetailItemDesc.setText("Item Description: " + itemDesc);

        Bitmap image = extras.getParcelable("Image");
        imageViewDetailItem.setImageBitmap(image);

        String imageURL = extras.getString("ImageURL");
        itemID = Integer.parseInt(imageURL.split("=")[1]);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}