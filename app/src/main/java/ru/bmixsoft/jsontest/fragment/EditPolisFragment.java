package ru.bmixsoft.jsontest.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
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
import ru.bmixsoft.jsontest.adapter.PolisItemAdater;
import ru.bmixsoft.jsontest.model.Polis;
import ru.bmixsoft.jsontest.utils.Utils;

/**
 * Created by Михаил on 22.11.2016.
 */
public class EditPolisFragment extends MyFragment
implements PolisItemAdater.Callback
{

    private static final String TAG ="EditPolisFragment";
    protected static EditPolisFragment instance;
    private GridView mGridView;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    private static final int REQUEST_DLGSERV = 0;
    private DBHelper myDb;

    //callback
    public void OnChangePolis(Polis polis)
    {
        changePolis(polis);
    }

    public static MyFragment getInstance(Context context)
    {
        if (instance == null) {
            synchronized (MainFragment.class) {
                if (instance == null) {
                    instance = new EditPolisFragment();
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
     //   getActivity().setTitle("Мои полисы");
        setRetainInstance(true);
        myDb = DBFactory.getInstance(getActivity().getApplicationContext()).getDBHelper(Polis.class);
    }

    public void changePolis(Polis curPolis)
    {
        FragmentManager fm = getActivity().getSupportFragmentManager();
      //  fm.beginTransaction();
        String newPolisId = myDb.getUID();
        int action;
        if (curPolis == null) {
            Polis polis = new Polis(newPolisId, "", "", "", "", "", "", "");
            myDb.insert(polis);
            action = DialogEditPolisFragment.actionNew;
        }
        else
        {
            newPolisId = curPolis.getId();
            action = DialogEditPolisFragment.actionEdit;
        }

        DialogEditPolisFragment dialog = DialogEditPolisFragment.newInstance(newPolisId, action);
        dialog.setCallback(new DialogEditPolisFragment.Callback() {
            @Override
            public void onRefreshFragment() {
                refreshAdapter();
            }
        });
        dialog.setTargetFragment(this, REQUEST_DLGSERV);
        dialog.show(fm, "addPolisDialog");

        refreshAdapter();
    }

    public synchronized void refreshAdapter()
    {
        try {
            int cntAfter = -1;
            if(mGridView != null && mGridView.getAdapter() != null)
                cntAfter = mGridView.getAdapter().getCount();
            ArrayList<Polis> listPolis = (ArrayList<Polis>) myDb.getArrayList(Polis.class, null);

            PolisItemAdater polisItemAdater = new PolisItemAdater(getActivity(), listPolis, this);
            mGridView.setAdapter(polisItemAdater);
            if (cntAfter !=  -1  && cntAfter != listPolis.size())
                notify();

        } catch (Exception e) {
            Utils.safePrintError(e); // e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        refreshAdapter();
        super.onResume();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_DLGSERV){
         }
        refreshAdapter();


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
                changePolis(null);
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }


}
