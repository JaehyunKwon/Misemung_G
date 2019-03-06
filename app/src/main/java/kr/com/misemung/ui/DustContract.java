package kr.com.misemung.ui;

import kr.com.misemung.vo.AirInfo;

public class DustContract {

    interface View {

        void showDustResult(AirInfo airInfo, String name);

        void reload(AirInfo airInfo, String name);
    }

    interface UserActionsListener {
        void loadFineDustData(AirInfo airInfo, String name);
    }
}
