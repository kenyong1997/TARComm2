package com.turkfyp.tarcomm2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.turkfyp.tarcomm2.R;
import com.turkfyp.tarcomm2.guillotine.animation.GuillotineAnimation;
import com.turkfyp.tarcomm2.widget.CanaroTextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.BindView;

import android.content.Intent;

public class MainActivity extends AppCompatActivity {
    private static final long RIPPLE_DURATION = 250;
    private ViewPager mViewpager;
    private ViewPagerAdapter mAdapter;
    private ArrayList<ViewPagerModel> mContents;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.root)
    FrameLayout root;
    @BindView(R.id.content_hamburger)
    View contentHamburger;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        ButterKnife.bind(this);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }

        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine, null);
        root.addView(guillotineMenu);

        new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .build();

        mViewpager = (ViewPager) findViewById(R.id.viewpager);
        mContents = new ArrayList<>();
        int images[] =  {R.drawable.one,R.drawable.two,R.drawable.three,R.drawable.four,R.drawable.five,R.drawable.six};
        String names[] = {"Mina","Mina","Mina","Mina","Mina","Mina"};
        String desc[] = {"TWICE","TWICE","TWICE","TWICE","TWICE","TWICE"};
        String location[] = {"Korea","Korea","Korea","Korea","Korea","Korea"};

        for(int i =0;i<images.length;i++)
        {
            ViewPagerModel viewPagerModel = new ViewPagerModel();

            viewPagerModel.images = images[i];
            viewPagerModel.name = names[i];
            viewPagerModel.desc = desc[i];
            viewPagerModel.location = location[i];

            mContents.add(viewPagerModel);


        }
        mAdapter = new ViewPagerAdapter(mContents,this);
        mViewpager.setPageTransformer(true, new ViewPagerStack());
        mViewpager.setOffscreenPageLimit(6);
        mViewpager.setAdapter(mAdapter);



    }
   public void map_onclick(View view){
        Intent i = new Intent (this,MapActivity2.class);
        startActivity(i);
   }
    public void highlight_event_onclick(View view){
        Intent i = new Intent (this,MainActivity.class);
        startActivity(i);
    }
    public void market_onclick(View view){
        Intent i = new Intent (this,MarketplaceActivity.class);
        startActivity(i);
    }
    private class ViewPagerStack implements ViewPager.PageTransformer{

        @Override
        public void transformPage(View page, float position) {
            if(position >= 0){
                page.setScaleX(0.7f - 0.05f * position);
                page.setScaleY(0.7f);
                page.setTranslationX(-page.getWidth() * position);

                page.setTranslationY(-30*position);
            }
        }
    }

}
