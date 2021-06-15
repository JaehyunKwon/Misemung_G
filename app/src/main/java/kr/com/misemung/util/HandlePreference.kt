package kr.com.misemung.util

import android.content.Context
import android.content.SharedPreferences
import kr.com.misemung.MiseMungGApplication

/**
 * SharedPreferences 관리
 */
object HandlePreference {
    /**
     * Application Preference 전역 식별자.
     */
    const val PREF_NAME = "kr.com.misemung.case"

    /** Fragment 리스트 사이즈  */
    var FRAGMENT_SIZE = "fragment_size"

    /** Fragment 리스트 사이즈 값 저장  */
    var fragmentListSize: Int
        get() {
            val settings: SharedPreferences =
                MiseMungGApplication.Companion.Instance().getSharedPreferences(
                    PREF_NAME, Context.MODE_PRIVATE
                )
            return settings.getInt(FRAGMENT_SIZE, 0)
        }
        set(size) {
            val settings: SharedPreferences =
                MiseMungGApplication.Companion.Instance().getSharedPreferences(
                    PREF_NAME, Context.MODE_PRIVATE
                )
            val editor = settings.edit()
            editor.putInt(FRAGMENT_SIZE, size)
            editor.commit()
        }
}