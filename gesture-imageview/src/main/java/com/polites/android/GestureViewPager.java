package com.polites.android;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class GestureViewPager extends ViewPager {

	private OnPageChangeListener userListener;
	private OnPageChangeListener pageIndicatorListener;
	private OnPageChangeListener listener = new OnPageChangeListener() {
		
		private int currentPosition = -1;
		
		@Override
		public void onPageSelected(int position) {
			currentPosition = position;
			if(userListener != null)
				userListener.onPageSelected(position);
			if(pageIndicatorListener != null)
				pageIndicatorListener.onPageSelected(position);
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			if(userListener != null)
				userListener.onPageScrolled(arg0, arg1, arg2);
			if(pageIndicatorListener != null)
				pageIndicatorListener.onPageScrolled(arg0, arg1, arg2);
		}
		@Override
		public void onPageScrollStateChanged(int state) {
			Log.d("GestureViewPager", "Listener");
			if(getAdapter() instanceof GesturePagerAdapter && state == ViewPager.SCROLL_STATE_IDLE){
				GesturePagerAdapter adapter = (GesturePagerAdapter)getAdapter();
				int count = adapter.getCount();
				if(count <= 1)
					return;
				if(currentPosition == 0)
					toNormalScaleIfNotNull(adapter.getImage(currentPosition + 1));
				else if(currentPosition == count - 1)
					toNormalScaleIfNotNull(adapter.getImage(currentPosition - 1));
				else{
					toNormalScaleIfNotNull(adapter.getImage(currentPosition + 1));
					toNormalScaleIfNotNull(adapter.getImage(currentPosition - 1));
				}
			}
			if(userListener != null)
				userListener.onPageScrollStateChanged(state);
			if(pageIndicatorListener != null)
				pageIndicatorListener.onPageScrollStateChanged(state);
		}
	};
	
	private void toNormalScaleIfNotNull(GestureImageView image){
		if(image != null)
			image.toNormalScale();
	}
	
	public GestureViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnPageChangeListener(listener);
	}

	public GestureViewPager(Context context) {
		super(context);
		this.setOnPageChangeListener(listener);
	}
	
	@Override
	protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
		if(v instanceof GestureImageView){
			GestureImageView img = (GestureImageView)v;
			if(!img.isScaled())
				return super.canScroll(v, checkV, dx, x, y);
			if(dx < 0)
				return !img.isRightScroll();
			else
				return !img.isLeftScroll();
		}
		else
			return super.canScroll(v, checkV, dx, x, y);
	}
	
	public void setOnPageChangeUserListener(OnPageChangeListener listener) {
		userListener = listener;
	}
	
	public void setOnPageChangePageIndicatorListener(OnPageChangeListener listener) {
		pageIndicatorListener = listener;
	}
}
