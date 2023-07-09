package kr.com.misemung.network

import android.content.Context
import android.os.*
import android.util.Log
import kr.com.misemung.realm.repository.CityRepository.City
import kr.com.misemung.ui.MainActivity
import kr.com.misemung.vo.CityInfo
import kr.com.misemung.vo.GeoInfo
import org.json.JSONObject

/**
 * GPS로 받은 위치를 가져오는 스레드
 *
 * @author kjh
 */
class GetTranscoordTask(
    private val context: Context?,
    receiver: Boolean,
    x: String,
    y: String,
    from: String,
    to: String
) {
    //파서용 변수
    private val data = 0 //이건 파싱해서 array로 넣을때 번지
    var isreceiver: Boolean
    private var getX: String?
    private var getY: String?
    private var addr //결과값
            : String?
    private val gridx: String
    private val gridy: String
    private val coordfrom: String
    private val coordto: String
    private val handler //값 핸들러
            : Handler
    private val getInfo = "v2/local/geo/coord2regioncode.json"
    private fun requestGeoInfo() {
        val request = Request(getInfo)
        request.addParam("x", gridx)
        request.addParam("y", gridy)
        request.addParam("input_coord", coordfrom)
        request.addParam("output_coord", coordto)
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
                                val ja = JSONObject(response.response).getJSONArray("documents")
                                val geoInfo = GeoInfo()

                                //for(int i=0; i<ja.length(); i++) {
                                val jo: JSONObject = ja.getJSONObject(0)
                                Log.e("GetTransCoordTask", "#### jo.getX : " + jo.optString("x"))
                                Log.e("GetTransCoordTask", "#### jo.getY : " + jo.optString("y"))
                                Log.e(
                                    "GetTransCoordTask",
                                    "#### jo.getRegion_2depth_name : " + jo.optString("region_2depth_name")
                                )
                                Log.e(
                                    "GetTransCoordTask",
                                    "#### jo.getRegion_3depth_name : " + jo.optString("region_3depth_name")
                                )
                                geoInfo.x = jo.optString("x")
                                geoInfo.y = jo.optString("y")
                                geoInfo.region_2depth_name = jo.optString("region_2depth_name")
                                geoInfo.region_3depth_name = jo.optString("region_3depth_name")
                                //}
                                parserData(geoInfo)
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

    private fun parserData(result: GeoInfo?): Void? {
        if (result == null) {
            return null
        } else {
            Log.w("========== parserData ", "getX ==> " + result.x)
            Log.i("========== parserData ", "getY ==> " + result.y)
            Log.i(
                "========== parserData ",
                "getRegion_2depth_name ==> " + result.region_2depth_name
            )
            Log.i(
                "========== parserData ",
                "getRegion_3depth_name ==> " + result.region_3depth_name
            )
            getX = result.x
            getY = result.y
            addr = result.region_2depth_name + " " + result.region_3depth_name
            showtext()
        }
        return null
    }

    /**
     * 이 부분이 뿌려주는곳
     * 뿌리는건 핸들러가~
     * @author kjh
     */
    private fun showtext() {

        //기본 핸들러니깐 handler.post하면됨
        handler.post {
            active = false
            val info = CityInfo()
            info.tmX = getX
            info.tmY = getY

            // DB에 저장
            City.setCurrentCity(1, addr, info)
            MainActivity.Companion.TransCoordThreadResponse(getX, getY, addr)
        }
    }

    companion object {
        // 스레드
        var active = false
    }

    init {
        handler = Handler()
        isreceiver = receiver
        gridx = x
        gridy = y
        coordfrom = from
        coordto = to
        getX = null
        getY = null
        addr = null

        // 좌표계 api 호출
        requestGeoInfo()
    }
}