package com.example.find_emergencyroom_composable

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EmergencyRoomDetailLayout(
    dutyName: String?,
    roomCount: String?,
    dutyAddress: String?,
    dutyTel: String?,
    wgs84Lat: String?,
    wgs84Lon: String?,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.End
    ) {
        Spacer(modifier = Modifier.fillMaxHeight(0.1f))

        Image(
            painter = painterResource(R.drawable.ic_launcher_background),
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .clip(RectangleShape),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = dutyName ?: "",
                style = TextStyle(fontSize = 30.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.padding(5.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = "주소",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Gray,
                )
                Spacer(modifier = Modifier.padding(1.dp))
                Text(
                    text = dutyAddress ?: "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.padding(2.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.Call,
                    contentDescription = "주소",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Gray,
                )
                Spacer(modifier = Modifier.padding(1.dp))
                Text(
                    text = dutyTel ?: "",
                    style = TextStyle(
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier.clickable {
                        telDialog(context, dutyTel)
                    },
                )
            }
            Spacer(modifier = Modifier.padding(2.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "주소",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Gray,
                )
                Spacer(modifier = Modifier.padding(1.dp))
                Text(
                    text = "병상 수: ${roomCount ?: ""}",
                    style = TextStyle(fontSize = 20.sp),
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    navigator(
                        context,
                        dutyAddress ?: "",
                        wgs84Lat ?: "",
                        wgs84Lon ?: "",
                    )
                },
            ) {
                Text(text = "길찾기")
            }
        }
    }
}

fun telDialog(
    context: Context,
    dutyTel: String?,
) {
    val options = arrayOf("전화걸기", "복사하기")
    val builder = AlertDialog.Builder(context)
    builder.setTitle("전화번호")
        .setItems(options) { _, which ->
            when (which) {
                0 -> {
                    dutyTel?.let { tel ->
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:$tel")
                        }
                        context.startActivity(intent)
                    }
                }

                1 -> {
                    val clipboardManager =
                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("전화번호", dutyTel)
                    clipboardManager.setPrimaryClip(clip)
                }
            }
        }
        .show()
}

fun navigator(
    context: Context,
    dutyAddress: String,
    wgs84Lat: String,
    wgs84Lon: String,
) {
    val googleMapUri = "https://www.google.com/maps/dir/?api=1&" +
            "&destination=${wgs84Lat},${wgs84Lon}&"
    val naverMapUri = "nmap://route/public?" +
            "dlat=${wgs84Lat}&dlng=${wgs84Lon}&dname=${dutyAddress}&"
    val kakaoMapUrl = "kakaomap://route?" +
            "&ep=${wgs84Lat},${wgs84Lon}"

    val options = arrayOf("구글 지도", "네이버 지도", "카카오 지도")
    val builder = AlertDialog.Builder(context)
    builder.setTitle("길찾기 앱 선택")
        .setItems(options) { _, which ->
            when (which) {
                0 -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(googleMapUri))
                    intent.setPackage("com.google.android.apps.maps")
                    try {
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        downloadGoogleMapDialog(context)
                    }
                }

                1 -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(naverMapUri))
                    intent.setPackage("com.nhn.android.nmap")
                    try {
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        downloadNaverMapDialog(context)
                    }
                }

                2 -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(kakaoMapUrl))
                    intent.setPackage("net.daum.android.map")
                    try {
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        downloadKakaoMapDialog(context)
                    }
                }
            }
        }
        .show()
}

private fun downloadNaverMapDialog(context: Context) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle("네이버 지도 다운로드")
        .setMessage("네이버 지도가 설치되어 있지 않습니다.\n다운로드하시겠습니까?")
        .setPositiveButton("다운로드") { _, _ ->
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=com.nhn.android.nmap")
            )
            context.startActivity(intent)
        }
        .setNegativeButton("취소", null)
        .show()
}

private fun downloadGoogleMapDialog(context: Context) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle("구글 지도 다운로드")
        .setMessage("구글 지도가 설치되어 있지 않습니다.\n다운로드하시겠습니까?")
        .setPositiveButton("다운로드") { _, _ ->
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=com.google.android.apps.maps")
            )
            context.startActivity(intent)
        }
        .setNegativeButton("취소", null)
        .show()
}

private fun downloadKakaoMapDialog(context: Context) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle("카카오 지도 다운로드")
        .setMessage("카카오 지도가 설치되어 있지 않습니다.\n다운로드하시겠습니까?")
        .setPositiveButton("다운로드") { _, _ ->
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=net.daum.android.map")
            )
            context.startActivity(intent)
        }
        .setNegativeButton("취소", null)
        .show()
}

@Preview(showBackground = true)
@Composable
fun PreviewDetailLayout() {
    EmergencyRoomDetailLayout(
        dutyName = "dutyName dutyName dutyName",
        roomCount = "roomCount",
        dutyAddress = "dutyAddress dutyAddress dutyAddress ",
        dutyTel = "dutyTel",
        "",
        "",
    )
}