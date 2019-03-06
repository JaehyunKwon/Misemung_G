package kr.com.misemung.network;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import kr.com.misemung.realm.repository.CityRepository;
import kr.com.misemung.ui.MainActivity;
import kr.com.misemung.vo.CityInfo;
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
	String getX,getY,addr;	//결과값
	String gridx,gridy,coordfrom,coordto;
	Handler handler;	//값 핸들러
	String getInfo="v2/local/geo/coord2regioncode.json";

	public GetTransCoordTask(Context context, boolean receiver, String x, String y, String from, String to){

		this.context = context;
		handler = new Handler();
		isreceiver = receiver;
		gridx = x;
		gridy = y;
        coordfrom = from;
        coordto = to;
		getX = null;
		getY = null;
		addr = null;

		// 좌표계 api 호출
		requestGeoInfo();

	}

	private void requestGeoInfo() {
		Request request = new Request(getInfo);
		request.addParam("x", gridx);
		request.addParam("y", gridy);
        request.addParam("input_coord", coordfrom);
        request.addParam("output_coord", coordto);

		NetworkTask.requestExecutor(context, request, false, response -> {
            try{
                if(response.getApi().equals(getInfo)) {

                    Log.e("#### onHttpResponse : ", "==================");

                    JSONArray ja = new JSONObject(response.getResponse()).getJSONArray("documents");
                    JSONObject jo;
                    GeoInfo geoInfo = new GeoInfo();

                    //for(int i=0; i<ja.length(); i++) {
                        jo = ja.getJSONObject(0);
                        Log.e("GetTransCoordTask", "#### jo.getX : " + jo.optString("x"));
                        Log.e("GetTransCoordTask", "#### jo.getY : " + jo.optString("y"));
                        Log.e("GetTransCoordTask", "#### jo.getRegion_2depth_name : " + jo.optString("region_2depth_name"));
                        Log.e("GetTransCoordTask", "#### jo.getRegion_3depth_name : " + jo.optString("region_3depth_name"));

                        geoInfo.setX(jo.optString("x"));
                        geoInfo.setY(jo.optString("y"));
                        geoInfo.setRegion_2depth_name(jo.optString("region_2depth_name"));
                        geoInfo.setRegion_3depth_name(jo.optString("region_3depth_name"));
                    //}
                    parserData(geoInfo);
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }, false);
	}

	private Void parserData(GeoInfo result) {

		if(result == null){
			return null;
		} else {

			Log.w("========== parserData ", "getX ==> " + result.getX());
			Log.i("========== parserData ", "getY ==> " + result.getY());
			Log.i("========== parserData ", "getRegion_2depth_name ==> " + result.getRegion_2depth_name());
			Log.i("========== parserData ", "getRegion_3depth_name ==> " + result.getRegion_3depth_name());

			getX = result.getX();
			getY = result.getY();
			addr = result.getRegion_2depth_name()+" "+result.getRegion_3depth_name();

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

        //기본 핸들러니깐 handler.post하면됨
        handler.post(() -> {

            active=false;

            MainActivity.TransCoordThreadResponse(getX, getY, addr);

        });
	}

}
