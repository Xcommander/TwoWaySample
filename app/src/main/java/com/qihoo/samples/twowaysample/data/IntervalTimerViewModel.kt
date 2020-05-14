package com.qihoo.samples.twowaysample.data

import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.ObservableInt
import com.qihoo.samples.twowaysample.BR
import com.qihoo.samples.twowaysample.data.IntervalTimerViewModel.TimerStates.PAUSED
import com.qihoo.samples.twowaysample.data.IntervalTimerViewModel.TimerStates.STARTED
import com.qihoo.samples.twowaysample.util.ObservableViewModel
import com.qihoo.samples.twowaysample.util.Timer
import com.qihoo.samples.twowaysample.util.cleanSecondsString
import java.util.*
import kotlin.math.round

/**
 * @author JasonXu
 * @date   2020/4/25
 */
const val INITIAL_SECONDS_PER_FIRST_SET = 5 // Seconds
const val INITIAL_SECONDS_PER_SECOND_SET = 2 // Seconds
const val INITIAL_NUMBER_OF_SETS = 5

class IntervalTimerViewModel(private val timer: Timer) : ObservableViewModel() {
    private var state = TimerStates.STOPPED
    private var stage = StartedStages.WORKING
    var firstTimePerSetValue = ObservableInt(INITIAL_SECONDS_PER_FIRST_SET * 10)
    var secondTimePerSetValue = ObservableInt(INITIAL_SECONDS_PER_SECOND_SET * 10)
    var firstTimeDisplayTime = ObservableInt(firstTimePerSetValue.get())
    var secondTimeDisplayTime = ObservableInt(secondTimePerSetValue.get())

    private var numberOfSetsTotal = INITIAL_NUMBER_OF_SETS
    private var numberOfSetsElapsed = 0
    var numberOfSets: Array<Int> = emptyArray()
        @Bindable get() {
            return arrayOf(numberOfSetsElapsed, numberOfSetsTotal)
        }
        set(value) {
            val newTotal = value[1]
            //没有发生变化
            if (newTotal == numberOfSets[1]) return
            if (newTotal != 0 && newTotal > numberOfSetsElapsed) {
                field = value
                numberOfSetsTotal = newTotal
            }
            notifyPropertyChanged(BR.numberOfSets)
        }

    /**
     *在observable对象中，要使对象的属性，能给随时更新到UI对应的属性中去，一定要在对象属性的get方法添加@Bindable注解
     *然后通过notifyPropertyChanged去通知属性发生了变化
     *对于双向绑定来说，UI更新到对象属性不需要做额外的操作
     *但是对象属性要更新到UI的话，如果这个对象属性是livedata或者observable属性，则不需要做任何事
     *如果不是，则需要绑定对象属性的get方法（@Bindable），并且通过notifyPropertyChanged去通知UI，对象属性发生了变化。
     */
    var timerRunning: Boolean
        @Bindable
        get() {
            Log.e("xulinchao", "get")
            return state == STARTED
        }
        set(value) {
            Log.e("xulinchao", "set")
            //TODO("如何避免两次刷新?")
            if (value) startButtonClicked() else pauseButtonClicked()
        }

    val firstWorking: Boolean
        @Bindable
        get() {
            return stage == StartedStages.WORKING
        }


    private fun startButtonClicked() {
        when (state) {
            TimerStates.PAUSED -> {
                pausedToStart()
            }
            TimerStates.STOPPED -> {
                stoppedToStart()
            }
            STARTED -> {

            }

        }
        val task = object : TimerTask() {
            override fun run() {
                //开始计时,注意这里是在子线程中，所以说子线程中更新观察值，然后观察值更新到UI上，所以说采用
                //数据绑定的好处之一，不必关心子线程操作UI，因为子线程操作的观察值，然后观察值再通知到UI,
                // 最好UI来进行更新
                if (state == STARTED) {
                    //进行更新
                    updateCountdowns()
                }
            }

        }
        timer.start(task)
    }

    private fun updateCountdowns() {
        if (state == TimerStates.STOPPED) {
            //reset 所有
            return

        }
        val elapsed = if (state == TimerStates.PAUSED) {
            timer.getPausedTime()
        } else {
            timer.getElapsedTime()
        }
        /**
         * 第一个计数器处于休息状态，则第二个处于工作状态
         */
        if (stage == StartedStages.RESTING) {
            //second work
            updateSecondTimeValue(elapsed)

        } else {
            //first work
            updateFirstTimeValue(elapsed)

        }


    }

    fun setsDecrease() {
        if (numberOfSetsTotal > numberOfSetsElapsed + 1) {
            numberOfSetsTotal -= 1
            notifyPropertyChanged(BR.numberOfSets)
        }
    }

    fun setsIncrease() {
        numberOfSetsTotal += 1
        notifyPropertyChanged(BR.numberOfSets)
    }

    private fun updateFirstTimeValue(time: Long) {
        stage = StartedStages.WORKING
        val newTime = firstTimePerSetValue.get() - (time / 100).toInt()
        if (newTime <= 0) {
            //结束第一阶段
            firstTimeFinish()
        }
        //获取最小值
        firstTimeDisplayTime.set(newTime.coerceAtLeast(0))

    }

    private fun firstTimeFinish() {
        stage = StartedStages.RESTING
        timer.resetStartTime()
        //BR是存储了数据绑定的ID（变量名称，专门是指@Bindable的可观察的变量）
        notifyPropertyChanged(BR.firstWorking)

    }

    private fun updateSecondTimeValue(time: Long) {
        val newTime = secondTimePerSetValue.get() - (time / 100).toInt()
        secondTimeDisplayTime.set(newTime.coerceAtLeast(0))
        if (newTime <= 0) {
            //结束第二阶段,开始新的一轮，记下次数，以及设定的次数

        }

    }

    fun firstTimeIncrease() = timePerSetIncrease(firstTimePerSetValue, 1)
    fun firstTimeDecrease() = timePerSetIncrease(firstTimePerSetValue, -1, 10)
    fun secondTimeIncrease() = timePerSetIncrease(secondTimePerSetValue, 1)
    fun secondTimeDecrease() = timePerSetIncrease(secondTimePerSetValue, -1)
    private fun timePerSetIncrease(timePerSet: ObservableInt, sign: Int = 1, min: Int = 0) {
        if (timePerSet.get() < 10 && sign < 0) return

        val currentValue = timePerSet.get()
        val newValue = when {
            currentValue < 100 -> currentValue + sign * 10
            currentValue < 60 * 10 -> (round(currentValue / 50f) * 50 + 50 * sign).toInt()
            else -> (round(currentValue / 100f) * 100 + 100 * sign).toInt()
        }
        //设定最小值
        timePerSet.set(newValue.coerceAtLeast(min))
    }

    fun timePerFirstChanged(newValue: CharSequence) {
        firstTimePerSetValue.set(cleanSecondsString(newValue.toString()))
        if (!timerRunning) {
            firstTimeDisplayTime.set(firstTimePerSetValue.get())
        }
    }

    fun timePerSecondChanged(newValue: CharSequence) {
        secondTimePerSetValue.set(cleanSecondsString(newValue.toString()))
        if (isSecondTimeAndRunning()) {
            secondTimeDisplayTime.set(secondTimePerSetValue.get())
        }
    }

    private fun isSecondTimeAndRunning(): Boolean {
        return (state == PAUSED || state == STARTED)
                && firstTimeDisplayTime.get() == 0
    }


    private fun pausedToStart() {
        //根据时间差，重新设置起始时间
        timer.updatePauseTime()
        state = STARTED
        notifyPropertyChanged(BR.timerRunning)
    }

    //reset所有状态
    private fun stoppedToStart() {
        timer.resetStartTime()
        state = STARTED
        notifyPropertyChanged(BR.timerRunning)
    }

    private fun pauseButtonClicked() {
        notifyPropertyChanged(BR.timerRunning)
    }

    /**
     * 计数器的整体状态
     */
    enum class TimerStates { STOPPED, STARTED, PAUSED }

    /**
     * 单个计数器的状态
     */
    enum class StartedStages { WORKING, RESTING }
}