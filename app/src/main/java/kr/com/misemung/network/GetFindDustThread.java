package kr.com.misemung.network;

import android.os.Handler;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

import kr.com.misemung.ui.MainActivity;
import kr.com.misemung.vo.AirInfo;

/**
 * 대기정보를 가져오는 스레드
 *
 * @author kjh
 *
 */

/**
 * 기상청을 연결하는 스레드
 * 이곳에서 풀파서를 이용하여 기상청에서 정보를 받아와 각각의 array변수에 넣어줌
 * @author kjh
 */
public class GetFindDustThread extends Thread {	//기상청 연결을 위한 스레드
	static public boolean active = false;
	//파서용 변수
	private int data = 0;			//이건 파싱해서 array로 넣을때 번지
	public boolean isreceiver;
	private String sTotalCount;	//결과수
	private AirInfo airInfo = null;
	private String[] sSo2Value;
	private String[] sCoValue;
	private String[] sO3Value;
	private String[] sNo2Value;
	private String[] sPm10Value;
	private String[] sPm25Value;
	private String[] sKhaiValue;
	private String[] sKhaiGrade;
	private String[] sSo2Grade;
	private String[] sCoGrade;
	private String[] sO3Grade;
	private String[] sNo2Grade;
	private String[] sPm10Grade;
	private String[] sPm25Grade;	//예보시간,날짜,온도,풍향,습도,날씨
	private boolean bTotalCount,bDate,bSo2Value,bCoValue,bO3Value,bNo2Value,bPm10Value,bPm25Value
			,bKhaiValue,bKhaiGrade,bSo2Grade,bCoGrade,bO3Grade,bNo2Grade,bPm10Grade,bPm25Grade;	//여긴 저장을 위한 플래그들
	private boolean tResponse;	//이건 text로 뿌리기위한 플래그
	private String dongName;
	private Handler handler;	//날씨저장 핸들러
	private String searchDate="dataTerm=daily";
	private String station="stationName=";
	private String infoCnt="numOfRows=1";

	public GetFindDustThread(boolean receiver,String dong){

		Log.w("스레드가 받은 측정소", dong);
		handler=new Handler();
		isreceiver=receiver;
		//dongName=dong;
		try{
			dongName = URLEncoder.encode(dong, "utf-8");
		}catch(Exception e){

		}


		bTotalCount=bDate=bSo2Value=bCoValue=bO3Value=bNo2Value=bPm10Value=bKhaiValue=bKhaiGrade=bSo2Grade=bCoGrade=bO3Grade=bNo2Grade=bPm10Grade=false;	//부울상수는 false로 초기화해주자
	}
	public void run(){

		if(active){
			try{
				String[] sDate = new String[1000];
				sSo2Value=new String[1000];	//아황산가스 농도
				sCoValue=new String[1000];	//일산화탄소 농도
				sO3Value=new String[1000];	//오존 농도
				sNo2Value=new String[1000];	//이산화질소 농도
				sPm10Value=new String[1000];	//미세먼지 농도
				sPm25Value=new String[1000];	//초미세먼지 농도
				sKhaiValue=new String[1000];	//통합 대기환경수치
				sKhaiGrade=new String[1000];	//통합 대기환경 지수
				sSo2Grade=new String[1000];	//아황산가스 지수
				sCoGrade=new String[1000];	//일산화 탄소 지수
				sO3Grade=new String[1000];	//오존 지수
				sNo2Grade=new String[1000];	//이산화질소 지수
				sPm10Grade=new String[1000];	//미세먼지 지수
				sPm25Grade=new String[1000];	//초미세먼지 지수
				data=0;

				String dustUrl = API.REQUEST_FIND_DUST() +"?"
						+station+dongName+"&"+infoCnt+"&"+searchDate+"&ServiceKey="+API.SERVICE_KEY+"&ver=1.3";
				Log.w("스레드가 받은 ", dustUrl);
				URL url = new URL(dustUrl);		//URL객체생성
				InputStream is = url.openStream();	//연결할 url을 inputstream에 넣어 연결을 하게된다.

				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();	//이곳이 풀파서를 사용하게 하는곳
				factory.setNamespaceAware(true);									//이름에 공백도 인식
				XmlPullParser xpp = factory.newPullParser();							//풀파서 xpp라는 객체 생성

				xpp.setInput(is,"UTF-8");			//이렇게 하면 연결이 된다. 포맷형식은 utf-8로

				int eventType = xpp.getEventType();	//풀파서에서 태그정보를 가져온다.

				// 기상 정보 받을 데이터
				airInfo = new AirInfo();

				while(eventType != XmlPullParser.END_DOCUMENT){	//문서의 끝이 아닐때
					switch(eventType){
						case XmlPullParser.START_TAG:	//'<'시작태그를 만났을때

							if(xpp.getName().equals("dataTime")){	//측정일
								bDate=true;

							} if(xpp.getName().equals("so2Value")){		//아황산가스 농도
								bSo2Value=true;

							} if(xpp.getName().equals("coValue")){		//일산화탄소 농도
								bCoValue=true;

							} if(xpp.getName().equals("o3Value")){		//오존 농도
								bO3Value=true;

							} if(xpp.getName().equals("no2Value")){		//이산화질소 농도
								bNo2Value=true;

							} if(xpp.getName().equals("pm10Value")){	//미세먼지 농도
								bPm10Value=true;

							} if(xpp.getName().equals("pm25Value")){	//초미세먼지 농도
								bPm25Value=true;

							} if(xpp.getName().equals("khaiValue")){		//통합대기환경수치
								bKhaiValue=true;

							} if(xpp.getName().equals("khaiGrade")){	//통합대기환경지수
								bKhaiGrade=true;

							} if(xpp.getName().equals("so2Grade")){	//아황산가스 지수
								bSo2Grade=true;

							} if(xpp.getName().equals("coGrade")){	//일산화탄소 지수
								bCoGrade=true;

							} if(xpp.getName().equals("o3Grade")){	//오존 지수
								bO3Grade=true;

							} if(xpp.getName().equals("no2Grade")){	//이산화질소 지수
								bNo2Grade=true;

							} if(xpp.getName().equals("pm10Grade1h")){	//미세먼지 지수
								bPm10Grade=true;

							} if(xpp.getName().equals("pm25Grade1h")){	//초미세먼지 지수
								bPm25Grade=true;

							} if(xpp.getName().equals("totalCount")){	//결과 수
								bTotalCount=true;

							}

							break;

						case XmlPullParser.TEXT:	//텍스트를 만났을때
							//앞서 시작태그에서 얻을정보를 만나면 플래그를 true로 했는데 여기서 플래그를 보고
							//변수에 정보를 넣어준 후엔 플래그를 false로~
							if(bDate){				//동네이름
								sDate[data]=xpp.getText();		//측정일
								airInfo.setDate(xpp.getText());
								bDate=false;
							} if(bSo2Value){					//아황산가스 농도
								sSo2Value[data]=xpp.getText();
								airInfo.setSo2value(xpp.getText());
								bSo2Value=false;
							} if(bCoValue){				//일산화탄소 농도
								sCoValue[data]=xpp.getText();
								airInfo.setCovalue(xpp.getText());
								bCoValue=false;
							} if(bO3Value){				//오존 농도
								sO3Value[data]=xpp.getText();
								airInfo.setO3value(xpp.getText());
								bO3Value=false;
							} if(bNo2Value){				//이산화질소 농도
								sNo2Value[data]=xpp.getText();
								airInfo.setNo2value(xpp.getText());
								bNo2Value=false;
							} if(bPm10Value){				//미세먼지 농도
								sPm10Value[data]=xpp.getText();
								airInfo.setPm10value(xpp.getText());
								bPm10Value=false;
							} if(bPm25Value){				//초미세먼지 농도
								sPm25Value[data]=xpp.getText();
								airInfo.setPm25value(xpp.getText());
								bPm25Value=false;
							} if(bKhaiValue){				//통합대기환경수치
								sKhaiValue[data]=xpp.getText();
								airInfo.setKhaivalue(xpp.getText());
								bKhaiValue=false;
							} if(bKhaiGrade){				//통합대기환경지수
								sKhaiGrade[data]=xpp.getText();
								airInfo.setKhaigrade(xpp.getText());
								bKhaiGrade=false;
							} if(bSo2Grade){				//아황산가스 지수
								sSo2Grade[data]=xpp.getText();
								airInfo.setSo2grade(xpp.getText());
								bSo2Grade=false;
							} if(bCoGrade){				//일산화탄소 지수
								sCoGrade[data]=xpp.getText();
								airInfo.setCograde(xpp.getText());
								bCoGrade=false;
							} if(bO3Grade){				//오존 지수
								sO3Grade[data]=xpp.getText();
								airInfo.setO3grade(xpp.getText());
								bO3Grade=false;
							} if(bNo2Grade){				//이산화질소 지수
								sNo2Grade[data]=xpp.getText();
								airInfo.setNo2grade(xpp.getText());
								bNo2Grade=false;
							} if(bPm10Grade) {                //미세먼지 지수
								sPm10Grade[data] = xpp.getText();
								airInfo.setPm10grade1h(xpp.getText());
								bPm10Grade = false;
							} if(bPm25Grade){				//초미세먼지 지수
								sPm25Grade[data] = xpp.getText();
								airInfo.setPm25grade1h(xpp.getText());
								bPm25Grade=false;
							} if(bTotalCount){
								sTotalCount=xpp.getText();
								airInfo.setTotalCount(xpp.getText());
								bTotalCount=false;
							}
							break;

						case XmlPullParser.END_TAG:		//'</' 엔드태그를 만나면 (이부분이 중요)

							if(xpp.getName().equals("response")){	//태그가 끝나느 시점의 태그이름이 item이면(이건 거의 문서의 끝
								tResponse=true;						//따라서 이때 모든 정보를 화면에 뿌려주면 된다.
								view_text();					//뿌려주는 곳~
							}

							if(xpp.getName().equals("item")){	//item 예보시각기준 예보정보가 하나씩이다.
								data++;							//즉 item == 예보 개수 그러므로 이때 array를 증가해주자
							}
							break;
					}
					eventType = xpp.next();	//이건 다음 이벤트로~
				}

			}catch(Exception e){
				e.printStackTrace();
			}
		}



	}

	/**
	 * 이 부분이 뿌려주는곳 
	 * 뿌리는건 핸들러가~
	 * @author Ans
	 */
	private void view_text(){

        //기본 핸들러니깐 handler.post하면됨
        handler.post(() -> {

            active=false;
            if(tResponse){		//문서를 다 읽었다
                tResponse=false;
                data=0;		//
                ((MainActivity)MainActivity.mContext).FindDustThreadResponse(airInfo);

            }


        });
	}
}
