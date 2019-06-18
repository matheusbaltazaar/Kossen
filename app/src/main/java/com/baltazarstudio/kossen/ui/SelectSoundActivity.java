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
import com.baltazarstudio.kossen.context.AppContext;
import com.baltazarstudio.kossen.ui.adapter.ListSoundAdapter;

public class SelectSoundActivity extends AppCompatActivity {

    private int previousSelectedSound;
    private SharedPreferences preferences;
    private MenuItem menuItemOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_sound);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = getSharedPreferences(AppContext.PREFS, MODE_PRIVATE);
        previousSelectedSound = preferences.getInt(AppContext.DAIMOKU_GOAL_SOUND, 0);

        final ListView listSoundsDefault = findViewById(R.id.listview_sounds);
        listSoundsDefault.setAdapter(new ListSoundAdapter(this));
        listSoundsDefault.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateRing(id);
                ((BaseAdapter) parent.getAdapter()).notifyDataSetChanged();
            }
        });
        listSoundsDefault.setDivider(null);


    }

    private void updateRing(long id) {
        preferences.edit()
                .putInt(AppContext.DAIMOKU_GOAL_SOUND, (int) id)
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
                .putInt(AppContext.DAIMOKU_GOAL_SOUND, previousSelectedSound)
                .apply();
        super.onBackPressed();
    }
}
