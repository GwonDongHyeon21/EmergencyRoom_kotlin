//package com.example.find_emergencyroom_composable
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import androidx.navigation.compose.rememberNavController
//import com.example.find_emergencyroom_composable.api.emergencyRoomApi
//import com.example.find_emergencyroom_composable.model.EmergencyRoom
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//
//var emergencyRoomList = mutableListOf<EmergencyRoom>()
//
//@Composable
//fun EmergencyRoomListLayout(navController: NavController) {
//    val coroutineScope = rememberCoroutineScope()
//    var isLoading by remember { mutableStateOf(true) }
//
//    LaunchedEffect(Unit) {
//        emergencyRoomList.clear()
//        coroutineScope.launch(Dispatchers.IO) {
//            emergencyRoomApi()
//            isLoading = false
//        }
//    }
//
//    Row(
//        modifier = Modifier
//            .padding(20.dp)
//            .fillMaxSize(),
//        horizontalArrangement = Arrangement.Center
//    ) {
//        Text(
//            text = "응급실 리스트",
//            style = TextStyle(fontSize = 20.sp)
//        )
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(20.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center,
//    ) {
//        if (isLoading) {
//            Text("Loading...")
//        } else {
//            LazyColumn(
//                modifier = Modifier
//                    .padding(top = 40.dp)
//                    .fillMaxSize(),
//                verticalArrangement = Arrangement.spacedBy(5.dp)
//            ) {
//                items(emergencyRoomList) { room ->
//                    ListLayout(navController, room)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun ListLayout(navController: NavController, room: EmergencyRoom) {
//    Surface(
//        shape = RoundedCornerShape(10.dp),
//        shadowElevation = 5.dp,
//        modifier = Modifier
//            .clickable {
//                navController.navigate("roomOnMap")
//            }
//            .fillMaxWidth(),
//    ) {
//        Text(
//            text = room.dutyName,
//            style = TextStyle(fontSize = 20.sp),
//            modifier = Modifier
//                .padding(start = 10.dp, top = 5.dp, bottom = 5.dp)
//        )
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun EmergencyRoomListPreview() {
//    EmergencyRoomListLayout(rememberNavController())
//}