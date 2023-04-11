package com.exraion.model.daily_xp

import com.google.gson.annotations.SerializedName

data class DailyXpRequest(
    @field:SerializedName("daily_xp_id")
    val dailyXpId: String,
)
