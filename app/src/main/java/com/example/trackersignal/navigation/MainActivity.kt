package com.example.trackersignal.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.trackersignal.R
import com.example.trackersignal.databinding.ActivityMainBinding
import com.example.trackersignal.functions.LocationFragment
import com.example.trackersignal.functions.NetworkFragment
import com.example.trackersignal.functions.OthersFragment
import com.example.trackersignal.functions.SharedViewModel

class MainActivity : AppCompatActivity() {

    //navigation between screens
    private lateinit var binding: ActivityMainBinding

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        //navigation between screens
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        replaceFragment(NetworkFragment())
        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.network -> replaceFragment(NetworkFragment())
                R.id.location -> replaceFragment(LocationFragment())
                R.id.others -> replaceFragment(OthersFragment())

                else ->{

                }
            }
            true
        }
    }
    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }
}