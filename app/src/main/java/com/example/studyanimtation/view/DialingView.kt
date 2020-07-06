package com.example.studyanimtation.view

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.CycleInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView

class DialingView : ImageView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {


    }

    fun startDialing() {
        val animator: ObjectAnimator = ObjectAnimator.ofFloat(
            this, "scaleY", 1f,0.2f,1f
        )
        animator.interpolator = CycleInterpolator(0.5f)
        animator.duration = 2000
        animator.repeatCount = ObjectAnimator.INFINITE
        animator.start()
    }
}