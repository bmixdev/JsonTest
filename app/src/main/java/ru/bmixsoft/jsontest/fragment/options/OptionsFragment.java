package ru.bmixsoft.jsontest.fragment.options;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import net.qiushao.lib.dbhelper.DBFactory;

import java.util.ArrayList;
import java.util.HashMap;

import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.fragment.MainFragment;
import ru.bmixsoft.jsontest.fragment.MyFragment;
import ru.bmixsoft.jsontest.fragment.dialog.SeekDialogFragment;
import ru.bmixsoft.jsontest.fragment.dialog.StrDialogFragment;
import ru.bmixsoft.jsontest.fragment.dialog.TimePeriodDialogFragment;
import ru.bmixsoft.jsontest.fragment.helpuser.HelpUserDialogFragment;
import ru.bmixsoft.jsontest.service.PollService;
import ru.bmixsoft.jsontest.sqlviewer.ActivitySqlViewer;
import ru.bmixsoft.jsontest.utils.ColorPicker;
import ru.bmixsoft.jsontest.utils.PermissionsHelper;
import ru.bmixsoft.jsontest.utils.TestHelper;
import ru.bmixsoft.jsontest.utils.UpdateHelper;
import ru.bmixsoft.jsontest.utils.Utils;

/**
 * Created by Михаил on 18.11.2016.
 */
public class OptionsFragment extends MyFragment {


    private int mPickedColor = Color.WHITE;

    private static final String DIALOG_SEEK = "dialog_seek";
    private static final int REQUEST_SEEK_VALUE = 101;

    private static final String DIALOG_STR = "dialog_str";
    private static final int REQUEST_STR_VALUE = 102;

    private static final String DIALOG_TIME_PERIOD = "dialog_time_period";
    private static final int REQUEST_TIMEPERIOD_VALUE = 103;


    private int cntClickToDbgMode = 0;

    protected static MyFragment instance;

    private UpdateHelper mUpdateHelper;

    private ArrayList<Option> mOptionArrayList;
    private LibOption mLibOption;

    private HashMap<Integer, View> mapOptionView;

    public static MyFragment getInstance(Context context) {
        if (instance == null) {
            synchronized (MainFragment.class) {
                if (instance == null) {
                    instance = new OptionsFragment();
                }
            }
        }
        return instance;
    }


    // Custom method to get the screen width in pixels
    public Point getScreenSize() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        //Display dimensions in pixels
        display.getSize(size);
        return size;
    }

    // Custom method to get status bar height in pixels
    public int getStatusBarHeight() {
        int height = 0;
        int resourceId = getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = getContext().getResources().getDimensionPixelSize(resourceId);
        }
        return height;
    }

    @TargetApi(14)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActivity().setTitle("Настройки");
        setRetainInstance(true);
        mLibOption = new LibOption(getActivity().getApplication());
        mOptionArrayList = mLibOption.initOptions();
        mapOptionView = new HashMap<Integer, View>();
    }

    @TargetApi(14)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mOptionArrayList = mLibOption.fillOptions();

        ScrollView mainScrollView = new ScrollView(getActivity());
        ScrollView.LayoutParams sp = new ScrollView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mainScrollView.setLayoutParams(sp);
        LinearLayout mainLayout = new LinearLayout(getActivity());
        mainLayout.setPadding(Utils.dpToPx(getActivity(), 4), Utils.dpToPx(getActivity(), 14), Utils.dpToPx(getActivity(), 4), 0);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        boolean isOnDbgMode = Utils.isDebugMode();
        if (mOptionArrayList != null && mOptionArrayList.size() > 0) {
            for (final Option option : mOptionArrayList) {
                if (option.getIsVisible() == 0 && isOnDbgMode != true) continue;
                LinearLayout layout = new LinearLayout(getActivity(), null, R.style.ShapeNormal);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layout.setOrientation(LinearLayout.HORIZONTAL);
                layout.setPadding(10, 3, 10, 5);
                layout.setMinimumHeight(200);
                layout.setLayoutParams(layoutParams);
                layout.setBackground(getResources().getDrawable(R.drawable.card_background));
                LinearLayout.LayoutParams layoutParamsTv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                // layout.setBackgroundColor(R.color.dark_grey);
                TextView tv = new TextView(getActivity());
                layoutParamsTv.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                layoutParamsTv.weight = 2;
                tv.setTextColor(getResources().getColor(android.R.color.black));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

                tv.setLayoutParams(layoutParamsTv);
                tv.setText(option.getName());
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cntClickToDbgMode++;
                        if (cntClickToDbgMode > 20) {
                            Utils.d("Разблокирован режим разработчика");
                            Option optionDbg = mLibOption.getOption("onDebugMode");
                            optionDbg.setIsVisible(1);
                            mLibOption.setOption(optionDbg.getCode(), "0");
                            mLibOption.setVisible(optionDbg.getCode(), true);
                            FragmentActivity activity = getActivity();
                           // if (activity instanceof MainActivityNew) {
                           //     ((MainActivityNew) activity).refreshUI();
                           // }
                        }
                    }
                });
                layout.addView(tv);

                LinearLayout.LayoutParams layoutParamsBtn = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                layoutParamsBtn.gravity = Gravity.CENTER;
                layoutParamsBtn.weight = 6;

                switch (option.getTypeValue()) {
                    case LibOption.typeBoolean: {
                        Switch switchButton = new Switch(getActivity());
                        switchButton.setLayoutParams(layoutParamsBtn);
                        switchButton.setPadding(2, 2, 2, 2);
                        switchButton.setTag(option.getId());
                        switch (option.getCode().toString()) {

                            case "onRunService": {
                                switchButton.setChecked(PollService.isServiceAlarmOn(getActivity()));
                                //Utils.d("onRunService - 0");
                                break;
                            }
                            default:
                                switchButton.setChecked(option.getValue().equals("0") ? false : true);
                        }
                        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                Option opt = LibOption.getInstance(getActivity().getApplicationContext()).getOptionById((int) buttonView.getTag());
                                switch (opt.getCode().toString()) {

                                    case "onRunService": {
                                        PollService.setServiceAlarm(getActivity(), isChecked);
                                        //      Utils.d("onRunService - 1");
                                        break;
                                    }
                                    default:

                                }
                                opt.setValue(isChecked == true ? "1" : "0");
                                mLibOption.refresh();

                            }
                        });
                        layout.addView(switchButton);
                        break;
                    }

                    case LibOption.typeString:
                        TextView textView = new TextView(getActivity());
                        textView.setLayoutParams(layoutParamsBtn);
                        textView.setPadding(2, 2, 2, 2);
                        textView.setText(option.getValue());
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showStrDlg(option.getName(), "", option.getId(), option.getValue());
                            }
                        });
                        layout.addView(textView);
                        mapOptionView.put(option.getId(), textView);
                        break;

                    case LibOption.typeInt:
                        final TextView textViewInt = new TextView(getActivity());
                        textViewInt.setLayoutParams(layoutParamsBtn);
                        textViewInt.setPadding(2, 2, 2, 2);
                        textViewInt.setText(option.getValue());
                        textViewInt.setOnClickListener(new View.OnClickListener() {
                                                           @Override
                                                           public void onClick(View view) {
                                                               String headerOption = "";
                                                               Integer maxValue = 1024;
                                                               if (option.getCode().equals("onIntervalRun")) {
                                                                   headerOption = "Периодичность запуска сервиса, в минутах";
                                                                   maxValue = 60;
                                                               }
                                                               showSeekDlg(option.getName(), headerOption, option.getId(), Integer.valueOf(option.getValue()), maxValue);
                                                           }
                                                       }
                        );
                        layout.addView(textViewInt);
                        mapOptionView.put(option.getId(), textViewInt);
                        break;
                    case LibOption.typeTimeInterval:
                        final TextView textViewTimeInterval = new TextView(getActivity());
                        textViewTimeInterval.setLayoutParams(layoutParamsBtn);
                        textViewTimeInterval.setPadding(2, 2, 2, 2);
                        textViewTimeInterval.setText(option.getValue());
                        textViewTimeInterval.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        showTimePeriodDlg(option.getName(), "", option.getId(), option.getValue());
                                                                    }
                                                                }
                        );
                        layout.addView(textViewTimeInterval);
                        mapOptionView.put(option.getId(), textViewTimeInterval);
                        break;
                    case LibOption.typeButton: {
                        Button btn = new Button(getActivity());
                        btn.setTextSize(8);
                        btn.setBackground(getResources().getDrawable(R.drawable.bkg));
                        layoutParamsBtn.setMargins(5, 5, 20, 5);
                        btn.setLayoutParams(layoutParamsBtn);
                        btn.setTag(option.getId());
                        btn.setText(option.getValue());
                        btn.setPadding(4, 4, 20, 4);

                        switch (option.getCode()) {
                            case "dropSqlLite": {
                                // btn.setBackgroundColor(getResources().getColor(android.R.color.background_dark));

                                btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        new AlertDialog.Builder(getContext())
                                                .setTitle("Подтверждение действия")
                                                .setMessage("Осторожно!!!\nКеш программы будет безвозвратно удален!!!\nВыполнение программы будет завершено!")
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // continue with delete
                                                        DBFactory.getInstance(getActivity().getApplicationContext()).dropDataBase();
                                                        mLibOption = new LibOption(getActivity().getApplicationContext());
                                                        mOptionArrayList = mLibOption.initOptions();
                                                        //((SingleFragmentActivity) getActivity()).restartActivity();
                                                        getActivity().finish();

                                                    }
                                                })
                                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // do nothing
                                                    }
                                                })
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .create()
                                                .show();
                                    }
                                });
                            }
                            break;
                            case "colorHeader": {

                                //btn.setText("");
                                btn.setBackgroundColor(Utils.getColorHeader());
                                // Set an click listener for Button widget
                                btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        final Button btnView = (Button) view;
                                        // Get a GridView object from ColorPicker class
                                        GridView gv = (GridView) ColorPicker.getColorPicker(getContext());

                                        // Initialize a new AlertDialog.Builder object
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                        // Set the alert dialog content to GridView (color picker)
                                        builder.setView(gv);

                                        // Initialize a new AlertDialog object
                                        final AlertDialog dialog = builder.create();

                                        // Show the color picker window
                                        dialog.show();

                                        // Set the color picker dialog size
                                        dialog.getWindow().setLayout(
                                                getScreenSize().x - 20,
                                                getScreenSize().y - getStatusBarHeight() - 20);

                                        // Set an item click listener for GridView widget
                                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                // Get the pickedColor from AdapterView
                                                mPickedColor = (int) parent.getItemAtPosition(position);

                                                // Set the layout background color as picked color
                                                Option opt = LibOption.getInstance(getActivity().getApplicationContext()).getOptionById((int) btnView.getTag());
                                                opt.setValue(String.valueOf(mPickedColor));
                                                btnView.setBackgroundColor(mPickedColor);
                                                //не буду менять актионбар

                                                //AppCompatActivity activity = (AppCompatActivity) getActivity();
                                                //activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(mPickedColor));

                                                mLibOption.refresh();

                                                // close the color picker
                                                dialog.dismiss();
                                            }
                                        });
                                    }
                                });

                            }
                            break;
                            case "openSqlViewer":
                                //btn.setText("->");
                                // Set an click listener for Button widget
                                btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // Intent serverIntent = new Intent(getActivity(), SQLActivity.class);
                                        Intent serverIntent = new Intent(getActivity(), ActivitySqlViewer.class);
                                        startActivity(serverIntent);
                                    }
                                });
                                break;
                            case "updateApp":
                                //btn.setText("->");
                                // Set an click listener for Button widget
                                btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        /*
                                        if (shouldAskPermissions()) {
                                            askPermissions();
                                        }

                                        UpdateApp atualizaApp = new UpdateApp();
                                        atualizaApp.setContext(getActivity());
                                        atualizaApp.execute("");
                                        */
                                        PermissionsHelper.verifyStoragePermissions(getActivity());
                                        if (mUpdateHelper == null) {
                                            mUpdateHelper = new UpdateHelper(getActivity(), null);
                                            mUpdateHelper.updateVersion();
                                        }
                                    }
                                });
                                break;
                            case "btnTest":
                               // btn.setText("->");
                                // Set an click listener for Button widget
                                btn.setOnClickListener(new View.OnClickListener() {
                                                           @Override
                                                           public void onClick(View view) {
                                                               TestHelper.exec(getActivity());
                                                           }
                                                       }
                                );
                                break;

                            case "btnShowHelpUser":
                                //btn.setText("->");
                                // Set an click listener for Button widget
                                btn.setOnClickListener(new View.OnClickListener() {
                                                           @Override
                                                           public void onClick(View view) {
                                                               HelpUserDialogFragment.show(getActivity());
                                                           }
                                                       }
                                );
                                break;
                            case "shareLinkToApp":
                                btn.setOnClickListener(new View.OnClickListener() {
                                                           @Override
                                                           public void onClick(View view) {
                                                               UpdateHelper.shareApp(getActivity());
                                                           }
                                                       }
                                );
                                break;
                        }
                        layout.addView(btn);
                        break;
                    }

                }
                mainLayout.addView(layout);
            }
        }
        mainScrollView.addView(mainLayout);
        return mainScrollView;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_SEEK_VALUE) {
            int curSeek = data.getIntExtra(SeekDialogFragment.EXTRA_NUM, 10);
            int curOptionId = data.getIntExtra(SeekDialogFragment.EXTRA_UID, -1);
            if (curOptionId != -1) {
                mLibOption.setOption(mLibOption.getOptionById(curOptionId).getCode(), String.valueOf(curSeek));
                if (mapOptionView != null) {

                    if (mapOptionView.containsKey((Integer) curOptionId)) {
                        TextView curView = (TextView) mapOptionView.get((Integer) curOptionId);
                        curView.setText(String.valueOf(curSeek));
                    }
                }

            }
        }

        if (requestCode == REQUEST_STR_VALUE) {
            String curStr = data.getStringExtra(StrDialogFragment.EXTRA_STR);
            int curOptionId = data.getIntExtra(StrDialogFragment.EXTRA_UID, -1);
            if (curOptionId != -1) {
                mLibOption.setOption(mLibOption.getOptionById(curOptionId).getCode(), curStr);
                if (mapOptionView != null) {

                    if (mapOptionView.containsKey((Integer) curOptionId)) {
                        TextView curView = (TextView) mapOptionView.get((Integer) curOptionId);
                        curView.setText(curStr);
                    }
                }

            }
        }


        if (requestCode == REQUEST_TIMEPERIOD_VALUE) {
            String curStr = data.getStringExtra(TimePeriodDialogFragment.EXTRA_STR);
            int curOptionId = data.getIntExtra(TimePeriodDialogFragment.EXTRA_UID, -1);
            if (curOptionId != -1) {
                mLibOption.setOption(mLibOption.getOptionById(curOptionId).getCode(), curStr);
                if (mapOptionView != null) {

                    if (mapOptionView.containsKey((Integer) curOptionId)) {
                        TextView curView = (TextView) mapOptionView.get((Integer) curOptionId);
                        curView.setText(curStr);
                    }
                }

            }
        }
    }


    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }


    private void showSeekDlg(String title, String header, int uid, int value, int maxValue) {
        FragmentManager fm = getActivity()
                .getSupportFragmentManager();
        SeekDialogFragment dialog = SeekDialogFragment.newInstance(title, header, uid, value, maxValue);
        dialog.setTargetFragment(this, REQUEST_SEEK_VALUE);
        dialog.show(fm, DIALOG_SEEK);
    }



    private void showStrDlg(String title, String header, int uid, String value) {
        FragmentManager fm = getActivity()
                .getSupportFragmentManager();
        StrDialogFragment dialog = StrDialogFragment.newInstance(title, header, uid, value);
        dialog.setTargetFragment(this, REQUEST_STR_VALUE);
        dialog.show(fm, DIALOG_STR);
    }


    private void showTimePeriodDlg(String title, String header, int uid, String value) {
        FragmentManager fm = getActivity()
                .getSupportFragmentManager();
        TimePeriodDialogFragment dialog = TimePeriodDialogFragment.newInstance(title, header, uid, value);
        dialog.setTargetFragment(this, REQUEST_TIMEPERIOD_VALUE);
        dialog.show(fm, DIALOG_TIME_PERIOD);
    }
}
