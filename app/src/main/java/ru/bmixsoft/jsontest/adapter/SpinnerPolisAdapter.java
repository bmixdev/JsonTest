package ru.bmixsoft.jsontest.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.qiushao.lib.dbhelper.DBFactory;
import net.qiushao.lib.dbhelper.DBHelper;

import java.util.ArrayList;

import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.model.Polis;

/**
 * Created by Михаил on 05.12.2016.
 */

public class SpinnerPolisAdapter extends ArrayAdapter<Polis>{

    // Your sent context
    private Context context;
    // Your custom values for the spinner (User)
    private Polis[] values;

    private ArrayList<Polis> mPolisList;

    public SpinnerPolisAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
        //this.values = values;

        DBHelper db = DBFactory.getInstance(context.getApplicationContext()).getDBHelper(Polis.class);
        mPolisList = (ArrayList<Polis>) db.getArrayList(Polis.class, null);
    }

    public int getCount(){
        return mPolisList.size();
    }

    public Polis getItem(int position){
        return mPolisList.get(position);
    }
    public int getPositionByPolisId(String polisId){
        //Polis polis = Polis.getPolis(polisId);
        int idx = -1;
        for (Polis p: mPolisList
             ) {
            idx = idx + 1;
            if (p.getId().equals(polisId))
            {
                break;
            }

        }
        return idx; //mPolisList.indexOf(polis);
    }


    public long getItemId(int position){
        return position;
    }

    public View getCustomView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
        View row = layoutInflater.inflate(R.layout.row_spinner, null);
        TextView textView = (TextView) row.findViewById(R.id.row_spinner_title);
        Polis polis = mPolisList.get(position);
        String polisText = polis.getPolisText();
        textView.setText(polisText);

        return row;
    }

    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
     return  getCustomView(position, convertView, parent);
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return  getCustomView(position, convertView, parent);
    }
}