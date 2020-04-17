package ru.bmixsoft.jsontest.fragment.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import ru.bmixsoft.jsontest.R;

/**
 * Created by Mike on 18.01.2018.
 */

public class InfoDialogFragment extends DialogFragment {

        public static final String EXTRA_TITLE = InfoDialogFragment.class.getPackage().getName() + ".title";
        public static final String EXTRA_HEADER = InfoDialogFragment.class.getPackage().getName() + ".header";
    public static final String EXTRA_VALUE = InfoDialogFragment.class.getPackage().getName() + ".value";

    public static final String EXTRA_TYPE_DLG = InfoDialogFragment.class.getPackage().getName() + ".type";


        private String mTitle;
        private String mHeader;
        private String mValue;
        private int mType;

        public enum TypeDialog{

              INFO(0)
            , ERROR(1);

            private int mValue;

            TypeDialog(int value) {
                mValue = value;
            }

            public int getValue() {
                return mValue;
            }

            public static TypeDialog fromValue(int value) {
                switch (value) {
                    case 0:
                        return INFO;
                    case 1:
                    default:
                        return ERROR;
                }
            }
        }

    public static InfoDialogFragment newInstance(String title, String header, String value, int type) {
            Bundle args = new Bundle();
            args.putString(EXTRA_TITLE, title);
            args.putString(EXTRA_HEADER, header);
            args.putString(EXTRA_VALUE, value);
            args.putInt(EXTRA_TYPE_DLG, type);
            InfoDialogFragment fragment = new InfoDialogFragment();
            fragment.setArguments(args);
            return fragment;
        }



    public static void show(Activity activity, String title, String header, String value, TypeDialog typeDlg)
    {
        FragmentManager fm = ((FragmentActivity) activity).getSupportFragmentManager();
        fm.beginTransaction();

        InfoDialogFragment dialog = InfoDialogFragment.newInstance(title, header, value, typeDlg.getValue());
        dialog.show(fm, "InfoDialogFragment");
    }

    public static void showInfo(Activity activity, String title, String header, String value)
    {
        FragmentManager fm = ((FragmentActivity) activity).getSupportFragmentManager();
        InfoDialogFragment dialog = InfoDialogFragment.newInstance(title, header, value, 0);
        dialog.show(fm, "InfoDialogFragment");
    }


    public static void showError(Activity activity, String title, String header, String value)
    {
        FragmentManager fm = ((FragmentActivity) activity).getSupportFragmentManager();
        InfoDialogFragment dialog = InfoDialogFragment.newInstance(title, header, value, 1);
        dialog.show(fm, "InfoDialogFragment");
    }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            mTitle = getArguments().getString(EXTRA_TITLE);
            mHeader = getArguments().getString(EXTRA_HEADER);
            mValue = getArguments().getString(EXTRA_VALUE);
            mType = getArguments().getInt(EXTRA_TYPE_DLG);

            View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_info_dialog,null);

            /*
            ImageView img = (ImageView) v.findViewById(R.id.fragInfoDlg_ico);
            img.setImageResource(mType == 1 ? R.drawable.ic_dlg_error : R.drawable.ic_dlg_info);
            */

            TextView headerView = (TextView) v.findViewById(R.id.fragInfoDlg_header);
            headerView.setText(mHeader);
            //Если Ошибка
            if (mType == 1)
            {
                headerView.setBackgroundColor(getActivity().getResources().getColor(R.color.red));
            }

            TextView bodyText = (TextView) v.findViewById(R.id.fragInfoDlg_body);
            bodyText.setText(mValue);

            AlertDialog dlg =  new AlertDialog.Builder(getActivity())
                    .setTitle(mTitle)
                    .setView(v)
                    .setIcon(mType == 1 ? R.drawable.ic_dlg_error : R.drawable.ic_dlg_info)
                    .setPositiveButton(
                            android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

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

                    }
                    return false;
                }
            };


            dlg.setOnKeyListener(keyListener);

            return dlg;
        }



}
