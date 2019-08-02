package com.baltazarstudio.kossen.ui;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.baltazarstudio.kossen.R;
import com.baltazarstudio.kossen.database.Database;
import com.baltazarstudio.kossen.model.Daimoku;
import com.baltazarstudio.kossen.ui.adapter.MetaAdapter;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Database database = new Database(this);
        List<Daimoku> daimokuList = database.getAllDaimoku();


        TextView totalTime = findViewById(R.id.history_total_time);
        totalTime.setText(getTotalTimeFormatted(daimokuList));

        ListView listHistorico = findViewById(R.id.listview_history);
        listHistorico.setAdapter(new MetaAdapter(this, daimokuList));
        listHistorico.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Daimoku daimoku = (Daimoku) adapterView.getAdapter().getItem(i);
                createDialogDaimoku(daimoku);
            }
        });


    }

    private void createDialogDaimoku(Daimoku daimoku) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.daimoku_details, null);

        dialogView.findViewById(R.id.ll_layout_when).setVisibility(View.VISIBLE);
        TextView tvWhen = dialogView.findViewById(R.id.tv_alert_when);
        tvWhen.setText(daimoku.getData());

        TextView tvTime = dialogView.findViewById(R.id.tv_alert_time);
        tvTime.setText(daimoku.getDuracao());

        EditText etInfo = dialogView.findViewById(R.id.et_daimuku_info);
        etInfo.setText(daimoku.getInformacao());
        etInfo.setFocusable(false);


        new AlertDialog.Builder(this)
                .setView(dialogView)
                .create().show();
    }

    private String getTotalTimeFormatted(List<Daimoku> daimokuList) {
        int horas = 0;
        int minutos = 0;
        int segundos = 0;

        for (Daimoku daimoku : daimokuList) {
            // PADRÃO DURAÇÃO >> ##:##:##

            segundos += Integer.parseInt(daimoku.getDuracao().substring(6)); // Segundos;
            if (segundos > 59) {
                minutos++;
                segundos = 60 - segundos;
            }

            minutos += Integer.parseInt(daimoku.getDuracao().substring(3, 5)); // Minutos
            if (minutos > 59) {
                horas++;
                minutos = 60 - minutos;
            }

            horas += Integer.parseInt(daimoku.getDuracao().substring(0, 2)); // Horas

        }

        return horas + " horas, "
                + minutos + " minutos e "
                + segundos + " segundos";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
