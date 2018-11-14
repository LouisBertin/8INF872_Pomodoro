package com.example.louisbertin.pomodoro;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

public class Tab1 extends Fragment {

    private final int ONE_SECOND = 1000;

    private Button bTimer;
    private CountDownTimer timer;
    private boolean running;
    private long timeToCount = 1500000;
    private Switch soundSwitch;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1, container, false);

        getSavedInstance(savedInstanceState);

        setTimer(rootView);
        setSoundSwitch(rootView);

        if (running) startTimer();

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putLong("time", timeToCount);
        outState.putBoolean("running", running);
    }

    private void getSavedInstance(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            timeToCount = savedInstanceState.getLong("time");
            running = savedInstanceState.getBoolean("running");
        }
    }

    private View.OnClickListener timeClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!running) startTimer();
            else stopTimer();
        }
    };

    public void setTimer(View rootView) {
        timer = new CountDownTimer(timeToCount, ONE_SECOND) {

            @Override
            public void onTick(long l) {
                timeToCount -= ONE_SECOND;
                updateTimer((int) l / ONE_SECOND);
            }

            @Override
            public void onFinish() {
                MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.airhorn);
                mediaPlayer.start();
                bTimer.setText("0:00");
            }
        };

        bTimer = rootView.findViewById(R.id.timer_button);
        bTimer.setOnClickListener(timeClick);
        updateTimer((int) timeToCount / ONE_SECOND);
    }

    public void updateTimer(int secondsLeft) {
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft - (minutes * 60);

        String secondString = Integer.toString(seconds);

        if (seconds < 10) {
            secondString = "0" + secondString;
        }

        bTimer.setText(String.format("%s:%s", Integer.toString(minutes), secondString));
    }

    public void startTimer() {
        running = true;
        timer.start();
    }

    public void stopTimer() {
        running = false;
        timer.cancel();
        updateTimer((int) timeToCount / ONE_SECOND);
    }

    private void setSoundSwitch(View rootView) {
        soundSwitch = rootView.findViewById(R.id.silentSwitch);
        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) ((MainActivity) getActivity()).setSoundOff();
                else ((MainActivity) getActivity()).setSoundOn();
                Log.d("pwt", "value changed" + soundSwitch.isChecked());
            }
        });
    }

    public void setTimeToCount(long timeToCount) {
        this.timeToCount = timeToCount;
    }
}

