package com.turkfyp.tarcomm2.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import com.turkfyp.tarcomm2.R;

public class LoginActivity extends AppCompatActivity {

    EditText editTextUserName, editTextPassword;
    private ProgressDialog pDialog;
    String userFullName;
    String email;
    String checkPassword;



    public Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        editTextUserName = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        pDialog = new ProgressDialog(this);

        session = new Session(this);

        if (session.loggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }


    public void onLoginClicked(View view) {

        String password = editTextPassword.getText().toString();
        String username = editTextUserName.getText().toString();

        if (username.matches("")) {
            Toast.makeText(this, "Please fill in username.", Toast.LENGTH_SHORT).show();
        } else if (password.matches("")) {
            Toast.makeText(this, "Please fill in password.", Toast.LENGTH_SHORT).show();
        }
        validateUser(this, "https://tarcomm.000webhostapp.com/select_user.php", username, password);


    }

    public void validateUser(Context context, String url, final String userEmail, final String password) {
        //mPostCommentResponse.requestStarted();
        RequestQueue queue = Volley.newRequestQueue(context);


        if (!pDialog.isShowing()) {
            pDialog.setMessage("Logging in...");
            pDialog.show();
        }


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
                                String err = "";
                                jsonObject = new JSONObject(response);
                                int success = jsonObject.getInt("success");
                                String message = jsonObject.getString("message");
                                if (success == 0) {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    if (pDialog.isShowing())
                                        pDialog.dismiss();
                                } else if (success == 1) {
                                    checkPassword = jsonObject.getString("password");
                                    if (pDialog.isShowing())
                                        pDialog.dismiss();
                                    if (checkPassword.equals(password)) {
                                        userFullName = jsonObject.getString("fullname");
                                        email = userEmail;
                                        //contactNumber = jsonObject.getString("contactNumber");
                                        //userEmail = jsonObject.getString("userEmail");
                                        Toast.makeText(getApplicationContext(), "Welcome, " + userFullName + ".", Toast.LENGTH_LONG).show();


                                        //Logged in successfully

                                        //save sharedPreferences
                                        session.setLoggedIn(true);


                                        //save the userFullName details to sharedPreference
                                        SharedPreferences pref = getSharedPreferences("tarcommUser", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = pref.edit();
                                        editor.putString("loggedInUser", userFullName);
                                        editor.putString("password", password);
                                        editor.putString("email",userEmail);


                                        editor.commit();

                                        //go to main activity
                                        goToMain();
                                    } else {
                                        err += "Password is incorrect.";
                                    }
                                } else if (success == 2) {
                                    //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    err += "User not found.";
                                    if (pDialog.isShowing())
                                        pDialog.dismiss();
                                } else {
                                    Toast.makeText(getApplicationContext(), "err", Toast.LENGTH_SHORT).show();
                                    if (pDialog.isShowing())
                                        pDialog.dismiss();
                                }
                                //show error
                                if (err.length() > 0) {
                                    Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show();
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

    public void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);

        intent.putExtra("userFullName", userFullName);
        intent.putExtra("userEmail", email);
        startActivity(intent);
        finish();
    }


    public void onSignUpClicked(View view) {

        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

}



