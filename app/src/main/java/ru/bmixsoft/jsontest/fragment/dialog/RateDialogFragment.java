package ru.bmixsoft.jsontest.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.fragment.options.LibOption;

/**
 * Created by Mike on 02.02.2018.
 */

public class RateDialogFragment extends DialogFragment {

    public static void show(FragmentActivity activity)
    {
        FragmentManager fm = activity.getSupportFragmentManager();
        RateDialogFragment dialog = RateDialogFragment.newInstance();
        //     dialog.setTargetFragment(this, REQUEST_DLGSERV);
        dialog.show(fm, "rateFragment");
    }

    public static RateDialogFragment newInstance()
    {
        Bundle args = new Bundle();
        RateDialogFragment fragment = new RateDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static void rate(Context context)
    {
        final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_rate_dialog, null);

        AlertDialog dlg = new AlertDialog.Builder(getActivity()).setView(v)
                //        .setTitle(R.string.titleFragmentDialogHelpUser)
        /*
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK);
                    }
                })
                */
                .create();

        Button btnAfter = (Button) v.findViewById(R.id.btnRateFragmentAfter);
        btnAfter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LibOption.setOption(getActivity(), "showModeRateDialog", 1);
                sendResult(1);
            }
        });


        Button btnNo = (Button) v.findViewById(R.id.btnRateFragmentNo);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LibOption.setOption(getActivity(), "showModeRateDialog", 2);
                sendResult(2);
            }
        });


        Button btnRate = (Button) v.findViewById(R.id.btnRateFragmentRate);
        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LibOption.setOption(getActivity(), "showModeRateDialog", 2);
                rate(getActivity());
                sendResult(2);
            }
        });

        // обработчик нажатия кнопки назад
        DialogInterface.OnKeyListener keyListener = new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if( keyCode == KeyEvent.KEYCODE_BACK)
                {
                    sendResult(0);
                }
                return false;
            }
        };

        dlg.setOnKeyListener(keyListener);

        /*
        final Drawable drawable = new ColorDrawable(getActivity().getResources().getColor(R.color.dialog_aplha));
        drawable.setAlpha(100);
        dlg.getWindow().setBackgroundDrawable(drawable);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dlg.getWindow().setLayout(size.x, size.y);
        */

        return dlg;
    }

    private void sendResult(int resultCode){
      //  LibOption lo =LibOption.getInstance(getActivity().getApplication());
       // lo.setOption("showHelpUserLayout", "0");
        dismiss();
    }


}
