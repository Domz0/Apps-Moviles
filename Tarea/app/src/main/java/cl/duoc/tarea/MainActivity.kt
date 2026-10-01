package cl.duoc.tarea

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cl.duoc.tarea.ui.theme.HomeScreen
import cl.duoc.tarea.ui.theme.HomeScreenPreview
import cl.duoc.tarea.ui.theme.TareaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TareaTheme {
                //Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    //Greeting(
                        //name = "Android",
                        //modifier = Modifier.padding(innerPadding)
                    //)
                    HomeScreen()
                }
            }
        }
    }

/*
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TareaTheme {
        Greeting("Android")
        HomeScreenPreview()
    }
}

 */