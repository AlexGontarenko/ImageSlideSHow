package ru.redmadrobot.alexgontarenko.slideshow.parcers;

import android.os.Parcel;
import android.os.Parcelable;

import ru.redmadrobot.alexgontarenko.slideshow.enity.PanoramioSlideObject;

public class BasePanoramioResponse implements Parcelable {


    private PanoramioSlideObject data;

    public BasePanoramioResponse() {
        super();
            data = new PanoramioSlideObject();
    }

    protected BasePanoramioResponse(Parcel in) {
        if(in.readInt() == 1) {
            try {
                data = in.readParcelable(Class.forName(in.readString()).getClassLoader());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public PanoramioSlideObject getData() {
        return data;
    }


    public boolean isOk()
    {
        return data!=null;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(data==null? 0: 1);
        if(data != null) {
            dest.writeString(data.getClass().getName());
            dest.writeParcelable(data, flags);
        }
    }

    public static final Parcelable.Creator<BasePanoramioResponse> CREATOR = new Parcelable.Creator<BasePanoramioResponse>()
    {
        public BasePanoramioResponse createFromParcel(Parcel in) {
            return new BasePanoramioResponse(in);
        }

        public BasePanoramioResponse[] newArray(int size) {
            return new BasePanoramioResponse[size];
        }
    };
}
