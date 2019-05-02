package com.baltazarstudio.kossen.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.baltazarstudio.kossen.R;
import com.baltazarstudio.kossen.adapter.ListSoundAdapter;
import com.baltazarstudio.kossen.context.AppContext;

public class SelectSoundActivity extends AppCompatActivity {


    private BaseAdapter defaultAdapter;
    private BaseAdapter melodiesAdapter;
    private int previousSelectedSound;
    private SharedPreferences preferences;
    private MenuItem menuItemOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_sound);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = getSharedPreferences(AppContext.PREFS, MODE_PRIVATE);
        previousSelectedSound = preferences.getInt(AppContext.TARGET_SOUND, 0);

        defaultAdapter = new ListSoundAdapter(this, ListSoundAdapter.DEFAULT);
        melodiesAdapter = new ListSoundAdapter(this, ListSoundAdapter.MELODIES);

        final AdapterView.OnItemClickListener updateRingAdapterListener
                = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateRing(id);
                defaultAdapter.notifyDataSetChanged();
                melodiesAdapter.notifyDataSetChanged();
            }
        };


        final ListView listSoundsDefault = findViewById(R.id.listview_sounds_default);
        listSoundsDefault.setAdapter(defaultAdapter);
        listSoundsDefault.setOnItemClickListener(updateRingAdapterListener);
        listSoundsDefault.setDivider(null);

        final ListView listSoundsMelodies = findViewById(R.id.listview_sounds_melodies);
        listSoundsMelodies.setAdapter(melodiesAdapter);
        listSoundsMelodies.setOnItemClickListener(updateRingAdapterListener);
        listSoundsMelodies.setDivider(null);

    }

    private void updateRing(long id) {
        preferences.edit()
                .putInt(AppContext.TARGET_SOUND, (int) id)
                .apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuItemOK = menu.add("OK");
        menuItemOK.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item == menuItemOK) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        preferences.edit()
                .putInt(AppContext.TARGET_SOUND, previousSelectedSound)
                .apply();
        super.onBackPressed();
    }
}
