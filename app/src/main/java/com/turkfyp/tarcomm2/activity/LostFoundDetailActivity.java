package com.turkfyp.tarcomm2.activity;

import android.app.ProgressDialog;
import android.content.Intent;
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

    protected TextView tvDetailLostItemName, tvDetailLostItemDesc, tvDetailLostItemOwner,tvDetailOwnerContact,tvDetailLostDate;
    protected ImageView imageViewLostItem,ivEditItem,ivDeleteItem;
    protected String ownerContact, itemOwner, lostItemName, lostItemDesc, lostDate,email;
    protected Boolean checkYourUpload;
    protected Bitmap image;
    protected String imageURL,itemCategory;

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
        tvDetailLostDate = (TextView)findViewById(R.id.tvDetailLostDate);
        imageViewLostItem= (ImageView) findViewById(R.id.ivLostItemImage);
        ivDeleteItem = (ImageView) findViewById(R.id.ivDeleteItem);
        ivEditItem = (ImageView) findViewById(R.id.ivEditItem);
        pDialog = new ProgressDialog(this);


        //get the extras and values
        Bundle extras = getIntent().getExtras();
        ownerContact = extras.getString("lostItemContactNo");
        itemOwner=extras.getString("lostItemContactName");
        lostItemName=extras.getString("lostItemName");
        lostItemDesc=extras.getString("lostItemDesc");
        itemCategory=extras.getString("lostCategory");
        lostDate = extras.getString("lostDate");
        checkYourUpload=extras.getBoolean("checkYourUpload");
        email =extras.getString("email");

        if(checkYourUpload){
            ivEditItem.setVisibility(View.VISIBLE);
            ivDeleteItem.setVisibility(View.VISIBLE);
        }

        //(WILL BE ADDED IF NEEDED)
        //if the userFullName logged in is the seller of item
//        String checkItemSeller = itemSeller.toUpperCase();
//        String checkLoginUser = MainActivity.LOGGED_IN_USER.toUpperCase();

        //set the text and image by using extras value
        tvDetailLostItemName.setText(lostItemName);
        tvDetailLostItemOwner.setText(itemOwner);
        tvDetailLostItemDesc.setText(lostItemDesc);
        tvDetailOwnerContact.setText(ownerContact);
        tvDetailLostDate.setText(lostDate);

        image = extras.getParcelable("LostImage");
        imageViewLostItem.setImageBitmap(image);

        imageURL = extras.getString("LostImageURL");
        itemID = Integer.parseInt(imageURL.split("=")[1]);
    }

    public void onBackClicked(View view){
        finish();
    }
    public void onSellerClicked(View view){

        Intent sellerIntent = new Intent(this,ViewOtherProfileActivity.class);
        sellerIntent.putExtra("email",email);

        startActivity(sellerIntent);
    }
    public void onEditLostItemClicked(View view){

        Intent itemDetailIntent = new Intent(this,EditLostItemActivity.class);
        itemDetailIntent.putExtra("lostItemContactNo",ownerContact);
        itemDetailIntent.putExtra("lostItemContactName",itemOwner);
        itemDetailIntent.putExtra("lostItemName",lostItemName);
        itemDetailIntent.putExtra("lostItemDesc",lostItemDesc);
        itemDetailIntent.putExtra("lostDate",lostDate);
        itemDetailIntent.putExtra("itemCategory",itemCategory);
        itemDetailIntent.putExtra("Image", image);
        itemDetailIntent.putExtra("ImageURL", imageURL);

        startActivity(itemDetailIntent);
    }
}

