package ru.redmadrobot.alexgontarenko.slideshow.enity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class PanoramioSlideObject implements Parcelable {

    public static  final int MIN_COUNT = 5;
    public static final int MAX_COUNT = 20;
    public static final int DEFAULT_COUNT = 7;
    public static final int MIN_PAUSE_SLIDE_MS = 500;
    public static final int MAX_PAUSE_SLIDE_MS = 30000;
    public static final int DEFAULT_PAUSE_SLIDE_MS = 3000;

    private int slidePause;
    private int countImg;
    private ArrayList<String> imgList;

    public PanoramioSlideObject() {
        this(DEFAULT_PAUSE_SLIDE_MS);
    }

    public PanoramioSlideObject(int slidePause) {
        this(slidePause, DEFAULT_COUNT);
    }

    public PanoramioSlideObject(int slidePause, int countImg) {
        this(slidePause, countImg, new ArrayList<String>());
    }

    public PanoramioSlideObject(int slidePause, int countImg, ArrayList<String> imgList) {
        setSlidePause(slidePause);
        setCountImg(countImg);
        this.imgList = imgList;
    }

    protected PanoramioSlideObject(Parcel in) {
        slidePause = in.readInt();
        countImg = in.readInt();
        final int containsArray = in.readInt();
        if(containsArray == 1)
            imgList = in.createStringArrayList();
    }

    public boolean isEmpty(){
        return (countImg == 0 || imgList == null || imgList.isEmpty()) ? true : false;
    }

    public void setSlidePause(int slidePause) {
        if(slidePause < MIN_PAUSE_SLIDE_MS) this.slidePause = MIN_PAUSE_SLIDE_MS;
        else if( slidePause > MAX_PAUSE_SLIDE_MS) this.slidePause = MAX_PAUSE_SLIDE_MS;
        else this.slidePause = slidePause;
    }

    public void setCountImg(int countImg) {
        if(countImg < MIN_COUNT) this.countImg = MIN_COUNT;
        else if(countImg > MAX_COUNT) this.countImg = MAX_COUNT;
        else this.countImg = countImg;
    }

    public void setImgList(ArrayList<String> imgList) {
        this.imgList = imgList;
    }

    public int getSlidePause() {
        return slidePause;
    }

    public int getCountImg() {
        return countImg;
    }

    public ArrayList<String> getImgList() {
        return imgList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(slidePause);
        dest.writeInt(countImg);
        dest.writeInt(imgList == null ? 0 : 1);
        if(imgList != null) {
            dest.writeStringList(imgList);
        }
    }

    public static final Parcelable.Creator<PanoramioSlideObject> CREATOR = new Parcelable.Creator<PanoramioSlideObject>()
    {
        public PanoramioSlideObject createFromParcel(Parcel in) {
            return new PanoramioSlideObject(in);
        }

        public PanoramioSlideObject[] newArray(int size)
        {
            return new PanoramioSlideObject[size];
        }
    };
}
