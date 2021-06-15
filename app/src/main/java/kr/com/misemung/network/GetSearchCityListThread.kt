package kr.com.misemung.network

import android.os.*
import android.util.Log
import kr.com.misemung.ui.MainActivity
import kr.com.misemung.vo.CityInfo
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.net.URL
import java.net.URLEncoder
import java.util.*

/**
 * 대기정보를 가져오는 스레드
 *
 * @author kjh
 */
class GetSearchCityListThread(receiver: Boolean, umd: String?) : Thread() {
    //파서용 변수
    private var data = 0 //이건 파싱해서 array로 넣을때 번지
    var isreceiver: Boolean
    private lateinit var sSidoName: Array<String?>
    private lateinit var sSggName: Array<String?>
    private lateinit var sUmdName: Array<String?>
    private lateinit var sTmX: Array<String?>
    private lateinit var sTmY //시도, 시군구, 읍면동, x,y좌표 값
            : Array<String?>
    private var bSidoName = false
    private var bSggName = false
    private var bUmdName = false
    private var bTmX = false
    private var bTmY //여긴 저장을 위한 플래그들
            = false
    private var cityInfo: CityInfo? = null
    private var cityInfoList: ArrayList<CityInfo>? = null
    private var tResponse //이건 text로 뿌리기위한 플래그
            = false
    private val handler //날씨저장 핸들러
            : Handler
    private val stationUrl: String
    private var umdName = "umdName="
    private val infoCnt = "numOfRows=200"
    override fun run() {
        if (active) {
            try {
                bTmY = false
                bTmX = bTmY
                bUmdName = bTmX
                bSggName = bUmdName
                bSidoName = bSggName
                sSidoName = arrayOfNulls(1000) //시도이름
                sSggName = arrayOfNulls(1000) //시군구
                sUmdName = arrayOfNulls(1000) //읍면동
                sTmX = arrayOfNulls(1000) //x좌표
                sTmY = arrayOfNulls(1000) //y좌표
                data = 0
                val url = URL(stationUrl) //URL객체생성
                Log.w("스레드가 받은 ", stationUrl)
                val `is` = url.openStream() //연결할 url을 inputstream에 넣어 연결을 하게된다.
                val factory = XmlPullParserFactory.newInstance() //이곳이 풀파서를 사용하게 하는곳
                factory.isNamespaceAware = true //이름에 공백도 인식
                val xpp = factory.newPullParser() //풀파서 xpp라는 객체 생성
                xpp.setInput(`is`, "UTF-8") //이렇게 하면 연결이 된다. 포맷형식은 utf-8로
                var eventType = xpp.eventType //풀파서에서 태그정보를 가져온다.

                // 기상 정보 받을 데이터
                cityInfo = CityInfo()
                cityInfoList = ArrayList()
                while (eventType != XmlPullParser.END_DOCUMENT) {    //문서의 끝이 아닐때
                    when (eventType) {
                        XmlPullParser.START_TAG -> {
                            if (xpp.name == "sidoName") {    //시도
                                bSidoName = true
                            }
                            if (xpp.name == "sggName") {    //시군구
                                bSggName = true
                            }
                            if (xpp.name == "umdName") {    //읍면동
                                bUmdName = true
                            }
                            if (xpp.name == "tmX") {    //x좌표
                                bTmX = true
                            }
                            if (xpp.name == "tmY") {    //y좌표
                                bTmY = true
                            }
                        }
                        XmlPullParser.TEXT -> {
                            //앞서 시작태그에서 얻을정보를 만나면 플래그를 true로 했는데 여기서 플래그를 보고
                            //변수에 정보를 넣어준 후엔 플래그를 false로~
                            if (bSidoName) {                //시도이름
                                sSidoName[data] = xpp.text
                                cityInfo!!.sidoName = sSidoName[data]
                                bSidoName = false
                            }
                            if (bSggName) {
                                sSggName[data] = xpp.text
                                cityInfo!!.sggName = sSggName[data]
                                bSggName = false
                            }
                            if (bUmdName) {
                                sUmdName[data] = xpp.text
                                cityInfo!!.umdName = sUmdName[data]
                                bUmdName = false
                            }
                            if (bTmX) {
                                sTmX[data] = xpp.text
                                cityInfo!!.tmX = sTmX[data]
                                bTmX = false
                            }
                            if (bTmY) {
                                sTmY[data] = xpp.text
                                cityInfo!!.tmY = sTmY[data]
                                bTmY = false
                            }
                        }
                        XmlPullParser.END_TAG -> {
                            if (xpp.name == "response") {    //respose는 문서의 끝이므로
                                tResponse = true //따라서 이때 모든 정보를 화면에 뿌려주면 된다.
                                view_text() //뿌려주는 곳~
                            }
                            if (xpp.name == "item") {    //item 예보시각기준 예보정보가 하나씩이다.
                                cityInfoList!!.add(data, cityInfo!!)
                                cityInfo = CityInfo()
                                data++ //즉 item == 예보 개수 그러므로 이때 array를 증가해주자
                            }
                        }
                    }
                    eventType = xpp.next() //이건 다음 이벤트로~
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("GetStationListThread", "run_Exception ==> $e")
            }
        }
    }

    /**
     * 이 부분이 뿌려주는곳
     * 뿌리는건 핸들러가~
     * @author Ans
     */
    private fun view_text() {

        //기본 핸들러니깐 handler.post하면됨
        handler.post {
            active = false
            if (tResponse) {        //문서를 다 읽었다
                tResponse = false
                data = 0
                (MainActivity.Companion.mContext as MainActivity).SearchCityThreadResponse(
                    cityInfoList
                )
            }
        }
    }

    companion object {
        //기상청 연결을 위한 스레드
        var active = false
    }

    init {
        Log.w("전달받은 위치", umd!!)
        handler = Handler()
        isreceiver = receiver
        try {
            umdName += URLEncoder.encode(umd, "utf-8")
        } catch (ignored: Exception) {
            ignored.printStackTrace()
            Log.e("GetSearchStation", "GetSearchStation_Exception ==> $ignored")
        }
        stationUrl =
            API.REQUEST_FIND_SEARCH() + "?" + umdName + "&" + infoCnt + "&ServiceKey=" + API.SERVICE_KEY
    }
}