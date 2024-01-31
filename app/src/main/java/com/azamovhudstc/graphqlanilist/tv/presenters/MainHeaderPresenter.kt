package com.azamovhudstc.graphqlanilist.tv.presenters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.RowHeaderPresenter
import com.azamovhudstc.graphqlanilist.databinding.TvRowHeaderBinding

class MainHeaderPresenter: RowHeaderPresenter() {

    override fun onCreateViewHolder(parent: ViewGroup?): Presenter.ViewHolder {
        val viewHolder = HeaderRowViewholder(TvRowHeaderBinding.inflate(LayoutInflater.from(parent?.context), parent, false))
        setSelectLevel(viewHolder, 0f)
        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder?, item: Any?) {
        (viewHolder as HeaderRowViewholder)?.let { holder ->
            (item as ListRow)?.let { item ->
                holder.binding.title.text = item.headerItem.name
            }
        }
    }

    inner class HeaderRowViewholder(val binding: TvRowHeaderBinding) : RowHeaderPresenter.ViewHolder(binding.root) {}
}