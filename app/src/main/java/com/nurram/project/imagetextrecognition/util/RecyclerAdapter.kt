package com.nurram.project.imagetextrecognition.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nurram.project.imagetextrecognition.databinding.RowItemBinding
import com.nurram.project.imagetextrecognition.util.RecyclerAdapter.RecyclerHolder
import java.util.*

class RecyclerAdapter(private val mBlocks: ArrayList<String>,
    val onClick: (String) -> Unit) : RecyclerView.Adapter<RecyclerHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowItemBinding.inflate(inflater, parent, false)
        return RecyclerHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) = holder.bind(mBlocks[position])

    override fun getItemCount(): Int = mBlocks.size

    inner class RecyclerHolder(private val binding: RowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(text: String) {
            binding.apply { textLines.append(text) }
            itemView.setOnClickListener { onClick(text) }
        }
    }
}