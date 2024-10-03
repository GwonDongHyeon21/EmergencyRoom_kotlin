package com.example.find_emergencyroom_composable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "main") {
        composable("main") {
            MainLayout(navController)
        }
        composable("roomOnMap") {
            EmergencyRoomOnMapLayout(navController)
        }
    }
}

@Composable
fun MainLayout(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxHeight(0.3f)
        )
        Text(
            text = "응급실 찾기",
            modifier = Modifier
                .fillMaxHeight(0.6f),
            style = TextStyle(
                fontSize = 20.sp,
                fontStyle = FontStyle.Normal
            )
        )

        Button(
            modifier = Modifier
                .padding(60.dp),
            onClick = {
                navController.navigate("roomOnMap")
            }) {
            Text("찾기")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    MainLayout(rememberNavController())
}