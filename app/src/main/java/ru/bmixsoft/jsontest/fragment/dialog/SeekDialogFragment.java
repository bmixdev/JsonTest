package ru.bmixsoft.jsontest.fragment.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import ru.bmixsoft.jsontest.R;

/**
 * Created by Mike on 18.01.2018.
 */

public class SeekDialogFragment extends DialogFragment {

        public static final String EXTRA_UID ="ru.bmixsoft.jsontest.fragment.seekdialogfragment.uid";
        public static final String EXTRA_NUM ="ru.bmixsoft.jsontest.fragment.seekdialogfragment.num";
        public static final String EXTRA_MAX_VALUE ="ru.bmixsoft.jsontest.fragment.seekdialogfragment.maxvalue";
        public static final String EXTRA_TITLE ="ru.bmixsoft.jsontest.fragment.seekdialogfragment.title";
        public static final String EXTRA_HEADER ="ru.bmixsoft.jsontest.fragment.seekdialogfragment.header";


    private int mUID;
        private int mSeekValue;
        private int mMaxValue;
        private String mTitle;
        private String mHeader;

        private View mUIView;

        private TextView mProgressDesc;

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mSeekValue = progress;
            setDescProgress();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    public static SeekDialogFragment newInstance(String title, String header, int uid, int curValue, int maxValue) {
            Bundle args = new Bundle();
            args.putInt(EXTRA_UID, uid);
            args.putInt(EXTRA_NUM, curValue);
            args.putInt(EXTRA_MAX_VALUE, maxValue);
            args.putString(EXTRA_TITLE, title);
            args.putString(EXTRA_HEADER, header);
            SeekDialogFragment fragment = new SeekDialogFragment();
            fragment.setArguments(args);
            return fragment;
        }

        private void sendResult(int resultCode) {
            if (getTargetFragment() == null)
                return;
            Intent i = new Intent();
            i.putExtra(EXTRA_UID, mUID);
            i.putExtra(EXTRA_NUM, mSeekValue);
            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
        }

        private void setDescProgress()
        {
            mProgressDesc.setText("Текущее значение: "+String.valueOf(mSeekValue));
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            mSeekValue = getArguments().getInt(EXTRA_NUM);
            mUID  = getArguments().getInt(EXTRA_UID);
            mMaxValue = getArguments().getInt(EXTRA_MAX_VALUE);
            mTitle = getArguments().getString(EXTRA_TITLE);
            mHeader = getArguments().getString(EXTRA_HEADER);

            View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_seek_dialog,null);

            TextView headerView = (TextView) v.findViewById(R.id.fragSeekDlg_header);
            headerView.setText(mHeader);

            SeekBar seekBar = (SeekBar) v.findViewById(R.id.fragSeekDlg_seek);
            seekBar.setMax(mMaxValue);
            seekBar.setProgress(mSeekValue);
            seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

            mProgressDesc = (TextView) v.findViewById(R.id.fragSeekDlg_txt);
            setDescProgress();

            return new AlertDialog.Builder(getActivity())
                    .setTitle(mTitle)
                    .setView(v)
                    .setPositiveButton(
                            android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    sendResult(Activity.RESULT_OK);
                                }
                            })
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();
        }



}
