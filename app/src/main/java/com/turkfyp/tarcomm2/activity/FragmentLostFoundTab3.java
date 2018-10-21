package com.turkfyp.tarcomm2.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.turkfyp.tarcomm2.DatabaseObjects.ItemAdapter;
import com.turkfyp.tarcomm2.DatabaseObjects.LostFound;
import com.turkfyp.tarcomm2.DatabaseObjects.LostFoundAdapter;
import com.turkfyp.tarcomm2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentLostFoundTab3 extends Fragment {

    public static boolean allowRefresh;

    private static final String TAG = "FragmentLostFoundTab3";

    private static String GET_URL = "https://tarcomm.000webhostapp.com/getLostFoundUserPost.php";
    ListView lvLostFound;
    SwipeRefreshLayout swipeRefreshLostFound;
    List<LostFound> lostFoundList;

    RequestQueue queue;

    public FragmentLostFoundTab3() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_lost_found_tab3, container, false);

        lvLostFound = (ListView) v.findViewById(R.id.lvLostFound);
        swipeRefreshLostFound = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLostFound);
        FloatingActionButton fabAddItem = (FloatingActionButton)v.findViewById(R.id.addItemFAB);
        try {
            //initialize textBookList
            lostFoundList = new ArrayList<>();

            downloadLostFoundRecords(getActivity().getApplicationContext(), GET_URL);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        //when a particular item was selected to view more details
        lvLostFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                LostFound selectedItem =(LostFound) parent.getItemAtPosition(position);
                //TODO: Change this to Lost Found Detail in future
                Intent itemDetailIntent = new Intent(getActivity(),MarketplaceDetailActivity.class);
                itemDetailIntent.putExtra("lostItemName",selectedItem.getLostItemName());
                itemDetailIntent.putExtra("lostItemDesc",selectedItem.getLostItemDesc());
                itemDetailIntent.putExtra("lostDate", selectedItem.getLostDate());
                itemDetailIntent.putExtra("lostItemContactName",selectedItem.getContactName());
                itemDetailIntent.putExtra("lostItemContactNo",selectedItem.getContactNo());

                ImageView ivImage = (ImageView) view.findViewById(R.id.ivItemImage);
                ivImage.buildDrawingCache();
                Bitmap image = ivImage.getDrawingCache();
                itemDetailIntent.putExtra("Image", image);
                itemDetailIntent.putExtra("ImageURL", selectedItem.getLostItemURL());

                startActivity(itemDetailIntent);
            }
        });

        fabAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addItemIntent = new Intent(getActivity().getApplicationContext(), AddLostFoundItemActivity.class);
                startActivity(addItemIntent);
            }
        });
        return v;

    }

    //retrieve the records from database
    public void downloadLostFoundRecords(Context context, String url) {
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
                                    lostFoundList.clear();
                                    for (int i = 0; i < j.length(); i++) {
                                        JSONObject textbookResponse = (JSONObject) j.get(i);
                                        String category = textbookResponse.getString("category");
                                        String lostItemName = textbookResponse.getString("lostItemName");
                                        String lostItemDesc = textbookResponse.getString("lostItemDesc");
                                        String lostItemURL = textbookResponse.getString("url");
                                        String email = textbookResponse.getString("email");
                                        String contactName = textbookResponse.getString("fullname");
                                        String contactNo = textbookResponse.getString("contactno");
                                        String lostDate = textbookResponse.getString("lostDate");

                                        LostFound lostFound = new LostFound(category, lostItemName, lostItemDesc, lostItemURL, lostDate, email, contactName, contactNo);
                                        lostFoundList.add(lostFound);
                                    }

                                    //load the item into adapter
                                    loadItem();

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
                            Toast.makeText(getActivity(), "Error: " + volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }){
                @Override
                protected Map<String, String> getParams() {
                    SharedPreferences preferences = getActivity().getSharedPreferences("tarcommUser", Context.MODE_PRIVATE);

                    Map<String, String> params = new HashMap<>();
                    params.put("email", preferences.getString("email",""));
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


    private void loadItem() {
        final LostFoundAdapter adapter = new LostFoundAdapter(getActivity(), R.layout.fragment_lost_found_tab2, lostFoundList);
        lvLostFound.setAdapter(adapter);

    }



    @Override
    public void onResume() {
        super.onResume();
        if (allowRefresh) {
            allowRefresh = false;
            if (lostFoundList == null) {
                lostFoundList = new ArrayList<>();
                downloadLostFoundRecords(getActivity().getApplicationContext(), GET_URL);
            } else {
                loadItem();
            }
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }


}
