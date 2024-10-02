package com.example.find_emergencyroom_composable.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EmergencyRoom(
    val dutyName: String,
    val dutyTel: String,
    val phId: String,
) : Parcelable