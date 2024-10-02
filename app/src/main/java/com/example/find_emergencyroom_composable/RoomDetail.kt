package com.example.find_emergencyroom_composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.find_emergencyroom_composable.api.findLocation
import com.example.find_emergencyroom_composable.model.EmergencyRoom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

var location = mutableListOf<String>()

@Composable
fun RoomOnMap(room: EmergencyRoom) {
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        emergencyRoomList.clear()
        coroutineScope.launch(Dispatchers.IO) {
            findLocation(room.phId)
            isLoading = false
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDetailScreen() {
    RoomOnMap(EmergencyRoom("", "", ""))
}