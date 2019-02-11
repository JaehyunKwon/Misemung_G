package kr.com.misemung.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.telephony.TelephonyManager;


import java.util.Date;
import java.util.UUID;

import kr.com.misemung.MiseMungGApplication;

/**
 * SharedPreferences 관리
 */
public class HandlePreference
{
	/**
	 * Application Preference 전역 식별자.
	 */
	public final static String PREF_NAME		= "kr.com.misemung.case";


	/** Fragment 리스트 사이즈 */
	public static String FRAGMENT_SIZE = "fragment_size";
	public static int getFragmentListSize() {
		SharedPreferences settings = MiseMungGApplication.Instance().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		return settings.getInt(FRAGMENT_SIZE, 0);
	}
	/** Fragment 리스트 사이즈 값 저장 */
	public static void setFragmentListSize(int size) {
		SharedPreferences settings = MiseMungGApplication.Instance().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(FRAGMENT_SIZE, size);
		editor.commit();
	}
}

