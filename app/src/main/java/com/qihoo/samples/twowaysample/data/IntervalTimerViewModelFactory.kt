package com.qihoo.samples.twowaysample.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.qihoo.samples.twowaysample.util.DefaultTimer
import java.lang.IllegalArgumentException

/**
 * @author JasonXu
 * @date   2020/5/3
 */
object IntervalTimerViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(IntervalTimerViewModel::class.java)){
            return IntervalTimerViewModel(DefaultTimer) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}