package kr.com.misemung.network;

import android.os.Handler;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import kr.com.misemung.ui.MainActivity;
import kr.com.misemung.ui.adapter.SearchAdapter;
import kr.com.misemung.vo.AirInfo;
import kr.com.misemung.vo.CityInfo;

/**
 * 대기정보를 가져오는 스레드
 *
 * @author kjh
 *
 */
public class GetSearchCityListThread extends Thread {	//기상청 연결을 위한 스레드
	public static boolean active = false;
	//파서용 변수
	private int data = 0;			//이건 파싱해서 array로 넣을때 번지
	public boolean isreceiver;
	private String[] sSidoName,sSggName,sUmdName,sTmX,sTmY;	//시도, 시군구, 읍면동, x,y좌표 값

	private boolean bSidoName,bSggName,bUmdName,bTmX,bTmY;	//여긴 저장을 위한 플래그들

	private CityInfo cityInfo = null;
	private ArrayList<CityInfo> cityInfoList = null;

	private boolean tResponse;	//이건 text로 뿌리기위한 플래그

	private Handler handler;	//날씨저장 핸들러
	private String stationUrl;
	private String umdName="umdName=";
	private String infoCnt="numOfRows=200";

	public GetSearchCityListThread(boolean receiver, String umd){

		Log.w("전달받은 위치", umd);
		handler = new Handler();
		isreceiver = receiver;
		try{
			umdName+=URLEncoder.encode(umd, "utf-8");
		} catch (Exception ignored) {
			ignored.printStackTrace();
			Log.e("GetSearchStation", "GetSearchStation_Exception ==> " + ignored);
		}

		stationUrl = API.REQUEST_FIND_SEARCH()+"?"+umdName+"&"+infoCnt+"&ServiceKey="+API.SERVICE_KEY;

	}

	public void run(){

		if(active){
			try{
				bSidoName=bSggName=bUmdName=bTmX=bTmY=false;
				sSidoName=new String[100];	//시도이름
				sSggName=new String[100];	//시군구
				sUmdName=new String[100];	//읍면동
				sTmX=new String[100];	//x좌표
				sTmY=new String[100];	//y좌표
				data=0;

				URL url=new URL(stationUrl);		//URL객체생성
				Log.w("스레드가 받은 ", stationUrl);
				InputStream is = url.openStream();	//연결할 url을 inputstream에 넣어 연결을 하게된다.

				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();	//이곳이 풀파서를 사용하게 하는곳
				factory.setNamespaceAware(true);									//이름에 공백도 인식
				XmlPullParser xpp = factory.newPullParser();						//풀파서 xpp라는 객체 생성

				xpp.setInput(is,"UTF-8");			//이렇게 하면 연결이 된다. 포맷형식은 utf-8로

				int eventType = xpp.getEventType();	//풀파서에서 태그정보를 가져온다.

				// 기상 정보 받을 데이터
				cityInfo = new CityInfo();
				cityInfoList = new ArrayList<>();
				while(eventType!= XmlPullParser.END_DOCUMENT){	//문서의 끝이 아닐때

					switch(eventType){
						case XmlPullParser.START_TAG:	//'<'시작태그를 만났을때

							if(xpp.getName().equals("sidoName")){	//시도
								bSidoName=true;
							}if(xpp.getName().equals("sggName")){	//시군구
								bSggName=true;
							}if(xpp.getName().equals("umdName")){	//읍면동
								bUmdName=true;
							}if(xpp.getName().equals("tmX")){	//x좌표
								bTmX=true;
							}if(xpp.getName().equals("tmY")){	//y좌표
								bTmY=true;
							}

							break;

						case XmlPullParser.TEXT:	//텍스트를 만났을때
							//앞서 시작태그에서 얻을정보를 만나면 플래그를 true로 했는데 여기서 플래그를 보고
							//변수에 정보를 넣어준 후엔 플래그를 false로~
							if(bSidoName){				//시도이름
								sSidoName[data]=xpp.getText();
								cityInfo.setSidoName(sSidoName[data]);
								bSidoName=false;
							}if(bSggName){
								sSggName[data]=xpp.getText();
								cityInfo.setSggName(sSggName[data]);
								bSggName=false;
							}if(bUmdName){
								sUmdName[data]=xpp.getText();
								cityInfo.setUmdName(sUmdName[data]);
								bUmdName=false;
							}if(bTmX){
								sTmX[data]=xpp.getText();
								cityInfo.setTmX(sTmX[data]);
								bTmX=false;
							}if(bTmY){
								sTmY[data]=xpp.getText();
								cityInfo.setTmY(sTmY[data]);
								bTmY=false;
							}
							break;

						case XmlPullParser.END_TAG:		//'</' 엔드태그를 만나면 (이부분이 중요)

							if(xpp.getName().equals("response")){	//respose는 문서의 끝이므로
								tResponse=true;						//따라서 이때 모든 정보를 화면에 뿌려주면 된다.
								view_text();					//뿌려주는 곳~
							}

							if(xpp.getName().equals("item")){	//item 예보시각기준 예보정보가 하나씩이다.
								cityInfoList.add(data, cityInfo);
								cityInfo = new CityInfo();
								data++;							//즉 item == 예보 개수 그러므로 이때 array를 증가해주자
							}
							break;
					}
					eventType=xpp.next();	//이건 다음 이벤트로~
				}

			}catch(Exception e){
				e.printStackTrace();
				Log.e("GetStationListThread", "run_Exception ==> " + e);
			}
		}

	}

	/**
	 * 이 부분이 뿌려주는곳 
	 * 뿌리는건 핸들러가~
	 * @author Ans
	 */
	private void view_text(){

		handler.post(new Runnable() {	//기본 핸들러니깐 handler.post하면됨

			@Override
			public void run() {

				active=false;
				if(tResponse){		//문서를 다 읽었다
					tResponse=false;
					data=0;
					((MainActivity)MainActivity.mContext).SearchCityThreadResponse(cityInfoList);
				}


			}
		});
	}
}
