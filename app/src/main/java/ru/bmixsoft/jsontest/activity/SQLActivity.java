package ru.bmixsoft.jsontest.activity;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.evrencoskun.tableview.TableView;
import com.evrencoskun.tableview.adapter.AbstractTableAdapter;

import net.qiushao.lib.dbhelper.DBFactory;
import net.qiushao.lib.dbhelper.DBHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.model.City;
import ru.bmixsoft.jsontest.tableview.TableViewAdapter;
import ru.bmixsoft.jsontest.tableview.model.Cell;
import ru.bmixsoft.jsontest.tableview.model.ColumnHeader;
import ru.bmixsoft.jsontest.tableview.model.RowHeader;
import ru.bmixsoft.jsontest.utils.Utils;


/**
 * Created by Михаил on 04.12.2017.
 */
public class SQLActivity extends Activity {

    private EditText mTvSqlView;
    private TextView mBottomExec;
    private TextView mTvErrSql;
    private DBHelper db;

    TableView mTableView;

    public static final int COLUMN_SIZE = 100;

    public static final int ROW_SIZE = 100;


    private List<RowHeader> m_jRowHeaderList;

    private List<ColumnHeader> m_jColumnHeaderList;

    private List<List<Cell>> m_jCellList;



    private AbstractTableAdapter m_iTableViewAdapter;

    private TableView m_iTableView;


    public ArrayList<String> title;

    public ArrayList<ArrayList<String>> data = new ArrayList<>();




    private void initData() {

        m_jRowHeaderList = new ArrayList<>();

        m_jColumnHeaderList = new ArrayList<>();

        m_jCellList = new ArrayList<>();

        for (int i = 0; i < ROW_SIZE; i++) {

            m_jCellList.add(new ArrayList<Cell>());

        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sql_exec);

        initData();

        // Set up the custom title
        mTvSqlView = (EditText) findViewById(R.id.sql_activity_tvsql);
        mBottomExec = (Button) findViewById(R.id.sql_activity_exec);
        mTvErrSql = (TextView) findViewById(R.id.tvSqlErr);

        mTableView = (TableView) findViewById(R.id.sqlTableView);

        // Set adapter

        m_iTableViewAdapter = new TableViewAdapter(this);

        mTableView.setAdapter(m_iTableViewAdapter);
        FrameLayout.LayoutParams tlp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams

                .MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        mTableView.setLayoutParams(tlp);

//        TableView tableView = (TableView) findViewById(R.id.sqlTblView);

        /*
        for (int i = 0; i < 80; i++) {

            data.add(new String("str"+i));

        }

*/


        db = DBFactory.getInstance(getApplicationContext()).getDBHelper(City.class);

        mBottomExec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                ArrayList<Object> tmpArr = (ArrayList<Object>) db.getArrayList(mTvSqlView.getText().toString().toUpperCase());
                myArrList = new ArrayList<String>();
                if (tmpArr.size() > 0) {
                    for (Object obj : tmpArr) {
                        myArrList.add(obj.toString());

                    }
                    lvAdapter.data = myArrList;
                    lvAdapter.notifyDataSetChanged();
                }
                */

                db.readLock.lock();
                try {
                    Cursor cursor = db.rawQuery(mTvSqlView.getText().toString().toUpperCase(), null);
                    if (cursor != null) {

                        title = new ArrayList<>();
                        data = new ArrayList<>();
                        title.add("ROWID");
                        data.add(new ArrayList<String>());

                        for (int i = 1; i < cursor.getColumnCount() + 1 ; i++)
                        {
                            title.add(cursor.getColumnName(i - 1));
                            data.add(new ArrayList<String>());
                        }

                        int idx = 0;
                        while (cursor.moveToNext()) {
                            idx++;
                            ArrayList<String> child = data.get(0);
                            child.add(String.valueOf(idx));
                            for (int i = 1; i < cursor.getColumnCount() + 1 ; i++)
                            {
                                ArrayList<String> child1 = data.get(i);
                                child1.add(cursor.getString(i - 1));
                            }
                        }
                        cursor.close();
    //                    final FixTableAdapter fixTableAdapter = new FixTableAdapter(title,data);

  //                      fixTableLayout.refreshAdapter(fixTableAdapter);

                    }
                } catch (Exception e) {
                    Utils.safePrintError(e); // e.printStackTrace();
                    mTvErrSql.setText("Ошибка\n"+e.getStackTrace().toString());
                } finally {
                    db.readLock.unlock();
                }

             //   fixTableAdapter.notify();

            }
        });


        loadData();

        // lvAdapter = new MyAdapter(this, myArrList);
        //mLvResult.refreshAdapter(lvAdapter);

    }

    private void loadData() {

        List<RowHeader> rowHeaders = getRowHeaderList();

        List<List<Cell>> cellList = getCellList(); //getRandomCellList(); //

        List<ColumnHeader> columnHeaders = getColumnHeaderList(); //getRandomColumnHeaderList(); //

        m_jRowHeaderList.addAll(rowHeaders);

        for (int i = 0; i < cellList.size(); i++) {

            m_jCellList.get(i).addAll(cellList.get(i));

        }



        // Load all data

        m_jColumnHeaderList.addAll(columnHeaders);

        m_iTableViewAdapter.setAllItems(m_jColumnHeaderList, m_jRowHeaderList, m_jCellList);



    }



    private List<RowHeader> getRowHeaderList() {

        List<RowHeader> list = new ArrayList<>();

        for (int i = 0; i < ROW_SIZE; i++) {

            RowHeader header = new RowHeader(String.valueOf(i), "slidemenu_row " + i, i);

            list.add(header);

        }



        return list;

    }



    private List<ColumnHeader> getColumnHeaderList() {

        List<ColumnHeader> list = new ArrayList<>();



        for (int i = 0; i < COLUMN_SIZE; i++) {

            String strTitle = "column " + i;

            if (i % 6 == 2) {

                strTitle = "large column " + i;

            }

            ColumnHeader header = new ColumnHeader(String.valueOf(i), strTitle, i);

            list.add(header);

        }



        return list;

    }



    private List<ColumnHeader> getRandomColumnHeaderList() {

        List<ColumnHeader> list = new ArrayList<>();



        for (int i = 0; i < COLUMN_SIZE; i++) {

            String strTitle = "column " + i;

            int nRandom = new Random().nextInt();

            if (nRandom % 4 == 0 || nRandom % 3 == 0 || nRandom == i) {

                strTitle = "large column " + i;

            }



            ColumnHeader header = new ColumnHeader(String.valueOf(i), strTitle, i);

            list.add(header);

        }



        return list;

    }



    private List<List<Cell>> getCellList() {

        List<List<Cell>> list = new ArrayList<>();

        for (int i = 0; i < ROW_SIZE; i++) {

            List<Cell> cellList = new ArrayList<>();

            for (int j = 0; j < COLUMN_SIZE; j++) {

                String strText = "cell " + j + " " + i;

                if (j % 4 == 0 && i % 5 == 0) {

                    strText = "large cell " + j + " " + i + ".";

                }

                String strID = j + "-" + i;



                Cell cell = new Cell(strID, strText, strText, i, j);

                cellList.add(cell);

            }

            list.add(cellList);

        }



        return list;

    }



    private List<List<Cell>> getRandomCellList() {

        List<List<Cell>> list = new ArrayList<>();

        for (int i = 0; i < ROW_SIZE; i++) {

            List<Cell> cellList = new ArrayList<>();

            list.add(cellList);

            for (int j = 0; j < COLUMN_SIZE; j++) {

                String strText = "cell " + j + " " + i;

                int nRandom = new Random().nextInt();

                if (nRandom % 2 == 0 || nRandom % 5 == 0 || nRandom == j) {

                    strText = "large cell  " + j + " " + i + getRandomString() + ".";

                }



                String strID = j + "-" + i;



                Cell cell = new Cell(strID, strText, strText, i, j);

                cellList.add(cell);

            }

        }



        return list;

    }





    private String getRandomString() {

        Random r = new Random();

        String str = " a ";

        for (int i = 0; i < r.nextInt(); i++) {

            str = str + " a ";

        }



        return str;

    }




/*
    public class FixTableAdapter implements IDataAdapter {



        public ArrayList<String> titles;



        public ArrayList<ArrayList<String>> data;

        public Cursor mCursor;

        public FixTableAdapter(ArrayList<String> title, ArrayList<ArrayList<String>>  data) {

            this.titles = title;

            this.data = data;
        }



        public void setData(ArrayList<ArrayList<String>>  data) {

            this.data = data;

        }

        public void setCursor(Cursor cursor)
        {

        }



        @Override

        public String getTitleAt(int pos) {

            return titles.get(pos);

        }



        @Override

        public int getTitleCount() {

            return titles.size();

        }



        @Override

        public int getItemCount() {

            return data.size();

        }



        @Override
        public void convertData(int position, List<TextView> bindViews) {

            for (int i= 0; i < data.size(); i++) {
                bindViews.get(i)
                        .setText(data.get(i).get(position));
            }
        }



        @Override

        public void convertLeftData(int position,TextView bindView) {

            bindView.setText(data.get(0).get(position));

        }

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // mMyThread.myHandler.getLooper().quit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
*/

}

