package ru.bmixsoft.jsontest.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.crashlytics.android.Crashlytics;

import net.qiushao.lib.dbhelper.DBFactory;
import net.qiushao.lib.dbhelper.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;

import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.activity.GetTalonActivity;
import ru.bmixsoft.jsontest.adapter.TalonItemAdater;
import ru.bmixsoft.jsontest.fragment.dialog.RateDialogFragment;
import ru.bmixsoft.jsontest.fragment.dialog.WebDialogFragment;
import ru.bmixsoft.jsontest.fragment.options.LibOption;
import ru.bmixsoft.jsontest.httpserv.AsyncJSoupHttpHelper;
import ru.bmixsoft.jsontest.httpserv.HttpServ;
import ru.bmixsoft.jsontest.httpserv.JSoupHelper;
import ru.bmixsoft.jsontest.model.Polis;
import ru.bmixsoft.jsontest.model.Talon;
import ru.bmixsoft.jsontest.utils.Utils;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Михаил on 14.04.2016.
 */
public class MainFragment extends MyFragment
        implements TalonItemAdater.Callback, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "MainFragment";
    // private Callbacks mCallbacks;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    protected static MyFragment instance;
    private TalonItemAdater talonItemAdater;
    private GridView mGridView;
    private DBHelper mDBHelper;

    private final static int REQUEST_GET_TALON = 0;


    /*
    public interface Callbacks {
        void onDeviceSelected(Device device);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks)activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
*/

    public void OnChangeTalon(Talon talon, int action) {
        switch (action) {
            case TalonItemAdater.ACTION_DELETE:

                break;
            default:
                break;
        }
    }

    public MainFragment() {
        //   refreshTalons();
    }

    public static MyFragment getInstance(Context context) {
        if (instance == null) {
            synchronized (MainFragment.class) {
                if (instance == null) {
                    instance = new MainFragment();
                }
            }
        }
        return instance;
    }


    public synchronized void refreshAdapter() {
        try {
            ArrayList<Talon> listTalons = (ArrayList<Talon>) mDBHelper.getArrayList(Talon.class, "CAST(substr(ShortDate,7,4)" +
                    "||substr(ShortDate,4,2)" +
                    "||substr(ShortDate,1,2) AS INTEGER) ASC, CAST(replace(Time_from,':','') AS INTEGER) ASC");

            talonItemAdater = new TalonItemAdater(getActivity(), listTalons, this);
            mGridView.setAdapter(talonItemAdater);
        } catch (Exception e) {
            Utils.safePrintError(e); // e.printStackTrace();
        }
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(false);
        //  talonItemAdater.notifyDataSetChanged();

    }

    @TargetApi(14)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mDBHelper = DBFactory.getInstance(getActivity().getApplicationContext()).getDBHelper(Talon.class);
        refreshTalons();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (talonItemAdater != null)
            talonItemAdater.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stdgrid_list, container, false);


        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.std_swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mGridView = (GridView) v.findViewById(R.id.stdGridView);
        refreshAdapter();

        return v;
    }


    //когда сдвинули по свайпу
    @Override
    public void onRefresh() {
        refreshTalons();
    }

    public void refreshTalons() {
        try {
            int cntRun = LibOption.getOptionValueInt(getActivity().getApplication(), "cntRunApp");
            LibOption.setOption(getActivity().getApplication(), "cntRunApp", cntRun + 1);
            int showModeRateFrag = LibOption.getOptionValueInt(getActivity().getApplication(), "showModeRateDialog");
            if (showModeRateFrag < 2 && cntRun % 5 == 0) {
                RateDialogFragment.show(getActivity());
            }

            HttpServ httpServ = HttpServ.getInstance(getActivity());
            httpServ.getPatientOrder(new HttpServ.Callback() {
                @Override
                public void onProc(HashMap<String, Object> result) {

                }

                @Override
                public void onFinish(HashMap<String, Object> result, ArrayList<HashMap<String, Object>> resultArrayList) {
                    refreshAdapter();
                }
            });


        }
        catch (Exception e)
        {
            Crashlytics.log("refreshTalons: Ошибка обновления талонов: "+getActivity() == null ? "Активность еще не создана": e.getMessage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Utils.viberate(mActivity, 50);
        int idx = item.getItemId();
        switch (idx) {
            case R.id.menu_main_zapis:

                long cntPolis = mDBHelper.size(Polis.class);
                if (cntPolis != 0) {
                    // проверка доступности веб-ресурса
                    JSoupHelper.checkAvailableSite(getActivity(), new JSoupHelper.Callback()
                    {
                        @Override
                        public void onFinish(boolean success, HashMap<String, Object> result) {
                            String title = "";
                            if (result.containsKey(AsyncJSoupHttpHelper.hmk_result_head_txt))
                                title = (String) result.get(AsyncJSoupHttpHelper.hmk_result_head_txt);
                            if ((int) result.get(AsyncJSoupHttpHelper.hmk_success) == 1) {
                                Intent i = new Intent(getActivity(), GetTalonActivity.class);
                                i.putExtra(GetTalonActivity.TAG_ACTION, GetTalonActivity.actionAddTalon);
                                startActivityForResult(i, REQUEST_GET_TALON);
                            } else {
                                //InfoDialogFragment.show((FragmentActivity) getActivity(), getString(R.string.chkAvailableServer), title, (String) result.get(AsyncJSoupHttpHelper.hmk_result), (int) result.get(AsyncJSoupHttpHelper.hmk_success) == 1 ? InfoDialogFragment.TypeDialog.INFO : InfoDialogFragment.TypeDialog.ERROR);
                                WebDialogFragment.show((FragmentActivity) getActivity(),getActivity().getString(R.string.errorWrkSite), getActivity().getString(R.string.urlChkAvailableServer));
                            }
                        }
                    });

                } else {
                    Utils.msgWarning(getString(R.string.errorCreateNewTalonNotPolise));
                }
                break;
/*
            case R.id.menu_main_refresh_talons:
                refreshTalons();
                break;
                */
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GET_TALON) {
            if (resultCode == RESULT_OK) {
                refreshTalons();
                refreshAdapter();
            }
            //refreshAdapter();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_menu_main, menu);
    }
}
