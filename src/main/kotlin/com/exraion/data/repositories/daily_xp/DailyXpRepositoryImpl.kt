package com.exraion.data.repositories.daily_xp

import com.exraion.data.database.DatabaseFactory
import com.exraion.data.dummy.Dummy
import com.exraion.data.tables.DailyXpTable
import com.exraion.data.tables.DailyXpUserTable
import com.exraion.data.tables.UserTable
import com.exraion.model.daily_xp.DailyXpRequest
import com.exraion.model.daily_xp.DailyXpResponse
import com.exraion.util.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus

class DailyXpRepositoryImpl(
    private val dbFactory: DatabaseFactory
): DailyXpRepository {
    override suspend fun insertDailyXp() = dbFactory.dbQuery {
        val isInitialDataAlreadyInserted = DailyXpTable.selectAll().count() > 0

        if(isInitialDataAlreadyInserted.not()) {
            Dummy.getInitialDailyXp().forEach { item ->
                DailyXpTable.insert { table ->
                    table[dailyXpId] = item.dailyXpId
                    table[dailyXp] = item.dailyXp
                    table[day] = item.day
                }
            }

            val allUids = UserTable.selectAll().map { it[UserTable.uid] }
            allUids.forEach { uid ->
                Dummy.getInitialDailyXp().forEach { item ->
                    DailyXpUserTable.insert { table ->
                        table[dailyXpId] = item.dailyXpId
                        table[DailyXpUserTable.uid] = uid
                        table[dayTaken] = null
                        table[isTaken] = false
                    }
                }
            }
        }
    }

    override suspend fun checkDailyXpAvailability(uid: String) = dbFactory.dbQuery {
        val collectableXp = DailyXpUserTable.select {
            DailyXpUserTable.uid eq uid and (DailyXpUserTable.isTaken eq true)
        }

        val isStudentReachMax = collectableXp.count() >= 7
        val isLastDayToday = collectableXp.mapNotNull { it[DailyXpUserTable.dayTaken] }.sortDate().firstOrNull() == createTimeStamp(
            DateFormat.DATE)

        val daysTaken = collectableXp.mapNotNull { it[DailyXpUserTable.dayTaken] }

        val gapFromLastDay = if(daysTaken.isNotEmpty()) {
            createTimeStamp(DateFormat.DATE) gapBetween daysTaken.sortDate().first()
        } else {
            0
        }

        if((isStudentReachMax and isLastDayToday.not()) || gapFromLastDay >= 2)
            DailyXpUserTable.update(where =  { DailyXpUserTable.uid eq uid }) {
                it[isTaken] = false
                it[dayTaken] = null
            }
    }

    override suspend fun getDailXps(uid: String): List<DailyXpResponse> = dbFactory.dbQuery {
        DailyXpTable.join(DailyXpUserTable, JoinType.INNER) {
            DailyXpTable.dailyXpId eq DailyXpUserTable.dailyXpId
        }.select { DailyXpUserTable.uid eq uid }
            .orderBy(DailyXpTable.day to SortOrder.ASC)
            .map { it.toDailyXpResponse() }
    }

    override suspend fun getTodayDailyXP(uid: String): DailyXpResponse = dbFactory.dbQuery {

        val isTodayTaken = DailyXpUserTable.select {
            DailyXpUserTable.uid eq uid and (DailyXpUserTable.isTaken eq true)
        }.mapNotNull { it[DailyXpUserTable.dayTaken] }.sortDate().firstOrNull() == createTimeStamp(DateFormat.DATE)

        if(isTodayTaken) {
            DailyXpUserTable.join(DailyXpTable, JoinType.INNER) {
                DailyXpUserTable.dailyXpId eq DailyXpTable.dailyXpId
            }.select {
                DailyXpUserTable.uid eq uid and (DailyXpUserTable.dayTaken eq createTimeStamp(DateFormat.DATE))
            }.orderBy(DailyXpTable.day).map { it.toDailyXpResponse() }.first()
        } else {
            DailyXpUserTable.join(DailyXpTable, JoinType.INNER) {
                DailyXpUserTable.dailyXpId eq DailyXpTable.dailyXpId
            }.select {
                DailyXpUserTable.uid eq uid and (DailyXpUserTable.isTaken eq false)
            }.orderBy(DailyXpTable.day).map { it.toDailyXpResponse() }.first()
        }
    }

    override suspend fun takeDailyXp(uid: String, body: DailyXpRequest): Unit = dbFactory.dbQuery {
        DailyXpUserTable.update(where = { (DailyXpUserTable.uid eq uid) and (DailyXpUserTable.dailyXpId eq body.dailyXpId) }) {
            it[dayTaken] = createTimeStamp(DateFormat.DATE)
            it[isTaken] = true
        }

        val dailyXp = DailyXpTable.select { DailyXpTable.dailyXpId eq body.dailyXpId }.first()[DailyXpTable.dailyXp]
        UserTable.update(where = { UserTable.uid eq uid }) {
            it[xp] = xp plus dailyXp
        }
    }
}