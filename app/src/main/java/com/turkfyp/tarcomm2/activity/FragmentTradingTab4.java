package com.turkfyp.tarcomm2.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.turkfyp.tarcomm2.DatabaseObjects.Item;
import com.turkfyp.tarcomm2.DatabaseObjects.ItemAdapter;
import com.turkfyp.tarcomm2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FragmentTradingTab4 extends Fragment {

    public static boolean allowRefresh;

    private static final String TAG = "FragmentTradingTab4";

    private static String GET_URL = "https://tarcomm.000webhostapp.com/getItemUserPost.php";
    ListView lvMarketplace;
    SwipeRefreshLayout swipeRefreshMarketplace;
    List<Item> itemList;

    RequestQueue queue;

    public FragmentTradingTab4() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_trading_tab4, container, false);

        lvMarketplace = (ListView) v.findViewById(R.id.lvMarketplace);
        swipeRefreshMarketplace = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshMarketplace);
        FloatingActionButton fabAddMarketItem = (FloatingActionButton)v.findViewById(R.id.addMarketItemFAB);


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
                Intent itemDetailIntent = new Intent(getActivity(),YourMarketplaceDetailActivity.class);
                itemDetailIntent.putExtra("itemName",selectedItem.getItemName());
                itemDetailIntent.putExtra("itemPrice",selectedItem.getItemPrice());
                itemDetailIntent.putExtra("itemDesc",selectedItem.getItemDescription());
                itemDetailIntent.putExtra("itemSeller",selectedItem.getSellerName());
                itemDetailIntent.putExtra("sellerContact",selectedItem.getSellerContact());

                ImageView ivImage = (ImageView) view.findViewById(R.id.ivItemImage);
                ivImage.buildDrawingCache();
                Bitmap image = ivImage.getDrawingCache();
                itemDetailIntent.putExtra("Image", image);
                itemDetailIntent.putExtra("ImageURL", selectedItem.getImageURL());

                startActivity(itemDetailIntent);
            }
        });

        fabAddMarketItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addItemIntent = new Intent(getActivity().getApplicationContext(), AddMarketItemActivity.class);
                startActivity(addItemIntent);
            }
        });
        return v;
    }

    //retrieve the records from database
    public void downloadTradingRecords(Context context, String url) {
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
                                    itemList.clear();
                                    for (int i = 0; i < j.length(); i++) {
                                        JSONObject textbookResponse = (JSONObject) j.get(i);

                                        String itemCategory = textbookResponse.getString("itemCategory");
                                        String itemName = textbookResponse.getString("itemName");
                                        String itemDescription = textbookResponse.getString("itemDesc");
                                        String imageURL = textbookResponse.getString("url");
                                        String itemPrice = textbookResponse.getString("itemPrice");
                                        String email = textbookResponse.getString("email");
                                        String sellerName = textbookResponse.getString("fullname");
                                        String sellerContact = textbookResponse.getString("contactno");

                                        Item item = new Item(itemCategory, itemName, itemDescription, imageURL, itemPrice, email, sellerName, sellerContact);
                                        itemList.add(item);
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
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(), "Error. " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
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
        final ItemAdapter adapter = new ItemAdapter(getActivity(), R.layout.fragment_trading_tab4, itemList);
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
