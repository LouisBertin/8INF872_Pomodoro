package com.example.louisbertin.pomodoro.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.louisbertin.pomodoro.MainActivity;
import com.example.louisbertin.pomodoro.R;

public class SplashActivity extends AppCompatActivity {

    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;
    private TextView[] mDots;

    private Button mSkipButton;

    private SliderAdapter sliderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkFirstRun();

        setContentView(R.layout.activity_splash);

        setElements();

        mSlideViewPager.setAdapter(sliderAdapter);

        addDotsIndicator(0);

        mSlideViewPager.addOnPageChangeListener(viewListener);
    }

    private void checkFirstRun() {
        SharedPreferences settings = getSharedPreferences("preferences", 0);
        boolean firstRun = settings.getBoolean("firstRun", true);
        if (firstRun) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("firstRun", false);
            editor.apply();
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void setElements() {
        mSlideViewPager = findViewById(R.id.splash_view_pager);
        mDotLayout = findViewById(R.id.splash_dots_layout);

        mSkipButton = findViewById(R.id.splash_button_skip);
        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        sliderAdapter = new SliderAdapter(this);
    }

    public void addDotsIndicator(int position) {
        mDots = new TextView[3];
        mDotLayout.removeAllViews();

        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorGrey));

            mDotLayout.addView(mDots[i]);
        }

        mDots[position].setTextColor(getResources().getColor(R.color.colorWhite));
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {
        }

        @Override
        public void onPageSelected(int i) {
            addDotsIndicator(i);

            if (i == mDots.length - 1)
                mSkipButton.setText("Let's go!");
            else
                mSkipButton.setText("Skip");
        }

        @Override
        public void onPageScrollStateChanged(int i) {
        }
    };

}
