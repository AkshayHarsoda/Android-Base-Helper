@file:Suppress("unused")

package com.akshay.harsoda.android.base.helper.base

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseViewHolder<VB: ViewBinding>(fBinding: VB): RecyclerView.ViewHolder(fBinding.root)