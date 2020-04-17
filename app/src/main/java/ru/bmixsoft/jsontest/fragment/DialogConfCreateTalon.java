package ru.bmixsoft.jsontest.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import net.qiushao.lib.dbhelper.DBFactory;
import net.qiushao.lib.dbhelper.DBHelper;

import java.util.List;

import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.model.Polis;
import ru.bmixsoft.jsontest.model.Talon;
import ru.bmixsoft.jsontest.utils.Utils;

/**
 * Created by Михаил on 13.01.2017.
 */
public class DialogConfCreateTalon extends DialogFragment {

    public static final String EXTRA_SELECT_USER_ID = "ru.bmixsoft.jsontest.fragment.dialogconfcreatetalon.selectcuruser";

    public InterfaceCommunicator interfaceCommunicator;
    private int mCurSelectUser;
    private Talon mTalon;
    private String mCurAction;

    private static final String ACTION_SAVE_EMAIL = "save_email";

    private TextView mTextViewDbg;

    private Polis mCurPolis;

    private DBHelper db;

    private final static String DbgTAG = "DialogConfCreateTalon";

    public interface InterfaceCommunicator {
        void sendRequestCode(int code);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
                 // Instantiate the NoticeDialogListener so we can send events to the host
                interfaceCommunicator = (InterfaceCommunicator) activity;
            } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }

    public static DialogConfCreateTalon newInstance()
    {
        Bundle args = new Bundle();
       // args.putInt(EXTRA_POLIS_ID,  polis_id);
        DialogConfCreateTalon fragment = new DialogConfCreateTalon();
        fragment.setArguments(args);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mCurSelectUser = getArguments().getInt(EXTRA_SELECT_USER_ID);

        db = DBFactory.getInstance(getActivity().getApplicationContext()).getDBHelper(Talon.class);
        List<Object> ls = db.query("id=?", new String[]{"1"}, Talon.class, null);
                if (ls.size() > 0) {
                    mTalon = new Talon();
                    mTalon = (Talon) ls.get(0);
                }

        mCurPolis = Polis.getPolis(mTalon.getPolisId());

        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_polis_edit, null);
        mTextViewDbg = (TextView) v.findViewById(R.id.dlg_coftal_dbg_txt);

        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(v)
                .setTitle("Оформить талон")
                .create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });

        Button btnConfirm = (Button) v.findViewById(R.id.dialog_confirm_talon_ok);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                procAction();
            }
        });

        Button btnCancle = (Button) v.findViewById(R.id.dialog_confirm_talon_cancel);
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResult(0);
                getDialog().cancel();
            }
        });
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mCurAction = ACTION_SAVE_EMAIL;
        return dialog;
    }

    private void procAction(){
        Log.d(DbgTAG,"createTalon-->");
        try {

            Log.d(DbgTAG,"	mCurAction: " +mCurAction);
			RequestParams params;
			params = new RequestParams();
            switch (mCurAction){
                case ACTION_SAVE_EMAIL:
                    params.add("email", mCurPolis.getEmail());
                    break;
            }

        } catch(Exception e){
            Log.d(DbgTAG,"	error: "+e.getMessage());
            Utils.safePrintError(e); // e.printStackTrace();
        }
        Log.d(DbgTAG,"createTalon--<");
    }

    private void sendResult(int resultCode){
        interfaceCommunicator.sendRequestCode(resultCode);
    }

}
