package com.turkfyp.tarcomm2.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.turkfyp.tarcomm2.R;

import org.w3c.dom.Text;

public class FragmentAddLostFound extends Fragment {


    RadioGroup rgLostCategory;
    RadioButton rbLostCategory;
    TextView tvLostFoundDate;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_lost_found_item, container, false);

        rgLostCategory = (RadioGroup)v.findViewById(R.id.rgLostCategory);
        rbLostCategory = (RadioButton)v.findViewById(rgLostCategory.getCheckedRadioButtonId());
        tvLostFoundDate = (TextView)v.findViewById(R.id.tvLostFoundDate);

        rgLostCategory.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.category_lost){
                    tvLostFoundDate.setText("Lost Date");
                }else if (checkedId == R.id.category_found){
                    tvLostFoundDate.setText("Found Date");
                }
            }
        });
        return v;
    }
    }
