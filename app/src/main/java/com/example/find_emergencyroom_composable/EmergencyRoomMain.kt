package com.example.find_emergencyroom_composable

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController

class EmergencyRoomMain : ComponentActivity() {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private var permissionCheck: Boolean = true
    private var isRequestingPermission: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            isRequestingPermission = false
            permissionCheck = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        }

        setContent {
            Layout()
        }
    }

    fun requestLocationPermission(context: Context): Boolean {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return if (hasFineLocationPermission && hasCoarseLocationPermission) {
            true
        } else {
            if (!isRequestingPermission) {
                isRequestingPermission = true
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
                return permissionCheck
            } else {
                return permissionCheck
            }
        }
    }
}

@Composable
fun Layout() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "main") {
        composable("main") {
            EmergencyRoomMainLayout(navController)
        }
        composable("roomOnMap") {
            EmergencyRoomOnMapLayout(navController)
        }
        composable(
            route = "roomDetail/{dutyName}/{roomCount}/{dutyAddress}/{lat}/{lon}/{dutyTel}",
            arguments = listOf(
                navArgument("dutyName") { type = NavType.StringType },
                navArgument("roomCount") { type = NavType.StringType },
                navArgument("dutyAddress") { type = NavType.StringType },
                navArgument("dutyTel") { type = NavType.StringType },
                navArgument("lat") { type = NavType.StringType },
                navArgument("lon") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val dutyName = backStackEntry.arguments?.getString("dutyName")
            val roomCount = backStackEntry.arguments?.getString("roomCount")
            val dutyAddress = backStackEntry.arguments?.getString("dutyAddress")
            val dutyTel = backStackEntry.arguments?.getString("dutyTel")
            val lat = backStackEntry.arguments?.getString("lat")
            val lon = backStackEntry.arguments?.getString("lon")

            EmergencyRoomDetailLayout(dutyName, roomCount, dutyAddress, dutyTel, lat, lon)
        }
    }
}

@Composable
fun EmergencyRoomMainLayout(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxHeight(0.3f)
        )
        Text(
            text = "응급실 찾기",
            modifier = Modifier
                .fillMaxHeight(0.8f),
            style = TextStyle(
                fontSize = 20.sp,
                fontStyle = FontStyle.Normal
            )
        )

        Button(
            onClick = {
                navController.navigate("roomOnMap")
            }) {
            Text("찾기")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainLayout() {
    EmergencyRoomMainLayout(rememberNavController())
}