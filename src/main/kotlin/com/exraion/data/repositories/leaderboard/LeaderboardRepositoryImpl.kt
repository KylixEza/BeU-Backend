package com.exraion.data.repositories.leaderboard

import com.exraion.data.database.DatabaseFactory
import com.exraion.data.tables.UserTable
import com.exraion.model.leaderboard.LeaderboardResponse
import com.exraion.util.toUserResponse
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class LeaderboardRepositoryImpl(
    private val dbFactory: DatabaseFactory
): LeaderboardRepository {
    override suspend fun getLeaderboard(): List<LeaderboardResponse> = dbFactory.dbQuery {

        val leaderboard = UserTable.selectAll()
            .orderBy(UserTable.xp, SortOrder.DESC)
            .limit(10)
            .mapNotNull { it.toUserResponse() }

        val leaderboardSize = leaderboard.size

        val list = mutableListOf<LeaderboardResponse>()

        for(rank in 0 until leaderboardSize) {
            val user = leaderboard[rank]
            list.add(
                LeaderboardResponse(
                    user.name,
                    user.avatar,
                    user.xp,
                    rank + 1
                )
            )
        }
        return@dbQuery list
    }

    override suspend fun getUserRank(uid: String): LeaderboardResponse {
        val leaderboard = dbFactory.dbQuery {
            UserTable.selectAll()
                .orderBy(UserTable.xp, SortOrder.DESC)
                .mapNotNull { it.toUserResponse() }
        }

        val selectedUser = dbFactory.dbQuery {
            UserTable.select { UserTable.uid.eq(uid) }.firstNotNullOf { it.toUserResponse() }
        }

        val rank = leaderboard.indexOf(selectedUser) + 1

        return LeaderboardResponse(
            selectedUser.name,
            selectedUser.avatar,
            selectedUser.xp,
            rank
        )
    }
}