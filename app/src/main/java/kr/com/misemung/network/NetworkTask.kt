package kr.com.misemung.network

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

/*****************************************************************
 * <pre>
 * project : KES3_Android
 * program name : com.kyobo.ebook.common.b2c.util.NetworkConnections
 * description : 네트워크 API Task
 *
 * @author : JSW
</pre> *
 * <pre>
 * created date : 2017. 2. 16.
 * modification log
 * ================================================================
 * date        name             description
 * ----------------------------------------------------------------
 * 2017. 2. 16.    JSW    first generated
</pre> *
 */
class NetworkTask(
    private val context: Context?,
    private val req: Request,
    private val showProgress: Boolean,
    private val listener: OnHttpResponseListener,
    shortConnect: Boolean
) : AsyncTask<String?, String?, Response?>() {
    private val mErrorDialog: Dialog? = null
    private val networkDialog: Dialog? = null
    private var boundary: String? = null
    private val LINE_FEED = "\r\n"
    private val outputStream: OutputStream? = null
    override fun onPreExecute() {
        super.onPreExecute()

        /*if (!NetworkConnections.isConnected()) {
            try{
                if(req.getApi().equals(API.Viewer.Annotation) || req.getApi().equals(API.Viewer.ReadComplete)
                        || req.getApi().equals(API.Viewer.Recommend) || req.getApi().equals(API.Viewer.Relation)){
                    return;
                }
            }catch (Exception e){
                return;
            }

            if(networkDialog != null){
                networkDialog.dismiss();
            }

            return;
        }*/
        if (showProgress && context is Activity) {
            try {
                val activity = context
            } catch (e: Exception) {
            }
        }
    }

    override fun onProgressUpdate(vararg values: String?) {
        super.onProgressUpdate(*values)
    }

    override fun onCancelled(response: Response?) {
        super.onCancelled(response)
    }

    override fun doInBackground(vararg params: String?): Response? {
        return doRequest(context, req)
    }

    override fun onPostExecute(response: Response?) {
        super.onPostExecute(response)
        if (context == null) {
            // ....... nothing
        } else if (context is Activity) {
            if (!context.isFinishing) {
                listener.onHttpResponse(response)
            }
        } else {
            listener.onHttpResponse(response)
        }
    }

    private fun doRequest(context: Context?, req: Request): Response {
        boundary = "===" + System.currentTimeMillis() + "==="
        val response = Response()
        response.api = req.api
        response.requestParams = req.params
        var httpURLConnection: HttpURLConnection? = null
        try {
            val uri = req.getUri()
            val url = URL(uri.toString())
            Log.e("NETWORKTASK", "request URL=$url")
            httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.connectTimeout = connect_timeout
            httpURLConnection.readTimeout = read_timeout
            httpURLConnection.requestMethod = "GET"
            httpURLConnection.useCaches = false
            httpURLConnection.doInput = true

            // 헤더 설정
            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            httpURLConnection.setRequestProperty(
                "Authorization",
                "KakaoAK 9c9c3f7111b8e728d440bbebe4451e75"
            )
            // 헤더 설정
            httpURLConnection.connect()
            val responseCode = httpURLConnection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                val responseString = readStream(httpURLConnection.inputStream)
                Log.e("HTTP Result", responseString)
                response.resultCode = responseCode.toString()
                response.resultErrorCode = "0"
                response.response = responseString
                return response
            } else if (responseCode == HttpURLConnection.HTTP_UNAVAILABLE) {
                val responseString = readStream(httpURLConnection.inputStream)
                response.resultCode = responseCode.toString()
                response.resultErrorCode = responseCode.toString()
                response.response = responseString
            } else {
                // response error 처리 추가 필요
                val responseString = readStream(httpURLConnection.errorStream)
                val `object` = JSONObject(responseString)
                response.resultCode = responseCode.toString()
                response.resultErrorCode = responseCode.toString()
                response.resultMsg = `object`.getString("resultMsg")
                response.response = responseString
                Log.d("HTTP Result Error", responseString)
            }
        } catch (e: Exception) {
            if (e is SocketTimeoutException) {
                Log.e("NetworkTask", "HTTP - SocketTimeoutException () : $e")
            } else {
                var responseCode = 0
                var errStreamString: String? = null
                var obj: JSONObject? = null
                try {
                    if (httpURLConnection!!.errorStream == null) {
                        errStreamString = ""
                    } else {
                        responseCode = httpURLConnection.responseCode
                        errStreamString = readStream(httpURLConnection.errorStream)
                        if (errStreamString.isNotEmpty()) {
                            obj = JSONObject(errStreamString)
                            response.resultErrorCode = responseCode.toString()
                            response.resultCode = obj.getString("resultCode")
                            response.resultMsg = obj.getString("resultMsg")
                            response.response = errStreamString
                        }
                    }
                } catch (exc: Exception) {
                    exc.printStackTrace()
                }
                Log.e("NetworkTask", "code : $responseCode, errStr : $errStreamString")
            }
        } finally {
            httpURLConnection?.disconnect()
        }
        return response
    }

    @Throws(IOException::class)
    private fun readStream(stream: InputStream): String {
        val sb = StringBuilder()
        val br = BufferedReader(InputStreamReader(stream))
        var line: String?
        while (br.readLine().also { line = it } != null) {
            sb.append(line)
        }
        return sb.toString()
    }

    companion object {
        private var connect_timeout = 0
        private var read_timeout = 0
        private var shortConnect = false
        fun setTimeout(connect: Int, read: Int) {
            connect_timeout = connect
            read_timeout = read
        }

        fun request(
            context: Context?,
            req: Request,
            showProgress: Boolean,
            listener: OnHttpResponseListener
        ) {
            NetworkTask(context, req, showProgress, listener, false).execute("")
        }

        fun requestExecutor(
            context: Context?,
            req: Request,
            showProgress: Boolean,
            listener: OnHttpResponseListener,
            shortConnect: Boolean
        ) {
            NetworkTask(context, req, showProgress, listener, shortConnect).executeOnExecutor(
                THREAD_POOL_EXECUTOR
            )
        }
    }

    init {
        Companion.shortConnect = shortConnect
        if (shortConnect) {
            setTimeout(3000, 3000)
        } else {
            setTimeout(5000, 60000)
        }
    }
}