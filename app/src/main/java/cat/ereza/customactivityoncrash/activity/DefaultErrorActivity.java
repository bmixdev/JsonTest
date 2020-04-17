/*
 * Copyright 2014-2017 Eduard Ereza Martínez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cat.ereza.customactivityoncrash.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.FirebaseApp;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.config.CaocConfig;
import ru.bmixsoft.jsontest.BuildConfig;
import ru.bmixsoft.jsontest.R;

public final class DefaultErrorActivity extends AppCompatActivity {

    @SuppressLint("PrivateResource")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //This is needed to avoid a crash if the developer has not specified
        //an app-level theme that extends Theme.AppCompat
        TypedArray a = obtainStyledAttributes(R.styleable.AppCompatTheme);
        if (!a.hasValue(R.styleable.AppCompatTheme_windowActionBar)) {
            setTheme(R.style.Theme_AppCompat_Light_DarkActionBar);
        }
        a.recycle();

        setContentView(R.layout.customactivityoncrash_default_error_activity);

        if (! BuildConfig.DEBUG_MODE) {
            Exception e = new Exception(CustomActivityOnCrash.getStackTraceFromIntent(getIntent()));
            FirebaseApp.initializeApp(getApplicationContext());
            Crashlytics.logException(e);
        }

        //Close/restart button logic:
        //If a class if set, use restart.
        //Else, use close and just finish the app.
        //It is recommended that you follow this logic if implementing a custom error activity.
        Button restartButton = (Button) findViewById(R.id.customactivityoncrash_error_activity_restart_button);

        final CaocConfig config = CustomActivityOnCrash.getConfigFromIntent(getIntent());

        if (config == null) {
            //This should never happen - Just finish the activity to avoid a recursive crash.
            finish();
            return;
        }

        if (config.isShowRestartButton() && config.getRestartActivityClass() != null) {
            restartButton.setText(R.string.customactivityoncrash_error_activity_restart_app);
            restartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomActivityOnCrash.restartApplication(DefaultErrorActivity.this, config);
                }
            });
        } else {
            restartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomActivityOnCrash.closeApplication(DefaultErrorActivity.this, config);
                }
            });
        }

        Button moreInfoButton = (Button) findViewById(R.id.customactivityoncrash_error_activity_more_info_button);

        if (config.isShowErrorDetails()) {
            moreInfoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //We retrieve all the error data and show it

                    AlertDialog dialog = new AlertDialog.Builder(DefaultErrorActivity.this)
                            .setTitle(R.string.customactivityoncrash_error_activity_error_details_title)
                            .setMessage(CustomActivityOnCrash.getAllErrorDetailsFromIntent(DefaultErrorActivity.this, getIntent()))
                            .setPositiveButton(R.string.customactivityoncrash_error_activity_error_details_close, null)
                            .setNeutralButton(R.string.customactivityoncrash_error_activity_error_details_copy,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            copyErrorToClipboard();
                                        }
                                    })
                            .show();
                    TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                    if (textView != null) {
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.customactivityoncrash_error_activity_error_details_text_size));
                    }
                }
            });
        } else {
            moreInfoButton.setVisibility(View.GONE);
        }

        final Button sndMailButton = (Button) findViewById(R.id.customactivityoncrash_error_activity_more_info_button_sndmail);
        sndMailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DefaultErrorActivity.this.sendErrorToEmail(CustomActivityOnCrash.getAllErrorDetailsFromIntent(DefaultErrorActivity.this, getIntent()));
            }
        });


        Integer defaultErrorActivityDrawableId = config.getErrorDrawable();
        ImageView errorImageView = (ImageView) findViewById(R.id.customactivityoncrash_error_activity_image);

        if (defaultErrorActivityDrawableId != null) {
            errorImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), defaultErrorActivityDrawableId, getTheme()));
        }
    }

    private void copyErrorToClipboard() {
        String errorInformation = CustomActivityOnCrash.getAllErrorDetailsFromIntent(DefaultErrorActivity.this, getIntent());

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        //Are there any devices without clipboard...?
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText(getString(R.string.customactivityoncrash_error_activity_error_details_clipboard_label), errorInformation);
            clipboard.setPrimaryClip(clip);
            try {
                Toast.makeText(DefaultErrorActivity.this, R.string.customactivityoncrash_error_activity_error_details_copied, Toast.LENGTH_SHORT).show();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void sendErrorToEmail(String errDescr){
        Intent sendIntent = new Intent(
                Intent.ACTION_SEND);
        String subject = "В приложение возникла ошибка. Исправьте ее!";
        StringBuilder body = new StringBuilder("Error_stack_trace:");
        body.append('\n').append('\n');
        body.append(errDescr).append('\n')
                .append('\n');
        // sendIntent.setType("text/plain");
        sendIntent.setType("message/rfc822");
        sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.email_developer)});
        sendIntent.putExtra(Intent.EXTRA_TEXT, body.toString());
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendIntent.setType("message/rfc822");
        DefaultErrorActivity.this.startActivity(sendIntent);
        System.exit(1);
    }
}
