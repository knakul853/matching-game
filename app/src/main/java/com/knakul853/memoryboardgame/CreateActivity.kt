package com.knakul853.memoryboardgame

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.knakul853.memoryboardgame.models.BoardSize
import com.knakul853.memoryboardgame.utils.*
import java.io.ByteArrayOutputStream
import java.util.*

class CreateActivity : AppCompatActivity() {

    private lateinit var boardSize: BoardSize
    private lateinit var rvImagePicker: RecyclerView
    private lateinit var etGameName: EditText
    private lateinit var btnSave: Button
    private lateinit var pbUploading:ProgressBar
    private val chosenImageUri = mutableListOf<Uri>()
    private lateinit var adapter:ImagePickerAdapter

    companion object{
        private val PICK_PHOTOS_REQ_CODE = 878
        private val READ_EXTERNAL_PHOTOS_CODE = 989
        private val READ_PHOTOS_PERMISSION = android.Manifest.permission.READ_EXTERNAL_STORAGE
        private val TAG = "CreateActivity"
        private val MIN_GAME_LENGTH = 3
        private val MAX_GAME_LENGTH = 14
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)
        boardSize = intent.getSerializableExtra(EXTRA_BOARD_SIZE) as BoardSize
        rvImagePicker = findViewById(R.id.rvImagePicker)
        etGameName = findViewById(R.id.etGameName)
        btnSave =  findViewById(R.id.btnSave)
        pbUploading = findViewById(R.id.pbUploading)

        etGameName.filters = arrayOf(InputFilter.LengthFilter(MAX_GAME_LENGTH))

        btnSave.setOnClickListener({
        })
        etGameName.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                btnSave.isEnabled = shouldSaveEnabled()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {}

        })

        title = "Choose pics ( 0 / ${boardSize.getNumPairs()})"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        adapter = ImagePickerAdapter(this, chosenImageUri, boardSize, object : ImagePickerAdapter.ImageClickListener{
           override fun onPlaceHolderClicked() {
               //photo choosing flow
               if(isPermissionGranted(this@CreateActivity, READ_PHOTOS_PERMISSION)){
                   launchIntentForPhotos()
               }
               else{
                   requestPermission(this@CreateActivity, READ_PHOTOS_PERMISSION, READ_EXTERNAL_PHOTOS_CODE)
               }
           }

       })
        rvImagePicker.adapter = adapter
        rvImagePicker.setHasFixedSize(true)
        rvImagePicker.layoutManager = GridLayoutManager(this, boardSize.getWidth())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == READ_EXTERNAL_PHOTOS_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                launchIntentForPhotos()
            }
            else{
                Toast.makeText(this, "You must give permission to create your photos permission", Toast.LENGTH_SHORT).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode != PICK_PHOTOS_REQ_CODE || resultCode != Activity.RESULT_OK || data == null){
            Log.w(TAG, "didn't get the data from launch activity, user likely to cancel flow")
            return
        }

        val selectedUri = data.data //one data
        val clipData = data.clipData  // multiple data

        if(clipData != null){
            Log.i(TAG, "clip data num of images${clipData.itemCount}")
            for(i in 0 until clipData.itemCount){
                val clipItem = clipData.getItemAt(i)
                if(chosenImageUri.size < boardSize.getNumPairs()){
                    chosenImageUri.add(clipItem.uri)
                }
            }
        }else if(selectedUri != null){
            chosenImageUri.add(selectedUri)
        }
        adapter.notifyDataSetChanged()
        supportActionBar?.title = "choose image ( ${chosenImageUri.size} / ${boardSize.getNumPairs()})"
        btnSave.isEnabled = shouldSaveEnabled()

    }




    private fun getImageByteArray(photoUri: Uri): ByteArray {

        val originalBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            val source = ImageDecoder.createSource(contentResolver, photoUri)
            ImageDecoder.decodeBitmap(source)
        }
        else{
            MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
        }

        Log.i(TAG, "original width ${originalBitmap.width} and height ${originalBitmap.height}")

        val scaleBitmap = BitmapScaler.scaleToFitHeight(originalBitmap, 250)
        Log.i(TAG, "original width ${scaleBitmap.width} and height ${scaleBitmap.height}")

        val byteOutPutStream = ByteArrayOutputStream()
        scaleBitmap.compress(Bitmap.CompressFormat.JPEG, 65, byteOutPutStream)
        return byteOutPutStream.toByteArray()

    }

    private fun shouldSaveEnabled(): Boolean {

        if(chosenImageUri.size != boardSize.getNumPairs()
            || etGameName.text.isBlank() || etGameName.text.length < MIN_GAME_LENGTH){
            return false
        }
        return true
    }

    private fun launchIntentForPhotos() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(Intent.createChooser(intent, "Choose pics"), PICK_PHOTOS_REQ_CODE)
    }
}
