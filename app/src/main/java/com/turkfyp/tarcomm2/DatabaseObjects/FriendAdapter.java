package com.turkfyp.tarcomm2.DatabaseObjects;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.turkfyp.tarcomm2.R;




public class FriendAdapter extends ArrayAdapter<Event> {

    public FriendAdapter(Activity context, int resource, List<Event> list) {
        super(context, resource, list);
    }


}
