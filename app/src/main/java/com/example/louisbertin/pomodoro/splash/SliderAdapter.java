package com.example.louisbertin.pomodoro.splash;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.louisbertin.pomodoro.R;

import java.util.Objects;

public class SliderAdapter extends PagerAdapter {

    private Context context;

    SliderAdapter(Context context) {
        this.context = context;
    }

    private int[] slideImages = {
            R.drawable.ic_caml,
            R.drawable.ic_caml,
            R.drawable.ic_caml

    };

    private String[] slideHeadings = {
            "WELCOME, PADAWAN.",
            "NO MORE ZERO DAYS",
            "THE ALMIGHTY POMODORO"
    };

    private String[] slideDesc = {
            "Are you ready to become a more productive person starting today?",
            "Even the slightest amount of work towards your dream counts if you do so regularly. Five minutes of work a day is better than none!",
            "As long as the timer is running, place your focus on your current task only. You will be able to take a short break afterwards, then repeat this cycle as long as you want."
    };

    @Override
    public int getCount() {
        return slideHeadings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = Objects.requireNonNull(layoutInflater).inflate(R.layout.slide_layout, container, false);

        ImageView slideImageView = view.findViewById(R.id.slide_image);
        TextView slideHeading = view.findViewById(R.id.slide_heading);
        TextView slideDescription = view.findViewById(R.id.slide_description);

        slideImageView.setImageResource(slideImages[position]);
        slideHeading.setText(slideHeadings[position]);
        slideDescription.setText(slideDesc[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}
