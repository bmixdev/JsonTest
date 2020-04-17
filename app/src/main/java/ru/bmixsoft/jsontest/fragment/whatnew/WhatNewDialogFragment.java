package ru.bmixsoft.jsontest.fragment.whatnew;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import net.qiushao.lib.dbhelper.DBFactory;

import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.fragment.options.LibOption;

/**
 * Created by Mike on 18.01.2018.
 */

public class WhatNewDialogFragment extends DialogFragment {

        public static final String EXTRA_TITLE = WhatNewDialogFragment.class.getPackage().getName() + ".title";
        public static final String EXTRA_HEADER = WhatNewDialogFragment.class.getPackage().getName() + ".header";


        private String mTitle;
        private String mHeader;

    public static WhatNewDialogFragment newInstance(String title, String header) {
            Bundle args = new Bundle();
            args.putString(EXTRA_TITLE, title);
            args.putString(EXTRA_HEADER, header);
            WhatNewDialogFragment fragment = new WhatNewDialogFragment();
            fragment.setArguments(args);
            return fragment;
        }


    public static void show(Activity activity)
    {
        FragmentManager fm = ((FragmentActivity) activity).getSupportFragmentManager();
        WhatNewDialogFragment dialog = WhatNewDialogFragment.newInstance("Что нового?", "Список изменений:");
        dialog.show(fm, "WhatNewDialogFragment");
    }


    private void printHtmlText(TextView textView)
        {
            FactoryWhatNew fwn = FactoryWhatNew.getInstance(getActivity(), DBFactory.getInstance(getActivity().getApplication()).getDBHelper(WhatNew.class));
            String descr = fwn.getAllNews();

            if (descr.isEmpty()) dismiss();

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                textView.setText(Html.fromHtml(descr,Html.FROM_HTML_MODE_LEGACY));
            } else {
                textView.setText(Html.fromHtml(descr));
            }
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            mTitle = getArguments().getString(EXTRA_TITLE);
            mHeader = getArguments().getString(EXTRA_HEADER);

            View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_whatnew_dialog,null);

            TextView headerView = (TextView) v.findViewById(R.id.fragWhatNewDlg_header);
            headerView.setText(mHeader);

            TextView bodyText = (TextView) v.findViewById(R.id.fragWhatNewDlg_body);
            printHtmlText(bodyText);

            AlertDialog dlg =  new AlertDialog.Builder(getActivity())
                    .setTitle(mTitle)
                    .setView(v)
                    .setPositiveButton(
                            android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    LibOption.setOption(getActivity(), "showWhatNew", false);
                                }
                            })
                    .create();



            // обработчик нажатия кнопки назад
            DialogInterface.OnKeyListener keyListener = new DialogInterface.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode,
                                     KeyEvent event) {
                    if( keyCode == KeyEvent.KEYCODE_BACK)
                    {
                        LibOption.setOption(getActivity(), "showWhatNew", false);
                    }
                    return false;
                }
            };


            dlg.setOnKeyListener(keyListener);

            return dlg;
        }



}
