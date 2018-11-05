package com.turkfyp.tarcomm2.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
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

public class EditProfileActivity extends AppCompatActivity {
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private Uri filePath;

    ProgressDialog progressDialog;

    ImageView imgViewEditProfilePic;
    EditText etEditName,etEditContactNo,etEditCourse,etEditBioData;
    Spinner edit_faculty_spinner;
    DatePicker dpEditDOB;
    RadioGroup rgEditGender;
    RadioButton rbEditGender,rb_male,rb_female;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        SharedPreferences preferences = getSharedPreferences("tarcommUser", MODE_PRIVATE);


        //Set Profile Picture on Navigation Bar
        String imageURL = preferences.getString("profilePicURL","");
        convertImage(imageURL);



        //------------------------Activity Codes

        imgViewEditProfilePic = (ImageView)findViewById(R.id.imgViewEditProfilePic);
        etEditName = (EditText)findViewById(R.id.etEditName);
        etEditContactNo=(EditText)findViewById(R.id.etEditContactNo);
        etEditCourse=(EditText)findViewById(R.id.etEditCourse);
        etEditBioData= (EditText)findViewById(R.id.etEditBioData);
        edit_faculty_spinner = (Spinner) findViewById(R.id.edit_faculty_spinner);
        dpEditDOB = (DatePicker)findViewById(R.id.dpEditDOB);
        rgEditGender = (RadioGroup) findViewById(R.id.rgEditGender);
        rb_male = (RadioButton)findViewById(R.id.rb_male);
        rb_female = (RadioButton) findViewById(R.id.rb_female);
        rbEditGender = (RadioButton) findViewById(rgEditGender.getCheckedRadioButtonId());

        //set spinner and date
        String faculty = preferences.getString("faculty","");
        String dob = preferences.getString("dateofbirth","");
        String gender = preferences.getString("gender","");

        String stryear=dob.substring(0,4);
        String strmonth=dob.substring(5,7);
        String strday=dob.substring(8,10);


        int year=Integer.parseInt(stryear);
        int month=Integer.parseInt(strmonth);
        int day=Integer.parseInt(strday);

        if(gender.equals("Male")){
            rb_male.setChecked(true);
        }else if(gender.equals("Female")){
            rb_female.setChecked(true);
        }

        etEditName.setText(preferences.getString("loggedInUser", ""));
        etEditContactNo.setText(preferences.getString("contactNo", ""));
        edit_faculty_spinner.setSelection(getIndex(edit_faculty_spinner,faculty));
        dpEditDOB.updateDate(year,month-1,day);
        etEditCourse.setText(preferences.getString("course",""));
        etEditBioData.setText(preferences.getString("biodata",""));

    }

    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return 0;
    }

    public void onImgEditProfileClicked(View view){
        showFileChooser();
    }

    public  void onCancelEditProfileClicked(View view){
        onBackPressed();
    }

    public  void onSaveEditProfileClicked(View view){

        SharedPreferences preferences = getSharedPreferences("tarcommUser", MODE_PRIVATE);
        rbEditGender = (RadioButton) findViewById(rgEditGender.getCheckedRadioButtonId());
        final String email = preferences.getString("email","");
        final String password = preferences.getString("password","");
        final String fullName = etEditName.getText().toString();
        final String contactNo = etEditContactNo.getText().toString();
        final String gender = rbEditGender.getText().toString();
        final String faculty = edit_faculty_spinner.getSelectedItem().toString();
        final String course = etEditCourse.getText().toString();
        final String biodata = etEditBioData.getText().toString();

        int dpDay = dpEditDOB.getDayOfMonth();
        int dpMonth = dpEditDOB.getMonth();
        int dpYear = dpEditDOB.getYear();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar d = Calendar.getInstance();
        d.set(dpYear, dpMonth, dpDay);
        String strDate = dateFormatter.format(d.getTime());

        if(TextUtils.isEmpty(fullName)){
            etEditName.setError("This field is required.");
        }
        if(TextUtils.isEmpty(contactNo)){
            etEditContactNo.setError("This field is required.");
        }
        if(TextUtils.isEmpty(course)){
            etEditCourse.setError("This field is required.");
        }
        if(TextUtils.isEmpty(biodata)){
            etEditBioData.setError("This field is required.");
        }
        if (!isValidContact(contactNo)) {
            etEditContactNo.setError("Invalid number, please enter valid contact number without hyphen(-)");
        }

        if(isValidContact(contactNo) && !TextUtils.isEmpty(fullName) && !TextUtils.isEmpty(contactNo) && !TextUtils.isEmpty(course) && !TextUtils.isEmpty(biodata)){
            User user = new User();
            user.setEmail(email);
            user.setPassword(password);
            user.setFullname(fullName);
            user.setContactno(contactNo);
            user.setDateofbirth(strDate);
            user.setGender(gender);
            user.setFaculty(faculty);
            user.setCourse(course);
            user.setBiodata(biodata);
            uploadImage(user);

            //create a new userFullName in database
            progressDialog = new ProgressDialog(this);
            try {
                makeServiceCall(this, "https://tarcomm.000webhostapp.com/updateUserProfile.php", user);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

    }


    public void onEditProfileClicked(View view){
        Intent i = new Intent (this,EditProfileActivity.class);
        startActivity(i);
    }
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    public void onBackClicked(View view){
        finish();
    }
    public boolean isValidContact(String string) {
        String PATTERN;

        //PATTERN = only 1 - 9 and length is 12
        PATTERN = "^[0-9]{10}$";

        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }

    public void makeServiceCall(Context context, String url, final User user) {

        RequestQueue queue = Volley.newRequestQueue(context);

        //Send data
        try {

            //setting up progress dialog
            if (!progressDialog.isShowing()) ;
            progressDialog.setMessage("Saving");
            progressDialog.show();

            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(response);
                                int success = jsonObject.getInt("success");
                                String message = jsonObject.getString("message");
                                if (success == 0) {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();

                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();

                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(EditProfileActivity.this,ViewProfileActivity.class));
                                    finish();
                                }

                                SharedPreferences preferences = getSharedPreferences("tarcommUser", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();

                                String imgURL = "https://tarcomm.000webhostapp.com/getUserImage.php?email="+user.getEmail();

                                editor.putString("loggedInUser", user.getFullname());
                                editor.putString("dateofbirth",user.getDateofbirth());
                                editor.putString("gender",user.getGender());
                                editor.putString("password", user.getPassword());
                                editor.putString("email",user.getEmail());
                                editor.putString("profilePicURL", imgURL);
                                editor.putString("contactNo", user.getContactno());
                                editor.putString("faculty",user.getFaculty());
                                editor.putString("course", user.getCourse());
                                editor.putString("biodata", user.getBiodata());

                                editor.apply();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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

                    // put the parameters with specific values
                    params.put("email", user.getEmail());
                    params.put("profilepicURL", user.getProfilepicURL());
                    params.put("fullName", user.getFullname());
                    params.put("gender",user.getGender());
                    params.put("contactNo", user.getContactno());
                    params.put("dob",user.getDateofbirth());
                    params.put("faculty", user.getFaculty());
                    params.put("course", user.getCourse());
                    params.put("biodata", user.getBiodata());


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

    private Session session;
    public void logout_onclick(View view){
        session = new Session(view.getContext());
        session.setLoggedIn(false);
        finish();
        Intent i = new Intent (this,LoginActivity.class);
        startActivity(i);
    }

    //Side Menu Navigation
    public void highlight_event_onclick(View view){
        Intent i = new Intent (this,MainActivity.class);
        startActivity(i);
    }
    public void event_onclick(View view){
        Intent i = new Intent (this,EventActivity.class);
        startActivity(i);
    }
    public void market_onclick(View view){
        Intent i = new Intent (this,MarketplaceActivity.class);
        startActivity(i);
    }
    public void lost_and_found_onclick(View view){
        Intent i = new Intent (this,LostAndFoundActivity.class);
        startActivity(i);
    }
    public void map_onclick(View view){
        Intent i = new Intent (this,MapActivity.class);
        startActivity(i);
    }
    public void view_profile_onclick(View view){
        Intent i = new Intent (this,ViewProfileActivity.class);
        startActivity(i);
    }
    //End Side Menu Navigation

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


                CircleImageView imgViewProfilePic = (CircleImageView) findViewById(R.id.imgViewEditProfilePic);
                imgViewProfilePic.setImageBitmap(bitmap);
            }
        }
        ConvertImage convertImage = new ConvertImage();
        convertImage.execute(imageURL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgViewEditProfilePic.setImageBitmap(bitmap);
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

    private String uploadImage(final User user) {
        class UploadImage extends AsyncTask<Bitmap, Void, String> {
            String image;

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];
                String uploadImage = getStringImage(bitmap);

                image = uploadImage;

                user.setProfilepicURL(image);
                return uploadImage;
            }
        }
        UploadImage ui = new UploadImage();
        ui.execute(bitmap);

        return ui.image;
    }
}


