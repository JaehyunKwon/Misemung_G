package kr.com.misemung;

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

import kr.com.misemung.vo.AirInfo;
import kr.com.misemung.vo.ListInfo;

@SuppressLint("ValidFragment")
public class DustFragment extends Fragment {

    private LinearLayout ll_main;
    private static TextView main_place;
    private TextView main_level;
    private TextView main_desc;
    private ImageView main_img;

    private RecyclerView list_recyclerView;
    private DustGridAdapter adapter;

    private AirInfo airInfo;
    private String stationName;

    public DustFragment(AirInfo airInfo, String stationName) {
        this.airInfo = airInfo;
        this.stationName = stationName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dust, container, false);

        ll_main = rootView.findViewById(R.id.ll_main);
        main_place = rootView.findViewById(R.id.main_place);
        main_level = rootView.findViewById(R.id.main_level);
        main_desc = rootView.findViewById(R.id.main_desc);
        main_img = rootView.findViewById(R.id.main_img);
        list_recyclerView = rootView.findViewById(R.id.list_recyclerView);

        // 리사이클 뷰 그리드뷰 형식으로 선언
        GridLayoutManager gridLayoutManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            gridLayoutManager = new GridLayoutManager(getContext(), 2);
        }
        list_recyclerView.setLayoutManager(gridLayoutManager);

        // 리스트 item Adapter
        adapter = new DustGridAdapter();

        // 메인 레벨
        main_level.setText(transGrade(airInfo.getPm25grade1h()));
        main_place.setText(stationName);

        adapter.addItem(new ListInfo("미세먼지", transGrade(airInfo.getPm10grade1h()), airInfo.getPm10value()+ " ㎍/m³"));
        adapter.addItem(new ListInfo("초미세먼지", transGrade(airInfo.getPm25grade1h()), airInfo.getPm25value()+ " ㎍/m³"));
        adapter.addItem(new ListInfo("아황산가스", transGrade(airInfo.getSo2grade()), airInfo.getSo2value()+ " ppm"));
        adapter.addItem(new ListInfo("일산화탄소", transGrade(airInfo.getCograde()), airInfo.getCovalue()+ " ppm"));
        adapter.addItem(new ListInfo("오존", transGrade(airInfo.getO3grade()), airInfo.getO3value()+ " ppm"));
        adapter.addItem(new ListInfo("이산화질소", transGrade(airInfo.getNo2grade()), airInfo.getNo2value()+ " ppm"));

        list_recyclerView.setAdapter(adapter);

        return rootView;
    }

    public String transGrade(String intGrade) {
        String trans = null;
        switch (intGrade) {
            case "1":
                trans = "좋음";
                ll_main.setBackgroundResource(R.drawable.rectangle_best);
                main_desc.setText(R.string.best_desc);
                break;
            case "2":
                trans = "보통";
                ll_main.setBackgroundResource(R.drawable.rectangle_good);
                main_desc.setText(R.string.good_desc);
                break;
            case "3":
                trans = "나쁨";
                break;
            case "4":
                trans = "매우나쁨";
                break;
            default:
                break;

        }
        return trans;
    }
}
