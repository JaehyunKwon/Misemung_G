package kr.com.misemung;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import kr.com.misemung.realm.entity.AirRecord;


/**
 * 미세멍지 application
 */
public class MiseMungGApplication extends MultiDexApplication {
	private static final String TAG = MiseMungGApplication.class.getSimpleName();
	private static MiseMungGApplication ourApplication;

	private static Context mContext;

	public Activity nowActivity;	//현재 실행되는 액티비티(viewer 여부)

    private FirebaseAnalytics mAnalytics;

    public static MiseMungGApplication Instance()
	{
		return ourApplication;
	}
	public MiseMungGApplication()
	{
		ourApplication = this;
	}

	public static Activity currentActivity = null;

	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "MiseMungGApplication Create");

		mContext = getApplicationContext();

		Realm.init(mContext);

		try {
			RealmConfiguration newConfig = new RealmConfiguration.Builder()
					.name("misemung.realm")
					.schemaVersion(1)
					.deleteRealmIfMigrationNeeded()
					.compactOnLaunch()
					.build();
			Realm.setDefaultConfiguration(newConfig);

			Realm realm = Realm.getDefaultInstance();
			RealmConfiguration config = realm.getConfiguration();

			Log.e("MiseMungGApplication", "REALM path:" + config.getPath());
			File realmFile = new File(config.getPath());
			if (!realmFile.exists()) {
				realm.createObject(AirRecord.class);
			}

		} catch (RuntimeException ignore) {}

		//Thread.setDefaultUncaughtExceptionHandler(new EBookUncaughtExceptionHandler(getBaseContext(), getContentResolver(), Thread.getDefaultUncaughtExceptionHandler()));

	}

	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.d(TAG, "Application onConfigurationChanged fontScale: " + newConfig.fontScale);
		Log.d(TAG, "Application onConfigurationChanged keyboardHidden: " + newConfig.keyboardHidden);
		Log.d(TAG, "Application onConfigurationChanged locale: "+newConfig.locale);
		Log.d(TAG, "Application onConfigurationChanged orientation: " + newConfig.orientation);
		Log.d(TAG, "Application onConfigurationChanged screenLayout: " + newConfig.screenLayout);
	}

	public void onLowMemory() {
		super.onLowMemory();
		Log.d(TAG, "Application onLowMemory");
	}

	public void setNowActivity(Activity activity) {
		MiseMungGApplication.Instance().nowActivity = activity;
	}

	synchronized  public FirebaseAnalytics getDefaultAnalytics() {
		if(mAnalytics == null) {
			mAnalytics = FirebaseAnalytics.getInstance(this);
		}
		return mAnalytics;
	}

	public void setLogFirebaseAnalytics(String gubun) {
		if (mAnalytics == null) {
			getDefaultAnalytics();
		}

		mAnalytics.logEvent(gubun, new Bundle());
	}

	public Activity getCurrentActivity() {
		return MiseMungGApplication.currentActivity;
	}

	public static void setCurrentActivity(Activity currentActivity) {
		MiseMungGApplication.currentActivity = currentActivity;
	}

	/** Dir Database */
	public String getAppDatabaseDir(){
		return getDatabasePath("sbooks.db").getAbsolutePath();
	}
	/** Dir File */
	public String getAppFileDir(){
		return getFilesDir().getAbsolutePath();
	}
	/** Dir Cache */
	public String getAppCacheDir(){
		if(getExternalCacheDir() == null)
			return getCacheDir().getAbsolutePath();
		else
			return getExternalCacheDir().getAbsolutePath();
	}
	/** Dir Cert */
	public String getAppDRMCertDir(){
		return new File( getAppDataDir(), "cert").getAbsolutePath();
	}
	/** Dir Lic */
	public String getAppDRMLicDir(){
		return new File( getAppDataDir(), "lic").getAbsolutePath();
	}
	/** Dir SharedPreferences */
	public String getAppSharedPreferencesDir(){
		return new File( getAppDataDir(), "shared_prefs").getAbsolutePath();
	}
	/** Dir Data */
	public String getAppDataDir(){
		return getFilesDir().getParent();
	}

	public static String[] getStorageDirectories() {
		String[] storageDirectories;
		String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			List<String> results = new ArrayList<String>();
			File[] externalDirs = mContext.getExternalFilesDirs(null);
			if (externalDirs != null) {
				for (File file : externalDirs) {
					if (file == null) {
						continue;
					}
					String path = file.getPath().split("/Android")[0];
					if((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Environment.isExternalStorageRemovable(file))
							|| rawSecondaryStoragesStr != null && rawSecondaryStoragesStr.contains(path)){
						results.add(path);
					}
				}
				storageDirectories = results.toArray(new String[0]);
			} else {
				storageDirectories = null;
			}
		} else {
			final Set<String> rv = new HashSet<String>();

			if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
				final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
				Collections.addAll(rv, rawSecondaryStorages);
			}
			storageDirectories = rv.toArray(new String[rv.size()]);
		}
		return storageDirectories;
	}
}