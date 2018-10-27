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

public class LostFoundDetailActivity extends AppCompatActivity {

    protected TextView tvDetailLostItemName, tvDetailLostItemDesc, tvDetailLostItemOwner,tvDetailOwnerContact;
    protected ImageView imageViewLostItem;
    protected String ownerContact, itemOwner, lostItemName, lostItemDesc;

    int itemID;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_found_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //No Activity Title
        getSupportActionBar().setTitle(null);


        //Link code to UI
        tvDetailLostItemName = (TextView) findViewById(R.id.tvDetailLostItemName);
        tvDetailLostItemDesc = (TextView) findViewById(R.id.tvDetailLostItemDesc);
        tvDetailLostItemOwner = (TextView) findViewById(R.id.tvDetailLostItemOwner);
        tvDetailOwnerContact = (TextView)findViewById(R.id.tvDetailOwnerContact);
        imageViewLostItem= (ImageView) findViewById(R.id.ivLostItemImage);

        pDialog = new ProgressDialog(this);


        //get the extras and values
        Bundle extras = getIntent().getExtras();
        ownerContact = extras.getString("lostItemContactNo");
        itemOwner=extras.getString("lostItemContactName");
        lostItemName=extras.getString("lostItemName");
        lostItemDesc=extras.getString("lostItemDesc");



        //(WILL BE ADDED IF NEEDED)
        //if the userFullName logged in is the seller of item
//        String checkItemSeller = itemSeller.toUpperCase();
//        String checkLoginUser = MainActivity.LOGGED_IN_USER.toUpperCase();

        //set the text and image by using extras value
        tvDetailLostItemName.setText(lostItemName);
        tvDetailLostItemOwner.setText(itemOwner);
        tvDetailLostItemDesc.setText(lostItemDesc);
        tvDetailOwnerContact.setText(ownerContact);

        Bitmap image = extras.getParcelable("Image");
        imageViewLostItem.setImageBitmap(image);

        String imageURL = extras.getString("ImageURL");
        itemID = Integer.parseInt(imageURL.split("=")[1]);
    }

    public void onBackClicked(View view){
        finish();
    }
}

