package com.exraion.data.repositories.leaderboard

import com.exraion.model.leaderboard.LeaderboardResponse

interface LeaderboardRepository {

    suspend fun getLeaderboard(): List<LeaderboardResponse>  //clear
    suspend fun getUserRank(uid: String): LeaderboardResponse //clear

}