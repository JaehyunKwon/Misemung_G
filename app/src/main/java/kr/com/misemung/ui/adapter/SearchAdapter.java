package kr.com.misemung.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import kr.com.misemung.R;
import kr.com.misemung.realm.repository.CityRepository;
import kr.com.misemung.ui.MainActivity;
import kr.com.misemung.vo.AddrInfo;
import kr.com.misemung.vo.CityInfo;

import static kr.com.misemung.ui.MainActivity.getNearStation;


public class SearchAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<CityInfo> filtered;
    private ViewHolder viewHolder;
    private LayoutInflater inflate;

    public SearchAdapter(Context context, ArrayList<CityInfo> arr) {
        this.context = context;
        this.filtered = arr;
        this.inflate = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return filtered.size();
    }

    @Override
    public Object getItem(int position) {
        return filtered.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflate.inflate(R.layout.item_search_city_list_view, null);

            viewHolder = new ViewHolder();
            viewHolder.umdName = convertView.findViewById(R.id.item_umd_title);

            convertView.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        // 리스트에 있는 데이터를 리스트뷰 셀에 뿌린다.
        viewHolder.umdName.setText(filtered.get(position).getUmdName());

        viewHolder.umdName.setOnClickListener(v -> {
            // 위경도 -> TM 좌표계변환
            ((MainActivity)MainActivity.mContext).getTranscoord(filtered.get(position).getUmdName(), filtered.get(position).getTmX(), filtered.get(position).getTmY());

            // 메인 변수값에 set
            MainActivity.stationName = filtered.get(position).getUmdName();
            MainActivity.getListFlag = false;
        });

        return convertView;
    }

    class ViewHolder {
        public TextView umdName;
    }

}