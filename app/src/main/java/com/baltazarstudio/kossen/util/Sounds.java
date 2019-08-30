package com.baltazarstudio.kossen.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AlertDialog;

import com.baltazarstudio.kossen.R;

import java.util.HashMap;
import java.util.Map;

public class Sounds {

    private static final Map<String, Integer> sounds = init();
    private static final String DAIMOKU_GOAL_SOUND = "goal_sound";
    private static final int DEFAULT_DAIMOKU_GOAL_SOUND = R.raw.camila_cabello_havana;
    private static MediaPlayer mp;

    private static Map<String, Integer> init() {
        Map<String, Integer> sounds = new HashMap<>();
        sounds.put("Camila Cabello Havana", R.raw.camila_cabello_havana);
        sounds.put("Magical Morning", R.raw.magical_morning);
        sounds.put("Light Ringtone", R.raw.light_ringtone);
        return sounds;
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }

    private static void updateRing(Context context, int musica) {
        getPrefs(context).edit()
                .putInt(DAIMOKU_GOAL_SOUND, musica)
                .apply();
    }

    public static int getSelectedSound(Context context) {
        return getPrefs(context).getInt(DAIMOKU_GOAL_SOUND, DEFAULT_DAIMOKU_GOAL_SOUND);
    }

    @SuppressLint("InflateParams")
    public static void showSoundDialog(final Context context) {

        final int previousSoundId = getSelectedSound(context);
        final boolean[] confirmed = {false};

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_select_sound, null);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Escolha uma melodia")
                .setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmed[0] = true;
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (mp != null) {
                            mp.stop();
                            mp = null;
                        }
                        if (!confirmed[0]) {
                            updateRing(context, previousSoundId);
                        }
                    }
                })
                .create();

        RadioGroup group = dialogView.findViewById(R.id.radiogroup_sound);

        for (final String musica : sounds.keySet()) {
            RadioButton radioButton = new RadioButton(context);
            radioButton.setLayoutParams(group.getLayoutParams());
            radioButton.setPadding(4, 12, 4, 12);
            radioButton.setText(musica);
            radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
            group.addView(radioButton);

            radioButton.setChecked(sounds.get(musica) == getSelectedSound(context));
            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (checked) {
                        if (mp != null) {
                            mp.stop();
                        }
                        mp = MediaPlayer.create(context, sounds.get(musica));
                        mp.start();
                        updateRing(context, sounds.get(musica));
                    }
                }
            });

        }
        dialog.show();
    }
}
