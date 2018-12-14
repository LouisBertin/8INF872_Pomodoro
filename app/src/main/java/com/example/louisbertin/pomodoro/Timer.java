package com.example.louisbertin.pomodoro;

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

import java.util.Objects;

public class Timer extends Fragment {

    private enum State {
        Running,
        Break,
        Stopped
    }

    private final int ONE_SECOND = 1000;
    private final int FIVE_MINUTES = 300000;

    private View rootView;

    // Current task
    private TextView currentTaskText;
    private String currentTask = "“Do your homework!”";

    // Cycle
    private State state;
    private int cycleStreak;

    // Time
    private long initialTime;
    private static long currentTime;

    private ImageView timerStartImage;
    @SuppressLint("StaticFieldLeak")
    private static TextView timerMinutesText;
    @SuppressLint("StaticFieldLeak")
    private static TextView timerSecondsText;
    private static CountDownTimer timer;

    private Switch soundSwitch;
    private MediaPlayer mediaPlayer;

    protected NotificationCompat.Builder mBuilder;
    private Context mContext;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_timer, container, false);

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
        state = currentTime != 0 ? State.Running : State.Stopped;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putLong("time", currentTime);
        outState.putSerializable("state", state);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            currentTime = savedInstanceState.getLong("time");
            state = (State) savedInstanceState.getSerializable("state");
        }
    }

    private void checkRunningState() {
        if (state != State.Running) {
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
        return FIVE_MINUTES + FIVE_MINUTES * index;
    }

    private View.OnClickListener timeClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (state) {
                case Running:
                    stopTimer();
                    break;
                case Break:
                    stopCycle();
                    break;
                case Stopped:
                    startTimer();
                    break;
            }
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
        setNewTimer(time, State.Running);
    }

    private void setNewTimer(long time, final State s) {
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

                switch (s) {
                    case Running:
                        state = State.Break;
                        startBreakTime();
                        break;
                    case Break:
                        state = State.Running;
                        stopBreakTime();
                        break;
                }
            }
        };
    }

    public void startTimer() {
        state = State.Running;

        currentTaskText.setText(currentTask);
        updateVisibility(State.Running);
        displayTimerNotification();

        timer.start();
    }

    public void stopTimer() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(R.string.title_cancel)
                .setMessage(getString(R.string.message_cancel) + cycleStreak + '.')
                .setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        state = State.Stopped;
                        updateVisibility(state);
                        timer.cancel();
                        setNewTimer(initialTime);
                        cycleStreak = 0;

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

    private void stopCycle() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(R.string.title_cancel_break)
                .setMessage(getString(R.string.message_cancel_break) + cycleStreak + '.')
                .setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        state = State.Stopped;
                        updateVisibility(state);
                        timer.cancel();
                        setNewTimer(initialTime);
                        cycleStreak = 0;

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
        Uri alarmValue = Uri.parse(alarmString);
        mediaPlayer = MediaPlayer.create(getContext(), alarmValue);
    }

    /*
    * Envoie une notification à la fin d'un cycle.
    * */
    public void startBreakTime() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(R.string.title_stop)
                .setMessage(getString(R.string.message_stop) + ++cycleStreak + '.')
                .setPositiveButton(R.string.button_stop, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mediaPlayer.stop();
                        currentTaskText.setText(R.string.timer_break);
                        setNewTimer(FIVE_MINUTES, state);
                        timer.start();
                    }
                })
                .create()
                .show();
    }

    /*
    * Envoie une notification à la fin d'une pause de cycle.
    * */
    private void stopBreakTime() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Your break is finished!")
                .setMessage("Do you want to continue?")
                .setPositiveButton("Let's go!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mediaPlayer.stop();
                        currentTaskText.setText(currentTask);
                        setNewTimer(initialTime, state);
                        timer.start();
                    }
                })
                .setNegativeButton("I'm done.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mediaPlayer.stop();
                        state = State.Stopped;
                        updateVisibility(state);
                        setNewTimer(initialTime);
                        cycleStreak = 0;
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
        SharedPreferences mSharedPreference = PreferenceManager.getDefaultSharedPreferences(getContext());
        String color = mSharedPreference.getString("button_color", "008577");
        int colorInt = Color.parseColor("#" + color);

        RelativeLayout Layout = rootView.findViewById(R.id.timer_button);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(colorInt);
        Layout.setBackground(shape);
    }
}

