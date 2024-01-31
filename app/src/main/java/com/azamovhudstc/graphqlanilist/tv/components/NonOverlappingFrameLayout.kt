package com.azamovhudstc.graphqlanilist.tv.components

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout


internal class NonOverlappingFrameLayout : FrameLayout {
    constructor(context: Context) : this(context, null) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0) {}
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
    }

    /**
     * Avoid creating hardware layer when Transition is animating alpha.
     */
    override fun hasOverlappingRendering(): Boolean {
        return false
    }
}