package com.baltazarstudio.kossen.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baltazarstudio.kossen.R;
import com.baltazarstudio.kossen.context.AppContext;
import com.baltazarstudio.kossen.database.Database;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private Timer mainTimer;
    private TextView tvTimer;
    private boolean clockEnabled = false;
    private int totalSeconds = 0;
    private Runnable updateUIThread;
    private Runnable targetReachedThread;
    private Button buttonStartStop;
    private int totalGoalSeconds = 0;
    private boolean isGoalReached = false;
    private Button buttonReset;
    private TextView tvGoalTime;
    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonStartStop = findViewById(R.id.bt_start_stop_time);
        buttonStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clockEnabled = !clockEnabled;

                if (clockEnabled) {
                    resumeClock();
                } else {
                    stopClock();
                }
            }
        });

        tvTimer = findViewById(R.id.tv_time_counter);

        targetReachedThread = initializeTargetReachedThread(this);
        updateUIThread = initializeUpdateUIThread();

        tvGoalTime = findViewById(R.id.tv_goal_time);

        final View.OnClickListener increaseListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseGoalTime((int) v.getTag());
            }
        };

        final TextView increaseOneHour = findViewById(R.id.button_increase_time_one_hour);
        increaseOneHour.setTag(60);
        increaseOneHour.setOnClickListener(increaseListener);

        final TextView increaseThirtyMinutes = findViewById(R.id.button_increase_time_thirty_minutes);
        increaseThirtyMinutes.setTag(30);
        increaseThirtyMinutes.setOnClickListener(increaseListener);

        final TextView increaseFifteenMinutes = findViewById(R.id.button_increase_time_fifteen_minutes);
        increaseFifteenMinutes.setTag(15);
        increaseFifteenMinutes.setOnClickListener(increaseListener);

        final TextView increaseFiveMinutes = findViewById(R.id.button_increase_time_five_minutes);
        increaseFiveMinutes.setTag(5);
        increaseFiveMinutes.setOnClickListener(increaseListener);

        buttonReset = findViewById(R.id.button_reset);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalSeconds != 0) // Se o timer não foi iniciado
                    createResetAlert();
                else
                    restoreClock();
            }
        });


        database = new Database(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_history) {
            startActivity(new Intent(this, HistoryActivity.class));
        } else if (id == R.id.action_sound) {
            startActivity(new Intent(this, SelectSoundActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void increaseGoalTime(int minutes) {
        totalGoalSeconds += minutes * 60;
//        totalGoalSeconds = 5;
        tvGoalTime.setText(formatLabelTimeString(totalGoalSeconds));

        toggleUIComponents(true);
    }

    private Runnable initializeUpdateUIThread() {
        return new Runnable() {
            @Override
            public void run() {
                if (isGoalReached) {
                    buttonStartStop.setText(R.string.all_button_resume);
                    toggleUIComponents(true);
                } else {
                    tvTimer.setText(formatLabelTimeString(totalSeconds));
                    toggleUIComponents(false);
                }
            }
        };
    }

    private Runnable initializeTargetReachedThread(final Context context) {
        return new Runnable() {
            @Override
            public void run() {
                isGoalReached = true;
                stopClock();
                playSound();
                updateUI();
                Toast.makeText(context, AppContext.getCurrentDateTime(), Toast.LENGTH_LONG).show();

                createDialogGoalReached();
            }
        };
    }

    private void createDialogGoalReached() {
        final String data = AppContext.getCurrentDateTime();

        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.all_label_goal_reached)
                .setMessage(String.format("Tempo: %s", data))
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        database.register(
                                tvTimer.getText().toString(),
                                data);
                        dialog.dismiss();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        stopSound();
                    }
                })
                .create().show();
    }

    private void stopClock() {
        if (mainTimer != null)
            mainTimer.cancel();

        if (totalSeconds == 0 || isGoalReached)
            buttonStartStop.setText(R.string.all_button_start);
        else
            buttonStartStop.setText(R.string.all_button_resume);

        toggleUIComponents(true);
    }

    private void resumeClock() {
        mainTimer = new Timer();
        mainTimer.scheduleAtFixedRate(initializeTimer(), 0, 1000);
        buttonStartStop.setText(R.string.all_button_stop);
    }

    private void restoreClock() {
        stopClock();
        tvTimer.setText(R.string.tv_clock_label_placeholder);
        tvGoalTime.setText(R.string.tv_clock_label_placeholder);
        buttonStartStop.setText(R.string.all_button_start);
        totalSeconds = 0;
        totalGoalSeconds = 0;
        clockEnabled = false;
        isGoalReached = false;
        toggleUIComponents(false);
    }

    private void toggleUIComponents(boolean enabled) {
//        restartMenuItem.setEnabled(enabled);
        buttonReset.setEnabled(enabled);

//        if (enabled) {
//            buttonReset.setBackgroundResource(R.drawable.all_rounded_border_button_green);
//            buttonReset.setTextColor(ContextCompat.getColor(this, R.color.green));
//        } else {
//            buttonReset.setBackgroundResource(R.drawable.all_rounded_border_button_gray);
//            buttonReset.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
//        }
    }

    private TimerTask initializeTimer() {
        return new TimerTask() {
            @Override
            public void run() {
                updateUI();
                incrementTotalSeconds();
            }
        };
    }

    private void updateUI() {
        this.runOnUiThread(updateUIThread);
    }

    private void targetReached() {
        this.runOnUiThread(targetReachedThread);
    }

    private String formatLabelTimeString(int totalSeconds) {
        String clock;
        if (totalSeconds < 60) {
            clock = "00:00:"
                    + (totalSeconds < 10 ? "0" + totalSeconds : totalSeconds);
        } else if (totalSeconds < 3600) {
            int minutos = totalSeconds / 60;
            int seconds = totalSeconds % 60;
            clock = "00:"
                    + (minutos < 10 ? "0" + minutos : minutos)
                    + ":"
                    + (seconds < 10 ? "0" + seconds : seconds);
        } else {

            int horas = totalSeconds / 3600; // 60 * 60
            int minutos = (totalSeconds % 3600);
            int seconds = minutos % 60;
            minutos /= 60;


            clock = (horas < 10 ? "0" + horas : horas)
                    + ":"
                    + (minutos < 10 ? "0" + minutos : minutos)
                    + ":"
                    + (seconds < 10 ? "0" + seconds : seconds);
        }

        return clock;
    }

    private void incrementTotalSeconds() {
        totalSeconds++;

        // EVERY TIME TOTAL SECONDS IS INCREMENTED
        // THE GOAL TIME IS VERIFIED
        if (totalSeconds == totalGoalSeconds) {
            targetReached();
        }
    }

    private void playSound() {
        SharedPreferences preferences = getSharedPreferences(AppContext.PREFS, MODE_PRIVATE);

        final MediaPlayer mediaPlayer = MediaPlayer.create(
                this,
                preferences.getInt(AppContext.DAIMOKU_GOAL_SOUND,
                        AppContext.DEFAULT_DAIMOKU_GOAL_SOUND));
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            int timesPlayed = 0;

            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (++timesPlayed < 5)
                    mediaPlayer.start();
                else
                    mediaPlayer.release();
            }
        });
        mediaPlayer.start();
    }

    private void stopSound() {
        // TODO
    }

    private void createResetAlert() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmação")
                .setMessage("Quer mesmo reiniciar a contagem ?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        restoreClock();
                    }
                })
                .setNegativeButton("Não", null)
                .create().show();
    }

}
