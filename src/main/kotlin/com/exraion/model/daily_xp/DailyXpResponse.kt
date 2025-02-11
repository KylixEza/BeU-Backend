package com.exraion.model.daily_xp

import com.google.gson.annotations.SerializedName

data class DailyXpResponse(
    @field:SerializedName("daily_xp_id")
    val dailyXpId: String,
    @field:SerializedName("daily_xp")
    val dailyXp: Int,
    @field:SerializedName("day")
    val day: Int,
    @field:SerializedName("is_taken")
    val isTaken: Boolean,
)
