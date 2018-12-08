package com.example.louisbertin.pomodoro.MainFragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.louisbertin.pomodoro.MainActivity;
import com.example.louisbertin.pomodoro.R;

import java.util.Objects;

public class Tab1 extends Fragment {

    private enum State {
        Running,
        Stopped
    }

    private final int ONE_SECOND = 1000;

    private View rootView;

    // Current task
    private TextView currentTaskText;
    private String currentTask = "“Do your homework!”";

    private ImageView timerStartImage;
    @SuppressLint("StaticFieldLeak")
    private static TextView timerMinutesText;
    @SuppressLint("StaticFieldLeak")
    private static TextView timerSecondsText;
    private static CountDownTimer timer;

    private boolean running;
    private long initialTime;
    private static long currentTime;
    private Switch soundSwitch;
    private Uri alarmValue;
    private MediaPlayer mediaPlayer;

    protected NotificationCompat.Builder mBuilder;
    private Context mContext;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab1, container, false);

        setSoundSwitch();
        setRingtone();

        PreferenceManager.setDefaultValues(getContext(), R.xml.pref_main, false);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        setTimer();
        setCurrentTask();
        checkRunningState();
    }

    // Initialise it from onAttach()
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public void onStart() {
        super.onStart();

        changeButtonColor();

        // if app is quit with back button : don't kill everything
        if (currentTime != 0) {
            running = true;
        }
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
        if (!running) {
            setNewTimer(initialTime);
            updateVisibility(State.Stopped);
        } else {
            currentTaskText.setText(currentTask);
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

    private void setCurrentTask() {
        currentTaskText = rootView.findViewById(R.id.timer_current_task);
    }

    public void setTimer() {
        initialTime = getTimeFromSettings();

        timerStartImage = rootView.findViewById(R.id.timer_start);
        timerMinutesText = rootView.findViewById(R.id.timer_minutes);
        timerSecondsText = rootView.findViewById(R.id.timer_seconds);
        rootView.findViewById(R.id.timer_button).setOnClickListener(timeClick);

        updateTimerText((int) currentTime / ONE_SECOND);
    }

    private void setNewTimer(long time) {
        currentTime = time;
        timer = new CountDownTimer(time, ONE_SECOND) {

            @Override
            public void onTick(long l) {
                // TODO : dumb code.. need to reformat
                int time_in_seconds = (int) l / 1000;
                // format time
                int hours = time_in_seconds / 3600;
                int minutes = (time_in_seconds % 3600) / 60;
                int seconds = time_in_seconds % 60;
                String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

                // display notification on lock screen
                mBuilder.setContentText(timeString);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
                notificationManager.notify(0, mBuilder.build());

                currentTime -= ONE_SECOND;
                updateTimerText((int) l / ONE_SECOND);
            }

            @Override
            public void onFinish() {
                ((MainActivity) Objects.requireNonNull(getActivity())).setSoundOn();
                mediaPlayer.start();
                stopRingtone();
            }
        };
    }

    public void startTimer() {
        running = true;

        currentTaskText.setText(currentTask);
        updateVisibility(State.Running);
        displayTimerNotification();

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
                        updateVisibility(State.Stopped);
                        timer.cancel();
                        setNewTimer(initialTime);

                        // remove lock screen notification
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
                        notificationManager.cancel(0);
                    }
                })
                .setNegativeButton(R.string.button_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create()
                .show();
    }

    private void setSoundSwitch() {
        soundSwitch = rootView.findViewById(R.id.silentSwitch);
        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ((MainActivity) Objects.requireNonNull(getActivity())).setSoundOff();
                } else {
                    ((MainActivity) Objects.requireNonNull(getActivity())).setSoundOn();
                }
                Log.d("pwt", "value changed" + soundSwitch.isChecked());
            }
        });
    }


    private void setRingtone() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String alarmString = sharedPreferences.getString("key_pom_end_ringtone", "-1");
        alarmValue = Uri.parse(alarmString);
        mediaPlayer = MediaPlayer.create(getContext(), alarmValue);
    }

    public void stopRingtone() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(R.string.title_stop)
                .setMessage(R.string.message_stop)
                .setPositiveButton(R.string.button_stop, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mediaPlayer.stop();
                        updateVisibility(State.Stopped);
                        running = false;
                        setNewTimer(initialTime);
                    }
                })
                .create()
                .show();
    }

    private void updateTimerText(int time) {
        int minutes = time / 60;
        int seconds = time - (minutes * 60);

        timerMinutesText.setText((minutes < 10) ? "0" + Integer.toString(minutes) : Integer.toString(minutes));
        timerSecondsText.setText((seconds < 10) ? "0" + Integer.toString(seconds) : Integer.toString(seconds));
    }

    private void updateVisibility(State state) {
        switch (state) {
            case Running:
                currentTaskText.setVisibility(View.VISIBLE);
                timerStartImage.setVisibility(View.INVISIBLE);
                timerMinutesText.setVisibility(View.VISIBLE);
                timerSecondsText.setVisibility(View.VISIBLE);
                soundSwitch.setVisibility(View.VISIBLE);
                break;
            case Stopped:
                currentTaskText.setVisibility(View.INVISIBLE);
                timerStartImage.setVisibility(View.VISIBLE);
                timerMinutesText.setVisibility(View.INVISIBLE);
                timerSecondsText.setVisibility(View.INVISIBLE);
                soundSwitch.setVisibility(View.INVISIBLE);
                break;
        }
    }

    /**
     * display notification when timer is running
     */
    private void displayTimerNotification() {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);

        // Init notification objects
        NotificationChannel mChannel = null;
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(mContext, null);

        mBuilder.setSmallIcon(R.drawable.ic_caml)
                .setContentTitle("Timer is running!")
                .setContentText("Go back to Caml app...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Add channel if SDK > 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel("my_channel_id", "caml", NotificationManager.IMPORTANCE_LOW);
            // Configure the notification channel.
            mChannel.enableLights(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mBuilder.setChannelId("my_channel_id");

            mNotificationManager.createNotificationChannel(mChannel);
        }

        mNotificationManager.notify(0, mBuilder.build());
    }

    private void changeButtonColor() {
        SharedPreferences mSharedPreference= PreferenceManager.getDefaultSharedPreferences(getContext());
        String color = mSharedPreference.getString("button_color", "008577" );
        int colorInt = Color.parseColor("#" + color);

        RelativeLayout Layout = (RelativeLayout) rootView.findViewById(R.id.timer_button);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(colorInt);
        Layout.setBackground(shape);
    }
}

