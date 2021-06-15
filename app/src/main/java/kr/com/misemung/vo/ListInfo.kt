package kr.com.misemung.vo

class ListInfo(var title: String, var level: String, var value: String) {
    override fun toString(): String {
        return "ListInfo{" +
                "title='" + title + '\'' +
                ", level='" + level + '\'' +
                ", value='" + value + '\'' +
                '}'
    }
}