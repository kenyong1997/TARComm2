package com.turkfyp.tarcomm2.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.turkfyp.tarcomm2.DatabaseObjects.Item;
import com.turkfyp.tarcomm2.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class FragmentAddMarketItem extends Fragment {
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private Uri filePath;
    ProgressDialog progressDialog;

    RadioGroup rgItemCategory;
    RadioButton rbItemCategory;
    TextView tvItemPrice;
    EditText etItemPrice,etAddItemName,etAddItemDesc;
    ImageView imgViewMarketItem;

    Button btnCancelAddItem, btnUploadAddItem;
    String currentDate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_add_market_item, container, false);

        rgItemCategory = (RadioGroup)v.findViewById(R.id.rgItemCategory);
        rbItemCategory = (RadioButton)v.findViewById(rgItemCategory.getCheckedRadioButtonId());
        tvItemPrice = (TextView)v.findViewById(R.id.tvItemPrice);
        etItemPrice = (EditText)v.findViewById(R.id.etItemPrice);
        etAddItemName = (EditText)v.findViewById(R.id.etAddItemName);
        etAddItemDesc = (EditText)v.findViewById(R.id.etAddItemDesc);
        imgViewMarketItem =(ImageView)v.findViewById(R.id.imgViewMarketItem);

        btnCancelAddItem =(Button)v.findViewById(R.id.btnCancelAddItem);
        btnUploadAddItem =(Button)v.findViewById(R.id.btnUploadAddItem);

        rgItemCategory.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.category_sell){
                    tvItemPrice.setVisibility(View.VISIBLE);
                    etItemPrice.setVisibility(View.VISIBLE);
                }else if (checkedId == R.id.category_buy){
                    tvItemPrice.setVisibility(View.VISIBLE);
                    etItemPrice.setVisibility(View.VISIBLE);
                }else if (checkedId == R.id.category_trade){
                    tvItemPrice.setVisibility(View.GONE);
                    etItemPrice.setVisibility(View.GONE);
                }
            }
        });

        imgViewMarketItem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showFileChooser();
            }
        });

        btnCancelAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        btnUploadAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Get Updated Radio Button Value
                rbItemCategory = (RadioButton)v.findViewById(rgItemCategory.getCheckedRadioButtonId());

                String itemName = etAddItemName.getText().toString();
                String itemDesc = etAddItemDesc.getText().toString();
                String itemCategory = rbItemCategory.getText().toString();
                String itemPrice;

                //get current Date
                Date cal = Calendar.getInstance().getTime();
                java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                currentDate = df.format(cal);

                if(itemCategory.equals("Want To Sell"))
                    itemCategory = "WTS";
                else if(itemCategory.equals("Want To Buy"))
                    itemCategory = "WTB";
                else if (itemCategory.equals("Want To Trade"))
                    itemCategory = "WTT";

                if(itemCategory.equals("WTT"))
                    itemPrice = "0";
                else
                    itemPrice = etItemPrice.getText().toString();

                SharedPreferences preferences = getActivity().getSharedPreferences("tarcommUser", Context.MODE_PRIVATE);

                if(TextUtils.isEmpty(itemName))
                    etAddItemName.setError("This field is required.");
                if(TextUtils.isEmpty(itemDesc))
                    etAddItemDesc.setError("This field is required.");
                if(TextUtils.isEmpty(itemPrice))
                    itemPrice = "0";

                if(!TextUtils.isEmpty(itemName) && !TextUtils.isEmpty(itemDesc)&& imgViewMarketItem.getDrawable() != null) {
                    Item item = new Item();
                    item.setItemCategory(itemCategory);
                    item.setItemName(itemName);
                    item.setItemDescription(itemDesc);
                    item.setItemPrice(itemPrice);
                    item.setEmail(preferences.getString("email", ""));
                    item.setSellerName(preferences.getString("loggedInUser", ""));
                    item.setSellerContact(preferences.getString("contactNo", ""));
                    item.setItemLastModified(currentDate);
                    uploadImage(item);

                    progressDialog = new ProgressDialog(getActivity());
                    try {
                        makeServiceCall(getActivity().getApplicationContext(), "https://tarcomm.000webhostapp.com/createItem.php", item);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity().getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else
                    Toast.makeText(getActivity().getApplicationContext(), "Please add an image or fill all the mandatory field", Toast.LENGTH_LONG).show();
            }
        });

        return v;
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {

                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);

                imgViewMarketItem.setBackgroundColor(Color.WHITE);
                imgViewMarketItem.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 40, baos);
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

    public void makeServiceCall(Context context, String url, final Item item) {
        RequestQueue queue = Volley.newRequestQueue(context);

        try{

            if(!progressDialog.isShowing()){
                progressDialog.setMessage("Uploading");
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

                                    Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();

                                    Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getActivity(),MarketplaceActivity.class));
                                    getActivity().finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity().getApplicationContext(), "Error. " + error.toString(), Toast.LENGTH_LONG).show();
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
                    params.put("email", item.getEmail());
                    params.put("itemLastModified", item.getItemLastModified());

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
}

