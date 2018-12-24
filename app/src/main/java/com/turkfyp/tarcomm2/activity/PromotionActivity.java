package com.turkfyp.tarcomm2.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import com.turkfyp.tarcomm2.R;

public class PromotionActivity extends AppCompatActivity {

    protected TextView tvPromotionPhone, tvPromotionEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion);

        tvPromotionPhone = (TextView) findViewById(R.id.tvPromotionPhone);
        tvPromotionEmail = (TextView) findViewById(R.id.tvPromotionEmail);

        SpannableString content = new SpannableString("0126552875");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvPromotionPhone.setText(content);

        SpannableString content2 = new SpannableString("kenyong1997@gmail.com");
        content2.setSpan(new UnderlineSpan(), 0, content2.length(), 0);
        tvPromotionEmail.setText(content2);
    }
    public void onBackClicked(View view){
        finish();
    }

    public void onClickPromotionPhone(View view) {

        PackageManager packageManager = getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);

        try {
            //If want include message to whatsapp
            //String url = "https://api.whatsapp.com/send?phone=+6"+ ownerContact +"&text=" + URLEncoder.encode(message, "UTF-8");

            String url = "https://api.whatsapp.com/send?phone=+6"+ "0126552875";
            sendIntent.setPackage("com.whatsapp");
            sendIntent.setData(Uri.parse(url));
            if (sendIntent.resolveActivity(packageManager) != null) {
                startActivity(sendIntent);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
