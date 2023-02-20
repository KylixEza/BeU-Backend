package com.exraion.model.menu

import com.google.gson.annotations.SerializedName

data class MenuDetailResponse(
    @field:SerializedName("menu_id")
    val menuId: String,

    @field:SerializedName("title")
    val title: String,
)
