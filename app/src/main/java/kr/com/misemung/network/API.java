package kr.com.misemung.network;


public class API {

    public static final String SERVER_URL = "http://openapi.airkorea.or.kr/openapi/services/rest/";   // 개발 서버
    public static final String SERVICE_KEY = "M3v8WQCkR8IrE65Qb2iaOz0Ns3j%2FCnzlOteV7ch%2BbHpRfJEpbE96MuO%2Bqf2VoHLk2x1iS8hFv%2BUwO%2B39BbukBg%3D%3D";


    /** 대기정보 요청 */
    public static final String REQUEST_FIND_DUST(){
        return  String.format(SERVER_URL + "ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty");
    }

    /** 가까운 측정소 리스트 */
    public static final String REQUEST_FIND_NEARBY(){
        return  String.format(SERVER_URL + "MsrstnInfoInqireSvc/getNearbyMsrstnList");
    }

    /** 가까운 측정소 리스트(시,도 입력) */
    public static final String REQUEST_FIND_SIDO(){
        return  String.format(SERVER_URL + "MsrstnInfoInqireSvc/getMsrstnList");
    }

    /** 검색 서비스 리스트 (읍,면,동 입력) */
    public static final String REQUEST_FIND_SEARCH(){
        return  String.format(SERVER_URL + "MsrstnInfoInqireSvc/getTMStdrCrdnt");
    }
}
