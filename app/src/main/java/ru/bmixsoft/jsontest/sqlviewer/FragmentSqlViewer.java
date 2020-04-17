package ru.bmixsoft.jsontest.sqlviewer;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.evrencoskun.tableview.TableView;
import com.evrencoskun.tableview.adapter.AbstractTableAdapter;

import net.qiushao.lib.dbhelper.DBFactory;
import net.qiushao.lib.dbhelper.DBHelper;

import java.util.ArrayList;
import java.util.List;

import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.fragment.dialog.StrDialogFragment;
import ru.bmixsoft.jsontest.model.City;
import ru.bmixsoft.jsontest.tableview.TableViewAdapter;
import ru.bmixsoft.jsontest.tableview.TableViewListener;
import ru.bmixsoft.jsontest.tableview.model.Cell;
import ru.bmixsoft.jsontest.tableview.model.ColumnHeader;
import ru.bmixsoft.jsontest.tableview.model.RowHeader;
import ru.bmixsoft.jsontest.utils.Utils;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSqlViewer extends Fragment {

    private EditText mTvSqlView;
    private TextView mBottomExec;
    private TextView mTvErrSql;
    private DBHelper db;

    private List<RowHeader> m_jRowHeaderList;
    private List<ColumnHeader> m_jColumnHeaderList;
    private List<List<Cell>> m_jCellList;

    private AbstractTableAdapter m_iTableViewAdapter;
    private TableView m_iTableView;

    public FragmentSqlViewer() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();

        //setFullScreenMode();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sql_viewer, container, false);

        final RelativeLayout fragment_container = (RelativeLayout) view.findViewById(R.id
                .fragment_container);

        // Create Table view
        m_iTableView = createTableView();
        fragment_container.addView(m_iTableView);

        mTvSqlView = (EditText) view.findViewById(R.id.sql_activity_tvsql);
        mBottomExec = (Button) view.findViewById(R.id.sql_activity_exec);
        mTvErrSql = (TextView) view.findViewById(R.id.sqlTvError);

        mTvErrSql.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Utils.d(mTvErrSql.getText().toString());
                return true;
            }
        });

        db = DBFactory.getInstance(getContext().getApplicationContext()).getDBHelper(City.class);

        mBottomExec.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               db.readLock.lock();
                                               try {
                                                   Cursor cursor = db.rawQueryWithError(mTvSqlView.getText().toString(), null);
                                                   if (cursor != null) {

                                                       initData();
                                                       for (int i = 0; i < cursor.getColumnCount(); i++) {
                                                           ColumnHeader header = new ColumnHeader(String.valueOf(i), cursor.getColumnName(i), i);
                                                           m_jColumnHeaderList.add(header);
                                                       }

                                                       int idx = 0;
                                                       while (cursor.moveToNext()) {

                                                           RowHeader header = new RowHeader(String.valueOf(idx), "slidemenu_row " + idx, idx);
                                                           m_jRowHeaderList.add(header);

                                                           List<Cell> cellList = new ArrayList<>();
                                                           for (int i = 0; i < cursor.getColumnCount(); i++) {

                                                               String strID = idx + "-" + i;
                                                               Cell cell = new Cell(strID, "", cursor.getString(i), idx, i);
                                                               cellList.add(cell);
                                                           }
                                                           m_jCellList.add(cellList);
                                                           idx++;
                                                       }
                                                       m_iTableView = createTableView();
                                                       fragment_container.removeAllViews();
                                                       fragment_container.addView(m_iTableView);
                                                       loadData();
                                                       cursor.close();
                                                       mTvErrSql.setText("");
                                                   }
                                               } catch (Exception e) {
                                                   mTvErrSql.setText(Utils.errStack(e));
                                                   Utils.safePrintError(e); // e.printStackTrace();
                                               } finally {
                                                   db.readLock.unlock();
                                               }

                                           }
                                       }
        );

        loadData();
        return view;
    }

    private TableView createTableView() {
        TableView tableView = new TableView(getContext());

        // Set adapter
        m_iTableViewAdapter = new TableViewAdapter(getContext());
        tableView.setAdapter(m_iTableViewAdapter);

        // Set layout params
        FrameLayout.LayoutParams tlp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams
                .MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        tableView.setLayoutParams(tlp);

        // Set TableView listener
        tableView.setTableViewListener(new TableViewListener(tableView, new TableViewListener.CallBackTV() {
            @Override
            public void OnCellClick(int curRow, int curCol) {

                final Cell cell = getCell(curRow, curCol);
                if (cell != null && cell.getData() != null)
                {
                    String strValue = cell.getShowData();
                    StrDialogFragment.show(getActivity(),"Значение","",strValue);
                }
            }
        }));
        return tableView;
    }

    private Cell getCell(int curRow, int curCol)
    {
        if ( m_jCellList != null)
        {
            for (List<Cell> list : m_jCellList)
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

    private void initData() {
        m_jRowHeaderList = new ArrayList<>();
        m_jColumnHeaderList = new ArrayList<>();
        m_jCellList = new ArrayList<>();
        /*
        for (int i = 0; i < ROW_SIZE; i++) {
            m_jCellList.add(new ArrayList<Cell>());
        }
        */
    }

    private void loadData() {
        m_iTableViewAdapter.setAllItems(m_jColumnHeaderList, m_jRowHeaderList, m_jCellList);
    }

    private void loadData1() {
    m_jRowHeaderList =new ArrayList<>();
    m_jColumnHeaderList =new ArrayList<>();
    m_jCellList =new ArrayList<>();

        for(
    int j = 0;
    j<20;j++)

    {
        m_jColumnHeaderList.add(new ColumnHeader(String.valueOf(j), "#" + String.valueOf(j), j));
    }

        for(
    int i = 0;
    i< 10;i++)

    {
        m_jRowHeaderList.add(new RowHeader(String.valueOf(i), "#" + String.valueOf(i), i));

        List<Cell> cellList = new ArrayList<>();

        for (int j = 0; j < 10; j++) {
            String strID = i + "-" + j;
            Cell cell = new Cell(strID, "", String.valueOf(j), i, j);
            cellList.add(cell);
        }
        m_jCellList.add(cellList);

    }
        m_iTableViewAdapter.setAllItems(m_jColumnHeaderList, m_jRowHeaderList, m_jCellList);

    }

    private void setFullScreenMode() {
        // Set full screen mode
        this.getActivity().getWindow().getDecorView().setSystemUiVisibility(View
                .SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View
                .SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide
                // nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }
}
