package com.turkfyp.tarcomm2.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
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


public class FragmentTradingTab3 extends Fragment {

    public static boolean allowRefresh;

    private static final String TAG = "FragmentTradingTab3";

    private static String GET_URL = "https://taroute.000webhostapp.com/getOthers.php";
    ListView lvMarketplace;
    SwipeRefreshLayout swipeRefreshMarketplace;
    List<Item> itemList;

    RequestQueue queue;

    public FragmentTradingTab3() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_trading_tab3, container, false);

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

        //when a particular item was selected to view more details
        lvMarketplace.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item selectedItem =(Item)parent.getItemAtPosition(position);
                Intent itemDetailIntent = new Intent(getActivity(),MarketplaceDetailActivity.class);
                itemDetailIntent.putExtra("itemName",selectedItem.getItemName());
                itemDetailIntent.putExtra("itemPrice",selectedItem.getItemPrice());
                itemDetailIntent.putExtra("itemDesc",selectedItem.getItemDescription());
                itemDetailIntent.putExtra("itemSeller",selectedItem.getItemSeller());
                itemDetailIntent.putExtra("sellerContact",selectedItem.getSellerContact());
                itemDetailIntent.putExtra("dateAdded",selectedItem.getDateAdded());
                itemDetailIntent.putExtra("desiredLocation",selectedItem.getDesiredLocation());

                ImageView ivImage = (ImageView) view.findViewById(R.id.ivItemImage);
                ivImage.buildDrawingCache();
                Bitmap image = ivImage.getDrawingCache();
                itemDetailIntent.putExtra("Image", image);
                itemDetailIntent.putExtra("ImageURL", selectedItem.getImageURL());

                startActivity(itemDetailIntent);
            }
        });

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
                            Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), "Error: " + volleyError.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });

        // Set the tag on the request.
        jsonObjectRequest.setTag(TAG);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);


    }


    private void loadItem() {
        final ItemAdapter adapter = new ItemAdapter(getActivity(), R.layout.fragment_trading_tab3, itemList);
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
