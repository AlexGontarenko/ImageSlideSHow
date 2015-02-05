package ru.redmadrobot.alexgontarenko.slideshow.slider;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import ru.redmadrobot.alexgontarenko.slideshow.enity.PanoramioSlideObject;

public class SlideShowView extends FrameLayout {

    private enum Status {
        STOPPED, PAUSED, PLAYING;
    }

    private Handler slideHandler;
    private View progressIndicator;
    private Status status = Status.STOPPED;
    private SlideList playlist = null;
    private SlideShowAdapter adapter = null;
    private SlideTransitionFactory transitionFactory = null;
    private int notAvailableSlidesSkipped = 0;
    SparseArray<View> recycledViews;

    private class WaitSlideRunnable implements Runnable {
        protected int currentSlide = 0;
        protected int previousSlide = 0;

        public void startWaiting(int currentSlide, int previousSlide) {
            this.currentSlide = currentSlide;
            this.previousSlide = previousSlide;

            slideHandler.post(this);
        }

        @Override
        public void run() {
            final SlideShowAdapter.SlideStatus status = adapter.getSlideStatus(currentSlide);

            switch (status) {
                case LOADING:
                    slideHandler.postDelayed(this, 200);
                    break;

                default:
                    playSlide(currentSlide, previousSlide);
            }
        }
    };

    private WaitSlideRunnable waitForCurrentSlide = new WaitSlideRunnable();

    private Runnable moveToNextSlide = new Runnable() {
        @Override
        public void run() {
            next();
        }
    };

    public SlideShowView(Context context) {
        this(context, null, 0);
    }

    public SlideShowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideShowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialise();
        init();
    }

    private void init() {

        SlideList pl = new SlideList();
        pl.setSlideDuration(PanoramioSlideObject.DEFAULT_PAUSE_SLIDE_MS);
        pl.setAutoAdvanceEnabled(true);
        setPlaylist(pl);

        SlideTransitionFactory tf = new SlideTransitionFactory(SlideTransitionFactory.DEFAULT_DURATION);
        setTransitionFactory(tf);

    }

    private void initialise() {
        slideHandler = new Handler();
        recycledViews = new SparseArray<View>();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        slideHandler.removeCallbacksAndMessages(null);
        super.onDetachedFromWindow();
    }

    @Override
    protected void onFinishInflate() {
        if (progressIndicator == null) {
            ProgressBar pb = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
            pb.setIndeterminate(true);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER;
            pb.setLayoutParams(lp);

            progressIndicator = pb;
        } else {
            removeView(progressIndicator);
        }
        super.onFinishInflate();
    }

    private void ensureComponentsAvailable() {
        if (adapter == null) {
            throw new RuntimeException("The SlideShowView needs an adapter (currently null)");
        }
    }

    public SlideShowAdapter getAdapter() {
        return adapter;
    }


    public void setAdapter(SlideShowAdapter adapter) {
        this.adapter = adapter;

        if (adapter != null) {
            SlideList pl = getPlaylist();
            pl.onSlideCountChanged(adapter.getCount());
            pl.setSlideDuration(adapter.getDurationSlide());
            pl.rewind();

            prepareSlide(pl.getFirstSlide());
        }
    }

    public SlideTransitionFactory getTransitionFactory() {
        if (transitionFactory == null) {
            transitionFactory = new SlideTransitionFactory();
        }
        return transitionFactory;
    }

    public void setTransitionFactory(SlideTransitionFactory transitionFactory) {
        this.transitionFactory = transitionFactory;
    }

    public SlideList getPlaylist() {
        if (playlist == null) {
            setPlaylist(new SlideList());
        }
        return playlist;
    }

    public void setPlaylist(SlideList playlist) {
        this.playlist = playlist;
        if (adapter != null) {
            playlist.onSlideCountChanged(adapter.getCount());
        }
    }

    public void play() {
        ensureComponentsAvailable();
        getPlaylist().rewind();
        next();
    }

    public void next() {
        final SlideList pl = getPlaylist();

        final int previousPosition = pl.getCurrentSlide();
        pl.next();
        final int currentPosition = pl.getCurrentSlide();

        playSlide(currentPosition, previousPosition);
    }

    public void previous() {
        final SlideList pl = getPlaylist();

        final int previousPosition = pl.getCurrentSlide();
        pl.previous();
        final int currentPosition = pl.getCurrentSlide();

        playSlide(currentPosition, previousPosition);
    }

    public void stop() {
        status = Status.STOPPED;

        slideHandler.removeCallbacksAndMessages(null);

        removeAllViews();
        recycledViews.clear();
    }

    public void pause() {
        switch (status) {
            case PAUSED:
            case STOPPED:
                return;

            case PLAYING:
                status = Status.PAUSED;

                slideHandler.removeCallbacksAndMessages(null);
        }
    }


    public void resume() {
        switch (status) {
            case PLAYING:
                return;

            case PAUSED:
                play();
                return;

            default:
                status = Status.PLAYING;

                SlideList pl = getPlaylist();
                if (pl.isAutoAdvanceEnabled()) {
                    slideHandler.removeCallbacks(moveToNextSlide);
                    slideHandler.postDelayed(moveToNextSlide, pl.getSlideDuration(pl.getCurrentSlide()));
                }
        }
    }

    public void togglePause() {
        if (status == Status.PLAYING) pause();
        else resume();
    }

    protected void playSlide(int currentPosition, int previousPosition) {
        final SlideShowAdapter.SlideStatus slideStatus = adapter.getSlideStatus(currentPosition);
        final SlideList pl = getPlaylist();

        if (currentPosition < 0) {
            stop();
            return;
        }

        slideHandler.removeCallbacksAndMessages(null);

        switch (slideStatus) {
            case READY:
                notAvailableSlidesSkipped = 0;

                status = Status.PLAYING;

                // Schedule next slide
                if (pl.isAutoAdvanceEnabled()) {
                    slideHandler.postDelayed(moveToNextSlide, pl.getSlideDuration(currentPosition));
                }

                displaySlide(currentPosition, previousPosition);

                break;

            case NOT_AVAILABLE:
                ++notAvailableSlidesSkipped;
                if (notAvailableSlidesSkipped < adapter.getCount()) {
                    prepareSlide(pl.getNextSlide());
                    next();
                } else {
                    stop();
                }
                break;

            case LOADING:
                showProgressIndicator();
                waitForCurrentSlide.startWaiting(currentPosition, previousPosition);

                break;
        }
    }

    private void prepareSlide(int position) {
        if (adapter != null && position >= 0) {
            adapter.prepareSlide(position);
        }
    }

    private void displaySlide(final int currentPosition, final int previousPosition) {

        hideProgressIndicator();

        // Add the slide view to our hierarchy
        final View inView = getSlideView(currentPosition);
        inView.setVisibility(View.INVISIBLE);
        addView(inView);

        final SlideTransitionFactory tf = getTransitionFactory();

        final Animator inAnimator = tf.getInAnimator(inView, this, previousPosition, currentPosition);
        if (inAnimator != null) {
            inAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    inView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                }
            });
            inAnimator.start();
        } else {
            inView.setVisibility(View.VISIBLE);
        }

        int childCount = getChildCount();
        if (childCount > 1) {

            final View outView = getChildAt(0);
            final Animator outAnimator = tf.getOutAnimator(outView, this, previousPosition, currentPosition);
            if (outAnimator != null) {
                outAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        outView.setVisibility(View.INVISIBLE);
                        recyclePreviousSlideView(previousPosition, outView);
                    }
                });
                outAnimator.start();
            } else {
                outView.setVisibility(View.INVISIBLE);
                recyclePreviousSlideView(previousPosition, outView);
            }
        }
    }


    private View getSlideView(int position) {
        // Do we have a view in our recycling bean?
        int viewType = adapter.getItemViewType(position);
        View recycledView = recycledViews.get(viewType);

        View v = adapter.getView(position, recycledView, this);
        return v;
    }

    private void recyclePreviousSlideView(int position, View view) {
        // Remove view from our hierarchy
        removeView(view);

        // Add to recycled views
        int viewType = adapter.getItemViewType(position);
        recycledViews.put(viewType, view);
        view.destroyDrawingCache();

        if (view instanceof ImageView) {
            ((ImageView) view).setImageDrawable(null);
        }

        // The adapter can recycle some memory with discard slide
        adapter.discardSlide(position);

        // The adapter can prepare the next slide
        prepareSlide(getPlaylist().getNextSlide());
    }

    protected void showProgressIndicator() {
        removeView(progressIndicator);

        progressIndicator.setAlpha(0);
        addView(progressIndicator);
        progressIndicator.animate().alpha(1).setDuration(500).start();
    }

    protected void hideProgressIndicator() {
        removeView(progressIndicator);
    }
}