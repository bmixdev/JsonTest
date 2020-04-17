package ru.bmixsoft.jsontest.fragment.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import ru.bmixsoft.jsontest.R;

/**
 * Created by Mike on 18.01.2018.
 */

public class TimePeriodDialogFragment extends DialogFragment {

    public static final String EXTRA_UID = "ru.bmixsoft.jsontest.fragment.dialog.timePeriodDialogFragment.uid";
    public static final String EXTRA_STR = "ru.bmixsoft.jsontest.fragment.dialog.timePerioDialogFragment.value";
    public static final String EXTRA_TITLE = "ru.bmixsoft.jsontest.fragment.dialog.timePerioDialogFragment.title";
    public static final String EXTRA_HEADER = "ru.bmixsoft.jsontest.fragment.dialog.timePerioDialogFragment.header";


    private int mUID;
    private String mValue;
    private String mTitle;
    private String mHeader;
    private String timeBegin;
    private String timeEnd;
    private TextView textViewTxt;

    private View mUIView;

    private TextView mFooterDesc;

    public static TimePeriodDialogFragment newInstance(String title, String header, int uid, String curValue) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_UID, uid);
        args.putString(EXTRA_STR, curValue);
        args.putString(EXTRA_TITLE, title);
        args.putString(EXTRA_HEADER, header);
        TimePeriodDialogFragment fragment = new TimePeriodDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(int resultCode) {
        if (getTargetFragment() == null)
            return;
        Intent i = new Intent();
        i.putExtra(EXTRA_UID, mUID);
        i.putExtra(EXTRA_STR, mValue);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }

    private void setDescProgress() {
        // mFooterDesc.setText("Текущее значение: "+mValue);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mValue = getArguments().getString(EXTRA_STR);
        mUID = getArguments().getInt(EXTRA_UID);
        mTitle = getArguments().getString(EXTRA_TITLE);
        mHeader = getArguments().getString(EXTRA_HEADER);

        timeBegin = mValue.substring(0, 5);
        timeEnd = mValue.substring(6);


        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_time_period_dialog, null);


        ViewPager vpPager = (ViewPager) v.findViewById(R.id.timePeriodViewPager);
        SimplePagerAdapter adapterViewPager = new SimplePagerAdapter(getActivity());
        vpPager.setAdapter(adapterViewPager);
        vpPager.setCurrentItem(0);
        textViewTxt = (TextView) v.findViewById(R.id.fragTimePeriodDlg_txt);
        textViewTxt.setText(mValue);

        return new AlertDialog.Builder(getActivity())
                .setTitle(mTitle)
                .setView(v)
                .setPositiveButton(
                        android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                              //  mValue = editText.getText().toString();
                                sendResult(Activity.RESULT_OK);
                            }
                        })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    public enum ResourcesModel {

        // создаем 3 перечисления с тайтлом и макетом
        // для удобной работы в адаптере
        FIRST_SCREEN(R.string.time_period_dlg_screen_1, R.layout.time_period_dialog_page_1),
        SECOND_SCREEN(R.string.time_period_dlg_screen_2, R.layout.time_period_dialog_page_1);

        private int mTitleResourceId;
        private int mLayoutResourceId;

        ResourcesModel(int titleResId, int layoutResId) {
            mTitleResourceId = titleResId;
            mLayoutResourceId = layoutResId;
        }

        public int getTitleResourceId() {
            return mTitleResourceId;
        }

        public int getLayoutResourceId() {
            return mLayoutResourceId;
        }
    }

    public class SimplePagerAdapter extends PagerAdapter {

        private Context mContext;

        public SimplePagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public Object instantiateItem(ViewGroup viewGroup, int position) {
            // получаем ресурсы (название и макет) соответствующий позиции в адаптере
            ResourcesModel resources = ResourcesModel.values()[position];
            LayoutInflater inflater = LayoutInflater.from(mContext);
            // инициализируем экран ViewPager'а в соответствии с позицией
            ViewGroup layout = (ViewGroup) inflater.inflate(
                    resources.getLayoutResourceId(), viewGroup, false);
            TimePicker timePicker = new TimePicker(mContext);
            ViewGroup.LayoutParams params = new TimePicker.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            timePicker.setLayoutParams(params);
            timePicker.setTag(position);
            timePicker.setIs24HourView(true);
            String timeStr = position == 0 ? timeBegin : timeEnd;
            if (Build.VERSION.SDK_INT >= 23 ) {
                timePicker.setHour(Integer.valueOf(timeStr.substring(0, 2)));
                timePicker.setMinute(Integer.valueOf(timeStr.substring(3, 5)));
            }
            else
            {
                timePicker.setCurrentHour(Integer.valueOf(timeStr.substring(0, 2)));
                timePicker.setCurrentMinute(Integer.valueOf(timeStr.substring(3, 5)));
            }
            timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                  int pos = (int) timePicker.getTag();
                  String hour = String.valueOf(i).length() < 2 ? "0" + String.valueOf(i) : String.valueOf(i);                                 String minute = String.valueOf(i1).length() < 2 ? "0" + String.valueOf(i1) : String.valueOf(i1);
                  if (pos == 0)
                  {
                     timeBegin = hour + ":" + minute;
                  }
                  else
                  {
                      timeEnd = hour + ":" + minute;
                  }
                  mValue = timeBegin + "-" +timeEnd;
                  textViewTxt.setText(mValue);
                }
            });
            layout.addView(timePicker);
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
            return ResourcesModel.values().length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // получаем название соответствующее позиции в адаптере
            ResourcesModel customPagerEnum = ResourcesModel.values()[position];

            return mContext.getString(customPagerEnum.getTitleResourceId());
        }
    }

}

