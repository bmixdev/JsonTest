package ru.bmixsoft.jsontest.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.qiushao.lib.dbhelper.DBFactory;
import net.qiushao.lib.dbhelper.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;

import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.activity.GetLpuForPolis;
import ru.bmixsoft.jsontest.fragment.LpuForPolisFragment;
import ru.bmixsoft.jsontest.fragment.dialog.WebDialogFragment;
import ru.bmixsoft.jsontest.httpserv.AsyncJSoupHttpHelper;
import ru.bmixsoft.jsontest.httpserv.HttpServ;
import ru.bmixsoft.jsontest.httpserv.JSoupHelper;
import ru.bmixsoft.jsontest.model.City;
import ru.bmixsoft.jsontest.model.FavoritesDoct;
import ru.bmixsoft.jsontest.model.LpuLinkPolis;
import ru.bmixsoft.jsontest.model.Polis;
import ru.bmixsoft.jsontest.utils.Utils;

/**
 * Created by Михаил on 22.11.2016.
 */
public class PolisItemAdater extends ArrayAdapter<Polis> {


    private Activity mContext;
    private ArrayList<Polis> mItems;
    private DBHelper myDb;


    private Callback mCallback;

    public interface Callback {
        void OnChangePolis(Polis polis);
    }

    public void registerCallBack(Callback callback) {
        mCallback = callback;
    }

    public PolisItemAdater(Context context, ArrayList<Polis> items, Callback callback) {
        super(context, 0, items);
        mContext = (Activity) context;
        mItems = items;
        myDb = DBFactory.getInstance(getContext().getApplicationContext()).getDBHelper(Polis.class);
        registerCallBack(callback);

    }


    private void editPolis(Polis polis) {
        if (Utils.isDebugMode()) Utils.d(getContext().getApplicationContext(), polis.toString());
        mCallback.OnChangePolis(polis);
//        notifyDataSetChanged();

    }

    private void deletePolis(final Polis polis) {
        if (Utils.isDebugMode()) Utils.d(getContext().getApplicationContext(), polis.toString());


        new AlertDialog.Builder(getContext())
                .setTitle("Подтверждение действия")
                .setMessage("Вы действительно хотите удалить полис?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        long cntFavorites = myDb.size("PolisId = ?", new String[]{polis.getId().toString()}, FavoritesDoct.class);
                        if (cntFavorites > 0) {
                            Utils.msgError(R.string.errorDeletePolisExistFavorites);
                            return;
                        }
                        long cntTalons = myDb.size("PolisId = ?", new String[]{polis.getId().toString()}, FavoritesDoct.class);
                        if (cntTalons > 0) {
                            Utils.msgError(R.string.errorDeletePolisExistTalons);
                            return;
                        }
                        // continue with delete
                        remove(polis);
                        myDb.refreshDataOnArrayList(mItems, Polis.class, true);

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

    private void syncPolis(Polis polis) {
        if (Utils.isDebugMode()) Utils.d(getContext().getApplicationContext(), polis.toString());
        HttpServ serv = HttpServ.getInstance(mContext);
        serv.authorizationPoilis(polis, new HttpServ.Callback() {
            @Override
            public void onProc(HashMap<String, Object> result) {
                notifyDataSetChanged();
            }

            @Override
            public void onFinish(HashMap<String, Object> result, ArrayList<HashMap<String, Object>> resultArrayList) {
                notifyDataSetChanged();
            }
        }, null);

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mContext.getLayoutInflater().inflate(R.layout.fragment_polis_item, parent, false);

        }
        final Polis item = getItem(position);

        LinearLayout mLayoutHeader = (LinearLayout) convertView.findViewById(R.id.llHeaderPolis);
        mLayoutHeader.setBackgroundColor(Utils.getColorHeader());
        LinearLayout mLayoutHeader1 = (LinearLayout) convertView.findViewById(R.id.llHeaderPolis1);
        mLayoutHeader1.setBackgroundColor(Utils.getColorHeader());
        LinearLayout mLayoutHeader2 = (LinearLayout) convertView.findViewById(R.id.llHeaderPolis2);
        mLayoutHeader2.setBackgroundColor(Utils.getColorHeader());
        LinearLayout mLayoutHeader3 = (LinearLayout) convertView.findViewById(R.id.llHeaderPolis3);
        mLayoutHeader3.setBackgroundColor(Utils.getColorHeader());

        TextView tvPolisDesc = (TextView) convertView.findViewById(R.id.tvItemPolisDesc);
        tvPolisDesc.setText(item.getFio());

        TextView tvDbg = (TextView) convertView.findViewById(R.id.tvItemPolisDbg);
        if (Utils.isDebugMode()) {
            tvDbg.setText(item.toString());
            tvDbg.setVisibility(Utils.isDebugMode() == true ? View.VISIBLE : View.INVISIBLE);
        }
        TextView tvNumPolis = (TextView) convertView.findViewById(R.id.tvItemPolisNum);
        tvNumPolis.setText(item.getPolusNum());

        TextView tvBirthday = (TextView) convertView.findViewById(R.id.tvItemPolisBirthday);
        tvBirthday.setText(item.getBirthday());

        TextView tvEmail = (TextView) convertView.findViewById(R.id.tvItemPolisEmail);
        tvEmail.setText(item.getEmail().toString());


        ImageButton btnSync = (ImageButton) convertView.findViewById(R.id.btnPolisItemRefresh);
        btnSync.setTag(position);
        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Polis tmpCurPolis = getItem((Integer) v.getTag());
                Long cntCitys = myDb.size(City.class);
                if (cntCitys == 0) {
                    HttpServ.getInstance(mContext).getCityList(new HttpServ.Callback() {
                        @Override
                        public void onProc(HashMap<String, Object> result) {

                        }

                        @Override
                        public void onFinish(HashMap<String, Object> result, ArrayList<HashMap<String, Object>> resultArrayList) {
                            syncPolis(tmpCurPolis);
                        }
                    }, tmpCurPolis.getId());
                }
                else
                {
                    syncPolis(tmpCurPolis);
                }
            }
        });

        ImageButton btnEdit = (ImageButton) convertView.findViewById(R.id.btnPolisItemEdit);
        btnEdit.setTag(position);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPolis(getItem((Integer) v.getTag()));
            }
        });

        ImageButton btnDelete = (ImageButton) convertView.findViewById(R.id.btnPolisItemDelete);
        btnDelete.setTag(position);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePolis(getItem((Integer) v.getTag()));
            }
        });

        int tmpCnt = myDb.size("POLIS_ID=?", new String[]{item.getId().toString()}, LpuLinkPolis.class);

        Button btnViewCurLpu = (Button) convertView.findViewById(R.id.btnPolisItemViewCurLPU);
        btnViewCurLpu.setTag(position);
        btnViewCurLpu.setText(mContext.getString(R.string.cntAvalibleLpu) + String.valueOf(tmpCnt) + "\n\n" + mContext.getString(R.string.newTalon));
        btnViewCurLpu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final int pos = (Integer) v.getTag();
                    // проверка доступности веб-ресурса
                    JSoupHelper.checkAvailableSite(mContext, new JSoupHelper.Callback() {
                        @Override
                        public void onFinish(boolean success, HashMap<String, Object> result) {
                            String title = "";
                            if (result.containsKey(AsyncJSoupHttpHelper.hmk_result_head_txt))
                                title = (String) result.get(AsyncJSoupHttpHelper.hmk_result_head_txt);
                            if ((int) result.get(AsyncJSoupHttpHelper.hmk_success) == 1) {
                                final String tmpCurPolisId = getItem(pos).getId();
                                Long cntCitys = myDb.size(City.class);
                                if (cntCitys == 0) {
                                    HttpServ.getInstance(mContext).getCityList(new HttpServ.Callback() {
                                        @Override
                                        public void onProc(HashMap<String, Object> result) {

                                        }

                                        @Override
                                        public void onFinish(HashMap<String, Object> result, ArrayList<HashMap<String, Object>> resultArrayList) {
                                            startActivity(tmpCurPolisId);
                                        }
                                    }, tmpCurPolisId);
                                }
                                else
                                {
                                    startActivity(tmpCurPolisId);
                                }
                            } else {
                                //InfoDialogFragment.show((FragmentActivity) mContext, mContext.getString(R.string.chkAvailableServer), title, (String) result.get(AsyncJSoupHttpHelper.hmk_result), (int) result.get(AsyncJSoupHttpHelper.hmk_success) == 1 ? InfoDialogFragment.TypeDialog.INFO : InfoDialogFragment.TypeDialog.ERROR);
                                WebDialogFragment.show((FragmentActivity) mContext, mContext.getString(R.string.errorWrkSite), mContext.getString(R.string.urlChkAvailableServer));
                            }
                        }
                    });

                } catch (Exception e) {
                    Utils.safePrintError(e); //   e.printStackTrace();
                }
            }
        });

        return convertView;
    }

    private void startActivity(String curPolisId)
    {
        Intent i = new Intent(mContext, GetLpuForPolis.class);
        i.putExtra(LpuForPolisFragment.INTENT_OPT_POLIS, curPolisId);
        mContext.startActivity(i);

    }

}