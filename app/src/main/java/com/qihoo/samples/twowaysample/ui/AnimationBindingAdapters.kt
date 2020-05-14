package com.qihoo.samples.twowaysample.ui

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.qihoo.samples.twowaysample.R

object AnimationBindingAdapters {
    private const val BG_COLOR_ANIMATION_DURATION = 500L
    private const val VERTICAL_BIAS_ANIMATION_DURATION = 900L

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

    @BindingAdapter(value = ["animateVerticalBias", "animateVerticalBiasState"], requireAll = true)
    @JvmStatic
    fun animateVerticalBias(view: View, timerRunning: Boolean, activeStage: Boolean) {
        when {
            timerRunning && activeStage -> animateVerticalBias(view, 0.6f)
            timerRunning && !activeStage -> animateVerticalBias(view, 0.4f)
            else -> animateVerticalBias(view, 0.5f)
        }

    }

    private fun animateVerticalBias(view: View, position: Float) {
        val layoutParams: ConstraintLayout.LayoutParams =
            view.layoutParams as ConstraintLayout.LayoutParams
        val animator = ValueAnimator.ofFloat(layoutParams.verticalBias, position)
        animator.addUpdateListener { animation ->
            val newParams: ConstraintLayout.LayoutParams =
                view.layoutParams as ConstraintLayout.LayoutParams
            val animatedValue = animation.animatedValue as Float
            newParams.verticalBias = animatedValue
            view.requestLayout()
        }
        animator.interpolator = DecelerateInterpolator()
        animator.duration = VERTICAL_BIAS_ANIMATION_DURATION
        animator.start()
    }
}