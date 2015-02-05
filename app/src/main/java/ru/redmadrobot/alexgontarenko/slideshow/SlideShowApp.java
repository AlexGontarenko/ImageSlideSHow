package ru.redmadrobot.alexgontarenko.slideshow;

import android.app.Application;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by AlexG on 04.02.2015.
 */
public class SlideShowApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        final File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());

        final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(5)
                        // default 3
                .threadPriority(Thread.NORM_PRIORITY - 1)
                        // default Thread.NORM_PRIORITY-1
                .denyCacheImageMultipleSizesInMemory().memoryCache(new LruMemoryCache(5 * 1024 * 1024))
                .memoryCacheSize(3 * 1024 * 1024).diskCache(new UnlimitedDiscCache(cacheDir))
                .diskCacheSize(15 * 1024 * 1024).build();
        ImageLoader.getInstance().init(config);

    }
}
