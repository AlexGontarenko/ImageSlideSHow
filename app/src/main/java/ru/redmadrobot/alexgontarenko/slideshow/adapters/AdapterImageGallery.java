package ru.redmadrobot.alexgontarenko.slideshow.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.polites.android.GestureImageView;
import com.polites.android.GesturePagerAdapter;

import java.util.HashMap;
import java.util.List;

import ru.redmadrobot.alexgontarenko.slideshow.R;


public class AdapterImageGallery extends GesturePagerAdapter {

    private Context mContext;
    private DisplayImageOptions options;
    private List<String> mImages;
    private HashMap<Integer, GestureImageView> mMap;
    private LayoutInflater inflater;
    private ImageLoader imageLoader;

    public AdapterImageGallery(Context context, List<String> beans) {
        mContext = context;
        mImages = beans;
        imageLoader = ImageLoader.getInstance();
        inflater = LayoutInflater.from(context);
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_launcher)
                .showImageOnFail(R.drawable.ic_launcher)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();
        mMap = new HashMap<Integer, GestureImageView>();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout;
        imageLayout = inflater.inflate(R.layout.item_gallery, view, false);
        assert imageLayout != null;
        GestureImageView imageView = (GestureImageView) imageLayout.findViewById(R.id.image_pager_galery);
        imageView.setAdjustViewBounds(true);
        imageView.setRecycle(false);

        imageLoader.displayImage(mImages.get(position), imageView, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            }
        });
        mMap.put(position, imageView);
        view.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        View obgect = (View) view;
        GestureImageView v = (GestureImageView)obgect.findViewById(R.id.image_pager_galery);
        v.recycle();
        v.setImageBitmap(null);
        mMap.remove(position);
        container.removeView((View) view);
    }

    @Override
    public int getCount() {
        return mImages.size();
    }

    @Override
    public GestureImageView getImage(int position) {
        return (GestureImageView) mMap.get(position);
    }

    @Override
    public void finishUpdate(View arg0) {
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(View arg0) {
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
