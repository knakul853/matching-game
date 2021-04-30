package com.knakul853.memoryboardgame.utils

import android.content.Context
import com.knakul853.memoryboardgame.models.ChooseGame

fun initData( context: Context) : MutableList<ChooseGame> {

    var data:MutableList<ChooseGame> = mutableListOf<ChooseGame>();
    data.add(createData("default_header", context)) // default game data.
    data.add(createData("fruits_header", context)) // fruits game data.
    data.add(createData("cars_header", context)) // fruits game data.
    data.add(createData("animals_header", context)) // fruits game data.
    data.add(createData("skys_5_5", context)) // fruits game data.
    data.add(createData("insects_6_1", context)) // fruits game data.
    data.add(createData("football_7_1", context))
    data.add(createData("cricket_8_1", context))




    return data
}



fun createData(header:String, context:Context): ChooseGame{

    val headerId = context.resources.getIdentifier("$header", "drawable", context.packageName )
    if(header=="default_header"){
        return ChooseGame( headerId, true)
    }

    return ChooseGame(headerId, false)
}


