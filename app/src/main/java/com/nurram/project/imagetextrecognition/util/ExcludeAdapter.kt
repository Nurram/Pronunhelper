package com.nurram.project.imagetextrecognition.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nurram.project.imagetextrecognition.databinding.ItemListBinding
import com.nurram.project.imagetextrecognition.room.Word
import com.nurram.project.imagetextrecognition.util.ExcludeAdapter.ExcludeHolder

class ExcludeAdapter(val onClick: (word: Word) -> Unit): RecyclerView.Adapter<ExcludeHolder>() {
    private var mWords =  arrayListOf<Word>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ExcludeHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding = ItemListBinding.inflate(inflater, viewGroup, false)
        return ExcludeHolder(binding)
    }

    override fun onBindViewHolder(holder: ExcludeHolder, i: Int) = holder.bind(mWords[i])

    override fun getItemCount() = mWords.size

    inner class ExcludeHolder(private val binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(word: Word) {
            binding.apply {
                excludeTv.text = word.word
                excludeDelete.setOnClickListener { onClick(word) }
            }
        }
    }

    fun addData(words: ArrayList<Word>) {
        mWords = words
        notifyDataSetChanged()
    }
}