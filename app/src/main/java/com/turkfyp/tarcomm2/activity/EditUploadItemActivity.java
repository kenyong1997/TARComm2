package com.turkfyp.tarcomm2.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.turkfyp.tarcomm2.DatabaseObjects.Item;
import com.turkfyp.tarcomm2.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class EditUploadItemActivity extends AppCompatActivity {
    private int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;

    ProgressDialog progressDialog;

    protected String sellerContact, itemSeller, itemName, itemDesc, itemPrice, itemCategory;
    protected ImageView imgViewEditMarketItem;
    protected int itemID;
    protected EditText etEditItemName,etEditItemDesc,etEditItemPrice;
    protected TextView tvEditItemPrice;
    protected RadioGroup rgItemCategory;
    protected RadioButton category_sell,category_buy,category_trade, rbEditItemCategory;

    private Bitmap bitmap;
    protected String newImgURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_upload_item);

        etEditItemName = (EditText)findViewById(R.id.etEditItemName);
        etEditItemDesc = (EditText)findViewById(R.id.etEditItemDesc);
        etEditItemPrice = (EditText) findViewById(R.id.etEditItemPrice);
        rgItemCategory = (RadioGroup) findViewById(R.id.rgItemCategory);
        category_sell = (RadioButton) findViewById(R.id.category_sell);
        category_buy = (RadioButton) findViewById(R.id.category_buy);
        category_trade = (RadioButton) findViewById(R.id.category_trade);
        imgViewEditMarketItem = (ImageView) findViewById(R.id.imgViewEditMarketItem);
        tvEditItemPrice = (TextView) findViewById(R.id.tvEditItemPrice);

        //get the extras and values
        Bundle extras = getIntent().getExtras();
        itemSeller = extras.getString("itemSeller");
        sellerContact = extras.getString("sellerContact");
        itemName = extras.getString("itemName");
        itemDesc = extras.getString("itemDesc");
        itemPrice = extras.getString("itemPrice");
        itemCategory = extras.getString("itemCategory");


        String imageURL = extras.getString("ImageURL");
        itemID = Integer.parseInt(imageURL.split("=")[1]);

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(this);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        //For Glide image
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(circularProgressDrawable)
                .error(R.drawable.background_white);

        Glide.with(getApplicationContext()).load(imageURL).apply(options).into(imgViewEditMarketItem);

        convertImage(imageURL);

        //set text on edittext
        etEditItemName.setText(itemName);
        etEditItemDesc.setText(itemDesc);
        etEditItemPrice.setText(itemPrice);
        if(itemCategory.equals("WTS")){
            category_sell.setChecked(true);
        }else if(itemCategory.equals("WTB")){
            category_buy.setChecked(true);
        }else if(itemCategory.equals("WTT")){
            category_trade.setChecked(true);
            tvEditItemPrice.setVisibility(View.GONE);
            etEditItemPrice.setVisibility(View.GONE);
        }

        rgItemCategory.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.category_sell){
                    tvEditItemPrice.setVisibility(View.VISIBLE);
                    etEditItemPrice.setVisibility(View.VISIBLE);
                }else if (checkedId == R.id.category_buy){
                    tvEditItemPrice.setVisibility(View.VISIBLE);
                    etEditItemPrice.setVisibility(View.VISIBLE);
                }else if (checkedId == R.id.category_trade){
                    tvEditItemPrice.setVisibility(View.GONE);
                    etEditItemPrice.setVisibility(View.GONE);
                }
            }
        });

        imgViewEditMarketItem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showFileChooser();
            }
        });
    }

    public void onSaveEditUploadItemClicked(View view){

        //Get Updated Radio Button Value
        rbEditItemCategory = (RadioButton) findViewById(rgItemCategory.getCheckedRadioButtonId());

        String itemName = etEditItemName.getText().toString();
        String itemDesc = etEditItemDesc.getText().toString();
        String itemCategory = rbEditItemCategory.getText().toString();
        String itemPrice;

        newImgURL = getStringImage(bitmap);

        if(itemCategory.equals("Want To Sell"))
            itemCategory = "WTS";
        else if(itemCategory.equals("Want To Buy"))
            itemCategory = "WTB";
        else if (itemCategory.equals("Want To Trade"))
            itemCategory = "WTT";

        if(itemCategory.equals("WTT"))
            itemPrice = "0";
        else
            itemPrice = etEditItemPrice.getText().toString();

        SharedPreferences preferences = getSharedPreferences("tarcommUser", Context.MODE_PRIVATE);

        if(TextUtils.isEmpty(itemName))
            etEditItemName.setError("This field is required.");
        if(TextUtils.isEmpty(itemDesc))
            etEditItemDesc.setError("This field is required.");
        if(TextUtils.isEmpty(itemPrice))
            itemPrice = "0";


        if(!TextUtils.isEmpty(itemName) && !TextUtils.isEmpty(itemDesc)) {
            Item item = new Item();
            item.setItemCategory(itemCategory);
            item.setItemName(itemName);
            item.setItemDescription(itemDesc);
            item.setItemPrice(itemPrice);
            item.setEmail(preferences.getString("email", ""));
            item.setSellerName(preferences.getString("loggedInUser", ""));
            item.setSellerContact(preferences.getString("contactNo", ""));
            item.setImageURL(newImgURL);

            progressDialog = new ProgressDialog(this);
            try {
                makeServiceCall(getApplicationContext(), "https://tarcomm.000webhostapp.com/updateItem.php", item);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext() , "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }
    }

    public void onImgEditProfileClicked(View view){
        showFileChooser();
    }

    public void onBackClicked(View view){
        finish();
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgViewEditMarketItem.setBackgroundColor(Color.WHITE);
                imgViewEditMarketItem.setImageBitmap(bitmap);
                newImgURL = getStringImage(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void makeServiceCall(Context context, String url, final Item item) {
        RequestQueue queue = Volley.newRequestQueue(context);

        try{

            if(!progressDialog.isShowing()){
                progressDialog.setMessage("Saving");
                progressDialog.show();
            }

            StringRequest postRequest = new StringRequest(
                    Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonObject;

                            try{
                                jsonObject = new JSONObject(response);
                                int success = jsonObject.getInt("success");
                                String message = jsonObject.getString("message");

                                if(success == 0){
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();

                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();

                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(EditUploadItemActivity.this,MarketplaceActivity.class));
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Error. " + error.toString(), Toast.LENGTH_LONG).show();
                }
            }){
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    try {
                        Thread.sleep(500);
                        params.put("itemImage", item.getImageURL());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // put the parameters with specific values
                    params.put("itemCategory", item.getItemCategory());
                    params.put("itemName", item.getItemName());
                    params.put("itemDesc", item.getItemDescription());
                    params.put("itemPrice", String.valueOf(item.getItemPrice()));
                    params.put("itemID", String.valueOf(itemID));
                    params.put("email", item.getEmail());

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }
            };

            queue.add(postRequest);

        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    private void convertImage(String imageURL){
        class ConvertImage extends AsyncTask<String, Void, Bitmap> {

            @Override
            protected Bitmap doInBackground(String... strings) {
                String imageURL = strings[0];

                try {
                    bitmap = Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(imageURL)
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



