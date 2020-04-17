package ru.bmixsoft.jsontest.tableview.holder;

/**
 * Created by evrencoskun on 23/10/2017.
 */

import android.view.View;
import android.widget.TextView;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

import ru.bmixsoft.jsontest.R;

public class RowHeaderViewHolder extends AbstractViewHolder {
    public final TextView row_header_textview;

    public RowHeaderViewHolder(View p_jItemView) {
        super(p_jItemView);
        row_header_textview = (TextView) p_jItemView.findViewById(R.id.row_header_textview);
    }
}