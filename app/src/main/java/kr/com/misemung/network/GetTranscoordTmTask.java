package kr.com.misemung.network;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.com.misemung.realm.repository.CityRepository;
import kr.com.misemung.ui.MainActivity;
import kr.com.misemung.vo.AddrInfo;
import kr.com.misemung.vo.CityInfo;

import static kr.com.misemung.ui.MainActivity.getNearStation;

/**
 * 변환된 좌표계를 가져오는 스레드
 *
 * @author kjh
 *
 */

public class GetTranscoordTmTask {	// 스레드
	static public boolean active = false;
	private Context context;
	public boolean isreceiver;
	private AddrInfo addrInfo;
	private String getAddr, getX, getY;	//결과값
	private Handler handler;	//값 핸들러
	private String getInfo="v2/local/geo/transcoord.json";

	public GetTranscoordTmTask(Context context, boolean receiver, String addr, String x, String y){

		this.context = context;
		handler = new Handler();
		isreceiver = receiver;
		getAddr = addr;
		getX = x;
		getY = y;

		// 주소 api 호출
		requestGeoInfo();

	}

	private void requestGeoInfo() {
		Request request = new Request(getInfo);
		request.addParam("x", getX);
		request.addParam("y", getY);
		request.addParam("input_coord", "WGS84");
		request.addParam("output_coord", "TM");

		NetworkTask.requestExecutor(context, request, false, response -> {
            try{
                if(response.getApi().equals(getInfo)) {

                    Log.e("#### onHttpResponse : ", "==================");

                    JSONArray docArr = new JSONObject(response.getResponse()).getJSONArray("documents"); // API 최상위 리스트
                    JSONObject addrObj;
                    addrInfo = new AddrInfo();

					for(int i=0; i<docArr.length(); i++) {

						addrObj = docArr.getJSONObject(i);

						Log.e("GetTranscoordTmTask", "#### jo.getX : " + addrObj.optString("x"));
						Log.e("GetTranscoordTmTask", "#### jo.getY : " + addrObj.optString("y"));

						String x = addrObj.optString("x");
						String y = addrObj.optString("y");

						addrInfo.setX(x);
						addrInfo.setY(y);

						parserData(addrInfo);
					}
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }, false);
	}

	private Void parserData(AddrInfo result) {

		if(result == null){
			return null;
		} else {
			showtext();
		}

		return null;
	}


	/**
	 * 이 부분이 뿌려주는곳
	 * 뿌리는건 핸들러가~
	 * @author kjh
	 */
	public void showtext(){

        //기본 핸들러니깐 handler.post하면됨
        handler.post(() -> {

            active=false;

			CityInfo info = new CityInfo();
			info.setUmdName(getAddr);
			info.setTmX(addrInfo.getX());
			info.setTmY(addrInfo.getY());
			// DB에 저장
			CityRepository.City.set(info.getUmdName(), info);

			// 가까운 측정소 위치 조회
			getNearStation(info.getTmX(), info.getTmY());
		});

	}

}
