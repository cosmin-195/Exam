package com.example.exam

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import okhttp3.*
import rx.Observable
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subjects.PublishSubject
import java.util.concurrent.ExecutorService

import javax.inject.Inject

class Manager @Inject constructor(
    val context: Context,
    val service: Service,
    val executorService: ExecutorService,
    val dao: RecipeDao
) {
    val subject = PublishSubject.create<String>()
    val client = OkHttpClient()
    val request = Request.Builder().url("http://10.0.2.2:3000").build()
    val listener = object : WebSocketListener() {
        override fun onMessage(webSocket: WebSocket, text: String) {
            println("Received message: $text")
            subject.onNext(text)
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            subject.onCompleted()
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
        }
    }
    val webSocket = client.newWebSocket(request, listener)
    val socketObservable = subject.asObservable()

    

    fun networkConnectivity(): Boolean {
        val cm = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.getNetworkCapabilities(cm.activeNetwork)
        Log.d("NETWORK", "networkinfo ${networkInfo}")
        Log.d("NETWORK" ,"metered: ${cm.isActiveNetworkMetered}")

        if (networkInfo!=null){
            Log.d("NETWORK" ,"hasCapability: ${networkInfo.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)}")
        }
        return networkInfo != null && networkInfo.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun loadRecipes(type: String) {
        service.getRecipesWithPath(type)
            .subscribeOn(Schedulers.newThread())
            .subscribe(object : Subscriber<List<Recipe>>() {
                override fun onCompleted() {
                    Log.d("manager", "Recipes completed")
                }

                override fun onError(e: Throwable?) {
                    Log.d("manager", "Recipes error", e)
                }

                override fun onNext(t: List<Recipe>?) {
                    executorService.submit {
                        dao.deleteTypes()
                        if (t != null) {
                            dao.deleteRecipes()
                            dao.addRecipies(t)
                        }
                    }
                    Log.d("manager", "Recipe types persisted")
                }
            })
    }

    fun loadTypes() {
        service.types
            .subscribeOn(Schedulers.newThread())
            .subscribe(object : Subscriber<List<RecipeType>>() {
                override fun onCompleted() {
                    Log.d("manager", "Recipe types completed")
                }

                override fun onError(e: Throwable?) {
                    Log.d("manager", "Recipe types error", e)
                }

                override fun onNext(t: List<RecipeType>?) {
                    executorService.submit {
                        dao.deleteTypes()
                        if (t != null) {
                            dao.addTypes(t)
                        }
                    }
                    Log.d("manager", "Recipe types persisted")
                }
            })
    }

    fun saveRecipe(recipe: Recipe){
        service.addRecipe(recipe)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Subscriber<Recipe>(){
                override fun onCompleted() {
                    executorService.submit{
                        dao.addRecipe(recipe)
                        Log.d("manager", "Recipe add completed")
                    }
                }
                override fun onError(e: Throwable?) {
                    Log.d("manager", "Recipe types error", e)
                }

                override fun onNext(t: Recipe?) {
                    Log.d("manager", "Recipe persisted")
                }

            })
    }

    fun saveLocally(recipe: Recipe){
        executorService.submit{
            dao.addRecipe(recipe)
        }
    }
}