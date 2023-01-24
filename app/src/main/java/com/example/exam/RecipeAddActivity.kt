package com.example.exam

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.exam.ui.theme.ExamTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.multibindings.IntKey
import javax.inject.Inject

@AndroidEntryPoint
class RecipeAddActivity : ComponentActivity() {

    @Inject
    lateinit var manager: Manager

    @Inject
    lateinit var dao: RecipeDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var toAdd = Recipe(-1)
        setContent {
            ExamTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        AddName(toAdd = toAdd)
                        AddRating(toAdd = toAdd)
                        AddType(toAdd = toAdd)
                        Save(toSave = toAdd, manager = manager, dao = dao)
                    }
                }
            }
        }
    }
}

@Composable
fun Save(toSave: Recipe, manager: Manager, dao: RecipeDao) {
    val context = LocalContext.current
    var snackbarShown by remember { mutableStateOf(false) }

    Button(
        onClick = {
            println("COONECTED TO SERVER: ${manager.networkConnectivity()}")
            Log.d("ADD","COONECTED TO SERVER: ${manager.networkConnectivity()}");
            if (manager.networkConnectivity()) {
                manager.saveRecipe(toSave)
                context.startActivity(Intent(context, RecipeListActivity::class.java))
            } else {
                manager.saveLocally(toSave)
                snackbarShown = true
            }
        },
    ) {
        Text(text = "Save")
    }
    if (snackbarShown) {
        Snackbar(
            content = {
                Text(text = "No internet connection")
            },
        )
    }
}


@Composable
fun AddName(toAdd: Recipe) {
    var text by remember { mutableStateOf("") }
    Text(text = "name")
    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            toAdd.name = it
        },
        label = { Text("Add a name") }
    )
}

@Composable
fun AddType(toAdd: Recipe) {
    var text by remember { mutableStateOf("") }
    Text(text = "type")
    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            toAdd.type = it
        },
        label = { Text("Add a type") }
    )
}

@Composable
fun AddRating(toAdd: Recipe) {
    var text by remember { mutableStateOf(0) }
    Text(text = "rating")
    OutlinedTextField(
        value = text.toString(),
        onValueChange = {
            text = it.toInt()
            toAdd.rating = it.toInt()
        },
        label = { Text("Add a rating") }
    )
}
