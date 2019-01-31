package kr.com.misemung.network;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import kr.com.misemung.ui.MainActivity;
import kr.com.misemung.vo.GeoInfo;

/**
 * 변환된 좌표계를 가져오는 스레드
 *
 * @author kjh
 *
 */

public class GetTransCoordTask {	// 스레드
	static public boolean active = false;
	Context context;
	//파서용 변수
	int data = 0;			//이건 파싱해서 array로 넣을때 번지
	public boolean isreceiver;
	String getX,getY;	//결과값
	String gridx,gridy,coordfrom,coordto;
	Handler handler;	//값 핸들러
	String getInfo="v2/local/geo/transcoord.json";

	public GetTransCoordTask(Context context, boolean receiver, String x, String y, String from, String to){

		this.context = context;
		handler = new Handler();
		isreceiver = receiver;
		gridx = x;
		gridy = y;
		coordfrom = from;
		coordto = to;
		getX = getY = null;

		// 좌표계 api 호출
		requestGeoInfo();

	}

	private void requestGeoInfo() {
		Request request = new Request(getInfo);
		request.addParam("x", gridx);
		request.addParam("y", gridy);
		request.addParam("input_coord", coordfrom);
		request.addParam("output_coord", coordto);

		NetworkTask.requestExecutor(context, request, false, new OnHttpResponseListener() {
			@Override
			public void onHttpResponse(Response response) {
				try{
					if(response.getApi().equals(getInfo)) {

						Log.e("#### onHttpResponse : ", "==================");

						JSONArray ja = new JSONObject(response.getResponse()).getJSONArray("documents");
						JSONObject jo;
						GeoInfo geoInfo = new GeoInfo();

						for(int i=0; i<ja.length(); i++) {
							jo = ja.getJSONObject(i);
							Log.e("#### jo.getX : ", jo.optString("x"));
							Log.e("#### jo.getY : ", jo.optString("y"));

							geoInfo.setX(jo.optString("x"));
							geoInfo.setY(jo.optString("y"));
						}
						parserData(geoInfo);
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}, false);
	}

	private Void parserData(GeoInfo result) {

		if(result == null){
			return null;
		} else {

			Log.w("========== parserData ", "getX ==> " + result.getX());
			Log.i("========== parserData ", "getY ==> " + result.getY());

			getX = result.getX();
			getY = result.getY();

			showtext();
		}

		return null;
	}


	/**
	 * 이 부분이 뿌려주는곳
	 * 뿌리는건 핸들러가~
	 * @author kjh
	 */
	private void showtext(){

		handler.post(new Runnable() {	//기본 핸들러니깐 handler.post하면됨

			@Override
			public void run() {

				active=false;

				MainActivity.TransCoordThreadResponse(getX, getY);

			}
		});
	}

}
