package com.example.trackersignal.functions

import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    var operatorNome: String = ""
    var ssid: String = ""
    var networkType: String = ""
    var networkSubType: String = ""
    var linkSpeedMbps: Int = 0
    var pingResult: String = ""
}
