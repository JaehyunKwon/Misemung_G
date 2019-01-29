package kr.com.misemung.vo;

public class ListInfo {

    private String title;
    private String level;
    private String value;

    public ListInfo(String title, String level, String value) {
        this.title = title;
        this.level = level;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ListInfo{" +
                "title='" + title + '\'' +
                ", level='" + level + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
