package kr.com.misemung.network

import kr.com.misemung.vo.GeoInfo
import java.util.*

/**
 * Created by KJH on 2019. 3. 16..
 */
class Response {
    var api: String? = null
    var resultErrorCode: String? = null
    var resultCode: String? = null
    var resultMsg: String? = null
    var response: String? = null
    var responseList: List<GeoInfo>? = null
    var requestParams: HashMap<String, Any>? = null
}