package com.exraion.data.repositories.daily_xp

import com.exraion.model.daily_xp.DailyXpRequest
import com.exraion.model.daily_xp.DailyXpResponse

interface DailyXpRepository {
    suspend fun insertDailyXp()
    suspend fun checkDailyXpAvailability(uid: String)
    suspend fun getDailXps(uid: String): List<DailyXpResponse>
    suspend fun getTodayDailyXP(uid: String): DailyXpResponse
    suspend fun takeDailyXp(uid: String, body: DailyXpRequest)
}