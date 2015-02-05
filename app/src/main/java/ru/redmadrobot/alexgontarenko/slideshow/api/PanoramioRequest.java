package ru.redmadrobot.alexgontarenko.slideshow.api;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import ru.redmadrobot.alexgontarenko.slideshow.parcers.BasePanoramioResponse;

public class PanoramioRequest extends AsyncTaskLoader<BasePanoramioResponse> {

    public final static int ID = 123456;
    private final static String ARGS_COUNT = "ARGS_COUNT";
    private final static String ARGS_LAT = "ARGS_LAT";
    private final static String ARGS_LON = "ARGS_LON";
    private final static String ARGS_EPS = "ARGS_EPS";

    private final int count;
    private final double lat;
    private final double lon;
    private final double eps;
    protected BasePanoramioResponse result;

    public PanoramioRequest(Context context, Bundle args) {
        super(context);
        count = args.getInt(ARGS_COUNT);
        lat = args.getDouble(ARGS_LAT);
        lon = args.getDouble(ARGS_LON);
        eps = args.getDouble(ARGS_EPS);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (result == null || takeContentChanged())
            forceLoad();
        else
            deliverResult(result);
    }

    @Override
    public BasePanoramioResponse loadInBackground() {
        BasePanoramioResponse data = null;
        try {
            data = (BasePanoramioResponse) UtilsAPI.getPlaceImg(getContext(),count,lat,lon,eps);
        } catch (APIWebEsception apiWebEsception) {
            apiWebEsception.printStackTrace();
            data = null;
        }
        return data;
    }

    @Override
    public void deliverResult(BasePanoramioResponse data) {
        if (isStarted()) {
            super.deliverResult(data);
            this.result = data;
        }
    }

    public static Bundle getArgs(int count, double lat, double lon, double epsM) {
        final Bundle args = new Bundle();
        args.putInt(ARGS_COUNT, count);
        args.putDouble(ARGS_LAT,lat);
        args.putDouble(ARGS_LON,lon);
        args.putDouble(ARGS_EPS,epsM);
        return args;
    }
}