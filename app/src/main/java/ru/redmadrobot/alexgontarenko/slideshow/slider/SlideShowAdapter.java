package ru.redmadrobot.alexgontarenko.slideshow.slider;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.FrameLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import ru.redmadrobot.alexgontarenko.slideshow.enity.PanoramioSlideObject;

public class SlideShowAdapter extends BaseAdapter {

    public enum SlideStatus {
        READY, LOADING, NOT_AVAILABLE;
    }

    private static class BitmapCache {
        SlideStatus status = SlideStatus.LOADING;
        WeakReference<Bitmap> bitmap = null;
    }

    private int durationSlide;
    private ArrayList<String> items;
    private SparseArray<SlideLoadingListener> activeTargets;

    private Context context;
    private SparseArray<BitmapCache> cachedBitmaps;
    final private ImageLoader imageLoader;

    public SlideShowAdapter(Context context, PanoramioSlideObject slideObject) {
        this.context = context;
        this.cachedBitmaps = new SparseArray<BitmapCache>(3);
        durationSlide = slideObject.getSlidePause();
        items = slideObject.getImgList();
        this.activeTargets = new SparseArray<SlideLoadingListener>(3);
        imageLoader = ImageLoader.getInstance();
    }

    public Context getContext() {
        return context;
    }

    public int getDurationSlide(){
        return durationSlide;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public String getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void shutdown() {
        activeTargets.clear();
    }


    protected void onBitmapLoaded(int position, Bitmap bitmap) {
        activeTargets.remove(position);
        BitmapCache bc = cachedBitmaps.get(position);
        if (bc != null) {
            bc.status = bitmap == null ? SlideStatus.NOT_AVAILABLE : SlideStatus.READY;
            bc.bitmap = new WeakReference<Bitmap>(bitmap);
        }
    }

    protected void onBitmapNotAvailable(int position) {
        activeTargets.remove(position);
        BitmapCache bc = cachedBitmaps.get(position);
        if (bc != null) {
            bc.status = SlideStatus.NOT_AVAILABLE;
            bc.bitmap = null;
        }
    }

    protected void loadBitmap(final int position) {
        if (position < 0 || position >= items.size()) onBitmapNotAvailable(position);

        SlideLoadingListener listener = new SlideLoadingListener(position);
        activeTargets.put(position, listener);
        imageLoader.loadImage(items.get(position), listener);
    }

    private class SlideLoadingListener extends SimpleImageLoadingListener {
        int position;

        private SlideLoadingListener(int position) {
            this.position = position;
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            SlideShowAdapter.this.onBitmapNotAvailable(position);
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            SlideShowAdapter.this.onBitmapLoaded(position, loadedImage);
        }
    }


    protected ImageView newImageViewInstance() {
        ImageView iv = new ImageView(context);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        iv.setLayoutParams(lp);
        lp.setMargins(0,0,0,0);
        iv.setPadding(0,0,0,0);
        iv.setAdjustViewBounds(true);
        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        return iv;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView iv;
        if (convertView == null) {
            iv = newImageViewInstance();
        } else {
            iv = (ImageView) convertView;
        }
        BitmapCache bc = cachedBitmaps.get(position);
        if (bc == null) {
            prepareSlide(position);
            bc = cachedBitmaps.get(position);
        }
        if (bc != null && bc.status == SlideStatus.READY) {
            iv.setImageBitmap(bc.bitmap.get());
        }
        return iv;
    }

    public void prepareSlide(int position) {
        BitmapCache bc = cachedBitmaps.get(position);
        if (bc != null && bc.bitmap != null && bc.bitmap.get() != null) {
            bc.bitmap.get().recycle();
            bc.bitmap.clear();
        }

        bc = new BitmapCache();
        cachedBitmaps.put(position, bc);
        loadBitmap(position);
    }

    public void discardSlide(int position) {
        BitmapCache bc = cachedBitmaps.get(position);
        if (bc != null && bc.bitmap != null && bc.bitmap.get() != null) {
            bc.bitmap.get().recycle();
            bc.bitmap.clear();
        }
        cachedBitmaps.remove(position);
    }

    public SlideStatus getSlideStatus(int position) {
        BitmapCache bc = cachedBitmaps.get(position);
        return bc != null ? bc.status : SlideStatus.NOT_AVAILABLE;
    }
}
