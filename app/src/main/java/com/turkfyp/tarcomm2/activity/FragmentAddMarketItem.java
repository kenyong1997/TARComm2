package com.turkfyp.tarcomm2.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.turkfyp.tarcomm2.R;

public class FragmentAddMarketItem extends Fragment {


    RadioGroup rgItemCategory;
    RadioButton rbItemCategory;
    TextView tvItemPrice;
    EditText etItemPrice;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_market_item, container, false);


        rgItemCategory = (RadioGroup)v.findViewById(R.id.rgItemCategory);
        rbItemCategory = (RadioButton)v.findViewById(rgItemCategory.getCheckedRadioButtonId());
        tvItemPrice = (TextView)v.findViewById(R.id.tvItemPrice);
        etItemPrice = (EditText)v.findViewById(R.id.etItemPrice);


        rgItemCategory.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.category_sell){
                    tvItemPrice.setVisibility(View.VISIBLE);
                    etItemPrice.setVisibility(View.VISIBLE);
                }else if (checkedId == R.id.category_buy){
                    tvItemPrice.setVisibility(View.VISIBLE);
                    etItemPrice.setVisibility(View.VISIBLE);
                }else if (checkedId == R.id.category_trade){
                    tvItemPrice.setVisibility(View.INVISIBLE);
                    etItemPrice.setVisibility(View.INVISIBLE);
                }
            }
        });

        return v;
    }
}

