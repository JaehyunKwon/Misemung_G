package kr.com.misemung.ui;

import kr.com.misemung.vo.AirInfo;

public class DustPresenter implements DustContract.UserActionsListener {

    private final DustContract.View mView;

    public DustPresenter(DustContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void loadFineDustData(AirInfo airInfo, String name) {
        mView.showDustResult(airInfo, name);
    }
}
