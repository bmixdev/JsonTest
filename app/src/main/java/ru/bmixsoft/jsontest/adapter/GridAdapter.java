package ru.bmixsoft.jsontest.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Михаил on 17.11.2016.
 */
public class GridAdapter extends BaseAdapter {

        private Context mContext;
        private GridAdapter instance;

        // Keep all Images in array
        public String[] mData = { "1", "2", "3", "4","5" };

        // Constructor
        public GridAdapter(Context c) {
            mContext = c;
            instance = this;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mData.length; // длина массива
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return mData[position];
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(mContext);
            textView.setText(mData[position]);
            return textView;
        }
}
