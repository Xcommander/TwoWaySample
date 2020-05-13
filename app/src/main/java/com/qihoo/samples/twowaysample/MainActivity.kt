package com.qihoo.samples.twowaysample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.qihoo.samples.twowaysample.data.IntervalTimerViewModel
import com.qihoo.samples.twowaysample.data.IntervalTimerViewModelFactory
import com.qihoo.samples.twowaysample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        val viewModel = ViewModelProvider(
            viewModelStore,
            IntervalTimerViewModelFactory
        ).get(
            IntervalTimerViewModel::class.java
        )
        binding.viewmodel = viewModel
    }
}
