package kr.com.misemung.ui

import kr.com.misemung.ui.DustContract.UserActionsListener
import kr.com.misemung.vo.AirInfo

class DustPresenter(private val mView: DustContract.View) : UserActionsListener {
    override fun loadFineDustData(airInfo: AirInfo?, name: String?) {
        mView.showDustResult(airInfo, name)
    }
}