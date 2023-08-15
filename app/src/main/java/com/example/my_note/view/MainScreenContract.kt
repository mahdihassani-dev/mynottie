package com.example.my_note.view

import com.example.my_note.model.NoteData

interface MainScreenContract {


    interface Presenter {

        fun onAttach(view: MainScreenContract.View)
        fun onDetach()
        fun onSearchNote(filter: String)
        fun onAddNewNoteClicked(note: NoteData)
        fun onNoteClicked(note: NoteData)
        fun onNoteLongClicked(note: NoteData)

    }

    interface View {

        fun showNotes(date: ArrayList<NoteData>)
        fun addNewNote(newNote: NoteData)
        fun deleteNote(oldNote: NoteData)
        fun editNote(editedNote: NoteData)

    }

}