package com.baltazarstudio.kossen.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import com.baltazarstudio.kossen.R;
import com.baltazarstudio.kossen.database.Database;
import com.baltazarstudio.kossen.model.Meta;
import com.baltazarstudio.kossen.ui.adapter.MetaAdapter;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Database database = new Database(this);
        List<Meta> metas = database.getAllGoals();


        TextView totalTime = findViewById(R.id.label_history_total_time);
        totalTime.setText(formatExtenseTimeTotal(metas));

        ListView listHistorico = findViewById(R.id.listview_history);
        listHistorico.setAdapter(new MetaAdapter(this, metas));


    }

    private String formatExtenseTimeTotal(List<Meta> metas) {
        int horas = 0;
        int minutos = 0;
        int segundos = 0;

        for (Meta meta : metas) {
            horas += Integer.parseInt(meta.getDuracao().substring(0, 2));
            minutos += Integer.parseInt(meta.getDuracao().substring(3, 5));
            segundos += Integer.parseInt(meta.getDuracao().substring(6));
        }

        String time = "";

        if (segundos != 0) {
            time = ", " + segundos + " segundos";
        }

        if (minutos != 0) {
            time = ", " + minutos + " minutos" + time;
        }

        if (horas != 0) {
            time = horas + " horas" + time;
        }

        if ((horas == 0 && minutos == 0 && segundos != 0)
                || (horas == 0 && minutos != 0 && segundos == 0)) {
            time = time.replace(",", "").trim();
        } else if (horas != 0 && minutos == 0 && segundos == 0) {
            // FAÇA NADA
        } else if (horas == 0 && minutos != 0 && segundos != 0) {
            time = time.replaceFirst(",", "");
            time = time.replace(",", " e").trim();
        } else if (horas != 0 && minutos == 0 && segundos != 0) {
            time = time.replace(",", " e");
        } else if (horas != 0 && minutos != 0 && segundos == 0) {
            time = time.replace(",", " e");
        } else if (horas != 0 && minutos != 0 && segundos != 0) {
            time = time.replace(",", " e");
            time = time.replace("horas e", "horas,");
        } else {
            time = "Não há horas registradas";
        }

        return time;
    }

}
