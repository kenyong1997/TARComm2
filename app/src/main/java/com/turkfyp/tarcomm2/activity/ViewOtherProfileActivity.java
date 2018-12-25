package com.turkfyp.tarcomm2.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.turkfyp.tarcomm2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewOtherProfileActivity extends AppCompatActivity {


    String email,gender,profilePicURL,contactNo,dateofbirth,faculty,biodata,course,name;
    TextView tvProfileName, tvProfileFaculty, tvProfileCourse, tvProfileEmail, tvProfilePhone,tvProfileBioData,tvProfileFriendCount,tvProfileMarketPost, tvProfileLostFoundPost;
    Button btnEditProfile;
    RequestQueue queue;
    private static final String TAG = "ViewOtherProfileActivity";

    private static String GET_URL = "https://tarcomm.000webhostapp.com/getTotalPost.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_other_profile);


        //------------------------Activity Codes
        tvProfileName = (TextView) findViewById(R.id.tvProfileName);
        tvProfileFaculty = (TextView) findViewById(R.id.tvProfileFaculty);
        tvProfileCourse = (TextView) findViewById(R.id.tvProfileCourse);
        tvProfileEmail = (TextView) findViewById(R.id.tvProfileEmail);
        tvProfilePhone = (TextView) findViewById(R.id.tvProfilePhone);
        tvProfileBioData = (TextView) findViewById(R.id.tvProfileBioData);
        btnEditProfile = (Button) findViewById(R.id.btnEditProfile);

        tvProfileFriendCount = (TextView) findViewById(R.id.tvProfileFriendCount);
        tvProfileMarketPost = (TextView) findViewById(R.id.tvProfileMarketPost);
        tvProfileLostFoundPost = (TextView) findViewById(R.id.tvProfileLostFoundPost);

        Bundle extras = getIntent().getExtras();
        email = extras.getString("email");


        findUser(this,"https://tarcomm.000webhostapp.com/select_user.php",email);
        countPost(this,"https://tarcomm.000webhostapp.com/getTotalPost.php");


    }


    public void onBackClicked(View view){
        finish();
    }

    public void onViewOtherMarketPostClicked(View view){
        Intent i = new Intent (this,ViewOtherMarketPostActivity.class);
        i.putExtra("email",email);
        startActivity(i);
    }
    public void onViewOtherLostFoundPostClicked(View view){
        Intent i = new Intent (this,ViewOtherLostFoundPostActivity.class);
        i.putExtra("email",email);
        startActivity(i);
    }


    public void countPost(Context context, String url) {
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);

        try{
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray j = new JSONArray(response);
                                try {

                                    JSONObject postCountResponse = (JSONObject) j.get(0);
                                    String lostFoundPostCount = postCountResponse.getString("totalPost");
                                    tvProfileLostFoundPost.setText(lostFoundPostCount);

                                    postCountResponse = (JSONObject) j.get(1);
                                    String marketPostCount = postCountResponse.getString("totalPost");
                                    tvProfileMarketPost.setText(marketPostCount);

                                    postCountResponse = (JSONObject) j.get(2);
                                    String friendCount = postCountResponse.getString("totalPost");
                                    tvProfileFriendCount.setText(friendCount);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(getApplicationContext(), "Error: " + volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }){
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<>();
                    params.put("email", email);
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void findUser(Context context, String url, final String userEmail) {
        //mPostCommentResponse.requestStarted();
        RequestQueue queue = Volley.newRequestQueue(context);

        //Send data
        try {
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonObject;
                            try {

                                jsonObject = new JSONObject(response);
                                        email = userEmail;
                                        name = jsonObject.getString("fullname");
                                        gender=jsonObject.getString("gender");
                                        profilePicURL = jsonObject.getString("profilepicURL");
                                        contactNo = jsonObject.getString("contactno");
                                        dateofbirth = jsonObject.getString("dateofbirth");
                                        faculty = jsonObject.getString("faculty");
                                        course = jsonObject.getString("course");
                                        biodata = jsonObject.getString("biodata");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //underline contact
                            SpannableString content = new SpannableString(contactNo);
                            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                            tvProfilePhone.setText(content);
                            tvProfileName.setText(name);
                            tvProfileEmail.setText(email);
                            tvProfileFaculty.setText(faculty);
                            tvProfileCourse.setText(course);
                            tvProfileBioData.setText(biodata);
                            convertImage(profilePicURL);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Error. " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", userEmail);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onClickContact(View view) {

        PackageManager packageManager = getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);

        try {
            //If want include message to whatsapp
            //String url = "https://api.whatsapp.com/send?phone=+6"+ contactNo +"&text=" + URLEncoder.encode(message, "UTF-8");

            String url = "https://api.whatsapp.com/send?phone=+6"+ contactNo;
            sendIntent.setPackage("com.whatsapp");
            sendIntent.setData(Uri.parse(url));
            if (sendIntent.resolveActivity(packageManager) != null) {
                startActivity(sendIntent);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //Get Profile Image for Navigation Menu
    private void convertImage(String imageURL){
        class ConvertImage extends AsyncTask<String, Void, Bitmap> {

            @Override
            protected Bitmap doInBackground(String... strings) {
                String imageURL = strings[0];

                try {
                    URL url = new URL(imageURL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    return myBitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);

                CircleImageView imgViewProfilePic = (CircleImageView) findViewById(R.id.imgViewProfilePic);
                imgViewProfilePic.setImageBitmap(bitmap);
            }
        }
        ConvertImage convertImage = new ConvertImage();
        convertImage.execute(imageURL);
    }
}
