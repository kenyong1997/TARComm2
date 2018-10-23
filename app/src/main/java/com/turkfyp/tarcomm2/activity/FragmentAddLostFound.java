package com.turkfyp.tarcomm2.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
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
import android.widget.DatePicker;
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
import com.turkfyp.tarcomm2.DatabaseObjects.LostFound;
import com.turkfyp.tarcomm2.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class FragmentAddLostFound extends Fragment {

    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private Uri filePath;
    ProgressDialog progressDialog;

    RadioGroup rgLostCategory;
    RadioButton rbLostCategory;
    TextView tvLostFoundDate;
    ImageView imgViewAddLostFoundItem;
    EditText etAddLostFoundItemName, etAddLostFoundItemDesc;
    Button btnAddLostFound, btnCancelAddLostFound;
    DatePicker dpLostFoundDate;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_add_lost_found_item, container, false);

        rgLostCategory = (RadioGroup)v.findViewById(R.id.rgLostCategory);
        rbLostCategory = (RadioButton)v.findViewById(rgLostCategory.getCheckedRadioButtonId());
        tvLostFoundDate = (TextView)v.findViewById(R.id.tvLostFoundDate);
        imgViewAddLostFoundItem = (ImageView)v.findViewById(R.id.imgViewAddLostFoundItem);

        etAddLostFoundItemDesc = (EditText)v.findViewById(R.id.etAddLostFoundItemDesc);
        etAddLostFoundItemName = (EditText)v.findViewById(R.id.etAddLostFoundItemName);
        btnAddLostFound = (Button) v.findViewById(R.id.btnAddLostFound);
        btnCancelAddLostFound = (Button) v.findViewById(R.id.btnCancelAddLostFound);
        dpLostFoundDate = (DatePicker) v.findViewById(R.id.dpLostFoundDate);


        rgLostCategory.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.category_lost){
                    tvLostFoundDate.setText("Lost Date");
                }else if (checkedId == R.id.category_found){
                    tvLostFoundDate.setText("Found Date");
                }
            }
        });

        imgViewAddLostFoundItem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showFileChooser();
            }
        });

        btnCancelAddLostFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        btnAddLostFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get updated radio button value
                rbLostCategory = (RadioButton)v.findViewById(rgLostCategory.getCheckedRadioButtonId());

                SharedPreferences preferences = getActivity().getSharedPreferences("tarcommUser", Context.MODE_PRIVATE);

                String category = rbLostCategory.getText().toString();
                String lostItemName = etAddLostFoundItemName.getText().toString();
                String lostItemDesc = etAddLostFoundItemDesc.getText().toString();


                int dpDay = dpLostFoundDate.getDayOfMonth();
                int dpMonth = dpLostFoundDate.getMonth();
                int dpYear = dpLostFoundDate.getYear();
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                Calendar d = Calendar.getInstance();
                d.set(dpYear, dpMonth, dpDay);
                String lostDate = dateFormatter.format(d.getTime());

                if(TextUtils.isEmpty(lostItemName))
                    etAddLostFoundItemName.setError("This field is required.");
                if(TextUtils.isEmpty(lostItemDesc))
                    etAddLostFoundItemDesc.setError("This field is required.");

                if(!TextUtils.isEmpty(lostItemName) && !TextUtils.isEmpty(lostItemDesc)) {
                    LostFound lostFound = new LostFound();
                    lostFound.setCategory(category);
                    lostFound.setLostItemName(lostItemName);
                    lostFound.setLostItemDesc(lostItemDesc);
                    lostFound.setLostDate(lostDate);
                    lostFound.setEmail(preferences.getString("email", ""));
                    lostFound.setContactName(preferences.getString("loggedInUser", ""));
                    lostFound.setContactNo(preferences.getString("contactNo", ""));
                    uploadImage(lostFound);

                    progressDialog = new ProgressDialog(getActivity());
                    try {
                        makeServiceCall(getActivity().getApplicationContext(), "https://tarcomm.000webhostapp.com/createLostFoundItem.php", lostFound);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity().getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
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

                imgViewAddLostFoundItem.setBackgroundColor(Color.WHITE);
                imgViewAddLostFoundItem.setImageBitmap(bitmap);

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

    private String uploadImage(final LostFound lostFound) {
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

        return ui.image;
    }

    public void makeServiceCall(Context context, String url, final LostFound lostFound) {
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
                                    getActivity().onBackPressed();
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

                    // put the parameters with specific values
                    params.put("category", lostFound.getCategory());
                    params.put("lostItemName", lostFound.getLostItemName());
                    params.put("lostItemDesc", lostFound.getLostItemDesc());
                    params.put("lostItemImage", lostFound.getLostItemURL());
                    params.put("email", lostFound.getEmail());
                    params.put("lostDate", lostFound.getLostDate());

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
