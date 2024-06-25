@file:Suppress("unused")

package com.akshay.harsoda.android.base.helper.base

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T>(internal var mList: MutableList<T>) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return onCreateHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        if (position >= 0) {
            onBindHolder(holder, position)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    abstract fun onCreateHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*>

    abstract fun onBindHolder(holder: BaseViewHolder<*>, position: Int)

    @SuppressLint("NotifyDataSetChanged")
    open fun addAllItems(mLists: Collection<T>, isReplaceAllItem: Boolean = true) {
        if (isReplaceAllItem) {
            removeAll()
        }
        mList.addAll(mLists)
        notifyDataSetChanged()
    }

    open fun addItem(mLists: T) {
        mList.add(mLists)
        notifyItemInserted(mList.size)
        notifyItemRangeChanged(0, itemCount)
    }

    open fun addItem(mLists: T, position: Int) {
        mList.add(position, mLists)
        notifyItemInserted(position)
        notifyItemRangeChanged(position, itemCount)
    }

    open fun replaceItem(mLists: T, position: Int) {
        mList.removeAt(position)
        mList.add(position, mLists)
        notifyItemChanged(position)
        notifyItemRangeChanged(position, itemCount)
    }

    open fun updateItem(fItem: T, position: Int) {
        removeItem(position)
        addItem(fItem, position)
    }

    open fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    @SuppressLint("NotifyDataSetChanged")
    open fun removeAll() {
        mList.removeAll(mList)
        mList.clear()
        notifyDataSetChanged()
    }

    open fun getItem(position: Int): T {
        return mList[position]
    }

    @SuppressLint("NotifyDataSetChanged")
    open fun clearList() {
        mList.clear()
        notifyDataSetChanged()
    }
}