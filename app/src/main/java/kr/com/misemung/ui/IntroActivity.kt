package kr.com.misemung.ui

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kr.com.misemung.R
import kr.com.misemung.common.CommonPopup
import kr.com.misemung.common.Permission
import kr.com.misemung.util.NetworkConnections
import java.util.*

class IntroActivity : AppCompatActivity() {
    private val REQUEST_MANDATORY_PERMISSION = 51
    private val RESULT_PERMISSION = 60

    private var mNetworkErrorDialog: Dialog? = null
    private var mDialog: Dialog? = null
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
            if (Permission().hasMandatoryPermission(this)) {
                // 2초 후 인트로 액티비티 제거
                mIntroHandler = Handler()
                mIntroHandler!!.postDelayed({ goNextActivity() }, 1000)
            } else {
                // 퍼미션 요청
                requestPermission()
            }
        } else {
            showNetworkErrorPopup()
        }
    }

    /**
     * 단말 permission 요청
     */
    private fun requestPermission() {
        var count = 0
        for (permission in Permission().PERMISSION_MANDATORY) {
            val accessCoarseLocation = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            val accessFineLocation = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            Log.d("MainActivity", "accessCoarseLocation ==> $accessCoarseLocation")
            Log.w("MainActivity", "accessFineLocation ==> $accessFineLocation")

            // ACCESS_COARSE_LOCATION 권한이 true 이면, ACCESS_BACKGROUND_LOCATION 권한 PASS.
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (accessCoarseLocation) {
                    continue
                } else {
                    count++
                }
            }
        }
        if (count > 0) {
            var index = 0
            val strPermission = arrayOfNulls<String>(count)
            for (permission in Permission().PERMISSION_MANDATORY) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    strPermission[index] = permission
                    index++
                }
            }
            ActivityCompat.requestPermissions(
                this,
                strPermission,
                REQUEST_MANDATORY_PERMISSION
            )
        } else {
            startIntro()
        }
    }

    /**
     * 단말 permission 요청에 따른 응답 수신
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_MANDATORY_PERMISSION -> {
                if (Permission().hasMandatoryPermission(this)) {
                    startIntro()
                } else {
                    mDialog = CommonPopup.showConfirmCancelDialog(
                        this,
                        getString(R.string.alert_title),
                        getString(R.string.alert_message),
                        getString(R.string.alert_go_permission),
                        getString(R.string.alert_finish),
                        { view1: View? ->
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivityForResult(intent, RESULT_PERMISSION)
                            mDialog!!.dismiss()
                        }) { view2: View? ->
                        mDialog!!.dismiss()
                        finish()
                    }
                }
                return
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
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