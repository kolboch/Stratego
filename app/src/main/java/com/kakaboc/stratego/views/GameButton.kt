package com.kakaboc.stratego.views

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton

/**
 * Created by Karlo on 2018-05-20.
 */

class GameButton(context: Context?, attributesSet: AttributeSet) : ImageButton(context, attributesSet) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredHeight)
    }
}