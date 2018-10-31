package com.example.louisbertin.pomodoro;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class Tab1 extends Fragment{

    private Button bTimer;
    private CountDownTimer timer;
    private boolean running;
    private long timeToCount=1500000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1, container, false);
        bTimer=(Button) rootView.findViewById(R.id.timer_button);
        bTimer.setOnClickListener(timeClick);
        updateTimer((int)timeToCount/1000);
        running=false;
        return rootView;
    }

    private View.OnClickListener timeClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!running){
                startTimer();
            }else stopTimer();
        }
    };

    public void updateTimer(int secondsLeft) {
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft - (minutes * 60);

        String secondString = Integer.toString(seconds);

        if (seconds <10) {
            secondString = "0" + secondString;
        }

        bTimer.setText(Integer.toString(minutes) + ":" + secondString);
    }

    public void startTimer(){
            running = true;

            timer = new CountDownTimer(timeToCount, 1000) {

                @Override
                public void onTick(long l) {
                    updateTimer((int) l / 1000);
                }

                @Override
                public void onFinish() {
                    MediaPlayer mediaPlayer =MediaPlayer.create(getContext(),R.raw.airhorn);
                    mediaPlayer.start();
                    bTimer.setText("0:00");
                }
            }.start();
    }

    public void stopTimer(){
        timer.cancel();
        running=false;
        updateTimer((int)timeToCount/1000);
    }

    public void setTimeToCount(long timeToCount) {
        this.timeToCount = timeToCount;
    }
}

