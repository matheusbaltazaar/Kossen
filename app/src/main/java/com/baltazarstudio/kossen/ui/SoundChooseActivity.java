package com.baltazarstudio.kossen.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.baltazarstudio.kossen.R;
import com.baltazarstudio.kossen.adapter.ListSoundAdapter;

public class SoundChooseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_choose);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ListView listSoundsDefault = findViewById(R.id.listview_sounds_default);
        listSoundsDefault.setAdapter(new ListSoundAdapter(this, ListSoundAdapter.DEFAULT));
        listSoundsDefault.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((ListSoundAdapter) parent.getAdapter()).updateTargetRing(position);
            }
        });
        listSoundsDefault.setDivider(null);

        ListView listSoundsMelodies = findViewById(R.id.listview_sounds_melodies);
        listSoundsMelodies.setAdapter(new ListSoundAdapter(this, ListSoundAdapter.MELODIES));
        listSoundsMelodies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((ListSoundAdapter) parent.getAdapter()).updateTargetRing(position);
            }
        });
        listSoundsMelodies.setDivider(null);

    }


    @Override
    public void onBackPressed() {
        Toast.makeText(this, "FINALIZADO", Toast.LENGTH_LONG).show();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
