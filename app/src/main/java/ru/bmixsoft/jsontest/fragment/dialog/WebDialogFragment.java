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
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ru.bmixsoft.jsontest.R;

/**
 * Created by Mike on 18.01.2018.
 */

public class WebDialogFragment extends DialogFragment {

    public static final String EXTRA_TITLE = WebDialogFragment.class.getPackage().getName() + ".title";
    public static final String EXTRA_URL = WebDialogFragment.class.getPackage().getName() + ".url";

    private String mTitle;
    private String mUrl;

    public static WebDialogFragment newInstance(String title, String url) {
            Bundle args = new Bundle();
            args.putString(EXTRA_TITLE, title);
            args.putString(EXTRA_URL, url);
            WebDialogFragment fragment = new WebDialogFragment();
            fragment.setArguments(args);
            return fragment;
        }



    public static void show(Activity activity, String title, String url)
    {
        FragmentManager fm = ((FragmentActivity) activity).getSupportFragmentManager();
        fm.beginTransaction();

        WebDialogFragment dialog = WebDialogFragment.newInstance(title, url);
        dialog.show(fm, "WebDialogFragment");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mTitle = getArguments().getString(EXTRA_TITLE);
        mUrl = getArguments().getString(EXTRA_URL);

        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_web_dialog,null);

        WebView webView = (WebView) v.findViewById(R.id.fragWebDlgWebView);
        // включаем поддержку JavaScript
        webView.getSettings().setUserAgentString(getActivity().getString(R.string.defaultHttpUserMobileAgent));
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient()); //open urls inside browser
        // указываем страницу загрузки
        webView.loadUrl(mUrl);

        AlertDialog dlg =  new AlertDialog.Builder(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                .setTitle(mTitle)
                .setView(v)
                //.setIcon(R.drawable.ic_dlg_info)
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
