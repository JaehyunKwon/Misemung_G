package kr.com.misemung.network;

import android.net.Uri;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Created by KJH on 2017. 3. 16..
 */

public class Request {
    private final Uri.Builder uBuilder;
    private final String api;
    private Uri uri;
    private ArrayList<FilePart> partList;
    private HashMap<String, Object> params = new HashMap<>();
    private String jsonString = "";

    public Request(String api) {
        this.api = api;

        uBuilder = Uri.parse("https://dapi.kakao.com/")
                .buildUpon();
//                .appendPath(api.getKind());

        StringTokenizer st = new StringTokenizer(api, "/");
        while (st.hasMoreTokens()) {
            uBuilder.appendPath(st.nextToken());
        }
    }

    /**
     * parameter를 추가한다.
     * */
    public void addParam(String key, Object value) {
        Object val = "";
        if (value == null) {
            val = "";
        } else {
            val = value;
        }

        //if (API.GET.equals(api.getMethod())) {
            uBuilder.appendQueryParameter(key, (String) val);
        //}

        params.put(key, val);
    }

    public void addJson(String json) {
        jsonString = json;
    }

    /**
     * uri path를 추가한다.
     * */
    public void addPath(String path) {
        if (path.length() != 0) {
            uBuilder.appendPath(path);
        }
    }

    public void addPart(String key, String name, File part) {
        if (part == null) {
            new RuntimeException("File part must not null");
        }

        if (partList == null) {
            partList = new ArrayList<FilePart>();
        }
        partList.add(new FilePart(key, name, part));
    }

    public ArrayList<FilePart> getPartList() {
        return partList;
    }

    public Uri getUri() {
        if (uri == null) {
            uri = uBuilder.build();
        }

        return uri;
    }

    public String getApi() {
        return api;
    }

    @Override
    public String toString() {
        return "Request{" +
                ", api=" + api +
                ", uri=" + uri +
                '}';
    }

    public HashMap<String, Object> getParams() {
        return params;
    }

    public String getJsonString() {
        return jsonString;
    }

    public static class FilePart {
        private String key;
        private String name;
        private File part;

        public FilePart(String key, String name, File part) {
            this.setKey(key);
            this.setName(name);
            this.setPart(part);
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public File getPart() {
            return part;
        }

        public void setPart(File part) {
            this.part = part;
        }
    }
}
