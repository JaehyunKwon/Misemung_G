package kr.com.misemung.ui;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import kr.com.misemung.R;
import kr.com.misemung.network.GetFindDustThread;
import kr.com.misemung.network.GetSearchCityListThread;
import kr.com.misemung.network.GetStationListThread;
import kr.com.misemung.network.GetTransCoordTask;
import kr.com.misemung.ui.adapter.DustPagerAdapter;
import kr.com.misemung.ui.adapter.SearchAdapter;
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
	LocationManager locationManager;

	GoogleApiClient mGoogleApiClient;
	Location mLastLocation;

	private ArrayList<Fragment> fragment_list = new ArrayList<>();
	private ViewPager viewPager;

	private SearchAdapter searchAdapter;

	private EditText where;
	private ListView searchListView;
	private Button this_place;
	private Button this_detail_place;
	private String stationName = "";

	String from = "WGS84";
	String to = "TM";
	static int stationCnt = 0;
	public static Context mContext;    //static에서 context를 쓰기위해
	boolean mLocationPermissionGranted = false;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		getDeviceLocation();

	}

	private void getDeviceLocation() {
		if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
				Manifest.permission.ACCESS_FINE_LOCATION)
				== PackageManager.PERMISSION_GRANTED) {
			mLocationPermissionGranted = true;
			init();
		} else {
			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
					1000);
		}
	}

	public void init() {

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		mContext = this;    //static에서 context를 쓰기위해~

		viewPager = findViewById(R.id.viewpager);
		where = findViewById(R.id.where);
        searchListView = findViewById(R.id.searchListView);
		this_place = findViewById(R.id.this_place);
		this_detail_place = findViewById(R.id.this_detail_place);

		where.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		where.setInputType(InputType.TYPE_CLASS_TEXT);
		where.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEARCH) {
					String stationName;
					stationName = where.getText().toString();
                    getSearchCityDust(stationName);
					return true;
				}
				return false;
			}
		});
		mGoogleApiClient = new GoogleApiClient.Builder(this)    //google service
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();

		// 현재위치 검색 클릭
		this_place.setText("현재위치");
		this_place.setOnClickListener(this);

	}

	/**
	 * 대기정보 API 조회
	 * */
	public void getFindDust(String name) {    //대기정보를 가져오는 스레드

		stationName = name;
		GetFindDustThread.active = true;
		GetFindDustThread getweatherthread = new GetFindDustThread(false, stationName);        //스레드생성(UI 스레드사용시 system 뻗는다)
		getweatherthread.start();    //스레드 시작

	}

	/**
	 * 대기정보 API 조회 결과
	 * */
	public void FindDustThreadResponse(AirInfo airInfo) {    //대기정보 가져온 결과값
		stationCnt = 0;    //측정개수정보(여기선 1개만 가져온다
		stationCnt = Integer.parseInt(airInfo.getTotalCount());

		Log.w("stationcnt", String.valueOf(stationCnt));

		fragment_list.add(new DustFragment(airInfo, stationName));
		Log.w("MainActivity","fragment_list ==> "+fragment_list.size());
		viewPager.setAdapter(new DustPagerAdapter(getSupportFragmentManager(), fragment_list));

		GetFindDustThread.active = false;
		GetFindDustThread.interrupted();
	}

	/**
	 * 검색된 도시 API 조회
	 * */
	public void getSearchCityDust(String name) {    //대기정보를 가져오는 스레드

		stationName = name;
		GetSearchCityListThread.active = true;
		GetSearchCityListThread getstationthread = new GetSearchCityListThread(false, stationName);        //스레드생성(UI 스레드사용시 system 뻗는다)
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
	}

	public void getStationList(String name) {    //이건 측정소 정보가져올 스레드

		stationName = name;
		GetStationListThread.active = true;
		GetStationListThread getstationthread = new GetStationListThread(false, stationName);        //스레드생성(UI 스레드사용시 system 뻗는다)
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
		//where.setText(sStation[0]);
		this_detail_place.setVisibility(View.VISIBLE);
		this_detail_place.setText(sStation[0]);

		searchListView.setVisibility(View.GONE);

		getFindDust(sStation[0]);
		//GetFindDustThread.active = false;
		//GetFindDustThread.interrupted();
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
		}
		GetTransCoordTask.active = false;
	}


	/**
	 * 버튼에 대한 처리
	 */

	public void onClick(View v) {

		switch (v.getId()) {

			case R.id.this_place:
				findGPS();

				break;
			default:
				break;
		}
	}



	@Override
	public void onRequestPermissionsResult(int requestCode,
										   @NonNull String permissions[],
										   @NonNull int[] grantResults) {
		mLocationPermissionGranted = false;
		switch (requestCode) {
			case 1000: {
				// If request is cancelled, the result arrays are empty.
				for (String permission : permissions) {
					if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
						mLocationPermissionGranted = true;
						init();
					}
				}
			}
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
			Log.d("mLastLocation", String.valueOf(mLastLocation.getLatitude()) + "," + mLastLocation.getLongitude());
			getStation(String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()));
		} else {
		}

		mGoogleApiClient.disconnect();

	}

	private void alertCheckGPS() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("GPS 기능이 필요합니다. 활성화 시키겠습니까?")
				.setCancelable(false)
				.setPositiveButton("예",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								intent.addCategory(Intent.CATEGORY_DEFAULT);
								startActivity(intent);
							}
						})
				.setNegativeButton("아니오",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
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



