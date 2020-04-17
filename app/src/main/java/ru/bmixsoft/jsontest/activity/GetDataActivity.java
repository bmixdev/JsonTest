package ru.bmixsoft.jsontest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pl.openrnd.multilevellistview.ItemInfo;
import pl.openrnd.multilevellistview.MultiLevelListAdapter;
import pl.openrnd.multilevellistview.MultiLevelListView;
import pl.openrnd.multilevellistview.NestType;
import pl.openrnd.multilevellistview.NodeItemInfo;
import pl.openrnd.multilevellistview.OnItemClickListener;
import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.httpserv.Actions;
import ru.bmixsoft.jsontest.httpserv.HttpServ;
import ru.bmixsoft.jsontest.model.Speciality;
import ru.bmixsoft.jsontest.multiview.data.DataProvider;
import ru.bmixsoft.jsontest.multiview.views.LevelBeamView;


public class GetDataActivity extends AppCompatActivity {

	private MultiLevelListView mListView;

	private View emptyView;

	private String msgProc;
	private ListAdapter listAdapter;
	private String mCurAction;
	private String mCurActionExt;
	private ArrayList<HashMap<String, Object>> myList;


	public static final String TAG_ACTION = "action";
	public static final String TAG_ACTION_EXT = "action_ext";
	public static final String TAG_POLIS_ID = "polis_id";

	public static final String extMsgOnlyDoct = "filOnlyDocts";


	public static final String KEY_IS_EXIST_LIST = "is_exist_list";
	public static final String KEY_LIST = "slidemenu";
	public static final String KEY_TYPE_URL = "type_url";

	private HttpServ.Callback mCallback;
	private HttpServ mHttpServ;
	private String mLpuCode;
	private Object mCurObjectPressed;
	private String mPolisId;

	private View mLastClickedView;
	private Object mLastClickedItem;
	private NodeItemInfo mLastClickeditemInfo;
	private boolean isGetDateTimeClicked = false;

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putSerializable(KEY_LIST, myList);
		outState.putBoolean(KEY_IS_EXIST_LIST, myList.size() > 0 ? true : false);
		outState.putString(KEY_TYPE_URL, mCurAction.toString());
	}

	private void refreshListAdater()
	{
		listAdapter.setDataItems(DataProvider.getInstance(getApplicationContext(), myList).getInitialItems());
	}

	private void confViews() {
		setContentView(R.layout.data_activity);

		mListView = (MultiLevelListView) findViewById(R.id.listView);
		if ( mCurAction.equals(Actions.ACTION_GET_CITY_LIST))
		{
			mListView.setAlwaysExpanded(true);
		}
		emptyView = findViewById(R.id.emptyList);
		listAdapter = new ListAdapter();

		mListView.setAdapter(listAdapter);
		mListView.setOnItemClickListener(mOnItemClickListener);
		refreshListAdater();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			boolean isExistList = savedInstanceState.getBoolean(KEY_IS_EXIST_LIST);
			if (isExistList) {
				myList = new ArrayList<HashMap<String, Object>>();
				myList = (ArrayList<HashMap<String,Object>>) savedInstanceState.get(KEY_LIST);

				String tmpStr = savedInstanceState.getString(KEY_TYPE_URL);
				mCurAction = tmpStr;

				confViews();
				return;
			}
		}

		myList = new ArrayList<HashMap<String, Object>>();
		msgProc = new String();

		mCurAction = getIntent().getStringExtra(TAG_ACTION);
		mCurActionExt = getIntent().getStringExtra(TAG_ACTION_EXT);
		mPolisId = getIntent().getStringExtra(TAG_POLIS_ID);

		confViews();

		mCallback = new HttpServ.Callback() {
			@Override
			public void onProc(HashMap<String, Object> result) {

			}

			@Override
			public void onFinish(HashMap<String, Object> result, ArrayList<HashMap<String, Object>> resultArrayList) {
				if (myList.size() > 0) {
					if (!isGetDateTimeClicked) {
						refreshListAdater();
					} else {
						if (mLastClickeditemInfo != null && mLastClickeditemInfo.mNode != null)
							listAdapter.extendNode(mLastClickeditemInfo.mNode, NestType.MULTIPLE);
					}
				} else {
					if (resultArrayList != null) myList = resultArrayList;
					if (!isGetDateTimeClicked) {
						refreshListAdater();
					} else {
						if (mLastClickeditemInfo != null && mLastClickeditemInfo.mNode != null)
							listAdapter.extendNode(mLastClickeditemInfo.mNode, NestType.MULTIPLE);
					}
				}
				if (myList.size() == 0) mListView.setEmptyView(emptyView);
			}
		};

		mHttpServ = HttpServ.getInstance(this);
		switch (mCurAction.toString()) {
			case Actions.ACTION_GET_CITY_LIST:
				mHttpServ.getCityList(mCallback, mPolisId);
				return;
			case Actions.ACTION_GET_LPU_LIST:
				String city = new String(getIntent().getStringExtra(Actions.RESULT_ARRLST_RESULT_CODE));
				mHttpServ.getLpuList(city, mCallback);
				return;
			case Actions.ACTION_GET_SPEC_LIST:
				mLpuCode = new String(getIntent().getStringExtra(Actions.RESULT_ARRLST_RESULT_CODE));
				mHttpServ.getSpecList(mLpuCode, mCallback);
				return;
			case Actions.ACTION_GET_DOCT_LIST:
				mLpuCode = new String(getIntent().getStringExtra(Actions.RESULT_ARRLST_RESULT_CODE));
				String specId = (getIntent().getStringExtra(Actions.PARAM_SPEC_ID));
				Speciality speciality = Speciality.get(specId);

				mHttpServ.getDoctList(mLpuCode, speciality.getCode(), mCallback, mCurActionExt.equals(extMsgOnlyDoct) ? true : false, "-1", "");
				return;
		}
	}

	private void setDataResult(HashMap<String,Object> map)
		{
			Intent i = new Intent();
			i.putExtra(TAG_ACTION, mCurAction);
			switch (mCurAction.toString()){
				case Actions.ACTION_GET_CITY_LIST:
				case Actions.ACTION_GET_LPU_LIST:
			    case Actions.ACTION_GET_SPEC_LIST:
				case Actions.ACTION_GET_DOCT_LIST:
					i.putExtra(GetTalonActivity.RES_EDT_TXT, (String) map.get(Actions.RESULT_ARRLST_NAME));
					i.putExtra(GetTalonActivity.RES_LPU,(String) map.get(Actions.RESULT_ARRLST_RESULT_CODE));
					i.putExtra(GetTalonActivity.RES_TIME_ID,(String) map.get(Actions.RESULT_ARRLST_RESULT_TIME_ID));
					if (mCurActionExt == null || extMsgOnlyDoct == null || !mCurActionExt.equals(extMsgOnlyDoct)){
						i.putExtra(Actions.RESULT_ARRLST_RESULT_TIME_STR,(String) map.get(Actions.RESULT_ARRLST_RESULT_TIME_STR));
						i.putExtra(Actions.RESULT_ARRLST_RESULT_DATE_STR,(String) map.get(Actions.RESULT_ARRLST_RESULT_DATE_STR));
						i.putExtra(Actions.RESULT_ARRLST_RESULT_DOCPOST_ID,(String) map.get(Actions.RESULT_ARRLST_RESULT_DOCPOST_ID));

					}
					break;
			}
			
			setResult(RESULT_OK, i);
			this.finish();
		}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		private void showItemDescription(Object object, ItemInfo itemInfo) {
			HashMap<String, Object> map = (HashMap<String, Object>) object;

			if (map.get(DataProvider.KEY_PARENT_GROUP) == "0" &&
					(map.get(Actions.RESULT_ARRLST_AVALIBLE) == null || map.get(Actions.RESULT_ARRLST_AVALIBLE) == "1")
					) {
				setDataResult(map);
			}
			else {
				if (map.get(DataProvider.KEY_PARENT_GROUP) == "1" && ( map.containsKey(Actions.RESULT_ARRLST_IS_NEED_HTTP) && (boolean) map.get(Actions.RESULT_ARRLST_IS_NEED_HTTP) == true))
				{
					if (map.size() > 0) {
						mHttpServ.getTimeList(mLpuCode, (String) map.get(Actions.RESULT_ARRLST_RESULT_DOCTOR_ID)
								, (String) map.get(Actions.RESULT_ARRLST_RESULT_DOCPOST_ID)
								, (String) map.get(Actions.RESULT_ARRLST_RESULT_DATE_STR)
								, (String) map.get(Actions.RESULT_ARRLST_RESULT_DAYSCHEDUL_ID)
								, map, myList, mCallback);
					}
					isGetDateTimeClicked = true;
				}

			}
		}

		@Override
		public void onItemClicked(MultiLevelListView parent, View view, Object item, ItemInfo itemInfo) {
			mLastClickedView = view;
			mLastClickedItem = item;
			mLastClickeditemInfo = (NodeItemInfo) itemInfo;
			showItemDescription(item, itemInfo);
		}

		@Override
		public void onGroupItemClicked(MultiLevelListView parent, View view, Object item, ItemInfo itemInfo) {
			mLastClickedView = view;
			mLastClickedItem = item;
			mLastClickeditemInfo = (NodeItemInfo) itemInfo;
			showItemDescription(item, itemInfo);
		}
	};
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private class ListAdapter extends MultiLevelListAdapter {

		private class ViewHolder {
			TextView nameView;
			TextView infoView;
			ImageView arrowView;
			LevelBeamView levelBeamView;
		}

		@Override
		public List<?> getSubObjects(Object object) {
			return DataProvider.getInstance(getApplicationContext(),myList).getSubItems((HashMap<String, Object>)object);
		}

		@Override
		public boolean isExpandable(Object object) {
			return DataProvider.isExpandable((HashMap<String, Object>) object);
		}

		@Override
		public View getViewForObject(Object object, View convertView, ItemInfo itemInfo) {
			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(GetDataActivity.this).inflate(R.layout.data_item, null);
				viewHolder.infoView = (TextView) convertView.findViewById(R.id.dataItemInfo);
				viewHolder.nameView = (TextView) convertView.findViewById(R.id.dataItemName);
				viewHolder.arrowView = (ImageView) convertView.findViewById(R.id.dataItemArrow);
				viewHolder.levelBeamView = (LevelBeamView) convertView.findViewById(R.id.dataItemLevelBeam);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			HashMap<String, Object> map = (HashMap<String, Object>)  object;

			if (map.get(Actions.RESULT_ARRLST_AVALIBLE) == null ) {
				viewHolder.nameView.setTextColor(getResources().getColor(R.color.level_default));
			}else
			{

				if (map.get(Actions.RESULT_ARRLST_AVALIBLE) == "1") {
					viewHolder.nameView.setTextColor(getResources().getColor(R.color.green));}
				else{
					if (map.get(Actions.RESULT_ARRLST_BUSY_DAY)  == "2")
						viewHolder.nameView.setTextColor(getResources().getColor(R.color.bg_header));
					else
						viewHolder.nameView.setTextColor(getResources().getColor(R.color.grey));
				}
			}

			viewHolder.nameView.setText((String) map.get(Actions.RESULT_ARRLST_NAME));
			viewHolder.infoView.setText((String) map.get(Actions.RESULT_ARRLST_VALUME));

			if (itemInfo.isExpandable() /*&& !mAlwaysExpandend*/) {
				viewHolder.arrowView.setVisibility(View.VISIBLE);
				viewHolder.arrowView.setImageResource(itemInfo.isExpanded() ?
						R.drawable.arrow_up : R.drawable.arrow_down);
			} else {
				viewHolder.arrowView.setVisibility(View.GONE);
			}

			viewHolder.levelBeamView.setLevel(itemInfo.getLevel());

			return convertView;
		}
	}

}
