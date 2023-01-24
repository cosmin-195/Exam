package com.example.exam

import rx.Observable
import com.example.exam.Recipe
import com.example.exam.RecipeType
import retrofit2.http.*

interface Service {
    @GET("recipes")
    fun getRecipes(@Query("type") type: String): Observable<List<Recipe>>

    @GET("recipes/{type}")
    fun getRecipesWithPath(@Path("type") type: String): Observable<List<Recipe>>

    @get:GET("types")
    val types: Observable<List<RecipeType>>

    @POST("recipe")
    fun addRecipe(@Body r : Recipe):Observable<Recipe>

    companion object {
        const val SERVICE_ENDPOINT = "http://10.0.2.2:3000"
    }
}