package com.example.android.keeptrack.note

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    val dbNotes = database.getNotesWithParentId(noteKey)

    var note: Note? = null

    private val _shouldClose = MutableLiveData<Boolean?>()

    val shouldClose: LiveData<Boolean?>
        get() = _shouldClose

    init {
        viewModelScope.launch {
            populateNote()
        }
    }

    private suspend fun populateNote() {
        withContext(Dispatchers.IO) {
            note = if (noteKey != null) database.getNoteWithIdEagerly(noteKey) else null
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
        showDialog(context) { text ->
            viewModelScope.launch {
                add(text)
            }
        }
    }

    private suspend fun add(text: String) {
        withContext(Dispatchers.IO) {
            database.insert(Note(text = text, parentId = noteKey))
        }
    }

    fun onEdit(note: Note) {
        showDialog(context, note.text) { text ->
            viewModelScope.launch {
                edit(note, text)
            }
        }
    }

    private suspend fun edit(note: Note, text: String) {
        withContext(Dispatchers.IO) {
            note.text = text
            database.update(note)
        }
    }

    fun onDelete(id: Long) {
        viewModelScope.launch {
            delete(id)
            _shouldClose.postValue(true)
        }
    }

    private suspend fun delete(id: Long) {
        withContext(Dispatchers.IO) {
            database.delete(id)
        }
    }
}

 