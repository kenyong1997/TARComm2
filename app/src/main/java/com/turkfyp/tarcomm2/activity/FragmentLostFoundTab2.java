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
import com.turkfyp.tarcomm2.DatabaseObjects.LostFound;
import com.turkfyp.tarcomm2.DatabaseObjects.LostFoundRVAdapter;
import com.turkfyp.tarcomm2.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentLostFoundTab2 extends Fragment {

    public static boolean allowRefresh;
    private static final String TAG = "FragmentLostFoundTab2";

    private static String GET_URL = "https://tarcomm.000webhostapp.com/getFoundItem.php";
    SwipeRefreshLayout swipeRefreshLostFound;
    List<LostFound> lostFoundList;

    RequestQueue queue;
    RecyclerView rvLostFound;

    public FragmentLostFoundTab2() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_lost_found_tab2, container, false);

        rvLostFound = (RecyclerView) v.findViewById(R.id.rvLostFound);
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
//        lvLostFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                LostFound selectedItem =(LostFound) parent.getItemAtPosition(position);
//                Intent lostFoundDetailIntent = new Intent(getActivity(),LostFoundDetailActivity.class);
//                lostFoundDetailIntent.putExtra("lostItemName",selectedItem.getLostItemName());
//                lostFoundDetailIntent.putExtra("lostItemDesc",selectedItem.getLostItemDesc());
//                lostFoundDetailIntent.putExtra("email",selectedItem.getEmail());
//                lostFoundDetailIntent.putExtra("lostDate", selectedItem.getLostDate());
//                lostFoundDetailIntent.putExtra("lostItemContactName",selectedItem.getContactName());
//                lostFoundDetailIntent.putExtra("lostItemContactNo",selectedItem.getContactNo());
//                lostFoundDetailIntent.putExtra("checkYourUpload",false);
//
//                ImageView ivImage = (ImageView) view.findViewById(R.id.imageViewLostItemImage);
//                ivImage.buildDrawingCache();
//                Bitmap image = ivImage.getDrawingCache();
//                lostFoundDetailIntent.putExtra("LostImage", image);
//                lostFoundDetailIntent.putExtra("LostImageURL", selectedItem.getLostItemURL());
//
//                startActivity(lostFoundDetailIntent);
//            }
//        });

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
                                JSONObject lostFoundResponse = (JSONObject) response.get(i);
                                String category = lostFoundResponse.getString("category");
                                String lostItemName = lostFoundResponse.getString("lostItemName");
                                String lostItemDesc = lostFoundResponse.getString("lostItemDesc");
                                String lostItemURL = lostFoundResponse.getString("url");
                                String email = lostFoundResponse.getString("email");
                                String contactName = lostFoundResponse.getString("fullname");
                                String contactNo = lostFoundResponse.getString("contactno");
                                String lostDate = lostFoundResponse.getString("lostDate");

                                LostFound lostFound = new LostFound(category, lostItemName, lostItemDesc, lostItemURL, lostDate, email, contactName, contactNo);
                                lostFoundList.add(lostFound);
                            }
                            //load the item into adapter
                            setRVAdapter(lostFoundList);

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

    private void setRVAdapter(List<LostFound> lostFoundList){
        LostFoundRVAdapter myAdapter = new LostFoundRVAdapter(getActivity(),lostFoundList) ;
        rvLostFound.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvLostFound.setAdapter(myAdapter);
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
                setRVAdapter(lostFoundList);
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
