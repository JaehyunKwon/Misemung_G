package kr.com.misemung.network

import android.content.Context
import android.os.*
import android.util.Log
import kr.com.misemung.ui.MainActivity
import kr.com.misemung.vo.AddrInfo
import kr.com.misemung.vo.CityInfo
import org.json.JSONObject
import java.util.*

/**
 * 주소 검색 리스트를 가져오는 스레드
 *
 * @author kjh
 */
class GetAddressTask(private val context: Context?, receiver: Boolean, address: String?) {
    //파서용 변수
    private var data = 0 //이건 파싱해서 array로 넣을때 번지
    private var cityInfoList: ArrayList<CityInfo>? = null
    private var isreceiver: Boolean = receiver
    private var getX: String? = null
    private var getY: String? = null
    private var addr //결과값
            : String?
    private val handler //값 핸들러
            : Handler = Handler()
    private val getInfo = "v2/local/search/address.json"
    private fun requestGeoInfo() {
        val request = Request(getInfo)
        request.addParam("query", addr)
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
                                var sub_addrObj: JSONObject // json "address" 부분 담는 JSONObject
                                val addrInfo = AddrInfo()
                                cityInfoList = ArrayList()
                                if (docArr.length() < 1) {
                                    parserData(null)
                                } else {
                                    for (i in 0 until docArr.length()) {
                                        data = i
                                        addrObj = docArr.getJSONObject(data)
                                        sub_addrObj = addrObj.getJSONObject("address")
                                        Log.e(
                                            "GetAddressTask",
                                            "#### jo.getRegion_2depth_name : " + sub_addrObj.optString(
                                                "region_2depth_name"
                                            )
                                        )
                                        Log.e(
                                            "GetAddressTask",
                                            "#### jo.getRegion_3depth_h_name : " + sub_addrObj.optString(
                                                "region_3depth_h_name"
                                            )
                                        )
                                        Log.e(
                                            "GetAddressTask",
                                            "#### jo.getRegion_3depth_name : " + sub_addrObj.optString(
                                                "region_3depth_name"
                                            )
                                        )
                                        Log.e(
                                            "GetAddressTask",
                                            "#### jo.getX : " + sub_addrObj.optString("x")
                                        )
                                        Log.e(
                                            "GetAddressTask",
                                            "#### jo.getY : " + sub_addrObj.optString("y")
                                        )
                                        val region_2depth_name =
                                            sub_addrObj.optString("region_2depth_name")
                                        val region_3depth_h_name =
                                            sub_addrObj.optString("region_3depth_h_name")
                                        val region_3depth_name =
                                            sub_addrObj.optString("region_3depth_name")
                                        val x = sub_addrObj.optString("x")
                                        val y = sub_addrObj.optString("y")
                                        addrInfo.address_name = (region_2depth_name
                                                + " " + if (region_3depth_h_name == "") region_3depth_name else region_3depth_h_name)
                                        addrInfo.x = x
                                        addrInfo.y = y
                                        parserData(addrInfo)
                                    }
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
        if (result != null) {
            Log.i("========== parserData ", "getAddress_name ==> " + result.address_name)
            Log.w("========== parserData ", "getX ==> " + result.x)
            Log.i("========== parserData ", "getY ==> " + result.y)
            addr = result.address_name
            getX = result.x
            getY = result.y
            val info = CityInfo()
            info.umdName = addr
            info.tmX = getX
            info.tmY = getY
            cityInfoList!!.add(data, info)
        }
        showtext()
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
            (MainActivity.Companion.mContext as MainActivity).SearchCityThreadResponse(cityInfoList)
        }
    }

    companion object {
        // 스레드
        var active = false
    }

    init {
        addr = address

        // 주소 api 호출
        requestGeoInfo()
    }
}