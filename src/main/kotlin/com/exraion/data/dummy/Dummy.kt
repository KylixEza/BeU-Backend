package com.exraion.data.dummy

import com.exraion.model.daily_xp.DailyXp

object Dummy {

    fun getInitialDailyXp() = listOf(
        DailyXp(
            "DAILYXP001",
            5,
            1
        ),
        DailyXp(
            "DAILYXP002",
            10,
            2
        ),
        DailyXp(
            "DAILYXP003",
            15,
            3
        ),
        DailyXp(
            "DAILYXP004",
            20,
            4
        ),
        DailyXp(
            "DAILYXP005",
            25,
            5
        ),
        DailyXp(
            "DAILYXP006",
            30,
            6
        ),
        DailyXp(
            "DAILYXP007",
            35,
            7
        ),
    )

}