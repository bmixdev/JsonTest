package ru.bmixsoft.jsontest.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.fragment.EditPolisFragment;
import ru.bmixsoft.jsontest.fragment.FavoritesDoctFragment;
import ru.bmixsoft.jsontest.fragment.MainFragment;
import ru.bmixsoft.jsontest.fragment.SlideMenuListFragment;
import ru.bmixsoft.jsontest.fragment.helpuser.HelpUserDialogFragment;
import ru.bmixsoft.jsontest.fragment.options.LibOption;
import ru.bmixsoft.jsontest.fragment.options.OptionsFragment;
import ru.bmixsoft.jsontest.fragment.whatnew.WhatNewDialogFragment;
import ru.bmixsoft.jsontest.utils.Utils;

/**
 * Created by Михаил on 02.12.2016.
 */
public class MainActivityNew extends AppCompatActivity {

    private SlidingMenu mSlidingMenu;
    private ActionBar mActionBar;
    private boolean isWhatNewDialogShow = false;

    private static final String keyNeedShowWhatNewDlg = "keyNeedShowWhatNewDlg";

    public void createSlidePanel(Activity activity){

        //использование контекстного меню
        //      setHasOptionsMenu(true);
        AppCompatActivity act = (AppCompatActivity) activity;

        mActionBar = act.getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        //  mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        //mActionBar.setIcon(android.R.drawable.ic_menu_more);
        mActionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_info_details);

        mSlidingMenu = new SlidingMenu(act);
        mSlidingMenu.setMode(SlidingMenu.LEFT);
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        mSlidingMenu.setFadeDegree(0.20f);
        mSlidingMenu.attachToActivity(act, SlidingMenu.SLIDING_CONTENT);
        mSlidingMenu.setBehindWidthRes(R.dimen.slidingmenu_behind_width);
        //    mSlidingMenu.setMenu(R.layout.sidemenu);
        mSlidingMenu.setMenu(R.layout.menu_frame);
        mSlidingMenu.setSlidingEnabled(false);
        mSlidingMenu.setOnCloseListener(new SlidingMenu.OnCloseListener() {
            @Override
            public void onClose() {
                mActionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_info_details);

            }
        });
        mSlidingMenu.setOnOpenListener(new SlidingMenu.OnOpenListener() {
            @Override
            public void onOpen() {

               // Drawable d = getResources().getDrawable(android.R.drawable.ic_menu_info_details);
           //     d.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.ADD);
                mActionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_more);
            }
        });

        FragmentManager fragmentManager = act.getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.menu_frame, new SlideMenuListFragment())
                .commitAllowingStateLoss();
    }


    @Override
    public void onBackPressed()
    {
        // super.onBackPressed();
        if (!mSlidingMenu.isMenuShowing()) {
            mSlidingMenu.showMenu();

            //Drawable d = getResources().getDrawable(android.R.drawable.ic_menu_more);
           // d.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.ADD);
            mActionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_more);
            mSlidingMenu.setSlidingEnabled(true);
        } else {
            mActionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_info_details);
            mSlidingMenu.toggle();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Utils.viberate(getApplicationContext(), 50);
            switch (item.getItemId()) {
                case R.id.navigation_talons:
                    loadFragment(MainFragment.getInstance(getApplicationContext()));
                    return true;
                case R.id.navigation_polise:
                    loadFragment(EditPolisFragment.getInstance(getApplicationContext()));
                    return true;
                case R.id.navigation_favorites:
                    loadFragment(FavoritesDoctFragment.getInstance(getApplicationContext()));
                    return true;
                case R.id.navigation_options:
                    loadFragment(OptionsFragment.getInstance(getApplicationContext()));
                    return true;
            }
            return false;
        }
    };


    private void loadFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl_content, fragment);
        ft.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (!outState.isEmpty()) {
            outState.putBoolean(keyNeedShowWhatNewDlg, isWhatNewDialogShow);
        }
        super.onSaveInstanceState(outState);
    }

    public void refreshUI()
    {
        loadFragment(OptionsFragment.getInstance(getApplicationContext()));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        isWhatNewDialogShow = false;
        if (savedInstanceState != null) {
            isWhatNewDialogShow = savedInstanceState.getBoolean(keyNeedShowWhatNewDlg);
        }

        setContentView(R.layout.fragment_main_view_new);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_polise);


        AppCompatActivity activity = this;
//        activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.default_background_color)));
        boolean isShowHelpUser = LibOption.getOptionValueBool(getApplication(), "showHelpUserLayout");
        if (isShowHelpUser){
            FragmentManager fm = getSupportFragmentManager();
            HelpUserDialogFragment dialog = HelpUserDialogFragment.newInstance();
            dialog.show(fm, "helpUserFragment");

        }
        createSlidePanel(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if ( LibOption.getOptionValueBool(this, "showWhatNew"))
        {
            try {
                if (!isWhatNewDialogShow) {
                    WhatNewDialogFragment.show(this);
                    isWhatNewDialogShow = true;
                }
            } catch (Exception e)
            {
                Crashlytics.logException(e);
            }
        }
      //  viewPager.setCurrentItem(1); viewPager.setCurrentItem(0);
     //   adapterViewPager.notifyDataSetChanged();
    }

/*
    public void refreshUI()
    {
        int curItem = viewPager.getCurrentItem();
        viewPager.setCurrentItem(0); viewPager.setCurrentItem(curItem);
        adapterViewPager.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed()
    {
        // super.onBackPressed();
        if (!mSlidingMenu.isMenuShowing()) {
            mSlidingMenu.showMenu();
            mActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_pressed);
            mSlidingMenu.setSlidingEnabled(true);
         } else {
            mActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_normal);
            mSlidingMenu.toggle();
        }
    }
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // MenuInflater inflater = getMenuInflater();
        // inflater.inflate(R.menu.fragment_menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Utils.viberate(this, 50);
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
