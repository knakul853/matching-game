package com.knakul853.memoryboardgame

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.knakul853.memoryboardgame.models.BoardSize
import com.knakul853.memoryboardgame.models.MemoryCard
import com.squareup.picasso.Picasso
import kotlin.math.min
class MemoryBoardAdapter(
    private val context: Context,
    private val boardSize: BoardSize,
    private val cards: List<MemoryCard>,
    private val cardClickListner:CardClickListner
) :
    RecyclerView.Adapter<MemoryBoardAdapter.ViewHolder>() {

    companion object {
        private const val MARGIN_SIZE = 10
        private const val TAG = "MemoryBoardAdapter"
    }

    interface CardClickListner{
        fun onCardClicked(position: Int)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val cardWidth = parent.width/boardSize.getWidth() - (2 * MARGIN_SIZE)
        val cardHeight = parent.height/boardSize.getHeight() - (2 * MARGIN_SIZE)
        val cardSideLength = min(cardHeight, cardWidth)

        val view : View = LayoutInflater.from(context).inflate(R.layout.memory_card, parent, false)

        val layoutparams = view.findViewById<CardView>(R.id.cardView).layoutParams as ViewGroup.MarginLayoutParams
        layoutparams.height = cardSideLength
        layoutparams.width = cardSideLength
        layoutparams.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = boardSize.numCards

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageButton = itemView.findViewById<ImageButton>(R.id.imageButton)

        fun bind(position: Int) {
            val memoryCard = cards[position]
            if(cards[position].isFaceUp){
                if(memoryCard.imageUrl!=null){
                    Picasso.get().load(memoryCard.imageUrl).placeholder(R.drawable.ic_image).into(imageButton)
                }else{
                    imageButton.setImageResource(cards[position].identifier)
                }
            }else{
                imageButton.setImageResource(R.drawable.default_header)
            }

            imageButton.alpha = if (memoryCard.isMatched) 0.4f else 1.0f
           val colorStateList =  if(memoryCard.isMatched)ContextCompat.getColorStateList(context, R.color.color_grey) else null
            ViewCompat.setBackgroundTintList(imageButton, colorStateList)
            imageButton.setOnClickListener({
                cardClickListner.onCardClicked(position)
            })

        }
    }


}
