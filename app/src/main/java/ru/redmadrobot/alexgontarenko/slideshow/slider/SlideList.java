package ru.redmadrobot.alexgontarenko.slideshow.slider;

public class SlideList {
    public static final long DEFAULT_SLIDE_DURATION = 3000;

    private int slideCount = 0;

    private int currentSlide = -1;

    private boolean isAutoAdvanceEnabled = true;
    private long slideDuration = DEFAULT_SLIDE_DURATION;

    public SlideList() {
    }

    public int getFirstSlide() {
        return slideCount > 0 ? 0 : -1;
    }

    public int getCurrentSlide() {
        return currentSlide;
    }

    public int getNextSlide() {
        if (currentSlide < slideCount - 1) return currentSlide + 1;
        else return 0;
    }

    public int getPreviousSlide() {
        if (currentSlide > 0) return currentSlide - 1;
        else return slideCount - 1;
    }

    public void rewind() {
        currentSlide = -1;
    }

    public int next() {
        currentSlide = getNextSlide();
        return currentSlide;
    }

    public int previous() {
        currentSlide = getPreviousSlide();
        return currentSlide;
    }

    public void onSlideCountChanged(int newSlideCount) {
        this.slideCount = newSlideCount;
        if (currentSlide >= newSlideCount) {
            currentSlide = this.slideCount - 1;
        }
    }

    /**
     * Indicate if the slide show is advancing to the next slide after slideDuration ms are elapsed
     * @return
     */
    public boolean isAutoAdvanceEnabled() {
        return isAutoAdvanceEnabled;
    }

    /**
     * Set if the slide show should advance to the next slide after slideDuration ms are elapsed
     *
     * @param isAutoAdvanceEnabled true to automatically move to next slide after slideDuration ms
     */
    public void setAutoAdvanceEnabled(boolean isAutoAdvanceEnabled) {
        this.isAutoAdvanceEnabled = isAutoAdvanceEnabled;
    }

    public long getSlideDuration(int position) {
        return slideDuration;
    }

    public void setSlideDuration(long slideDuration) {
        this.slideDuration = slideDuration;
    }
}
