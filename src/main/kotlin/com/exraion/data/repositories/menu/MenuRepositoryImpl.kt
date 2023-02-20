package com.exraion.data.repositories.menu

import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import com.exraion.data.database.DatabaseFactory
import com.exraion.data.tables.FavoriteTable
import com.exraion.data.tables.MenuTable
import com.exraion.data.tables.ReviewTable
import com.exraion.model.menu.MenuBody
import com.exraion.model.menu.MenuListResponse
import com.exraion.util.toMenuListResponse
import org.jetbrains.exposed.sql.*

class MenuRepositoryImpl(
    private val dbFactory: DatabaseFactory
): MenuRepository {
    private fun getGeneralMenu(): FieldSet {
        return MenuTable.join(ReviewTable, JoinType.LEFT) {
            MenuTable.menuId.eq(ReviewTable.menuId)
        }.join(FavoriteTable, JoinType.LEFT) {
            MenuTable.menuId.eq(FavoriteTable.menuId)
        }
            .slice(
                MenuTable.menuId,
                MenuTable.difficulty,
                MenuTable.cookTime,
                MenuTable.image,
                MenuTable.startPrice,
                MenuTable.endPrice,
                Avg(ReviewTable.rating, 1).alias("rating"),
                MenuTable.title,
                FavoriteTable.uid
            )
    }
    override suspend fun insertMenu(body: MenuBody) {
        dbFactory.dbQuery {
            MenuTable.insert {
                it[menuId] = "MENU${NanoIdUtils.randomNanoId()}"
                it[title] = body.title
                it[description] = body.description
                it[category] = body.category
                it[difficulty] = body.difficulty
                it[calories] = body.calories
                it[cookTime] = body.cookTime
                it[estimatedTime] = body.estimatedTime
                it[startPrice] = body.startPrice
                it[endPrice] = body.endPrice
                it[benefit] = body.benefit
                it[videoUrl] = body.videoUrl
                it[image] = body.image
                it[xpGained] = body.xpGained
                it[ordered] = 0
                it[isExclusive] = false
                it[isAvailable] = true
            }
        }
    }

    override suspend fun getRandomMenus(uid: String): List<MenuListResponse> = dbFactory.dbQuery {
        getGeneralMenu()
            .selectAll()
            .groupBy(MenuTable.menuId, FavoriteTable.uid)
            .map { it.toMenuListResponse(uid) }
            .shuffled()
            .take(10)
    }

    override suspend fun getDietMenus(uid: String): List<MenuListResponse> = dbFactory.dbQuery {
        getGeneralMenu()
            .select { (MenuTable.category eq "Vegetables") }
            .groupBy(MenuTable.menuId)
            .map { it.toMenuListResponse(uid) }
    }

    override suspend fun getCategorizedMenus(uid: String, category: String): List<MenuListResponse> = dbFactory.dbQuery {
        getGeneralMenu()
            .select { (MenuTable.category eq category) }
            .groupBy(MenuTable.menuId)
            .map { it.toMenuListResponse(uid) }
    }

    override suspend fun getMenusBySearch(uid: String, query: String): List<MenuListResponse> = dbFactory.dbQuery {
        getGeneralMenu()
            .select { (MenuTable.title like "%$query%") }
            .groupBy(MenuTable.menuId)
            .map { it.toMenuListResponse(uid) }
    }
}