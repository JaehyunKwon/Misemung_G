package kr.com.misemung.ui;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

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

import io.realm.RealmResults;
import kr.com.misemung.R;
import kr.com.misemung.network.GetFindDustThread;
import kr.com.misemung.network.GetSearchCityListThread;
import kr.com.misemung.network.GetStationListThread;
import kr.com.misemung.network.GetTransCoordTask;
import kr.com.misemung.realm.entity.AirRecord;
import kr.com.misemung.realm.entity.CityRecord;
import kr.com.misemung.realm.repository.AirRepository;
import kr.com.misemung.realm.repository.CityRepository;
import kr.com.misemung.ui.adapter.DustPagerAdapter;
import kr.com.misemung.ui.adapter.SearchAdapter;
import kr.com.misemung.util.BaseUtil;
import kr.com.misemung.vo.AirInfo;
import kr.com.misemung.vo.CityInfo;

/**
 * MainActivity
 *
 * @author kjh
 *
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener
		, GoogleApiClient.OnConnectionFailedListener
		, GoogleApiClient.ConnectionCallbacks {

	//GPS
	private LocationManager locationManager;

	private GoogleApiClient mGoogleApiClient;
	private Location mLastLocation;

	private ProgressBar loadingProgressBar;

	private MagicIndicator magicIndicator;

	private ArrayList<Fragment> fragment_list = new ArrayList<>(); // 프레그먼트 arraylist
	private ArrayList<String> stationList = new ArrayList<>(); // 탭 title arraylist
	private ArrayList<String> stationArrList; // 배열을 저장하는 arraylist
	private ViewPager viewPager;

	private SearchAdapter searchAdapter;

	private EditText where;
	private ListView searchListView;
	private Button this_place;
	public static String stationName = "";

	private String from = "WGS84";
	private String to = "TM";
	private static int stationCnt = 0;
	public static Context mContext;    //static에서 context를 쓰기위해
	private boolean mLocationPermissionGranted = false;

	public static boolean getListFlag = false;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

        initLayout();
	}


	public void initLayout() {

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		mContext = this;    //static에서 context를 쓰기위해~

		loadingProgressBar = findViewById(R.id.loadingProgressBar);
        magicIndicator = findViewById(R.id.magic_indicator);
		viewPager = findViewById(R.id.viewpager);
		where = findViewById(R.id.where);
        searchListView = findViewById(R.id.searchListView);
		this_place = findViewById(R.id.this_place);

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
		mGoogleApiClient = new GoogleApiClient.Builder(this)    //google service
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();

		// 현재위치 검색 클릭
		this_place.setText("현재위치");
		this_place.setOnClickListener(this);

		// 처음 페이지 진입할때 DB 데이터 보고 새로 갱신
		/*RealmResults<CityRecord> cityList = CityRepository.City.selectByCityList();
		if (cityList != null) {
			for (CityRecord record : cityList) {
				loadingProgressBar.setVisibility(View.VISIBLE);
				stationName = record.umdName;
				// 가까운 측정소 찾기 API
				getNearStation(record.tmX, record.tmY);
				getListFlag = true;
			}
		}*/
        getFragmentList();

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

		Log.w("MainActivity", "stationCnt :: " + String.valueOf(stationCnt));

		// 데이터가 0 일때 다음 station으로 검색
		if (stationCnt == 0) {
			getFindDust(stationArrList.get(1));
			return;
		}

		AirRepository.Air.set(stationName, airInfo);

		if (getListFlag) {
			getFragmentList();
		} else {
			getAddFragmentList(stationName);
		}

		GetFindDustThread.active = false;
		GetFindDustThread.interrupted();
	}

	private void getAddFragmentList(String umdName) {
		RealmResults<AirRecord> airListRecord = AirRepository.Air.selectByList(umdName);

		if(airListRecord != null) {
			for (int i = 0; i < airListRecord.size(); i++) {
				fragment_list.add(new DustFragment(airListRecord.get(i), airListRecord.get(i).stationName));
                stationList.add(stationName);

				Log.w("MainActivity", "fragment_list ==> " + fragment_list.size());
				viewPager.setAdapter(new DustPagerAdapter(getSupportFragmentManager(), fragment_list));
            }
            viewPager.setCurrentItem(fragment_list.size());
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
                    simplePagerTitleView.setSelectedColor(Color.parseColor("#e94220"));
                    simplePagerTitleView.setOnClickListener(v -> viewPager.setCurrentItem(index));
                    return simplePagerTitleView;
                }

                @Override
                public IPagerIndicator getIndicator(Context context) {
                    WrapPagerIndicator indicator = new WrapPagerIndicator(context);
                    indicator.setFillColor(Color.parseColor("#ebe4e3"));
                    return indicator;
                }
            });
            magicIndicator.setNavigator(commonNavigator);
            ViewPagerHelper.bind(magicIndicator, viewPager);

			loadingProgressBar.setVisibility(View.GONE);
		}
	}

	private void getFragmentList() {
		RealmResults<CityRecord> cityList = CityRepository.City.selectByCityList();
		if (cityList != null) {
			for (CityRecord record : cityList) {
				stationName = record.umdName;
                stationList.add(stationName);
				Log.i("MainActivity","getFragmentList_stationName :: "+ stationName);
				RealmResults<AirRecord> airListRecord = AirRepository.Air.selectByList(stationName);
				for (int i = 0; i < airListRecord.size(); i++) {
					fragment_list.add(new DustFragment(airListRecord.get(i), airListRecord.get(i).stationName));
					viewPager.setAdapter(new DustPagerAdapter(getSupportFragmentManager(), fragment_list));
				}
				Log.w("MainActivity", "getFragmentList_fragment_list ==> " + fragment_list.size());
            }
			viewPager.setCurrentItem(fragment_list.size());
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
                    simplePagerTitleView.setSelectedColor(Color.parseColor("#e94220"));
                    simplePagerTitleView.setOnClickListener(v -> viewPager.setCurrentItem(index));
                    return simplePagerTitleView;
                }

                @Override
                public IPagerIndicator getIndicator(Context context) {
                    WrapPagerIndicator indicator = new WrapPagerIndicator(context);
                    indicator.setFillColor(Color.parseColor("#ebe4e3"));
                    return indicator;
                }
            });
            magicIndicator.setNavigator(commonNavigator);
            ViewPagerHelper.bind(magicIndicator, viewPager);

		}
		loadingProgressBar.setVisibility(View.GONE);
	}

	/**
	 * 검색된 도시 API 조회
	 * */
	public void getSearchCityDust(String name) {    //대기정보를 가져오는 스레드

		GetSearchCityListThread.active = true;
		GetSearchCityListThread getstationthread = new GetSearchCityListThread(false, name);        //스레드생성(UI 스레드사용시 system 뻗는다)
		getstationthread.start();    //스레드 시작

	}

	/**
	 * 검색된 도시 API 조회 결과
	 * */
	public void SearchCityThreadResponse(ArrayList<CityInfo> cityInfos) {    //측정소 정보를 가져온 결과
		searchListView.setVisibility(View.VISIBLE);

		searchAdapter = new SearchAdapter(this, cityInfos);
		searchListView.setAdapter(searchAdapter);

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
	public void NearStationThreadResponse(String[] sStation, String[] sAddr, String[] sTm) {    //측정소 정보를 가져온 결과
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
			GetTransCoordTask.active = true;
			new GetTransCoordTask(mContext, false, xGrid, yGrid, from, to);        //스레드생성(UI 스레드사용시 system 뻗는다)
		} else {
			Toast.makeText(getApplication(), "좌표값 잘못 되었습니다.", Toast.LENGTH_SHORT).show();
		}

	}

	public static void TransCoordThreadResponse(String x, String y) {    //대기정보 가져온 결과값
		if (x == null || y == null) {
			return;
		}

		if (x.equals("NaN") || y.equals("NaN")) {
		} else {
			//totalcnt.append("\r\n변환된 좌표값은 " + x + "," + y + "입니다.");
			getNearStation(x, y);
			getListFlag = false;
		}
		GetTransCoordTask.active = false;
	}


	/**
	 * 버튼에 대한 처리
	 */

	public void onClick(View v) {

		switch (v.getId()) {

			case R.id.this_place:
				loadingProgressBar.setVisibility(View.VISIBLE);
				findGPS();

				break;
			default:
				break;
		}
	}

	private void findGPS() {

		//GPS가 켜져있는지 체크
		//켜져있지 않으면 설정으로 이동.
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			//GPS 설정화면으로 이동
			alertCheckGPS();

		}
		//GPS 켜져있다면 위치 찾기.
		else {
			mGoogleApiClient.connect();
		}
	}

	@Override
	public void onConnected(Bundle bundle) {
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
				&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			return;
		}

		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		if (mLastLocation != null) {
			loadingProgressBar.setVisibility(View.GONE);
			Log.d("mLastLocation", String.valueOf(mLastLocation.getLongitude()) + "," + mLastLocation.getLatitude());
			getStation(String.valueOf(mLastLocation.getLongitude()), String.valueOf(mLastLocation.getLatitude()));
		} else {
		}

		mGoogleApiClient.disconnect();

	}

	private void alertCheckGPS() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("GPS 기능이 필요합니다. 활성화 시키겠습니까?")
				.setCancelable(false)
				.setPositiveButton("예",
						(dialog, id) -> {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            startActivity(intent);
                        })
				.setNegativeButton("아니오",
						(dialog, id) -> dialog.cancel());
		AlertDialog alert = builder.create();
		alert.show();
	}


	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

	}

	@Override
	public void onPointerCaptureChanged(boolean hasCapture) {

	}

}



