package kr.com.misemung.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.com.misemung.R;
import kr.com.misemung.realm.entity.AirRecord;
import kr.com.misemung.ui.adapter.DustGridAdapter;
import kr.com.misemung.vo.AirInfo;
import kr.com.misemung.vo.ListInfo;

@SuppressLint("ValidFragment")
public class DustFragment extends Fragment {

    private LinearLayout ll_main;
    private static TextView main_place;
    private TextView main_level;
    private TextView main_desc;
    private ImageView main_img;
    private ImageView position_bottom;

    private RecyclerView list_recyclerView;
    private DustGridAdapter adapter;

    private AirInfo airInfo;
    private AirRecord airRecord;
    private String stationName;

    public DustFragment(AirRecord airRecord, String stationName) {
        this.airRecord = airRecord;
        this.stationName = stationName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_dust, container, false);

        ll_main = rootView.findViewById(R.id.ll_main);
        main_place = rootView.findViewById(R.id.main_place);
        main_level = rootView.findViewById(R.id.main_level);
        main_desc = rootView.findViewById(R.id.main_desc);
        main_img = rootView.findViewById(R.id.main_img);
        position_bottom = rootView.findViewById(R.id.position_bottom);
        list_recyclerView = rootView.findViewById(R.id.list_recyclerView);

        // 리사이클 뷰 그리드뷰 형식으로 선언
        GridLayoutManager gridLayoutManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            gridLayoutManager = new GridLayoutManager(getContext(), 2);
        }
        list_recyclerView.setLayoutManager(gridLayoutManager);

        // 리스트 item Adapter
        adapter = new DustGridAdapter(getContext());

        // 메인 레벨
        main_level.setText(transMicroDustGrade(airRecord.pm25Grade1h));
        main_place.setText(stationName);

        adapter.addItem(new ListInfo("미세먼지", transGrade(airRecord.pm10Grade1h), airRecord.pm10value+ " ㎍/m³"));
        adapter.addItem(new ListInfo("초미세먼지", transGrade(airRecord.pm25Grade1h), airRecord.pm25value+ " ㎍/m³"));
        adapter.addItem(new ListInfo("아황산가스", transGrade(airRecord.so2grade), airRecord.so2value+ " ppm"));
        adapter.addItem(new ListInfo("일산화탄소", transGrade(airRecord.cograde), airRecord.covalue+ " ppm"));
        adapter.addItem(new ListInfo("오존", transGrade(airRecord.o3grade), airRecord.o3value+ " ppm"));
        adapter.addItem(new ListInfo("이산화질소", transGrade(airRecord.no2grade), airRecord.no2value+ " ppm"));

        list_recyclerView.setAdapter(adapter);

        // 아래 자세히 보기 버튼 클릭시 스크롤 포지션 맨 마지막으로 이동
        position_bottom.setOnClickListener(v -> rootView.scrollTo(0, main_img.getHeight()));

        return rootView;
    }

    public String transMicroDustGrade(String intGrade) {
        String mdTrans;
        switch (intGrade) {
            case "1":
                mdTrans = "좋음";
                ll_main.setBackgroundResource(R.drawable.rectangle_best);
                main_img.setImageResource(R.drawable.main_best);
                main_desc.setText(R.string.best_desc);
                break;
            case "2":
                mdTrans = "보통";
                ll_main.setBackgroundResource(R.drawable.rectangle_normal);
                main_img.setBackgroundResource(R.drawable.main_normal);
                main_desc.setText(R.string.normal_desc);
                break;
            case "3":
                mdTrans = "나쁨";
                ll_main.setBackgroundResource(R.drawable.rectangle_bad);
                main_img.setBackgroundResource(R.drawable.main_bad);
                main_desc.setText(R.string.bad_desc);
                break;
            case "4":
                mdTrans = "매우나쁨";
                ll_main.setBackgroundResource(R.drawable.rectangle_worst);
                main_img.setBackgroundResource(R.drawable.main_worst);
                main_desc.setText(R.string.worst_desc);
                break;
            default:
                mdTrans = "정보없음";
                ll_main.setBackgroundResource(R.drawable.rectangle_normal);
                main_img.setBackgroundResource(R.drawable.main_normal);
                main_desc.setText(R.string.default_desc);
                break;

        }
        return mdTrans;
    }

    public String transGrade(String intGrade) {
        String trans;
        switch (intGrade) {
            case "1":
                trans = "좋음";
                break;
            case "2":
                trans = "보통";
                break;
            case "3":
                trans = "나쁨";
                break;
            case "4":
                trans = "매우나쁨";
                break;
            default:
                trans = "정보없음";
                break;

        }
        return trans;
    }
}
