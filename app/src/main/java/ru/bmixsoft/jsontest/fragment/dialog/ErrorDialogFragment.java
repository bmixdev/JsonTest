package ru.bmixsoft.jsontest.fragment.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.util.Locale;

import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.utils.Utils;

/**
 * Created by Mike on 18.01.2018.
 */

public class ErrorDialogFragment extends DialogFragment {

        public static final String EXTRA_UID ="ru.bmixsoft.jsontest.fragment.dialog.errordialogfragment.uid";
        public static final String EXTRA_STR ="ru.bmixsoft.jsontest.fragment.dialog.errordialogfragment.num";
        public static final String EXTRA_TITLE ="ru.bmixsoft.jsontest.fragment.dialog.errordialogfragment.title";
        public static final String EXTRA_HEADER ="ru.bmixsoft.jsontest.fragment.dialog.errordialogfragment.header";


        private int mUID;
        private String mValue;
        private String mTitle;
        private String mHeader;

        private EditText editText;
        private View mUIView;

        private TextView mFooterDesc;


    private static long getAvailableInternalMemorySize(StatFs stat) {
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    private static long getTotalInternalMemorySize(StatFs stat) {
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    private static StatFs getStatFs() {
        File path = Environment.getDataDirectory();
        return new StatFs(path.getPath());
    }


    public static String addSystemInformation(Context context) {

        StringBuilder message = new StringBuilder();
        message.append("Locale: ").append(Locale.getDefault()).append('\n');
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi;
            pi = pm.getPackageInfo(context.getPackageName(), 0);
            message.append("Version: ").append(pi.versionName).append('\n');
            message.append("Package: ").append(pi.packageName).append('\n');
        } catch (Exception e) {
            Log.e("CustomExceptionHandler", "Error", e);
            message.append("Could not get Version information for ").append(
                    context.getPackageName());
        }
        message.append("Phone Model: ").append(android.os.Build.MODEL)
                .append('\n');
        message.append("Android Version: ")
                .append(android.os.Build.VERSION.RELEASE).append('\n');
        message.append("Board: ").append(android.os.Build.BOARD).append('\n');
        message.append("Brand: ").append(android.os.Build.BRAND).append('\n');
        message.append("Device: ").append(android.os.Build.DEVICE).append('\n');
        message.append("Host: ").append(android.os.Build.HOST).append('\n');
        message.append("ID: ").append(android.os.Build.ID).append('\n');
        message.append("Model: ").append(android.os.Build.MODEL).append('\n');
        message.append("Product: ").append(android.os.Build.PRODUCT)
                .append('\n');
        message.append("Type: ").append(android.os.Build.TYPE).append('\n');
        StatFs stat = getStatFs();
        message.append("Total Internal memory: ")
                .append(getTotalInternalMemorySize(stat)).append('\n');
        message.append("Available Internal memory: ")
                .append(getAvailableInternalMemorySize(stat)).append('\n');
        return message.toString();
    }

    public static void show(Activity activity, String title, String header, String msg)
    {
        FragmentManager fm = activity.getFragmentManager();
        ErrorDialogFragment dialog = ErrorDialogFragment.newInstance(title, header, 0, msg);
        dialog.show(fm, "ErrorDlgFragment");
    }

    public static ErrorDialogFragment newInstance(String title, String header, int uid, String curValue) {
            Bundle args = new Bundle();
            args.putInt(EXTRA_UID, uid);
            args.putString(EXTRA_STR, curValue);
            args.putString(EXTRA_TITLE, title);
            args.putString(EXTRA_HEADER, header);
            ErrorDialogFragment fragment = new ErrorDialogFragment();
            fragment.setArguments(args);
            return fragment;
        }

        private void sendResult(int resultCode) {
            if (getTargetFragment() == null)
                return;

            mValue = editText.getText().toString();
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

            View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_error_dialog,null);

            TextView headerView = (TextView) v.findViewById(R.id.fragErrorDlg_header);
            headerView.setText(mHeader);

            editText = (EditText) v.findViewById(R.id.fragErrorDlg_edit);
            editText.setText(mValue);

            mFooterDesc = (TextView) v.findViewById(R.id.fragErrorDlg_txt);
            setDescProgress();

            Button btnContinue = (Button) v.findViewById(R.id.btnErrorDlgFragmentContinue);
            btnContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendResult(Activity.RESULT_OK);
                    dismiss();
                }
            });

            Button btnSndDeveloper = (Button) v.findViewById(R.id.btnErrorDlgFragmentSndDeveloper);
            btnSndDeveloper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendResult(Activity.RESULT_OK);
                    Utils.sndEmail(getActivity(), mHeader+"\n"+addSystemInformation(getActivity())+ mValue);
                    dismiss();
                }
            });


            return new AlertDialog.Builder(getActivity())
                    .setTitle(mTitle)
                    .setView(v)
                    /*
                    .setPositiveButton(
                            android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                    .setNegativeButton(android.R.string.cancel, null)
                    */
                    .create();
        }



}
