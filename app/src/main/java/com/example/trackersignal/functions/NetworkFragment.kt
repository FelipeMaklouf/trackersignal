package com.example.trackersignal.functions

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.trackersignal.R
import java.io.IOException
import java.net.InetAddress

class NetworkFragment : Fragment() {
    lateinit var sharedViewModel: SharedViewModel

    private lateinit var telephonyManager: TelephonyManager
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var wifiManager: WifiManager

    private lateinit var operatorTextView: TextView
    private lateinit var networkTypeTextView: TextView
    private lateinit var networkSpeedTextView: TextView
    private lateinit var pingResultTextView: TextView
    private lateinit var wifiSpeedTextView: TextView
    private lateinit var handler: Handler
    private val updateInterval = 1000

    @SuppressLint("SetTextI18n", "CutPasteId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_network, container, false)

        operatorTextView = view.findViewById(R.id.operatorTextView)
        networkTypeTextView = view.findViewById(R.id.networkTypeTextView)
        networkSpeedTextView = view.findViewById(R.id.networkSpeedTextView)
        wifiSpeedTextView = view.findViewById(R.id.networkSpeedTextView)
        pingResultTextView = view.findViewById(R.id.pingResultTextView)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        telephonyManager =
            requireContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        wifiManager = requireContext().getSystemService(Context.WIFI_SERVICE) as WifiManager

        PingTask().execute()
        handler = Handler()
        updateInfo()

        return view
    }
    override fun onPause() {
        super.onPause()
        operatorInfo()
    }
    override fun onResume() {
        super.onResume()
        updateInfo()
    }

    private fun updateInfo() {
        PingTask().execute()
        operatorInfo()
        handler.postDelayed({
            updateInfo()
        }, updateInterval.toLong())
    }

    @SuppressLint("SetTextI18n")
    private fun operatorInfo() {
        val operatorNome = telephonyManager.networkOperatorName
        val networkInfo = connectivityManager.activeNetworkInfo
        val networkType = networkInfo?.typeName
        val networkSubType = networkInfo?.subtypeName
        val wifiInfo = wifiManager.connectionInfo
        val ssid = wifiInfo.ssid
        val linkSpeedMbps = wifiInfo?.linkSpeed

        if (networkInfo != null && networkInfo.isConnected) {
            if (networkType == "MOBILE") {
                networkTypeTextView.text = "Network Type: Mobile Data"
                operatorTextView.text = "Operator: $operatorNome"
            } else {
                networkTypeTextView.text = "Network Type: $networkType"
                operatorTextView.text = "Provider: $ssid"
            }

            if (linkSpeedMbps != null && linkSpeedMbps > 0) {
                wifiSpeedTextView.text = "WiFi Speed: $linkSpeedMbps Mbps"
            } else {
                when (networkSubType) {
                    "LTE" -> networkSpeedTextView.text = "Network Speed: 4G"
                    "UMTS" -> networkSpeedTextView.text = "Network Speed: 3G+"
                    "HSPA+" -> networkSpeedTextView.text = "Network Speed: 3G"
                    "GPRS" -> networkSpeedTextView.text = "Network Speed: 2G"
                }
            }
        }
        sharedViewModel.operatorNome = operatorNome
        sharedViewModel.ssid = ssid
        if (networkType != null) {
            sharedViewModel.networkType = networkType
        }
        if (networkSubType != null) {
            sharedViewModel.networkSubType = networkSubType
        }
        if (linkSpeedMbps != null) {
            sharedViewModel.linkSpeedMbps = linkSpeedMbps
        }
    }
    private inner class PingTask : AsyncTask<Void, Void, String>() {
        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: Void?): String {
            return try {
                val address = InetAddress.getByName("www.google.com")
                val startTime = System.currentTimeMillis()
                if (address.isReachable(3000)) {
                    val endTime = System.currentTimeMillis()
                    (endTime - startTime).toString()
                } else {
                    "Timeout"
                }
            } catch (e: IOException) {
                "Erro: ${e.message}"
            }
        }
        @SuppressLint("SetTextI18n")
        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: String?) {
            pingResultTextView.text = "Latency:\n$result ms"
            if (result != null) {
                sharedViewModel.pingResult = result
            }
        }
    }

}
