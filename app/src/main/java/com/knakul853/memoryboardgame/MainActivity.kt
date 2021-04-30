package com.knakul853.memoryboardgame



import android.animation.ArgbEvaluator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.knakul853.memoryboardgame.models.BoardSize
import com.knakul853.memoryboardgame.models.MemoryGame
import com.knakul853.memoryboardgame.models.WinnerState
import com.knakul853.memoryboardgame.utils.*
import com.github.jinatonic.confetti.CommonConfetti
import com.google.android.material.snackbar.Snackbar

import com.google.gson.Gson
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: MemoryBoardAdapter
    private lateinit var memoryGame: MemoryGame
    private lateinit var rvBoard: RecyclerView
    private lateinit var tvNumMoves: TextView
    private lateinit var tvNumPairs: TextView
    private lateinit var clRoot: CoordinatorLayout
    private var gameName: String?=null
    private var customGameImages: List<String>? = null
    private var gameNumber:Int = -1
    var mMediaPlayer: MediaPlayer? = null
    lateinit var preferences: SharedPreferences
    var alt = 0;


    companion object {
       private const val CREATE_REQUEST_CODE = 853
        private const val TAG = "MainActivity"
        private const val PREFERENCES_FILE_NAME = "PREFERENCES_FILE_NAME"

    }

    private var boardSize = BoardSize.EASY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvBoard  = findViewById(R.id.rvBoard)
        tvNumMoves = findViewById(R.id.tvNumMoves)
        tvNumPairs = findViewById(R.id.tvNumPairs)
        clRoot = findViewById(R.id.clRoot)
        gameNumber = intent.getIntExtra(EXTRA_GAME_NUMBER, -1);
        if(gameNumber != -1){
            Log.i(TAG, gameNumber.toString());
        }

      setUpGame()

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.menuItem_refresh -> {
                if(memoryGame.getNumMoves() > 0 && !memoryGame.isWinner()){
                    showAlertDialogue("Quit your current game?", null, View.OnClickListener {
                        setUpGame()

                    })
                }
                else{
                    setUpGame()
                }

                return true
            }

            R.id.menuItem_boardSize ->{
                showNewSizeDialogue()
                return true
            }
//
//            R.id.menuItem_createCustomGame -> {
//                showCreationDialogue()
//            }
//            R.id.menuItem_DownloadGame -> {
//                showDownloadDialogue()
//                return true
//            }
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == CREATE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val custumGameName = data?.getStringExtra(EXTRA_GAME_NAME)
            if(custumGameName == null){
                Log.e(TAG, "Got null costume game from create activity")
                return
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }



    private fun showNewSizeDialogue() {

        val boardSizeView = LayoutInflater.from(this).inflate(R.layout.dialogue_board_size, null)
        val radioGroupSize = boardSizeView.findViewById<RadioGroup>(R.id.rbGroupSize)
        when( boardSize){
            BoardSize.EASY -> radioGroupSize.check(R.id.rbEasy)
            BoardSize.MEDIUM -> radioGroupSize.check(R.id.rbMedium)
            BoardSize.HARD -> radioGroupSize.check(R.id.rbHard)
        }

        showAlertDialogue("Choose new size", boardSizeView, View.OnClickListener {

            boardSize = when (radioGroupSize.checkedRadioButtonId){
                R.id.rbEasy -> BoardSize.EASY
                R.id.rbMedium -> BoardSize.MEDIUM
                else -> BoardSize.HARD
            }
            gameName = null
            customGameImages = null
            setUpGame()
        })
    }

    private fun showAlertDialogue(title: String, view: View?,positiveClickListner:View.OnClickListener ) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Ok"){ _,_ ->
                positiveClickListner.onClick(null)
            }.show()

    }

    private fun setUpGame() {

        if(gameNumber==-1){
            return;
        }

        supportActionBar?.title = findTitle()
        when (boardSize){
            BoardSize.EASY -> {
                tvNumMoves.text = "EASY (4x2)"
                tvNumPairs.text = "Pairs: 0 / 4"
            }
            BoardSize.MEDIUM -> {
                tvNumMoves.text = "MEDIUM (6x3)"
                tvNumPairs.text = "Pairs: 0 / 9"

            }
            BoardSize.HARD -> {
                tvNumMoves.text = "HARD (6x4)"
                tvNumPairs.text = "Pairs: 0 / 12"

            }
        }
        tvNumPairs.setTextColor(ContextCompat.getColor(this,R.color.color_progress_none))
        memoryGame = MemoryGame(boardSize, customGameImages, gameArray.get(gameNumber))

        //set adapter for the recyclerview.
        adapter = MemoryBoardAdapter(this, boardSize, memoryGame.cards, object : MemoryBoardAdapter.CardClickListner{
            override fun onCardClicked(position: Int) {
                updateGameWithFlip(position)
            }

        })
        rvBoard.setHasFixedSize(true)
        rvBoard.adapter = adapter

        //set the layout manager
        rvBoard.layoutManager = GridLayoutManager(this, boardSize.getWidth())
    }

    private fun findTitle(): String? {
        val name = PREFERENCES_FILE_NAME + boardSize.getNumPairs().toString() + gameNumber.toString()
        preferences = application.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        val gson =  Gson();
        var json = preferences.getString(name, "");
        val obj = gson.fromJson(json, WinnerState::class.java)
        var title = "Memory Game"
        if(obj == null){
            title = "Best Score(${boardSize}): Nil"
        }else{
            title = "Best Score(${boardSize}): ${obj.numberOfMoves}"

        }
        return title
    }

    private fun updateGameWithFlip(position: Int) {
        //Error checking
        if(memoryGame.isWinner()){
            Snackbar.make(clRoot, "you won!  Congratulations.",Snackbar.LENGTH_LONG).show()
            return
        }
        if(memoryGame.isFaceUp(position)){
            Snackbar.make(clRoot, "Invalid move!",Snackbar.LENGTH_SHORT).show()
            playSound(MediaPlayer.create(this, R.raw.not_allowed))

            return
        }

        playSound(MediaPlayer.create(this, R.raw.turn_card))

        //Actual flip method
        if(memoryGame.flipCard(position)){
            Log.i(TAG, "You found a match: ${memoryGame.numPairsFound}")
            val color = ArgbEvaluator().evaluate(
                memoryGame.numPairsFound.toFloat() / boardSize.getNumPairs(),
                ContextCompat.getColor(this, R.color.color_progress_none),
                ContextCompat.getColor(this, R.color.color_progress_full),

                )as Int
            tvNumPairs.setTextColor(color)
            tvNumPairs.setText("Pairs: ${memoryGame.numPairsFound} / ${boardSize.getNumPairs()}")
            if(memoryGame.isWinner()) {
                Snackbar.make(clRoot, "you won!  Congratulations.", Snackbar.LENGTH_LONG).show()
                CommonConfetti.rainingConfetti(clRoot, intArrayOf(Color.YELLOW, Color.GREEN, Color.BLUE, Color.LTGRAY)).oneShot()
                playSound(MediaPlayer.create(this, R.raw.winning_sound))
                saveWinningState()

            }
            else if(alt==0){
                playSound(MediaPlayer.create(this, R.raw.match_sound))

            }

            alt = 1 - alt

        }

        tvNumMoves.setText("Moves: ${memoryGame.getNumMoves()}")
        adapter.notifyDataSetChanged()
    }

    private fun saveWinningState() {
        if(gameNumber== -1 )return

        val name = PREFERENCES_FILE_NAME + boardSize.getNumPairs().toString() + gameNumber.toString()
       preferences = application.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        val gson =  Gson();
        var json = preferences.getString(name, "");
        val obj = gson.fromJson(json, WinnerState::class.java)

        val winingObj = WinnerState(boardSize)
        winingObj.numberOfMoves = memoryGame.getNumMoves()
        winingObj.level = gameNumber
        json = gson.toJson(winingObj)

        if(obj == null){
            preferences.edit().putString(name, json).apply()

        }
        else if(obj.numberOfMoves > memoryGame.getNumMoves()){
            preferences.edit().putString(name, json).apply()
        }

        supportActionBar?.title = findTitle()

        checkUnlockedGame()

    }

    private fun checkUnlockedGame() {
        val name = LandingActivity.PREFERENCES_FILE_NAME +"12";
        val gson =  Gson();

        for(i in 0..(LandingActivity.gameData?.size ?: -1) -1){
            preferences = application.getSharedPreferences(LandingActivity.PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
            var json = preferences.getString(name+(i.toString()), "");
            val obj = gson.fromJson(json, WinnerState::class.java)

            if(obj == null)return;

            if(obj.numberOfMoves != 10000000){
                if(i+1< LandingActivity.gameData?.size ?: -1) {
                    LandingActivity.gameData?.get(i+1)?.isPrevGameOver = true
                }
            }
            LandingActivity.landingAdapter?.notifyDataSetChanged()
        }
        LandingActivity.landingAdapter?.notifyDataSetChanged()
    }

    // 1. Plays the water sound
    fun playSound(player: MediaPlayer) {
        mMediaPlayer = player
        if (mMediaPlayer == null) {
            mMediaPlayer!!.isLooping = false
            mMediaPlayer!!.start()
        } else mMediaPlayer!!.start()
    }

    // 2. Pause playback
    fun pauseSound(view: View) {
        if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) mMediaPlayer!!.pause()
    }

    // 3. {optional} Stops playback
    fun stopSound(view: View) {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }

    // 4. Closes the MediaPlayer when the app is closed
    override fun onStop() {
        super.onStop()
        if (mMediaPlayer != null) {
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }


}

