package ru.bmixsoft.jsontest.fragment.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.utils.Utils;

/**
 * Created by Михаил on 06.12.2016.
 */
public class DatePickerFragment extends DialogFragment {

    public static final String EXTRA_DATE ="ru.bmixsoft.jsontest.fragment.datepickerfragment.date";

    private Date mDate;
    private DatePicker datePicker;
    private EditText dateEdit;
    private Integer curDate;

    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DATE, date);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(int resultCode) {
        if (getTargetFragment() == null)
            return;
        Intent i = new Intent();
        i.putExtra(EXTRA_DATE, mDate);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, i);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDate = (Date)getArguments().getSerializable(EXTRA_DATE);

        // создание объекта Calendar для получения года, месяца и дня
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_date_dialog,null);

        datePicker = (DatePicker)v.findViewById(R.id.dialog_date_datePicker);
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            public void onDateChanged(DatePicker view, int year, int month, int day) {
                // Преобразование года, месяца и дня в объект Date
                mDate = new GregorianCalendar(year, month, day).getTime();
                curDate = year;
                dateEdit.setText(String.valueOf(curDate));
                // обновление аргумента для сохранения
                // выбранного значения при повороте
                getArguments().putSerializable(EXTRA_DATE, mDate);
            }
        });
        curDate = Integer.valueOf(Utils.dateToStr(mDate,"yyyy"));
        dateEdit = (EditText) v.findViewById(R.id.dialog_date_EdtDate);
        dateEdit.setText(String.valueOf(curDate));

        Button btnMinus = (Button) v.findViewById(R.id.dialog_date_btnMinusDate);

        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeYear(-1);
            }
        });

        /*
        btnMinus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    changeYear(-1);
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                {

                }
                return false;
            }
        });
*/

        Button btnPlus = (Button) v.findViewById(R.id.dialog_date_btnPlusDate);
        /*
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {changeYear(1); }
        });
*/

        btnPlus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    changeYear(1);
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                {

                }
                return false;
            }
        });


        return new AlertDialog.Builder(getActivity())
                .setTitle("Выбор даты")
                .setView(v)
                .setPositiveButton(
                        android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                sendResult(Activity.RESULT_OK);
                            }
                        })
                .create();
    }

    private void changeYear(int value)
    {

        curDate = curDate + value;
        dateEdit.setText(String.valueOf(curDate));
        if (Build.VERSION.SDK_INT >= 23 ) {
            datePicker.updateDate(curDate, datePicker.getMonth(), datePicker.getDayOfMonth());
        }
        else
        {
            datePicker.updateDate(curDate, datePicker.getMonth(), datePicker.getDayOfMonth());
        }

    }

}
