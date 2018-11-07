package com.turkfyp.tarcomm2.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.turkfyp.tarcomm2.DatabaseObjects.Item;
import com.turkfyp.tarcomm2.DatabaseObjects.User;
import com.turkfyp.tarcomm2.R;
import com.turkfyp.tarcomm2.guillotine.animation.GuillotineAnimation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditUploadItemActivity extends AppCompatActivity {
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private Uri filePath;

    ProgressDialog progressDialog;

    protected String sellerContact, itemSeller, itemName, itemDesc, itemPrice, itemCategory;
    protected ImageView imgViewEditMarketItem;
    protected int itemID;
    protected EditText etEditItemName,etEditItemDesc,etEditItemPrice;
    protected TextView tvEditItemPrice;
    protected RadioGroup rgItemCategory;
    protected RadioButton category_sell,category_buy,category_trade, rbEditItemCategory;
    protected Button btnCancelEditItem,btnUploadEditItem;
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


        //put on click
        //rbEditItemCategory = (RadioButton) findViewById(rgItemCategory.getCheckedRadioButtonId());

        //get the extras and values
        Bundle extras = getIntent().getExtras();
        itemSeller = extras.getString("itemSeller");
        sellerContact = extras.getString("sellerContact");
        itemName = extras.getString("itemName");
        itemDesc = extras.getString("itemDesc");
        itemPrice = extras.getString("itemPrice");
        itemCategory = extras.getString("itemCategory");


        Bitmap image = extras.getParcelable("Image");
        imgViewEditMarketItem.setImageBitmap(image);

        bitmap = image;

        String imageURL = extras.getString("ImageURL");
        itemID = Integer.parseInt(imageURL.split("=")[1]);

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
            uploadImage(item);


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

    public  void onCancelEditProfileClicked(View view){
        onBackPressed();
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

                    // put the parameters with specific values
                    params.put("itemCategory", item.getItemCategory());
                    params.put("itemName", item.getItemName());
                    params.put("itemDesc", item.getItemDescription());
                    params.put("itemImage", item.getImageURL());
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






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgViewEditMarketItem.setBackgroundColor(Color.WHITE);
                imgViewEditMarketItem.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 10, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private Item uploadImage(final Item item) {
        class UploadImage extends AsyncTask<Bitmap, Void, String> {
            String image;

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];
                String uploadImage = getStringImage(bitmap);

                image = uploadImage;

                item.setImageURL(image);
                return uploadImage;
            }
        }
        UploadImage ui = new UploadImage();
        ui.execute(bitmap);

        return item;
    }
}



