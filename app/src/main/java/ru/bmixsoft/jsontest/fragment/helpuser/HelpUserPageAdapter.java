package ru.bmixsoft.jsontest.fragment.helpuser;


import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ru.bmixsoft.jsontest.R;

/**
 * Created by Mike on 29.01.2018.
 */

public class HelpUserPageAdapter extends PagerAdapter {


    private Context mContext;
    private DialogFragment mDialogFragment;

    private Callback mCallback;

    public interface Callback{
        public void OnDialogDismiss();
    }

    public HelpUserPageAdapter(Context context, DialogFragment fragment, Callback callback) {
        mContext = context;
        mDialogFragment = fragment;
        mCallback = callback;
    }

    @Override
    public Object instantiateItem(ViewGroup viewGroup, int position) {
        // получаем ресурсы (название и макет) соответствующий позиции в адаптере
        HelpUserModel resources = HelpUserModel.values()[position];
        LayoutInflater inflater = LayoutInflater.from(mContext);
        // инициализируем экран ViewPager'а в соответствии с позицией
        ViewGroup layout = (ViewGroup) inflater.inflate(
                resources.getmLayoutResId(), viewGroup, false);
        if (resources.getmLayoutResId() == R.layout.fragment_help_user_screen_last) {
            Button btn = (Button) layout.findViewById(R.id.btnHUSLastOK);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.OnDialogDismiss();
                }
            });
        }
        viewGroup.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup viewGroup, int position, Object view) {
        viewGroup.removeView((View) view);
    }

    @Override
    public int getCount() {
        // кличество элементов в адаптере соответствует
        // количеству значений в enum классе
        return HelpUserModel.values().length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // получаем название соответствующее позиции в адаптере
        HelpUserModel customPagerEnum = HelpUserModel.values()[position];

        return mContext.getString(customPagerEnum.getmTitleResId());
    }
}
