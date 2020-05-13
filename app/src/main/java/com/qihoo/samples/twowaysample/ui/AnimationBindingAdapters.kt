package com.qihoo.samples.twowaysample.ui

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.qihoo.samples.twowaysample.R

object AnimationBindingAdapters {
    private const val BG_COLOR_ANIMATION_DURATION = 500L

    @BindingAdapter(value = ["animateBackground", "animateBackgroundStage"], requireAll = true)
    @JvmStatic
    fun animateBackground(view: View, timerRunning: Boolean, firstWorking: Boolean) {
        if (!timerRunning) {
            view.setBackgroundColor(
                ContextCompat.getColor(
                    view.context,
                    R.color.disabledInputColor
                )
            )
            view.setTag(R.id.hasBeenAnimated, false)
            return
        }
        if (firstWorking) {
            //第一阶段
            animateBgColor(view, true)
            view.setTag(R.id.hasBeenAnimated, true)
        } else {
            //第二阶段
            val hasItBeenAnimated = view.getTag(R.id.hasBeenAnimated) as Boolean?
            if (hasItBeenAnimated == true) {
                animateBgColor(view, false)
                view.setTag(R.id.hasBeenAnimated, false)
            }
        }

    }

    //区域背景动画
    private fun animateBgColor(view: View, tint: Boolean) {
        val colorRes = ContextCompat.getColor(view.context, R.color.colorPrimaryLight)
        val colorRes2 = ContextCompat.getColor(view.context, R.color.disabledInputColor)
        val animator = if (tint) {
            ObjectAnimator.ofObject(view, "backgroundColor", ArgbEvaluator(), colorRes2, colorRes)
        } else {
            ObjectAnimator.ofObject(view, "backgroundColor", ArgbEvaluator(), colorRes, colorRes2)
        }
        animator.duration = BG_COLOR_ANIMATION_DURATION
        animator.start()
    }
}