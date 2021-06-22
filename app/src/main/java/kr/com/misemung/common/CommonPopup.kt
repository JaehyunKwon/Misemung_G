package kr.com.misemung.common

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import kr.com.misemung.R

/**
 * 공통 팝업 처리
 */
object CommonPopup {
    /**
     * @param context
     * @param strAlert
     * @return
     */
    fun makeConfirmDialog(
        context: Context,
        strAlert: String?,
        confirmListener: View.OnClickListener?
    ): Dialog {
        val dialog = Dialog(context, android.R.style.Theme_Light_NoTitleBar)
        val mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val insertView = mInflater.inflate(R.layout.popup_common_view, null)

        //TextView titleTv = insertView.findViewById(R.id.tv_title);
        val messageTv = insertView.findViewById<TextView>(R.id.tv_message)

        //titleTv.setText(title);
        messageTv.text = strAlert
        insertView.findViewById<View>(R.id.btn_cancel).visibility = View.GONE
        val mBtnConfirm = insertView.findViewById<Button>(R.id.btn_confirm)
        mBtnConfirm.setOnClickListener(confirmListener)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(insertView)
        dialog.setCancelable(false)
        return dialog
    }

    /**
     * @param context
     * @param strAlert
     * @return
     */
    fun showConfirmDialog(
        context: Context,
        strAlert: String?,
        confirmListener: View.OnClickListener?
    ): Dialog {
        val dialog = Dialog(context, android.R.style.Theme_Light_NoTitleBar)
        val mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val insertView = mInflater.inflate(R.layout.popup_common_view, null)

        //TextView titleTv = insertView.findViewById(R.id.tv_title);
        val messageTv = insertView.findViewById<TextView>(R.id.tv_message)

        //titleTv.setText(title);
        messageTv.text = strAlert
        insertView.findViewById<View>(R.id.btn_cancel).visibility = View.GONE
        val mBtnConfirm = insertView.findViewById<TextView>(R.id.btn_confirm)
        mBtnConfirm.setOnClickListener(confirmListener)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(insertView)
        dialog.setCancelable(false)
        dialog.show()
        return dialog
    }

    /**
     * @param context
     * @param strAlert
     * @return
     */
    fun showConfirmCancelDialog(
        context: Context,
        title: String?,
        strAlert: String?,
        cancelStr: String?,
        confirmStr: String?,
        cancelListener: View.OnClickListener?,
        confirmListener: View.OnClickListener?
    ): Dialog {
        val dialog = Dialog(context, android.R.style.Theme_Light_NoTitleBar)
        val mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val insertView = mInflater.inflate(R.layout.popup_common_view, null)

        val messageTv = insertView.findViewById<TextView>(R.id.tv_message)

        messageTv.text = strAlert
        val confirmBtn = insertView.findViewById<TextView>(R.id.btn_confirm)
        val cancelBtn = insertView.findViewById<TextView>(R.id.btn_cancel)
        confirmBtn.text = confirmStr
        cancelBtn.text = cancelStr
        cancelBtn.setOnClickListener(cancelListener)
        confirmBtn.setOnClickListener(confirmListener)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(insertView)
        dialog.setCancelable(false)
        dialog.show()
        return dialog
    }

    /**
     * @param context
     * @param strAlert
     * @return
     */
    fun showConfirmCancelDialog(
        context: Context,
        strAlert: String?,
        confirmStr: String?,
        cancelStr: String?,
        confirmListener: View.OnClickListener?,
        cancelListener: View.OnClickListener?
    ): Dialog {
        val dialog = Dialog(context, android.R.style.Theme_Light_NoTitleBar)
        val mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val insertView = mInflater.inflate(R.layout.popup_common_view, null)

        val messageTv = insertView.findViewById<TextView>(R.id.tv_message)

        messageTv.text = strAlert
        val confirmBtn = insertView.findViewById<TextView>(R.id.btn_confirm)
        val cancelBtn = insertView.findViewById<TextView>(R.id.btn_cancel)
        confirmBtn.text = confirmStr
        cancelBtn.text = cancelStr
        confirmBtn.setOnClickListener(confirmListener)
        cancelBtn.setOnClickListener(cancelListener)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(insertView)
        dialog.setCancelable(false)
        dialog.show()
        return dialog
    }
}