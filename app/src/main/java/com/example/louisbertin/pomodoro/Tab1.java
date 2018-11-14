package com.example.louisbertin.pomodoro;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class Tab1 extends Fragment {

    private final int ONE_SECOND = 1000;
    private final int SECONDS_IN_MINUTE = 60;

    private Button bTimer;
    private CountDownTimer timer;
    private boolean running;
    private long timeToCount;
    private Switch soundSwitch;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1, container, false);

        getSavedInstance(savedInstanceState);

        setTimer(rootView);
        setSoundSwitch(rootView);

        if (running) startTimer();
        else bTimer.setText(R.string.timer_start);

        PreferenceManager.setDefaultValues(getContext(), R.xml.pref_main, false);
        /*
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String test = sharedPreferences.getString("key_time_pom", "hi");

        long timeSettings = getTimeFromSettings();

        System.out.println("test: " + timeSettings + "s");
        Toast.makeText(getContext(), test, Toast.LENGTH_SHORT).show();
        */

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

    private long getTimeFromSettings() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        long time = Long.parseLong(sharedPreferences.getString("key_time_pom", "-1"));

        time = 300000 + 3 * time * 100000;

        return time;
    }

    private View.OnClickListener timeClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!running) startTimer();
            else stopTimer();
        }
    };

    public void setTimer(View rootView) {
        timeToCount = getTimeFromSettings();
        System.out.println(timeToCount);

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
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(R.string.title_cancel)
                .setMessage(R.string.message_cancel)
                .setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        running = false;
                        timer.cancel();
                        bTimer.setText(R.string.timer_start);
                    }
                })
                .setNegativeButton(R.string.button_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        alert.show();
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

