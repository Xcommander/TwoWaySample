package com.qihoo.samples.twowaysample.util

import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.ViewModel

/**
 * @author JasonXu
 * @date   2020/4/25
 */
open class ObservableViewModel : ViewModel(), androidx.databinding.Observable {
    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    override fun removeOnPropertyChangedCallback(callback: androidx.databinding.Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }

    override fun addOnPropertyChangedCallback(callback: androidx.databinding.Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }

    /**
     * 通知所有属性变化，则不需要添加@Bindable注解
     */
    fun notifyChange() {
        callbacks.notifyCallbacks(this, 0, null)
    }

    /**
     * 单个属性的通知，需要在get()方法添加@Bindable注解
     */
    fun notifyPropertyChanged(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }

}