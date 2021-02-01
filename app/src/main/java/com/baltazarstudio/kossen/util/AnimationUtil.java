package com.baltazarstudio.kossen.util;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.baltazarstudio.kossen.R;

public class AnimationUtil {

        public static void leftToRight(Context context, View view) {
            view.startAnimation(load(context, R.anim.left_to_right));
        }

        public static void fadeIn(Context context, View view) {
            view.startAnimation(load(context, R.anim.fade_in));
        }

        private static Animation load(Context context, int anim) {
            return AnimationUtils.loadAnimation(context, anim);
        }
}