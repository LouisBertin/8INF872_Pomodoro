package com.example.louisbertin.pomodoro;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Objects;

public class Tab1 extends Fragment {

    private final int ONE_SECOND = 1000;

    private View rootView;
    private Switch soundSwitch;

    // Timer button
    private Button bTimer;
    private CountDownTimer timer;

    // Time display
    private TextView timeText;
    private TextView timeDisplay;

    private boolean running;
    private long initialTime;
    private long currentTime;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab1, container, false);

        setSoundSwitch();

        PreferenceManager.setDefaultValues(getContext(), R.xml.pref_main, false);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        checkRunningState();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putLong("time", currentTime);
        outState.putBoolean("running", running);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            currentTime = savedInstanceState.getLong("time");
            running = savedInstanceState.getBoolean("running");
        }
    }

    private void checkRunningState() {
        if (running) {
            initialTime = getTimeFromSettings();

            setTimer();
            setDisplayTime();

            // startTimer();
        } else {
            setTimer();
            setNewTimer(initialTime);
            setDisplayTime();

            bTimer.setText(R.string.timer_start);
            timeText.setVisibility(View.VISIBLE);
            timeDisplay.setVisibility(View.VISIBLE);
        }
    }

    /*
    * Retourne le temps paramétré dans Settings en millisecondes.
    * */
    private long getTimeFromSettings() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        long index = Long.parseLong(sharedPreferences.getString("key_time_pom", "-1"));
        return 300000 + 300000 * index;
    }

    private View.OnClickListener timeClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!running) startTimer();
            else stopTimer();
        }
    };

    public void setTimer() {
        initialTime = getTimeFromSettings();

        bTimer = rootView.findViewById(R.id.timer_button);
        bTimer.setOnClickListener(timeClick);
        updateTimer((int) currentTime / ONE_SECOND);
    }

    private void setDisplayTime() {
        timeText = rootView.findViewById(R.id.tab1_time_text);

        timeDisplay = rootView.findViewById(R.id.tab1_time);
        timeDisplay.setText(getTime((int) initialTime / 1000));
    }

    private void setNewTimer(long time) {
        currentTime = time;
        timer = new CountDownTimer(time, ONE_SECOND) {

            @Override
            public void onTick(long l) {
                currentTime -= ONE_SECOND;
                updateTimer((int) l / ONE_SECOND);
            }

            @Override
            public void onFinish() {
                MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.airhorn);
                mediaPlayer.start();
                bTimer.setText("0:00");
            }
        };
    }

    public void updateTimer(int secondsLeft) {
        bTimer.setText(getTime(secondsLeft));
    }

    public void startTimer() {
        running = true;
        timer.start();

        timeText.setVisibility(View.INVISIBLE);
        timeDisplay.setVisibility(View.INVISIBLE);
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
                        setNewTimer(initialTime);

                        bTimer.setText(R.string.timer_start);
                        timeText.setVisibility(View.VISIBLE);
                        timeDisplay.setVisibility(View.VISIBLE);
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

    private void setSoundSwitch() {
        soundSwitch = rootView.findViewById(R.id.silentSwitch);
        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) ((MainActivity) Objects.requireNonNull(getActivity())).setSoundOff();
                else ((MainActivity) Objects.requireNonNull(getActivity())).setSoundOn();
                Log.d("pwt", "value changed" + soundSwitch.isChecked());
            }
        });
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    private String getTime(int time) {
        int minutes = time / 60;
        int seconds = time - (minutes * 60);

        String secondString = Integer.toString(seconds);

        if (seconds < 10) {
            secondString = "0" + secondString;
        }

        return String.format("%s:%s", Integer.toString(minutes), secondString);
    }
}

