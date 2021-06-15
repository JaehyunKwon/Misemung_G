package kr.com.misemung.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.*
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.com.misemung.R
import kr.com.misemung.ui.MainActivity
import kr.com.misemung.vo.CityInfo
import java.util.*

class SearchAdapter(context: Context, private val filtered: ArrayList<CityInfo>?) :
    BaseAdapter() {

    override fun getCount(): Int {
        return filtered!!.size
    }

    override fun getItem(position: Int): Any {
        return filtered!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_search_city_list_view, parent, false)

            viewHolder = ViewHolder()
            viewHolder.umdName = view.findViewById(R.id.item_umd_title)

            view.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
            view = convertView
        }

        // 리스트에 있는 데이터를 리스트뷰 셀에 뿌린다.
        viewHolder.umdName?.text = filtered!![position].umdName
        viewHolder.umdName?.setOnClickListener { v: View? ->
            // 위경도 -> TM 좌표계변환
            (MainActivity.Companion.mContext as MainActivity).getTranscoord(
                filtered[position].umdName,
                filtered[position].tmX,
                filtered[position].tmY
            )

            // 메인 변수값에 set
            MainActivity.Companion.stationName = filtered[position].umdName
            MainActivity.Companion.getListFlag = false
        }
        return view
    }

    internal inner class ViewHolder {
        var umdName: TextView? = null
    }

}