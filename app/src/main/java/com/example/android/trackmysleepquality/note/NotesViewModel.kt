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

package com.example.android.trackmysleepquality.note

import android.content.Context
import androidx.lifecycle.*
import com.example.android.trackmysleepquality.database.Note
import com.example.android.trackmysleepquality.database.NoteDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//class NotesViewModel(private val context: Context, private val database: NoteDatabaseDao): ViewModel() {
//
//    val notes: MutableLiveData<List<Note>> = MutableLiveData<List<Note>>(listOf<Note>(Note(1, "asd"), Note(2, "asdasda")))
//
//    fun onAdd() {
//
//        showDialog(context) { a -> println(a) }
////        database.insert(Note(text = "asd"))
//    }

class NotesViewModel(private val context: Context, private val database: NoteDatabaseDao) :
    ViewModel() {

    val notes: MutableLiveData<List<Note>> =
        MutableLiveData<List<Note>>(listOf<Note>(Note(1, "asd"), Note(2, "asdasda")))

    val dbNotes = database.getAllNotes()

    fun onAdd() {
        showDialog(context) { a ->
            viewModelScope.launch {
                add(a)
            }
        }
    }

    private suspend fun add(value: String) {
        showDialog(context) { a ->
            viewModelScope.launch {
                add(a)
            }
        }
        withContext(Dispatchers.IO) {
            database.insert(Note(text = value, parentId = null))
        }
    }
}

 