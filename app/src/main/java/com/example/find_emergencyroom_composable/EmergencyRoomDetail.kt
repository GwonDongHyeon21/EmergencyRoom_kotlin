package com.example.find_emergencyroom_composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun EmergencyRoomDetailLayout(
    dutyName: String?,
    roomCount: String?,
    dutyAddress: String?,
    dutyTel: String?,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.fillMaxHeight(0.1f))

        Row(
            modifier = Modifier.fillMaxHeight(0.3f),
        ) {
            //정사각형 모양
            //Image
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f),
                color = Color.LightGray,
                shape = RectangleShape
            ){}

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 20.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.End
            ) {
                Column {
                    Text(text = dutyName ?: "")
                    Text(text = roomCount ?: "")
                    Text(text = dutyAddress ?: "")
                    Text(text = dutyTel ?: "")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDetailLayout() {
    EmergencyRoomDetailLayout(
        dutyName = "dutyName",
        roomCount = "roomCount",
        dutyAddress = "dutyAddress",
        dutyTel = "dutyTel"
    )
}