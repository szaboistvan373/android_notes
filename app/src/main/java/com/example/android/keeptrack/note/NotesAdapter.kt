/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

private val ITEM_VIEW_TYPE_HEADER = 0
private val ITEM_VIEW_TYPE_ITEM = 1



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

//    class TextViewHolder(binding: Binding) : RecyclerView.ViewHolder(binding.root) { {
//        companion object {
//            fun from(parent: ViewGroup): TextViewHolder {
//                val layoutInflater = LayoutInflater.from(parent.context)
//                val view = layoutInflater.inflate(R.layout.header, parent, false)
//                return TextViewHolder(view)
//            }
//        }
//    }

        class TextViewHolder private constructor(val binding: HeaderBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(text: String) {
                binding.asdasdasd = text
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

/**
 * Callback for calculating the diff between two non-null items in a list.
 *
 * Used by ListAdapter to calculate the minumum number of changes between and old list and a new
 * list that's been passed to `submitList`.
 */
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