package kr.com.misemung.util;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class BaseUtil {

    /**
     * 소프트키 감추기
     * */
    public static boolean hideSoftKeyboard(View v) {
        try {
            Context context = v.getContext();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            return imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Horizontal 스크롤 파악하기 위한 Detector
     * */
    public static class XScrollDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return Math.abs(distanceX) > Math.abs(distanceY);
        }
    }
}
