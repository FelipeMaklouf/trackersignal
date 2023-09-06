package com.example.trackersignal.functions

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.trackersignal.R
import java.io.File
import java.io.IOException


class LocationFragment : Fragment() {

    // Location
    private companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
        private const val LOCATION_UPDATE_INTERVAL = 1000L
    }

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var fragmentManager: FragmentManager

    private lateinit var textViewLatitude: TextView
    private lateinit var textViewLongitude: TextView
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_location, container, false)
        textViewLatitude = view.findViewById(R.id.textViewLatitude)
        textViewLongitude = view.findViewById(R.id.textViewLongitude)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        fragmentManager = requireActivity().supportFragmentManager

        // Start location updates
        startLocationUpdates()

        val generateButton: Button = view.findViewById(R.id.generate_button)
        generateButton.setOnClickListener {
            saveLocationToCSV(currentLatitude, currentLongitude)
            Toast.makeText(requireContext(), "Dados salvos no Arquivo", Toast.LENGTH_SHORT).show()
        }
        saveLocationToCSV(currentLatitude, currentLongitude)

        return view
    }

    private fun startLocationUpdates() {
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                updateLocationUI(location)
            }

            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        // Request location permission if not granted
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireContext() as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        } else {
            // Register the location listener to receive updates
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                LOCATION_UPDATE_INTERVAL,
                0f,
                locationListener
            )
        }
    }
    override fun onPause() {
        super.onPause()
        saveLocationToCSV(currentLatitude, currentLongitude)
    }
    override fun onResume() {
        super.onResume()
        saveLocationToCSV(currentLatitude, currentLongitude)
    }

    @SuppressLint("SetTextI18n")
    private fun updateLocationUI(location: Location) {
        currentLatitude = location.latitude
        currentLongitude = location.longitude

        textViewLatitude.text = "Latitude: $currentLatitude"
        textViewLongitude.text = "Longitude: $currentLongitude"

    }

    private fun saveLocationToCSV(latitude: Double, longitude: Double) {
        var operatorNome = sharedViewModel.operatorNome
        var ssid = sharedViewModel.ssid
        var networkType = sharedViewModel.networkType
        var networkSubType = sharedViewModel.networkSubType
        var linkSpeedMbps = sharedViewModel.linkSpeedMbps
        var pingResult = sharedViewModel.pingResult

        Log.d("Teste Latencia", "latency: $pingResult")

        val csvFileName = "info_data.csv"
        val csvHeader = "Operadora, Nome Wifi (Caso Ativado),Latitude,Longitude, Tipo de Rede, Tipo de Sub Rede, Velocidade Wifi, Latencia\n"
        val csvRow = "$operatorNome,$ssid,$latitude,$longitude,$networkType, $networkSubType, $linkSpeedMbps, $pingResult\n"

        val customFolderPath = Environment.getExternalStorageDirectory().toString() + "/Documents/Tracker/Log"
        val csvFile = File(customFolderPath, csvFileName)

        try {
            if (!csvFile.parentFile?.exists()!!) {
                csvFile.parentFile?.mkdirs()
            }

            if (!csvFile.exists()) {
                csvFile.createNewFile()
                csvFile.writeText(csvHeader)
            }
            csvFile.appendText(csvRow)
            Toast.makeText(requireContext(), "Dados salvos no arquivo", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Log.e("LocationFragment", "Error writing CSV file: ${e.message}")
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        saveLocationToCSV(currentLatitude, currentLongitude)
    }
}