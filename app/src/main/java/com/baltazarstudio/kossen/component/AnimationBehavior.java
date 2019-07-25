package com.baltazarstudio.kossen.component;

import android.util.SparseIntArray;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.baltazarstudio.kossen.R;

public class AnimationBehavior {

    public static final int FADE_OUT = 0;
    public static final int FADE_IN = 1;
    //public static final int RIGHT_TO_LEFT = 2;
    private static final int SHOW = 0;
    private static final int HIDE = 1;
    private static SparseIntArray animationMap;
    private static int mBehavior;
    private static Animation.AnimationListener listener;

    private static void run(int behavior, View view, int animation) {
        mBehavior = behavior;
        initListener(view);

        Animation mAnimation = AnimationUtils.loadAnimation(
                view.getContext(),
                getAnimationResource(animation));
        mAnimation.setAnimationListener(listener);
        view.startAnimation(mAnimation);
    }

    public static void show(final View view, int animation) {
        run(SHOW, view, animation);
    }

    public static void hide(final View view, int animation) {
        run(HIDE, view, animation);
    }

    private static int getAnimationResource(int i) {
        if (animationMap == null) {
            animationMap = new SparseIntArray();
            animationMap.put(FADE_IN, R.anim.fade_in);
            animationMap.put(FADE_OUT, R.anim.fade_out);
        }
        return  animationMap.get(i);
    }

    private static void initListener(final View view) {
        listener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (mBehavior == SHOW) {
                    view.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mBehavior == HIDE) {
                    view.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
    }
}
