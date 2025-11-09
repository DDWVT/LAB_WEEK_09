package com.example.lab_week_09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lab_week_09.ui.theme.LAB_WEEK_09Theme
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LAB_WEEK_09Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    App(
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun App(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            Home { listDataString ->
                navController.navigate(
                    "resultContent/?listData=$listDataString")
            }
        }
        composable(
            "resultContent/?listData={listData}",
            arguments = listOf(navArgument("listData") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            ResultContent(
                listDataJson = backStackEntry.arguments?.getString("listData").orEmpty()
            )
        }
    }
}

@Composable
fun Home(
    navigateFromHomeToResult: (String) -> Unit
) {
    var studentNameInput by remember { mutableStateOf("") }
    val listData = remember {
        mutableStateListOf(
            Student("Tanu"),
            Student("Tina"),
            Student("Tono")
        )
    }

    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val listType = Types.newParameterizedType(List::class.java, Student::class.java)
    val jsonAdapter = moshi.adapter<List<Student>>(listType)

    HomeContent(
        listData = listData,
        inputValue = studentNameInput,
        onInputValueChange = { input -> studentNameInput = input },
        onButtonClick = {
            if (studentNameInput.isNotBlank()) {
                listData.add(Student(studentNameInput))
                studentNameInput = ""
            }
        },
        navigateFromHomeToResult = {
            val listAsJson = jsonAdapter.toJson(listData.toList())
            navigateFromHomeToResult(listAsJson)
        }
    )
}

@Composable
fun HomeContent(
    listData: SnapshotStateList<Student>,
    inputValue: String,
    onInputValueChange: (String) -> Unit,
    onButtonClick: () -> Unit,
    navigateFromHomeToResult: () -> Unit
) {
    LazyColumn {
        item {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OnBackgroundTitleText(text = stringResource(
                    id = R.string.enter_item)
                )
                TextField(
                    value = inputValue,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    onValueChange = { onInputValueChange(it) }
                )
                Row {
                    PrimaryTextButton(text = stringResource(id =
                        R.string.button_click)) {
                        onButtonClick()
                    }
                    PrimaryTextButton(text = stringResource(id =
                        R.string.button_navigate)) {
                        navigateFromHomeToResult()
                    }
                }
            }
        }

        items(listData) { item ->
            Column(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OnBackgroundItemText(text = item.name)
            }
        }
    }
}

@Composable
fun ResultContent(listDataJson: String) {
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val listType = Types.newParameterizedType(List::class.java, Student::class.java)
    val jsonAdapter = moshi.adapter<List<Student>>(listType)

    val studentList = try {
        if (listDataJson.isNotBlank()) jsonAdapter.fromJson(listDataJson) else emptyList()
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }

    LazyColumn(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(studentList ?: emptyList()) { student ->
            OnBackgroundItemText(text = student.name)
        }
    }
}

@Composable
fun OnBackgroundTitleText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun OnBackgroundItemText(text: String) {
    Text(text = text, style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun PrimaryTextButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.padding(horizontal = 8.dp)) {
        Text(text)
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewHome() {
    LAB_WEEK_09Theme {
        Home(navigateFromHomeToResult = {})
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewResultContent() {
    LAB_WEEK_09Theme {
        val sampleJson = """[{"name":"Tanu"},{"name":"Tina"},{"name":"Tono"}]"""
        ResultContent(listDataJson = sampleJson)
    }
}


data class Student(
    var name: String
)