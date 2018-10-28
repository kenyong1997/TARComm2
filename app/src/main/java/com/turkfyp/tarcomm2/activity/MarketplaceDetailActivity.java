package com.turkfyp.tarcomm2.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.turkfyp.tarcomm2.DatabaseObjects.Item;
import com.turkfyp.tarcomm2.R;

public class MarketplaceDetailActivity extends AppCompatActivity {

    protected TextView tvDetailItemName, tvDetailItemPrice, tvDetailItemDesc, tvDetailItemSeller,tvDetailSellerContact;
    protected ImageView imageViewDetailItem,ivEditItem,ivDeleteItem,ivItemPrice;
    protected LinearLayout llItemPrice;
    protected String sellerContact, itemSeller, itemName, itemDesc, itemPrice, imageURL, itemCategory;
    protected Boolean checkYourUpload,checkWTT;
    protected Bitmap image;

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


        //Link code to UI
        tvDetailItemName = (TextView) findViewById(R.id.tvDetailItemName);
        tvDetailItemPrice = (TextView) findViewById(R.id.tvDetailItemPrice);
        tvDetailItemDesc = (TextView) findViewById(R.id.tvDetailItemDesc);
        tvDetailItemSeller = (TextView) findViewById(R.id.tvDetailItemSeller);
        tvDetailSellerContact = (TextView)findViewById(R.id.tvDetailSellerContact);
        imageViewDetailItem = (ImageView) findViewById(R.id.imageViewDetailItem);
        ivDeleteItem = (ImageView)findViewById(R.id.ivDeleteItem);
        ivEditItem = (ImageView)findViewById(R.id.ivEditItem);
        ivItemPrice = (ImageView) findViewById(R.id.ivItemPrice);
        llItemPrice = (LinearLayout) findViewById(R.id.llItemPrice);
        pDialog = new ProgressDialog(this);


        //get the extras and values
        Bundle extras = getIntent().getExtras();
        itemSeller = extras.getString("itemSeller");
        sellerContact = extras.getString("sellerContact");
        itemName = extras.getString("itemName");
        itemDesc = extras.getString("itemDesc");
        itemPrice = extras.getString("itemPrice");
        checkYourUpload = extras.getBoolean("checkYourUpload");
        checkWTT = extras.getBoolean("checkWTT");
        itemCategory = extras.getString("itemCategory");



        //(WILL BE ADDED IF NEEDED)
        //if the userFullName logged in is the seller of item
//        String checkItemSeller = itemSeller.toUpperCase();
//        String checkLoginUser = MainActivity.LOGGED_IN_USER.toUpperCase();

        //set the text and image by using extras value
        tvDetailItemName.setText(itemName);
        tvDetailItemPrice.setText(String.format("RM %.2f", Double.parseDouble(itemPrice)));
        tvDetailItemSeller.setText(itemSeller);
        tvDetailItemDesc.setText(itemDesc);
        tvDetailSellerContact.setText(sellerContact);

        if(checkYourUpload){
            ivEditItem.setVisibility(View.VISIBLE);
            ivDeleteItem.setVisibility(View.VISIBLE);
        }
        if(checkWTT){
            tvDetailItemPrice.setVisibility(View.GONE);
            ivItemPrice.setVisibility(View.GONE);
            llItemPrice.setVisibility(View.GONE);
        }
        image = extras.getParcelable("Image");
        imageViewDetailItem.setImageBitmap(image);

        imageURL = extras.getString("ImageURL");
        itemID = Integer.parseInt(imageURL.split("=")[1]);


    }

    public void onEditItemClicked(View view){

        Intent itemDetailIntent = new Intent(this,EditUploadItemActivity.class);
        itemDetailIntent.putExtra("itemCategory",itemCategory);
        itemDetailIntent.putExtra("itemName",itemName);
        itemDetailIntent.putExtra("itemPrice",itemPrice);
        itemDetailIntent.putExtra("itemDesc",itemDesc);
        itemDetailIntent.putExtra("itemCategory",itemCategory);
        itemDetailIntent.putExtra("Image", image);
        itemDetailIntent.putExtra("ImageURL", imageURL);

        startActivity(itemDetailIntent);
    }
    public void onBackClicked(View view){
        finish();
    }
}
