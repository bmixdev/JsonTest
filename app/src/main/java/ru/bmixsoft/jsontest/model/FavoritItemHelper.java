package ru.bmixsoft.jsontest.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.evrencoskun.tableview.TableView;
import com.evrencoskun.tableview.adapter.AbstractTableAdapter;

import net.qiushao.lib.dbhelper.DBFactory;
import net.qiushao.lib.dbhelper.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.bmixsoft.jsontest.activity.ConfirmTalonActivity;
import ru.bmixsoft.jsontest.httpserv.Actions;
import ru.bmixsoft.jsontest.httpserv.HttpServ;
import ru.bmixsoft.jsontest.multiview.data.DataProvider;
import ru.bmixsoft.jsontest.tableview.TableViewAdapter;
import ru.bmixsoft.jsontest.tableview.TableViewListener;
import ru.bmixsoft.jsontest.tableview.model.Cell;
import ru.bmixsoft.jsontest.tableview.model.ColumnHeader;
import ru.bmixsoft.jsontest.tableview.model.RowHeader;
import ru.bmixsoft.jsontest.utils.Utils;

import static ru.bmixsoft.jsontest.activity.GetTalonActivity.REQUEST_DLG_CONF_TALON;


public class FavoritItemHelper {


    private static final String TAG ="FavoritItemHelper";
    private Context mContext;
    private RelativeLayout mFragmentContainer;
    private List<RowHeader> mRowHeaderList;
    private List<ColumnHeader> mColumnHeaderList;
    private List<List<Cell>> mCellList;

    private AbstractTableAdapter mTableViewAdapter;
    private TableView mTableView;
    private int mCntAvalibleDay;
    private ArrayList<HashMap<String, Object>> mListAvalibleDay;
    private ArrayList<HashMap<String, Object>> mListAvalibleTime;
    private DBHelper myDb;

    private FavoritesDoct mFavoritesDoct;


    public interface Callback{
        public void OnRefresh(int result);
    }

    TableViewListener.CallBackTV callBackTV = new TableViewListener.CallBackTV() {
        @Override
        public void OnCellClick(int curRow, int curCol) {
            OnMyCellClick( curRow, curCol);
        }
    };

    public FavoritItemHelper(Context context, RelativeLayout fragmentContainer, FavoritesDoct favoritesDoct)
    {
        this.mContext = context;
        this.mFragmentContainer = fragmentContainer;
        this.mFavoritesDoct = favoritesDoct;

        myDb = DBFactory.getInstance(context.getApplicationContext()).getDBHelper(FavoritesDoct.class);

        mListAvalibleDay = new ArrayList<>();
        mListAvalibleTime = new ArrayList<>();
        mRowHeaderList = new ArrayList<>();
        mCellList = new ArrayList<>();




        mRowHeaderList.clear();
        mCellList.clear();
        mTableView = createTableView(callBackTV);
        // Create Table view

    }


    public void getAvalibleDateTimes(final Callback callback)
    {

        mCntAvalibleDay = 0;
        mListAvalibleDay.clear();
        mListAvalibleTime.clear();

        HttpServ.Callback httpCallback = new HttpServ.Callback() {
            @Override
            public void onProc(HashMap<String, Object> result) {

            }

            @Override
            public void onFinish(HashMap<String, Object> result, ArrayList<HashMap<String, Object>> resultArrayList) {
                //Utils.d(String.valueOf(result.size()));
                for (final HashMap<String, Object> map : resultArrayList)
                {
                    if (map.get(DataProvider.KEY_PARENT_NODE) != null) {
                        if (! map.get(Actions.RESULT_ARRLST_AVALIBLE).equals("0"))
                        {
                            //String tmpDate = (String) map.get(Actions.RESULT_ARRLST_RESULT_DATE_STR);
                            ///m_jRowHeaderList.add(new RowHeader(String.valueOf(mCntAvalibleDay), tmpDate));
                            mListAvalibleDay.add(map);
                            mCntAvalibleDay++;

                            ArrayList<HashMap<String, Object>> resTimeArrList = new ArrayList<>();
                            HttpServ mHttpServ = new HttpServ(mContext);
                            //LPU tmpLpu = LPU.get(mFavoritesDoct.getLpuId());
                            mHttpServ.getTimeList(mFavoritesDoct.getLpuId(), (String) map.get(Actions.RESULT_ARRLST_RESULT_DOCTOR_ID)
                                    , (String) map.get(Actions.RESULT_ARRLST_RESULT_DOCPOST_ID)
                                    , (String) map.get(Actions.RESULT_ARRLST_RESULT_DATE_STR)
                                    , (String) map.get(Actions.RESULT_ARRLST_RESULT_DAYSCHEDUL_ID)
                                    , map, resTimeArrList, new HttpServ.Callback() {
                                        @Override
                                        public void onProc(HashMap<String, Object> result) {

                                        }

                                        @Override
                                        public void onFinish(HashMap<String, Object> result, ArrayList<HashMap<String, Object>> resultArrayList) {
                                            for (HashMap<String, Object> mapTime : resultArrayList)
                                            {
                                                if (mapTime.get(DataProvider.KEY_PARENT_NODE) != null) {
                                                    if (!mapTime.get(Actions.RESULT_ARRLST_AVALIBLE).equals("0")) {
                                                        mapTime.put("FavoriteId", mFavoritesDoct.getId());
                                                        mListAvalibleTime.add(mapTime);
                                                    }
                                                }
                                            }

                                            mCntAvalibleDay--;
                                            // если вернулись все доступные времена для кол-ва дней
                                            if (mCntAvalibleDay == 0)
                                            {
                                                if (mListAvalibleDay.size() > 0 && mListAvalibleTime.size() == 0)
                                                {
                                                    callback.OnRefresh(0);
                                                }
                                                else {
                                                    callback.OnRefresh(1);
                                                    putAvalibleDateToGrid(mFragmentContainer);
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                }
                if (mListAvalibleDay.size() == 0)
                {
                    callback.OnRefresh(0);
                }
            }

        };

        HttpServ mHttpServ = HttpServ.getInstance(mContext);
        Speciality speciality = Speciality.get(mFavoritesDoct.getSpecId());
        mHttpServ.getDoctList(mFavoritesDoct.getLpuId(), speciality.getCode(), httpCallback, false, mFavoritesDoct.getDoctId(), "Получение списка доступных дат");

    }

    private TableView createTableView(TableViewListener.CallBackTV callBackTV) {

        TableView tableView = new TableView(mContext);
        // если родительское вью скролиться нужно обязательно фиксить ширину столбцов
        tableView.setHasFixedWidth(true);
        tableView.setMinimumWidth(100);
        //цвет бакграунда
        tableView.setBackgroundColor(mContext.getResources().getColor(android.R.color.white));
        // Set adapter
        mTableViewAdapter = new TableViewAdapter(mContext);
        tableView.setAdapter(mTableViewAdapter);

        // Set layout params
        FrameLayout.LayoutParams tlp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams
                .MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        tableView.setLayoutParams(tlp);


        // Set TableView listener
        tableView.setTableViewListener(new TableViewListener(tableView, callBackTV));

        return tableView;
    }

    private Cell getCell(int curRow, int curCol)
    {
        if ( mCellList != null)
        {
            for (List<Cell> list : mCellList)
            {
                for (Cell cell : list)
                {
                    if (cell.getRow() != curRow) break;
                    if (cell.getCol() != curCol) continue;
                    return cell;
                }
            }
        }
        return null;
    }

    public void OnMyCellClick( int curRow, int curCol)
    {
        final Cell cell = getCell(curRow, curCol);
        if (cell != null && cell.getData() != null)
        {
            new AlertDialog.Builder(mContext)
                    .setTitle("Оформление нового талона")
                    .setMessage("Вы действительно хотите оформить новый талон?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            HashMap<String, Object> map = (HashMap<String, Object>) cell.getData();
                            FavoritesDoct fd = FavoritesDoct.get( (String) map.get("FavoriteId"));
                            reservTalon(fd, (String) map.get(Actions.RESULT_ARRLST_RESULT_DOCPOST_ID), (String) map.get(Actions.RESULT_ARRLST_RESULT_TIME_ID), (String) map.get(Actions.RESULT_ARRLST_RESULT_TIME_STR), (String) map.get(Actions.RESULT_ARRLST_RESULT_DATE_STR));

                            try {
                                Intent i = new Intent(mContext, ConfirmTalonActivity.class);
                                Activity act = (Activity) mContext;
                                act.startActivityForResult(i, REQUEST_DLG_CONF_TALON);
                            } catch (Exception e) {
                                Log.d("reservTalonWithFav", "	error: " + e.getMessage());
                                Utils.safePrintError(e); // e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .create()
                    .show();
        }
    }

    public void reservTalon(FavoritesDoct fd, String curDocPost, String timeSchedule, String timeStr, String dateStr)
    {
        myDb = DBFactory.getInstance(mContext.getApplicationContext()).getDBHelper(CurTalon.class);
        myDb.clean(CurTalon.class);
        CurTalon curTalon = new CurTalon();
        curTalon.setId("1");
        curTalon.setCity(fd.getCityId());
        curTalon.setLpu(fd.getLpuId());
        curTalon.setSpec(fd.getSpecId());
        curTalon.setDoctor(fd.getDoctId());
        curTalon.setDocPost(curDocPost);
        curTalon.setTimeSchedule(timeSchedule);
        curTalon.setTimeStr(timeStr);
        curTalon.setDateStr(dateStr);
        curTalon.setPolisId(fd.getPolisId());
        curTalon.saveToDataBase();
    }

    private void putAvalibleDateToGrid(RelativeLayout fragment_container)
    {
        int cntRowNum = 0;
        for (HashMap<String, Object> mapDate : mListAvalibleDay)
        {
            String tmpDate = Utils.strToDateWeekStr((String) mapDate.get(Actions.RESULT_ARRLST_RESULT_DATE_STR), "dd.MM.yyyy");
            int cntColNum = 0;
            List<Cell> cellList = new ArrayList<>();
            for (HashMap<String, Object> mapTime : mListAvalibleTime)
            {
                HashMap<String, Object> parentMap = (HashMap<String, Object>) mapTime.get(DataProvider.KEY_PARENT_NODE);
                if (parentMap.equals(mapDate))
                {
                    Cell cell = new Cell(String.valueOf(cntRowNum)+"-"+String.valueOf(cntColNum), mapTime,(String) mapTime.get(Actions.RESULT_ARRLST_RESULT_TIME_STR), cntRowNum, cntColNum);
                    cellList.add(cell);
                    cntColNum++;
                }
            }
            if (cellList.size() > 0) {
                mRowHeaderList.add(new RowHeader(String.valueOf(cntRowNum), tmpDate, cntRowNum));
                mCellList.add(cellList);
                cntRowNum++;
            }

        }
        mTableViewAdapter.setAllItems(mColumnHeaderList, mRowHeaderList,  mCellList);
        fragment_container.removeAllViews();
        fragment_container.addView(mTableView);
    }


}
