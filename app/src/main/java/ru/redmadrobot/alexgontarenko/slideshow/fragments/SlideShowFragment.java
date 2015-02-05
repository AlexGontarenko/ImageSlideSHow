package ru.redmadrobot.alexgontarenko.slideshow.fragments;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import ru.redmadrobot.alexgontarenko.slideshow.MainActivity;
import ru.redmadrobot.alexgontarenko.slideshow.R;
import ru.redmadrobot.alexgontarenko.slideshow.enity.PanoramioSlideObject;
import ru.redmadrobot.alexgontarenko.slideshow.slider.SlideShowAdapter;
import ru.redmadrobot.alexgontarenko.slideshow.slider.SlideShowView;

public class SlideShowFragment extends Fragment implements OnClickListener {

    private static final String SAVED_DATA = "SAVED_DATA";
    private static final String SAVED_SHOW = "SAVED_SHOW";
    private SlideShowView slideShowView;
    private SlideShowAdapter adapter;
    private boolean isStarted;
    private PanoramioSlideObject slideObject;
    private View paskhalka;
    private View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            init(savedInstanceState);
        } else {
            Bundle bundle = getArguments();
            init(bundle);
        }
        setHasOptionsMenu(true);
    }

    protected void init(Bundle bundle) {
        slideObject = (PanoramioSlideObject) bundle.getParcelable(SAVED_DATA);
        isStarted = bundle.getBoolean(SAVED_SHOW, true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rootView = inflater.inflate(R.layout.fragment_slide_show, container, false);
        slideShowView = (SlideShowView) rootView.findViewById(R.id.view_slideshow);
        paskhalka = rootView.findViewById(R.id.view_paskhalka);
        paskhalka.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_DATA, slideObject);
    }

    @Override
    public void onResume() {
        super.onResume();
        moveView();
        if(isStarted)
            startSlideShow();
    }


    @SuppressLint("NewApi")
    private void moveView()
    {
        paskhalka.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {

            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    paskhalka.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    paskhalka.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                final int maxWidth = rootView.getWidth()-paskhalka.getWidth();
                final int maxHeight = rootView.getHeight()-paskhalka.getHeight();
                final int top = (int) (Math.random()*maxHeight);
                final int left = (int) (Math.random()*maxWidth);
                paskhalka.setTranslationX(left);
                paskhalka.setTranslationY(top);
            }
        });

    }

    @Override
    public void onPause() {
        stopSlideShow();
        super.onPause();
    }

    private void startSlideShow() {
        adapter = new SlideShowAdapter(getActivity(), slideObject);
        slideShowView.setAdapter(adapter);
        slideShowView.play();
    }

    private void stopSlideShow() {
        if(adapter!=null)
            adapter.shutdown();
        adapter = null;
        slideShowView.stop();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_slideshow, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        final MenuItem item = menu.findItem(R.id.menu_slideshow);
        if(item!=null) {
            item.setTitle(isStarted? R.string.label_menu_stop : R.string.label_menu_start);
            item.setIcon(isStarted? R.drawable.ic_action_stop : R.drawable.ic_action_play);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_slideshow:
                slideStart(!isStarted);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void slideStart(boolean start) {
        if (start) {
            startSlideShow();
        } else {
            stopSlideShow();
        }
        isStarted = start;
        getActivity().invalidateOptionsMenu();
    }

    public static Bundle getArgs(PanoramioSlideObject slideObject) {
        final Bundle args = new Bundle();
        args.putParcelable(SAVED_DATA, slideObject);
        return args;
    }

    @Override
    public void onClick(View v) {
        MainActivity activity = (MainActivity) getActivity();
        activity.openGallery(slideObject, 0);
    }
}
