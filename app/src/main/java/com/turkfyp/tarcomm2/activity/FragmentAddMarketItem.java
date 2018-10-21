package com.turkfyp.tarcomm2.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.turkfyp.tarcomm2.DatabaseObjects.User;
import com.turkfyp.tarcomm2.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class FragmentAddMarketItem extends Fragment {

    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private Uri filePath;

    RadioGroup rgItemCategory;
    RadioButton rbItemCategory;
    TextView tvItemPrice;
    EditText etItemPrice;
    ImageView imgViewMarketItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_market_item, container, false);


        rgItemCategory = (RadioGroup)v.findViewById(R.id.rgItemCategory);
        rbItemCategory = (RadioButton)v.findViewById(rgItemCategory.getCheckedRadioButtonId());
        tvItemPrice = (TextView)v.findViewById(R.id.tvItemPrice);
        etItemPrice = (EditText)v.findViewById(R.id.etItemPrice);
        imgViewMarketItem =(ImageView)v.findViewById(R.id.imgViewMarketItem);

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

        imgViewMarketItem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showFileChooser();
            }
        });

        return v;
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {

                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);

                imgViewMarketItem.setBackgroundColor(Color.WHITE);
                imgViewMarketItem.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    private String uploadImage(final User user) {
        class UploadImage extends AsyncTask<Bitmap, Void, String> {
            String image;

            // ADD EVENT LOCATION INFORMATION LATER !!!

            ProgressDialog loading;
            ImgRequestHandler rh = new ImgRequestHandler();


            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];
                String uploadImage = getStringImage(bitmap);

                image = uploadImage;

                user.setProfilepicURL(image);
                return uploadImage;
            }
        }
        UploadImage ui = new UploadImage();
        ui.execute(bitmap);

        return ui.image;
    }
}

