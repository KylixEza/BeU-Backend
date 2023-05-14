package com.exraion.data.repositories.menu

import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import com.exraion.data.database.DatabaseFactory
import com.exraion.data.tables.*
import com.exraion.model.ingredient.IngredientBody
import com.exraion.model.ingredient.IngredientResponse
import com.exraion.model.menu.MenuBody
import com.exraion.model.menu.MenuDetailResponse
import com.exraion.model.menu.MenuListResponse
import com.exraion.util.toIngredientResponse
import com.exraion.util.toMenuDetailResponse
import com.exraion.util.toMenuListResponse
import com.exraion.util.toReviewResponse
import org.jetbrains.exposed.sql.*

class MenuRepositoryImpl(
    private val dbFactory: DatabaseFactory
): MenuRepository {
    private fun getBaseListMenu(): FieldSet {
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
                MenuTable.isExclusive,
                FavoriteTable.uid
            )
    }

    private fun getBaseMenu(): FieldSet {
        return MenuTable.join(ReviewTable, JoinType.LEFT) {
            MenuTable.menuId.eq(ReviewTable.menuId)
        }.join(FavoriteTable, JoinType.LEFT) {
            MenuTable.menuId.eq(FavoriteTable.menuId)
        }
            .slice(
                MenuTable.menuId,
                MenuTable.description,
                MenuTable.difficulty,
                MenuTable.calories,
                MenuTable.image,
                MenuTable.benefit,
                MenuTable.title,
                MenuTable.videoUrl,
                MenuTable.isAvailable,
                MenuTable.estimatedTime,
                Avg(ReviewTable.rating, 1).alias("rating"),
                Count(ReviewTable.rating).alias("review_count"),
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

    override suspend fun insertStep(menuId: String, step: String): Unit = dbFactory.dbQuery {
        StepTable.insert {
            it[StepTable.menuId] = menuId
            it[StepTable.step] = step
        }
    }

    override suspend fun insertIngredient(menuId: String, body: IngredientBody): Unit = dbFactory.dbQuery {
        IngredientTable.insert {
            it[IngredientTable.menuId] = menuId
            it[ingredient] = body.ingredient
            it[price] = body.price
        }
    }

    override suspend fun insertTool(menuId: String, tool: String): Unit = dbFactory.dbQuery {
        ToolTable.insert {
            it[ToolTable.menuId] = menuId
            it[ToolTable.tool] = tool
        }
    }

    override suspend fun getRandomMenus(uid: String): List<MenuListResponse> = dbFactory.dbQuery {
        getBaseListMenu()
            .selectAll()
            .groupBy(MenuTable.menuId, FavoriteTable.uid)
            .map { it.toMenuListResponse(uid) }
            .shuffled()
            .take(10)
    }

    override suspend fun getDietMenus(uid: String): List<MenuListResponse> = dbFactory.dbQuery {
        getBaseListMenu()
            .select { (MenuTable.category eq "Vegetables") }
            .groupBy(MenuTable.menuId, FavoriteTable.uid)
            .map { it.toMenuListResponse(uid) }
    }

    override suspend fun getExclusiveMenus(uid: String): List<MenuListResponse> = dbFactory.dbQuery {
        getBaseListMenu()
            .select { (MenuTable.isExclusive eq true) }
            .groupBy(MenuTable.menuId, FavoriteTable.uid)
            .map { it.toMenuListResponse(uid) }
    }


    override suspend fun getCategorizedMenus(uid: String, category: String): List<MenuListResponse> = dbFactory.dbQuery {
        getBaseListMenu()
            .select { (MenuTable.category eq category) }
            .groupBy(MenuTable.menuId, FavoriteTable.uid)
            .map { it.toMenuListResponse(uid) }
    }

    override suspend fun getMenusBySearch(uid: String, query: String): List<MenuListResponse> = dbFactory.dbQuery {
        getBaseListMenu()
            .select { (MenuTable.title like "%$query%") }
            .groupBy(MenuTable.menuId, FavoriteTable.uid)
            .map { it.toMenuListResponse(uid) }
    }

    override suspend fun getDetailMenu(uid: String, menuId: String): MenuDetailResponse = dbFactory.dbQuery {

        val ingredients = IngredientTable.select { (IngredientTable.menuId eq menuId) }
            .map { it[IngredientTable.ingredient] }

        val steps = StepTable.select { (StepTable.menuId eq menuId) }
            .map { it[StepTable.step] }

        val tools = ToolTable.select { (ToolTable.menuId eq menuId) }
            .map { it[ToolTable.tool] }

        val reviews = ReviewTable.join(UserTable, JoinType.FULL)
            .select {
                ReviewTable.menuId.eq(menuId)
            }.mapNotNull {
                it.toReviewResponse()
            }

        getBaseMenu()
            .select { (MenuTable.menuId eq menuId) }
            .groupBy(MenuTable.menuId, FavoriteTable.uid)
            .map { it.toMenuDetailResponse(
                uid,
                ingredients,
                tools,
                steps,
                reviews
            ) }.first()
    }

    override suspend fun getIngredients(menuId: String): List<IngredientResponse> = dbFactory.dbQuery {
        IngredientTable
            .select { (IngredientTable.menuId eq menuId) }
            .map { it.toIngredientResponse() }
    }
}