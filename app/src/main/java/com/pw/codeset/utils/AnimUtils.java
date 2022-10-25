package com.pw.codeset.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.AnimRes;

import com.pw.codeset.R;

public class AnimUtils {
    public static Animation getAnim(Context context, @AnimRes int id, Animation.AnimationListener listener) {
        Animation animation = AnimationUtils.loadAnimation(context, id);
        animation.setAnimationListener(listener);
        return animation;
    }

    public static void BottomIn(View view) {
        if (view == null || view.getContext() == null) {
            return;
        }
        view.startAnimation(getAnim(view.getContext(), R.anim.slide_bottom_in, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        }));
    }
    public static void BottomOut(View view) {
        if (view == null || view.getContext() == null) {
            return;
        }
        view.startAnimation(getAnim(view.getContext(), R.anim.slide_bottom_out, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        }));
    }
    public static void TopIn(View view) {
        if (view == null || view.getContext() == null) {
            return;
        }
        view.startAnimation(getAnim(view.getContext(), R.anim.slide_top_in, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        }));
    }
    public static void TopOut(View view) {
        if (view == null || view.getContext() == null) {
            return;
        }
        view.startAnimation(getAnim(view.getContext(), R.anim.slide_top_out, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        }));
    }

    public static void AlphaIn(View view) {
        if (view == null || view.getContext() == null) {
            return;
        }
        view.startAnimation(getAnim(view.getContext(), R.anim.alpha_in, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        }));
    }
    public static void AlphaOut(View view) {
        if (view == null || view.getContext() == null) {
            return;
        }
        view.startAnimation(getAnim(view.getContext(), R.anim.alpha_out, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        }));
    }

}
