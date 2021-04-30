package com.knakul853.memoryboardgame

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.knakul853.memoryboardgame.models.BoardSize
import kotlin.math.min

class ImagePickerAdapter(
    private val  context: Context,
    private val chosenImageUri: MutableList<Uri>,
    private  val boardSize: BoardSize,
    private val imageClickListener:ImageClickListener
) : RecyclerView.Adapter<ImagePickerAdapter.ViewHolder>() {

    interface ImageClickListener{
        fun onPlaceHolderClicked()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.card_image, parent, false)
        val cardWidth = parent.width / boardSize.getWidth()
        val cardHeight = parent.height / boardSize.getHeight()
        val cardSideLength = min(cardHeight, cardWidth)

        val layoutParams = view.findViewById<ImageView>(R.id.ivCostumeImage).layoutParams
        layoutParams.width = cardSideLength
        layoutParams.height = cardSideLength

        return ViewHolder(view)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if(position < chosenImageUri.size){
            holder.bind(chosenImageUri[position])
        }
        else{
            holder.bind();
        }
    }

    override fun getItemCount(): Int {
        return boardSize.getNumPairs()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        private val ivImage = itemView.findViewById<ImageView>(R.id.ivCostumeImage)
        fun bind(uri: Uri) {
            ivImage.setImageURI(uri)
            ivImage.setOnClickListener(null)
        }
        fun bind() {
            ivImage.setOnClickListener({
            imageClickListener.onPlaceHolderClicked()
            })

        }

    }

}
