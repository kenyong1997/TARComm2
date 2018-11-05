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
import com.turkfyp.tarcomm2.DatabaseObjects.LostFound;
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

public class EditLostItemActivity extends AppCompatActivity {
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private Uri filePath;

    ProgressDialog progressDialog;


    protected ImageView ivEditLostFoundItem;
    protected int lostID;
    protected EditText etEditLostFoundItemName,etEditLostFoundItemDesc;
    protected RadioGroup rgEditLostCategory;
    protected RadioButton category_lost,category_found, rbEditLostItemCategory;
    protected Button btnCancelEditLostFound, btnEditLostFound;
    protected DatePicker dpEditLostFoundDate;

    protected String ownerContact,itemOwner,lostItemName,lostItemDesc,itemCategory,lostDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_lost_item);

        etEditLostFoundItemName = (EditText)findViewById(R.id.etEditLostFoundItemName);
        etEditLostFoundItemDesc = (EditText)findViewById(R.id.etEditLostFoundItemDesc);
        rgEditLostCategory = (RadioGroup) findViewById(R.id.rgEditLostCategory);
        category_lost = (RadioButton) findViewById(R.id.category_lost);
        category_found = (RadioButton) findViewById(R.id.category_found);
        ivEditLostFoundItem = (ImageView) findViewById(R.id.ivEditLostFoundItem);
        btnCancelEditLostFound = (Button) findViewById(R.id.btnCancelEditLostFound);
        btnEditLostFound = (Button) findViewById(R.id.btnEditLostFound);
        dpEditLostFoundDate = (DatePicker) findViewById(R.id.dpEditLostFoundDate);

        //put on click
        //rbEditItemCategory = (RadioButton) findViewById(rgItemCategory.getCheckedRadioButtonId());

        //get the extras and values
        Bundle extras = getIntent().getExtras();
        ownerContact= extras.getString("lostItemContactNo");
        itemOwner = extras.getString("lostItemContactName");
        lostItemName = extras.getString("lostItemName");
        lostItemDesc = extras.getString("lostItemDesc");
        itemCategory = extras.getString("itemCategory");
        lostDate = extras.getString("lostDate");

        Bitmap image = extras.getParcelable("Image");
        ivEditLostFoundItem.setImageBitmap(image);

        bitmap = image;

        String imageURL = extras.getString("ImageURL");
        lostID = Integer.parseInt(imageURL.split("=")[1]);

        //set text on edittext
        etEditLostFoundItemName.setText(lostItemName);
        etEditLostFoundItemDesc.setText(lostItemDesc);
        if(itemCategory.equals("Lost")){
            category_lost.setChecked(true);
        }else if(itemCategory.equals("Found")){
            category_found.setChecked(true);
        }

        //set date on default
        String stryear=lostDate.substring(0,4);
        String strmonth=lostDate.substring(5,7);
        String strday=lostDate.substring(8,10);

        int year=Integer.parseInt(stryear);
        int month=Integer.parseInt(strmonth);
        int day=Integer.parseInt(strday);
        dpEditLostFoundDate.updateDate(year,month-1,day);



        ivEditLostFoundItem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showFileChooser();
            }
        });
        btnCancelEditLostFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

//        btnEditLostFound.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                //Get Updated Radio Button Value
//                rbEditLostItemCategory = (RadioButton) findViewById(rgEditLostCategory.getCheckedRadioButtonId());
//
//                String lostItemName = etEditLostFoundItemName.getText().toString();
//                String lostItemDesc = etEditLostFoundItemDesc.getText().toString();
//                String lostItemCategory = rbEditLostItemCategory.getText().toString();
//
//
//
//                int dpDay = dpEditLostFoundDate.getDayOfMonth();
//                int dpMonth = dpEditLostFoundDate.getMonth();
//                int dpYear = dpEditLostFoundDate.getYear();
//                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
//                Calendar d = Calendar.getInstance();
//                d.set(dpYear, dpMonth, dpDay);
//                String strDate = dateFormatter.format(d.getTime());
//
//
//
//                SharedPreferences preferences = getSharedPreferences("tarcommUser", Context.MODE_PRIVATE);
//
//                if(TextUtils.isEmpty(lostItemName))
//                    etEditLostFoundItemName.setError("This field is required.");
//                if(TextUtils.isEmpty(lostItemDesc))
//                    etEditLostFoundItemDesc.setError("This field is required.");
//
//
//
//                if(!TextUtils.isEmpty(lostItemName) && !TextUtils.isEmpty(lostItemDesc)) {
//                    LostFound lostFoundItem = new LostFound();
//                    lostFoundItem.setContactName(itemOwner);
//                    lostFoundItem.setCategory(lostItemCategory);
//                    lostFoundItem.setContactNo(ownerContact);
//                    lostFoundItem.setLostDate(strDate);
//                    lostFoundItem.setLostItemDesc(lostItemDesc);
//                    lostFoundItem.setLostItemName(lostItemName);
//                    lostFoundItem.setEmail(preferences.getString("email", ""));
//                    uploadImage(lostFoundItem);
//
//
//
//                    progressDialog = new ProgressDialog(getApplicationContext());
//                    try {
//                        makeServiceCall(getApplicationContext(), "https://tarcomm.000webhostapp.com/createItem.php", lostFoundItem);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Toast.makeText(getApplicationContext() , "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                    }
//
//                }
//            }
//        });
//

    }

    public void onSaveEditLostItemClicked(View view){
        //Get Updated Radio Button Value
        rbEditLostItemCategory = (RadioButton) findViewById(rgEditLostCategory.getCheckedRadioButtonId());

        String lostItemName = etEditLostFoundItemName.getText().toString();
        String lostItemDesc = etEditLostFoundItemDesc.getText().toString();
        String lostItemCategory = rbEditLostItemCategory.getText().toString();



        int dpDay = dpEditLostFoundDate.getDayOfMonth();
        int dpMonth = dpEditLostFoundDate.getMonth();
        int dpYear = dpEditLostFoundDate.getYear();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar d = Calendar.getInstance();
        d.set(dpYear, dpMonth, dpDay);
        String strDate = dateFormatter.format(d.getTime());



        SharedPreferences preferences = getSharedPreferences("tarcommUser", Context.MODE_PRIVATE);

        if(TextUtils.isEmpty(lostItemName))
            etEditLostFoundItemName.setError("This field is required.");
        if(TextUtils.isEmpty(lostItemDesc))
            etEditLostFoundItemDesc.setError("This field is required.");



        if(!TextUtils.isEmpty(lostItemName) && !TextUtils.isEmpty(lostItemDesc)) {
            LostFound lostFoundItem = new LostFound();
            lostFoundItem.setContactName(itemOwner);
            lostFoundItem.setCategory(lostItemCategory);
            lostFoundItem.setContactNo(ownerContact);
            lostFoundItem.setLostDate(strDate);
            lostFoundItem.setLostItemDesc(lostItemDesc);
            lostFoundItem.setLostItemName(lostItemName);
            lostFoundItem.setEmail(preferences.getString("email", ""));
            uploadImage(lostFoundItem);



            progressDialog = new ProgressDialog(this);
            try {
                makeServiceCall(getApplicationContext(), "https://tarcomm.000webhostapp.com/updateLostFoundItem.php", lostFoundItem);
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




    public void makeServiceCall(Context context, String url, final LostFound lostFound) {
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
                                    startActivity(new Intent(EditLostItemActivity.this,LostAndFoundActivity.class));
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
                    params.put("category", lostFound.getCategory());
                    params.put("lostItemName", lostFound.getLostItemName());
                    params.put("lostItemDesc", lostFound.getLostItemDesc());
                    params.put("lostItemImage", lostFound.getLostItemURL());
                    params.put("lostDate", lostFound.getLostDate());
                    params.put("lostID", String.valueOf(lostID));

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
                ivEditLostFoundItem.setBackgroundColor(Color.WHITE);
                ivEditLostFoundItem.setImageBitmap(bitmap);

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

    private LostFound uploadImage(final LostFound lostFound) {
        class UploadImage extends AsyncTask<Bitmap, Void, String> {
            String image;

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];
                String uploadImage = getStringImage(bitmap);

                image = uploadImage;

                lostFound.setLostItemURL(image);
                return uploadImage;
            }
        }
        UploadImage ui = new UploadImage();
        ui.execute(bitmap);

        return lostFound;
    }
}



