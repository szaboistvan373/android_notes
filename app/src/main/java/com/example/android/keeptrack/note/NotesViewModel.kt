package com.example.android.keeptrack.note

import android.content.Context
import androidx.lifecycle.*
import com.example.android.keeptrack.database.Note
import com.example.android.keeptrack.database.NoteDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotesViewModel(
    private val noteKey: Long?,
    private val context: Context,
    private val database: NoteDatabaseDao
) :
    ViewModel() {

    val dbNotes = database.getNotesWithParentIdLazily(noteKey)

    var note: Note? = null

    init {
        viewModelScope.launch {
            asd()
        }
    }

    private suspend fun asd() {
        withContext(Dispatchers.IO) {
            note = if (noteKey != null) database.getNoteWithId(noteKey) else null
        }
    }

    private val _navigateToNote = MutableLiveData<Long>()
    val navigateToNote
        get() = _navigateToNote

    fun onNoteClicked(id: Long) {
        _navigateToNote.value = id
    }

    fun onNoteNavigated() {
        _navigateToNote.value = null
    }

    fun onAdd() {
        showDialog(context) { a ->
            viewModelScope.launch {
                add(a)
            }
        }
    }

    private suspend fun add(value: String) {
        withContext(Dispatchers.IO) {
            database.insert(Note(text = value, parentId = noteKey))
        }
    }
}

 