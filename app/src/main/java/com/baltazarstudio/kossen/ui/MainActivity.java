package com.baltazarstudio.kossen.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baltazarstudio.kossen.R;
import com.baltazarstudio.kossen.component.Chronometer;
import com.baltazarstudio.kossen.component.TimeInputText;
import com.baltazarstudio.kossen.context.AppContext;
import com.baltazarstudio.kossen.context.PlayerContext;
import com.baltazarstudio.kossen.database.Database;
import com.baltazarstudio.kossen.model.Daimoku;
import com.baltazarstudio.kossen.ui.adapter.HistoricoDaimokuAdapter;
import com.baltazarstudio.kossen.ui.bell.ChooseBellActivity;
import com.baltazarstudio.kossen.util.AnimationUtil;
import com.baltazarstudio.kossen.util.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class MainActivity extends AppCompatActivity {


    /**
     * - (DONE) Novo layout Contador (link no Slack)
     * - (DONE) Dados de perfil (Capturar foto, crop imagem etc)
     * - (DONE) Ícone com foto no canto esquerdo da toolbar (no drawer icon)
     * - (DONE) Tela de Apresentação
     * - (DONE) Ajustar sons e mensagens para quando concluir a oração
     * - (DONE) Fonte personalizada (Calligraphy3 ?)
     * - Tela para detalhes e filtragens do histórico daimoku
     * <p>
     * - Refatoração: Código (Revisar todas as classes)
     * - Migração projeto para Kotlin
     */


    private TextView labelGoalTime;
    private TextView tvCurrentTime;
    private TimeInputText inputTime;

    private FloatingActionButton buttonResumePause;
    private FloatingActionButton buttonRestore;
    private Button buttonSalvar;


    private Chronometer mChronometer;
    private ImageView menuMore;
    private CircularImageView userProfileImage;
    private ProgressBar mProgress;
    private Database database;
    private TextView tvMessagemPraticante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = new Database(this);
        AppContext.setPerfil(database.carregarPerfil());

        setUpProfilePhoto();
        setUpMenu();
        setUpMessage();
        setUpChronometer();
        setUpCircularProgress();
        setUpHistorico();

        startIntroAnimations();

         checkFirstUse();
     }

     @SuppressLint("SetTextI18n")
     private void setUpMessage() {
         tvMessagemPraticante = findViewById(R.id.tv_messagem_praticante);

         String nomePraticante = AppContext.getPerfil().getNome() != null && !AppContext.getPerfil().getNome().isEmpty() ?
                 AppContext.getPerfil().getNome() : "praticante";

         tvMessagemPraticante.setText("Olá, " + nomePraticante + "! Vamos fazer daimoku?");
     }

     private void setUpProfilePhoto() {
         userProfileImage = findViewById(R.id.iv_icon_user_profile);

         userProfileImage.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 startActivityForResult(new Intent(MainActivity.this, ProfileActivity.class), 0);
             }
         });

         loadPhoto();
     }

     private void loadPhoto() {
         String foto = AppContext.getPerfil().getArquivoFotoBase64();

         if (foto != null && !foto.isEmpty()) {
             userProfileImage.setImageBitmap(Utils.getBitmapFromBase64(foto));
         } else {
             userProfileImage.setImageResource(R.drawable.ic_user_profile);
         }
     }

    private void setUpMenu() {
         menuMore = findViewById(R.id.button_main_menu);
         menuMore.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 PopupMenu popup = new PopupMenu(MainActivity.this, v, Gravity.END);
                 popup.getMenu().add(Menu.NONE, 0, Menu.NONE, "Mudar toque do sino");
                 popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                     @Override
                     public boolean onMenuItemClick(MenuItem item) {
                         if (item.getItemId() == 0) {//Sounds.showSoundDialog(MainActivity.this);
                             startActivity(new Intent(MainActivity.this, ChooseBellActivity.class));
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
                AnimationUtil.fadeIn(getBaseContext(), buttonRestore);
                buttonSalvar.setVisibility(View.VISIBLE);
                AnimationUtil.fadeIn(getBaseContext(), buttonSalvar);
                //playSound();
                //createDialogGoalReached();
                tvMessagemPraticante.setText("Vitória! Quer continuar, " + AppContext.getPerfil().getNome() + "?");
                PlayerContext.playBell(MainActivity.this);
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

                if (mChronometer.isRunning()) { // AÇÃO PAUSAR
                    toggleUIComponents(true);
                    buttonResumePause.setImageResource(R.drawable.ic_play);
                    buttonRestore.setVisibility(View.VISIBLE);
                    mChronometer.stop();

                    AnimationUtil.fadeIn(v.getContext(), buttonRestore);

                    AnimationUtil.fadeIn(getBaseContext(), buttonSalvar);
                    buttonSalvar.setVisibility(View.VISIBLE);

                } else { // AÇÃO COMEÇAR | CONTINUAR
                    toggleUIComponents(false);
                    buttonResumePause.setImageResource(R.drawable.ic_pause);
                    buttonSalvar.setVisibility(View.GONE);
                    buttonRestore.setVisibility(View.GONE);

                    if (mChronometer.hasStarted()) {
                        AnimationUtil.leftToRight(getBaseContext(), buttonResumePause);
                        AnimationUtil.fadeOut(getBaseContext(), buttonSalvar);
                    }

                    tvMessagemPraticante.setText("Nam-myoho-renge-kyo...");

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

        buttonSalvar = findViewById(R.id.button_salvar_daimoku);
        buttonSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegistrarDaimokuDialog dialog
                        = new RegistrarDaimokuDialog(MainActivity.this, mChronometer.getCurrentTimeFormatted());
                dialog.setOnSavedDaimokuListener(new RegistrarDaimokuDialog.OnSavedDaimokuListener() {
                    @Override
                    public void onSave() {
                        setUpHistorico();
                        buttonResumePause.setEnabled(false);
                        buttonSalvar.setEnabled(false);
                    }
                });
                dialog.show();
            }
        });
     }

     private void setUpCircularProgress() {
         mProgress = findViewById(R.id.progress_circular_contagem_tempo);
         mProgress.setMax(100);
     }

     private void setUpHistorico() {
         // CASO FOR USAR UM DIA, TRABALHO EM PARTE RESOLVIDO
        /*private String calculateTotalTime(List<Daimoku> daimokuList) {
            int horas = 0;
            int minutos = 0;
            int segundos = 0;

            for (Daimoku daimoku : daimokuList) {
                // PADRÃO DURAÇÃO >> ##:##:##

                segundos += Integer.parseInt(daimoku.getDuracao().substring(6)); // Segundos;
                if (segundos > 59) {
                    minutos++;
                    segundos -= 60;
                }

                minutos += Integer.parseInt(daimoku.getDuracao().substring(3, 5)); // Minutos
                if (minutos > 59) {
                    horas++;
                    minutos -= 60;
                }

                horas += Integer.parseInt(daimoku.getDuracao().substring(0, 2)); // Horas

            }

            return horas + " horas, "
                    + minutos + " minutos e "
                    + segundos + " segundos";
        }*/


         TextView tvSemHistorico = findViewById(R.id.tv_historico_sem_historico_daimoku);
         RecyclerView rvHistorico = findViewById(R.id.rv_historico);

         List<Daimoku> listaDaimoku = this.database.getTodosDaimoku();

         if (listaDaimoku.size() > 0) {
             tvSemHistorico.setVisibility(View.GONE);
             rvHistorico.setVisibility(View.VISIBLE);

             rvHistorico.setLayoutManager(new LinearLayoutManager(this));
             rvHistorico.setAdapter(new HistoricoDaimokuAdapter(this, listaDaimoku, rvHistorico.getLayoutManager()));
             //rvHistorico.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

         } else {
             tvSemHistorico.setVisibility(View.VISIBLE);
             rvHistorico.setVisibility(View.GONE);
         }

     }

     @SuppressLint("SetTextI18n")
    private void restoreClock() {
        mChronometer.reset();
        mProgress.setProgress(0);
         tvCurrentTime.setText("00:00:00");
         labelGoalTime.setText("00:00:00");
         inputTime.resetTime();

         buttonResumePause.setImageResource(R.drawable.ic_play);
         buttonResumePause.setEnabled(true);
         buttonSalvar.setEnabled(true);
         AnimationUtil.leftToRight(getBaseContext(), buttonResumePause);

         buttonRestore.setVisibility(View.GONE);

         buttonSalvar.setVisibility(View.GONE);
         AnimationUtil.leftToRight(getBaseContext(), buttonResumePause);

         toggleUIComponents(true);
         setUpMessage();
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

        findViewById(R.id.rv_historico).setEnabled(enabled);
     }

    private void startIntroAnimations() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                AnimationUtil.leftToRight(getBaseContext(), findViewById(R.id.tv_historico_title));
                AnimationUtil.fadeInSlow(getBaseContext(), mProgress);
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

     private void checkFirstUse() {
         if (AppContext.isFirstUse(this)) {
             startActivityForResult(new Intent(this, ProfileActivity.class), 0);
         }
     }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         loadPhoto();
         setUpMessage();

         if (AppContext.isFirstUse(this)) {
             AppContext.assertFirstUse(this);
             startIntroAnimations();
         }
     }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
 }
