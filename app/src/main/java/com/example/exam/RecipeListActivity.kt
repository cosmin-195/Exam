package com.example.exam

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.exam.ui.theme.ExamTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@AndroidEntryPoint
class RecipeListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = ViewModelProvider(this)[RecipesViewModel::class.java]
        val id = this.intent.getStringExtra("update")
        id?.let {view.loadEvents(it)}


        setContent {
            ExamTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val categories: List<Recipe>? by view.categories.observeAsState()
                    categories?.let {
                        Column(
                            verticalArrangement = Arrangement.SpaceEvenly,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            DisplayRecipes(
                                recipes = it,
                                viewModel = view,
                                )
                            Draw()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Draw() {
    val context = LocalContext.current
    Button(onClick = {
        context.startActivity(Intent(context, RecipeAddActivity::class.java))
    }) {
        Text(text = "Add a new recipe")
    }

}

@Composable
fun DisplayRecipes(
    recipes: List<Recipe>,
    viewModel: RecipesViewModel,
) {
    val context = LocalContext.current
    Column {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(recipes) { item: Recipe ->
                RecipeListItem(recipe = item, deleteOp = {
                    //view model.remove
                }){
                    //other logic
                }
            }
        }
    }
}

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val dao: RecipeDao,
    private val manager: Manager
) : ViewModel() {
    var categories: LiveData<MutableList<Recipe>> = dao.recipes;

    fun loadEvents(type: String): Boolean {
        if (manager.networkConnectivity()) {
            Log.d("main", "connected")
            manager.loadRecipes(type)
            return true;
        }
        return false;
    }
}