package kr.com.misemung.ui

import kr.com.misemung.vo.AirInfo

class DustContract {
    interface View {
        fun showDustResult(airInfo: AirInfo?, name: String?)
        fun reload(airInfo: AirInfo?, name: String?)
    }

    internal interface UserActionsListener {
        fun loadFineDustData(airInfo: AirInfo?, name: String?)
    }
}