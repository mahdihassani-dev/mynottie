package com.example.my_note.view

import com.example.my_note.model.NoteData

interface MainScreenContract {


    interface Presenter {

        fun onAttach(view: MainScreenContract.View)

        fun onRefresh()
        fun onDetach()
        fun onSearchNote(filter: String)
        fun onAddNewNoteClicked(note: NoteData)
        fun onUpdateNote(note: NoteData, pos: Int)
        fun onDeleteNote(note: NoteData, pos: Int)

    }

    interface View {

        fun showNotes(data: List<NoteData>)
        fun refreshNotes(data: List<NoteData>)
        fun addNewNote(newNote: NoteData)
        fun deleteNote(oldNote: NoteData, pos: Int)
        fun updateNote(editedNote: NoteData, pos: Int)

    }

}