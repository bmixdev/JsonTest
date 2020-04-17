package ru.bmixsoft.jsontest.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.fragment.MyFragment;

public abstract class OneSingleFragmentActivity
        extends AppCompatActivity
{
    protected static final String FRAGMENT_TAG = "OneSingleFragmentActivity.Fragment";

    protected abstract MyFragment createFragment();

    public MyFragment mFragment;
    private FragmentManager mFragmentManager;
    private ActionBar mActionBar;

    public void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

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
        mActionBar.setDisplayShowTitleEnabled(true);
        //   mActionBar.setIcon(android.R.drawable.ic_menu_more);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // FrameLayout fl = new FrameLayout(this);
        // fl.setId(R.id.fragmentContainer);
        // setContentView(fl);
        setContentView(R.layout.activity_fragment_one);

        mFragmentManager = getSupportFragmentManager();
        mFragment = createFragment();
            mFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerOne, mFragment)
                .commitAllowingStateLoss();
        createSlidePanel(this);

    }

    @Override
    public void onBackPressed()
    {
        this.finish();
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
