package ru.bmixsoft.jsontest.activity;

import ru.bmixsoft.jsontest.fragment.ConfirmTalonFragment;
import ru.bmixsoft.jsontest.fragment.MyFragment;

/**
 * Created by bmix on 19.01.2017.
 */
public class ConfirmTalonActivity extends OneSingleFragmentActivity
{

    @Override
    protected MyFragment createFragment() {
        if (mFragment == null){
            mFragment = ConfirmTalonFragment.getInstance(this);
        }
        return mFragment;
    }

}
