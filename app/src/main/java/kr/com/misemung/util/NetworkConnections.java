package kr.com.misemung.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import kr.com.misemung.MiseMungGApplication;

/*****************************************************************
 * <pre>
 * project : KES3_Android
 * program name : com.kyobo.ebook.common.b2c.util.NetworkConnections
 * description : 네트워크 접속체크
 *
 * @author :
 * </pre>
 * <pre>
 * created date : 2017. 3. 02.
 * modification log
 * ================================================================
 *  date        name             description
 * ----------------------------------------------------------------
 * 2017. 3. 02.        first generated
 * </pre>
 *****************************************************************/
public class NetworkConnections {


	/** 네트워크 통신가능여부 */
	public static boolean isConnected(){
		try {

			NetworkInfo info = getActiveNetworkInfo();
			if(info!=null && info.isConnectedOrConnecting()){
				return info.isAvailable();
			}
			
      		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/** 제한적인 네트워크 접속여부 : 3G, 4G 등 모바일 네트워크 */
	public static boolean isRestricted(){
		try {
			NetworkInfo info = getActiveNetworkInfo();
			if(info!=null && info.isConnectedOrConnecting() && isMobileType(info.getType()) ){
				return info.isAvailable();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/** 공개적인 네트워크 접속여부 : WiFi, Wibro, Bluetooth 등 모바일이 아닌 네트워크*/
	public static boolean isProfessed(){
		try {
			NetworkInfo info = getActiveNetworkInfo();
			if(info!=null && info.isConnectedOrConnecting() && !isMobileType(info.getType()) ){
				return info.isAvailable();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/** 모바일타입 여부 */
	private static boolean isMobileType(int type){
		return type == ConnectivityManager.TYPE_MOBILE
				|| type == ConnectivityManager.TYPE_MOBILE_DUN
				|| type == ConnectivityManager.TYPE_MOBILE_HIPRI
				|| type == ConnectivityManager.TYPE_MOBILE_MMS
				|| type == ConnectivityManager.TYPE_MOBILE_SUPL;
	}
	
	/** ConnectivityManager */
	private static ConnectivityManager getConnectivityManager() {
		return (ConnectivityManager) MiseMungGApplication.Instance().getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	/** 활성 네트워크 조회 */
	private static NetworkInfo getActiveNetworkInfo() {
		return getConnectivityManager().getActiveNetworkInfo();
	}

	/** 특정 네트워크 접속여부<br>
	 * ConnectivityManager.TYPE_MOBILE (=0) : Mobile
	 * ConnectivityManager.TYPE_WIFI (=1) : Wifi
	 * ConnectivityManager.TYPE_WIMAX (=6) : wimax ,wibro
	 * ConnectivityManager.TYPE_BLUETOOTH (=7) : Bluetooth
	 * 
	 * */
	public static boolean isConnected(int connectivityType){
		try {
			NetworkInfo networkInfo = getConnectivityManager().getNetworkInfo(connectivityType);

			if(networkInfo!=null && networkInfo.isConnectedOrConnecting()){
				return networkInfo.isAvailable();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public synchronized static boolean isOnline() {
		// 구글 url 고정 ip 호출
		CheckConnect socketConnection = new CheckConnect("172.217.25.78");
		socketConnection.start();

		try{
			socketConnection.join();
			Log.e("NetworkConnections", "isSuccess() ==> " + socketConnection.isSuccess());
			return socketConnection.isSuccess();
		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}

	public static class CheckConnect extends Thread {
		String hostname;
		int port = 80;
		int timeout = 1000;
		boolean success = false;

		// socket 통신
		SocketAddress socketAddress;
		Socket socket;

		public CheckConnect(String host) {
			// 구글 url 고정 ip 호출
			this.hostname = host;
		}

		@Override
		public void run() {
			super.run();
			try {
				socketAddress = new InetSocketAddress(hostname, port);
				socket = new Socket();
				socket.setSoTimeout(timeout);           /* InputStream에서 데이터읽을때의 timeout */
				socket.connect(socketAddress, timeout); /* socket연결 자체에 대한 timeout */
				success = true;
			} catch (IOException e) {
				e.printStackTrace();
				success = false;
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public boolean isSuccess(){
			return success;
		}

	}

	/**
	 * 로밍상태 체크
	 * */
	public static boolean isRoaming(Context context) {
		TelephonyManager tm = null;
		tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.isNetworkRoaming();
	}

	/**
	 * Wifi 전용 단말 구분
	 * */
	public static boolean isWifiDevice(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final Class<?> cmlClass = cm.getClass();
		boolean hasMobileNetwork = false;
		try {
			final Method wifiCheckMethod = cmlClass.getMethod("isNetworkSupported", int.class);
			hasMobileNetwork = (Boolean) wifiCheckMethod.invoke(cm, ConnectivityManager.TYPE_MOBILE);
		} catch (Exception ex) {
		}

		return !hasMobileNetwork;
	}
}
