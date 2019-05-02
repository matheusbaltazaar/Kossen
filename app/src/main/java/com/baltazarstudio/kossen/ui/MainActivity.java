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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baltazarstudio.kossen.R;
import com.baltazarstudio.kossen.context.AppContext;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private Timer mainTimer;
    private TextView tvTimer;
    private boolean clockEnabled = false;
    private int totalSeconds = 0;
    private Runnable clockLabelUpdateThread;
    private Runnable targetReachedThread;
    private Button button;
    private TextView lblChooseHour;
    private TextView lblChooseMinute;
    private TextView lblChooseSecond;
    private int totalTargetSeconds = 0;
    private boolean isTargetReached = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.bt_start_stop_clock);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clockEnabled = !clockEnabled;

                if (clockEnabled) {
                    if (totalSeconds == 0)
                        defineTimeTarget();
                    resumeClock();
                } else {
                    stopClock();
                }
            }
        });

        tvTimer = findViewById(R.id.tv_clock_counter);

        targetReachedThread = initializeTargetReachedThread(this);

        clockLabelUpdateThread = new Runnable() {
            @Override
            public void run() {
                tvTimer.setText(getFormattedTime());
            }
        };


        final ImageButton buttonIncreaseHour = findViewById(R.id.button_increase_hour);
        buttonIncreaseHour.setTag(R.id.button_increase_hour);
        buttonIncreaseHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increase(buttonIncreaseHour);
            }
        });
        final ImageButton buttonDecreaseHour = findViewById(R.id.button_decrease_hour);
        buttonDecreaseHour.setTag(R.id.button_decrease_hour);
        buttonDecreaseHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrease(buttonDecreaseHour);
            }
        });
        final ImageButton buttonIncreaseMinute = findViewById(R.id.button_increase_minute);
        buttonIncreaseMinute.setTag(R.id.button_increase_minute);
        buttonIncreaseMinute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increase(buttonIncreaseMinute);
            }
        });
        final ImageButton buttonDecreaseMinute = findViewById(R.id.button_decrease_minute);
        buttonDecreaseMinute.setTag(R.id.button_decrease_minute);
        buttonDecreaseMinute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrease(buttonDecreaseMinute);
            }
        });
        final ImageButton buttonIncreaseSecond = findViewById(R.id.button_increase_second);
        buttonIncreaseSecond.setTag(R.id.button_increase_second);
        buttonIncreaseSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increase(buttonIncreaseSecond);
            }
        });
        final ImageButton buttonDecreaseSecond = findViewById(R.id.button_decrease_second);
        buttonDecreaseSecond.setTag(R.id.button_decrease_second);
        buttonDecreaseSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrease(buttonDecreaseSecond);
            }
        });

        lblChooseHour = findViewById(R.id.lb_choose_hour);
        lblChooseHour.setText(R.string.all_label_time_choose_placeholder);
        lblChooseMinute = findViewById(R.id.lb_choose_minute);
        lblChooseMinute.setText(R.string.all_label_time_choose_placeholder);
        lblChooseSecond = findViewById(R.id.lb_choose_second);
        lblChooseSecond.setText(R.string.all_label_time_choose_placeholder);

    }

    private Runnable initializeTargetReachedThread(final Context context) {
        return new Runnable() {
            @Override
            public void run() {
                isTargetReached = true;
                playDefinedSound();
                stopClock();
                Toast.makeText(context, "FINALIZADO", Toast.LENGTH_LONG).show();
            }
        };
    }


    private void increase(ImageButton button) {
        int id = (int) button.getTag();

        if (id == R.id.button_increase_hour) {
            int number = Integer.parseInt(lblChooseHour.getText().toString());

            if (number == 23)
                number = 0;
            else
                number++;

            lblChooseHour.setText(number < 10 ? "0" + number : Integer.toString(number));

        } else if (id == R.id.button_increase_minute) {
            int number = Integer.parseInt(lblChooseMinute.getText().toString());

            if (number == 59)
                number = 0;
            else
                number++;

            lblChooseMinute.setText(number < 10 ? "0" + number : Integer.toString(number));

        } else if (id == R.id.button_increase_second) {
            int number = Integer.parseInt(lblChooseSecond.getText().toString());

            if (number == 59)
                number = 0;
            else
                number++;

            lblChooseSecond.setText(number < 10 ? "0" + number : Integer.toString(number));

        }
    }

    private void decrease(ImageButton button) {
        int id = (int) button.getTag();

        if (id == R.id.button_decrease_hour) {
            int number = Integer.parseInt(lblChooseHour.getText().toString());

            if (number == 0)
                number = 23;
            else
                number--;

            lblChooseHour.setText(number < 10 ? "0" + number : Integer.toString(number));

        } else if (id == R.id.button_decrease_minute) {
            int number = Integer.parseInt(lblChooseMinute.getText().toString());

            if (number == 0)
                number = 59;
            else
                number--;

            lblChooseMinute.setText(number < 10 ? "0" + number : Integer.toString(number));

        } else if (id == R.id.button_decrease_second) {
            int number = Integer.parseInt(lblChooseSecond.getText().toString());

            if (number == 0)
                number = 59;
            else
                number--;

            lblChooseSecond.setText(number < 10 ? "0" + number : Integer.toString(number));

        }
    }

    private void stopClock() {
        mainTimer.cancel();

        if (totalSeconds == 0 || isTargetReached)
            button.setText(R.string.all_button_start);
        else
            button.setText(R.string.all_button_resume);
    }

    private void resumeClock() {
        mainTimer = new Timer();
        mainTimer.scheduleAtFixedRate(initializeTimer(), 0, 1000);
        button.setText(R.string.all_button_stop);
    }

    private void restoreClock() {
        stopClock();
        tvTimer.setText(R.string.tv_clock_label_placeholder);
        totalSeconds = 0;
        clockEnabled = false;
    }

    private TimerTask initializeTimer() {
        return new TimerTask() {
            @Override
            public void run() {
                incrementTotalSeconds();
                updateClockLabel();
                verifyTimeTarget();
            }
        };
    }

    private void updateClockLabel() {
        this.runOnUiThread(clockLabelUpdateThread);
    }

    private void targetReached() {
        this.runOnUiThread(targetReachedThread);
    }

    private String getFormattedTime() {
        String clock;
        if (totalSeconds < 60) {
            clock = "00:00:"
                    + (totalSeconds < 10 ? "0" + totalSeconds : totalSeconds);
        } else {
            int seconds = totalSeconds % 60;
            int minutos = totalSeconds / 60;

            if (minutos < 60) {
                clock = "00:"
                        + (minutos < 10 ? "0" + minutos : minutos)
                        + ":"
                        + (seconds < 10 ? "0" + seconds : seconds);
            } else {
                int horas = minutos / 60;
                minutos = minutos % 60;
                seconds = minutos % 60;
                clock = (horas < 10 ? "0" + horas : horas)
                        + ":"
                        + (minutos < 10 ? "0" + minutos : minutos)
                        + ":"
                        + (seconds < 10 ? "0" + seconds : seconds);
            }
        }

        return clock;
    }

    private void defineTimeTarget() {
        int seconds = Integer.parseInt(lblChooseSecond.getText().toString());
        int minutes = Integer.parseInt(lblChooseMinute.getText().toString());
        int hours = Integer.parseInt(lblChooseHour.getText().toString());

        totalTargetSeconds = seconds
                + minutes * 60
                + hours * 3600;

    }

    private void verifyTimeTarget() {
        if (totalSeconds == totalTargetSeconds) {
            targetReached();
        }
    }

    private void incrementTotalSeconds() {
        totalSeconds++;
    }

    private void playDefinedSound() {
        SharedPreferences preferences = getSharedPreferences(AppContext.PREFS, MODE_PRIVATE);

        final MediaPlayer mediaPlayer = MediaPlayer.create(
                this,
                preferences.getInt(AppContext.TARGET_SOUND,
                        AppContext.DEFAULT_SOUND));
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_restart) {
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
        } else if (id == R.id.action_sound) {
            startActivity(new Intent(this, SelectSoundActivity.class));
        } else if (id == R.id.action_history) {
            // SHOW ACTIVITY DAIMOKU HISTORY
        }
        return super.onOptionsItemSelected(item);
    }
}
