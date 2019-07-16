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

    private Database database;

    // Media Player
    private MediaPlayer mediaPlayer;
    private SharedPreferences preferences;

    private Chronometer mChronometer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("prefs", MODE_PRIVATE);

        buttonStartStop = findViewById(R.id.bt_start_stop_time);
        buttonStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mChronometer.isRunning()) {
                    mChronometer.resume();
                    buttonStartStop.setText(R.string.all_button_stop);
                } else {
                    mChronometer.stop();
                    buttonStartStop.setText(R.string.all_button_start);
                    toggleUIComponents(true);
                }
            }
        });


        labelCurrentTime = findViewById(R.id.tv_current_time);
        labelGoalTime = findViewById(R.id.tv_goal_time);

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
                if (mChronometer.isRunning())
                    createResetAlert();
                else
                    restoreClock();
            }
        });

        setUpChronometer();

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


    private void setUpChronometer() {
        mChronometer = new Chronometer();
        mChronometer.setChronometerListener(new Chronometer.ChronometerListener() {
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

    private void increase(int minutes) {
//        totalGoalSeconds += minutes * 60;
        // TODO
        labelGoalTime.setText(mChronometer.getGoalTime());

        toggleUIComponents(true);
    }

    private void restoreClock() {
        mChronometer.reset();
        labelCurrentTime.setText(mChronometer.getCurrentTime());
        labelGoalTime.setText(mChronometer.getGoalTime());
        buttonStartStop.setText(R.string.all_button_start);
        toggleUIComponents(false);
    }

    private void toggleUIComponents(boolean enabled) {
        buttonReset.setEnabled(enabled);
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
                                labelCurrentTime.getText().toString(),
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
