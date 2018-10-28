package com.turkfyp.tarcomm2.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.turkfyp.tarcomm2.DatabaseObjects.LostFound;
import com.turkfyp.tarcomm2.DatabaseObjects.LostFoundAdapter;
import com.turkfyp.tarcomm2.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentLostFoundTab1 extends Fragment {

    public static boolean allowRefresh;

    private static final String TAG = "FragmentLostFoundTab1";

    private static String GET_URL = "https://tarcomm.000webhostapp.com/getLostItem.php";
    ListView lvLostFound;
    SwipeRefreshLayout swipeRefreshLostFound;
    List<LostFound> lostFoundList;

    RequestQueue queue;

    public FragmentLostFoundTab1() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_lost_found_tab1, container, false);

        lvLostFound = (ListView) v.findViewById(R.id.lvLostFound);
        swipeRefreshLostFound = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLostFound);

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
                Intent lostFoundDetailIntent = new Intent(getActivity(),LostFoundDetailActivity.class);
                lostFoundDetailIntent.putExtra("lostItemName",selectedItem.getLostItemName());
                lostFoundDetailIntent.putExtra("email",selectedItem.getEmail());
                lostFoundDetailIntent.putExtra("lostItemDesc",selectedItem.getLostItemDesc());
                lostFoundDetailIntent.putExtra("lostDate", selectedItem.getLostDate());
                lostFoundDetailIntent.putExtra("lostItemContactName",selectedItem.getContactName());
                lostFoundDetailIntent.putExtra("lostItemContactNo",selectedItem.getContactNo());
                lostFoundDetailIntent.putExtra("checkYourUpload",false);

                ImageView ivImage = (ImageView) view.findViewById(R.id.imageViewLostItemImage);
                ivImage.buildDrawingCache();
                Bitmap image = ivImage.getDrawingCache();
                lostFoundDetailIntent.putExtra("LostImage", image);
                lostFoundDetailIntent.putExtra("LostImageURL", selectedItem.getLostItemURL());

                startActivity(lostFoundDetailIntent);
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

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            lostFoundList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject textbookResponse = (JSONObject) response.get(i);
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
        final LostFoundAdapter adapter = new LostFoundAdapter(getActivity(), R.layout.fragment_lost_found_tab1, lostFoundList);
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
