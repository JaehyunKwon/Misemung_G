package kr.com.misemung.util

import android.content.Context
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager

object BaseUtil {
    /**
     * 소프트키 감추기
     */
    fun hideSoftKeyboard(v: View?): Boolean {
        return try {
            val context = v!!.context
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Horizontal 스크롤 파악하기 위한 Detector
     */
    class XScrollDetector : SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            return Math.abs(distanceX) > Math.abs(distanceY)
        }
    }
}