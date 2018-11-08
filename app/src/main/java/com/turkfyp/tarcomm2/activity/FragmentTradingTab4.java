package com.turkfyp.tarcomm2.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.turkfyp.tarcomm2.DatabaseObjects.Item;
import com.turkfyp.tarcomm2.DatabaseObjects.ItemUploadAdapter;
import com.turkfyp.tarcomm2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class FragmentTradingTab4 extends Fragment {

    public static boolean allowRefresh;

    private static final String TAG = "FragmentTradingTab4";

    private static String GET_URL = "https://tarcomm.000webhostapp.com/getItemUserPost.php";
    SwipeRefreshLayout swipeRefreshMarketplace;
    List<Item> itemList;


    ExpandableListView elvItemUpload;
    ItemUploadAdapter itemUploadAdapter;
    List<String> listDataHeader;
    HashMap<String, List<Item>> listDataChild;

    RequestQueue queue;
    Bitmap bitmap;

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


        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;



        //lvMarketplace = (ListView) v.findViewById(R.id.lvMarketplace);
        elvItemUpload = (ExpandableListView) v.findViewById(R.id.elvItemUpload);

        //collapse group on default not working due to null pointer exception
        //elvItemUpload.collapseGroup(0);

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

        elvItemUpload.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                Item selectedItem;
                selectedItem = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);

                Intent itemDetailIntent = new Intent(getActivity(),MarketplaceDetailActivity.class);
                itemDetailIntent.putExtra("itemCategory", selectedItem.getItemCategory());
                itemDetailIntent.putExtra("itemName",selectedItem.getItemName());
                itemDetailIntent.putExtra("itemPrice",selectedItem.getItemPrice());
                itemDetailIntent.putExtra("itemDesc",selectedItem.getItemDescription());
                itemDetailIntent.putExtra("itemSeller",selectedItem.getSellerName());
                itemDetailIntent.putExtra("sellerContact",selectedItem.getSellerContact());
                itemDetailIntent.putExtra("email",selectedItem.getEmail());
                itemDetailIntent.putExtra("checkYourUpload",true);

                convertImage(selectedItem.getImageURL());
                itemDetailIntent.putExtra("Image", bitmap);
                itemDetailIntent.putExtra("ImageURL", selectedItem.getImageURL());

                startActivity(itemDetailIntent);
                return false;
            }
        });

        fabAddMarketItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addItemIntent = new Intent(getActivity().getApplicationContext(), AddMarketItemActivity.class);
                startActivity(addItemIntent);
            }
        });

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

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<Item>>();

        listDataHeader.add("Want To Sell Item");
        listDataHeader.add("Want To Buy Item");
        listDataHeader.add("Want To Trade Item");

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

                                    List<Item> sellItemList = new ArrayList<>();
                                    List<Item> buyItemList = new ArrayList<>();
                                    List<Item> tradeItemList = new ArrayList<>();

                                    for (int i = 0; i < j.length(); i++) {
                                        JSONObject lostFoundResponse = (JSONObject) j.get(i);

                                        String itemCategory = lostFoundResponse.getString("itemCategory");
                                        String itemName = lostFoundResponse.getString("itemName");
                                        String itemDescription = lostFoundResponse.getString("itemDesc");
                                        String imageURL = lostFoundResponse.getString("url");
                                        String itemPrice = lostFoundResponse.getString("itemPrice");
                                        String email = lostFoundResponse.getString("email");
                                        String sellerName = lostFoundResponse.getString("fullname");
                                        String sellerContact = lostFoundResponse.getString("contactno");

                                        Item item = new Item(itemCategory, itemName, itemDescription, imageURL, itemPrice, email, sellerName, sellerContact);

                                        if(itemCategory.equals("WTS"))
                                            sellItemList.add(item);
                                        else if(itemCategory.equals("WTB"))
                                            buyItemList.add(item);
                                        else
                                            tradeItemList.add(item);
                                    }
                                    listDataChild.put(listDataHeader.get(0), sellItemList);
                                    listDataChild.put(listDataHeader.get(1), buyItemList);
                                    listDataChild.put(listDataHeader.get(2), tradeItemList);

                                    itemUploadAdapter = new ItemUploadAdapter(getActivity(),listDataHeader, listDataChild);
                                    elvItemUpload.setAdapter(itemUploadAdapter);
                                    elvItemUpload.expandGroup(0);

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
    
    @Override
    public void onResume() {
        super.onResume();
        if (allowRefresh) {
            allowRefresh = false;
            if (itemList == null) {
                itemList = new ArrayList<>();
                downloadTradingRecords(getActivity().getApplicationContext(), GET_URL);
            } else {
                itemUploadAdapter = new ItemUploadAdapter(getActivity(),listDataHeader, listDataChild);
                elvItemUpload.setAdapter(itemUploadAdapter);
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

    //Get Profile Image for Navigation Menu
    private void convertImage(String imageURL){
        class ConvertImage extends AsyncTask<String, Void, Bitmap> {

            @Override
            protected Bitmap doInBackground(String... strings) {
                String imageURL = strings[0];

                try {
                    bitmap = Glide.with(getActivity())
                            .asBitmap()
                            .load(imageURL)
                            .submit(150,150)
                            .get();

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
            }
        }
        ConvertImage convertImage = new ConvertImage();
        convertImage.execute(imageURL);
    }
}
