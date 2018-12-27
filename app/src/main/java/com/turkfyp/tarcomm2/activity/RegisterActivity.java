package com.turkfyp.tarcomm2.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap,bitmap2;
    private Uri filePath;


    EditText editTextEmail, editTextPassword1,editTextContactNo,editTextFullName,editTextCourse, editTextBioData;
    DatePicker dpDOB;
    RadioGroup rgGender;
    RadioButton rbGender;
    ImageView imgViewProfilePic;
    boolean picChosen;
    ProgressDialog progressDialog;
    Spinner faculty_spinner;
    Drawable dw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        imgViewProfilePic = (ImageView) findViewById(R.id.imgViewProfilePic) ;
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword1 =(EditText) findViewById(R.id.editTextPassword1);
        editTextFullName =(EditText) findViewById(R.id.editTextFullName);
        editTextContactNo = (EditText) findViewById(R.id.editTextContactNo);
        rgGender = (RadioGroup) findViewById(R.id.gender);
        dpDOB = (DatePicker) findViewById(R.id.dob);

        faculty_spinner = (Spinner) findViewById(R.id.faculty_spinner);
        editTextCourse = (EditText) findViewById(R.id.editTextCourse);
        editTextBioData = (EditText) findViewById(R.id.editTextBioData);
        dw = getResources().getDrawable(R.drawable.def_male);
        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.gender_male){
                    imgViewProfilePic.setImageDrawable(getResources().getDrawable(R.drawable.user_male));
                    dw = getResources().getDrawable(R.drawable.def_male);
                    bitmap = ((BitmapDrawable)dw).getBitmap();

                }else if (checkedId == R.id.gender_female){
                    imgViewProfilePic.setImageDrawable(getResources().getDrawable(R.drawable.user_female));
                    dw = getResources().getDrawable(R.drawable.def_female);
                    bitmap = ((BitmapDrawable)dw).getBitmap();

                }
            }
        });

        bitmap = ((BitmapDrawable)dw).getBitmap();
    }

    public void imgViewProfilePic_onClicked(View view){

                showFileChooser();

    }
    public void onBackClicked(View view){
        finish();
    }
    public void onRegisterClicked(View view){

        rbGender = (RadioButton) findViewById(rgGender.getCheckedRadioButtonId());
        final String email = editTextEmail.getText().toString();
        final String password = editTextPassword1.getText().toString();
        final String fullName = editTextFullName.getText().toString();
        final String contactNo = editTextContactNo.getText().toString();
        final String gender = rbGender.getText().toString();
        final String faculty = faculty_spinner.getSelectedItem().toString();
        final String course = editTextCourse.getText().toString();
        final String biodata = editTextBioData.getText().toString();

        int dpDay = dpDOB.getDayOfMonth();
        int dpMonth = dpDOB.getMonth();
        int dpYear = dpDOB.getYear();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar d = Calendar.getInstance();
        d.set(dpYear, dpMonth, dpDay);
        String strDate = dateFormatter.format(d.getTime());




        if(TextUtils.isEmpty(email)){
            editTextEmail.setError("This field is required.");
        }
        if(TextUtils.isEmpty(password)){
            editTextPassword1.setError("This field is required.");
        }
        if(TextUtils.isEmpty(fullName)){
            editTextFullName.setError("This field is required.");
        }
        if(TextUtils.isEmpty(contactNo)){
            editTextContactNo.setError("This field is required.");
        }
        if (!isValidEmail(email)) {
            editTextEmail.setError("Invalid Email, please follow follow format (xxxx@gmail.com)");
        }
        if (!isValidContact(contactNo)) {
            editTextContactNo.setError("Invalid number, please enter valid contact number without hyphen(-)");
        }

            if(isValidEmail(email)&&!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(fullName)&&isValidEmail(email)&&isValidContact(contactNo)){
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
                    makeServiceCall(this, "https://tarcomm.000webhostapp.com/insert_user.php", user);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

        }
    }
    public boolean isValidEmail(String string) {
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }
    public boolean isValidContact(String string) {
        String PATTERN;

        //PATTERN = only 1 - 9 and length is 12
        PATTERN = "^[0-9]{10}$";

        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void makeServiceCall(Context context, String url, final User user) {

        RequestQueue queue = Volley.newRequestQueue(context);

        //Send data
        try {

            //setting up progress dialog

            if (!progressDialog.isShowing()) ;
            progressDialog.setMessage("Registering");
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
                                    startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                                }
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

                    try {
                        Thread.sleep(500);
                        params.put("profilepic", user.getProfilepicURL());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    // put the parameters with specific values
                    params.put("email", user.getEmail());
                    params.put("password", user.getPassword());
                    params.put("contactno", user.getContactno());
                    params.put("fullname", user.getFullname());
                    params.put("dateofbirth",user.getDateofbirth());
                    params.put("gender",user.getGender());

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgViewProfilePic.setImageBitmap(bitmap);
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
    private String uploadImage(final User user) {
        class UploadImage extends AsyncTask<Bitmap, Void, String> {
            String image;

            // ADD EVENT LOCATION INFORMATION LATER !!!

            ProgressDialog loading;
            ImgRequestHandler rh = new ImgRequestHandler();


            @Override
            protected String doInBackground(Bitmap... params) {

                bitmap = params[0];
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
