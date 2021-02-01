package com.baltazarstudio.kossen.component;

import android.os.Handler;

import java.util.Locale;

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

    public String getCurrentTimeFormatted() {
        return String.format(
                Locale.getDefault(),
                "%02d:%02d:%02d", currentTimeHours, currentTimeMinutes, currentTimeSeconds);
    }

    public int getCurrentTimeInSeconds() {
        int minutes = currentTimeHours * 60;
        minutes += 60 * currentTimeMinutes;
        return currentTimeSeconds + minutes;
    }

    public void setGoal(String time) {
        // "HH:mm:ss" pattern
        goalTimeHours = Integer.parseInt(time.substring(0, 2));
        goalTimeMinutes = Integer.parseInt(time.substring(3, 5));
        goalTimeSeconds = Integer.parseInt(time.substring(6));
    }

    public boolean hasGoal() {
        return goalTimeHours > 0
                || goalTimeMinutes > 0
                || goalTimeSeconds > 0;
    }

    public int getGoalTimeInSeconds() {
        int minutes = goalTimeHours * 60;
        minutes += 60 * goalTimeMinutes;
        return goalTimeSeconds + minutes;
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

    public boolean isRunning() { return isRunning; }

    private boolean isTargetReached() {
        return (currentTimeSeconds == goalTimeSeconds
                && currentTimeMinutes == goalTimeMinutes
                && currentTimeHours == goalTimeHours);
    }

    public boolean hasStarted() {
        return hasStarted;
    }

    public void setTimeListener(TimeListener listener) {
        this.mListener = listener;
    }

    public interface TimeListener {
        void onTick();
        void onTargetReached();
    }
}
