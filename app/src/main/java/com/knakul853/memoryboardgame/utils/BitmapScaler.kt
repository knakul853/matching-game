package com.knakul853.memoryboardgame.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.knakul853.memoryboardgame.LandingActivity

object BitmapScaler {

    //scale and maintain aspect ratio of given a desired width
    private var mRewardedAd: RewardedAd? = null


    fun scaleToFitWidth(b: Bitmap, width: Int): Bitmap{
        val factor = width / b.width.toFloat()
        return Bitmap.createScaledBitmap(b, width, (b.height * factor).toInt(), true)
    }

    fun scaleToFitHeight(b: Bitmap, height: Int): Bitmap{
        val factor = height / b.height.toFloat()
        return Bitmap.createScaledBitmap(b, height, (b.width * factor).toInt(), true)
    }

    fun displayRewardAds(context: LandingActivity) {
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(context,"ca-app-pub-3940256099942544/5224354917",
            adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(LandingActivity.TAG, adError?.message)
                mRewardedAd = null
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                Log.d(LandingActivity.TAG, "Ad was loaded.")
                mRewardedAd = rewardedAd
            }
        })

        mRewardedAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                Log.d(LandingActivity.TAG, "Ad failed to show.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is dismissed.
                // Don't set the ad reference to null to avoid showing the ad a second time.
                mRewardedAd = null
            }
        }
        if (mRewardedAd != null) {
            mRewardedAd?.show(context, OnUserEarnedRewardListener() {

            })
        } else {
            Log.d(LandingActivity.TAG, "The rewarded ad wasn't ready yet.")
        }
    }

}
