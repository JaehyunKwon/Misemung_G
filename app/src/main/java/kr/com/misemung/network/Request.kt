package kr.com.misemung.network

import android.net.Uri
import java.io.File
import java.util.*

/**
 * Created by KJH on 2017. 3. 16..
 */
class Request(val api: String) {
    private val uBuilder: Uri.Builder = Uri.parse("https://dapi.kakao.com/")
        .buildUpon()
    var uri: Uri? = null
    private var partList: ArrayList<FilePart>? = null
    val params = HashMap<String, Any>()
    private var jsonString = ""

    /**
     * parameter를 추가한다.
     */
    fun addParam(key: String, value: Any?) {
        var `val`: Any = ""
        `val` = value ?: ""

        //if (API.GET.equals(api.getMethod())) {
        uBuilder.appendQueryParameter(key, `val` as String)
        //}
        params[key] = `val`
    }

    fun addJson(json: String) {
        jsonString = json
    }

    /**
     * uri path를 추가한다.
     */
    fun addPath(path: String) {
        if (path.isNotEmpty()) {
            uBuilder.appendPath(path)
        }
    }

    fun addPart(key: String?, name: String?, part: File?) {
        if (part == null) {
            RuntimeException("File part must not null")
        }
        if (partList == null) {
            partList = ArrayList()
        }
        partList!!.add(FilePart(key, name, part))
    }

    @JvmName("getUri1")
    fun getUri(): Uri? {
        if (uri == null) {
            uri = uBuilder.build()
        }
        return uri
    }

    override fun toString(): String {
        return "Request{" +
                ", api=" + api +
                ", uri=" + uri +
                '}'
    }

    class FilePart(key: String?, name: String?, part: File?) {
        var key: String? = null
        var name: String? = null
        var part: File? = null

        init {
            this.key = key
            this.name = name
            this.part = part
        }
    }

    init {
        //.appendPath(api.getKind());
        val st = StringTokenizer(api, "/")
        while (st.hasMoreTokens()) {
            uBuilder.appendPath(st.nextToken())
        }
    }
}