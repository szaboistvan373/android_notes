package com.example.android.keeptrack.note

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
import com.example.android.keeptrack.R
import com.example.android.keeptrack.database.NoteDatabase
import com.example.android.keeptrack.databinding.FragmentNotesBinding


class NotesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentNotesBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_notes, container, false
        )

        val application = requireNotNull(this.activity).application

        val dataSource = NoteDatabase.getInstance(application).noteDatabaseDao

        var noteKey: Long? = null
        if (arguments != null) {
            noteKey = NotesFragmentArgs.fromBundle(arguments!!).noteKey
        }

        val viewModelFactory = NotesViewModelFactory(noteKey, requireNotNull(activity), dataSource)

        val notesViewModel =
            ViewModelProvider(
                this, viewModelFactory
            ).get(NotesViewModel::class.java)

        binding.notesViewModel = notesViewModel

        binding.lifecycleOwner = this

        val manager = LinearLayoutManager(activity)

        binding.notesList.layoutManager = manager

        val adapter = NotesAdapter(NotesListener(notesViewModel::onNoteClicked))
        binding.notesList.adapter = adapter

        notesViewModel.dbNotes.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.addHeaderAndSubmitList(notesViewModel.note?.text, it)
            }
        })

        notesViewModel.navigateToNote.observe(viewLifecycleOwner, Observer { id ->
            id?.let {

                this.findNavController().navigate(
                    NotesFragmentDirections
                        .actionNotesFragmentToNotesFragment(id)
                )
                notesViewModel.onNoteNavigated()
            }
        })

        return binding.root
    }
}
