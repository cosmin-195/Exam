package com.example.exam

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RecipeDao {

    @get:Query("select * from recipes")
    val recipes: LiveData<MutableList<Recipe>>

    @get:Query("select * from types")
    val types:LiveData<MutableList<RecipeType>>


    @Insert
    fun addTypes(types: List<RecipeType>)


    @Query("delete from types")
    fun deleteTypes()


    @Insert
    fun addRecipe(recipe: Recipe)

    @Insert
    fun addRecipies(recipes: List<Recipe>)

    @Delete
    fun deleteRecipe(r: Recipe)

    @Query("delete from recipes")
    fun deleteRecipes()

    @Update
    fun updateBook(r: Recipe)
}

@androidx.room.Database(entities = [Recipe::class, RecipeType::class], version = 1)
abstract class Database : RoomDatabase(){
    abstract val dao : RecipeDao
}