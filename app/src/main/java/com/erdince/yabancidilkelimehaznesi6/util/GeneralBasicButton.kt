package com.erdince.yabancidilkelimehaznesi6.util

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.setPadding

class GeneralBasicButton : AppCompatButton{
    private var isButtonEnabled = true
    @SuppressLint("ClickableViewAccessibility")
    @Override
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        if (isButtonEnabled) {
            when (event?.action) {

                MotionEvent.ACTION_DOWN -> {
                    this.alpha = 0.5f
                    return true
                }

                MotionEvent.ACTION_UP -> {
                    this.alpha = 1f
                    return true
                }
            }
        }
        return false
    }

    fun disableButton() {
        this.isClickable = false
        isButtonEnabled = false
        this.alpha = 0.5f
    }

    fun enableButton() {
        this.isClickable = true
        isButtonEnabled = true
        this.alpha = 1f
    }

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

    }


}