package com.example.android.keeptrack.note

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.android.keeptrack.database.Note

@BindingAdapter("noteText")
fun TextView.setNoteText(item: Note?) {
    item?.let {
        text = it.text
    }
}
