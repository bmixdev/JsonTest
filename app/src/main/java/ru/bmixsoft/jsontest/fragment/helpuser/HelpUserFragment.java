package ru.bmixsoft.jsontest.fragment.helpuser;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;

import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.fragment.options.LibOption;

/**
 * Created by Mike on 29.01.2018.
 */

public class HelpUserFragment extends DialogFragment {

    public static void show(FragmentActivity activity)
    {
        FragmentManager fm = activity.getSupportFragmentManager();
        HelpUserFragment dialog = HelpUserFragment.newInstance();
        //     dialog.setTargetFragment(this, REQUEST_DLGSERV);
        dialog.show(fm, "helpUserFragment");
    }

    public static HelpUserFragment newInstance()
    {
        Bundle args = new Bundle();
        HelpUserFragment fragment = new HelpUserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_help_user_dialog, null);
        HelpUserPageAdapter helpUserPageAdapter = new HelpUserPageAdapter(getActivity(), this, new HelpUserPageAdapter.Callback() {
            @Override
            public void OnDialogDismiss() {
                sendResult(0);
            }
        });
        ViewPager viewPager = (ViewPager) v.findViewById(R.id.viewPagerFragmentHelpUser);
        if (viewPager != null)
        {
            viewPager.setAdapter(helpUserPageAdapter);
        }


        PageIndicator pageIndicator = (CirclePageIndicator) v.findViewById(R.id.indicatorFragmentHelpUser);
        pageIndicator.setViewPager(viewPager);

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

        final Drawable drawable = new ColorDrawable(getActivity().getResources().getColor(R.color.dialog_aplha));
        drawable.setAlpha(100);
        dlg.getWindow().setBackgroundDrawable(drawable);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dlg.getWindow().setLayout(size.x, size.y);

        return dlg;
    }

    private void sendResult(int resultCode){
        //if (getTargetFragment() == null) return;

        //Intent i = new Intent();
        /*
        i.putExtra(EXTRA_SERV_TITLE, mTitle);
        i.putExtra(EXTRA_SERV_URL, mUrl);
        i.putExtra(EXTRA_SERV_CODE, mCode);
        */
        //getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
        LibOption lo =LibOption.getInstance(getActivity().getApplication());
        lo.setOption("showHelpUserLayout", "0");
        dismiss();
    }


}
