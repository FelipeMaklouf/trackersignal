package com.example.trackersignal.functions

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.trackersignal.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class OthersFragment : Fragment() {

    //data e hora
    private lateinit var dataTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var formattedTime: SimpleDateFormat

    //lopping
    private lateinit var handler: Handler
    private val updateInterval = 1000

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_others, container, false)

        //date and time
        dataTextView = view.findViewById(R.id.dataTextView)
        timeTextView = view.findViewById(R.id.timeTextView)
        formattedTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        getDateTime()

        //loppping
        handler = Handler()
        updateInfo()

        return view
    }
    private fun updateInfo() {
        getDateTime()
        handler.postDelayed({
            updateInfo()
        }, updateInterval.toLong())
    }
    @SuppressLint("SetTextI18n")
    private fun getDateTime() {
        val calendar = Calendar.getInstance()

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        val timeWithSecs = formattedTime.format(calendar.time)
        val formattedDate = String.format("%02d/%02d/%04d", day, month, year)

        dataTextView.text = "Date:\n$formattedDate"
        timeTextView.text = "Hour:\n$timeWithSecs"
    }
}