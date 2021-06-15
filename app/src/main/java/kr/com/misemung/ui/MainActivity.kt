package kr.com.misemung.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.text.InputType
import android.util.Log
import android.util.Pair
import android.view.*
import android.view.View.OnTouchListener
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import androidx.viewpager.widget.ViewPager
import com.fsn.cauly.CaulyAdInfoBuilder
import com.fsn.cauly.CaulyCloseAd
import com.fsn.cauly.CaulyCloseAdListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import io.realm.exceptions.RealmException
import kr.com.misemung.R
import kr.com.misemung.common.CommonPopup
import kr.com.misemung.common.Permission
import kr.com.misemung.databinding.ActivityMainBinding
import kr.com.misemung.network.*
import kr.com.misemung.realm.repository.AirRepository.Air
import kr.com.misemung.realm.repository.CityRepository.City
import kr.com.misemung.ui.adapter.DustPagerAdapter
import kr.com.misemung.ui.adapter.SearchAdapter
import kr.com.misemung.util.BaseUtil
import kr.com.misemung.util.BaseUtil.XScrollDetector
import kr.com.misemung.util.HandlePreference
import kr.com.misemung.vo.AirInfo
import kr.com.misemung.vo.CityInfo
import net.lucode.hackware.magicindicator.FragmentContainerHelper
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView
import java.util.*

/**
 * MainActivity
 *
 * @author kjh
 */
class MainActivity : AppCompatActivity(), OnRefreshListener, CaulyCloseAdListener {

    // GPS
    private var locatioNManager: LocationManager? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var fragmentList: ArrayList<Pair<Fragment, String?>> // 프레그먼트 arraylist
    private var stationList = ArrayList<String?>() // 탭 title arraylist
    private var searchAdapter: SearchAdapter? = null

    private val from = "WGS84"
    private val to = "TM"
    private var seq // db에 리스트 id로 넣기 위한 seq
            = 0
    private var mFramentContainerHelper: FragmentContainerHelper? = null
    private var mCloseAd: CaulyCloseAd? = null
    private var mGestureDetector: GestureDetector? = null
    private var isLockOnHorizontialAxis = false

    private var alertDialog: Dialog? = null

    private lateinit var binding: ActivityMainBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mContext = this

        // CloseAd 초기화
        val closeAdInfo = CaulyAdInfoBuilder(APP_CODE).build()
        mCloseAd = CaulyCloseAd()

        /*
		 * Optional //원하는 버튼의 문구를 설젇 할 수 있다. mCloseAd.setButtonText("취소", "종료");
		 * //원하는 텍스트의 문구를 설젇 할 수 있다. mCloseAd.setDescriptionText("종료하시겠습니까?");
		 */mCloseAd!!.setAdInfo(closeAdInfo)
        mCloseAd!!.setCloseAdListener(this)
        mGestureDetector = GestureDetector(mContext, XScrollDetector())
        initLayout()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun initLayout() {

        binding.where.imeOptions = EditorInfo.IME_ACTION_SEARCH
        binding.where.inputType = InputType.TYPE_CLASS_TEXT
        binding.where.setOnEditorActionListener(OnEditorActionListener setOnEditorActionListener@{ v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                BaseUtil.hideSoftKeyboard(binding.where)
                binding.where.clearFocus()
                binding.loadingProgressBar.visibility = View.VISIBLE
                // API 호출
                getSearchCityDust(binding.where.text.toString())
                return@setOnEditorActionListener true
            }
            false
        })
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // 좌우 스와이프시 버벅거리는 현상 때문에 추가
        //6binding.viewpager.setOnTouchListener(viewpagerTouchListener)

        // 아래로 드래그 후 새로고침
        binding.swipeLayout.setOnRefreshListener(this)

        locatioNManager = (getSystemService(Context.LOCATION_SERVICE) as LocationManager?)!!
        getEnabled(locatioNManager!!)

    }

    /**
     * 대기정보 API 조회
     */
    private fun getFindDust(name: String?) {    //대기정보를 가져오는 스레드
        GetFindDustThread.Companion.active = true
        val getweatherthread = GetFindDustThread(false, name) //스레드생성(UI 스레드사용시 system 뻗는다)
        getweatherthread.start() //스레드 시작
    }

    /**
     * 대기정보 API 조회 결과
     */
    fun FindDustThreadResponse(airInfo: AirInfo?) {    //대기정보 가져온 결과값
        stationCnt = 0 //측정개수정보(여기선 1개만 가져온다
        var cnt = Integer.parseInt(airInfo!!.totalCount)
        stationCnt = cnt
        Log.w("MainActivity", "stationCnt :: $stationCnt")

        // 데이터가 0 일때 다음 station으로 검색
        if (stationCnt == 0) {
            getFindDust(stationName)
            return
        }

        // GPS로 검색된 데이터
        if (gpsListFlag) {
            loadAllList(false)
            Air.setCurrent(1, stationName, airInfo)
            binding.tvRefreshGuide.visibility = View.GONE
            val view = fragmentList[0].first as DustContract.View
            view.reload(airInfo, stationName)
            mFramentContainerHelper!!.handlePageSelected(0)
            binding.viewpager.currentItem = 0
            Toast.makeText(mContext, "현재위치가 갱신되었습니다.", Toast.LENGTH_SHORT).show()
            gpsListFlag = false
            return
        }
        if (getListFlag) {
            //update
            Air.updateDustData(binding.viewpager.currentItem + 1, airInfo, stationName)

            //adapter 새로고침
            Objects.requireNonNull(binding.viewpager.adapter)!!.notifyDataSetChanged()
            getListFlag = false
            if (binding.swipeLayout.isRefreshing) {
                // 새로고침 완료
                binding.swipeLayout.isRefreshing = false
            }
        } else {
            seq = if (fragmentList.size == 0) {
                1
            } else {
                val id = Air.id as Int
                id
            }
            Air[stationName] = airInfo!!
            Log.i("MainActivity", "seq ==> $seq")
            addFragmentList(seq, stationName)
        }
        GetFindDustThread.Companion.active = false
        Thread.interrupted()
    }

    private fun addFragmentList(id: Int, umdName: String?) {
        Log.w("MainActivity", "id ==> $id")
        val airRecord = Air.selectByDustData(id, umdName)
        if (airRecord != null) {
            fragmentList.add(
                Pair(
                    DustFragment(airRecord, airRecord.stationName),
                    airRecord.stationName
                )
            )
        }
        Log.w("MainActivity", "fragment_list ==> " + fragmentList.size)
        Objects.requireNonNull(binding.viewpager.adapter)!!.notifyDataSetChanged()
        loadAllList(false)
    }

    private fun loadAllList(fadeAnim: Boolean) {
        Log.i("MainActivity", "getFragmentList_stationName :: $stationName")

        // 초기화
        fragmentList = ArrayList<Pair<Fragment, String?>>()
        stationList = ArrayList()
        val id = 1
        try {
            val selectByAllList = Air.selectByGPSData(id)
            Log.w("MainActivity", "gpsRecord :: $selectByAllList")

            if (selectByAllList != null) {
                fragmentList.add(Pair(DustFragment(selectByAllList, selectByAllList.stationName), "현재위치"))
            } else {
                fragmentList.add(Pair(DustFragment(null, null), "현재위치"))
            }
            stationList.add(0, "현재위치")
            val airListRecord = Air.selectByAllList()
            if (airListRecord.size > 0) {
                fadeAnimation(binding.tvRefreshGuide, fadeAnim)
                for (i in airListRecord.indices) {
                    if (airListRecord[i]!!.id != 1) {
                        val airRecord =
                            Air.selectByDustData(
                                airListRecord[i]!!.id,
                                airListRecord[i]!!.stationName
                            )
                        stationList.add(airRecord!!.stationName)
                        fragmentList.add(
                            Pair(
                                DustFragment(airRecord, airRecord.stationName),
                                airRecord.stationName
                            )
                        )
                    }
                }
            } else {
                binding.tvRefreshGuide.visibility = View.GONE
            }
            binding.viewpager.adapter = DustPagerAdapter(supportFragmentManager, fragmentList)
            Log.w("MainActivity", "getFragmentList_fragment_list ==> " + fragmentList.size)
            setTabTitleIndicator()
        } catch (e: RealmException) {
            Log.e("MainActivity", "RealmException :: $e")

        }
    }

    /**
     * tab Indicator setting
     */
    private fun setTabTitleIndicator() {
        binding.magicIndicator.setBackgroundColor(Color.WHITE)
        val commonNavigator = CommonNavigator(this)
        commonNavigator.scrollPivotX = 0.35f
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return fragmentList.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val simplePagerTitleView = SimplePagerTitleView(context)
                simplePagerTitleView.text = stationList[index]
                simplePagerTitleView.normalColor = Color.parseColor("#8e8e8e")
                simplePagerTitleView.selectedColor = Color.parseColor("#ffffff")
                simplePagerTitleView.setOnClickListener { v: View? ->
                    setCurrentItem(
                        binding.viewpager,
                        index
                    )
                }
                setCurrentItem(binding.viewpager, index)
                return simplePagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = WrapPagerIndicator(context)
                indicator.fillColor = Color.parseColor("#9e9e9e")
                return indicator
            }
        }
        binding.magicIndicator.navigator = commonNavigator
        ViewPagerHelper.bind(binding.magicIndicator, binding.viewpager)
        mFramentContainerHelper = FragmentContainerHelper(binding.magicIndicator)
        mFramentContainerHelper!!.handlePageSelected(HandlePreference.fragmentListSize)
        binding.loadingProgressBar.visibility = View.GONE
    }

    private fun setCurrentItem(viewPager: ViewPager?, index: Int) {
        Log.i("MainActivity", "index ==> $index")
        viewPager!!.currentItem = index
        HandlePreference.fragmentListSize = index
        Log.w("MainActivity", "getFragment ==> " + HandlePreference.fragmentListSize)
    }

    /**
     * fragment에서 넘어온 값으로 리스트 삭제
     */
    fun getDeleteDustList(id: Int) {
        binding.loadingProgressBar.visibility = View.VISIBLE
        Log.d("MainActivity", "delete_index ==> $id")
        Air.deleteDustData(id)
        City.deleteCityData(id)
        stationList.removeAt(binding.viewpager.currentItem)

        //adapter 새로고침
        val adapter = binding.viewpager.adapter as DustPagerAdapter?
        val item = binding.viewpager.currentItem
        Objects.requireNonNull(adapter)!!.deletePage(item)
        Log.e("MainActivity", "currentItem ==> $item")
        setTabTitleIndicator()
        loadAllList(false)
        Toast.makeText(mContext, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
    }

    /**
     * 검색된 도시 API 조회
     */
    private fun getSearchCityDust(name: String?) {    //대기정보를 가져오는 스레드

        /*GetSearchCityListThread.active = true;
		GetSearchCityListThread getstationthread = new GetSearchCityListThread(false, name);        //스레드생성(UI 스레드사용시 system 뻗는다)
		getstationthread.start();    //스레드 시작*/
        GetAddressTask.Companion.active = true
        GetAddressTask(mContext, false, name)
    }

    /**
     * 검색된 도시 API 조회 결과
     */
    fun SearchCityThreadResponse(cityInfos: ArrayList<CityInfo>?) {    //측정소 정보를 가져온 결과
        if (binding.searchNoData.visibility == View.VISIBLE) {
            binding.searchNoData.visibility = View.GONE
        }
        binding.searchListView.visibility = View.VISIBLE
        if (cityInfos!!.size > 0) {
            searchAdapter = SearchAdapter(this, cityInfos)
            binding.searchListView.adapter = searchAdapter
        } else {
            searchAdapter = SearchAdapter(this, cityInfos)
            binding.searchListView.adapter = searchAdapter
            binding.searchNoData.visibility = View.VISIBLE
        }
        binding.where.setText("")
        GetSearchCityListThread.Companion.active = false
        Thread.interrupted()
        binding.loadingProgressBar.visibility = View.GONE
    }

    fun getStationList(name: String?) {    //이건 측정소 정보가져올 스레드
        GetStationListThread.Companion.active = true
        val getstationthread = GetStationListThread(false, name) //스레드생성(UI 스레드사용시 system 뻗는다)
        getstationthread.start() //스레드 시작
    }

    fun StationListThreadResponse(cnt: String?) {    //측정소 정보를 가져온 결과
        stationCnt = 0
        stationCnt = cnt!!.toInt()
        GetFindDustThread.Companion.active = false
        Thread.interrupted()
    }

    /**
     * 위경도 -> TM 좌표계변환 API 조회
     */
    fun getTranscoord(addr: String?, xGrid: String?, yGrid: String?) {    //좌표계 변환
        GetTranscoordTmTask.Companion.active = true
        GetTranscoordTmTask(mContext, false, addr, xGrid, yGrid) //스레드생성(UI 스레드사용시 system 뻗는다)
    }

    /**
     * 가까운 측정소 API 조회 결과
     */
    fun NearStationThreadResponse(sStation: Array<String?>) {    //측정소 정보를 가져온 결과
        binding.searchListView.visibility = View.GONE

        // 결과가 나온 측정소 배열 list에 저장
        var stationArrList = ArrayList<String?>()
        stationArrList.addAll(sStation)

        // 처음엔 0번째 station으로 검색
        getFindDust(stationArrList[1])
        GetStationListThread.Companion.active = false
        Thread.interrupted()
    }

    /**
     * GPS 활성 여부
     * */
    private fun getEnabled(locatioNManager: LocationManager) {
        val gpsProvider = LocationManager.GPS_PROVIDER
        val networkProvider = LocationManager.NETWORK_PROVIDER
        // gps OFF 일 때
        if (!locatioNManager.isProviderEnabled(gpsProvider)) {
            alertDialog = CommonPopup.showConfirmCancelDialog(
                this,
                getString(R.string.gpsUse),
                getString(R.string.gpsOn),
                getString(R.string.cancel),
                getString(R.string.confirm),
                { view1: View? ->
                    loadAllList(true)
                    alertDialog!!.dismiss()
                }) { view2: View? ->
                // 위치정보 설정 Intent
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                alertDialog!!.dismiss()
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                locatioNManager.requestLocationUpdates(gpsProvider, 60000, 1.0f, locationListener)
                locatioNManager.requestLocationUpdates(
                    networkProvider,
                    60000,
                    1.0f,
                    locationListener
                )
            }
        }
    }

    /**
     * LocationListener
     * */
    var locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.d(
                "MainActivity", "GPS Location changed, Latitude: $location.latitude" +
                        ", Longitude: $location.longitude"
            )
            gpsListFlag = true
            getStation(location.longitude.toString(), location.latitude.toString())
        }

        override fun onProviderDisabled(provider: String) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    }

    /**
     * GPS 위치 받은 값으로 조회
     */
    private fun getStation(xGrid: String?, yGrid: String?) {
        if (xGrid != null && yGrid != null) {
            GetTranscoordTask.Companion.active = true
            GetTranscoordTask(mContext, false, xGrid, yGrid, from, to) //스레드생성(UI 스레드사용시 system 뻗는다)
        } else {
            Toast.makeText(mContext, "좌표값 잘못 되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * viewPager 좌우 스와이프 리스너
     */
    @SuppressLint("ClickableViewAccessibility")
    var viewpagerTouchListener = OnTouchListener { v, event ->
        if (!isLockOnHorizontialAxis) isLockOnHorizontialAxis =
            mGestureDetector!!.onTouchEvent(event)
        if (event.action == MotionEvent.ACTION_UP) isLockOnHorizontialAxis = false
        if (isLockOnHorizontialAxis) {
            binding.swipeLayout.isEnabled = false
        } else if (!isLockOnHorizontialAxis) {
            binding.swipeLayout.isEnabled = true
        }
        false
    }// Got last known location. In some rare situations this can be null.


    override fun onPointerCaptureChanged(hasCapture: Boolean) {}
    fun fadeAnimation(tv: View?, isfadeOut: Boolean) {
        val animationFade: Animation
        tv!!.alpha = 0f
        animationFade = if (isfadeOut) {
            AnimationUtils.loadAnimation(mContext, R.anim.fadeout)
        } else {
            AnimationUtils.loadAnimation(mContext, R.anim.fadein)
        }
        val mhandler = Handler()
        mhandler.postDelayed({


            // TODO Auto-generated method stub
            tv.alpha = 1f
            animationFade.setAnimationListener(object : AnimationListener {
                override fun onAnimationEnd(animation: Animation) {
                    tv.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation) {}
                override fun onAnimationStart(animation: Animation) {}
            })
            tv.startAnimation(animationFade)
        }, 0)
    }

    /**
     * 새로고침
     */
    override fun onRefresh() {
        binding.swipeLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
        getListFlag = true
        val record = City.selectByCityData(binding.viewpager.currentItem + 1)
        if (record != null) {
            stationName = record.umdName
            getNearStation(record.tmX, record.tmY)
        } else {
            if (binding.swipeLayout.isRefreshing) {
                // 새로고침 완료
                binding.swipeLayout.isRefreshing = false
            }
        }
    }

    override fun onResume() {
        if (mCloseAd != null) mCloseAd!!.resume(this)
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        locatioNManager?.removeUpdates(locationListener)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (binding.searchListView.visibility == View.VISIBLE) {
                binding.searchListView.visibility = View.GONE
            } else {
                // 카울리 종료 광고 팝업
                showCloseAd(null)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    fun showCloseAd(button: View?) {
        // 앱을 처음 설치하여 실행할 때, 필요한 리소스를 다운받았는지 여부.
        if (mCloseAd != null && mCloseAd!!.isModuleLoaded) {
            mCloseAd!!.show(this)
            //광고의 수신여부를 체크한 후 노출시키고 싶은 경우, show(this) 대신 request(this)를 호출.
            //onReceiveCloseAd에서 광고를 정상적으로 수신한 경우 , show(this)를 통해 광고 노출
        } else {
            // 광고에 필요한 리소스를 한번만 다운받는데 실패했을 때 앱의 종료팝업 구현
            showDefaultClosePopup()
        }
    }

    private fun showDefaultClosePopup() {
        AlertDialog.Builder(this).setTitle("").setMessage("종료하시겠습니까?")
            .setPositiveButton("확인") { dialog: DialogInterface?, which: Int -> finish() }
            .setNegativeButton("취소", null).show()
    }

    // CaulyCloseAdListener
    override fun onFailedToReceiveCloseAd(
        ad: CaulyCloseAd, errCode: Int,
        errMsg: String
    ) {
    }

    // CloseAd의 광고를 클릭하여 앱을 벗어났을 경우 호출되는 함수이다.
    override fun onLeaveCloseAd(ad: CaulyCloseAd) {}

    // CloseAd의 request()를 호출했을 때, 광고의 여부를 알려주는 함수이다.
    override fun onReceiveCloseAd(ad: CaulyCloseAd, isChargable: Boolean) {}

    // 왼쪽 버튼을 클릭 하였을 때, 원하는 작업을 수행하면 된다.
    override fun onLeftClicked(ad: CaulyCloseAd) {}

    // 오른쪽 버튼을 클릭 하였을 때, 원하는 작업을 수행하면 된다.
    // Default로는 오른쪽 버튼이 종료로 설정되어있다.
    override fun onRightClicked(ad: CaulyCloseAd) {
        finish()
    }

    override fun onShowedCloseAd(ad: CaulyCloseAd, isChargable: Boolean) {}

    companion object {
        private const val REQUEST_CODE_FINE_COARSE_PERMISSION = 10000
        var stationName: String? = ""
        private var stationCnt = 0
        var mContext //static에서 context를 쓰기위해
                : Context? = null
        var getListFlag = false
        var gpsListFlag = false

        //	private static final String APP_CODE = "CAULY";	// 테스트용
        private const val APP_CODE = "iR75C70S" // 상용

        /**
         * 가까운 측정소 API 조회
         */
        fun getNearStation(xGrid: String?, yGrid: String?) {    //이건 측정소 정보가져올 스레드
            GetStationListThread.Companion.active = true
            val getstationthread =
                GetStationListThread(false, xGrid, yGrid) //스레드생성(UI 스레드사용시 system 뻗는다)
            getstationthread.start() //스레드 시작
        }

        fun TransCoordThreadResponse(x: String?, y: String?, addr: String?) {    //대기정보 가져온 결과값
            if (x == null || y == null) {
                return
            }
            if (x == "NaN" || y == "NaN") {
                Toast.makeText(mContext, "제대로 된 좌표값이 전달 되지 않았습니다.", Toast.LENGTH_LONG).show()
            } else {
                stationName = addr
                getNearStation(x, y)
                getListFlag = false
            }
            GetTranscoordTask.Companion.active = false
        }
    }
}