package com.example.my_note.presenter

import com.example.my_note.model.Dao
import com.example.my_note.model.NoteData
import com.example.my_note.view.MainScreenContract

class MainPresenter(
    private val noteDao: Dao
) : MainScreenContract.Presenter {

    private var mainView: MainScreenContract.View? = null
    override fun onAttach(view: MainScreenContract.View) {
        mainView = view
        mainView!!.showNotes(noteDao.getAllNote())
    }

    override fun onRefresh() {
        mainView!!.refreshNotes(noteDao.getAllNote())
    }


    override fun onDetach() {
        mainView = null
    }

    override fun onSearchNote(filter: String) {

        if(filter.isNotEmpty()){
            //show filtered data
            val dataToShow = noteDao.searchNote(filter)
            mainView!!.refreshNotes(dataToShow)

        }else{
            // show all data
            val dataToShow = noteDao.getAllNote()
            mainView!!.refreshNotes(dataToShow)
        }

    }

    override fun onAddNewNoteClicked(note: NoteData) {
        noteDao.insertNote(note)
        mainView!!.addNewNote(note)
    }

    override fun onUpdateNote(note: NoteData, pos: Int) {

        noteDao.updateNote(note)
        mainView!!.updateNote(note, pos)

    }

    override fun onDeleteNote(note: NoteData, pos: Int) {

        noteDao.deleteNote(note)
        mainView!!.deleteNote(note, pos )

    }

}