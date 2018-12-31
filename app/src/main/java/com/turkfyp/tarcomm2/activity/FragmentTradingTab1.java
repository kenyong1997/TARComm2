package com.turkfyp.tarcomm2.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.turkfyp.tarcomm2.DatabaseObjects.Item;
import com.turkfyp.tarcomm2.DatabaseObjects.ItemRVAdapter;
import com.turkfyp.tarcomm2.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentTradingTab1 extends Fragment {

    public static boolean allowRefresh;
    private static final String TAG = "FragmentTradingTab1";

    private static String GET_URL = "https://tarcomm.000webhostapp.com/getItemWTS.php";
    SwipeRefreshLayout swipeRefreshMarketplace;
    List<Item> itemList;

    RequestQueue queue;
    RecyclerView rvMarketplace;

    public FragmentTradingTab1() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_trading_tab1, container, false);

        swipeRefreshMarketplace = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshMarketplace);
        rvMarketplace = (RecyclerView) v.findViewById(R.id.rvMarketplace);

        try {
            //initialize itemList
            itemList = new ArrayList<>();

            downloadTradingRecords(getActivity().getApplicationContext(), GET_URL);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        swipeRefreshMarketplace.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeRefreshMarketplace.setRefreshing(true);
                try {
                    downloadTradingRecords(getActivity().getApplicationContext(), GET_URL);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                swipeRefreshMarketplace.setRefreshing(false);
            }
        });

        return v;
    }

    //retrieve the records from database
    public void downloadTradingRecords(Context context, String url) {
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            itemList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject itemResponse = (JSONObject) response.get(i);
                                String itemCategory = itemResponse.getString("itemCategory");
                                String itemName = itemResponse.getString("itemName");
                                String itemDescription = itemResponse.getString("itemDesc");
                                String imageURL = itemResponse.getString("url");
                                String itemPrice = itemResponse.getString("itemPrice");
                                String email = itemResponse.getString("email");
                                String sellerName = itemResponse.getString("fullname");
                                String sellerContact = itemResponse.getString("contactno");
                                String itemLastModified = itemResponse.getString("itemLastModified");

                                Item item = new Item(itemCategory, itemName, itemDescription, imageURL, itemPrice, email, sellerName, sellerContact, itemLastModified);
                                itemList.add(item);
                            }
                            //Load item into RecyclerView Adapter
                            setRVAdapter(itemList);

                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Error1: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), "Error2: " + volleyError.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });

        // Set the tag on the request.
        jsonObjectRequest.setTag(TAG);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private void setRVAdapter(List<Item> itemList){
        ItemRVAdapter myAdapter = new ItemRVAdapter(getActivity(),itemList) ;
        rvMarketplace.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvMarketplace.setAdapter(myAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (allowRefresh) {
            allowRefresh = false;
            if (itemList == null) {
                itemList = new ArrayList<>();
                downloadTradingRecords(getActivity().getApplicationContext(), GET_URL);
            } else {
                setRVAdapter(itemList);
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
