package ru.bmixsoft.jsontest.fragment;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.fragment.dialog.RateDialogFragment;
import ru.bmixsoft.jsontest.fragment.dialog.SndEmailDialogFragment;
import ru.bmixsoft.jsontest.fragment.helpuser.HelpUserDialogFragment;
import ru.bmixsoft.jsontest.utils.UpdateHelper;
import ru.bmixsoft.jsontest.utils.Utils;

public class SlideMenuListFragment extends ListFragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.slidemenu, null);
        TextView tvAppInfo = v.findViewById(R.id.slideMenuAppInfo);
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.launch_title)).append(" ").append(UpdateHelper.getVersionBuilderName()).append("\n").append(getString(R.string.launch_bottom));
        tvAppInfo.setText(sb);
        ListView lv = v.findViewById(android.R.id.list);
        ColorDrawable drawable = new ColorDrawable(getResources().getColor(android.R.color.white));
        lv.setDivider(drawable);
        lv.setDividerHeight(Utils.dpToPx(getActivity(), 10));
        return v;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SlideAdapter adapter = new SlideAdapter(getActivity());

        adapter.add(new SlideItemMenu(getResources().getString(R.string.slideMenuMailToHelp), getResources().getString(R.string.slideMenuMailToHelp_foot), R.id.slide_menu_item_mail_help, R.drawable.ic_help));
        adapter.add(new SlideItemMenu(getResources().getString(R.string.slideMenuMailToRazrab), getResources().getString(R.string.slideMenuMailToRazrab_foot), R.id.slide_menu_item_mail_razrab, R.drawable.mail_razrab));
        adapter.add(new SlideItemMenu(getResources().getString(R.string.slideMenuMailToRateApp), getResources().getString(R.string.slideMenuMailToRateApp_foot), R.id.slide_menu_item_rate_app, R.drawable.ic_rate));
        adapter.add(new SlideItemMenu(getResources().getString(R.string.slideMenuMailToShareApp), getResources().getString(R.string.slideMenuMailToShareApp_foot), R.id.slide_menu_item_share_app, R.drawable.ic_share));
        adapter.add(new SlideItemMenu(getResources().getString(R.string.slideMenuMailTo4PDA), getResources().getString(R.string.slideMenuMailTo4PDA_foot), R.id.slide_menu_item_open_4pda, R.drawable.ic_write));
        adapter.add(new SlideItemMenu(getResources().getString(R.string.item_exit), "", R.id.slide_menu_item_exit, R.drawable.ic_exit));

        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        SlideItemMenu c = (SlideItemMenu) (getListAdapter()).getItem(position);
//        SingleFragmentActivity singleFragmentActivity = (SingleFragmentActivity)getActivity();
        switch (c.actionId) {
            case R.id.slide_menu_item_mail_help:
                HelpUserDialogFragment.show(getActivity());
                break;
            case R.id.slide_menu_item_mail_razrab:
                SndEmailDialogFragment.show(getActivity(), getString(R.string.slideMenuMailToRazrab), getString(R.string.slideMenuMailToRazrab_foot));
                break;
            case R.id.slide_menu_item_rate_app:
                RateDialogFragment.rate(getActivity());
                break;
            case R.id.slide_menu_item_share_app:
                UpdateHelper.shareApp(getActivity());
                break;
            case R.id.slide_menu_item_open_4pda:
                Utils.openUrl(getActivity(), getString(R.string.urlToAppIn4PDA));
                break;
            case R.id.slide_menu_item_exit:
                getActivity().setResult(R.integer.RESULT_CLOSE_ALL);
                getActivity().finish();
                break;
        }
    }

    private class SlideItemMenu {
        public String tag;
        public int iconRes;
        public int actionId;
        private String footer;

        public SlideItemMenu(String tag, String footer, int actionId, int iconRes) {
            this.tag = tag;
            this.footer = footer;
            this.iconRes = iconRes;
            this.actionId = actionId;
        }
    }

    public class SlideAdapter extends ArrayAdapter<SlideItemMenu> {

        public SlideAdapter(Context context) {
            super(context, 0);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.slidemenu_row, null);
            }
            ImageView icon = (ImageView) convertView.findViewById(R.id.slidemenu_row_icon);
            icon.setVisibility(View.VISIBLE);
            icon.setImageResource(getItem(position).iconRes);
            TextView title = (TextView) convertView.findViewById(R.id.slidemenu_row_title);
            title.setText(getItem(position).tag);
            TextView footer = (TextView) convertView.findViewById(R.id.slidemenu_row_footer);
            footer.setText(getItem(position).footer);
            return convertView;
        }

    }
}
