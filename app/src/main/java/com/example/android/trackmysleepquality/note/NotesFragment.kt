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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.NoteDatabase
import com.example.android.trackmysleepquality.databinding.FragmentNotesBinding
import com.example.android.trackmysleepquality.sleepquality.SleepQualityFragmentArgs
import com.example.android.trackmysleepquality.sleeptracker.SleepTrackerFragmentDirections


class NotesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentNotesBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_notes, container, false)

        val application = requireNotNull(this.activity).application

        val dataSource = NoteDatabase.getInstance(application).noteDatabaseDao

        var noteKey: Long? = null
        if (arguments != null) {
            noteKey = NotesFragmentArgs.fromBundle(arguments!!).noteKey
        }

        // Create an instance of the ViewModel Factory.
        val viewModelFactory = NotesViewModelFactory(noteKey, requireNotNull(activity), dataSource)

        // Get a reference to the ViewModel associated with this fragment.
        val notesViewModel =
                ViewModelProvider(
                        this, viewModelFactory).get(NotesViewModel::class.java)

        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.notesViewModel = notesViewModel

        // binding.setLifecycleOwner(this)
        binding.lifecycleOwner = this

        val manager = LinearLayoutManager(activity)

        binding.notesList.layoutManager = manager

        val adapter = NotesAdapter(NotesListener(notesViewModel::onNoteClicked))
        binding.notesList.adapter = adapter

        notesViewModel.dbNotes.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.addHeaderAndSubmitList(it)
            }
        })

        notesViewModel.navigateToNote.observe(viewLifecycleOwner, Observer { id ->
            id?.let {

                this.findNavController().navigate(
                    NotesFragmentDirections
                        .actionNotesFragmentToNotesFragment(id))
                notesViewModel.onNoteNavigated()
            }
        })

        return binding.root
    }
}
