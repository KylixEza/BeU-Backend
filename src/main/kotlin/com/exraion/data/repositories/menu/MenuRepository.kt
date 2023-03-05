package com.exraion.data.repositories.menu

import com.exraion.model.ingredient.IngredientResponse
import com.exraion.model.menu.MenuBody
import com.exraion.model.menu.MenuDetailResponse
import com.exraion.model.menu.MenuListResponse

interface MenuRepository {

    suspend fun insertMenu(body: MenuBody)
    suspend fun insertStep(menuId: String, step: String)
    suspend fun insertIngredient(menuId: String, ingredient: String)
    suspend fun insertTool(menuId: String, tool: String)
    suspend fun getRandomMenus(uid: String): List<MenuListResponse>
    suspend fun getDietMenus(uid: String): List<MenuListResponse>
    suspend fun getCategorizedMenus(uid: String, category: String): List<MenuListResponse>
    suspend fun getMenusBySearch(uid: String, query: String): List<MenuListResponse>
    suspend fun getDetailMenu(uid: String, menuId: String): MenuDetailResponse
    suspend fun getIngredients(menuId: String): List<IngredientResponse>
}