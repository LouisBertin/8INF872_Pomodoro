package com.example.louisbertin.pomodoro;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
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

import java.util.Objects;

public class Tab1 extends Fragment {

    private final int ONE_SECOND = 1000;

    private View rootView;

    // Timer button
    @SuppressLint("StaticFieldLeak")
    private static Button bTimer;
    private static CountDownTimer timer;

    private boolean running;
    private long initialTime;
    private static long currentTime;
    private Switch soundSwitch;
    private Uri alarmValue;
    private MediaPlayer mediaPlayer;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab1, container, false);

        // setSoundSwitch();

        setRingtone();

        mediaPlayer = MediaPlayer.create(getContext(), alarmValue);

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
        System.out.println("saving bundle");
        outState.putLong("time", currentTime);
        outState.putBoolean("running", running);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("created: " + savedInstanceState);

        if (savedInstanceState != null) {
            currentTime = savedInstanceState.getLong("time");
            running = savedInstanceState.getBoolean("running");
        }
    }

    private void checkRunningState() {
        setTimer();

        if (!running) {
            setNewTimer(initialTime);
            bTimer.setText(R.string.timer_start);
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

    private void setNewTimer(long time) {
        currentTime = time;
        timer = new CountDownTimer(time, ONE_SECOND) {

            @Override
            public void onTick(long l) {
                System.out.println("tick");
                currentTime -= ONE_SECOND;
                updateTimer((int) l / ONE_SECOND);
            }

            @Override
            public void onFinish() {
                mediaPlayer.start();
                bTimer.setText("0:00");
                stopRingtone();
            }
        };
    }

    public void updateTimer(int secondsLeft) {
        bTimer.setText(getTime(secondsLeft));
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
                        setNewTimer(initialTime);

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

    private void setSoundSwitch() {
        // soundSwitch = rootView.findViewById(R.id.silentSwitch);
        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) ((MainActivity) Objects.requireNonNull(getActivity())).setSoundOff();
                else ((MainActivity) Objects.requireNonNull(getActivity())).setSoundOn();
                Log.d("pwt", "value changed" + soundSwitch.isChecked());
            }
        });
    }

    private void setRingtone() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String alarmString = sharedPreferences.getString("key_pom_end_ringtone", "DEFAULT_RINGTONE");
        alarmValue = Uri.parse(alarmString);
    }

    public void stopRingtone() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(R.string.title_stop)
                .setMessage(R.string.message_stop)
                .setPositiveButton(R.string.button_stop, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mediaPlayer.stop();
                        running = false;
                        setNewTimer(initialTime);
                        bTimer.setText(R.string.timer_start);
                    }
                })
                .create();
        alert.show();
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

