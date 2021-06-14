package com.example.android.keeptrack.note

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.keeptrack.database.NoteDatabaseDao

class NotesViewModelFactory(
    private val noteKey: Long?,
    private val context: Context,
    private val database: NoteDatabaseDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            return NotesViewModel(noteKey, context, database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

 