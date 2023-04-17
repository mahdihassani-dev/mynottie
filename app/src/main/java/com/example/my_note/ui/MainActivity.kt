package com.example.my_note.ui

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.my_note.Adapters.NoteAdapter
import com.example.my_note.databinding.ActivityMainBinding
import com.example.my_note.room.Dao
import com.example.my_note.room.NoteData
import com.example.my_note.room.NoteDataBase
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


private const val TAG = "MyMainActivity"

const val DETAIL_KEY = "detailView"
const val TITLE_KEY = "titleView"
const val positionForUpdate = "ThePosition"
const val idForUpdate = "TheId"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var adapter: NoteAdapter
    lateinit var noteDao: Dao
    private var isKeyboardOpen = false

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                handleVisibility()
                fillDataAfterAddingNote(result)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noteDao = NoteDataBase.getDatabase(this).noteDao

        insertEvent()
        searchProcess()
        handleOnBackPressed()



    }

    override fun onResume() {
        super.onResume()
        setupDatabase()
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

    private fun setupDatabase() {

        handleVisibility()

        val noteData = noteDao.getAllNote()

        binding.noteRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.noteRecyclerview.isNestedScrollingEnabled = false
        adapter = NoteAdapter(ArrayList(noteData.reversed()))
        binding.noteRecyclerview.adapter = adapter

        handleAdapterEvents()

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

        adapter.onItemClick = { it: NoteData, i: Int ->

            val intent = Intent(this, NoteViewActivity::class.java)
            intent.putExtra(TITLE_KEY, it.title)
            intent.putExtra(DETAIL_KEY, it.details)
            intent.putExtra(positionForUpdate, i)
            intent.putExtra(idForUpdate, it.id)

            Log.i(TAG, i.toString())

            startActivity(intent)

        }

        adapter.onLongItemClick = { noteData: NoteData, i: Int ->

            deleteNote(noteData, i)
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

        addNewNote(newItem)

    }

    private fun addNewNote(noteData: NoteData) {

        adapter.addNewItem(noteData)
        binding.noteRecyclerview.smoothScrollToPosition(0)
        noteDao.insertNote(noteData)
        setupDatabase()
    }

    private fun deleteNote(it: NoteData, position: Int) {

        val mAlert = AlertDialog.Builder(this)
            .setTitle("Delete")
            .setMessage("do you want to delete the note")
            .setPositiveButton(
                "yes"
            ) { dialog, which ->

                Log.i(TAG, it.id.toString())
                Log.i(TAG, it.details)

                adapter.deleteItem(it, position)
                noteDao.deleteNote(it)
                handleVisibility()

                setupDatabase()

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


        if(it.isNotEmpty()){

            val filteredNotes = noteDao.searchNote(it)
            adapter.setData( ArrayList(filteredNotes) )

        }else{

            val allData = noteDao.getAllNote()
            adapter.setData( ArrayList(allData) )

        }


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

}
