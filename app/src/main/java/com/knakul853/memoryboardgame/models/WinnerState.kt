package com.knakul853.memoryboardgame.models

val PREFERENCES_FILE_NAME = "PREFERENCES_FILE_NAME"

data class WinnerState (
    val boardSize:BoardSize,
    var numberOfMoves:Int = 10000000,
    var level:Int = -1

    )
