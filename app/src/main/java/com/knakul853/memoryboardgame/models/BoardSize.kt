package com.knakul853.memoryboardgame.models

enum  class BoardSize (val numCards: Int){

    EASY(8),
    MEDIUM(18),
    HARD(24);

    companion object{
        fun getByValue(value:Int) = values().first{it.numCards == value }
    }
    fun getWidth():Int{
        return when (this){
            EASY -> return 2
            MEDIUM -> return 3
            HARD -> return 4
        }
    }

    fun getHeight(): Int{
        return numCards / getWidth();
    }

    fun getNumPairs():Int{
        return numCards / 2;
    }
}