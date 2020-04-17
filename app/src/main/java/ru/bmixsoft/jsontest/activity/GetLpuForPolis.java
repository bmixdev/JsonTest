package ru.bmixsoft.jsontest.activity;

import ru.bmixsoft.jsontest.fragment.LpuForPolisFragment;
import ru.bmixsoft.jsontest.fragment.MyFragment;

/**
 * Created by Михаил on 27.02.2017.
 */
public class GetLpuForPolis extends OneSingleFragmentActivity
{

    @Override
    protected MyFragment createFragment() {
        if (mFragment == null){
            mFragment = LpuForPolisFragment.getInstance(this);
        }
        return mFragment;
    }

}