package ru.bmixsoft.jsontest.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import net.qiushao.lib.dbhelper.DBFactory;
import net.qiushao.lib.dbhelper.DBHelper;

import java.util.Date;
import java.util.List;

import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.fragment.dialog.DatePickerFragment;
import ru.bmixsoft.jsontest.model.Polis;
import ru.bmixsoft.jsontest.utils.DateInputMask;
import ru.bmixsoft.jsontest.utils.Utils;

/**
 * Created by Михаил on 11.05.2016.
 */
public class DialogEditPolisFragment extends DialogFragment {

    public static final String EXTRA_POLIS_ID = "com.bmixdev.arduwifiinffo.deviceservice.title";
    public static final String EXTRA_ACTION = "ru.bmixsoft.jsontest.fragment.DialogEditPolisFragment.action";

    private Polis mPolis;
    private DBHelper myDb;

    private TextView textViewInputF;
    private TextView textViewInputI;
    private TextView textViewInputO;
    private TextView textViewInputPolisNum;
    private TextView textViewInputPhone;
    private TextView textViewInputEmail;
    private TextView textViewInputBirthday;
   // private TextView edtBirthday;
    private EditText edtBirthday;

    private static final String DIALOG_DATE = "date";
    private int mAction;
    public static final int actionNew = 1;
    public static final int actionEdit = 0;

    private static final int REQUEST_DATE = 0;

    private  Callback mCallback;

    public interface Callback{
        public void onRefreshFragment();
    }


    public static DialogEditPolisFragment newInstance(String polis_id, int action)
    {
        Bundle args = new Bundle();
        args.putString(EXTRA_POLIS_ID,  polis_id);
        args.putInt(EXTRA_ACTION,  action);
        DialogEditPolisFragment fragment = new DialogEditPolisFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public void setCallback(Callback callback)
    {
        mCallback = callback;
    }

    public void updateBirthday()
    {
        edtBirthday.setText(mPolis.getBirthday());
        textViewInputBirthday.setVisibility(edtBirthday.getText().length() > 0 ? View.VISIBLE : View.INVISIBLE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try {


            final String mPolisId = getArguments().getString(EXTRA_POLIS_ID);
            mAction = getArguments().getInt(EXTRA_ACTION);
            myDb = DBFactory.getInstance(getActivity().getApplicationContext()).getDBHelper(Polis.class);

            List<Object> ls = myDb.query("id=?", new String[]{mPolisId}, Polis.class, null);
            if (ls.size() == 1) {
                mPolis = new Polis();
                mPolis = (Polis) ls.get(0);
            }

            View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_polis_edit, null);
            textViewInputF = (TextView) v.findViewById(R.id.dlg_tv_input_f);
            textViewInputI = (TextView) v.findViewById(R.id.dlg_tv_input_i);
            textViewInputO = (TextView) v.findViewById(R.id.dlg_tv_input_o);
            textViewInputPolisNum = (TextView) v.findViewById(R.id.dlg_tv_input_polis_num);
            textViewInputPhone = (TextView) v.findViewById(R.id.dlg_tv_input_phone);
            textViewInputEmail = (TextView) v.findViewById(R.id.dlg_tv_input_email);
            textViewInputBirthday = (TextView) v.findViewById(R.id.dlg_tv_input_birthday);


            EditText edtNameF = (EditText) v.findViewById(R.id.dlg_edit_polis_f);
            edtNameF.setText(mPolis == null ? "" : mPolis.getLastName());
            textViewInputF.setVisibility((edtNameF.getText().length() > 0) ? View.VISIBLE : View.INVISIBLE);
            edtNameF.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    textViewInputF.setVisibility((s.length() > 0) ? View.VISIBLE : View.INVISIBLE);
                    mPolis.setLastName(s.toString().replace('\n', ' ').trim());
//                getArguments().putSerializable(EXTRA_SERV_TITLE, mTitle);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            EditText edtNameI = (EditText) v.findViewById(R.id.dlg_edit_polis_i);
            edtNameI.setText(mPolis.getMiddleName());
            textViewInputI.setVisibility((edtNameI.getText().length() > 0) ? View.VISIBLE : View.INVISIBLE);
            edtNameI.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    textViewInputI.setVisibility((s.length() > 0) ? View.VISIBLE : View.INVISIBLE);
                    mPolis.setMiddleName(s.toString().replace('\n', ' ').trim());
//                getArguments().putSerializable(EXTRA_SERV_TITLE, mTitle);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            EditText edtNameO = (EditText) v.findViewById(R.id.dlg_edit_polis_o);
            edtNameO.setText(mPolis.getFirstName());
            textViewInputO.setVisibility((edtNameO.getText().length() > 0) ? View.VISIBLE : View.INVISIBLE);
            edtNameO.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    textViewInputO.setVisibility((s.length() > 0) ? View.VISIBLE : View.INVISIBLE);
                    mPolis.setFirstName(s.toString().replace('\n', ' ').trim());
//                getArguments().putSerializable(EXTRA_SERV_TITLE, mTitle);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            EditText edtPolisNum = (EditText) v.findViewById(R.id.dlg_edit_polis_num);
            edtPolisNum.setText(mPolis.getPolusNum());
            textViewInputPolisNum.setVisibility((edtPolisNum.getText().length() > 0) ? View.VISIBLE : View.INVISIBLE);
            edtPolisNum.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    textViewInputPolisNum.setVisibility((s.length() > 0) ? View.VISIBLE : View.INVISIBLE);
                    mPolis.setPolusNum(s.toString().replace('\n', ' ').trim());
//                getArguments().putSerializable(EXTRA_SERV_TITLE, mTitle);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


            EditText edtPhone = (EditText) v.findViewById(R.id.dlg_edit_polis_phone);
            edtPhone.setText(mPolis.getPhoneNumber());
            textViewInputPhone.setVisibility((edtPhone.getText().length() > 0) ? View.VISIBLE : View.INVISIBLE);
            edtPhone.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    textViewInputPhone.setVisibility((s.length() > 0) ? View.VISIBLE : View.INVISIBLE);
                    mPolis.setPhoneNumber(s.toString().replace('\n', ' ').trim());
//                getArguments().putSerializable(EXTRA_SERV_TITLE, mTitle);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


            EditText edtEmail = (EditText) v.findViewById(R.id.dlg_edit_polis_email);
            edtEmail.setText(mPolis.getEmail());
            textViewInputEmail.setVisibility((edtEmail.getText().length() > 0) ? View.VISIBLE : View.INVISIBLE);
            edtEmail.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    textViewInputEmail.setVisibility((s.length() > 0) ? View.VISIBLE : View.INVISIBLE);
                    mPolis.setEmail(s.toString().replace('\n', ' ').trim());
//                getArguments().putSerializable(EXTRA_SERV_TITLE, mTitle);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        /*
        edtBirthday = (TextView) v.findViewById(R.id.dlg_edit_polis_birthday);
        updateBirthday();
        edtBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity()
                            .getSupportFragmentManager();
                Date tmpDate = Utils.strToDate(mPolis.getBirthday(), Utils.dateFrmt);
                if (tmpDate == null) {
                    tmpDate = Utils.Sysdate();
                }
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(tmpDate);
                dialog.setTargetFragment(DialogEditPolisFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });
        */

            edtBirthday = (EditText) v.findViewById(R.id.dlg_edit_polis_birthday);
            updateBirthday();
            new DateInputMask(edtBirthday);

            final AlertDialog dlg = new AlertDialog.Builder(getActivity()).setView(v)
                    .setTitle("Редактирование полиса")
                    .setPositiveButton(android.R.string.ok, null)
                    .create();

            dlg.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {

                    Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    button.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            mPolis.setBirthday(edtBirthday.getText().toString());

                            Boolean wantToCloseDialog = true;
                            if (mPolis.getLastName().isEmpty()) {
                                Utils.msgError(R.string.errorEditPolisNotLastName);
                                wantToCloseDialog = false;
                            }

                            if (mPolis.getLastName().isEmpty()) {
                                Utils.msgError(R.string.errorEditPolisNotMidleName);
                                wantToCloseDialog = false;
                            }

                       /* if (mPolis.getFirstName().isEmpty()) {
                            Utils.msgError(R.string.errorEditPolisNotFirstName);
                            wantToCloseDialog = false;
                        }
                        */

                            if (mPolis.getBirthday().replace(".", "").trim().isEmpty()) {
                                Utils.msgError(R.string.errorEditPolisNotBithday);
                                wantToCloseDialog = false;
                            }

                            if (mPolis.getPolusNum().isEmpty()) {
                                Utils.msgError(R.string.errorEditPolisNotNumPolis);
                                wantToCloseDialog = false;
                            }

                            if (mPolis.getEmail().isEmpty()) {
                                Utils.msgError(R.string.errorEditPolisNotEmail);
                                wantToCloseDialog = false;
                            }

                            if (wantToCloseDialog) {
                                sendResult(Activity.RESULT_OK);
                                dlg.dismiss();
                            }
                        }
                    });
                }
            });

            // Disable the back button
            DialogInterface.OnKeyListener keyListener = new DialogInterface.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode,
                                     KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (mAction == actionNew) {
                            myDb.execSQL("DELETE FROM POLIS WHERE ID = \"" + mPolisId + "\"");
                            if (mCallback != null)
                                mCallback.onRefreshFragment();
                        }
                    }
                    return false;
                }
            };

            dlg.setOnKeyListener(keyListener);

            return dlg;
        }
        catch (Exception e)
        {
            Crashlytics.log(1, "mPolis" ,mPolis.toString());
            Crashlytics.logException(e);
            throw e;
        }
    }

    private void sendResult(int resultCode){

        if (getTargetFragment() == null) return;

        myDb.insertOrReplace(mPolis);


        Intent i = new Intent();
        /*
        i.putExtra(EXTRA_SERV_TITLE, mTitle);
        i.putExtra(EXTRA_SERV_URL, mUrl);
        i.putExtra(EXTRA_SERV_CODE, mCode);
*/
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_DATE) {
            Date date = (Date)data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mPolis.setBirthday(Utils.dateToStr(date, Utils.dateFrmt));
            updateBirthday();
        }
    }

}
