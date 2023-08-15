package com.example.my_note.view

import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.example.my_note.databinding.NoteItemBinding
import com.example.my_note.model.NoteData
import java.util.*

class NoteAdapter(private val mList: ArrayList<NoteData>) :
    RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    var onItemClick: ((NoteData, Int) -> Unit)? = null
    var onLongItemClick: ((NoteData, Int) -> Unit)? = null

    inner class ViewHolder(private val binding: NoteItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(position: Int){

            val noteItem = mList[position]

            binding.txtTitleRecycle.text = noteItem.title
            binding.txtTime.text = noteItem.time
            binding.txtDate.text = noteItem.date
            binding.txtDetailRecycle.text = noteItem.details

            itemView.setOnClickListener {
                onItemClick?.invoke(noteItem, position)
            }

            itemView.setOnLongClickListener {
                onLongItemClick?.invoke(noteItem, position)

                true

            }


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bindData(position)
    }

    fun addNewItem(newItem: NoteData) {

        mList.add(newItem)
        notifyItemInserted(0)

    }

    fun deleteItem(noteData: NoteData, position: Int){

        mList.remove(noteData)
        notifyItemRemoved(position)

    }


    fun setData(newList: ArrayList<NoteData>) {

        // set new data to list :
        mList.clear()
        mList.addAll(newList.reversed())

        notifyDataSetChanged()

    }


}