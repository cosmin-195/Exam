package com.example.exam

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true) var id: Int,
    var name: String? = "name",
    var details: String? = "details",
    var time: Int? = 3,
    var type: String? = "begginer",
    var rating: Int? = 0
) {
    override fun toString(): String {
        return "Recipe(id=$id, name=$name, type=$type, rating=$rating)"
    }
}

@Entity(tableName = "types")
data class RecipeType(@PrimaryKey var name: String)