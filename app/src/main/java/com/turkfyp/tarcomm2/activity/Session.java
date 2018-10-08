package com.turkfyp.tarcomm2.activity;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by User-PC on 13/10/2017.
 */

public class Session {


    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context context;


    public Session(Context context)
    {
        this.context = context;
        preferences = context.getSharedPreferences("taroute",Context.MODE_PRIVATE);
        editor= preferences.edit();
    }


    public void setLoggedIn(boolean loggedIn)
    {
        editor.putBoolean("loggedInMode",loggedIn);
        editor.commit();
    }

    public boolean loggedIn()
    {
        return preferences.getBoolean("loggedInMode",false);
    }


    public String getLoggedInUserName()
    {
        return preferences.getString("loggedInUser","");
    }

}

