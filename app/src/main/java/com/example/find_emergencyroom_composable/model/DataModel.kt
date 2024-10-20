package com.example.find_emergencyroom_composable.model

data class EmergencyRoom(
    val phId: String,
    val dutyName: String,
    val dutyTel: String,
    val roomCount: String,
)

data class EmergencyRoomInformation(
    val phId: String,
    val dutyName: String,
    val dutyTel: String,
    val roomCount: String,
    val wgs84Lon: String,
    val wgs84Lat: String,
    val dutyAddress: String,
)