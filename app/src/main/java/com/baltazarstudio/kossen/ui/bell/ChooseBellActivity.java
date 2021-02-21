package com.baltazarstudio.kossen.ui.bell;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baltazarstudio.kossen.R;
import com.baltazarstudio.kossen.context.PlayerContext;
import com.baltazarstudio.kossen.ui.adapter.ChooseBellAdapter;

public class ChooseBellActivity extends AppCompatActivity {

    private ChooseBellAdapter mAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_bell);

        ImageView buttonVoltar = findViewById(R.id.button_bell_action_back);
        buttonVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView buttonConfirmar = findViewById(R.id.button_bell_confirm);
        buttonConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerContext.changeBell(ChooseBellActivity.this, mAdapter.getSelectedBell());
            }
        });

        mAdapter = new ChooseBellAdapter(this);
        RecyclerView rvChooseBell = findViewById(R.id.rv_choose_bell);
        rvChooseBell.setLayoutManager(new LinearLayoutManager(this));
        rvChooseBell.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PlayerContext.stopPlayer();
    }
}
