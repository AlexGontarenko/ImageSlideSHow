package ru.redmadrobot.alexgontarenko.slideshow.fragments;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import ru.redmadrobot.alexgontarenko.slideshow.MainActivity;
import ru.redmadrobot.alexgontarenko.slideshow.R;
import ru.redmadrobot.alexgontarenko.slideshow.api.PanoramioRequest;
import ru.redmadrobot.alexgontarenko.slideshow.enity.PanoramioSlideObject;
import ru.redmadrobot.alexgontarenko.slideshow.parcers.BasePanoramioResponse;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private static final String SAVED_DATA = "SAVED_DATA";
    private PanoramioSlideObject slideObject;
    private TextView currentPause, currentCount, myPosition;
    private Button btnStart;
    private LocationManager locationManager;
    private Location myLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null)
            slideObject = savedInstanceState.getParcelable(SAVED_DATA);
        else
            slideObject = new PanoramioSlideObject();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((ActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        btnStart = (Button) rootView.findViewById(R.id.btn_get_and_start);
        btnStart.setOnClickListener(this);
        ((TextView)rootView.findViewById(R.id.label_min_value_pause)).setText(Double.toString((PanoramioSlideObject.MIN_PAUSE_SLIDE_MS / 100) * 0.1));
        ((TextView)rootView.findViewById(R.id.label_max_value_pause)).setText(Double.toString((PanoramioSlideObject.MAX_PAUSE_SLIDE_MS/100)*0.1));
        ((TextView)rootView.findViewById(R.id.label_min_value_count)).setText(Integer.toString(PanoramioSlideObject.MIN_COUNT));
        ((TextView)rootView.findViewById(R.id.label_max_value_count)).setText(Integer.toString(PanoramioSlideObject.MAX_COUNT));
        myPosition = (TextView) rootView.findViewById(R.id.label_my_position);
        currentPause = (TextView) rootView.findViewById(R.id.current_value_pause);
        currentPause.setText(getString(R.string.label_time_slidihg, slideObject.getSlidePause()*0.001f));
        currentCount = (TextView) rootView.findViewById(R.id.current_value_count);
        currentCount.setText(getString(R.string.label_image_count, slideObject.getCountImg()));
        SeekBar seekBar = (SeekBar) rootView.findViewById(R.id.seekbar_pause_value);
        seekBar.setMax(PanoramioSlideObject.MAX_PAUSE_SLIDE_MS-PanoramioSlideObject.MIN_PAUSE_SLIDE_MS);
        seekBar.setProgress(slideObject.getSlidePause()-PanoramioSlideObject.MIN_PAUSE_SLIDE_MS);
        seekBar.setOnSeekBarChangeListener(listnerPaause);
        seekBar = (SeekBar)rootView.findViewById(R.id.seekbar_count_value);
        seekBar.setMax(PanoramioSlideObject.MAX_COUNT - PanoramioSlideObject.MIN_COUNT);
        seekBar.setProgress(slideObject.getCountImg()-PanoramioSlideObject.MIN_COUNT);
        seekBar.setOnSeekBarChangeListener(listnerCount);
        initLoaders();
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        myLocation = locationManager.getLastKnownLocation(provider);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateStatusPosition();
        invalidateStatusBatton();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 60, 30, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 60, 30, locationListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_DATA, slideObject);
    }

    @Override
    public void onClick(View v) {
        if(myLocation!=null)
        getLoaderManager().restartLoader(PanoramioRequest.ID,
                PanoramioRequest.getArgs(slideObject.getCountImg(), myLocation.getLatitude(), myLocation.getLongitude(),2000), panoramioCallback);
        invalidateStatusBatton();
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                myLocation = location;
                invalidateStatusPosition();
                invalidateStatusBatton();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    private OnSeekBarChangeListener listnerPaause = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            slideObject.setSlidePause(PanoramioSlideObject.MIN_PAUSE_SLIDE_MS + progress);
            currentPause.setText(getString(R.string.label_time_slidihg, slideObject.getSlidePause()  * 0.001f));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }
    };

    private OnSeekBarChangeListener listnerCount = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            slideObject.setCountImg(PanoramioSlideObject.MIN_COUNT + progress);
            currentCount.setText(getString(R.string.label_image_count, slideObject.getCountImg()));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }
    };

    protected void initLoaders() {

        if (getLoaderManager().getLoader(PanoramioRequest.ID) != null) {
            //showProgressDialog();
            getLoaderManager().initLoader(PanoramioRequest.ID, null, panoramioCallback);
        }
    }

    // Loader отправки данных
    private LoaderCallbacks<BasePanoramioResponse> panoramioCallback = new LoaderCallbacks<BasePanoramioResponse>() {

        @Override
        public void onLoaderReset(Loader<BasePanoramioResponse> loader) { }

        @Override
        public void onLoadFinished(Loader<BasePanoramioResponse> loader, BasePanoramioResponse data) {
            final BasePanoramioResponse base = data;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    finishCallback(base);
                }
            });
            getLoaderManager().destroyLoader(loader.getId());
        }

        @Override
        public Loader<BasePanoramioResponse> onCreateLoader(int id, Bundle args) {
            return new PanoramioRequest(getActivity().getApplicationContext(), args);
        }
    };

    private void invalidateStatusPosition() {
        myPosition.setText(myLocation == null ? R.string.position_find : R.string.position_finded);
        myPosition.setTextColor(myLocation==null? 0xffff0000 : 0xff00ff00);
    }

    private void invalidateStatusBatton(){
        btnStart.setEnabled(myLocation==null || getLoaderManager().getLoader(PanoramioRequest.ID)!=null? false : true);
    }

    protected void finishCallback(final BasePanoramioResponse base) {
        if(base!=null) {
            slideObject.setImgList(base.getData().getImgList());
            MainActivity activity = (MainActivity) getActivity();
            activity.openSlideShow(slideObject);
        } else {
           Toast.makeText(getActivity(),"Bad!",Toast.LENGTH_SHORT).show();
        }
        invalidateStatusBatton();
    }
}
