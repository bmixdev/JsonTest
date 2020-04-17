package ru.bmixsoft.jsontest.activity;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.fragment.MyFragment;
import ru.bmixsoft.jsontest.fragment.SlideMenuListFragment;
import ru.bmixsoft.jsontest.object.OnBackPressedListener;

public abstract class SingleFragmentActivity extends AppCompatActivity
implements SwipeRefreshLayout.OnRefreshListener {
    protected static final String FRAGMENT_TAG = "SingleFragmentActivity.Fragment";

    protected OnBackPressedListener onBackPressedListener;

    protected abstract MyFragment createFragment();

    public SwipeRefreshLayout mSwipeRefreshLayout;
    public MyFragment mFragment;
    private FragmentManager mFragmentManager;
    private SlidingMenu mSlidingMenu;
    private ActionBar mActionBar;


    public void changeFragment(MyFragment fragment)
    {
        if(mFragment != null)
        {
            mFragment = fragment;
            mFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, mFragment)
                    .commitAllowingStateLoss();
             }
    }

    public void createSlidePanel(Activity activity){

        //использование контекстного меню
  //      setHasOptionsMenu(true);
        AppCompatActivity act = (AppCompatActivity) activity;

        mActionBar = act.getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        //   mActionBar.setIcon(android.R.drawable.ic_menu_more);

        mSlidingMenu = new SlidingMenu(act);
        mSlidingMenu.setMode(SlidingMenu.LEFT);
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        mSlidingMenu.setFadeDegree(0.35f);
        mSlidingMenu.attachToActivity(act, SlidingMenu.SLIDING_CONTENT);
        mSlidingMenu.setBehindWidthRes(R.dimen.slidingmenu_behind_width);
        //    mSlidingMenu.setMenu(R.layout.sidemenu);
        mSlidingMenu.setMenu(R.layout.menu_frame);

        FragmentManager fragmentManager = act.getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.menu_frame, new SlideMenuListFragment())
                .commitAllowingStateLoss();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // FrameLayout fl = new FrameLayout(this);
       // fl.setId(R.id.fragmentContainer);
        //setContentView(fl);
        setContentView(R.layout.activity_fragment);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //!!! На время чтобы не работало обновдление
        //mSwipeRefreshLayout.setEnabled(true);

        mFragmentManager = getSupportFragmentManager();
     //   Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);

        //if (fragment == null) {
        mFragment = createFragment();
            mFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, mFragment)
                .commitAllowingStateLoss();
      //}

        createSlidePanel(this);

    }

    @Override
    public void onBackPressed()
    {
        // super.onBackPressed();
        if (!mSlidingMenu.isMenuShowing()) {
            mSlidingMenu.showMenu();
            mActionBar.setIcon(android.R.drawable.ic_menu_info_details);
        } else {
            mSlidingMenu.toggle();
            mActionBar.setIcon(android.R.drawable.ic_menu_more);
        }
    }


    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // MenuInflater inflater = getMenuInflater();
       // inflater.inflate(R.menu.fragment_menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
