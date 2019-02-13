package kr.com.misemung.ui.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import kr.com.misemung.R;
import kr.com.misemung.vo.ListInfo;

public class DustGridAdapter extends RecyclerView.Adapter<DustGridAdapter.ItemViewHolder> {

    private Context context;
    // adapter에 들어갈 list 입니다.
    private ArrayList<ListInfo> listData = new ArrayList<>();

    public DustGridAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dust_grid_view, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }

    public void addItem(ListInfo data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView item_title;
        private TextView item_level;
        private TextView item_value;
        private ProgressBar item_progress;

        ItemViewHolder(View itemView) {
            super(itemView);

            item_title = itemView.findViewById(R.id.item_title);
            item_level = itemView.findViewById(R.id.item_level);
            item_value = itemView.findViewById(R.id.item_value);
            item_progress = itemView.findViewById(R.id.item_progress);
        }

        void onBind(ListInfo data) {
            item_title.setText(data.getTitle());
            item_level.setText(data.getLevel());
            item_level.setTextColor(context.getResources().getColor(transGradeBgColor(data.getLevel())));
            item_value.setText(data.getValue());

            if (!data.getValue().contains("-")) {
                String[] pro_val = data.getValue().split(" ");
                double int_val = Float.parseFloat(pro_val[0]);
                item_progress.setProgress((int) int_val);
            }
        }

        public int transGradeBgColor(String strGrade) {
            int trans;
            switch (strGrade) {
                case "제일좋음":
                    trans = R.color.color_best_text;
                    break;
                case "매우좋음":
                    trans = R.color.color_so_good_text;
                    break;
                case "좋음":
                    trans = R.color.color_good_text;
                    break;
                case "보통":
                    trans = R.color.color_normal_text;
                    break;
                case "조심":
                    trans = R.color.color_careful_text;
                    break;
                case "나쁨":
                    trans = R.color.color_bad_text;
                    break;
                case "매우나쁨":
                    trans = R.color.color_so_bad_text;
                    break;
                case "최악":
                    trans = R.color.color_worst_text;
                    break;
                default:
                    trans = R.color.black;
                    break;

            }
            return trans;
        }
    }

}
