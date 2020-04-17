package ru.bmixsoft.jsontest.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import net.qiushao.lib.dbhelper.DBFactory;
import net.qiushao.lib.dbhelper.DBHelper;

import java.util.ArrayList;

import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.activity.GetTalonActivity;
import ru.bmixsoft.jsontest.adapter.FavoritesItemAdater;
import ru.bmixsoft.jsontest.model.FavoritesDoct;
import ru.bmixsoft.jsontest.model.Polis;
import ru.bmixsoft.jsontest.utils.Utils;

/**
 * Created by Михаил on 22.11.2016.
 */
public class FavoritesDoctFragment extends MyFragment
    implements FavoritesItemAdater.Callback
{
    private static final String TAG ="FavoritesDoctFragment";
    protected static FavoritesDoctFragment instance;
    private GridView mGridView;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    private static final int REQUEST_DLGSERV = 0;
    private DBHelper myDb;

    private final static int REQUEST_GET_FAVORITES = 10;


    //callback
    public void OnChangeFavoritesDoct(FavoritesDoct favoritesDoct)
    {
        refreshAdapter();
    }


    public static MyFragment getInstance(Context context)
    {
        if (instance == null) {
            synchronized (MainFragment.class) {
                if (instance == null) {
                    instance = new FavoritesDoctFragment();
                }
            }
        }
        else{

        }
        return instance;
    }


    @TargetApi(14)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActivity().setTitle("...");
        setRetainInstance(true);

        myDb = DBFactory.getInstance(getActivity().getApplicationContext()).getDBHelper(FavoritesDoct.class);

    }

    public synchronized void refreshAdapter()
    {

        try {

            ArrayList<FavoritesDoct> listFavorites = (ArrayList<FavoritesDoct>) myDb.getArrayList(FavoritesDoct.class, null);

            FavoritesItemAdater favoritesItemAdater = new FavoritesItemAdater(getActivity(), listFavorites, this);
            mGridView.setAdapter(favoritesItemAdater);

        } catch (Exception e) {
            Utils.safePrintError(e); // e.printStackTrace();
        }

    }

    @TargetApi(14)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stdgrid_list, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.std_swipe_container);
        //отключить свайп
        mSwipeRefreshLayout.setEnabled(false);

        mGridView = (GridView) v.findViewById(R.id.stdGridView);
        refreshAdapter();

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_menu_std, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Utils.viberate(mActivity, 50);
        int idx = item.getItemId();
        switch (idx) {
            case R.id.menu_std_add:
                long cntPolis = myDb.size(Polis.class);
                if (cntPolis != 0) {

                    Intent i = new Intent(getActivity(), GetTalonActivity.class);
                i.putExtra(GetTalonActivity.TAG_ACTION, GetTalonActivity.actionAddFavorites);
                startActivityForResult(i, REQUEST_GET_FAVORITES);
                } else {
                    Utils.msgWarning(getString(R.string.errorCreateNewTalonNotPolise));
                }

                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( requestCode == REQUEST_GET_FAVORITES)
        {
            refreshAdapter();
        }
    }


}
