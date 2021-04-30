package com.knakul853.memoryboardgame

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.knakul853.memoryboardgame.models.ChooseGame
import kotlin.math.min


class ChooseGameAdapter(
    private val context: Context,
    private val data: MutableList<ChooseGame>?,
    private val cardClickListner: ChooseGameAdapter.CardClickListner

) : RecyclerView.Adapter<ChooseGameAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.choose_game_element, parent, false)
        val cardWidth = parent.width / 2
        val cardHeight = parent.height / 2
        val cardSide = min(cardHeight, cardWidth)


        val layoutParams = view.findViewById<ImageView>(R.id.choose_game_image).layoutParams
        layoutParams.width = cardSide
        layoutParams.height = cardSide

        return ViewHolder(view)

    }

    interface CardClickListner{
        fun onCardClicked(position: Int)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }


    override fun getItemCount(): Int {
        if (data != null) {
            return data.size
        }
        return 0;
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(position: Int) {
            val oneGame = data?.get(position)
            if(oneGame!=null) {
                val imageView = itemView.findViewById<ImageView>(R.id.choose_game_image)
                val lockView = itemView.findViewById<ImageView>(R.id.lockImage)
                val special_view = itemView.findViewById<ImageView>(R.id.spacial)

                lockView.visibility = View.GONE
                imageView.setImageResource(oneGame.Header)

                if(position%5 == 0 && position !=0){
                    special_view.visibility = View.VISIBLE
                    special_view.setImageResource(R.drawable.spacial_game)
                    animateStart(special_view)
                }

                if (!oneGame.isPrevGameOver) {
                    imageView.alpha = 0.4f
                    lockView.setImageResource(R.drawable.lock)
                    lockView.visibility = View.VISIBLE
                }

                if (oneGame.isPrevGameOver){
                    imageView.alpha = 1.0f
                    imageView.setOnClickListener({
                        cardClickListner.onCardClicked(position)
                    })
            }
                else{
                    imageView.setOnClickListener({
                        Toast.makeText(context, "Please unlock previous game", Toast.LENGTH_SHORT)
                            .show()
                    })
                }
            }
        }
    }
    fun animateStart(view: View){
        val rotate = RotateAnimation(
            0F,
            180F,
            Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate.repeatCount = 1000000000
        rotate.interpolator = LinearInterpolator()
        rotate.setDuration(5000);
        view.startAnimation(rotate)

    }

}
