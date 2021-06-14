package com.example.android.keeptrack.note

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.keeptrack.database.Note
import com.example.android.keeptrack.databinding.HeaderBinding
import com.example.android.keeptrack.databinding.ListItemNoteBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1


class NotesAdapter(val clickListener: NotesListener) : ListAdapter<DataItem,
        RecyclerView.ViewHolder>(NoteDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun addHeaderAndSubmitList(parentText: String?, list: List<Note>?) {
        adapterScope.launch {
            val header = DataItem.Header(parentText)
            val items = when (list) {
                null -> {
                    listOf(header)
                }
                else -> listOf(header) + list.map { DataItem.NoteItem(it) }
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val item = getItem(position) as DataItem.NoteItem
                holder.bind(clickListener, item.note)
            }
            is TextViewHolder -> {
                val nightItem = getItem(position) as DataItem.Header
                holder.bind("${nightItem.parentText ?: "Root"} context")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> TextViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    class TextViewHolder private constructor(val binding: HeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(text: String) {
            binding.headerText = text
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): TextViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = HeaderBinding.inflate(layoutInflater, parent, false)

                return TextViewHolder(binding)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.NoteItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    class ViewHolder private constructor(val binding: ListItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: NotesListener, item: Note) {
            binding.note = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemNoteBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}

class NoteDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

class NotesListener(val clickListener: (noteId: Long) -> Unit) {
    fun onClick(note: Note) = clickListener(note.id)
}

sealed class DataItem {
    data class NoteItem(val note: Note) : DataItem() {
        override val id = note.id
    }

    data class Header(val parentText: String?) : DataItem() {
        override val id = Long.MIN_VALUE
    }

    abstract val id: Long
}