package ru.bmixsoft.jsontest.activity;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.adapter.MainPageAdapter;
import ru.bmixsoft.jsontest.fragment.SlideMenuListFragment;
import ru.bmixsoft.jsontest.fragment.helpuser.HelpUserDialogFragment;
import ru.bmixsoft.jsontest.fragment.options.LibOption;
import ru.bmixsoft.jsontest.fragment.whatnew.WhatNewDialogFragment;
import ru.bmixsoft.jsontest.utils.Utils;

/**
 * Created by Михаил on 02.12.2016.
 */
public class MainPageActivity extends AppCompatActivity {


    MainPageAdapter adapterViewPager;
    ViewPager viewPager;

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
      //  mActionBar.setDisplayShowTitleEnabled(false);
        //mActionBar.setIcon(android.R.drawable.ic_menu_more);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_normal);

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
                mActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_normal);

            }
        });
        mSlidingMenu.setOnOpenListener(new SlidingMenu.OnOpenListener() {
            @Override
            public void onOpen() {
                mActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_pressed);
            }
        });

        FragmentManager fragmentManager = act.getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.menu_frame, new SlideMenuListFragment())
                .commitAllowingStateLoss();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (!outState.isEmpty()) {
            outState.putBoolean(keyNeedShowWhatNewDlg, isWhatNewDialogShow);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        isWhatNewDialogShow = false;
        if (savedInstanceState != null) {
            isWhatNewDialogShow = savedInstanceState.getBoolean(keyNeedShowWhatNewDlg);
        }

        setContentView(R.layout.fragment_main_page_view);
        viewPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MainPageAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(adapterViewPager);

        viewPager.setCurrentItem(0);

        // Attach the page change listener inside the activity
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
              //  Toast.makeText(getApplication(),"Selected page position: " + position, Toast.LENGTH_SHORT).show();
                adapterViewPager.refreshFragmentAdapeter(position);
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)           {

                    if (position == 0 && positionOffset <= 0.01f) {
                        mSlidingMenu.setSlidingEnabled(true);
                    } else {
                        mSlidingMenu.setSlidingEnabled(false);
                    }

            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });

       PagerTitleStrip pts =(PagerTitleStrip) findViewById(R.id.pager_header);
        pts.setBackgroundColor(getResources().getColor(R.color.default_background_color));

        AppCompatActivity activity = this;
        activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.default_background_color)));
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
