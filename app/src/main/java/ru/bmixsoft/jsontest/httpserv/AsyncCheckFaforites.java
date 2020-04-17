package ru.bmixsoft.jsontest.httpserv;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import net.qiushao.lib.dbhelper.DBFactory;
import net.qiushao.lib.dbhelper.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;

import ru.bmixsoft.jsontest.model.FavoritesDoct;
import ru.bmixsoft.jsontest.model.Speciality;
import ru.bmixsoft.jsontest.multiview.data.DataProvider;
import ru.bmixsoft.jsontest.utils.Utils;

/**
 * Created by Михаил on 21.12.2017.
 */
//параметр, прогресс, результат
public class AsyncCheckFaforites extends AsyncTask<Void, String, String> {
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private static final String DbgTAG = "AsyncCheckFaforites";
    private Callback mCallback;
    private boolean mIsNeedWork;
    private final int mSecSleep = 1000 * 1;
    private boolean mIsProcNextRequest;
    private DBHelper mDBHelper;
    private ArrayList<FavoritesDoct> mListFD;
    private int mCurIdxFd;
    private HttpServ.Callback mHttpServCallback;
    private String mResultMsg;
    private FavoritesDoct mCurFavoritesDoct;


    public interface Callback {
        public void onFinishUI(String result);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public AsyncCheckFaforites(Context context) {
        mContext = context;
        mProgressDialog = new ProgressDialog(context);
        mIsNeedWork = true;
        mIsProcNextRequest = true;
        mDBHelper = DBFactory.getInstance().getDBHelper(FavoritesDoct.class);
        mCurIdxFd = 0;
        mResultMsg = "";
        mHttpServCallback = new HttpServ.Callback() {
            @Override
            public synchronized void onProc(HashMap<String, Object> result) {

            }

            @Override
            public synchronized void onFinish(HashMap<String, Object> result, ArrayList<HashMap<String, Object>> resultArrayList) {

                int cntAvalibleDay = 0;
                for (final HashMap<String, Object> map : resultArrayList) {
                    if (map.get(DataProvider.KEY_PARENT_NODE) != null) {
                        if (!map.get(Actions.RESULT_ARRLST_AVALIBLE).equals("0")) {
                            try {
                            cntAvalibleDay = cntAvalibleDay + (Integer)map.get(Actions.RESULT_ARRLST_AVALIBLE_CNT);
                            }
                            catch (Exception e)
                            {
                               Utils.safePrintError(e);
                                cntAvalibleDay = cntAvalibleDay + 1;
                            }
                        }
                    }
                }
                if (cntAvalibleDay > 0 && mCurFavoritesDoct != null)
                {
                    mResultMsg += mCurFavoritesDoct.getShortDoctFio() + ": " + String.valueOf(cntAvalibleDay) + "\n";
                }

                mCurIdxFd++;
                if (mCurIdxFd >= mListFD.size())
                    mIsNeedWork = false;
                mIsProcNextRequest = true;
            }

        };
    }

    //onPreExecute() – выполняется перед doInBackground(). Имеет доступ к UI
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // mProgressDialog.setMessage("Подождите. Анализ результата");
        // mProgressDialog.setCancelable(false);
        // mProgressDialog.show();
    }

    //doInBackground() – основной метод, который выполняется в новом потоке. Не имеет доступа к UI.
    @Override
    protected synchronized String doInBackground(Void... params) {

        mListFD = (ArrayList<FavoritesDoct>) mDBHelper.getArrayList("IsNeedChkJob=?",new String[]{"1"} ,FavoritesDoct.class, null);

        if (mListFD != null && mListFD.size() > 0) {

            while (mIsNeedWork) {
                try {
                    if (mIsProcNextRequest && mCurIdxFd < mListFD.size()) {
                        mCurFavoritesDoct = mListFD.get(mCurIdxFd);
                        HttpServ mHttpServ = new HttpServ(mContext);
                        mHttpServ.setIsNeedShowProcDialog(false);
                        Speciality speciality = Speciality.get(mCurFavoritesDoct.getSpecId());
                        mHttpServ.getDoctList(mCurFavoritesDoct.getLpuId(), speciality.getCode(), mHttpServCallback, false, mCurFavoritesDoct.getDoctId(), null);
                    }
                    mIsProcNextRequest = false;

                    Thread.sleep(mSecSleep);
                } catch (InterruptedException e) {
                    Utils.safePrintError(e); //  e.printStackTrace();
                }
            }
        }

        return mResultMsg;
/*
            AsyncJSoupHttpHelper.Callback httpCallback = new AsyncJSoupHttpHelper.Callback() {
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
//                                mListAvalibleDay.add(map);
                                mCntAvalibleDay++;

                                ArrayList<HashMap<String, Object>> resTimeArrList = new ArrayList<>();
                                AsyncJSoupHttpHelper mHttpServ = new AsyncJSoupHttpHelper(mContext);
                                mHttpServ.getTimeList(mFavoritesDoct.getLpuId().toString(), (Integer) map.get(Actions.RESULT_ARRLST_RESULT_DOCTOR_ID)
                                        , (Integer) map.get(Actions.RESULT_ARRLST_RESULT_DOCPOST_ID)
                                        , (String) map.get(Actions.RESULT_ARRLST_RESULT_DATE_STR)
                                        , (Integer) map.get(Actions.RESULT_ARRLST_RESULT_DAYSCHEDUL_ID)
                                        , map, resTimeArrList, new AsyncJSoupHttpHelper.Callback() {
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

            AsyncJSoupHttpHelper mHttpServ = AsyncJSoupHttpHelper.getInstance(mContext);
            mHttpServ.getDoctList(mFavoritesDoct.getLpuId().toString(), mFavoritesDoct.getSpecId().toString(), httpCallback, false, mFavoritesDoct.getDoctId(), "Получение списка доступных дат");
            }
*/


        //return null;
    }

    //onPostExecute() – выполняется после doInBackground() (может не вызываться, если AsyncTask был отменен). Имеет доступ к UI. Используйте его для обновления пользовательского интерфейса, как только ваша фоновая задача завершена. Данный обработчик при вызове синхронизируется с потоком GUI, поэтому внутри него вы можете безопасно изменять элементы пользовательского интерфейса.
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        //   if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
        if (mCallback != null)
            mCallback.onFinishUI(s);
    }

    //onProgressUpdate(). Имеет доступ к UI. Переопределите этот обработчик для публикации промежуточных обновлений в пользовательский интерфейс. При вызове он синхронизируется с потоком GUI, поэтому в нём вы можете безопасно изменять элементы пользовательского интерфейса
    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Log.d(DbgTAG, "onProgressUpdate -->");
        Log.d(DbgTAG, " values[0]: " + values[0]);
        // mProgressDialog.setMessage(values[0]);
        Log.d(DbgTAG, "onProgressUpdate --<");
    }


}

