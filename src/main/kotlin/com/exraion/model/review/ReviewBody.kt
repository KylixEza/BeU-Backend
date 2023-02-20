package com.exraion.model.review

import com.google.gson.annotations.SerializedName

data class ReviewBody(
	@field:SerializedName("menu_id")
	val menuId: String,

	@field:SerializedName("rating")
	val rating: Double
)
