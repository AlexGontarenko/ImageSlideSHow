package com.polites.android;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public abstract class GesturePagerAdapter extends PagerAdapter{
    	
    @Override
    public abstract Object instantiateItem(ViewGroup collection, int position);
    
    @Override
    public void destroyItem(ViewGroup collection, int position, Object view){
        collection.removeView((View) view);
    }
    
    @Override
    public abstract int getCount();
    
    @Override
    public boolean isViewFromObject(View view, Object object){
        return view.equals(object);
    }
    
    public abstract GestureImageView getImage(int position);
    
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
