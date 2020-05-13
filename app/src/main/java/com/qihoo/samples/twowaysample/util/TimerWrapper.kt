package com.qihoo.samples.twowaysample.util

import java.util.*

/**
 * @author JasonXu
 * @date   2020/5/3
 */
interface Timer {
    fun reset()
    fun start(task: TimerTask)
    fun getElapsedTime(): Long
    fun updatePauseTime()
    fun getPausedTime(): Long
    fun resetStartTime()
    fun resetPauseTime()
}

object DefaultTimer : Timer {
    private lateinit var timer: java.util.Timer
    private var startTime = System.currentTimeMillis()
    private var pauseTime = 0L
    private const val TIMER_PERIOD_MS = 100L
    override fun reset() {
        timer.cancel()
    }

    override fun start(task: TimerTask) {
        timer = Timer()
        timer.scheduleAtFixedRate(task, 0L, TIMER_PERIOD_MS)
    }

    override fun getElapsedTime(): Long = System.currentTimeMillis() - startTime

    override fun updatePauseTime() {
        startTime += System.currentTimeMillis() - pauseTime
    }

    override fun getPausedTime(): Long = pauseTime - startTime

    override fun resetStartTime() {
        startTime = System.currentTimeMillis()
    }

    override fun resetPauseTime() {
        pauseTime = System.currentTimeMillis()
    }

}