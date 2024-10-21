package com.example.find_emergencyroom_composable

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
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
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale

var emergencyRoomList = mutableListOf<EmergencyRoom>()
var emergencyRoomListAll = mutableListOf<EmergencyRoomInformation>()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyRoomOnMapLayout(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val startPosition = LatLng(37.5665, 126.9780)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(startPosition, 12f)
    }
    val uiSettings by remember {
        mutableStateOf(MapUiSettings(myLocationButtonEnabled = true))
    }
    val properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }
    var isLoading by remember { mutableStateOf(false) }
    var currentAddress by remember { mutableStateOf("") }
    var buttonEnabled by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf(emptyList<Address>()) }

    if (isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text("Loading...")
        }
    } else {
        BottomSheetScaffold(
            modifier = Modifier.fillMaxSize(),
            sheetPeekHeight = 40.dp,
            sheetContainerColor = Color(0xFFFFFFFF),
            sheetContent = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    item {
                        Text(
                            text = "응급실 리스트",
                            style = TextStyle(fontSize = 30.sp),
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    items(emergencyRoomListAll) { emergencyRoom ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    color = Color.Black,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
                                .clickable {
                                    navController.navigate(
                                        "roomDetail/ " +
                                                "${emergencyRoom.dutyName}/" +
                                                "${emergencyRoom.roomCount}/" +
                                                "${emergencyRoom.dutyAddress}/" +
                                                "${emergencyRoom.wgs84Lat}/" +
                                                "${emergencyRoom.wgs84Lon}/" +
                                                emergencyRoom.dutyTel
                                    )
                                }
                        ) {
                            Text(
                                text = emergencyRoom.dutyName,
                                modifier = Modifier.padding(2.dp),
                                style = TextStyle(fontSize = 20.sp),
                            )
                            Text(
                                text = "주소지: ${emergencyRoom.dutyAddress}",
                                modifier = Modifier.padding(2.dp),
                                style = TextStyle(fontSize = 12.sp),
                            )
                            Text(
                                text = "병상수: ${emergencyRoom.roomCount}",
                                modifier = Modifier.padding(2.dp),
                                style = TextStyle(fontSize = 15.sp),
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

            }
        ) {
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
                        val position = LatLng(
                            emergencyRoom.wgs84Lat.toDouble(),
                            emergencyRoom.wgs84Lon.toDouble()
                        )
                        Marker(
                            state = MarkerState(position = position),
                            title = emergencyRoom.dutyName,
                            snippet = "응급실 일반 병상수: ${emergencyRoom.roomCount}",
                            onInfoWindowClick = {
                                navController.navigate(
                                    "roomDetail/ " +
                                            "${emergencyRoom.dutyName}/" +
                                            "${emergencyRoom.roomCount}/" +
                                            "${emergencyRoom.dutyAddress}/" +
                                            "${emergencyRoom.wgs84Lat}/" +
                                            "${emergencyRoom.wgs84Lon}/" +
                                            emergencyRoom.dutyTel
                                )
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier.align(Alignment.TopCenter)
                ) {
                    Column(
                        modifier = Modifier.wrapContentSize()
                    ) {
                        TextField(
                            value = searchQuery,
                            onValueChange = {
                                if (it.length <= 20) {
                                    searchQuery = it
                                }
                                coroutineScope.launch {
                                    try {
                                        val results = searchLocation(context, searchQuery)
                                        searchResults = results ?: emptyList()
                                    } catch (_: Exception) {
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .padding(top = 10.dp),
                            shape = RoundedCornerShape(20.dp),
                            textStyle = TextStyle(
                                color = Color.Black,
                                fontSize = 15.sp,
                            ),
                            singleLine = true,
                            maxLines = 1,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                            ),
                            placeholder = {
                                Text(
                                    text = "주소 또는 장소를 입력하세요",
                                    style = TextStyle(
                                        fontSize = 15.sp,
                                        color = Color.Gray,
                                    ),
                                )
                            }
                        )

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .align(Alignment.CenterHorizontally)
                                .background(Color(240, 240, 240, 255))
                        ) {
                            items(searchResults) { location ->
                                Text(
                                    text = location.getAddressLine(0),
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .clickable {
                                            val latLng =
                                                LatLng(location.latitude, location.longitude)
                                            cameraPositionState.position =
                                                CameraPosition.fromLatLngZoom(latLng, 12f)
                                            currentAddress = location.getAddressLine(0)
                                            buttonEnabled = true
                                        },
                                )
                                Spacer(
                                    modifier = Modifier
                                        .height(2.dp)
                                        .fillMaxWidth(0.5f)
                                        .background(Color.White),
                                )
                            }
                        }
                    }

                    Button(
                        modifier = Modifier
                            .align(Alignment.Top)
                            .padding(top = 10.dp, start = 10.dp),
                        onClick = {
                            if (searchResults.isEmpty()) {
                                Toast.makeText(context, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "검색 결과를 선택하세요.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("검색")
                    }
                }

                IconButton(
                    onClick = {
                        val activity = context as EmergencyRoomMain
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
                        .padding(start = 20.dp, bottom = 60.dp)
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
                        searchQuery = ""
                        searchResults = emptyList()
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
                                    coroutineScope.launch(Dispatchers.IO) {
                                        findLocation(room.phId, room)
                                    }
                                }
                                locationResult.forEach { it.join() }
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(50.dp),
                    enabled = buttonEnabled
                ) {
                    Text(text = "이 위치에서 다시 찾기")
                }
            }
        }
    }
}

suspend fun searchLocation(context: Context, query: String): MutableList<Address>? {
    return withContext(Dispatchers.IO) {
        val geocoder = Geocoder(context)
        return@withContext try {
            val addresses = geocoder.getFromLocationName(query, 10)
            addresses?.ifEmpty {
                null
            }
        } catch (e: Exception) {
            null
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

@Preview(showBackground = true)
@Composable
fun PreviewOnMap() {
    EmergencyRoomOnMapLayout(rememberNavController())
}