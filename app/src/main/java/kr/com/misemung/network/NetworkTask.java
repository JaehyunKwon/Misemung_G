package kr.com.misemung.network;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;

/*****************************************************************
 * <pre>
 * project : KES3_Android
 * program name : com.kyobo.ebook.common.b2c.util.NetworkConnections
 * description : 네트워크 API Task
 *
 * @author : JSW
 * </pre>
 * <pre>
 * created date : 2017. 2. 16.
 * modification log
 * ================================================================
 *  date        name             description
 * ----------------------------------------------------------------
 * 2017. 2. 16.    JSW    first generated
 * </pre>
 *****************************************************************/

public class NetworkTask extends AsyncTask<String, String, Response> {

    private Dialog mErrorDialog = null;
    private Dialog networkDialog;

    private final Context context;
    private final Request req;
    private boolean showProgress;
    private OnHttpResponseListener listener;

    private static int connect_timeout;
    private static int read_timeout;
    private static boolean shortConnect;

    private String boundary;
    private String LINE_FEED = "\r\n";

    private OutputStream outputStream = null;

    public NetworkTask(Context context, Request req, boolean showProgress, OnHttpResponseListener listener, boolean shortConnect) {
        this.context = context;
        this.req = req;
        this.showProgress = showProgress;
        this.listener = listener;
        NetworkTask.shortConnect = shortConnect;

        if (shortConnect) {
            setTimeout(3000, 3000);
        } else {
            setTimeout(5000, 60000);
        }
    }

    public static void setTimeout(int connect, int read) {
        connect_timeout = connect;
        read_timeout = read;
    }

    public static void request(Context context, Request req, boolean showProgress, OnHttpResponseListener listener) {
        new NetworkTask(context, req, showProgress, listener, false).execute("");
    }

    public static void requestExecutor(Context context, Request req, boolean showProgress, OnHttpResponseListener listener, boolean shortConnect) {
        new NetworkTask(context, req, showProgress, listener, shortConnect).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

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

        if (showProgress && context instanceof Activity) {
            try {
                Activity activity = (Activity) context;
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(Response response) {
        super.onCancelled(response);
    }

    @Override
    protected Response doInBackground(String... strings) {
        return doRequest(context, req);
    }

    @Override
    protected void onPostExecute(Response response) {
        super.onPostExecute(response);

        if (context == null) {
            // ....... nothing
        } else if (context instanceof Activity) {
            Activity activity = (Activity)context;

            if (!activity.isFinishing()) {
                listener.onHttpResponse(response);
            }
        } else {
            listener.onHttpResponse(response);
        }
    }

    private Response doRequest(Context context, Request req) {

        boundary = "===" + System.currentTimeMillis() + "===";

        Response response = new Response();
        response.setApi(req.getApi());
        response.setRequestParams(req.getParams());

        HttpURLConnection httpURLConnection = null;

        try {

            final String api = req.getApi();

            Uri uri = req.getUri();
            URL url = new URL(uri.toString());

            Log.e("NETWORKTASK", "request URL=" + url);

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(connect_timeout);
            httpURLConnection.setReadTimeout(read_timeout);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setDoInput(true);

            // 헤더 설정
            httpURLConnection.setRequestProperty("Content-Type",    "application/json; charset=UTF-8");
            httpURLConnection.setRequestProperty("Authorization",   "KakaoAK 9c9c3f7111b8e728d440bbebe4451e75");
            // 헤더 설정

            httpURLConnection.connect();

            int responseCode = httpURLConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                String responseString = readStream(httpURLConnection.getInputStream());

                Log.e("HTTP Result", responseString);

                response.setResultCode(String.valueOf(responseCode));
                response.setResultErrorCode("0");
                response.setResponse(responseString);

                return response;
            } else if (responseCode == HttpURLConnection.HTTP_UNAVAILABLE){
                String responseString = readStream(httpURLConnection.getInputStream());

                response.setResultCode(String.valueOf(responseCode));
                response.setResultErrorCode(String.valueOf(responseCode));
                response.setResponse(responseString);
            } else {
                // response error 처리 추가 필요
                String responseString = readStream(httpURLConnection.getErrorStream());

                JSONObject object = new JSONObject(responseString);

                response.setResultCode(String.valueOf(responseCode));
                response.setResultErrorCode(String.valueOf(responseCode));
                response.setResultMsg(object.getString("resultMsg"));
                response.setResponse(responseString);

                Log.d("HTTP Result Error", responseString);
            }

        } catch (Exception e) {
            if (e instanceof SocketTimeoutException) {
                Log.e("NetworkTask", "HTTP - SocketTimeoutException () : " + e);
            } else {
                int responseCode = 0;
                String errStreamString = null;
                JSONObject obj = null;

                try {
                    if (httpURLConnection.getErrorStream() == null) {
                        errStreamString = "";
                    } else {
                        responseCode = httpURLConnection.getResponseCode();
                        errStreamString = readStream(httpURLConnection.getErrorStream());

                        if (!errStreamString.isEmpty()) {
                            obj = new JSONObject(errStreamString);

                            response.setResultErrorCode(String.valueOf(responseCode));
                            response.setResultCode(obj.getString("resultCode"));
                            response.setResultMsg(obj.getString("resultMsg"));
                            response.setResponse(errStreamString);
                        }
                    }

                } catch (Exception exc) {
                    exc.printStackTrace();
                }

                Log.e("NetworkTask", "code : " + responseCode + ", errStr : " + errStreamString);
            }
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return response;
    }

    private String readStream(InputStream stream) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        return sb.toString();
    }
}
