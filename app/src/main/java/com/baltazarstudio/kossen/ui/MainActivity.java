package com.baltazarstudio.kossen.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.baltazarstudio.kossen.R;
import com.baltazarstudio.kossen.component.Chronometer;
import com.baltazarstudio.kossen.component.TimeInputText;
import com.baltazarstudio.kossen.util.AnimationUtil;
import com.baltazarstudio.kossen.util.Sounds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikhaellopez.circularimageview.CircularImageView;

public class MainActivity extends AppCompatActivity {


    /**
     * - (PARTIALLY DONE) Novo layout Contador (link no Slack)
     * - Ícone com foto no canto esquerdo da toolbar (no drawer icon)
     * - Tela de Apresentação
     * - Capturar Foto de perfil Telefone (Crop imagem)
     * <p>
     * - Refatoração: Código (Revisar todas as classes)
     * - Migração projeto para Kotlin
     */


    private TextView labelGoalTime;
    private TextView tvCurrentTime;
    private TimeInputText inputTime;

    private FloatingActionButton buttonResumePause;
    private FloatingActionButton buttonRestore;


    // Media Player
    private MediaPlayer mMediaPlayer;

    private Chronometer mChronometer;
    private ImageView menuMore;
    private CircularImageView userProfileImage;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadProfilePhoto();
        setUpMenu();
        setUpChronometer();
        setUpCircularProgress();
        setUpHistorico();

        startIntroAnimations();

    }

    private void loadProfilePhoto() {
        userProfileImage = findViewById(R.id.iv_user_profile_image);

        // TODO
    }

    private void setUpCircularProgress() {
        mProgress = findViewById(R.id.progress_circular_contagem_tempo);
        mProgress.setMax(100);
    }

    private void setUpMenu() {
        menuMore = findViewById(R.id.button_menu);
        menuMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, v, Gravity.END);
                popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        if (id == R.id.action_history) {
                            startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                        } else if (id == R.id.action_sound) {
                            Sounds.showSoundDialog(MainActivity.this);
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });


    }

    private void setUpChronometer() {
        mChronometer = new Chronometer();
        mChronometer.setTimeListener(new Chronometer.TimeListener() {
            @Override
            public void onTick() {
                tvCurrentTime.setText(mChronometer.getCurrentTimeFormatted());
                if (mChronometer.hasGoal())
                    mProgress.setProgress(calcularProgresso());
            }

            @Override
            public void onTargetReached() {
                toggleUIComponents(true);
                mChronometer.stop();
                buttonResumePause.setImageResource(R.drawable.ic_play);
                buttonRestore.setVisibility(View.VISIBLE);
                AnimationUtil.leftToRight(getBaseContext(), buttonRestore);
                playSound();
                createDialogGoalReached();
            }
        });

        labelGoalTime = findViewById(R.id.tv_goal_time);
        tvCurrentTime = findViewById(R.id.tv_current_time);
        inputTime = findViewById(R.id.et_time_input);


        buttonResumePause = findViewById(R.id.button_resume_pause);
        buttonResumePause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!mChronometer.hasStarted()) {
                    labelGoalTime.setText(inputTime.getText());
                    mChronometer.setGoal(inputTime.getText().toString());

                    if (!mChronometer.hasGoal()) {
                        fillProgressAnimated();
                    }
                }

                if (mChronometer.isRunning()) {
                    toggleUIComponents(true);
                    buttonResumePause.setImageResource(R.drawable.ic_play);
                    buttonRestore.setVisibility(View.VISIBLE);
                    mChronometer.stop();

                    AnimationUtil.leftToRight(v.getContext(), buttonRestore);
                } else {
                    toggleUIComponents(false);
                    buttonResumePause.setImageResource(R.drawable.ic_pause);
                    buttonRestore.setVisibility(View.GONE);

                    if (mChronometer.hasStarted())
                        AnimationUtil.leftToRight(getBaseContext(), buttonResumePause);

                    mChronometer.resume();
                }
            }
        });

        buttonRestore = findViewById(R.id.button_restaurar);
        buttonRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restoreClock();
            }
        });
    }

    private void setUpHistorico() {
        TextView tvHistorico = findViewById(R.id.tv_historico_sem_historico_daimoku);
        RecyclerView rvHistorico = findViewById(R.id.rv_historico);

        if (/*Se houver histórico*/false) {
            tvHistorico.setVisibility(View.GONE);
            rvHistorico.setVisibility(View.VISIBLE);
        } else {
            tvHistorico.setVisibility(View.VISIBLE);
            rvHistorico.setVisibility(View.GONE);
        }

        // TODO Capturar Histórico
    }

    @SuppressLint("SetTextI18n")
    private void restoreClock() {
        mChronometer.reset();
        mProgress.setProgress(0);
        tvCurrentTime.setText("00:00:00");
        labelGoalTime.setText("00:00:00");
        inputTime.setText("00:00:00");

        buttonResumePause.setImageResource(R.drawable.ic_play);
        AnimationUtil.leftToRight(getBaseContext(), buttonResumePause);

        buttonRestore.setVisibility(View.GONE);
        toggleUIComponents(true);
    }

    private void toggleUIComponents(boolean enabled) {
        userProfileImage.setEnabled(enabled);
        menuMore.setEnabled(enabled);

        if (!mChronometer.hasStarted()) {
            if (enabled) {
                tvCurrentTime.setVisibility(View.GONE);
                inputTime.setVisibility(View.VISIBLE);
            } else {
                tvCurrentTime.setVisibility(View.VISIBLE);
                inputTime.setVisibility(View.GONE);
            }
        }

        // TODO Desabilitar click do histórico

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

/*
    @SuppressLint("InflateParams")
    private void createDialogRegisterDaimoku() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_register, null);
        final String time = mChronometer.getCurrentTimeFormatted();

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
                        Locale mLocale = new Locale("pt", "BR");
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", mLocale);
                        final String now = sdf.format(Calendar.getInstance().getTime());

                        Database database = new Database(getBaseContext());

                        Daimoku daimoku = new Daimoku();
                        daimoku.setData(now);
                        daimoku.setDuracao(time);
                        daimoku.setInformacao(etInfo.getText().toString());

                        database.register(daimoku);
                        Toast.makeText(getBaseContext(), R.string.all_alert_time_registered, Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }
*/
    private void playSound() {
        stopSound();
        mMediaPlayer = MediaPlayer.create(this, Sounds.getSelectedSound(this));
        mMediaPlayer.start();
    }

    private void stopSound() {
        if (mMediaPlayer != null)
            mMediaPlayer.stop();
    }

    private void startIntroAnimations() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                AnimationUtil.leftToRight(getBaseContext(), findViewById(R.id.tv_historico_title));
                AnimationUtil.fadeIn(getBaseContext(), mProgress);
            }
        });
    }

    private int calcularProgresso() {
        int tempoTotalEmSegundos = mChronometer.getGoalTimeInSeconds();
        int segundosAtual = mChronometer.getCurrentTimeInSeconds();

        // REGRA DE 3
        return (segundosAtual * 100) / tempoTotalEmSegundos;
    }

    private void fillProgressAnimated() {
        if (mProgress.getMax() == mProgress.getProgress())
            return;

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mProgress.incrementProgressBy(1);
                fillProgressAnimated();
            }
        });

    }

    @Override
    protected void onDestroy() {
        if (mMediaPlayer != null)
            mMediaPlayer.release();
        super.onDestroy();
    }
}
