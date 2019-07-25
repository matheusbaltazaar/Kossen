package com.baltazarstudio.kossen.component;

import android.os.Handler;

public class Chronometer {
    private TimeListener mListener;
    private boolean isRunning;
    private boolean hasStarted;
    private Handler handler;
    private Runnable runnable;

    private int currentTimeSeconds = 0;
    private int currentTimeMinutes = 0;
    private int currentTimeHours = 0;

    private int goalTimeSeconds = 0;
    private int goalTimeMinutes = 0;
    private int goalTimeHours = 0;

    public Chronometer() {
        handler = new Handler();
        isRunning = false;
        hasStarted = false;
    }

    public String getCurrentTime() {
        return format(currentTimeHours,
                currentTimeMinutes,
                currentTimeSeconds);
    }

    public String getGoalTime() {
        return format(goalTimeHours,
                goalTimeMinutes,
                goalTimeSeconds);
    }

    private String format(int horas, int minutos, int segundos) {
        return (horas < 10 ? "0" + horas : horas)
                + ":"
                + (minutos < 10 ? "0" + minutos : minutos)
                + ":"
                + (segundos < 10 ? "0" + segundos : segundos);
    }

    public void addMinutes(int minutes) {
        goalTimeMinutes += minutes;

        if (goalTimeMinutes > 59) {
            goalTimeHours++;
            goalTimeMinutes -= 60;
        }
    }

    public void resume() {
        handler.postDelayed(getRunnable(), 1000);
        isRunning = true;
        hasStarted = true;
    }

    public void stop() {
        handler.removeCallbacks(runnable);
        isRunning = false;
    }

    public void reset() {
        stop();
        currentTimeSeconds = 0;
        currentTimeMinutes = 0;
        currentTimeHours = 0;

        goalTimeSeconds = 0;
        goalTimeMinutes = 0;
        goalTimeHours = 0;

        hasStarted = false;
    }

    private Runnable getRunnable() {
        if (runnable == null) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    increase();
                    if (mListener != null)
                        mListener.onTick();

                    // EVERY TIME TOTAL SECONDS IS INCREMENTED
                    // THE GOAL TIME IS VERIFIED
                    if (isTargetReached()) {
                        if (mListener != null)
                            mListener.onTargetReached();
                        stop();
                        hasStarted = false;
                    } else {
                        resume();
                    }
                }
            };
        }
        return runnable;
    }

    private void increase() {
        currentTimeSeconds++;
        if (currentTimeSeconds == 60) {
            currentTimeMinutes++;
            currentTimeSeconds = 0;
        }

        if (currentTimeMinutes == 60) {
            currentTimeHours++;
            currentTimeMinutes = 0;
        }

    }

    public boolean isRunning() {
        return isRunning;
    }

    private boolean isTargetReached() {
        return (currentTimeSeconds == goalTimeSeconds
                && currentTimeMinutes == goalTimeMinutes
                && currentTimeHours == goalTimeHours);
    }

    public boolean hasStarted() {
        return hasStarted;
    }

    public boolean hasTarget() {
        return (goalTimeHours > 0
                && goalTimeMinutes > 0
                && goalTimeSeconds > 0);
    }

    public void setTimeListener(TimeListener listener) {
        this.mListener = listener;
    }

    public interface TimeListener {
        void onTick();

        void onTargetReached();
    }
}
