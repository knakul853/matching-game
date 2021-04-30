package com.knakul853.memoryboardgame

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.knakul853.memoryboardgame.models.ChooseGame
import com.knakul853.memoryboardgame.models.WinnerState
import com.knakul853.memoryboardgame.utils.EXTRA_GAME_NUMBER
import com.knakul853.memoryboardgame.utils.initData
import com.google.gson.Gson
import com.knakul853.memoryboardgame.utils.BitmapScaler


open class  LandingActivity : AppCompatActivity() {
    private lateinit var rvChooseGame: RecyclerView
    private lateinit var imageView:ImageView
    private lateinit var cardview:CardView
    private var mRewardedAd: RewardedAd? = null
    lateinit var mAdView : AdView


    var mMediaPlayer: MediaPlayer? = null
    lateinit var preferences: SharedPreferences


    companion object{
        var TAG = "LandingActivity"
        public var PREFERENCES_FILE_NAME = "PREFERENCES_FILE_NAME"
        public var gameData:MutableList<ChooseGame>?=null
        public var landingAdapter: ChooseGameAdapter? = null

    }
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)
        supportActionBar?.hide()
         MobileAds.initialize(this)

         rvChooseGame = findViewById(R.id.rvChooseGame)
        cardview = findViewById(R.id.cardView2)

         //add
         val adView = AdView(this)

         adView.adSize = AdSize.BANNER

         adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"
         mAdView = findViewById(R.id.adView)
         val adRequest = AdRequest.Builder().build()
         mAdView.loadAd(adRequest)
        animateHeader()

         gameData = initData(this)
         landingAdapter = ChooseGameAdapter(this, gameData, object :ChooseGameAdapter.CardClickListner{
             override fun onCardClicked(position: Int) {
                 //play this game
                 ///displayRewardAds()
                 BitmapScaler.displayRewardAds(this@LandingActivity)
                 playGame(position)

             }

         });


        rvChooseGame.layoutManager = GridLayoutManager(this, 2)

        rvChooseGame.adapter = landingAdapter

        checkUnlockedGame()

    }


    fun checkUnlockedGame() {

         val name = PREFERENCES_FILE_NAME +"12";
         val gson =  Gson();

         for(i in 0..(gameData?.size ?: -1) -1){
             preferences = application.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
             var json = preferences.getString(name+(i.toString()), "");
             val obj = gson.fromJson(json, WinnerState::class.java)

             if(obj == null)return;

             if(obj.numberOfMoves != 10000000){
                 if(i+1< gameData?.size ?: -1) {
                     gameData?.get(i+1)?.isPrevGameOver = true
                 }
             }
             landingAdapter?.notifyDataSetChanged()
         }
     }

     private fun playGame(position: Int) {
         playSound(MediaPlayer.create(this, R.raw.choose_game))
            val gameIntent = Intent(this, MainActivity::class.java)
            gameIntent.putExtra(EXTRA_GAME_NUMBER, position)
            startActivity(gameIntent)
    }

    fun animateHeader(){
        val rotate = ObjectAnimator.ofFloat(cardview, "rotation",  0f, 5f, 0f, -5f, 0f)
        rotate.repeatCount = 1000000000
        rotate.setDuration(1000);
        rotate.start()

    }

    // 1. Plays the water sound
    fun playSound(player: MediaPlayer) {
        mMediaPlayer = player
        if (mMediaPlayer == null) {
            mMediaPlayer!!.isLooping = false
            mMediaPlayer!!.start()
        } else mMediaPlayer!!.start()
    }

}