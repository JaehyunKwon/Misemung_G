package kr.com.misemung.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.telephony.TelephonyManager
import android.util.Log
import kr.com.misemung.MiseMungGApplication
import java.io.*
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress

/*****************************************************************
 * <pre>
 * project : KES3_Android
 * program name : com.kyobo.ebook.common.b2c.util.NetworkConnections
 * description : 네트워크 접속체크
 *
 * @author :
</pre> *
 * <pre>
 * created date : 2017. 3. 02.
 * modification log
 * ================================================================
 * date        name             description
 * ----------------------------------------------------------------
 * 2017. 3. 02.        first generated
</pre> *
 */
object NetworkConnections {
    /** 네트워크 통신가능여부  */
    val isConnected: Boolean
        get() {
            try {
                val info = activeNetworkInfo
                if (info != null && info.isConnectedOrConnecting) {
                    return info.isAvailable
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }

    /** 제한적인 네트워크 접속여부 : 3G, 4G 등 모바일 네트워크  */
    val isRestricted: Boolean
        get() {
            try {
                val info = activeNetworkInfo
                if (info != null && info.isConnectedOrConnecting && isMobileType(info.type)) {
                    return info.isAvailable
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }

    /** 공개적인 네트워크 접속여부 : WiFi, Wibro, Bluetooth 등 모바일이 아닌 네트워크 */
    val isProfessed: Boolean
        get() {
            try {
                val info = activeNetworkInfo
                if (info != null && info.isConnectedOrConnecting && !isMobileType(info.type)) {
                    return info.isAvailable
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }

    /** 모바일타입 여부  */
    private fun isMobileType(type: Int): Boolean {
        return type == ConnectivityManager.TYPE_MOBILE || type == ConnectivityManager.TYPE_MOBILE_DUN || type == ConnectivityManager.TYPE_MOBILE_HIPRI || type == ConnectivityManager.TYPE_MOBILE_MMS || type == ConnectivityManager.TYPE_MOBILE_SUPL
    }

    /** ConnectivityManager  */
    private val connectivityManager: ConnectivityManager
        private get() = MiseMungGApplication.Companion.Instance()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /** 활성 네트워크 조회  */
    private val activeNetworkInfo: NetworkInfo?
        private get() = connectivityManager.activeNetworkInfo

    /** 특정 네트워크 접속여부<br></br>
     * ConnectivityManager.TYPE_MOBILE (=0) : Mobile
     * ConnectivityManager.TYPE_WIFI (=1) : Wifi
     * ConnectivityManager.TYPE_WIMAX (=6) : wimax ,wibro
     * ConnectivityManager.TYPE_BLUETOOTH (=7) : Bluetooth
     *
     */
    fun isConnected(connectivityType: Int): Boolean {
        try {
            val networkInfo = connectivityManager.getNetworkInfo(connectivityType)
            if (networkInfo != null && networkInfo.isConnectedOrConnecting) {
                return networkInfo.isAvailable
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    // 구글 url 고정 ip 호출
    @get:Synchronized
    val isOnline: Boolean
        get() {
            // 구글 url 고정 ip 호출
            val socketConnection = CheckConnect("172.217.25.78")
            socketConnection.start()
            try {
                socketConnection.join()
                Log.e("NetworkConnections", "isSuccess() ==> " + socketConnection.isSuccess)
                return socketConnection.isSuccess
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }

    /**
     * 로밍상태 체크
     */
    fun isRoaming(context: Context): Boolean {
        var tm: TelephonyManager? = null
        tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm!!.isNetworkRoaming
    }

    /**
     * Wifi 전용 단말 구분
     */
    fun isWifiDevice(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val cmlClass: Class<*> = cm.javaClass
        var hasMobileNetwork = false
        try {
            val wifiCheckMethod =
                cmlClass.getMethod("isNetworkSupported", Int::class.javaPrimitiveType)
            hasMobileNetwork =
                wifiCheckMethod.invoke(cm, ConnectivityManager.TYPE_MOBILE) as Boolean
        } catch (ex: Exception) {
        }
        return !hasMobileNetwork
    }

    class CheckConnect     // 구글 url 고정 ip 호출
        (var hostname: String) : Thread() {
        var port = 80
        var timeout = 1000
        var isSuccess = false

        // socket 통신
        var socketAddress: SocketAddress? = null
        var socket: Socket? = null
        override fun run() {
            super.run()
            try {
                socketAddress = InetSocketAddress(hostname, port)
                socket = Socket()
                socket!!.soTimeout = timeout /* InputStream에서 데이터읽을때의 timeout */
                socket!!.connect(socketAddress, timeout) /* socket연결 자체에 대한 timeout */
                isSuccess = true
            } catch (e: IOException) {
                e.printStackTrace()
                isSuccess = false
            } finally {
                try {
                    socket!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}