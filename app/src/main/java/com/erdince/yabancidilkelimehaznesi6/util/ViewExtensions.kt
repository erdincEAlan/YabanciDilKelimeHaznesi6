package com.erdince.yabancidilkelimehaznesi6.util

import android.view.View
import android.view.animation.DecelerateInterpolator


    fun View.setVisibilityWithAnimation(isVisible : Boolean){
        if (isVisible){
            this.apply {
                alpha = 0f
                visibility = View.VISIBLE
                animate()
                    .alpha(1f)
                    .setDuration(2500)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            }
        }else{
            this.apply {
                alpha = 1f
                visibility = View.GONE
                animate()
                    .alpha(0f)
                    .setDuration(2500)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            }
        }


}