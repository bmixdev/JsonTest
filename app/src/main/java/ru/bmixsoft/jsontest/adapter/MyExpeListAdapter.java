package ru.bmixsoft.jsontest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pl.openrnd.multilevellistview.ItemInfo;
import pl.openrnd.multilevellistview.MultiLevelListAdapter;
import pl.openrnd.multilevellistview.MultiLevelListView;
import pl.openrnd.multilevellistview.OnItemClickListener;
import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.multiview.data.DataProvider;
import ru.bmixsoft.jsontest.multiview.views.LevelBeamView;

/**
 * Created by Михаил on 17.02.2017.
 */
public class MyExpeListAdapter extends MultiLevelListAdapter {

    public static final String TAG_NAME="name";
    public static final String TAG_VALUME="volume";

    public static final String TAG_AVALIBLE= "avalible";

    private Context mContext;
    public ArrayList<HashMap<String, Object>> mList;

    private Callback mCallback;
    private MyExpeListAdapter instance;

    public interface Callback{
        public void onClickListItem(HashMap<String, Object> map, ItemInfo itemInfo, MyExpeListAdapter adapter);
    }

    public OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        private void showItemDescription(Object object, ItemInfo itemInfo) {
            HashMap<String, Object> map = (HashMap<String, Object>) object;

///            if (map.get(DataProvider.KEY_PARENT_GROUP) == "0" &&(map.get(TAG_AVALIBLE) == null || map.get(TAG_AVALIBLE) == "1"))
                if (mCallback != null) mCallback.onClickListItem(map, itemInfo, instance);
        }

        @Override
        public void onItemClicked(MultiLevelListView parent, View view, Object item, ItemInfo itemInfo) {
            showItemDescription(item, itemInfo);
        }

        @Override
        public void onGroupItemClicked(MultiLevelListView parent, View view, Object item, ItemInfo itemInfo) {
            showItemDescription(item, itemInfo);
        }
    };


    class ViewHolder {
            TextView nameView;
            TextView infoView;
            ImageView arrowView;
            LevelBeamView levelBeamView;
        }

    public  MyExpeListAdapter(Context context, ArrayList<HashMap<String, Object>> list, Callback callback)
    {
        mContext = context;
        mList = list;
        mCallback = callback;
        instance = this;
    }

        @Override
        protected boolean isExpandable(Object object) {
            return DataProvider.isExpandable((HashMap<String, Object>) object);
        }

        @Override
        protected List<?> getSubObjects(Object object) {
            return DataProvider.getInstance(mContext, mList).getSubItems((HashMap<String, Object>)object);
        }

        @Override
        protected View getViewForObject(Object object, View convertView, ItemInfo itemInfo) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.data_item, null);
                viewHolder.infoView = (TextView) convertView.findViewById(R.id.dataItemInfo);
                viewHolder.nameView = (TextView) convertView.findViewById(R.id.dataItemName);
                viewHolder.arrowView = (ImageView) convertView.findViewById(R.id.dataItemArrow);
                viewHolder.levelBeamView = (LevelBeamView) convertView.findViewById(R.id.dataItemLevelBeam);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            HashMap<String, Object> map = (HashMap<String, Object>)  object;


            viewHolder.nameView.setText((String) map.get(TAG_NAME));
            //viewHolder.infoView.setText(getItemInfoDsc(itemInfo));
            viewHolder.infoView.setText((String) map.get(TAG_VALUME));

            if (itemInfo.isExpandable()/* && this.mAlwaysExpandend*/) {
                viewHolder.arrowView.setVisibility(View.VISIBLE);
                viewHolder.arrowView.setImageResource(itemInfo.isExpanded() ?
                        R.drawable.arrow_up : R.drawable.arrow_down);
            } else {
                viewHolder.arrowView.setVisibility(View.GONE);
            }

            viewHolder.levelBeamView.setLevel(itemInfo.getLevel());

            return convertView;
        }

    public void refresh()
    {
        setDataItems(DataProvider.getInstance(mContext, mList).getInitialItems());
    }

}
