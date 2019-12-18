package kr.com.misemung.network;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.com.misemung.ui.MainActivity;
import kr.com.misemung.vo.AddrInfo;
import kr.com.misemung.vo.CityInfo;

/**
 * 주소 검색 리스트를 가져오는 스레드
 *
 * @author kjh
 *
 */

public class GetAddressTask {	// 스레드
	static public boolean active = false;
	private Context context;
	//파서용 변수
	private int data = 0;			//이건 파싱해서 array로 넣을때 번지
	private ArrayList<CityInfo> cityInfoList = null;
	public boolean isreceiver;
	private String getX,getY,addr;	//결과값
	private Handler handler;	//값 핸들러
	private String getInfo="v2/local/search/address.json";

	public GetAddressTask(Context context, boolean receiver, String address){

		this.context = context;
		handler = new Handler();
		isreceiver = receiver;
		addr = address;

		// 주소 api 호출
		requestGeoInfo();

	}

	private void requestGeoInfo() {
		Request request = new Request(getInfo);
		request.addParam("query", addr);

		NetworkTask.requestExecutor(context, request, false, response -> {
            try{
                if(response.getApi().equals(getInfo)) {

                    Log.e("#### onHttpResponse : ", "==================");

                    JSONArray docArr = new JSONObject(response.getResponse()).getJSONArray("documents"); // API 최상위 리스트
                    JSONObject addrObj;
					JSONObject sub_addrObj; // json "address" 부분 담는 JSONObject
                    AddrInfo addrInfo = new AddrInfo();

					cityInfoList = new ArrayList<>();

					if (docArr.length() < 1) {
						parserData(null);
					} else {
						for (int i = 0; i < docArr.length(); i++) {
							data = i;

							addrObj = docArr.getJSONObject(data);
							sub_addrObj = addrObj.getJSONObject("address");

							Log.e("GetAddressTask", "#### jo.getRegion_2depth_name : " + sub_addrObj.optString("region_2depth_name"));
							Log.e("GetAddressTask", "#### jo.getRegion_3depth_h_name : " + sub_addrObj.optString("region_3depth_h_name"));
							Log.e("GetAddressTask", "#### jo.getRegion_3depth_name : " + sub_addrObj.optString("region_3depth_name"));
							Log.e("GetAddressTask", "#### jo.getX : " + sub_addrObj.optString("x"));
							Log.e("GetAddressTask", "#### jo.getY : " + sub_addrObj.optString("y"));

							String region_2depth_name = sub_addrObj.optString("region_2depth_name");
							String region_3depth_h_name = sub_addrObj.optString("region_3depth_h_name");
							String region_3depth_name = sub_addrObj.optString("region_3depth_name");

							String x = sub_addrObj.optString("x");
							String y = sub_addrObj.optString("y");

							addrInfo.setAddress_name(region_2depth_name
									+ " " + (region_3depth_h_name.equals("") ? region_3depth_name : region_3depth_h_name));
							addrInfo.setX(x);
							addrInfo.setY(y);

							parserData(addrInfo);
						}
					}
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }, false);
	}

	private Void parserData(AddrInfo result) {

		if (result != null) {

			Log.i("========== parserData ", "getAddress_name ==> " + result.getAddress_name());
			Log.w("========== parserData ", "getX ==> " + result.getX());
			Log.i("========== parserData ", "getY ==> " + result.getY());

			addr = result.getAddress_name();
			getX = result.getX();
			getY = result.getY();

			CityInfo info = new CityInfo();
			info.setUmdName(addr);
			info.setTmX(getX);
			info.setTmY(getY);

			cityInfoList.add(data, info);

		}
		showtext();

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

			((MainActivity)MainActivity.mContext).SearchCityThreadResponse(cityInfoList);

        });
	}

}
