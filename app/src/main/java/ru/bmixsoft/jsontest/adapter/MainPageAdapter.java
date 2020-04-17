package ru.bmixsoft.jsontest.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;

import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.fragment.EditPolisFragment;
import ru.bmixsoft.jsontest.fragment.FavoritesDoctFragment;
import ru.bmixsoft.jsontest.fragment.MainFragment;
import ru.bmixsoft.jsontest.fragment.options.LibOption;
import ru.bmixsoft.jsontest.fragment.options.OptionsFragment;

/**
 * Created by Михаил on 02.12.2016.
 */
public class MainPageAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 4;
        private Context mContext;
        private Fragment curFragment;
        private MainFragment talonFragment;


        public MainPageAdapter(FragmentManager fragmentManager, Context context) {
            super(fragmentManager);
            mContext = context;
            talonFragment = (MainFragment) MainFragment.getInstance(mContext);
            curFragment = talonFragment;
        }

        public Fragment getCurFragment()
        {
            return curFragment;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    //((AppCompatActivity) mActivityContext).setTitle();
                    curFragment = talonFragment;
                    break;
                case 1:
                    curFragment = EditPolisFragment.getInstance(mContext);
                    break;
                case 2:
                    curFragment = FavoritesDoctFragment.getInstance(mContext);
                    break;
                case 3:
                    curFragment = OptionsFragment.getInstance(mContext);
                    break;
                default:
                    curFragment = null;
            }
            return curFragment;
        }

    public void refreshFragmentAdapeter(int position)
    {

        switch (position) {
            case 0:
                MainFragment fragmentTalons = (MainFragment) MainFragment.getInstance(mContext);
                fragmentTalons.refreshAdapter();
                if (LibOption.getOptionValueBool(mContext.getApplicationContext(),"autoSyncTalons"))
                     fragmentTalons.refreshTalons();
                break;
            case 1:
                 EditPolisFragment fragment = (EditPolisFragment) EditPolisFragment.getInstance(mContext);
                fragment.refreshAdapter();
                break;
            case 2:
                FavoritesDoctFragment fragment1 = (FavoritesDoctFragment) FavoritesDoctFragment.getInstance(mContext);
                fragment1.refreshAdapter();
                break;
            case 3:
                break;
            default:
                break;
        }
    }


    // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {

            Drawable myDrawable;
            myDrawable = mContext.getResources().getDrawable(R.drawable.diagram03);
            SpannableStringBuilder sb = new SpannableStringBuilder("     "); // space added before text for convenience
            String pageName;


            switch (position) {
                case 0:
                    myDrawable = mContext.getResources().getDrawable(R.drawable.icon_tab_talons);
                    pageName = "  " + mContext.getResources().getString(R.string.slmenu_mainfrag);
                    break;
                case 1:
                    myDrawable = mContext.getResources().getDrawable(R.drawable.icon_tab_polise);
                    pageName = "  " + mContext.getResources().getString(R.string.slmenu_polis);
                    break;
                case 2:
                    myDrawable = mContext.getResources().getDrawable(R.drawable.icon_tab_favorites);
                    pageName = "  " + mContext.getResources().getString(R.string.slmenu_favorites);
                    break;
                case 3:
                    myDrawable = mContext.getResources().getDrawable(R.drawable.icon_tab_options);
                    pageName = "  " + mContext.getResources().getString(R.string.slmenu_options);
                    break;
                default:
                    myDrawable = mContext.getResources().getDrawable(R.drawable.diagram03);
                    pageName = "  " + "Page#"+position;
                    break;
            }
            myDrawable.setBounds(0, 0, myDrawable.getIntrinsicWidth(), myDrawable.getIntrinsicHeight());

            ImageSpan span = new ImageSpan(myDrawable, ImageSpan.ALIGN_BASELINE);
            sb.setSpan(span, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.append(pageName);
            sb.append("\n");
            sb.append("\n  ");
            return sb;
        }

}