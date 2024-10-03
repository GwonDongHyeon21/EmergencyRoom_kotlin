package com.example.find_emergencyroom_composable

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.find_emergencyroom_composable.api.emergencyRoomApi
import com.example.find_emergencyroom_composable.api.findLocation
import com.example.find_emergencyroom_composable.model.EmergencyRoom
import com.example.find_emergencyroom_composable.model.EmergencyRoomInformation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

var emergencyRoomList = mutableListOf<EmergencyRoom>()
var emergencyRoomListAll = mutableListOf<EmergencyRoomInformation>()

@Composable
fun EmergencyRoomOnMapLayout(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    val roomPosition = LatLng(37.5665, 126.9780)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(roomPosition, 12f)
    }
    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(myLocationButtonEnabled = true)
        )
    }
    val properties by remember { mutableStateOf(MapProperties()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        emergencyRoomList.clear()
        emergencyRoomListAll.clear()

        coroutineScope.launch(Dispatchers.IO) {
            emergencyRoomApi()
            val locationResult = emergencyRoomList.map { room ->
                coroutineScope {
                    launch(Dispatchers.IO) {
                        findLocation(room.phId, room)
                    }
                }
            }
            locationResult.forEach { it.join() }
            isLoading = false
        }
    }

    if (isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text("Loading...")
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = properties,
                uiSettings = uiSettings
            ) {
                emergencyRoomListAll.forEach { emergencyRoom ->
                    val position =
                        LatLng(emergencyRoom.wgs84Lat.toDouble(), emergencyRoom.wgs84Lon.toDouble())
                    Marker(
                        state = MarkerState(position = position),
                        title = emergencyRoom.dutyName,
                        snippet = emergencyRoom.dutyAddress
                    )
                }
            }

            IconButton(
                onClick = { requestLocationWithPermission() },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(top = 20.dp, bottom = 30.dp)
                    .size(50.dp)
                    .background(Color.White, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Current Location",
                    tint = Color.Black,
                )
            }
        }
    }
}

@Composable
fun requestLocationWithPermission() {
    val context = LocalContext.current
    val hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // 권한이 허용된 경우 위치를 가져오기
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    Toast.makeText(
                        context,
                        "Current Location: ${it.latitude}, ${it.longitude}",
                        Toast.LENGTH_LONG
                    ).show()
                } ?: run {
                    Toast.makeText(
                        context,
                        "Unable to get location",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_LONG).show()
        }
    }

    if (!hasLocationPermission) {
        LaunchedEffect(Unit) {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}
