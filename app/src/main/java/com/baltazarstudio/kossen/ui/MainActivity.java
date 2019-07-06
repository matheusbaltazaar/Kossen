package com.baltazarstudio.kossen.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baltazarstudio.kossen.R;
import com.baltazarstudio.kossen.database.Database;
import com.baltazarstudio.kossen.model.Sounds;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final String DAIMOKU_GOAL_SOUND = "goal_sound";
    private final int DEFAULT_DAIMOKU_GOAL_SOUND = R.raw.camila_cabello_havana;
    private TextView tvGoalTime;
    private TextView tvTimer;

    private int totalSeconds = 0;
    private int totalGoalSeconds = 0;
    private Button buttonReset;
    private boolean isGoalReached = false;

    private Database database;
    private Button buttonStartStop;
    private boolean clockEnabled = false;
    private Handler handler = new Handler();
    private Runnable runnable;
    // Media Player
    private MediaPlayer mediaPlayer;
    private SharedPreferences preferences = getSharedPreferences("prefs", MODE_PRIVATE);






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


        tvGoalTime = findViewById(R.id.tv_goal_time);

        final View.OnClickListener increaseListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increase((int) v.getTag());
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
            createDialogSelectSound();
        }
        return super.onOptionsItemSelected(item);
    }

    private void increase(int minutes) {
        totalGoalSeconds += minutes * 60;
//        totalGoalSeconds = 5;
        tvGoalTime.setText(formatLabelTimeString(totalGoalSeconds));

        toggleUIComponents(true);
    }

    private void stopClock() {
        handler.removeCallbacks(runnable);

        if (totalSeconds == 0 || isGoalReached)
            buttonStartStop.setText(R.string.all_button_start);
        else
            buttonStartStop.setText(R.string.all_button_resume);

        toggleUIComponents(true);
    }

    private void resumeClock() {
        buttonStartStop.setText(R.string.all_button_stop);

        if (isGoalReached) {
            buttonStartStop.setText(R.string.all_button_resume);
            toggleUIComponents(true);
        } else {
            tvTimer.setText(formatLabelTimeString(totalSeconds));
            toggleUIComponents(false);
        }

        handler.postDelayed(getRunnable(), 1000);
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
        buttonReset.setEnabled(enabled);
    }

    private Runnable getRunnable() {
        if (runnable == null)
            runnable = new Runnable() {
                @Override
                public void run() {
                    updateUI();

                    // EVERY TIME TOTAL SECONDS IS INCREMENTED
                    // THE GOAL TIME IS VERIFIED
                    if (totalSeconds == totalGoalSeconds) {
                        targetReached();
                    }

                    handler.postDelayed(runnable, 1000);
                }
            };
        return runnable;
    }

    private void updateUI() {
        if (isGoalReached) {
            buttonStartStop.setText(R.string.all_button_resume);
            toggleUIComponents(true);
        } else {
            incrementTotalSeconds();
            tvTimer.setText(formatLabelTimeString(totalSeconds));
        }
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

    private void targetReached() {
        isGoalReached = true;
        stopClock();
        playSound();
        updateUI();
        createDialogGoalReached();
    }

    private void createDialogGoalReached() {

        Locale mLocale = new Locale("pt", "BR");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", mLocale);
        final String data = sdf.format(Calendar.getInstance().getTime());

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

    private void incrementTotalSeconds() {
        totalSeconds++;

    }

    private void playSound() {
        int soundId = preferences.getInt(DAIMOKU_GOAL_SOUND, DEFAULT_DAIMOKU_GOAL_SOUND);

        mediaPlayer = MediaPlayer.create(this, soundId);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
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

    private void createDialogSelectSound() {
        final int previousSound = preferences.getInt(DAIMOKU_GOAL_SOUND, DEFAULT_DAIMOKU_GOAL_SOUND);
        final boolean[] confirmed = {false};

        new AlertDialog.Builder(this)
                .setTitle("Escolha uma melodia")
                .setSingleChoiceItems(R.array.array_sounds, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateRing(Sounds.get(which));
                        playSound();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmed[0] = true;
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mediaPlayer.release();
                        if (!confirmed[0]) {
                            updateRing(previousSound);
                        }
                    }
                })
                .create()
                .show();
    }

    private void updateRing(int id) {
        preferences.edit()
                .putInt(DAIMOKU_GOAL_SOUND, id)
                .apply();
    }
}
