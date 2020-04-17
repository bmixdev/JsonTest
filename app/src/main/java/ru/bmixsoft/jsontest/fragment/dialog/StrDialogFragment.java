package ru.bmixsoft.jsontest.fragment.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import ru.bmixsoft.jsontest.R;

/**
 * Created by Mike on 18.01.2018.
 */

public class StrDialogFragment extends DialogFragment {

        public static final String EXTRA_UID ="ru.bmixsoft.jsontest.fragment.dialog.strdialogfragment.uid";
        public static final String EXTRA_STR ="ru.bmixsoft.jsontest.fragment.dialog.strdialogfragment.num";
        public static final String EXTRA_TITLE ="ru.bmixsoft.jsontest.fragment.dialog.strdialogfragment.title";
        public static final String EXTRA_HEADER ="ru.bmixsoft.jsontest.fragment.dialog.strdialogfragment.header";


        private int mUID;
        private String mValue;
        private String mTitle;
        private String mHeader;

        private View mUIView;

        private TextView mFooterDesc;


    public static void show(FragmentActivity activity, String title, String header, String value)
    {
        FragmentManager fm = activity.getSupportFragmentManager();
        StrDialogFragment dialog = StrDialogFragment.newInstance(title, header, 0, value);
        //     dialog.setTargetFragment(this, REQUEST_DLGSERV);
        dialog.show(fm, "StrDialogFragment");
    }


    public static StrDialogFragment newInstance(String title, String header, int uid, String curValue) {
            Bundle args = new Bundle();
            args.putInt(EXTRA_UID, uid);
            args.putString(EXTRA_STR, curValue);
            args.putString(EXTRA_TITLE, title);
            args.putString(EXTRA_HEADER, header);
            StrDialogFragment fragment = new StrDialogFragment();
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

        private void setDescProgress()
        {
           // mFooterDesc.setText("Текущее значение: "+mValue);
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            mValue = getArguments().getString(EXTRA_STR);
            mUID  = getArguments().getInt(EXTRA_UID);
            mTitle = getArguments().getString(EXTRA_TITLE);
            mHeader = getArguments().getString(EXTRA_HEADER);

            View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_string_dialog,null);

            TextView headerView = (TextView) v.findViewById(R.id.fragStrDlg_header);
            headerView.setText(mHeader);

            final EditText editText = (EditText) v.findViewById(R.id.fragStrDlg_edit);
            editText.setText(mValue);

            mFooterDesc = (TextView) v.findViewById(R.id.fragStrDlg_txt);
            setDescProgress();

            return new AlertDialog.Builder(getActivity())
                    .setTitle(mTitle)
                    .setView(v)
                    .setPositiveButton(
                            android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    mValue = editText.getText().toString();
                                    sendResult(Activity.RESULT_OK);
                                }
                            })
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();
        }



}
