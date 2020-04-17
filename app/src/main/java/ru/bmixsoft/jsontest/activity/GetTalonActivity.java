package ru.bmixsoft.jsontest.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import net.qiushao.lib.dbhelper.DBFactory;
import net.qiushao.lib.dbhelper.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.adapter.SpinnerPolisAdapter;
import ru.bmixsoft.jsontest.fragment.ConfirmTalonFragment;
import ru.bmixsoft.jsontest.fragment.DialogConfCreateTalon;
import ru.bmixsoft.jsontest.httpserv.Actions;
import ru.bmixsoft.jsontest.httpserv.HttpServ;
import ru.bmixsoft.jsontest.model.City;
import ru.bmixsoft.jsontest.model.CurTalon;
import ru.bmixsoft.jsontest.model.Doctor;
import ru.bmixsoft.jsontest.model.FavoritesDoct;
import ru.bmixsoft.jsontest.model.LPU;
import ru.bmixsoft.jsontest.model.Polis;
import ru.bmixsoft.jsontest.model.Speciality;
import ru.bmixsoft.jsontest.utils.Utils;


public class GetTalonActivity extends AppCompatActivity implements DialogConfCreateTalon.InterfaceCommunicator{

	private ProgressDialog pDialog;
	private String msgProc;
	private City mCurCity;
	private final static String DbgTAG = "GetTalonActivity";

	private Polis mCurPolis;

	private boolean mSuccessInput = false;

	public EditText edtCity;
	public EditText edtLpu;
	public EditText edtSpec;
	public EditText edtDoct;
	public ImageView imgViewCity;
	public ImageView imgViewLpu;
	public ImageView imgViewSpec;
	public ImageView imgViewDoct;
	public Spinner spinnerPolis;
	public Button btnCreateNewTalon;
	private TextView dbgInfo;

	private String curLpuCity;
	private String curLpu;
	private String curSpec;
	private String curDoct;

	private String curTime;
	private String curDocPost;
	private String curPolis;


	private CurTalon mCurTalon;

	private String oldTalonId;

	private String mCurAction;
	public static final String TAG_ACTION = "action";
	public static final String TAG_CITY_ID = "cityId";
	public static final String TAG_LPU_ID = "lpuId";
	public static final String TAG_POLIS_ID = "polisId";

	public static final String TAG_SPEC_ID = "specId";
	public static final String TAG_DOC_ID = "docId";
	public static final String TAG_OLD_TALON_ID = "oldTalonId";

	public static final String actionAddTalon = "add_talon";
	public static final String actionReWrite = "rewrite_talon";
	public static final String actionAddFavorites = "add_favorites";


	private static final String SAVE_KEY_CUR_CITY = "SAVE_KEY_CUR_CITY";
	private static final String SAVE_KEY_CUR_LPU = "SAVE_KEY_CUR_LPU";
	private static final String SAVE_KEY_CUR_SPEC = "SAVE_KEY_CUR_SPEC";
	private static final String SAVE_KEY_CUR_DOCT = "SAVE_KEY_CUR_DOCT";
	private static final String SAVE_KEY_CUR_DOCPOST = "SAVE_KEY_CUR_DOCPOST";
	private static final String SAVE_KEY_CUR_TIME = "SAVE_KEY_CUR_TIME";
	private static final String SAVE_KEY_CUR_POLIS = "SAVE_KEY_CUR_POLIS";
	private static final String SAVE_KEY_DB = "SAVE_KEY_DB";
	private static final String SAVE_KEY_CSU = "SAVE_KEY_CSU";

	private static final String SAVE_KEY_CUR_SUC_INP = "SAVE_KEY_CUR_SUC_INP";

	private static final int REQUEST_DLG_CONFIRM = 0;
	public static final int REQUEST_DLG_CONF_TALON = 2;

	private DBHelper db;

	private static final Integer REQUEST_GET_DATA = 0;

	public static final String RES_EDT_TXT = "resEdtText";

	public static final String RES_LPU = "curLPUCode";
	public static final String RES_TIME_ID = "curTimeId";

	private static final String ACTION_GET_CITY = "GET_CITY";
	private static final String ACTION_GET_LPU = "GET_LPU";
	private static final String ACTION_GET_SPEC = "GET_SPEC";
	private static final String ACTION_GET_DOCT = "GET_DOCT";

	private ActionBar mActionBar;

	ArrayList<HashMap<String, String>> myList;

	public void d(String msg) {
		try {
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}



	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(SAVE_KEY_CUR_CITY, curLpuCity);
		outState.putString(SAVE_KEY_CUR_LPU, curLpu);
		outState.putString(SAVE_KEY_CUR_SPEC, curSpec);
		outState.putString(SAVE_KEY_CUR_DOCT, curDoct);
		outState.putString(SAVE_KEY_CUR_DOCPOST, curDocPost);
		outState.putString(SAVE_KEY_CUR_TIME, curTime);
		outState.putString(SAVE_KEY_CUR_POLIS, curPolis);
		outState.putBoolean(SAVE_KEY_CUR_SUC_INP, mSuccessInput);

	}

	private void createTalon(){
		Log.d(DbgTAG,"createTalon-->");
		if (mCurAction.equals(actionAddTalon) || mCurAction.equals(actionReWrite) ) {
			try {
				Intent i = new Intent(this, ConfirmTalonActivity.class);
				i.putExtra(ConfirmTalonFragment.INTENT_OLD_TALON_ID, oldTalonId);
				startActivityForResult(i, REQUEST_DLG_CONF_TALON);
			} catch (Exception e) {
				Log.d(DbgTAG, "	error: " + e.getMessage());
				Utils.safePrintError(e); // e.printStackTrace();
			}
		}
		else
		{
			if (mCurAction.equals(actionAddFavorites))
			{
				FavoritesDoct fd = new FavoritesDoct();
				fd.setCityId(mCurTalon.getCity());
				fd.setPolisId(mCurTalon.getPolisId());
				fd.setLpuId(mCurTalon.getLpu());
				fd.setSpecId(mCurTalon.getSpec());
				fd.setDoctId(mCurTalon.getDoctor());
				fd.setIsNeedChkJob(1);
				StringBuilder tmpDesc = new StringBuilder("");

				LPU tmpLpu = LPU.get4LpuCode(mCurTalon.getLpu());
				if (tmpLpu != null) {
					tmpDesc.append("\nПоликлиника: ").append(tmpLpu.getNAME());
					tmpDesc.append("\n").append(tmpLpu.getADDRESS());
					if (tmpLpu.getPHONE()!=null && !tmpLpu.getPHONE().isEmpty()) tmpDesc.append("\nТел.: ").append(tmpLpu.getPHONE());
					if (tmpLpu.getSITEURL()!=null&& !tmpLpu.getSITEURL().isEmpty()) tmpDesc.append("\nСайт: ").append(tmpLpu.getSITEURL());
					if (tmpLpu.getEMAIL()!=null&& !tmpLpu.getEMAIL().isEmpty()) tmpDesc.append("\nEmail: ").append(tmpLpu.getEMAIL());
				}
				Speciality tmpSpec = (Speciality) db.getObject(Speciality.class, mCurTalon.getSpec());
				if (tmpDesc != null)
				{
					if (tmpSpec != null && ! tmpSpec.getName().isEmpty())
						tmpDesc.append("\nСпециализация: ").append(tmpSpec.getName());
				}
				fd.setDesc(tmpDesc.toString());
				fd.appendDB(true);
				Crashlytics.log(1, "Добавление нового избранного. Поликлиника", tmpLpu.toString() );
				Crashlytics.log(1, "Добавление нового избранного. Описание", tmpDesc.toString() );

				finish();
			}
		}
		Log.d(DbgTAG,"createTalon--<");
	}

	// Create an anonymous implementation of OnClickListener
	private View.OnClickListener mCreateTalonListener = new View.OnClickListener() {
		public void onClick(View v) {
			createTalon();
		}
	};

	public void changePolis(Polis polis)
	{
		mCurPolis =  polis;
		if (mCurPolis != null) {
			ContentValues cv = new ContentValues();
			cv.put("PolisId", mCurPolis.getId());
			db.update(cv, "id=?", new String[]{"1"}, CurTalon.class);
			activeNextView();
		}
		HttpServ.getInstance(this).authorizationPoilis(mCurPolis, new HttpServ.Callback() {
			@Override
			public void onProc(HashMap<String, Object> result) {

			}

			@Override
			public void onFinish(HashMap<String, Object> result, ArrayList<HashMap<String, Object>> resultArrayList) {

			}
		}, null);
	}

	private void startGetDataActivity(String action)
	{
		Intent i = new Intent(getApplicationContext(), GetDataActivity.class);
		try {
			i.putExtra(GetDataActivity.TAG_POLIS_ID, mCurPolis.getId());
		}
		catch (Exception e)
		{
			Utils.printCrashToFireBase("GetTalonActivity", "startGetDataActivity", e);
			if (curPolis != null)
				i.putExtra(GetDataActivity.TAG_POLIS_ID, curPolis);
		}
		switch (action)
		{
			case ACTION_GET_CITY:
				i.putExtra(GetDataActivity.TAG_ACTION, Actions.ACTION_GET_CITY_LIST);
				break;
			case ACTION_GET_LPU:
				i.putExtra(GetDataActivity.TAG_ACTION, Actions.ACTION_GET_LPU_LIST);
				i.putExtra(Actions.RESULT_ARRLST_RESULT_CODE, curLpuCity);
				break;
			case ACTION_GET_SPEC:
				i.putExtra(GetDataActivity.TAG_ACTION, Actions.ACTION_GET_SPEC_LIST);
				i.putExtra(Actions.RESULT_ARRLST_RESULT_CODE, curLpu);
				break;
			case ACTION_GET_DOCT:
				i.putExtra(GetDataActivity.TAG_ACTION, Actions.ACTION_GET_DOCT_LIST);

				if (mCurAction.equals(actionAddFavorites))
					i.putExtra(GetDataActivity.TAG_ACTION_EXT, GetDataActivity.extMsgOnlyDoct);
				else
					i.putExtra(GetDataActivity.TAG_ACTION_EXT, "");

				i.putExtra(Actions.RESULT_ARRLST_RESULT_CODE, curLpu);
				i.putExtra(Actions.PARAM_SPEC_ID, curSpec);
				break;
			default:
		}
		startActivityForResult(i, REQUEST_GET_DATA);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		mActionBar = this.getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setDisplayShowTitleEnabled(true);

		mCurAction = getIntent().getStringExtra(TAG_ACTION);
		curLpuCity = getIntent().getStringExtra(TAG_CITY_ID);
		curLpu = getIntent().getStringExtra(TAG_LPU_ID);
		curPolis = getIntent().getStringExtra(TAG_POLIS_ID);
		curSpec = getIntent().getStringExtra(TAG_SPEC_ID);
		curDoct = getIntent().getStringExtra(TAG_DOC_ID);
		oldTalonId = getIntent().getStringExtra(TAG_OLD_TALON_ID);



		if (savedInstanceState != null) {
			curLpuCity = savedInstanceState.getString(SAVE_KEY_CUR_CITY);
			curLpu = savedInstanceState.getString(SAVE_KEY_CUR_LPU);
			curSpec = savedInstanceState.getString(SAVE_KEY_CUR_SPEC);
			curDoct = savedInstanceState.getString(SAVE_KEY_CUR_CITY);
			curDocPost = savedInstanceState.getString(SAVE_KEY_CUR_DOCPOST);
			curTime = savedInstanceState.getString(SAVE_KEY_CUR_TIME);
			curPolis = savedInstanceState.getString(SAVE_KEY_CUR_POLIS);
			mSuccessInput = savedInstanceState.getBoolean(SAVE_KEY_CUR_SUC_INP);

		}
		else{
			mSuccessInput = false;
		}

		if (mCurAction.equals(actionReWrite))
			setTitle(getString(R.string.activityRewriteTalonTitle));
		else
			setTitle(getString(R.string.activityNewTalonTitle));
		setContentView(R.layout.activity_new_talon);


		btnCreateNewTalon = (Button) this.findViewById(R.id.btnCreateNewTalon);
		btnCreateNewTalon.setOnClickListener(mCreateTalonListener);


		imgViewCity = (ImageView) this.findViewById(R.id.imgViewCity);
		edtCity = (EditText)this.findViewById(R.id.edtCityName);
		setColorCtrl(edtCity, imgViewCity, true);
		edtCity.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){
					startGetDataActivity(ACTION_GET_CITY);
				}

			});
		imgViewLpu = (ImageView) this.findViewById(R.id.imgViewLPU);
		edtLpu = (EditText)this.findViewById(R.id.edtLpuName);
		edtLpu.setEnabled(false);
		edtLpu.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){startGetDataActivity(ACTION_GET_LPU);
				}

			});

		imgViewSpec = (ImageView) this.findViewById(R.id.imgViewSpec);
		edtSpec = (EditText)this.findViewById(R.id.edtSpecName);
		edtSpec.setEnabled(false);
		edtSpec.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){startGetDataActivity(ACTION_GET_SPEC);
				}

			});

		imgViewDoct = (ImageView) this.findViewById(R.id.imgViewDoct);
		edtDoct = (EditText)this.findViewById(R.id.edtDoctName);
		edtDoct.setEnabled(false);
		edtDoct.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){startGetDataActivity(ACTION_GET_DOCT);
				}

			});

		spinnerPolis = (Spinner) this.findViewById(R.id.spinnerGetPolis);
		SpinnerPolisAdapter spinnerPolisAdapter = new SpinnerPolisAdapter(this, R.layout.slidemenu_row);
		spinnerPolisAdapter.setDropDownViewResource(R.layout.slidemenu_row);
		spinnerPolis.setAdapter(spinnerPolisAdapter);
		//spinnerPolis.setSelection(0);
		try {
			if (curPolis != null) {
				spinnerPolis.setSelection(spinnerPolisAdapter.getPositionByPolisId(curPolis));
				mCurPolis = Polis.getPolis(curPolis);
			}
			else spinnerPolis.setSelection(0);
		} catch (Exception e)
		{
			spinnerPolis.setSelection(0);
		}
		spinnerPolis.post(new Runnable() {
			@Override
			public void run() {
				spinnerPolis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					//	Utils.d(getApplicationContext(), spinnerPolis.getSelectedItem().toString());
						changePolis((Polis) spinnerPolis.getSelectedItem());
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						mCurPolis = null;
					}
				});
			}
		});

		//!!!!! убрать
		//	DBFactory.getInstance(getApplicationContext()).dropDataBase();

		db = DBFactory.getInstance(getApplicationContext()).getDBHelper(CurTalon.class);
		if (db.size(CurTalon.class) == 0)
		{
			mCurTalon = new CurTalon();
			mCurTalon.setId("1");
			db.insert(mCurTalon);

		}
		else{
			if(mCurTalon == null) {
				List<Object> ls = db.query("id=?", new String[]{"1"}, CurTalon.class, null);
				if (ls.size() > 0) {
					mCurTalon = new CurTalon();
					mCurTalon = (CurTalon) ls.get(0);

				}
			}
			//if(!Utils.isDebugMode()) {
				if (curLpuCity == null || (curLpuCity != null && !curLpuCity.equals(mCurTalon.getCity())) || curSpec == null) {

					db.delete("id = ?", new String[]{"1"}, CurTalon.class);
					mCurTalon = new CurTalon();
					mCurTalon.setId("1");
					db.insert(mCurTalon);
				}

				if (curLpuCity != null && !curLpuCity.isEmpty()){
					mCurTalon.setCity(curLpuCity);
					City city = City.get(curLpuCity);
					edtCity.setText(city.getFldNAME());
					mCurTalon.saveToDataBase();

				}
				if (curLpu != null && !curLpu.isEmpty() && (!curLpu.equals("0") && !curLpu.equals("null"))){
					try {
						//Utils.msgInfo("curLpu: "+curLpu);
						LPU lpu = LPU.get(curLpu);

						mCurTalon.setLpu(lpu.getLPUCODE());
						edtLpu.setText(lpu.getNAME());
						curLpu = lpu.getLPUCODE();
						setColorCtrl(edtSpec, imgViewSpec, false);
						mCurTalon.saveToDataBase();
					}
					catch (Exception e)
					{
						Crashlytics.setString("mCurAction", mCurAction);
						Crashlytics.setString("curLpu", curLpu);
						Crashlytics.setString("curLpuCity", curLpuCity);
						Crashlytics.logException(e);
						throw e;
					}

				}

			if (curSpec != null && !curSpec.isEmpty() && (!curSpec.equals("0") && !curSpec.equals("null"))){
				try {
					//Utils.msgInfo("curLpu: "+curLpu);
					Speciality spec = Speciality.get(curSpec);

					mCurTalon.setSpec(spec.getId());
					edtSpec.setText(spec.getName());
					setColorCtrl(edtDoct, imgViewDoct, false);
					mCurTalon.saveToDataBase();
				}
				catch (Exception e)
				{
					Crashlytics.setString("mCurAction", mCurAction);
					Crashlytics.setString("curLpu", curLpu);
					Crashlytics.setString("curLpuCity", curLpuCity);
					Crashlytics.setString("curSpec", curSpec);
					Crashlytics.logException(e);
					throw e;
				}

			}

			}
		/*
			else{
				curLpuCity = mCurTalon.getCity();
				curLpu = mCurTalon.getLpu();
				curSpec = mCurTalon.getSpec();

			}
		}
		*/

		dbgInfo = (TextView) findViewById(R.id.dbgInfo);
		dbgInfo.setVisibility(Utils.isDebugMode() == true ? View.VISIBLE : View.INVISIBLE);

		if (spinnerPolisAdapter.getCount() > 0)
			changePolis((Polis) spinnerPolisAdapter.getItem(spinnerPolis.getSelectedItemPosition()));


		if (mCurAction.equals(actionAddTalon)) {
			btnCreateNewTalon.setText("Оформить талон");
			edtDoct.setHint(getResources().getText(R.string.txtDoctName));
		}
		else
		if (mCurAction.equals(actionAddFavorites)) {
			btnCreateNewTalon.setText("Добавить в избранное");
			edtDoct.setHint(getResources().getText(R.string.txtDoctNameShort));
		}
		else
		if (mCurAction.equals(actionReWrite)) {
			btnCreateNewTalon.setText("Переоформить талон");
			edtDoct.setHint(getResources().getText(R.string.txtDoctName));
		}


		activeNextView();
	}


	private void setColorCtrl(EditText edt, ImageView img, boolean enabled) {
		edt.setEnabled(enabled);
		if (edt.getText().length() == 0) {
			edt.setTextColor(getResources().getColor(R.color.green));
			edt.setHintTextColor(getResources().getColor(enabled == true ? R.color.red : R.color.grey ));
		}
		else
		{
			edt.setTextColor(getResources().getColor(R.color.green));
			edt.setHintTextColor(getResources().getColor(R.color.grey));
		}
		setColorImage(img, enabled,edt.getText().toString());
	}

	private void setColorImage(ImageView img, boolean enabled, String edtTxt)
	{
		if (enabled){
			img.clearColorFilter();
			img.setColorFilter(getResources().getColor(!edtTxt.isEmpty() ? R.color.green : R.color.red ));
		}
		else {
			img.clearColorFilter();
			img.setColorFilter(getResources().getColor(R.color.grey));
		}
	}
	private void setColorImage(ImageView img)
	{
		setColorImage(img, true, "");
	}



	private void activeNextView()
	{
		mCurTalon = CurTalon.get(mCurTalon.getId());

		if (!mCurTalon.getCity().isEmpty()&& (!mCurTalon.getCity().equals("null") && !mCurTalon.getCity().equals("0"))) {
			setColorCtrl(edtCity, imgViewCity, true);
			setColorCtrl(edtLpu, imgViewLpu, true);
		}
		if (!mCurTalon.getLpu().isEmpty()&& (!mCurTalon.getLpu().equals("null") && !mCurTalon.getLpu().equals("0"))) {
			setColorCtrl(edtLpu, imgViewLpu, true);
			setColorCtrl(edtSpec, imgViewSpec, true);
		}
		if (!mCurTalon.getSpec().isEmpty()&& (!mCurTalon.getSpec().equals("null") && !mCurTalon.getSpec().equals("0")))	{
			setColorCtrl(edtSpec, imgViewSpec, true);
			setColorCtrl(edtDoct, imgViewDoct, true);
		}
		if (!mCurTalon.getDoctor().isEmpty() && (!mCurTalon.getDoctor().equals("null") && !mCurTalon.getDoctor().equals("0"))) {
			setColorCtrl(edtDoct, imgViewDoct, true);
		}

		if (Utils.isDebugMode()) dbgInfo.setText(mCurTalon.toString());
		if (! mCurAction.equals(actionAddFavorites)) {
			String tmpStr = mCurTalon.getTimeSchedule().toString();
			mSuccessInput = !tmpStr.equals("null") && !mCurTalon.getDoctor().equals(0);
		}
		else
		{
			mSuccessInput = !mCurTalon.getDoctor().equals("0") && !mCurTalon.getDoctor().equals("null");
		}
		btnCreateNewTalon.setEnabled (mSuccessInput);
	}

	@Override
	protected void onResume() {
		super.onResume();
		activeNextView();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO: Implement this method
		super.onActivityResult(requestCode, resultCode, data);
		if ( requestCode == REQUEST_GET_DATA){
			if(resultCode == RESULT_OK){
				String typeUrl = data.getStringExtra(GetDataActivity.TAG_ACTION);

				ContentValues cv = new ContentValues();
				switch (typeUrl.toString())
				{
				 case Actions.ACTION_GET_CITY_LIST:
				    String nameCity= data.getStringExtra(RES_EDT_TXT);
					 //скинуть контролы
					if(curLpuCity!= null && !curLpuCity.equals(data.getStringExtra(RES_LPU)))
					{
						curLpu = curDoct = curSpec = null;
						edtLpu.setText(""); edtSpec.setText(""); edtDoct.setText("");
						setColorCtrl(edtLpu, imgViewLpu, false);
						setColorCtrl(edtSpec, imgViewSpec, false);
						setColorCtrl(edtDoct, imgViewDoct, false);
					}
			        curLpuCity = data.getStringExtra(RES_LPU);
					 cv.put("City", curLpuCity);
					 cv.put("Lpu", 0);
					 cv.put("Spec", 0);
					 cv.put("Doctor", 0);
					 db.update(cv,"id=?", new String[]{"1"}, CurTalon.class);
			        edtCity.setText(nameCity.toString());
					break;
				case Actions.ACTION_GET_LPU_LIST:
					String nameLpu = data.getStringExtra(RES_EDT_TXT);
					//скинуть контролы
					if(curLpu!= null && !curLpu.equals(data.getStringExtra(RES_LPU)))
					{
						curDoct = curSpec = null;
						edtSpec.setText(""); edtDoct.setText("");
						setColorCtrl(edtSpec, imgViewSpec, false);
						setColorCtrl(edtDoct, imgViewDoct, false);
					}
					curLpu = data.getStringExtra(RES_LPU);
					cv.put("Lpu", curLpu);
					cv.put("Spec", 0);
					cv.put("Doctor", 0);
					db.update(cv,"id=?", new String[]{"1"}, CurTalon.class);
					edtLpu.setText(nameLpu.toString());
					break;
				case Actions.ACTION_GET_SPEC_LIST:
					String nameSpec = data.getStringExtra(RES_EDT_TXT);
					if(curSpec!= null && !curSpec.equals(data.getStringExtra(RES_LPU)))
					{
						curDoct = null;
						edtDoct.setText("");
						setColorCtrl(edtDoct, imgViewDoct, false);
					}
					curSpec = data.getStringExtra(RES_LPU);
					cv.put("Spec", curSpec);
					cv.put("Doctor", 0);
					db.update(cv,"id=?", new String[]{"1"}, CurTalon.class);
					edtSpec.setText(nameSpec.toString());
					break;
				case Actions.ACTION_GET_DOCT_LIST:
						String nameDoct = "";
						curDoct = data.getStringExtra(RES_LPU);
						if (db.size(Doctor.class) > 0)
						{
							Doctor doctor = Doctor.get(curDoct);
							if (doctor != null) nameDoct += doctor.getFamily()+" "+doctor.getName()+" "+doctor.getPatronymic();
						}
						cv.put("Doctor", curDoct);
						cv.put("DocPost", curDocPost);

						if (mCurAction.equals(actionAddTalon) || mCurAction.equals(actionReWrite)) {
							curTime = data.getStringExtra(RES_TIME_ID);
							String timeStr = data.getStringExtra(Actions.RESULT_ARRLST_RESULT_TIME_STR);
							String dateStr = data.getStringExtra(Actions.RESULT_ARRLST_RESULT_DATE_STR);
							curDocPost = data.getStringExtra(Actions.RESULT_ARRLST_RESULT_DOCPOST_ID);
							nameDoct += " " + dateStr + " " + timeStr;
							cv.put("TimeSchedule", curTime);
							cv.put("TimeStr", timeStr);
							cv.put("DateStr", dateStr);
						}
						db.update(cv, "id=?", new String[]{"1"}, CurTalon.class);
						edtDoct.setText(nameDoct.toString());
						break;
				}
			}
		}
		else{
			// если талон успешно зарезервирован
			if (requestCode == REQUEST_DLG_CONF_TALON){
				if(resultCode == Activity.RESULT_OK){
					setResult(Activity.RESULT_OK);
					// синхранизация с сервером
					finish();
				}
			}
		}
	}

	@Override
	public void sendRequestCode(int code) {
		Log.d(DbgTAG, "sendRequestCode -->");
	}

	@Override
	public void onBackPressed()
	{
		this.finish();
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// MenuInflater inflater = getMenuInflater();
		// inflater.inflate(R.menu.fragment_menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				this.onBackPressed();
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
