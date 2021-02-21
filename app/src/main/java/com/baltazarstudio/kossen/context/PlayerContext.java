package com.baltazarstudio.kossen.context;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.util.Log;

import com.baltazarstudio.kossen.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlayerContext {
    private static final String SOUND_PREFS = "prefs_sound";
    private static final String SOUND_PREFS_BELL = "prefs_sound_bell";

    private static SharedPreferences soundPrefs;
    private static MediaPlayer player;
    private static final HashMap<String, Integer> bellMap;

    static {
        bellMap = new HashMap<>();
        bellMap.put("Sino 1", R.raw.bell_1);
        bellMap.put("Sino 2", R.raw.bell_2);
        bellMap.put("Sino 3", R.raw.bell_3);
    }

    public static void playBell(Context context) {
        playBell(context, getCurrentBell(context), null);
    }

    public static void playBell(Context context, int rawId, final MediaPlayer.OnCompletionListener listener) {
        stopPlayer();
        player = MediaPlayer.create(context, rawId);

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            int timesPlayed = 0;

            @Override
            public void onCompletion(MediaPlayer mp) {
                timesPlayed++;

                if (timesPlayed < 3)
                    mp.start();
                else {
                    if (listener != null)
                        listener.onCompletion(mp);
                }
            }
        });

        player.start();
    }

    public static void changeBell(Context context, int rawId) {
        if (soundPrefs == null)
            soundPrefs = context.getSharedPreferences(SOUND_PREFS, Context.MODE_PRIVATE);

        soundPrefs.edit().putInt(SOUND_PREFS_BELL, rawId).apply();
    }

    public static void stopPlayer() {
        if (player != null) {
            player.stop();
            player.release();
        }
    }

    public static Map<String, Integer> getBellMap() {
        return bellMap;
    }

    public static int getCurrentBell(Context context) {
        if (soundPrefs == null)
            soundPrefs = context.getSharedPreferences(SOUND_PREFS, Context.MODE_PRIVATE);
        return soundPrefs.getInt(SOUND_PREFS_BELL, R.raw.bell_1); // Default -> R.raw.bell_1
    }
}
