package com.azamovhudstc.graphqlanilist.ui.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.azamovhudstc.graphqlanilist.R

class CustomAdapter(
    context: Context,
    private val items: Array<String>
) : ArrayAdapter<String>(context, R.layout.alert_item, items) {

    private var selectedPosition = -1 // Initially no item selected

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)

        // Change the text color of the selected item
        if (position == selectedPosition) {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.fav))
        } else {
            view.setBackgroundColor(Color.TRANSPARENT)
        }

        return view
    }

    fun setSelected(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }

    fun getSelectedPosition(): Int {
        return selectedPosition
    }
}
