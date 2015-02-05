package ru.redmadrobot.alexgontarenko.slideshow.slider;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

public class SlideTransitionFactory{

    public static final long DEFAULT_DURATION = 500;
    public static final Interpolator DEFAULT_INTERPOLATOR = new DecelerateInterpolator();

    private long duration = DEFAULT_DURATION;
    private Interpolator interpolator;

    public SlideTransitionFactory() {
        this(DEFAULT_DURATION, DEFAULT_INTERPOLATOR);
    }

    public SlideTransitionFactory(long duration) {
        this(duration, DEFAULT_INTERPOLATOR);
    }

    public SlideTransitionFactory(long duration, Interpolator interpolator) {
        this.interpolator = interpolator;
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Interpolator getInterpolator() {
        return interpolator;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
    }

    public Animator getInAnimator(View target, SlideShowView parent, int fromSlide, int toSlide){
        target.setAlpha(0);
        target.setScaleX(1);
        target.setScaleY(1);
        target.setTranslationX(0);
        target.setTranslationY(0);
        target.setRotationX(0);
        target.setRotationY(0);

        ObjectAnimator animator = ObjectAnimator.ofFloat(target, View.ALPHA, 1);
        animator.setDuration(getDuration());
        animator.setInterpolator(getInterpolator());
        return animator;
    }


    public Animator getOutAnimator(View target, SlideShowView parent, int fromSlide, int toSlide) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, View.ALPHA, 0);
        animator.setDuration(getDuration());
        animator.setInterpolator(getInterpolator());
        return animator;
    }
}
