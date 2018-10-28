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
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
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
import com.turkfyp.tarcomm2.DatabaseObjects.LostFoundUploadAdapter;
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

    ExpandableListView elvLostFoundUpload;
    LostFoundUploadAdapter lostFoundUploadAdapter;
    List<String> listDataHeader;
    HashMap<String, List<LostFound>> listDataChild;

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

        elvLostFoundUpload = (ExpandableListView) v.findViewById(R.id.elvLostFoundUpload);
        swipeRefreshLostFound = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLostFound);
        FloatingActionButton fabAddItem = (FloatingActionButton)v.findViewById(R.id.addItemFAB);

        try {
            //initialize lostFoundList
            lostFoundList = new ArrayList<>();

            downloadLostFoundRecords(getActivity().getApplicationContext(), GET_URL);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        elvLostFoundUpload.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                LostFound selectedItem;
                selectedItem = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);

                Intent lostFoundDetail = new Intent(getActivity(),LostFoundDetailActivity.class);
                lostFoundDetail.putExtra("lostCategory",selectedItem.getCategory());
                lostFoundDetail.putExtra("lostItemName",selectedItem.getLostItemName());
                lostFoundDetail.putExtra("email",selectedItem.getEmail());
                lostFoundDetail.putExtra("lostItemDesc",selectedItem.getLostItemDesc());
                lostFoundDetail.putExtra("lostDate", selectedItem.getLostDate());
                lostFoundDetail.putExtra("lostItemContactName",selectedItem.getContactName());
                lostFoundDetail.putExtra("lostItemContactNo",selectedItem.getContactNo());
                lostFoundDetail.putExtra("checkYourUpload",true);

                ImageView ivImage = (ImageView) view.findViewById(R.id.imageViewLostItemImage);
                ivImage.buildDrawingCache();
                Bitmap image = ivImage.getDrawingCache();
                lostFoundDetail.putExtra("LostImage", image);
                lostFoundDetail.putExtra("LostImageURL", selectedItem.getLostItemURL());

                startActivity(lostFoundDetail);
                return false;
            }
        });

        fabAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addItemIntent = new Intent(getActivity().getApplicationContext(), AddLostFoundItemActivity.class);
                startActivity(addItemIntent);
            }
        });

        swipeRefreshLostFound.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeRefreshLostFound.setRefreshing(true);
                try {
                    downloadLostFoundRecords(getActivity().getApplicationContext(), GET_URL);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                swipeRefreshLostFound.setRefreshing(false);
            }
        });

        return v;

    }

    //retrieve the records from database
    public void downloadLostFoundRecords(Context context, String url) {
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<LostFound>>();

        listDataHeader.add("Lost Item");
        listDataHeader.add("Found Item");

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

                                    List<LostFound> lostList = new ArrayList<>();
                                    List<LostFound> foundList = new ArrayList<>();

                                    for (int i = 0; i < j.length(); i++) {
                                        JSONObject lostFoundResponse = (JSONObject) j.get(i);
                                        String category = lostFoundResponse.getString("category");
                                        String lostItemName = lostFoundResponse.getString("lostItemName");
                                        String lostItemDesc = lostFoundResponse.getString("lostItemDesc");
                                        String lostItemURL = lostFoundResponse.getString("url");
                                        String email = lostFoundResponse.getString("email");
                                        String contactName = lostFoundResponse.getString("fullname");
                                        String contactNo = lostFoundResponse.getString("contactno");
                                        String lostDate = lostFoundResponse.getString("lostDate");

                                        LostFound lostFound = new LostFound(category, lostItemName, lostItemDesc, lostItemURL, lostDate, email, contactName, contactNo);

                                        if(category.toUpperCase().equals("LOST"))
                                            lostList.add(lostFound);
                                        else
                                            foundList.add(lostFound);

//                                        lostFoundList.add(lostFound);
                                    }
                                    //load the item into adapter
//                                    loadItem();

                                    listDataChild.put(listDataHeader.get(0), lostList);
                                    listDataChild.put(listDataHeader.get(1), foundList);

                                    lostFoundUploadAdapter = new LostFoundUploadAdapter(getActivity(),listDataHeader, listDataChild);
                                    elvLostFoundUpload.setAdapter(lostFoundUploadAdapter);
                                    elvLostFoundUpload.expandGroup(0);
                                    elvLostFoundUpload.expandGroup(1);

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
        final LostFoundAdapter adapter = new LostFoundAdapter(getActivity(), R.layout.fragment_lost_found_tab3, lostFoundList);
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
