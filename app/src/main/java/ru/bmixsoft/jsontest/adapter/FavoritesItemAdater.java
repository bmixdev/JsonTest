package ru.bmixsoft.jsontest.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import net.qiushao.lib.dbhelper.DBFactory;
import net.qiushao.lib.dbhelper.DBHelper;

import java.util.ArrayList;

import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.model.FavoritItemHelper;
import ru.bmixsoft.jsontest.model.FavoritesDoct;
import ru.bmixsoft.jsontest.model.Polis;
import ru.bmixsoft.jsontest.utils.Utils;

/**
 * Created by Михаил on 22.11.2016.
 */
public class FavoritesItemAdater
        extends ArrayAdapter<FavoritesDoct>
{


    private Activity mContext;
    private ArrayList<FavoritesDoct> mItems;
    private DBHelper myDb;

    private ArrayList<FavoritItemHelper> mFavoritItemHelperArrayList;

    private Callback mCallback;

    public interface Callback{
        void OnChangeFavoritesDoct(FavoritesDoct favoritesDoct);
    }

    public void registerCallBack(Callback callback)
    {
        mCallback = callback;
    }

    public FavoritesItemAdater(Context context, ArrayList<FavoritesDoct> items, Callback callback) {
        super(context, 0, items);
        mContext = (Activity) context;
        mItems = items;
        myDb = DBFactory.getInstance(getContext().getApplicationContext()).getDBHelper(FavoritesDoct.class);
        mFavoritItemHelperArrayList = new ArrayList<FavoritItemHelper>();
        for ( int idx = 0; idx < items.size(); idx++) {
            mFavoritItemHelperArrayList.add(idx, null);
        }
        registerCallBack(callback);
    }

    private void deleteFavoritesDoct(final FavoritesDoct favoritesDoct){
        //if (Utils.isDebugMode())  Utils.d(getContext().getApplicationContext(), favoritesDoct.toString());

        new AlertDialog.Builder(getContext())
                .setTitle("Подтверждение действия")
                .setMessage("Вы действительно хотите удалить избранное?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        remove(favoritesDoct);
                        myDb.refreshDataOnArrayList(mItems, FavoritesDoct.class, true);

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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
//        if (convertView == null){
           convertView = mContext.getLayoutInflater().inflate(R.layout.fragment_favorites_item, parent, false);
 //       }


        final FavoritesDoct item = getItem(position);

        if (item == null) return convertView;

        try {
            final LinearLayout mLayoutHeader = (LinearLayout) convertView.findViewById(R.id.llHeaderFav);
            mLayoutHeader.setBackgroundColor(Utils.getColorHeader());

            final LinearLayout mLayoutHeaderAvalibleTime = (LinearLayout) convertView.findViewById(R.id.llHeaderAvalibleFavTal);

            mLayoutHeaderAvalibleTime.setBackgroundColor(Utils.getColorHeader());
            final TextView mTVHeaderAvalibleFavTal = (TextView) convertView.findViewById(R.id.tvHeaderAvalibleFavTal);

            TextView tvDbg = (TextView) convertView.findViewById(R.id.tvItemFavDbg);
            if (Utils.isDebugMode()) {
                tvDbg.setText(item.toString());
                tvDbg.setVisibility(Utils.isDebugMode() == true ? View.VISIBLE : View.INVISIBLE);
            }

            TextView tvDesc = (TextView) convertView.findViewById(R.id.tvItemFavDesc);
            if (!item.getDoctId().equals("0") && !item.getDoctId().equals("null")) {
                tvDesc.setText(item.getDoctFio());
            } else
            {
                tvDesc.setTextColor(mContext.getResources().getColor(R.color.red));
                tvDesc.setText("Необходимо выбрать мед. специалиста!!!");
            }

            TextView tvPolisInfo = (TextView) convertView.findViewById(R.id.tvItemFavPolisInfo);
            try {
                tvPolisInfo.setText(Polis.getPolis(item.getPolisId()).getPolisText());
            } catch (Exception e) {
                Utils.safePrintError(e);
            }
            TextView tvInfo = (TextView) convertView.findViewById(R.id.tvItemFavInfo);
            tvInfo.setText(item.getInfo());

            //   if (mFavoritItemHelperArrayList.get(position) == null ) {
            final RelativeLayout fragment_container = (RelativeLayout) convertView.findViewById(R.id.fragment_container_fav);
            final FavoritItemHelper favoritItemHelper = new FavoritItemHelper(getContext(), fragment_container, item);
            mFavoritItemHelperArrayList.set(position, favoritItemHelper);
            // }

            CheckBox checkBoxActive = (CheckBox) convertView.findViewById(R.id.chkItemFavActive);
            checkBoxActive.setChecked(item.getIsNeedChkJob() == 1 ? true : false);
            checkBoxActive.setTag(item);
            checkBoxActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    FavoritesDoct fd = (FavoritesDoct) compoundButton.getTag();
                    fd.setIsNeedChkJob(b == true ? 1 : 0);
                    fd.saveToDataBase();
                }
            });

            ImageButton btnSync = (ImageButton) convertView.findViewById(R.id.btnFavoriteItemRefresh);
            btnSync.setTag(position);
            btnSync.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    FavoritesDoct fd = getItem((Integer) v.getTag());
                    if (fd != null && (item.getDoctId().equals("0") && !item.getDoctId().equals("null"))) {
                        Utils.msgWarning("Необходимо выбрать мед. специалиста!!!");
                        return;
                    }

                        //getAvalibleDateTimes(fd, fragment_container, mLayoutHeaderAvalibleTime, mTVHeaderAvalibleFavTal);
                    FavoritItemHelper fih = mFavoritItemHelperArrayList.get((Integer) v.getTag());
                    fih.getAvalibleDateTimes(new FavoritItemHelper.Callback() {
                        @Override
                        public void OnRefresh(int result) {
                            if (result == 1) {
                                mLayoutHeaderAvalibleTime.setBackgroundColor(Utils.getColorHeader());
                                mTVHeaderAvalibleFavTal.setText(getContext().getResources().getString(R.string.msg_list_avalible_talons));
                            } else if (result == 0) {
                                mLayoutHeaderAvalibleTime.setBackgroundColor(getContext().getResources().getColor(R.color.red));
                                mTVHeaderAvalibleFavTal.setText(getContext().getResources().getString(R.string.msg_not_avalible_talons));
                            }
                        }
                    });
                }
            });

            ImageButton btnDelete = (ImageButton) convertView.findViewById(R.id.btnFavoritesItemDelete);
            btnDelete.setTag(position);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    deleteFavoritesDoct(getItem((Integer) v.getTag()));
                }
            });
        }
        catch (Exception e)
        {
            Crashlytics.log(10, "FavoritesItemAdapter.getView", item.toString());
            throw e;
        }
        return convertView;
    }

}
