package kr.com.misemung

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.google.firebase.analytics.FirebaseAnalytics
import io.realm.Realm
import io.realm.RealmConfiguration
import kr.com.misemung.realm.entity.AirRecord
import java.io.File
import java.util.*

/**
 * 미세멍지 application
 */
class MiseMungGApplication : MultiDexApplication() {
    var nowActivity //현재 실행되는 액티비티(viewer 여부)
            : Activity? = null
    private var mAnalytics: FirebaseAnalytics? = null
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "MiseMungGApplication Create")
        mContext = applicationContext
        Realm.init(mContext)
        try {
            val newConfig = RealmConfiguration.Builder()
                .name("misemung.realm")
                .schemaVersion(1)
                .allowWritesOnUiThread(true)
                .deleteRealmIfMigrationNeeded()
                .compactOnLaunch()
                .build()
            Realm.setDefaultConfiguration(newConfig)
            val realm = Realm.getDefaultInstance()
            val config = realm.configuration
            Log.e("MiseMungGApplication", "REALM path:" + config.path)
            val realmFile = File(config.path)
            if (!realmFile.exists()) {
                realm.createObject(AirRecord::class.java)
            }
        } catch (ignore: RuntimeException) {
            Log.e("MiseMungGApplication", ignore.toString())
        }

        //Thread.setDefaultUncaughtExceptionHandler(new EBookUncaughtExceptionHandler(getBaseContext(), getContentResolver(), Thread.getDefaultUncaughtExceptionHandler()));
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "Application onConfigurationChanged fontScale: " + newConfig.fontScale)
        Log.d(TAG, "Application onConfigurationChanged keyboardHidden: " + newConfig.keyboardHidden)
        Log.d(TAG, "Application onConfigurationChanged locale: " + newConfig.locale)
        Log.d(TAG, "Application onConfigurationChanged orientation: " + newConfig.orientation)
        Log.d(TAG, "Application onConfigurationChanged screenLayout: " + newConfig.screenLayout)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Log.d(TAG, "Application onLowMemory")
    }

    @JvmName("setNowActivity1")
    fun setNowActivity(activity: Activity?) {
        Instance().nowActivity = activity
    }

    @get:Synchronized
    val defaultAnalytics: FirebaseAnalytics
        get() {
            if (mAnalytics == null) {
                mAnalytics = FirebaseAnalytics.getInstance(this)
            }
            return mAnalytics!!
        }

    fun setLogFirebaseAnalytics(gubun: String?) {
        if (mAnalytics == null) {
            defaultAnalytics
        }
        mAnalytics!!.logEvent(gubun!!, Bundle())
    }

    val currentActivity: Activity?
        get() = Companion.currentActivity

    /** Dir Database  */
    val appDatabaseDir: String
        get() = getDatabasePath("sbooks.db").absolutePath

    /** Dir File  */
    val appFileDir: String
        get() = filesDir.absolutePath

    /** Dir Cache  */
    val appCacheDir: String
        get() = if (externalCacheDir == null) cacheDir.absolutePath else externalCacheDir!!.absolutePath

    /** Dir Cert  */
    val appDRMCertDir: String
        get() = File(appDataDir, "cert").absolutePath

    /** Dir Lic  */
    val appDRMLicDir: String
        get() = File(appDataDir, "lic").absolutePath

    /** Dir SharedPreferences  */
    val appSharedPreferencesDir: String
        get() = File(appDataDir, "shared_prefs").absolutePath

    /** Dir Data  */
    val appDataDir: String
        get() = filesDir.parent

    companion object {
        private val TAG = MiseMungGApplication::class.java.simpleName
        private lateinit var ourApplication: MiseMungGApplication
        private var mContext: Context? = null
        fun Instance(): MiseMungGApplication {
            return ourApplication
        }

        var currentActivity: Activity? = null
        @JvmName("setCurrentActivity1")
        fun setCurrentActivity(currentActivity: Activity?) {
            Companion.currentActivity = currentActivity
        }

        val storageDirectories: Array<String>?
            get() {
                val storageDirectories: Array<String>?
                val rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE")
                storageDirectories = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    val results: MutableList<String> = ArrayList()
                    val externalDirs = mContext!!.getExternalFilesDirs(null)
                    if (externalDirs != null) {
                        for (file in externalDirs) {
                            if (file == null) {
                                continue
                            }
                            val path = file.path.split("/Android").toTypedArray()[0]
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Environment.isExternalStorageRemovable(
                                    file
                                )
                                || rawSecondaryStoragesStr != null && rawSecondaryStoragesStr.contains(
                                    path
                                )
                            ) {
                                results.add(path)
                            }
                        }
                        results.toTypedArray()
                    } else {
                        null
                    }
                } else {
                    val rv: Set<String> = HashSet()
                    if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
                        val rawSecondaryStorages =
                            rawSecondaryStoragesStr!!.split(File.pathSeparator).toTypedArray()
                        //Collections.addAll(rv, *rawSecondaryStorages)
                    }
                    rv.toTypedArray()
                }
                return storageDirectories
            }
    }

    init {
        ourApplication = this
    }
}