package kr.com.misemung.network;


import java.util.HashMap;
import java.util.List;

import kr.com.misemung.vo.GeoInfo;

/**
 * Created by JSW on 2017. 2. 16..
 */

public class Response {
    private String api;
    private String resultErrorCode;
    private String resultCode;
    private String resultMsg;
    private String response;
    private List<GeoInfo> responseList;
    private HashMap<String, Object> requestParams;

    public Response() { }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getResultErrorCode() {
        return resultErrorCode;
    }

    public void setResultErrorCode(String resultErrorCode) {
        this.resultErrorCode = resultErrorCode;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void setRequestParams(HashMap<String, Object> params) {
        this.requestParams = params;
    }

    public HashMap<String, Object> getRequestParams() {
        return requestParams;
    }

    public List<GeoInfo> getResponseList() {
        return responseList;
    }

    public void setResponseList(List<GeoInfo> responseList) {
        this.responseList = responseList;
    }
}
