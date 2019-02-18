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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import kr.com.misemung.R;
import kr.com.misemung.realm.entity.AirRecord;
import kr.com.misemung.ui.adapter.DustGridAdapter;
import kr.com.misemung.vo.ListInfo;

@SuppressLint("ValidFragment")
public class DustFragment extends Fragment {

    private ScrollView scroll_view;
    private RelativeLayout ll_main;
    private static TextView main_place;
    private TextView main_level;
    private TextView main_desc;
    private ImageView main_img;
    private ImageView position_bottom;
    private LinearLayout bottom_layout;

    private RecyclerView list_recyclerView;
    private DustGridAdapter adapter;

    private AirRecord airRecord;
    private String stationName;

    private int dust_level;
    private int mdust_level;

    public DustFragment(AirRecord airRecord, String stationName) {
        this.airRecord = airRecord;
        this.stationName = stationName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_dust, container, false);

        scroll_view = rootView.findViewById(R.id.scroll_view);
        ll_main = rootView.findViewById(R.id.ll_main);
        main_place = rootView.findViewById(R.id.main_place);
        main_level = rootView.findViewById(R.id.main_level);
        main_desc = rootView.findViewById(R.id.main_desc);
        main_img = rootView.findViewById(R.id.main_img);
        bottom_layout = rootView.findViewById(R.id.bottom_layout);
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

        // 메인 레벨 (미세먼지 등급이 초미세먼지보다 안 좋을 경우 미세먼지 등급기준)
        main_level.setText(dust_level > mdust_level
                ? transDustGrade(airRecord.pm10value) : transMicroDustGrade(airRecord.pm25value));
        main_place.setText(stationName);

        adapter.addItem(new ListInfo("미세먼지", transDustGrade(airRecord.pm10value), airRecord.pm10value+ " ㎍/m³"));
        adapter.addItem(new ListInfo("초미세먼지", transMicroDustGrade(airRecord.pm25value), airRecord.pm25value+ " ㎍/m³"));
        adapter.addItem(new ListInfo("아황산가스", transSO2Grade(airRecord.so2value), airRecord.so2value+ " ppm"));
        adapter.addItem(new ListInfo("일산화탄소", transCOGrade(airRecord.covalue), airRecord.covalue+ " ppm"));
        adapter.addItem(new ListInfo("오존", transO3Grade(airRecord.o3value), airRecord.o3value+ " ppm"));
        adapter.addItem(new ListInfo("이산화질소", transNO2Grade(airRecord.no2value), airRecord.no2value+ " ppm"));

        list_recyclerView.setAdapter(adapter);
        list_recyclerView.setNestedScrollingEnabled(false);

        // 아래 자세히 보기 버튼 클릭시 스크롤 포지션 맨 마지막으로 이동
        bottom_layout.setOnClickListener(v -> scroll_view.smoothScrollTo(0, ll_main.getHeight()));

        return rootView;
    }

    /**
     * 미세먼지 등급
     * */
    public String transDustGrade(String stringGrade) {
        String dTrans;
        if (!stringGrade.contains("-")) {
            int grade = Integer.parseInt(stringGrade);

            if (grade <= 15) { // 제일좋음
                dTrans = "제일좋음";
                ll_main.setBackgroundResource(R.drawable.rectangle_best);
                main_img.setImageResource(R.drawable.main_best);
                main_desc.setText(R.string.best_desc);
                dust_level = 1;

            } else if (grade <= 30) { // 좋음
                dTrans = "좋음";
                ll_main.setBackgroundResource(R.drawable.rectangle_so_good);
                main_img.setImageResource(R.drawable.main_so_good);
                main_desc.setText(R.string.so_good_desc);
                dust_level = 2;

            } else if (grade <= 40) { // 양호
                dTrans = "양호";
                ll_main.setBackgroundResource(R.drawable.rectangle_good);
                main_img.setImageResource(R.drawable.main_good);
                main_desc.setText(R.string.good_desc);
                dust_level = 3;

            } else if (grade <= 50) { // 보통
                dTrans = "보통";
                ll_main.setBackgroundResource(R.drawable.rectangle_normal);
                main_img.setImageResource(R.drawable.main_normal);
                main_desc.setText(R.string.normal_desc);
                dust_level = 4;

            } else if (grade <= 75) { // 조심
                dTrans = "조심";
                ll_main.setBackgroundResource(R.drawable.rectangle_careful);
                main_img.setImageResource(R.drawable.main_careful);
                main_desc.setText(R.string.careful_desc);
                dust_level = 5;

            } else if (grade <= 100) { // 나쁨
                dTrans = "나쁨";
                ll_main.setBackgroundResource(R.drawable.rectangle_bad);
                main_img.setBackgroundResource(R.drawable.main_bad);
                main_desc.setText(R.string.bad_desc);
                dust_level = 6;

            } else if (grade <= 150) { // 매우나쁨
                dTrans = "매우나쁨";
                ll_main.setBackgroundResource(R.drawable.rectangle_so_bad);
                main_img.setImageResource(R.drawable.main_so_bad);
                main_desc.setText(R.string.so_bad_desc);
                dust_level = 7;

            } else if (grade > 151) { // 최악
                dTrans = "최악";
                ll_main.setBackgroundResource(R.drawable.rectangle_worst);
                main_img.setBackgroundResource(R.drawable.main_worst);
                main_desc.setText(R.string.worst_desc);
                dust_level = 8;

            } else {
                dTrans = "정보없음";
                ll_main.setBackgroundResource(R.drawable.rectangle_normal);
                main_img.setBackgroundResource(R.drawable.main_normal);
                main_desc.setText(R.string.default_desc);
                dust_level = 0;
            }
        } else {
            dTrans = "정보없음";
            ll_main.setBackgroundResource(R.drawable.rectangle_normal);
            main_img.setBackgroundResource(R.drawable.main_normal);
            main_desc.setText(R.string.default_desc);
            dust_level = 0;
        }
        return dTrans;
    }

    /**
     * 초미세먼지 등급
     * */
    public String transMicroDustGrade(String microDust) {
        String mdTrans;
        if (!microDust.contains("-")) {
            int grade = Integer.parseInt(microDust);
            if (grade <= 8) { // 제일좋음
                mdTrans = "제일좋음";
                ll_main.setBackgroundResource(R.drawable.rectangle_best);
                main_img.setImageResource(R.drawable.main_best);
                main_desc.setText(R.string.best_desc);
                mdust_level = 1;

            } else if (grade <= 15) { // 좋음
                mdTrans = "좋음";
                ll_main.setBackgroundResource(R.drawable.rectangle_so_good);
                main_img.setImageResource(R.drawable.main_so_good);
                main_desc.setText(R.string.so_good_desc);
                mdust_level = 2;

            } else if (grade <= 20) { // 양호
                mdTrans = "양호";
                ll_main.setBackgroundResource(R.drawable.rectangle_good);
                main_img.setImageResource(R.drawable.main_good);
                main_desc.setText(R.string.good_desc);
                mdust_level = 3;

            } else if (grade <= 25) { // 보통
                mdTrans = "보통";
                ll_main.setBackgroundResource(R.drawable.rectangle_normal);
                main_img.setImageResource(R.drawable.main_normal);
                main_desc.setText(R.string.normal_desc);
                mdust_level = 4;

            } else if (grade <= 37) { // 조심
                mdTrans = "조심";
                ll_main.setBackgroundResource(R.drawable.rectangle_careful);
                main_img.setImageResource(R.drawable.main_careful);
                main_desc.setText(R.string.careful_desc);
                mdust_level = 5;

            } else if (grade <= 50) { // 나쁨
                mdTrans = "나쁨";
                ll_main.setBackgroundResource(R.drawable.rectangle_bad);
                main_img.setBackgroundResource(R.drawable.main_bad);
                main_desc.setText(R.string.bad_desc);
                mdust_level = 6;

            } else if (grade <= 75) { // 매우나쁨
                mdTrans = "매우나쁨";
                ll_main.setBackgroundResource(R.drawable.rectangle_so_bad);
                main_img.setImageResource(R.drawable.main_so_bad);
                main_desc.setText(R.string.so_bad_desc);
                mdust_level = 7;

            } else if (grade > 76){ // 최악
                mdTrans = "최악";
                ll_main.setBackgroundResource(R.drawable.rectangle_worst);
                main_img.setBackgroundResource(R.drawable.main_worst);
                main_desc.setText(R.string.worst_desc);
                mdust_level = 8;

            } else {
                mdTrans = "정보없음";
                ll_main.setBackgroundResource(R.drawable.rectangle_normal);
                main_img.setBackgroundResource(R.drawable.main_normal);
                main_desc.setText(R.string.default_desc);
                mdust_level = 0;
            }
        } else {
            mdTrans = "정보없음";
            ll_main.setBackgroundResource(R.drawable.rectangle_normal);
            main_img.setBackgroundResource(R.drawable.main_normal);
            main_desc.setText(R.string.default_desc);
            mdust_level = 0;
        }

        return mdTrans;
    }

    /**
     * 아황산가스 등급
     * */
    public String transSO2Grade(String stringGrade) {
        String dTrans;
        if (!stringGrade.contains("-")) {
            float grade = Float.parseFloat(stringGrade);

            if (grade <= 0.01) { // 제일좋음
                dTrans = "제일좋음";
            } else if (grade <= 0.02) { // 좋음
                dTrans = "좋음";
            } else if (grade <= 0.04) { // 양호
                dTrans = "양호";
            } else if (grade <= 0.05) { // 보통
                dTrans = "보통";
            } else if (grade <= 0.1) { // 조심
                dTrans = "조심";
            } else if (grade <= 0.15) { // 나쁨
                dTrans = "나쁨";
            } else if (grade <= 0.6) { // 매우나쁨
                dTrans = "매우나쁨";
            } else if (grade > 0.7) { // 최악
                dTrans = "최악";
            } else {
                dTrans = "정보없음";
            }
        } else {
            dTrans = "정보없음";
        }
        return dTrans;
    }

    /**
     * 일산화탄소 등급
     * */
    public String transCOGrade(String stringGrade) {
        String dTrans;
        if (!stringGrade.contains("-")) {
            float grade = Float.parseFloat(stringGrade);

            if (grade <= 1) { // 제일좋음
                dTrans = "제일좋음";
            } else if (grade <= 2) { // 좋음
                dTrans = "좋음";
            } else if (grade <= 5.5) { // 양호
                dTrans = "양호";
            } else if (grade <= 9) { // 보통
                dTrans = "보통";
            } else if (grade <= 12) { // 조심
                dTrans = "조심";
            } else if (grade <= 15) { // 나쁨
                dTrans = "나쁨";
            } else if (grade <= 32) { // 매우나쁨
                dTrans = "매우나쁨";
            } else if (grade > 33) { // 최악
                dTrans = "최악";
            } else {
                dTrans = "정보없음";
            }
        } else {
            dTrans = "정보없음";
        }
        return dTrans;
    }

    /**
     * 오존 등급
     * */
    public String transO3Grade(String stringGrade) {
        String dTrans;
        if (!stringGrade.contains("-")) {
            float grade = Float.parseFloat(stringGrade);

            if (grade <= 0.02) { // 제일좋음
                dTrans = "제일좋음";
            } else if (grade <= 0.03) { // 좋음
                dTrans = "좋음";
            } else if (grade <= 0.06) { // 양호
                dTrans = "양호";
            } else if (grade <= 0.09) { // 보통
                dTrans = "보통";
            } else if (grade <= 0.12) { // 조심
                dTrans = "조심";
            } else if (grade <= 0.15) { // 나쁨
                dTrans = "나쁨";
            } else if (grade <= 0.38) { // 매우나쁨
                dTrans = "매우나쁨";
            } else if (grade > 0.39) { // 최악
                dTrans = "최악";
            } else {
                dTrans = "정보없음";
            }
        } else {
            dTrans = "정보없음";
        }
        return dTrans;
    }

    /**
     * 이산화질소 등급
     * */
    public String transNO2Grade(String stringGrade) {
        String dTrans;
        if (!stringGrade.contains("-")) {
            float grade = Float.parseFloat(stringGrade);

            if (grade <= 0.02) { // 제일좋음
                dTrans = "제일좋음";
            } else if (grade <= 0.03) { // 좋음
                dTrans = "좋음";
            } else if (grade <= 0.05) { // 양호
                dTrans = "양호";
            } else if (grade <= 0.06) { // 보통
                dTrans = "보통";
            } else if (grade <= 0.13) { // 조심
                dTrans = "조심";
            } else if (grade <= 0.2) { // 나쁨
                dTrans = "나쁨";
            } else if (grade <= 1.1) { // 매우나쁨
                dTrans = "매우나쁨";
            } else if (grade > 1.2) { // 최악
                dTrans = "최악";
            } else {
                dTrans = "정보없음";
            }
        } else {
            dTrans = "정보없음";
        }
        return dTrans;
    }

}
