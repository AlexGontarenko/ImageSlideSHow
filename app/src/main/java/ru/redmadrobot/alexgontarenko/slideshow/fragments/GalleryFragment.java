package ru.redmadrobot.alexgontarenko.slideshow.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.polites.android.GestureViewPager;
import com.viewpagerindicator.UnderlineGesturePageIndicator;

import java.io.File;
import java.util.List;

import ru.redmadrobot.alexgontarenko.slideshow.R;
import ru.redmadrobot.alexgontarenko.slideshow.adapters.AdapterImageGallery;
import ru.redmadrobot.alexgontarenko.slideshow.enity.PanoramioSlideObject;

public class GalleryFragment extends Fragment {

    private static final String SAVED_DATA = "SAVED_DATA";
    private static final String SAVED_POSITION = "SAVED_POSITION";
    private GestureViewPager pager;
    private UnderlineGesturePageIndicator mGalleryIndicator;
    private int pagerPosition;
    private PanoramioSlideObject slideObject;

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

    protected void init(Bundle bundle){
        pagerPosition = bundle.getInt(SAVED_POSITION, 0);
        slideObject = (PanoramioSlideObject) bundle.getParcelable(SAVED_DATA);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((ActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        View rootView = inflater.inflate(R.layout.fragment_image_gallery, container, false);
        pager = (GestureViewPager) rootView.findViewById(R.id.view_pager_galery);
        pager.setAdapter(new AdapterImageGallery(getActivity(), slideObject.getImgList()));
        mGalleryIndicator = (UnderlineGesturePageIndicator) rootView.findViewById(R.id.pager_indicator_galery);
        mGalleryIndicator.setViewPager(pager);
        mGalleryIndicator.setSelectedColor(0x00ff4d);
        mGalleryIndicator.setCurrentItem(pagerPosition);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_POSITION, pagerPosition);
        outState.putParcelable(SAVED_DATA, slideObject);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_gallery, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_share:
                startActivity(onShare().getIntent());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //share method
    private ShareCompat.IntentBuilder onShare() {
        final ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder.from(getActivity());
        intentBuilder.setType("text/plain").setSubject(getString(R.string.place_at_me)).
                setText(getString(R.string.place_at_me)).setChooserTitle(R.string.share_with);
        setImageToShareIntent(intentBuilder.getIntent());
        return intentBuilder;
    }

    private void setImageToShareIntent(Intent shareIntent) {
        final List<String> images = slideObject.getImgList();
        if(images!=null&&images.size()>0){
            final ImageLoader imageLoader = ImageLoader.getInstance();
            int currentImage = pager.getCurrentItem();
            if(currentImage>-1){
                String url = images.get(currentImage);
                if(imageLoader.getDiscCache().get(url) != null){
                    final File image = imageLoader.getDiscCache().get(url);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(image));
                }
            }
        }
    }

    public static Bundle getArgs(PanoramioSlideObject slideObject, int position) {
        final Bundle args = new Bundle();
        args.putInt(SAVED_POSITION,position);
        args.putParcelable(SAVED_DATA,slideObject);
        return args;
    }
}
