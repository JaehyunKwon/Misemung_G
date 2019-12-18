package kr.com.misemung.ui;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fsn.cauly.CaulyAdInfo;
import com.fsn.cauly.CaulyAdInfoBuilder;
import com.fsn.cauly.CaulyCloseAd;
import com.fsn.cauly.CaulyCloseAdListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import io.realm.RealmResults;
import kr.com.misemung.R;
import kr.com.misemung.network.GetAddressTask;
import kr.com.misemung.network.GetFindDustThread;
import kr.com.misemung.network.GetSearchCityListThread;
import kr.com.misemung.network.GetStationListThread;
import kr.com.misemung.network.GetTranscoordTask;
import kr.com.misemung.network.GetTranscoordTmTask;
import kr.com.misemung.realm.entity.AirRecord;
import kr.com.misemung.realm.entity.CityRecord;
import kr.com.misemung.realm.repository.AirRepository;
import kr.com.misemung.realm.repository.CityRepository;
import kr.com.misemung.ui.adapter.DustPagerAdapter;
import kr.com.misemung.ui.adapter.SearchAdapter;
import kr.com.misemung.util.BaseUtil;
import kr.com.misemung.util.HandlePreference;
import kr.com.misemung.vo.AirInfo;
import kr.com.misemung.vo.CityInfo;

/**
 * MainActivity
 *
 * @author kjh
 *
 */

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener
		, CaulyCloseAdListener {


	private static final int REQUEST_CODE_FINE_COARSE_PERMISSION = 10000;

	private SwipeRefreshLayout mSwipeRefreshLayout;	// 아래로 드래그 후 새로고침하는 레이아웃

	// GPS
	private FusedLocationProviderClient mFusedLocationClient;

	private ProgressBar loadingProgressBar;

	private MagicIndicator magicIndicator;

	private ArrayList<Pair<Fragment, String>> fragment_list = new ArrayList<>(); // 프레그먼트 arraylist
	private ArrayList<String> stationList = new ArrayList<>(); // 탭 title arraylist
	private ArrayList<String> stationArrList; // 배열을 저장하는 arraylist
	private ViewPager viewPager;

	private SearchAdapter searchAdapter;

	private EditText where;
	private ListView searchListView;
	private TextView searchNoData;
	private TextView refresh_guide;
	public static String stationName = "";

	private String from = "WGS84";
	private String to = "TM";
	private static int stationCnt = 0;
	public static Context mContext;    //static에서 context를 쓰기위해

	public static boolean getListFlag = false;
	public static boolean gpsListFlag = false;
	private int seq; // db에 리스트 id로 넣기 위한 seq

	private FragmentContainerHelper mFramentContainerHelper;

	//	private static final String APP_CODE = "CAULY";	// 테스트용
	private static final String APP_CODE = "iR75C70S";	// 상용
	private CaulyCloseAd mCloseAd;

	private GestureDetector mGestureDetector;
	private boolean isLockOnHorizontialAxis;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		// CloseAd 초기화
		CaulyAdInfo closeAdInfo = new CaulyAdInfoBuilder(APP_CODE).build();
		mCloseAd = new CaulyCloseAd();

		/*
		 * Optional //원하는 버튼의 문구를 설젇 할 수 있다. mCloseAd.setButtonText("취소", "종료");
		 * //원하는 텍스트의 문구를 설젇 할 수 있다. mCloseAd.setDescriptionText("종료하시겠습니까?");
		 */
		mCloseAd.setAdInfo(closeAdInfo);
		mCloseAd.setCloseAdListener(this);

		mGestureDetector = new GestureDetector(mContext, new BaseUtil.XScrollDetector());

		initLayout();
	}


	@SuppressLint("ClickableViewAccessibility")
	public void initLayout() {

		mContext = this;    //static에서 context를 쓰기위해~

		mSwipeRefreshLayout = findViewById(R.id.swipe_layout);
		loadingProgressBar = findViewById(R.id.loadingProgressBar);
		magicIndicator = findViewById(R.id.magic_indicator);
		viewPager = findViewById(R.id.viewpager);
		where = findViewById(R.id.where);
		searchListView = findViewById(R.id.search_list_view);
		searchNoData = findViewById(R.id.search_no_data);
		refresh_guide = findViewById(R.id.tv_refresh_guide);

		where.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		where.setInputType(InputType.TYPE_CLASS_TEXT);
		where.setOnEditorActionListener((v, actionId, event) -> {
			if(actionId == EditorInfo.IME_ACTION_SEARCH) {
				BaseUtil.hideSoftKeyboard(where);
				where.clearFocus();
				loadingProgressBar.setVisibility(View.VISIBLE);
				// API 호출
				getSearchCityDust(where.getText().toString());
				return true;
			}
			return false;
		});
		mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

		// 좌우 스와이프시 버벅거리는 현상 때문에 추가
		viewPager.setOnTouchListener(viewpagerTouchListener);

		// 아래로 드래그 후 새로고침
		mSwipeRefreshLayout.setOnRefreshListener(this);

		getLastKnownLocation();

		loadAllList(true);

	}

	/**
	 * 대기정보 API 조회
	 * */
	public void getFindDust(String name) {    //대기정보를 가져오는 스레드

		GetFindDustThread.active = true;
		GetFindDustThread getweatherthread = new GetFindDustThread(false, name);        //스레드생성(UI 스레드사용시 system 뻗는다)
		getweatherthread.start();    //스레드 시작

	}

	/**
	 * 대기정보 API 조회 결과
	 * */
	public void FindDustThreadResponse(AirInfo airInfo) {    //대기정보 가져온 결과값

		stationCnt = 0;    //측정개수정보(여기선 1개만 가져온다
		stationCnt = Integer.parseInt(airInfo.getTotalCount());

		Log.w("MainActivity", "stationCnt :: " + stationCnt);

		// 데이터가 0 일때 다음 station으로 검색
		if (stationCnt == 0) {
			getFindDust(stationName);
			return;
		}

		// GPS로 검색된 데이터
		if (gpsListFlag) {
			loadAllList(false);
			refresh_guide.setVisibility(View.GONE);

			AirRepository.Air.setCurrent(1, stationName, airInfo);

			DustContract.View view = (DustContract.View) fragment_list.get(0).first;
			view.reload(airInfo, stationName);

			mFramentContainerHelper.handlePageSelected(0);
			viewPager.setCurrentItem(0);

			Toast.makeText(mContext, "현재위치가 갱신되었습니다.", Toast.LENGTH_SHORT).show();

			gpsListFlag = false;
			return;
		}

		if (getListFlag) {
			//update
			AirRepository.Air.updateDustData(viewPager.getCurrentItem()+1, airInfo, stationName);

			//adapter 새로고침
			Objects.requireNonNull(viewPager.getAdapter()).notifyDataSetChanged();
			getListFlag = false;

			if (mSwipeRefreshLayout.isRefreshing()) {
				// 새로고침 완료
				mSwipeRefreshLayout.setRefreshing(false);
			}
		} else {

			if (fragment_list.size() == 0) {
				seq = 1;
			} else {
				Number id = AirRepository.Air.getId();
				seq = (int) id;
			}
			AirRepository.Air.set(stationName, airInfo);

			Log.i("MainActivity", "seq ==> " + seq);
			addFragmentList(seq, stationName);
		}

		GetFindDustThread.active = false;
		GetFindDustThread.interrupted();
	}

	private void addFragmentList(int id, String umdName) {

		Log.w("MainActivity", "id ==> " + id);
		AirRecord airRecord = AirRepository.Air.selectByDustData(id, umdName);
		fragment_list.add(new Pair<>(new DustFragment(airRecord, airRecord.stationName), airRecord.stationName));
		Log.w("MainActivity", "fragment_list ==> " + fragment_list.size());

		Objects.requireNonNull(viewPager.getAdapter()).notifyDataSetChanged();

		loadAllList(false);

	}

	private void loadAllList(boolean fadeAnim) {

		Log.i("MainActivity","getFragmentList_stationName :: "+ stationName);

		// 초기화
		fragment_list = new ArrayList<>();
		stationList = new ArrayList<>();

		AirRecord gpsRecord = AirRepository.Air.selectByGPSData(1);
		if (gpsRecord != null) {
			fragment_list.add(new Pair<>(new DustFragment(gpsRecord, gpsRecord.stationName), "현재위치"));
			stationList.add(0, "현재위치");
		} else {
			fragment_list.add(new Pair<>(new DustFragment(), "현재위치"));
			stationList.add(0, "현재위치");
		}

		RealmResults<AirRecord> airListRecord = AirRepository.Air.selectByAllList();
		if (airListRecord.size() > 0) {
			fadeAnimation(refresh_guide, fadeAnim);

			for (int i = 0; i < airListRecord.size(); i++) {
				AirRecord airRecord = AirRepository.Air.selectByDustData(airListRecord.get(i).id, airListRecord.get(i).stationName);

				stationList.add(airRecord.stationName);
				fragment_list.add(new Pair<>(new DustFragment(airRecord, airRecord.stationName), airRecord.stationName));
			}
		} else {
			refresh_guide.setVisibility(View.GONE);
		}

		viewPager.setAdapter(new DustPagerAdapter(getSupportFragmentManager(), fragment_list));
		Log.w("MainActivity", "getFragmentList_fragment_list ==> " + fragment_list.size());

		setTabTitleIndicator();

	}

	/**
	 * tab Indicator setting
	 * */
	private void setTabTitleIndicator() {
		magicIndicator.setBackgroundColor(Color.WHITE);
		CommonNavigator commonNavigator = new CommonNavigator(this);
		commonNavigator.setScrollPivotX(0.35f);
		commonNavigator.setAdapter(new CommonNavigatorAdapter() {
			@Override
			public int getCount() {
				return fragment_list.size();
			}

			@Override
			public IPagerTitleView getTitleView(Context context, final int index) {
				SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
				simplePagerTitleView.setText(stationList.get(index));
				simplePagerTitleView.setNormalColor(Color.parseColor("#8e8e8e"));
				simplePagerTitleView.setSelectedColor(Color.parseColor("#ffffff"));
				simplePagerTitleView.setOnClickListener(v -> setCurrentItem(viewPager, index));
				setCurrentItem(viewPager, index);
				return simplePagerTitleView;
			}

			@Override
			public IPagerIndicator getIndicator(Context context) {
				WrapPagerIndicator indicator = new WrapPagerIndicator(context);
				indicator.setFillColor(Color.parseColor("#9e9e9e"));
				return indicator;
			}
		});
		magicIndicator.setNavigator(commonNavigator);
		ViewPagerHelper.bind(magicIndicator, viewPager);
		mFramentContainerHelper = new FragmentContainerHelper(magicIndicator);
		mFramentContainerHelper.handlePageSelected(HandlePreference.getFragmentListSize());

		loadingProgressBar.setVisibility(View.GONE);
	}

	private void setCurrentItem(ViewPager viewPager, int index) {
		Log.i("MainActivity", "index ==> " + index);
		viewPager.setCurrentItem(index);
		HandlePreference.setFragmentListSize(index);
		Log.w("MainActivity", "getFragment ==> " + HandlePreference.getFragmentListSize());
	}

	/**
	 * fragment에서 넘어온 값으로 리스트 삭제
	 * */
	public void getDeleteDustList(int id) {

		loadingProgressBar.setVisibility(View.VISIBLE);

		Log.d("MainActivity", "delete_index ==> " + id);
		AirRepository.Air.deleteDustData(id);
		CityRepository.City.deleteCityData(id);

		stationList.remove(viewPager.getCurrentItem());

		//adapter 새로고침
		DustPagerAdapter adapter = (DustPagerAdapter) viewPager.getAdapter();
		Objects.requireNonNull(adapter).deletePage(viewPager.getCurrentItem());

		setTabTitleIndicator();

		Toast.makeText(mContext, "삭제되었습니다.", Toast.LENGTH_SHORT).show();

	}

	/**
	 * 검색된 도시 API 조회
	 * */
	public void getSearchCityDust(String name) {    //대기정보를 가져오는 스레드

		/*GetSearchCityListThread.active = true;
		GetSearchCityListThread getstationthread = new GetSearchCityListThread(false, name);        //스레드생성(UI 스레드사용시 system 뻗는다)
		getstationthread.start();    //스레드 시작*/

		GetAddressTask.active = true;
		new GetAddressTask(mContext, false, name);
	}

	/**
	 * 검색된 도시 API 조회 결과
	 * */
	public void SearchCityThreadResponse(ArrayList<CityInfo> cityInfos) {    //측정소 정보를 가져온 결과
		if (searchNoData.getVisibility() == View.VISIBLE) {
			searchNoData.setVisibility(View.GONE);
		}

		searchListView.setVisibility(View.VISIBLE);

		if (cityInfos.size() > 0) {
			searchAdapter = new SearchAdapter(this, cityInfos);
			searchListView.setAdapter(searchAdapter);
		} else {
			searchAdapter = new SearchAdapter(this, cityInfos);
			searchListView.setAdapter(searchAdapter);
			searchNoData.setVisibility(View.VISIBLE);
		}

		where.setText("");

		GetSearchCityListThread.active = false;
		GetSearchCityListThread.interrupted();

		loadingProgressBar.setVisibility(View.GONE);
	}

	public void getStationList(String name) {    //이건 측정소 정보가져올 스레드

		GetStationListThread.active = true;
		GetStationListThread getstationthread = new GetStationListThread(false, name);        //스레드생성(UI 스레드사용시 system 뻗는다)
		getstationthread.start();    //스레드 시작

	}

	public void StationListThreadResponse(String cnt, String[] sStation) {    //측정소 정보를 가져온 결과
		stationCnt = 0;
		stationCnt = Integer.parseInt(cnt);

		GetFindDustThread.active = false;
		GetFindDustThread.interrupted();

	}

	/**
	 * 위경도 -> TM 좌표계변환 API 조회
	 * */
	public void getTranscoord(String addr, String xGrid, String yGrid) {    //좌표계 변환

		GetTranscoordTmTask.active = true;
		new GetTranscoordTmTask(mContext, false, addr, xGrid, yGrid);        //스레드생성(UI 스레드사용시 system 뻗는다)

	}

	/**
	 * 가까운 측정소 API 조회
	 * */
	public static void getNearStation(String xGrid, String yGrid) {    //이건 측정소 정보가져올 스레드

		GetStationListThread.active = true;
		GetStationListThread getstationthread = new GetStationListThread(false, xGrid, yGrid);        //스레드생성(UI 스레드사용시 system 뻗는다)
		getstationthread.start();    //스레드 시작

	}

	/**
	 * 가까운 측정소 API 조회 결과
	 * */
	public void NearStationThreadResponse(String[] sStation) {    //측정소 정보를 가져온 결과
		searchListView.setVisibility(View.GONE);

		// 결과가 나온 측정소 배열 list에 저장
		stationArrList = new ArrayList<>();
		stationArrList.addAll(Arrays.asList(sStation));

		// 처음엔 0번째 station으로 검색
		getFindDust(stationArrList.get(0));

		GetStationListThread.active = false;
		GetStationListThread.interrupted();
	}

	/**
	 * GPS 위치 받은 값으로 조회
	 * */
	public void getStation(String xGrid, String yGrid) {

		if (xGrid != null && yGrid != null) {
			GetTranscoordTask.active = true;
			new GetTranscoordTask(mContext, false, xGrid, yGrid, from, to);        //스레드생성(UI 스레드사용시 system 뻗는다)
		} else {
			Toast.makeText(mContext, "좌표값 잘못 되었습니다.", Toast.LENGTH_SHORT).show();
		}

	}

	public static void TransCoordThreadResponse(String x, String y, String addr) {    //대기정보 가져온 결과값
		if (x == null || y == null) {
			return;
		}

		if (x.equals("NaN") || y.equals("NaN")) {
			Toast.makeText(mContext, "제대로 된 좌표값이 전달 되지 않았습니다.", Toast.LENGTH_LONG).show();
		} else {
			stationName = addr;
			getNearStation(x, y);
			getListFlag = false;
		}
		GetTranscoordTask.active = false;
	}


	/**
	 * viewPager 좌우 스와이프 리스너
	 */
	View.OnTouchListener viewpagerTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (!isLockOnHorizontialAxis)
				isLockOnHorizontialAxis = mGestureDetector.onTouchEvent(event);

			if (event.getAction() == MotionEvent.ACTION_UP)
				isLockOnHorizontialAxis = false;

			if (isLockOnHorizontialAxis) {
				mSwipeRefreshLayout.setEnabled(false);
			} else if (!isLockOnHorizontialAxis) {
				mSwipeRefreshLayout.setEnabled(true);
			}
			return false;
		}
	};

	public void getLastKnownLocation() {
		if (ActivityCompat.checkSelfPermission(this,
				Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
				&& ActivityCompat.checkSelfPermission(this,
				Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// 권한 요청
			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
					REQUEST_CODE_FINE_COARSE_PERMISSION);
			return;
		}

		mFusedLocationClient.getLastLocation()
				.addOnSuccessListener(this, location -> {
					// Got last known location. In some rare situations this can be null.
					if (location != null) {
						gpsListFlag = true;
						getStation(String.valueOf(location.getLongitude()), String.valueOf(location.getLatitude()));
					}
				});
	}

	@Override
	public void onPointerCaptureChanged(boolean hasCapture) {

	}

	public void fadeAnimation(final View tv, boolean isfadeOut) {

		final Animation animationFade;

		tv.setAlpha(0f);

		if (isfadeOut) {
			animationFade = AnimationUtils.loadAnimation(mContext, R.anim.fadeout);
		} else {
			animationFade = AnimationUtils.loadAnimation(mContext, R.anim.fadein);
		}

		Handler mhandler = new Handler();
		mhandler.postDelayed(() -> {

            // TODO Auto-generated method stub
            tv.setAlpha(1f);
			animationFade.setAnimationListener(new Animation.AnimationListener()
			{
				public void onAnimationEnd(Animation animation)
				{
					tv.setVisibility(View.GONE);
				}
				public void onAnimationRepeat(Animation animation) {}
				public void onAnimationStart(Animation animation) {}
			});
            tv.startAnimation(animationFade);
        }, 0);

	}

	/**
	 * 새로고침
	 * */
	@Override
	public void onRefresh() {
		mSwipeRefreshLayout.setColorSchemeResources(
				android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light
		);

		getListFlag = true;

		CityRecord record = CityRepository.City.selectByCityData(viewPager.getCurrentItem()+1);
		if (record != null) {
			stationName = record.umdName;
			getNearStation(record.tmX, record.tmY);
		} else {
			if (mSwipeRefreshLayout.isRefreshing()) {
				// 새로고침 완료
				mSwipeRefreshLayout.setRefreshing(false);
			}
		}
	}

	@Override
	protected void onResume() {
		if (mCloseAd != null)
			mCloseAd.resume(this);
		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (searchListView.getVisibility() == View.VISIBLE) {
				searchListView.setVisibility(View.GONE);
			} else {
				// 카울리 종료 광고 팝업
				showCloseAd(null);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void showCloseAd(View button)
	{
		// 앱을 처음 설치하여 실행할 때, 필요한 리소스를 다운받았는지 여부.
		if (mCloseAd != null && mCloseAd.isModuleLoaded()) {
			mCloseAd.show(this);
			//광고의 수신여부를 체크한 후 노출시키고 싶은 경우, show(this) 대신 request(this)를 호출.
			//onReceiveCloseAd에서 광고를 정상적으로 수신한 경우 , show(this)를 통해 광고 노출

		} else {
			// 광고에 필요한 리소스를 한번만 다운받는데 실패했을 때 앱의 종료팝업 구현
			showDefaultClosePopup();
		}
	}

	private void showDefaultClosePopup() {
		new AlertDialog.Builder(this).setTitle("").setMessage("종료하시겠습니까?")
				.setPositiveButton("확인", (dialog, which) -> finish()).setNegativeButton("취소", null).show();
	}

	// CaulyCloseAdListener
	@Override
	public void onFailedToReceiveCloseAd(CaulyCloseAd ad, int errCode,
										 String errMsg) {

	}

	// CloseAd의 광고를 클릭하여 앱을 벗어났을 경우 호출되는 함수이다.
	@Override
	public void onLeaveCloseAd(CaulyCloseAd ad) {
	}

	// CloseAd의 request()를 호출했을 때, 광고의 여부를 알려주는 함수이다.
	@Override
	public void onReceiveCloseAd(CaulyCloseAd ad, boolean isChargable) {

	}

	// 왼쪽 버튼을 클릭 하였을 때, 원하는 작업을 수행하면 된다.
	@Override
	public void onLeftClicked(CaulyCloseAd ad) {

	}

	// 오른쪽 버튼을 클릭 하였을 때, 원하는 작업을 수행하면 된다.
	// Default로는 오른쪽 버튼이 종료로 설정되어있다.
	@Override
	public void onRightClicked(CaulyCloseAd ad) {
		finish();
	}

	@Override
	public void onShowedCloseAd(CaulyCloseAd ad, boolean isChargable) {

	}

}



