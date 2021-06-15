package kr.com.misemung.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.com.misemung.R
import kr.com.misemung.ui.adapter.DustGridAdapter.ItemViewHolder
import kr.com.misemung.vo.ListInfo
import java.util.*

class DustGridAdapter(private val context: Context?) : RecyclerView.Adapter<ItemViewHolder>() {
    // adapter에 들어갈 list 입니다.
    private val listData = ArrayList<ListInfo>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_dust_grid_view, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listData[position])
    }

    override fun getItemCount(): Int {
        // RecyclerView의 총 개수 입니다.
        return listData.size
    }

    fun addItem(data: ListInfo) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data)
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val item_title: TextView
        private val item_level: TextView
        private val item_value: TextView
        private val item_progress: ImageView
        fun onBind(data: ListInfo) {
            item_title.text = data.title
            item_level.text = data.level
            item_level.setTextColor(context!!.resources.getColor(transGradeBgColor(data.level)))
            item_value.text = data.value
        }

        fun transGradeBgColor(strGrade: String?): Int {
            val trans: Int
            when (strGrade) {
                "제일좋음" -> {
                    trans = R.color.color_best_text
                    item_progress.setImageResource(R.drawable.best_gauge)
                }
                "좋음" -> {
                    trans = R.color.color_so_good_text
                    item_progress.setImageResource(R.drawable.so_good_gauge)
                }
                "양호" -> {
                    trans = R.color.color_good_text
                    item_progress.setImageResource(R.drawable.good_gauge)
                }
                "보통" -> {
                    trans = R.color.color_normal_text
                    item_progress.setImageResource(R.drawable.normal_gauge)
                }
                "조심" -> {
                    trans = R.color.color_careful_text
                    item_progress.setImageResource(R.drawable.careful_gauge)
                }
                "나쁨" -> {
                    trans = R.color.color_bad_text
                    item_progress.setImageResource(R.drawable.bad_gauge)
                }
                "매우나쁨" -> {
                    trans = R.color.color_so_bad_text
                    item_progress.setImageResource(R.drawable.so_bad_gauge)
                }
                "최악" -> {
                    trans = R.color.color_worst_text
                    item_progress.setImageResource(R.drawable.worst_gauge)
                }
                "GPS OFF" -> {
                    trans = R.color.color_no_gps_text
                    item_progress.setImageResource(R.drawable.worst_gauge)
                }
                else -> {
                    trans = R.color.black
                    item_progress.setImageResource(R.drawable.worst_gauge)
                }
            }
            return trans
        }

        init {
            item_title = itemView.findViewById(R.id.item_title)
            item_level = itemView.findViewById(R.id.item_level)
            item_value = itemView.findViewById(R.id.item_value)
            item_progress = itemView.findViewById(R.id.item_progress)
        }
    }
}