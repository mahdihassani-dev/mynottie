package com.example.my_note.view

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.my_note.databinding.ActivityMainBinding
import com.example.my_note.model.Dao
import com.example.my_note.model.NoteData
import com.example.my_note.model.NoteDataBase
import com.example.my_note.presenter.MainPresenter
import com.example.my_note.util.DETAIL_KEY
import com.example.my_note.util.TITLE_KEY
import com.example.my_note.util.idForUpdate
import com.example.my_note.util.positionForUpdate
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


private const val TAG = "MyMainActivity"

class MainActivity : AppCompatActivity(), MainScreenContract.View {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainAdapter: NoteAdapter
    private lateinit var noteDao: Dao
    private lateinit var presenter : MainScreenContract.Presenter
    private var isKeyboardOpen = false

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                fillDataAfterAddingNote(result)
                handleVisibility()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noteDao = NoteDataBase.getDatabase(this).noteDao
        presenter = MainPresenter(noteDao)
        presenter.onAttach(this)

        insertEvent()
        searchProcess()
        handleOnBackPressed()
        handleAdapterEvents()

    }

    override fun onResume() {
        super.onResume()
        presenter.onRefresh()

    }
    private fun handleOnBackPressed() {
        onBackPressedDispatcher.addCallback(this /* lifecycle owner */, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                if(isKeyboardOpen){


                    binding.searchBox.visibility = View.INVISIBLE

                    binding.textViewMain.visibility = View.VISIBLE
                    binding.searchBtn.visibility = View.VISIBLE
                    binding.infoBtn.visibility = View.VISIBLE

                    isKeyboardOpen = false

                }else{

                    finish()

                }

            }
        })
    }
    private fun insertEvent() {
        binding.addNoteBtn.setOnClickListener {
            openEditorActivityForInsertResult()
        }

    }
    private fun openEditorActivityForInsertResult() {

        val intent = Intent(this, EditorActivity::class.java)
        resultLauncher.launch(intent)

    }
    private fun handleAdapterEvents() {

        mainAdapter.onItemClick = { it: NoteData, i: Int ->

            val intent = Intent(this, NoteViewActivity::class.java)
            intent.putExtra(TITLE_KEY, it.title)
            intent.putExtra(DETAIL_KEY, it.details)
            intent.putExtra(positionForUpdate, i)
            intent.putExtra(idForUpdate, it.id)

            Log.i(TAG, i.toString())

            startActivity(intent)

        }

        mainAdapter.onLongItemClick = { noteData: NoteData, i: Int ->

            deleteEvent(noteData, i)
        }

    }
    private fun fillDataAfterAddingNote(result: ActivityResult) {

        val data: Intent? = result.data

        val title = data?.getStringExtra(TitleContent).toString()
        val details = data?.getStringExtra(DetailContent).toString()

        val c: Date = Calendar.getInstance().time

        val df = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        val dateFormat: String = df.format(c)

        val tf = SimpleDateFormat(" HH:mm", Locale.getDefault())
        val timeFormat = tf.format(c)

        val newItem = NoteData(
            title = title,
            details = details,
            date = dateFormat,
            time = timeFormat,
        )

        presenter.onAddNewNoteClicked(newItem)
        binding.noteRecyclerview.smoothScrollToPosition(0)

    }
    private fun deleteEvent(it: NoteData, position: Int) {

        val mAlert = AlertDialog.Builder(this)
            .setTitle("Delete")
            .setMessage("do you want to delete the note")
            .setPositiveButton(
                "yes"
            ) { _, _ ->


                presenter.onDeleteNote(it, position)
                handleVisibility()

            }
            .setNegativeButton("no", null)
            .create()

        mAlert.show()


    }

    private fun searchProcess() {

        binding.searchBtn.setOnClickListener {

            binding.textViewMain.visibility = View.INVISIBLE
            binding.searchBtn.visibility = View.INVISIBLE
            binding.infoBtn.visibility = View.INVISIBLE

            binding.searchBox.visibility = View.VISIBLE
            binding.searchBox.requestFocus()
            showKeyboard(binding.searchBox.editText as TextInputEditText)

            isKeyboardOpen = true

        }

        binding.searchBox.setEndIconOnClickListener {

            binding.searchBox.visibility = View.INVISIBLE
            binding.searchBox.editText!!.text.clear()
            hideKeyboard(binding.searchBox)


            binding.textViewMain.visibility = View.VISIBLE
            binding.searchBtn.visibility = View.VISIBLE
            binding.infoBtn.visibility = View.VISIBLE

        }

        binding.searchFeild.addTextChangedListener {

            searchOnDatabase(it.toString())

        }

    }
    private fun searchOnDatabase(it: String) {
        presenter.onSearchNote(it)
    }
    private fun handleVisibility() {

        if (noteDao.getAllNote().isEmpty()) {

            binding.imageView.visibility = View.VISIBLE
            binding.textViewcenter.visibility = View.VISIBLE
            binding.noteRecyclerview.visibility = View.INVISIBLE

        } else {

            binding.imageView.visibility = View.INVISIBLE
            binding.textViewcenter.visibility = View.INVISIBLE
            binding.noteRecyclerview.visibility = View.VISIBLE

        }


    }
    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
    private fun showKeyboard(mEtSearch: TextInputEditText) {
        mEtSearch.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(mEtSearch, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun showNotes(data: List<NoteData>) {

        mainAdapter = NoteAdapter(ArrayList(data.reversed()))
        binding.noteRecyclerview.adapter = mainAdapter
        binding.noteRecyclerview.layoutManager = LinearLayoutManager(this)
        handleVisibility()
    }

    override fun refreshNotes(data: List<NoteData>) {
        mainAdapter.setData(ArrayList(data))
    }

    override fun addNewNote(newNote: NoteData) {
        mainAdapter.addNewItem(newNote)
    }

    override fun deleteNote(oldNote: NoteData, pos: Int) {
        mainAdapter.deleteItem(oldNote, pos)

    }

    override fun updateNote(editedNote: NoteData, pos: Int) {

    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDetach()
    }

}
