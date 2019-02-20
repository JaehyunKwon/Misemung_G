package kr.com.misemung.common;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import kr.com.misemung.R;


/**
 * 공통 팝업 처리
 */

public class CommonPopup {

    /**
     * @param context
     * @param strAlert
     * @return
     */
    public static Dialog makeConfirmDialog(Context context, String title, String strAlert, View.OnClickListener confirmListener){

        final Dialog dialog = new Dialog(context, android.R.style.Theme_Light_NoTitleBar);

        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View insertView = mInflater.inflate(R.layout.popup_common_view, null);

        TextView titleTv = insertView.findViewById(R.id.tv_title);
        TextView messageTv = insertView.findViewById(R.id.tv_message);

        titleTv.setText(title);
        messageTv.setText(strAlert);

        insertView.findViewById(R.id.btn_cancel).setVisibility(View.GONE);

        Button mBtnConfirm = insertView.findViewById(R.id.btn_confirm);
        mBtnConfirm.setOnClickListener(confirmListener);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(insertView);
        dialog.setCancelable(false);

        return dialog;
    }

    /**
     * @param context
     * @param strAlert
     * @return
     */
    public static Dialog showConfirmDialog(Context context, String title, String strAlert, View.OnClickListener confirmListener){

        Dialog dialog = new Dialog(context, android.R.style.Theme_Light_NoTitleBar);

        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View insertView = mInflater.inflate(R.layout.popup_common_view, null);

        TextView titleTv = insertView.findViewById(R.id.tv_title);
        TextView messageTv = insertView.findViewById(R.id.tv_message);

        titleTv.setText(title);
        messageTv.setText(strAlert);

        insertView.findViewById(R.id.btn_cancel).setVisibility(View.GONE);

        TextView mBtnConfirm = insertView.findViewById(R.id.btn_confirm);
        mBtnConfirm.setOnClickListener(confirmListener);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(insertView);
        dialog.setCancelable(false);
        dialog.show();

        return dialog;
    }

    /**
     * @param context
     * @param strAlert
     * @return
     */
    public static Dialog showConfirmCancelDialog(Context context, String title, String strAlert, View.OnClickListener confirmListener, View.OnClickListener cancelListener){

        final Dialog dialog = new Dialog(context, android.R.style.Theme_Light_NoTitleBar);

        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View insertView = mInflater.inflate(R.layout.popup_common_view, null);

        TextView titleTv = insertView.findViewById(R.id.tv_title);
        TextView messageTv = insertView.findViewById(R.id.tv_message);

        titleTv.setText(title);
        messageTv.setText(strAlert);

        TextView confirmBtn = insertView.findViewById(R.id.btn_confirm);
        TextView cancelBtn = insertView.findViewById(R.id.btn_cancel);

        confirmBtn.setOnClickListener(confirmListener);
        cancelBtn.setOnClickListener(cancelListener);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(insertView);
        dialog.setCancelable(false);
        dialog.show();

        return dialog;
    }

    /**
     * @param context
     * @param strAlert
     * @return
     */
    public static Dialog showConfirmCancelDialog(Context context, String title, String strAlert, String confirmStr, View.OnClickListener confirmListener, View.OnClickListener cancelListener){

        final Dialog dialog = new Dialog(context, android.R.style.Theme_Light_NoTitleBar);

        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View insertView = mInflater.inflate(R.layout.popup_common_view, null);

        TextView titleTv = insertView.findViewById(R.id.tv_title);
        TextView messageTv = insertView.findViewById(R.id.tv_message);

        titleTv.setText(title);
        messageTv.setText(strAlert);

        TextView confirmBtn = insertView.findViewById(R.id.btn_confirm);
        TextView cancelBtn = insertView.findViewById(R.id.btn_cancel);

        confirmBtn.setText(confirmStr);
        confirmBtn.setOnClickListener(confirmListener);
        cancelBtn.setOnClickListener(cancelListener);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(insertView);
        dialog.setCancelable(false);
        dialog.show();

        return dialog;
    }

}
