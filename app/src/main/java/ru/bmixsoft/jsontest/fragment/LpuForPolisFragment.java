package ru.bmixsoft.jsontest.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.qiushao.lib.dbhelper.DBFactory;
import net.qiushao.lib.dbhelper.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;

import pl.openrnd.multilevellistview.ItemInfo;
import pl.openrnd.multilevellistview.MultiLevelListView;
import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.activity.GetTalonActivity;
import ru.bmixsoft.jsontest.adapter.HashMapAdapters;
import ru.bmixsoft.jsontest.adapter.MyExpeListAdapter;
import ru.bmixsoft.jsontest.model.Polis;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Михаил on 27.02.2017.
 */
public class LpuForPolisFragment extends  MyFragment {


    private static final String DbgTAG ="LpuForPolisFragment";

    public static final String INTENT_OPT_POLIS = "INTENT_OPT_POLIS";

    protected static LpuForPolisFragment instance;


    private final static int REQUEST_GET_TALON = 0;

    private Polis mCurPolis;

    private static final String SAVE_KEY_CLASS = "CONFIRM_TALON_FRAGMENT";
    private static final String SAVE_KEY_CUR_ACTION = SAVE_KEY_CLASS+"_CUR_ACTION";
    private static final String SAVE_KEY_CUR_SEND_REQ = SAVE_KEY_CLASS+"_SEND_REQ";
    private static final String SAVE_KEY_CUR_SOAP = SAVE_KEY_CLASS+"_CUR_SOAP";

    private DBHelper myDb;


    public static MyFragment getInstance(Context context)
    {
        if (instance == null) {
            synchronized (MainFragment.class) {
                if (instance == null) {instance = new LpuForPolisFragment();}
            }
        }else{}
        return instance;
    }


    @TargetApi(14)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        String curPolisId;
        /*
        if (savedInstanceState != null) {
            curPolisId = savedInstanceState.getInt(INTENT_OPT_POLIS);

        }
        else{

        }
        */
        curPolisId = getActivity().getIntent().getStringExtra(INTENT_OPT_POLIS);

        myDb = DBFactory.getInstance().getDBHelper(Polis.class);
        mCurPolis = Polis.getPolis(curPolisId);
    }

    @TargetApi(14)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().setTitle("Список доступных поликлиник");

        View v = inflater.inflate(R.layout.fragment_my_expand_list_view, container, false);

        View emptyView = v.findViewById(R.id.emptyList);

        //////////////////////////////////////////////////////
        /// Список доступных пполиклиник
        /////////////////////////////////////////////////////
       final MultiLevelListView mListView  = (MultiLevelListView) v.findViewById(R.id.listViewExp);
        ArrayList<HashMap<String, Object>> myList = HashMapAdapters.getLpuLnkPolis(mCurPolis)
                ;
        MyExpeListAdapter listAdapter = new MyExpeListAdapter(getContext().getApplicationContext(), myList, new MyExpeListAdapter.Callback() {

            @Override
            public void onClickListItem(HashMap<String, Object> map, ItemInfo itemInfo, MyExpeListAdapter adapter) {

              //  Utils.d(map.get(MyExpeListAdapter.TAG_NAME).toString());

                Intent i = new Intent(getActivity(), GetTalonActivity.class);
                i.putExtra(GetTalonActivity.TAG_ACTION, GetTalonActivity.actionAddTalon);
                i.putExtra(GetTalonActivity.TAG_CITY_ID, (String) map.get(HashMapAdapters.TAG_CITY_ID));
                i.putExtra(GetTalonActivity.TAG_POLIS_ID, mCurPolis.getId());
                i.putExtra(GetTalonActivity.TAG_LPU_ID, (String) map.get(HashMapAdapters.TAG_LPU_ID));
                startActivityForResult(i, REQUEST_GET_TALON);

            }
        });
        mListView.setAdapter(listAdapter);
        mListView.setOnItemClickListener(listAdapter.mOnItemClickListener);
        listAdapter.refresh();

        if ( listAdapter.mList.size() == 0)
            mListView.setEmptyView(emptyView);

        ////////////////////////////////////////////////////


        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( requestCode == REQUEST_GET_TALON)
        {
            if (resultCode == RESULT_OK) {
                getActivity().finish();
            }
            //refreshAdapter();
        }
    }

}
