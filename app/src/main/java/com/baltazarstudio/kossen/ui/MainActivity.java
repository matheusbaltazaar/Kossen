package com.baltazarstudio.kossen.ui;

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

import com.baltazarstudio.kossen.R;
import com.baltazarstudio.kossen.component.Chronometer;
import com.baltazarstudio.kossen.database.Database;
import com.baltazarstudio.kossen.model.Sounds;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final String DAIMOKU_GOAL_SOUND = "goal_sound";
    private final int DEFAULT_DAIMOKU_GOAL_SOUND = R.raw.camila_cabello_havana;

    private TextView labelGoalTime;
    private TextView labelCurrentTime;
    private Button buttonReset;
    private Button buttonStartStop;


    // Media Player
    private MediaPlayer mediaPlayer;
    private SharedPreferences preferences;

    private Chronometer mChronometer;
    private Button buttonIncreaseOneHour;
    private Button buttonIncreaseThirtyMinutes;
    private Button buttonIncreaseFifteenMinutes;
    private Button buttonIncreaseFiveMinutes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("prefs", MODE_PRIVATE);

        labelGoalTime = findViewById(R.id.tv_goal_time);

        buttonIncreaseOneHour = findViewById(R.id.button_increase_time_one_hour);
        buttonIncreaseOneHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChronometer.addMinutes(60);
                labelGoalTime.setText(mChronometer.getGoalTime());
            }
        });

        buttonIncreaseThirtyMinutes = findViewById(R.id.button_increase_time_thirty_minutes);
        buttonIncreaseThirtyMinutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChronometer.addMinutes(30);
                labelGoalTime.setText(mChronometer.getGoalTime());
            }
        });

        buttonIncreaseFifteenMinutes = findViewById(R.id.button_increase_time_fifteen_minutes);
        buttonIncreaseFifteenMinutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChronometer.addMinutes(15);
                labelGoalTime.setText(mChronometer.getGoalTime());
            }
        });

        buttonIncreaseFiveMinutes = findViewById(R.id.button_increase_time_five_minutes);
        buttonIncreaseFiveMinutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChronometer.addMinutes(5);
                labelGoalTime.setText(mChronometer.getGoalTime());
            }
        });

        buttonReset = findViewById(R.id.button_reset);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChronometer.hasStarted())
                    createResetAlert();
                else
                    restoreClock();
            }
        });

        labelCurrentTime = findViewById(R.id.tv_current_time);

        buttonStartStop = findViewById(R.id.bt_start_stop_time);
        buttonStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChronometer.isRunning()) {
                    mChronometer.stop();
                    buttonStartStop.setText(R.string.all_button_start);
                    toggleUIComponents(true);
                } else {
                    mChronometer.resume();
                    buttonStartStop.setText(R.string.all_button_stop);
                    toggleUIComponents(false);
                }
            }
        });


        setUpChronometer();
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

    private void setUpChronometer() {
        mChronometer = new Chronometer();
        mChronometer.setTimeListener(new Chronometer.TimeListener() {
            @Override
            public void onTick() {
                labelCurrentTime.setText(mChronometer.getCurrentTime());
            }

            @Override
            public void onTargetReached() {
                labelCurrentTime.setText(mChronometer.getCurrentTime());
                playSound();
                createDialogGoalReached();
            }
        });
    }

    private void restoreClock() {
        mChronometer.reset();
        labelCurrentTime.setText(mChronometer.getCurrentTime());
        labelGoalTime.setText(mChronometer.getGoalTime());
        buttonStartStop.setText(R.string.all_button_start);
        toggleUIComponents(true);
    }

    private void toggleUIComponents(boolean enabled) {
        buttonReset.setEnabled(enabled);
        buttonIncreaseFiveMinutes.setEnabled(enabled);
        buttonIncreaseFifteenMinutes.setEnabled(enabled);
        buttonIncreaseThirtyMinutes.setEnabled(enabled);
        buttonIncreaseOneHour.setEnabled(enabled);
    }

    private void createDialogGoalReached() {
        Locale mLocale = new Locale("pt", "BR");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", mLocale);
        final String now = sdf.format(Calendar.getInstance().getTime());

        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.all_label_goal_reached)
                .setMessage(String.format("Tempo: %s", now))
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Database database = new Database(MainActivity.this);
                        database.register(labelCurrentTime.getText().toString(), now);
                        stopSound();
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    private void playSound() {
        stopSound();
        int soundId = preferences.getInt(DAIMOKU_GOAL_SOUND, DEFAULT_DAIMOKU_GOAL_SOUND);

        mediaPlayer = MediaPlayer.create(this, soundId);
        mediaPlayer.start();
    }

    private void stopSound() {
        if (mediaPlayer != null)
            mediaPlayer.stop();
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
        final int previousSoundId = preferences.getInt(DAIMOKU_GOAL_SOUND, DEFAULT_DAIMOKU_GOAL_SOUND);
        final boolean[] confirmed = {false};

        new AlertDialog.Builder(this)
                .setTitle("Escolha uma melodia")
                .setSingleChoiceItems(R.array.array_sounds, Sounds.getPosition(previousSoundId), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        playSound();
                        updateRing(Sounds.get(which));
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
                        stopSound();
                        if (!confirmed[0]) {
                            updateRing(previousSoundId);
                        }
                    }
                })
                .create().show();
    }

    private void updateRing(int id) {
        preferences.edit()
                .putInt(DAIMOKU_GOAL_SOUND, id)
                .apply();
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null)
            mediaPlayer.release();
        super.onDestroy();
    }
}
