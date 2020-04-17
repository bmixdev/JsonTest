package ru.bmixsoft.jsontest.object;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Created by Михаил on 23.05.2016.
 */
public class BaseBackPressedListener implements OnBackPressedListener {
    private final FragmentActivity activity;

    public BaseBackPressedListener(FragmentActivity activity) {
        this.activity = activity;
    }

    @Override
    public void doBack() {
        activity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}
