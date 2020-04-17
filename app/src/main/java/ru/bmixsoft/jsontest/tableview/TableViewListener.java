package ru.bmixsoft.jsontest.tableview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.evrencoskun.tableview.TableView;
import com.evrencoskun.tableview.listener.ITableViewListener;

/**
 * Created by evrencoskun on 21/09/2017.
 */

public class TableViewListener implements ITableViewListener {

    private Toast m_jToast;
    private Context m_jContext;
    private CallBackTV mCallBack;

    public interface CallBackTV{
       public void OnCellClick(int curRow, int curCol);
    }

    public TableViewListener(TableView p_jTableView, CallBackTV callBackTv) {
        this.m_jContext = p_jTableView.getContext();
        this.mCallBack = callBackTv;
    }

    /**
     * Called when user click any cell item.
     *
     * @param p_jCellView  : Clicked Cell ViewHolder.
     * @param p_nXPosition : X (Column) position of Clicked Cell item.
     * @param p_nYPosition : Y (Row) position of Clicked Cell item.
     */
    @Override
    public void onCellClicked(@NonNull RecyclerView.ViewHolder p_jCellView, int p_nXPosition, int
            p_nYPosition) {
        // Do want you want.

  //      showToast("Cell " + p_nXPosition + " " + p_nYPosition + " has been clicked.");
        if (mCallBack != null)
            mCallBack.OnCellClick(p_nYPosition, p_nXPosition);
    }

    /**
     * Called when user click any column header item.
     *
     * @param p_jColumnHeaderView : Clicked Column Header ViewHolder.
     * @param p_nXPosition        : X (Column) position of Clicked Column Header item.
     */
    @Override
    public void onColumnHeaderClicked(@NonNull RecyclerView.ViewHolder p_jColumnHeaderView, int
            p_nXPosition) {
        // Do want you want.
        //showToast("Column header  " + p_nXPosition + " has been clicked.");
    }

    /**
     * Called when user click any Row Header item.
     *
     * @param p_jRowHeaderView : Clicked Row Header ViewHolder.
     * @param p_nYPosition     : Y (Row) position of Clicked Row Header item.
     */
    @Override
    public void onRowHeaderClicked(@NonNull RecyclerView.ViewHolder p_jRowHeaderView, int
            p_nYPosition) {
        // Do want you want.

      //  showToast("Row header " + p_nYPosition + " has been clicked.");
    }


    private void showToast(String p_strMessage) {
        if (m_jToast == null) {
            m_jToast = Toast.makeText(m_jContext, "", Toast.LENGTH_SHORT);
        }

        m_jToast.setText(p_strMessage);
        m_jToast.show();
    }
}
