package ru.redmadrobot.alexgontarenko.slideshow;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import ru.redmadrobot.alexgontarenko.slideshow.enity.PanoramioSlideObject;
import ru.redmadrobot.alexgontarenko.slideshow.fragments.GalleryFragment;
import ru.redmadrobot.alexgontarenko.slideshow.fragments.SettingsFragment;
import ru.redmadrobot.alexgontarenko.slideshow.fragments.SlideShowFragment;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new SettingsFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
           case android.R.id.home:
           super.onBackPressed();
           return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openSlideShow(PanoramioSlideObject object) {
        Fragment fragment = new SlideShowFragment();
        fragment.setArguments(SlideShowFragment.getArgs(object));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment).addToBackStack(null)
                .commit();
    }

    public void openGallery(PanoramioSlideObject object, int position) {
        Fragment fragment = new GalleryFragment();
        fragment.setArguments(GalleryFragment.getArgs(object, position));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment).addToBackStack(null)
                .commit();
    }
}
