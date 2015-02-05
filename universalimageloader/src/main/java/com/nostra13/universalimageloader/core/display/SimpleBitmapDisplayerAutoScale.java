package com.nostra13.universalimageloader.core.display;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

public final class SimpleBitmapDisplayerAutoScale implements BitmapDisplayer {

	final private boolean isAutoScale;

	public SimpleBitmapDisplayerAutoScale(){
		isAutoScale=false;
	}

	public SimpleBitmapDisplayerAutoScale(boolean autoScale){
		isAutoScale=autoScale;
	}

	@Override
	public void display(Bitmap bitmap, ImageAware imageAware,LoadedFrom loadedFrom) {				
		if(isAutoScale && (imageAware.getWrappedView() instanceof ImageView)){
		double viewWidth = imageAware.getWidth(), viewHeight = imageAware.getHeight(), imgWidth =bitmap.getWidth(),imgHeight = bitmap.getHeight();
		if(imgWidth/imgHeight>=viewWidth/viewHeight)
			((ImageView)imageAware.getWrappedView()).setScaleType(ScaleType.CENTER_CROP);
		else 
			((ImageView)imageAware.getWrappedView()).setScaleType(ScaleType.CENTER_INSIDE);
		}
		imageAware.setImageBitmap(bitmap);
	}
}
