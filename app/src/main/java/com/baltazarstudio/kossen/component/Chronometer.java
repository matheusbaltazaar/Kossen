package com.baltazarstudio.kossen.component;

import android.os.Handler;

public class Chronometer {
    public interface ChronometerListener {

        void onTick();

        void onTargetReached();

    }

    private ChronometerListener mListener;


    private boolean isRunning;

    private Handler handler;
    private Runnable runnable;
    private int totalSeconds = 0;
    private int totalGoalSeconds = 0;

    public Chronometer() {
        handler = new Handler();
    }

    public String getCurrentTime() {
        return format(totalSeconds);
    }

    public String getGoalTime() {
        return format(totalGoalSeconds);
    }

    private String format(int totalSeconds) {
        String clock;
        if (totalSeconds < 60) {
            clock = "00:00:"
                    + (totalSeconds < 10 ? "0" + totalSeconds : totalSeconds);
        } else if (totalSeconds < 3600) {
            int minutos = totalSeconds / 60;
            int seconds = totalSeconds % 60;
            clock = "00:"
                    + (minutos < 10 ? "0" + minutos : minutos)
                    + ":"
                    + (seconds < 10 ? "0" + seconds : seconds);
        } else {

            int horas = totalSeconds / 3600; // 60 * 60
            int minutos = (totalSeconds % 3600);
            int seconds = minutos % 60;
            minutos /= 60;


            clock = (horas < 10 ? "0" + horas : horas)
                    + ":"
                    + (minutos < 10 ? "0" + minutos : minutos)
                    + ":"
                    + (seconds < 10 ? "0" + seconds : seconds);
        }

        return clock;
    }

    public void resume() {
        handler.postDelayed(getRunnable(), 1000);
        isRunning = true;
    }

    public void stop() {
        handler.removeCallbacks(runnable);
        isRunning = false;
    }

    public void reset() {

    }

    private Runnable getRunnable() {
        if (runnable == null)
            runnable = new Runnable() {
                @Override
                public void run() {
                    if (mListener != null)
                        mListener.onTick();
                    // EVERY TIME TOTAL SECONDS IS INCREMENTED
                    // THE GOAL TIME IS VERIFIED
                    if (isTargetReached()) {
                        if (mListener != null)
                            mListener.onTargetReached();
                        isRunning = false;
                    } else {
                        resume();
                    }
                }
            };
        return runnable;
    }

    public boolean isRunning() {
        return isRunning;
    }

    private boolean isTargetReached() {
        // TODO("Not implemented")
        return false;
    }


    public void setChronometerListener(ChronometerListener listener) {
        this.mListener = listener;
    }
}



