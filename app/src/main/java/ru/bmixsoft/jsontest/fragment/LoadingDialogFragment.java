package ru.bmixsoft.jsontest.fragment;

/**
 * Created by Михаил on 19.01.2017.
 */

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

public class LoadingDialogFragment extends DialogFragment {

    private String mMessage;
    private Context mContext;
    private FragmentManager mFragmentManager;
    private ProgressDialog mProgressDialog;


    public LoadingDialogFragment() {
   //     this.message = message;
    }

    public static LoadingDialogFragment newInstance(FragmentManager fragmentManager, Context context, String message) {
        LoadingDialogFragment fragment = new LoadingDialogFragment();
        fragment.mMessage = message;
        fragment.mContext = context;
        fragment.mFragmentManager = fragmentManager;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage(mMessage);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(true);
        return mProgressDialog;
    }

    public void show()
    {
        show(mFragmentManager, mMessage);
    }

    public void setMessage(String message)
    {
        mProgressDialog.setMessage(message);
    }

}