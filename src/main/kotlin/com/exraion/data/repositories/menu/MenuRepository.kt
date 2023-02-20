package com.exraion.data.repositories.menu

import com.exraion.model.menu.MenuBody
import com.exraion.model.menu.MenuListResponse

interface MenuRepository {

    suspend fun insertMenu(body: MenuBody)
    suspend fun getRandomMenus(uid: String): List<MenuListResponse>
    suspend fun getDietMenus(uid: String): List<MenuListResponse>
    suspend fun getCategorizedMenus(uid: String, category: String): List<MenuListResponse>
    suspend fun getMenusBySearch(uid: String, query: String): List<MenuListResponse>
}