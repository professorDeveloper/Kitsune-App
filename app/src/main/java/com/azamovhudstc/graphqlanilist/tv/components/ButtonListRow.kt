package com.azamovhudstc.graphqlanilist.tv.components

import androidx.leanback.widget.Row

class ButtonListRow(val text: String, val delegate: OnClickListener): Row() {
    override fun isRenderedAsRowView(): Boolean = false

    fun click() {
        delegate.onClick()
    }

    interface OnClickListener {
        fun onClick()
    }
}