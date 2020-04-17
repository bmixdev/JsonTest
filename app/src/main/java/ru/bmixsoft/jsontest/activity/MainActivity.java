package ru.bmixsoft.jsontest.activity;


import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;

import ru.bmixsoft.jsontest.fragment.MainFragment;
import ru.bmixsoft.jsontest.fragment.MyFragment;

/**
 * Created by Михаил on 14.04.2016.
 */
public class MainActivity extends SingleFragmentActivity
        implements
        SwipeRefreshLayout.OnRefreshListener
{

    @Override
    protected MyFragment createFragment() {
        if (mFragment == null){
            mFragment = MainFragment.getInstance(this);
        }
        return mFragment;
    }

    @Override
    public void onRefresh() {
        FragmentManager fm = getSupportFragmentManager();
        super.onRefresh();
    }

}
