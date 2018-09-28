package com.turkfyp.tarcomm2.activity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.turkfyp.tarcomm2.DatabaseObjects.Item;
import com.turkfyp.tarcomm2.DatabaseObjects.ItemAdapter;
import com.turkfyp.tarcomm2.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class FragmentTradingTab2 extends Fragment {


    public static boolean allowRefresh;

    private static final String TAG = "FragmentTradingTab2";

    private static String GET_URL = "https://taroute.000webhostapp.com/getHostels.php";
    ListView lvMarketplace;
    SwipeRefreshLayout swipeRefreshMarketplace;
    List<Item> itemList;

    RequestQueue queue;

    public FragmentTradingTab2(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_trading_tab2, container, false);

        lvMarketplace = (ListView) v.findViewById(R.id.lvMarketplace);
        swipeRefreshMarketplace = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshMarketplace);

        try {
            //initialize textBookList
            itemList = new ArrayList<>();

            downloadTradingRecords(getActivity().getApplicationContext(), GET_URL);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return v;
    }


    //retrieve the records from database
    public void downloadTradingRecords(Context context, String url) {
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            itemList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject textbookResponse = (JSONObject) response.get(i);
                                String itemSeller = textbookResponse.getString("itemSeller");
                                String itemName = textbookResponse.getString("itemName");
                                double itemPrice = Double.parseDouble(textbookResponse.getString("itemPrice"));
                                String itemDescription = textbookResponse.getString("itemDescription");
                                String itemCategory = textbookResponse.getString("itemCategory");
                                String dateAdded = textbookResponse.getString("dateAdded");
                                String imageURL = textbookResponse.getString("url");
                                String sellerContact = textbookResponse.getString("contactNumber");
                                String desiredLocation = textbookResponse.getString("desiredLocation");

                                Item item = new Item(itemSeller, itemName, itemPrice, itemDescription, itemCategory, dateAdded, imageURL, sellerContact,desiredLocation);
                                itemList.add(item);
                            }

                            //load the item into adapter
                            loadItem();

                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), "Error" + volleyError.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });

        // Set the tag on the request.
        jsonObjectRequest.setTag(TAG);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);


    }


    private void loadItem() {
        final ItemAdapter adapter = new ItemAdapter(getActivity(), R.layout.fragment_trading_tab2, itemList);
        lvMarketplace.setAdapter(adapter);

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
