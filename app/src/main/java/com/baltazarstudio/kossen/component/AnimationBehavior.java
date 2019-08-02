package com.baltazarstudio.kossen.component;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.baltazarstudio.kossen.R;

public class AnimationBehavior {

    private static final int SHOW = 0;
    private static final int HIDE = 1;
    private static int mBehavior;
    private static Animation.AnimationListener listener;

    private static void run(int behavior, View view, int animation) {
        mBehavior = behavior;
        initListener(view);

        Animation mAnimation = AnimationUtils.loadAnimation(
                view.getContext(),
                animation);
        mAnimation.setAnimationListener(listener);
        view.startAnimation(mAnimation);
    }

    public static void showFadeIn(final View view) {
        run(SHOW, view, R.anim.fade_in);
    }

    public static void hideFadeOut(final View view) {
        run(HIDE, view, R.anim.fade_out);
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
