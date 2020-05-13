package com.qihoo.samples.twowaysample.ui

import android.widget.EditText
import androidx.databinding.BindingAdapter

object BindingAdapters {
    @BindingAdapter(value = ["loseFocusWhen"])
    @JvmStatic
    fun loseFocusWhen(view: EditText, condition: Boolean) {
        if (condition) view.clearFocus()
    }

}