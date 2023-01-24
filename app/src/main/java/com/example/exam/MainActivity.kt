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
import androidx.compose.material.*
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
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = ViewModelProvider(this)[CategoriesViewModel::class.java]
        view.loadEvents()


        setContent {
            ExamTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val categories: List<RecipeType>? by view.categories.observeAsState()
                    categories?.let {
                        Column(
                            verticalArrangement = Arrangement.SpaceEvenly,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            DisplayCategories(
                                categoryList = it,
                                viewModel = view,
                                selectedItem = {})
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DisplayCategories(
    categoryList: List<RecipeType>,
    viewModel: CategoriesViewModel,
    selectedItem: (RecipeType) -> Unit
) {
    val context = LocalContext.current
    Column {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(categoryList) { item: RecipeType ->
                Button(onClick = {
                    val intent = Intent(context, RecipeListActivity::class.java)
                    intent.putExtra("update", item.name)
                    context.startActivity(intent)
                }) {
                    Text(text = item.name)
                }
            }
        }
    }
}

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val dao: RecipeDao,
    private val manager: Manager
) : ViewModel() {
    var categories: LiveData<MutableList<RecipeType>> = dao.types;

    fun loadEvents(): Boolean {
        if (manager.networkConnectivity()) {
            Log.d("main", "connected")
            manager.loadTypes()
            return true;
        }
        return false;
    }
}

