package kr.com.misemung.ui

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kr.com.misemung.R
import kr.com.misemung.common.CommonPopup
import kr.com.misemung.util.NetworkConnections
import java.util.*

class IntroActivity : AppCompatActivity() {
    private var mNetworkErrorDialog: Dialog? = null
    private val HANDLER_INTRO_START = 0
    private var loadingProgressBar: ProgressBar? = null

    @SuppressLint("HandlerLeak")
    private var mIntroHandler: Handler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        loadingProgressBar = findViewById(R.id.loadingProgressBar)
        startIntro()
    }

    private fun startIntro() {
        if (NetworkConnections.isConnected && NetworkConnections.isOnline) {
            if (permission) {
                // 2초 후 인트로 액티비티 제거
                mIntroHandler = Handler()
                mIntroHandler!!.postDelayed({ goNextActivity() }, 1000)
            } else {
                // 퍼미션 요청
                deviceLocation
            }
        } else {
            showNetworkErrorPopup()
        }
    }

    private val permission: Boolean
        private get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("IntroActivity", "getPermission")
            val checkPermissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            val locationPermission =
                checkSelfPermission(checkPermissions[0]) == PackageManager.PERMISSION_GRANTED
            val list: MutableList<String> = ArrayList()
            if (!locationPermission) {
                list.add(checkPermissions[0])
            }
            list.size <= 0
        } else {
            true
        }
    private val deviceLocation: Unit
        private get() {
            if (ContextCompat.checkSelfPermission(
                    this.applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    1000
                )
            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1000 -> {

                // If request is cancelled, the result arrays are empty.
                for (permission in permissions) {
                    if (ContextCompat.checkSelfPermission(this, permission)
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        startIntro()
                    } else {
                        reCheckPermissions()
                    }
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun reCheckPermissions() {
        // 권한 허용 체크
        val locationPermission = (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        var errMsg = ""
        if (!locationPermission) {
            errMsg = getString(R.string.location_permission_error)
        }
        CommonPopup.showConfirmDialog(
            this,  /*getString(R.string.noti_popup_title),*/
            errMsg
        ) { view: View? -> finish() }
    }

    /**
     * 특정 시간동안 Intro 노출 후 다음 화면으로 이동
     */
    private fun goNextActivity() {
        //인트로 화면 노출
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        finish()
    }

    /**
     * 네트워크 불가상태 알림 팝업
     */
    private fun showNetworkErrorPopup() {
        mNetworkErrorDialog = CommonPopup.showConfirmDialog(
            this,  //getString(R.string.noti_popup_title),
            getString(R.string.network_error_msg)
        ) { view: View? ->
            mNetworkErrorDialog!!.dismiss()
            finish()
        }
    }
}