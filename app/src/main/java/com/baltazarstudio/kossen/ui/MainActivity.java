package com.baltazarstudio.kossen.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baltazarstudio.kossen.R;
import com.baltazarstudio.kossen.component.AnimationBehavior;
import com.baltazarstudio.kossen.component.Chronometer;
import com.baltazarstudio.kossen.database.Database;
import com.baltazarstudio.kossen.model.Daimoku;
import com.baltazarstudio.kossen.model.Sounds;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final String DAIMOKU_GOAL_SOUND = "goal_sound";
    private final int DEFAULT_DAIMOKU_GOAL_SOUND = R.raw.camila_cabello_havana;

    private TextView labelGoalTime;
    private TextView labelCurrentTime;

    private Button buttonSave;
    private Button buttonStartStop;


    // Media Player
    private MediaPlayer mMediaPlayer;
    private SharedPreferences preferences;

    private Chronometer mChronometer;
    private Menu actionMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("prefs", MODE_PRIVATE);
        setUpChronometer();
        setUpView();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.actionMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_history) {
            startActivity(new Intent(this, HistoryActivity.class));
        } else if (id == R.id.action_sound) {
            createDialogSelectSound();
        } else if (id == R.id.action_reset) {
            if (mChronometer.hasStarted()) {
                createResetAlert();
            } else {
                restoreClock();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpView() {
        labelGoalTime = findViewById(R.id.tv_goal_time);

        Button buttonIncreaseOneHour = findViewById(R.id.button_increase_time_one_hour);
        buttonIncreaseOneHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChronometer.addMinutes(60);
                labelGoalTime.setText(mChronometer.getGoalTime());
            }
        });

        Button buttonIncreaseThirtyMinutes = findViewById(R.id.button_increase_time_thirty_minutes);
        buttonIncreaseThirtyMinutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChronometer.addMinutes(30);
                labelGoalTime.setText(mChronometer.getGoalTime());
            }
        });

        Button buttonIncreaseFifteenMinutes = findViewById(R.id.button_increase_time_fifteen_minutes);
        buttonIncreaseFifteenMinutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChronometer.addMinutes(15);
                labelGoalTime.setText(mChronometer.getGoalTime());
            }
        });

        Button buttonIncreaseFiveMinutes = findViewById(R.id.button_increase_time_five_minutes);
        buttonIncreaseFiveMinutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChronometer.addMinutes(5);
                labelGoalTime.setText(mChronometer.getGoalTime());
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

        buttonSave = findViewById(R.id.button_save_current_time);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.all_dialog_title_confirm_register)
                        .setMessage(R.string.all_dialog_message_confirm_register)
                        .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mChronometer.stop();
                                toggleUIComponents(true);
                                buttonStartStop.setEnabled(false);
                                createDialogRegister();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("NÃO", null)
                        .create().show();
            }
        });

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
                mChronometer.stop();
                buttonStartStop.setText(R.string.all_button_resume);
                toggleUIComponents(true);
                playSound();
                createDialogGoalReached();
            }
        });
    }

    private void restoreClock() {
        if (mChronometer.hasStarted()) {
            mChronometer.reset();
            labelCurrentTime.setText(mChronometer.getCurrentTime());
            labelGoalTime.setText(mChronometer.getGoalTime());
            buttonStartStop.setText(R.string.all_button_start);
            buttonStartStop.setEnabled(true);
            AnimationBehavior.hideFadeOut(buttonSave);
            Snackbar.make(buttonStartStop, R.string.all_alert_clock_reseted, Snackbar.LENGTH_LONG).show();
        }
    }

    private void toggleUIComponents(boolean enabled) {
        if (!enabled) {
            AnimationBehavior.hideFadeOut(findViewById(R.id.layout_clock_functions_buttons));

            if (!mChronometer.hasTarget()) {
                AnimationBehavior.hideFadeOut(buttonSave);
            }
        } else {
            AnimationBehavior.showFadeIn(findViewById(R.id.layout_clock_functions_buttons));
            AnimationBehavior.showFadeIn(buttonSave);
        }

        for (int i = 0; i < actionMenu.size(); i++) {
            actionMenu.getItem(i).setEnabled(enabled);
        }
    }

    private void createDialogGoalReached() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.all_label_goal_reached)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopSound();
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    @SuppressLint("InflateParams")
    private void createDialogRegister() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_register, null);
        final String time = mChronometer.getCurrentTime();

        TextView tvTime = dialogView.findViewById(R.id.tv_alert_time);
        tvTime.setText(time);

        final EditText etInfo = dialogView.findViewById(R.id.et_daimuku_info);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        dialogView.findViewById(R.id.button_register)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        register(time, etInfo.getText().toString());
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    private void register(String currentTime, String info) {
        Locale mLocale = new Locale("pt", "BR");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", mLocale);
        final String now = sdf.format(Calendar.getInstance().getTime());

        Database database = new Database(MainActivity.this);

        Daimoku daimoku = new Daimoku();
        daimoku.setData(now);
        daimoku.setDuracao(currentTime);
        daimoku.setInformacao(info);

        database.register(daimoku);
        Toast.makeText(this, R.string.all_alert_time_registered, Toast.LENGTH_LONG).show();
    }

    private void playSound() {
        stopSound();
        int soundId = preferences.getInt(DAIMOKU_GOAL_SOUND, DEFAULT_DAIMOKU_GOAL_SOUND);

        mMediaPlayer = MediaPlayer.create(this, soundId);
        mMediaPlayer.start();
    }

    private void stopSound() {
        if (mMediaPlayer != null)
            mMediaPlayer.stop();
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
        if (mMediaPlayer != null)
            mMediaPlayer.release();
        super.onDestroy();
    }
}
