package kr.com.misemung.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class BaseUtil {

    public static boolean hideSoftKeyboard(View v) {
        try {
            Context context = v.getContext();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            return imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch (Exception e) {
            return false;
        }
    }
}
