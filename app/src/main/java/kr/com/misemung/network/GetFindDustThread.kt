package kr.com.misemung.network

import android.os.*
import android.util.Log
import kr.com.misemung.ui.MainActivity
import kr.com.misemung.vo.AirInfo
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.net.URL
import java.net.URLEncoder

/**
 * 대기정보를 가져오는 스레드
 *
 * @author kjh
 */
/**
 * 기상청을 연결하는 스레드
 * 이곳에서 풀파서를 이용하여 기상청에서 정보를 받아와 각각의 array변수에 넣어줌
 * @author kjh
 */
class GetFindDustThread(receiver: Boolean, dong: String?) : Thread() {
    //파서용 변수
    private var data = 0 //이건 파싱해서 array로 넣을때 번지
    var isreceiver: Boolean
    private var sTotalCount //결과수
            : String? = null
    private var airInfo: AirInfo? = null
    private lateinit var sSo2Value: Array<String?>
    private lateinit var sCoValue: Array<String?>
    private lateinit var sO3Value: Array<String?>
    private lateinit var sNo2Value: Array<String?>
    private lateinit var sPm10Value: Array<String?>
    private lateinit var sPm25Value: Array<String?>
    private lateinit var sKhaiValue: Array<String?>
    private lateinit var sKhaiGrade: Array<String?>
    private lateinit var sSo2Grade: Array<String?>
    private lateinit var sCoGrade: Array<String?>
    private lateinit var sO3Grade: Array<String?>
    private lateinit var sNo2Grade: Array<String?>
    private lateinit var sPm10Grade: Array<String?>
    private lateinit var sPm25Grade //예보시간,날짜,온도,풍향,습도,날씨
            : Array<String?>
    private var bTotalCount: Boolean
    private var bDate: Boolean
    private var bSo2Value: Boolean
    private var bCoValue: Boolean
    private var bO3Value: Boolean
    private var bNo2Value: Boolean
    private var bPm10Value: Boolean
    private var bPm25Value = false
    private var bKhaiValue: Boolean
    private var bKhaiGrade: Boolean
    private var bSo2Grade: Boolean
    private var bCoGrade: Boolean
    private var bO3Grade: Boolean
    private var bNo2Grade: Boolean
    private var bPm10Grade: Boolean
    private var bPm25Grade //여긴 저장을 위한 플래그들
            = false
    private var tResponse //이건 text로 뿌리기위한 플래그
            = false
    private var dongName: String? = null
    private val handler //날씨저장 핸들러
            : Handler
    private val searchDate = "dataTerm=daily"
    private val station = "stationName="
    private val infoCnt = "numOfRows=1"
    override fun run() {
        if (active) {
            try {
                val sDate = arrayOfNulls<String>(1000)
                sSo2Value = arrayOfNulls(1000) //아황산가스 농도
                sCoValue = arrayOfNulls(1000) //일산화탄소 농도
                sO3Value = arrayOfNulls(1000) //오존 농도
                sNo2Value = arrayOfNulls(1000) //이산화질소 농도
                sPm10Value = arrayOfNulls(1000) //미세먼지 농도
                sPm25Value = arrayOfNulls(1000) //초미세먼지 농도
                sKhaiValue = arrayOfNulls(1000) //통합 대기환경수치
                sKhaiGrade = arrayOfNulls(1000) //통합 대기환경 지수
                sSo2Grade = arrayOfNulls(1000) //아황산가스 지수
                sCoGrade = arrayOfNulls(1000) //일산화 탄소 지수
                sO3Grade = arrayOfNulls(1000) //오존 지수
                sNo2Grade = arrayOfNulls(1000) //이산화질소 지수
                sPm10Grade = arrayOfNulls(1000) //미세먼지 지수
                sPm25Grade = arrayOfNulls(1000) //초미세먼지 지수
                data = 0
                val dustUrl = (API.REQUEST_FIND_DUST() + "?"
                        + station + dongName + "&" + infoCnt + "&" + searchDate + "&ServiceKey=" + API.SERVICE_KEY + "&ver=1.3")
                Log.w("스레드가 받은 ", dustUrl)
                val url = URL(dustUrl) //URL객체생성
                val `is` = url.openStream() //연결할 url을 inputstream에 넣어 연결을 하게된다.
                val factory = XmlPullParserFactory.newInstance() //이곳이 풀파서를 사용하게 하는곳
                factory.isNamespaceAware = true //이름에 공백도 인식
                val xpp = factory.newPullParser() //풀파서 xpp라는 객체 생성
                xpp.setInput(`is`, "UTF-8") //이렇게 하면 연결이 된다. 포맷형식은 utf-8로
                var eventType = xpp.eventType //풀파서에서 태그정보를 가져온다.

                // 기상 정보 받을 데이터
                airInfo = AirInfo()
                while (eventType != XmlPullParser.END_DOCUMENT) {    //문서의 끝이 아닐때
                    when (eventType) {
                        XmlPullParser.START_TAG -> {
                            if (xpp.name == "dataTime") {    //측정일
                                bDate = true
                            }
                            if (xpp.name == "so2Value") {        //아황산가스 농도
                                bSo2Value = true
                            }
                            if (xpp.name == "coValue") {        //일산화탄소 농도
                                bCoValue = true
                            }
                            if (xpp.name == "o3Value") {        //오존 농도
                                bO3Value = true
                            }
                            if (xpp.name == "no2Value") {        //이산화질소 농도
                                bNo2Value = true
                            }
                            if (xpp.name == "pm10Value") {    //미세먼지 농도
                                bPm10Value = true
                            }
                            if (xpp.name == "pm25Value") {    //초미세먼지 농도
                                bPm25Value = true
                            }
                            if (xpp.name == "khaiValue") {        //통합대기환경수치
                                bKhaiValue = true
                            }
                            if (xpp.name == "khaiGrade") {    //통합대기환경지수
                                bKhaiGrade = true
                            }
                            if (xpp.name == "so2Grade") {    //아황산가스 지수
                                bSo2Grade = true
                            }
                            if (xpp.name == "coGrade") {    //일산화탄소 지수
                                bCoGrade = true
                            }
                            if (xpp.name == "o3Grade") {    //오존 지수
                                bO3Grade = true
                            }
                            if (xpp.name == "no2Grade") {    //이산화질소 지수
                                bNo2Grade = true
                            }
                            if (xpp.name == "pm10Grade1h") {    //미세먼지 지수
                                bPm10Grade = true
                            }
                            if (xpp.name == "pm25Grade1h") {    //초미세먼지 지수
                                bPm25Grade = true
                            }
                            if (xpp.name == "totalCount") {    //결과 수
                                bTotalCount = true
                            }
                        }
                        XmlPullParser.TEXT -> {
                            //앞서 시작태그에서 얻을정보를 만나면 플래그를 true로 했는데 여기서 플래그를 보고
                            //변수에 정보를 넣어준 후엔 플래그를 false로~
                            if (bDate) {                //동네이름
                                sDate[data] = xpp.text //측정일
                                airInfo!!.date = sDate[data]
                                bDate = false
                            }
                            if (bSo2Value) {                    //아황산가스 농도
                                sSo2Value[data] = xpp.text
                                airInfo!!.so2value = sSo2Value[data]
                                bSo2Value = false
                            }
                            if (bCoValue) {                //일산화탄소 농도
                                sCoValue[data] = xpp.text
                                airInfo!!.covalue = sCoValue[data]
                                bCoValue = false
                            }
                            if (bO3Value) {                //오존 농도
                                sO3Value[data] = xpp.text
                                airInfo!!.o3value = sO3Value[data]
                                bO3Value = false
                            }
                            if (bNo2Value) {                //이산화질소 농도
                                sNo2Value[data] = xpp.text
                                airInfo!!.no2value = sNo2Value[data]
                                bNo2Value = false
                            }
                            if (bPm10Value) {                //미세먼지 농도
                                sPm10Value[data] = xpp.text
                                airInfo!!.pm10value = sPm10Value[data]
                                bPm10Value = false
                            }
                            if (bPm25Value) {                //초미세먼지 농도
                                sPm25Value[data] = xpp.text
                                airInfo!!.pm25value = sPm25Value[data]
                                bPm25Value = false
                            }
                            if (bKhaiValue) {                //통합대기환경수치
                                sKhaiValue[data] = xpp.text
                                airInfo!!.khaivalue = sKhaiValue[data]
                                bKhaiValue = false
                            }
                            if (bKhaiGrade) {                //통합대기환경지수
                                sKhaiGrade[data] = xpp.text
                                airInfo!!.khaigrade = sKhaiGrade[data]
                                bKhaiGrade = false
                            }
                            if (bSo2Grade) {                //아황산가스 지수
                                sSo2Grade[data] = xpp.text
                                airInfo!!.so2grade = sSo2Grade[data]
                                bSo2Grade = false
                            }
                            if (bCoGrade) {                //일산화탄소 지수
                                sCoGrade[data] = xpp.text
                                airInfo!!.cograde = sCoGrade[data]
                                bCoGrade = false
                            }
                            if (bO3Grade) {                //오존 지수
                                sO3Grade[data] = xpp.text
                                airInfo!!.o3grade = sO3Grade[data]
                                bO3Grade = false
                            }
                            if (bNo2Grade) {                //이산화질소 지수
                                sNo2Grade[data] = xpp.text
                                airInfo!!.no2grade = sNo2Grade[data]
                                bNo2Grade = false
                            }
                            if (bPm10Grade) {                //미세먼지 지수
                                sPm10Grade[data] = xpp.text
                                airInfo!!.pm10grade1h = sPm10Grade[data]
                                bPm10Grade = false
                            }
                            if (bPm25Grade) {                //초미세먼지 지수
                                sPm25Grade[data] = xpp.text
                                airInfo!!.pm25grade1h = sPm25Grade[data]
                                bPm25Grade = false
                            }
                            if (bTotalCount) {
                                sTotalCount = xpp.text
                                airInfo!!.totalCount = sTotalCount
                                bTotalCount = false
                            }
                        }
                        XmlPullParser.END_TAG -> {
                            if (xpp.name == "response") {    //태그가 끝나느 시점의 태그이름이 item이면(이건 거의 문서의 끝
                                tResponse = true //따라서 이때 모든 정보를 화면에 뿌려주면 된다.
                                view_text() //뿌려주는 곳~
                            }
                            if (xpp.name == "item") {    //item 예보시각기준 예보정보가 하나씩이다.
                                data++ //즉 item == 예보 개수 그러므로 이때 array를 증가해주자
                            }
                        }
                    }
                    eventType = xpp.next() //이건 다음 이벤트로~
                }
            } catch (e: Exception) {
                e.printStackTrace()
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
                data = 0 //
                (MainActivity.Companion.mContext as MainActivity).FindDustThreadResponse(airInfo)
            }
        }
    }

    companion object {
        //기상청 연결을 위한 스레드
        var active = false
    }

    init {
        Log.w("스레드가 받은 측정소", dong!!)
        handler = Handler()
        isreceiver = receiver
        //dongName=dong;
        try {
            dongName = URLEncoder.encode(dong, "utf-8")
        } catch (e: Exception) {
        }
        bPm10Grade = false
        bNo2Grade = bPm10Grade
        bO3Grade = bNo2Grade
        bCoGrade = bO3Grade
        bSo2Grade = bCoGrade
        bKhaiGrade = bSo2Grade
        bKhaiValue = bKhaiGrade
        bPm10Value = bKhaiValue
        bNo2Value = bPm10Value
        bO3Value = bNo2Value
        bCoValue = bO3Value
        bSo2Value = bCoValue
        bDate = bSo2Value
        bTotalCount = bDate //부울상수는 false로 초기화해주자
    }
}