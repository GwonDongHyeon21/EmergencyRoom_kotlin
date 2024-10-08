package com.example.find_emergencyroom_composable

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
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
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale

var emergencyRoomList = mutableListOf<EmergencyRoom>()
var emergencyRoomListAll = mutableListOf<EmergencyRoomInformation>()

@Composable
fun EmergencyRoomOnMapLayout(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val startPosition = LatLng(37.5665, 126.9780)
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(startPosition, 12f)
    }
    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(myLocationButtonEnabled = true)
        )
    }
    val properties by remember { mutableStateOf(MapProperties()) }
    var isLoading by remember { mutableStateOf(true) }
    var currentAddress by remember { mutableStateOf("") }
    var buttonEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        emergencyRoomList.clear()
        emergencyRoomListAll.clear()

        coroutineScope.launch(Dispatchers.IO) {
            emergencyRoomApi("서울특별시", "")
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
            modifier = Modifier.fillMaxSize()
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = properties,
                uiSettings = uiSettings,
                onMapClick = { latLng ->
                    coroutineScope.launch {
                        currentAddress = getAddressFromLatLng(context, latLng).toString()
                        Toast.makeText(context, currentAddress, Toast.LENGTH_SHORT).show()
                        buttonEnabled = true
                    }
                }
            ) {
                emergencyRoomListAll.forEach { emergencyRoom ->
                    val position =
                        LatLng(emergencyRoom.wgs84Lat.toDouble(), emergencyRoom.wgs84Lon.toDouble())
                    Marker(
                        state = MarkerState(position = position),
                        title = emergencyRoom.dutyName,
                        snippet = "응급실 일반 병상수: ${emergencyRoom.roomCount}",
                    )
                }
            }

            IconButton(
                onClick = {
                    val activity = context as MainActivity
                    if (activity.requestLocationPermission(context)) {
                        coroutineScope.launch {
                            val location = getCurrentLocation(context, fusedLocationClient)
                            location?.let {
                                val locationLatLng = LatLng(it.latitude, it.longitude)
                                currentAddress =
                                    getAddressFromLatLng(context, locationLatLng).toString()
                                cameraPositionState.position =
                                    CameraPosition.fromLatLngZoom(locationLatLng, 12f)
                                buttonEnabled = true
                            }
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 20.dp, bottom = 30.dp)
                    .background(Color.White, CircleShape)
                    .size(40.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Current Location",
                    tint = Color.Black,
                )
            }

            Button(
                onClick = {
                    buttonEnabled = false
                    isLoading = true
                    coroutineScope.launch {
                        emergencyRoomList.clear()
                        emergencyRoomListAll.clear()

                        coroutineScope.launch(Dispatchers.IO) {
                            val query = currentAddress.split(" ")
                            val query1 =
                                query.filter { it.endsWith("도") }.takeIf { it.isNotEmpty() }
                                    ?: listOf("")
                            val query2 =
                                query.filter { it.endsWith("시") }.takeIf { it.isNotEmpty() }
                                    ?: listOf("")
                            emergencyRoomApi(query1[0], query2[0])
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
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(20.dp),
                enabled = buttonEnabled
            ) {
                Text(text = "이 위치에서 다시 찾기")
            }
        }
    }
}

suspend fun getCurrentLocation(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
): Location? {
    if (ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return null
    }

    return try {
        fusedLocationClient.lastLocation.await()
    } catch (e: Exception) {
        null
    }
}

suspend fun getAddressFromLatLng(context: Context, latLng: LatLng): String? {
    return withContext(Dispatchers.IO) {
        val geocoder = Geocoder(context, Locale.getDefault())
        return@withContext try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (!addresses.isNullOrEmpty()) addresses[0].getAddressLine(0)
            else ""
        } catch (e: Exception) {
            ""
        }
    }
}