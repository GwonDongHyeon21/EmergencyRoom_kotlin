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

//class EmergencyRoomViewModel : ViewModel() {
//    private val _selectedEmergencyRoom = MutableLiveData<EmergencyRoomInformation?>()
//    val selectedEmergencyRoom: LiveData<EmergencyRoomInformation?> = _selectedEmergencyRoom
//
//    fun selectEmergencyRoom(emergencyRoom: EmergencyRoomInformation) {
//        _selectedEmergencyRoom.value = emergencyRoom
//    }
//}
