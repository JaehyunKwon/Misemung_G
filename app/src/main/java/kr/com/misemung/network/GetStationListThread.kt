package kr.com.misemung.network

import android.os.*
import android.util.Log
import kr.com.misemung.ui.MainActivity
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.net.URL
import java.net.URLEncoder

/**
 * 대기정보를 가져오는 스레드
 *
 * @author kjh
 */
class GetStationListThread : Thread {
    //파서용 변수
    private var data = 0 //이건 파싱해서 array로 넣을때 번지
    var isreceiver: Boolean
    private var sTotalCount //결과수
            : String? = null
    private lateinit var sStationName: Array<String?>
    private lateinit var sAddr: Array<String?>
    private lateinit var sTm //측정소 이름
            : Array<String?>
    private var bStationName = false
    private var bTotalCount = false
    private var bAddr = false
    private var bTm //여긴 저장을 위한 플래그들
            = false
    private var tResponse //이건 text로 뿌리기위한 플래그
            = false
    private var handler //날씨저장 핸들러
            : Handler
    private var stationUrl: String
    private var addr = "addr="
    private val infoCnt = "numOfRows=200"
    private var xGrid: String? = "tmX="
    private var yGrid: String? = "tmY="
    private var getAPI = 0

    constructor(receiver: Boolean, sido: String?) {
        Log.w("스레드가 시이름", sido!!)
        handler = Handler()
        isreceiver = receiver
        try {
            addr += URLEncoder.encode(sido, "utf-8")
        } catch (ignored: Exception) {
            ignored.printStackTrace()
            Log.e("GetStationListThread", "GetStationListThread_Exception ==> $ignored")
        }
        getAPI = 1 //사용할 API 구분용
        stationUrl =
            API.REQUEST_FIND_SIDO() + "?" + addr + "&" + infoCnt + "&ServiceKey=" + API.SERVICE_KEY
    }

    constructor(receiver: Boolean, gridX: String?, gridY: String?) {
        Log.w("받은 TM좌표", "$gridY,$gridX")
        handler = Handler()
        isreceiver = receiver
        xGrid += gridX
        yGrid += gridY
        getAPI = 2 //사용할 API 구분용
        stationUrl =
            API.REQUEST_FIND_NEARBY() + "?" + xGrid + "&" + yGrid + "&" + infoCnt + "&ServiceKey=" + API.SERVICE_KEY
    }

    override fun run() {
        if (active) {
            try {
                bTm = false
                bAddr = bTm
                bStationName = bAddr
                sStationName = arrayOfNulls(1000) //측정소
                sAddr = arrayOfNulls(1000) //주소
                sTm = arrayOfNulls(1000) //거리
                data = 0
                val url = URL(stationUrl) //URL객체생성
                Log.w("스레드가 받은 ", stationUrl)
                val `is` = url.openStream() //연결할 url을 inputstream에 넣어 연결을 하게된다.
                val factory = XmlPullParserFactory.newInstance() //이곳이 풀파서를 사용하게 하는곳
                factory.isNamespaceAware = true //이름에 공백도 인식
                val xpp = factory.newPullParser() //풀파서 xpp라는 객체 생성
                xpp.setInput(`is`, "UTF-8") //이렇게 하면 연결이 된다. 포맷형식은 utf-8로
                var eventType = xpp.eventType //풀파서에서 태그정보를 가져온다.
                while (eventType != XmlPullParser.END_DOCUMENT) {    //문서의 끝이 아닐때
                    when (eventType) {
                        XmlPullParser.START_TAG -> {
                            if (xpp.name == "stationName") {    //측정소
                                bStationName = true
                            }
                            if (xpp.name == "addr") {    //주소
                                bAddr = true
                            }
                            if (xpp.name == "tm") {    //거리
                                bTm = true
                            }
                            if (xpp.name == "totalCount") {    //측정소 수
                                bTotalCount = true
                            }
                        }
                        XmlPullParser.TEXT -> {
                            //앞서 시작태그에서 얻을정보를 만나면 플래그를 true로 했는데 여기서 플래그를 보고
                            //변수에 정보를 넣어준 후엔 플래그를 false로~
                            if (bStationName) {                //동네이름
                                sStationName[data] = xpp.text
                                bStationName = false
                            }
                            if (bAddr) {
                                sAddr[data] = xpp.text
                                bAddr = false
                            }
                            if (bTm) {
                                sTm[data] = xpp.text
                                bTm = false
                            }
                            if (bTotalCount) {
                                sTotalCount = xpp.text
                                bTotalCount = false
                            }
                        }
                        XmlPullParser.END_TAG -> {
                            if (xpp.name == "response") {    //respose는 문서의 끝이므로
                                tResponse = true //따라서 이때 모든 정보를 화면에 뿌려주면 된다.
                                view_text() //뿌려주는 곳~
                            }
                            if (xpp.name == "dmY") {    //측정소 리스트의 경우 item태그가 2개이므로
                                data++ //dmY로 구분
                            }
                            if (xpp.name == "tm") {    //가까운 측정소 구분은 tm으로 구분
                                data++
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
                Log.e("GetStationListThread", "station cnt ==> $sTotalCount")
                data = 0 //
                if (getAPI == 1) (MainActivity.Companion.mContext as MainActivity).StationListThreadResponse(
                    sTotalCount
                ) else if (getAPI == 2) (MainActivity.Companion.mContext as MainActivity).NearStationThreadResponse(
                    sStationName
                )
            }
        }
    }

    companion object {
        //기상청 연결을 위한 스레드
        var active = false
    }
}