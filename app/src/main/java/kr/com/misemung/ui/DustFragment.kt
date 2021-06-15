package kr.com.misemung.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.com.misemung.R
import kr.com.misemung.common.CommonPopup
import kr.com.misemung.realm.entity.AirRecord
import kr.com.misemung.ui.adapter.DustGridAdapter
import kr.com.misemung.vo.AirInfo
import kr.com.misemung.vo.ListInfo

@SuppressLint("ValidFragment")
class DustFragment : Fragment, DustContract.View {
    private var scroll_view: NestedScrollView? = null
    private var ll_main: RelativeLayout? = null
    private var delete_layout: FrameLayout? = null
    private var main_place: TextView? = null
    private var main_level: TextView? = null
    private var main_desc: TextView? = null
    private var main_img: ImageView? = null
    private var bottom_layout: FrameLayout? = null
    private var list_recyclerView: RecyclerView? = null
    private var adapter: DustGridAdapter? = null
    private var airRecord: AirRecord? = null
    private var stationName: String? = null
    private var dust_level = 0
    private var mdust_level = 0
    private var mPresenter: DustPresenter? = null
    private var mDeleteDialog: Dialog? = null

    constructor() {}
    constructor(airRecord: AirRecord, stationName: String?) {
        this.airRecord = airRecord
        this.stationName = stationName
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_dust, container, false)
        scroll_view = rootView.findViewById(R.id.scroll_view)
        ll_main = rootView.findViewById(R.id.ll_main)
        delete_layout = rootView.findViewById(R.id.delete_layout)
        main_place = rootView.findViewById(R.id.main_place)
        main_level = rootView.findViewById(R.id.main_level)
        main_desc = rootView.findViewById(R.id.main_desc)
        main_img = rootView.findViewById(R.id.main_img)
        bottom_layout = rootView.findViewById(R.id.bottom_layout)
        list_recyclerView = rootView.findViewById(R.id.list_recyclerView)

        // 리사이클 뷰 그리드뷰 형식으로 선언
        var gridLayoutManager: GridLayoutManager? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            gridLayoutManager = GridLayoutManager(context, 2)
        }
        // 그리드 뷰 구분선
        list_recyclerView!!.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.HORIZONTAL
            )
        )
        list_recyclerView!!.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        list_recyclerView!!.layoutManager = gridLayoutManager

        // 리스트 item Adapter
        adapter = DustGridAdapter(context)
        if (airRecord == null) {
            airRecord = AirRecord()
            airRecord!!.id = 1
            airRecord!!.pm10value = "?"
            airRecord!!.pm25value = "?"
            airRecord!!.so2value = "?"
            airRecord!!.covalue = "?"
            airRecord!!.o3value = "?"
            airRecord!!.no2value = "?"
        }
        adapter!!.addItem(
            ListInfo(
                "미세먼지",
                transDustGrade(airRecord!!.pm10value),
                airRecord!!.pm10value + " ㎍/m³"
            )
        )
        adapter!!.addItem(
            ListInfo(
                "초미세먼지",
                transMicroDustGrade(airRecord!!.pm25value),
                airRecord!!.pm25value + " ㎍/m³"
            )
        )
        adapter!!.addItem(
            ListInfo(
                "아황산가스",
                transSO2Grade(airRecord!!.so2value),
                airRecord!!.so2value + " ppm"
            )
        )
        adapter!!.addItem(
            ListInfo(
                "일산화탄소",
                transCOGrade(airRecord!!.covalue),
                airRecord!!.covalue + " ppm"
            )
        )
        adapter!!.addItem(
            ListInfo(
                "오존",
                transO3Grade(airRecord!!.o3value),
                airRecord!!.o3value + " ppm"
            )
        )
        adapter!!.addItem(
            ListInfo(
                "이산화질소",
                transNO2Grade(airRecord!!.no2value),
                airRecord!!.no2value + " ppm"
            )
        )
        list_recyclerView!!.adapter = adapter
        list_recyclerView!!.isNestedScrollingEnabled = false

        // 메인 레벨 (미세먼지 등급이 초미세먼지보다 안 좋을 경우 미세먼지 등급기준)
        main_level!!.text =
            if (dust_level > mdust_level) transFinalGrade(dust_level) else transFinalGrade(
                mdust_level
            )
        if (airRecord!!.id == 1) {
            if (main_level!!.text == "위치알못") {
                mainCardSetLayout("주인님, 어디세요?", true, false)
            } else {
                mainCardSetLayout(stationName, false, false)
            }
        } else {
            mainCardSetLayout(stationName, false, true)
        }

        // delete 버튼 클릭시 리스트 삭제
        delete_layout!!.setOnClickListener(View.OnClickListener { v: View? ->
            mDeleteDialog = CommonPopup.showConfirmCancelDialog(
                context,  //getString(R.string.noti_popup_title),
                getString(R.string.delete_msg),
                { v1: View? ->
                    (MainActivity.Companion.mContext as MainActivity).getDeleteDustList(
                        airRecord!!.id
                    )
                    mDeleteDialog!!.dismiss()
                }
            ) { v2: View? -> mDeleteDialog!!.dismiss() }
        })

        // 아래 자세히 보기 버튼 클릭시 스크롤 포지션 맨 마지막으로 이동
        bottom_layout!!.setOnClickListener(View.OnClickListener { v: View? ->
            scroll_view!!.smoothScrollTo(
                0,
                ll_main!!.height
            )
        })
        return rootView
    }

    /**
     * 미세먼지 등급
     */
    fun transDustGrade(stringGrade: String?): String {
        val dTrans: String
        if (!stringGrade!!.contains("-")) {
            if (!stringGrade.contains("?")) {
                val grade = stringGrade.toInt()
                if (grade <= 15) { // 제일좋음
                    dTrans = "제일좋음"
                    dust_level = 1
                } else if (grade <= 30) { // 좋음
                    dTrans = "좋음"
                    dust_level = 2
                } else if (grade <= 40) { // 양호
                    dTrans = "양호"
                    dust_level = 3
                } else if (grade <= 50) { // 보통
                    dTrans = "보통"
                    dust_level = 4
                } else if (grade <= 75) { // 조심
                    dTrans = "조심"
                    dust_level = 5
                } else if (grade <= 100) { // 나쁨
                    dTrans = "나쁨"
                    dust_level = 6
                } else if (grade <= 150) { // 매우나쁨
                    dTrans = "매우나쁨"
                    dust_level = 7
                } else if (grade > 151) { // 최악
                    dTrans = "최악"
                    dust_level = 8
                } else {
                    dTrans = "정보없음"
                    dust_level = 0
                }
            } else {
                dTrans = "GPS OFF"
                dust_level = -1
            }
        } else {
            dTrans = "정보없음"
            dust_level = 0
        }
        return dTrans
    }

    /**
     * 초미세먼지 등급
     */
    fun transMicroDustGrade(microDust: String?): String {
        val mdTrans: String
        if (!microDust!!.contains("-")) {
            if (!microDust.contains("?")) {
                val grade = microDust.toInt()
                if (grade <= 8) { // 제일좋음
                    mdTrans = "제일좋음"
                    mdust_level = 1
                } else if (grade <= 15) { // 좋음
                    mdTrans = "좋음"
                    mdust_level = 2
                } else if (grade <= 20) { // 양호
                    mdTrans = "양호"
                    mdust_level = 3
                } else if (grade <= 25) { // 보통
                    mdTrans = "보통"
                    mdust_level = 4
                } else if (grade <= 37) { // 조심
                    mdTrans = "조심"
                    mdust_level = 5
                } else if (grade <= 50) { // 나쁨
                    mdTrans = "나쁨"
                    mdust_level = 6
                } else if (grade <= 75) { // 매우나쁨
                    mdTrans = "매우나쁨"
                    mdust_level = 7
                } else if (grade > 76) { // 최악
                    mdTrans = "최악"
                    mdust_level = 8
                } else {
                    mdTrans = "정보없음"
                    mdust_level = 0
                }
            } else {
                mdTrans = "GPS OFF"
                mdust_level = -1
            }
        } else {
            mdTrans = "정보없음"
            mdust_level = 0
        }
        return mdTrans
    }

    /**
     * 아황산가스 등급
     */
    fun transSO2Grade(stringGrade: String?): String {
        val dTrans: String
        dTrans = if (!stringGrade!!.contains("-")) {
            if (!stringGrade.contains("?")) {
                val grade = stringGrade.toFloat()
                if (grade <= 0.01) { // 제일좋음
                    "제일좋음"
                } else if (grade <= 0.02) { // 좋음
                    "좋음"
                } else if (grade <= 0.04) { // 양호
                    "양호"
                } else if (grade <= 0.05) { // 보통
                    "보통"
                } else if (grade <= 0.1) { // 조심
                    "조심"
                } else if (grade <= 0.15) { // 나쁨
                    "나쁨"
                } else if (grade <= 0.6) { // 매우나쁨
                    "매우나쁨"
                } else if (grade > 0.7) { // 최악
                    "최악"
                } else {
                    "정보없음"
                }
            } else {
                "GPS OFF"
            }
        } else {
            "정보없음"
        }
        return dTrans
    }

    /**
     * 일산화탄소 등급
     */
    fun transCOGrade(stringGrade: String?): String {
        val dTrans: String
        dTrans = if (!stringGrade!!.contains("-")) {
            if (!stringGrade.contains("?")) {
                val grade = stringGrade.toFloat()
                if (grade <= 1) { // 제일좋음
                    "제일좋음"
                } else if (grade <= 2) { // 좋음
                    "좋음"
                } else if (grade <= 5.5) { // 양호
                    "양호"
                } else if (grade <= 9) { // 보통
                    "보통"
                } else if (grade <= 12) { // 조심
                    "조심"
                } else if (grade <= 15) { // 나쁨
                    "나쁨"
                } else if (grade <= 32) { // 매우나쁨
                    "매우나쁨"
                } else if (grade > 33) { // 최악
                    "최악"
                } else {
                    "정보없음"
                }
            } else {
                "GPS OFF"
            }
        } else {
            "정보없음"
        }
        return dTrans
    }

    /**
     * 오존 등급
     */
    fun transO3Grade(stringGrade: String?): String {
        val dTrans: String
        dTrans = if (!stringGrade!!.contains("-")) {
            if (!stringGrade.contains("?")) {
                val grade = stringGrade.toFloat()
                if (grade <= 0.02) { // 제일좋음
                    "제일좋음"
                } else if (grade <= 0.03) { // 좋음
                    "좋음"
                } else if (grade <= 0.06) { // 양호
                    "양호"
                } else if (grade <= 0.09) { // 보통
                    "보통"
                } else if (grade <= 0.12) { // 조심
                    "조심"
                } else if (grade <= 0.15) { // 나쁨
                    "나쁨"
                } else if (grade <= 0.38) { // 매우나쁨
                    "매우나쁨"
                } else if (grade > 0.39) { // 최악
                    "최악"
                } else {
                    "정보없음"
                }
            } else {
                "GPS OFF"
            }
        } else {
            "정보없음"
        }
        return dTrans
    }

    /**
     * 이산화질소 등급
     */
    fun transNO2Grade(stringGrade: String?): String {
        val dTrans: String
        dTrans = if (!stringGrade!!.contains("-")) {
            if (!stringGrade.contains("?")) {
                val grade = stringGrade.toFloat()
                if (grade <= 0.02) { // 제일좋음
                    "제일좋음"
                } else if (grade <= 0.03) { // 좋음
                    "좋음"
                } else if (grade <= 0.05) { // 양호
                    "양호"
                } else if (grade <= 0.06) { // 보통
                    "보통"
                } else if (grade <= 0.13) { // 조심
                    "조심"
                } else if (grade <= 0.2) { // 나쁨
                    "나쁨"
                } else if (grade <= 1.1) { // 매우나쁨
                    "매우나쁨"
                } else if (grade > 1.2) { // 최악
                    "최악"
                } else {
                    "정보없음"
                }
            } else {
                "GPS OFF"
            }
        } else {
            "정보없음"
        }
        return dTrans
    }

    /**
     * 최종 등급
     */
    fun transFinalGrade(finalGrade: Int): String {
        var dTrans = ""
        when (finalGrade) {
            -1 -> {
                dTrans = "위치알못"
                main_level!!.setTextColor(resources.getColor(R.color.color_no_gps_text))
                ll_main!!.setBackgroundResource(R.drawable.rectangle_no_gps)
                main_img!!.setImageResource(R.drawable.main_no_gps)
                main_desc!!.setText(R.string.no_gps_desc)
                main_desc!!.setTextColor(resources.getColor(R.color.color_no_gps_text))
            }
            0 -> {
                dTrans = "정보없음"
                main_desc!!.setText(R.string.default_desc)
            }
            1 -> {
                dTrans = "제일좋음"
                ll_main!!.setBackgroundResource(R.drawable.rectangle_best)
                main_img!!.setImageResource(R.drawable.main_best)
                main_desc!!.setText(R.string.best_desc)
            }
            2 -> {
                dTrans = "좋음"
                ll_main!!.setBackgroundResource(R.drawable.rectangle_so_good)
                main_img!!.setImageResource(R.drawable.main_so_good)
                main_desc!!.setText(R.string.so_good_desc)
            }
            3 -> {
                dTrans = "양호"
                ll_main!!.setBackgroundResource(R.drawable.rectangle_good)
                main_img!!.setImageResource(R.drawable.main_good)
                main_desc!!.setText(R.string.good_desc)
            }
            4 -> {
                dTrans = "보통"
                ll_main!!.setBackgroundResource(R.drawable.rectangle_normal)
                main_img!!.setImageResource(R.drawable.main_normal)
                main_desc!!.setText(R.string.normal_desc)
            }
            5 -> {
                dTrans = "조심"
                ll_main!!.setBackgroundResource(R.drawable.rectangle_careful)
                main_img!!.setImageResource(R.drawable.main_careful)
                main_desc!!.setText(R.string.careful_desc)
            }
            6 -> {
                dTrans = "나쁨"
                ll_main!!.setBackgroundResource(R.drawable.rectangle_bad)
                main_img!!.setImageResource(R.drawable.main_bad)
                main_desc!!.setText(R.string.bad_desc)
            }
            7 -> {
                dTrans = "매우나쁨"
                ll_main!!.setBackgroundResource(R.drawable.rectangle_so_bad)
                main_img!!.setImageResource(R.drawable.main_so_bad)
                main_desc!!.setText(R.string.so_bad_desc)
            }
            8 -> {
                dTrans = "최악"
                ll_main!!.setBackgroundResource(R.drawable.rectangle_worst)
                main_img!!.setImageResource(R.drawable.main_worst)
                main_desc!!.setText(R.string.worst_desc)
            }
        }
        return dTrans
    }

    override fun showDustResult(airInfo: AirInfo?, name: String?) {
        // 리사이클 뷰 그리드뷰 형식으로 선언
        var gridLayoutManager: GridLayoutManager? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            gridLayoutManager = GridLayoutManager(context, 2)
        }
        list_recyclerView!!.layoutManager = gridLayoutManager

        // 리스트 item Adapter
        adapter = DustGridAdapter(context)
        adapter!!.addItem(
            ListInfo(
                "미세먼지",
                transDustGrade(airInfo!!.pm10value),
                airInfo!!.pm10value + " ㎍/m³"
            )
        )
        adapter!!.addItem(
            ListInfo(
                "초미세먼지",
                transMicroDustGrade(airInfo!!.pm25value),
                airInfo!!.pm25value + " ㎍/m³"
            )
        )
        adapter!!.addItem(
            ListInfo(
                "아황산가스",
                transSO2Grade(airInfo!!.so2value),
                airInfo!!.so2value + " ppm"
            )
        )
        adapter!!.addItem(
            ListInfo(
                "일산화탄소",
                transCOGrade(airInfo!!.covalue),
                airInfo!!.covalue + " ppm"
            )
        )
        adapter!!.addItem(
            ListInfo(
                "오존",
                transO3Grade(airInfo!!.o3value),
                airInfo!!.o3value + " ppm"
            )
        )
        adapter!!.addItem(
            ListInfo(
                "이산화질소",
                transNO2Grade(airInfo!!.no2value),
                airInfo!!.no2value + " ppm"
            )
        )
        list_recyclerView!!.adapter = adapter
        list_recyclerView!!.isNestedScrollingEnabled = false

        // 메인 레벨 (미세먼지 등급이 초미세먼지보다 안 좋을 경우 미세먼지 등급기준)
        main_level!!.text =
            if (dust_level > mdust_level) transFinalGrade(dust_level) else transFinalGrade(
                mdust_level
            )
        if (airRecord!!.id == 1) {
            if (main_level!!.text == "위치알못") {
                mainCardSetLayout("주인님, 어디세요?", true, false)
            } else {
                mainCardSetLayout(name, false, false)
            }
        } else {
            mainCardSetLayout(name, false, true)
        }
    }

    private fun mainCardSetLayout(name: String?, isCurrent: Boolean, isVisible: Boolean) {
        if (isCurrent) {
            main_place!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            main_place!!.text = "주인님, 어디세요?"
            main_place!!.setTextColor(resources.getColor(R.color.color_no_gps_text))
        } else {
            main_level!!.setTextColor(resources.getColor(R.color.white))
            main_place!!.setCompoundDrawablesWithIntrinsicBounds(R.drawable.group_2, 0, 0, 0)
            main_place!!.compoundDrawablePadding = 10
            main_place!!.text = name
            main_place!!.setTextColor(resources.getColor(R.color.white))
            main_desc!!.setTextColor(resources.getColor(R.color.white))
        }
        if (isVisible) {
            delete_layout!!.visibility = View.VISIBLE
        } else {
            delete_layout!!.visibility = View.INVISIBLE
        }
    }

    override fun reload(airInfo: AirInfo?, name: String?) {
        mPresenter = DustPresenter(this)
        mPresenter!!.loadFineDustData(airInfo, name)
    }
}