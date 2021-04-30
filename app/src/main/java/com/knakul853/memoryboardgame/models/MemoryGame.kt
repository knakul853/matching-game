package com.knakul853.memoryboardgame.models

class MemoryGame(
    private val boardSize: BoardSize,
    private val customGameImages: List<String>?,
    levelImage: List<Int>
){
//    private val levelImages: List<Int>
    val cards: List<MemoryCard>
    var numPairsFound = 0
    private var indexOfSelectedCard: Int?=null
    private var foundMatch = false
    private var numFlips = 0

    init {
        if(customGameImages == null) {
            val choosenImages = levelImage.shuffled().take(boardSize.getNumPairs())
            val randomizedImages = (choosenImages + choosenImages).shuffled()
            cards = randomizedImages.map { MemoryCard(it) }
        }
        else{
            val randomizedImages = (customGameImages + customGameImages).shuffled()
            cards = randomizedImages.map { MemoryCard(it.hashCode(), it) }

        }
    }

    fun flipCard(position: Int) :Boolean{
        val card = cards[position]
        numFlips++;

        // 0 card previously flipped over: (restore the card)flip over the selected card.
        // 1 card previously flipped over: flip over the selected card + check if the image match
        // 2 card previously flipped over: restore the card + flip the selected card.

        if(indexOfSelectedCard == null){
            restoreCards()
            indexOfSelectedCard = position
        }
        else{
           foundMatch = checkForMatch(indexOfSelectedCard!!, position)
            indexOfSelectedCard = null
        }

        card.isFaceUp = !card.isFaceUp
        return foundMatch

    }

    private fun checkForMatch(indexOfSelectedCard: Int, position: Int): Boolean {

        if (cards[indexOfSelectedCard].identifier != cards[position].identifier){
            return false
        }
        cards[indexOfSelectedCard].isMatched = true
        cards[position].isMatched = true
        numPairsFound++
        return true

    }

    private fun restoreCards() {
        for (card in cards){
            if (!card.isMatched){
                card.isFaceUp = false
            }
        }
    }

    fun isWinner(): Boolean {
        return numPairsFound == boardSize.getNumPairs()

    }

    fun isFaceUp(position: Int): Boolean {
        return cards[position].isFaceUp
    }

    fun getNumMoves(): Int {
        return numFlips / 2
    }
}