package kr.com.misemung.network

import android.content.Context
import android.os.*
import android.util.Log
import kr.com.misemung.realm.repository.CityRepository.City
import kr.com.misemung.ui.MainActivity
import kr.com.misemung.vo.AddrInfo
import kr.com.misemung.vo.CityInfo
import org.json.JSONObject

/**
 * 변환된 좌표계를 가져오는 스레드
 *
 * @author kjh
 */
class GetTranscoordTmTask(
    private val context: Context?,
    receiver: Boolean,
    addr: String?,
    x: String?,
    y: String?
) {
    var isreceiver: Boolean
    private var addrInfo: AddrInfo? = null
    private val getAddr: String?
    private val getX: String?
    private val getY //결과값
            : String?
    private val handler //값 핸들러
            : Handler
    private val getInfo = "v2/local/geo/transcoord.json"
    private fun requestGeoInfo() {
        val request = Request(getInfo)
        request.addParam("x", getX)
        request.addParam("y", getY)
        request.addParam("input_coord", "WGS84")
        request.addParam("output_coord", "TM")
        NetworkTask.Companion.requestExecutor(
            context,
            request,
            false,
            object : OnHttpResponseListener {
                override fun onHttpResponse(response: Response?) {
                    try {
                        if (response != null) {
                            if (response.api == getInfo) {
                                Log.e("#### onHttpResponse : ", "==================")
                                val docArr =
                                    JSONObject(response.response).getJSONArray("documents") // API 최상위 리스트
                                var addrObj: JSONObject
                                addrInfo = AddrInfo()
                                for (i in 0 until docArr.length()) {
                                    addrObj = docArr.getJSONObject(i)
                                    Log.e(
                                        "GetTranscoordTmTask",
                                        "#### jo.getX : " + addrObj.optString("x")
                                    )
                                    Log.e(
                                        "GetTranscoordTmTask",
                                        "#### jo.getY : " + addrObj.optString("y")
                                    )
                                    val x = addrObj.optString("x")
                                    val y = addrObj.optString("y")
                                    addrInfo!!.x = x
                                    addrInfo!!.y = y
                                    parserData(addrInfo)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            },
            false
        )
    }

    private fun parserData(result: AddrInfo?): Void? {
        if (result == null) {
            return null
        } else {
            showtext()
        }
        return null
    }

    /**
     * 이 부분이 뿌려주는곳
     * 뿌리는건 핸들러가~
     * @author kjh
     */
    fun showtext() {

        //기본 핸들러니깐 handler.post하면됨
        handler.post {
            active = false
            val info = CityInfo()
            info.umdName = getAddr
            info.tmX = addrInfo!!.x
            info.tmY = addrInfo!!.y
            // DB에 저장
            City[info.umdName] = info

            // 가까운 측정소 위치 조회
            MainActivity.getNearStation(info.tmX, info.tmY)
        }
    }

    companion object {
        // 스레드
        var active = false
    }

    init {
        handler = Handler()
        isreceiver = receiver
        getAddr = addr
        getX = x
        getY = y

        // 주소 api 호출
        requestGeoInfo()
    }
}