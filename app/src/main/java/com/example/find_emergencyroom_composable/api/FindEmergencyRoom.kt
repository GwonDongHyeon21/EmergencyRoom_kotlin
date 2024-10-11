package com.example.find_emergencyroom_composable.api

import android.util.Log
import android.util.Xml
import com.example.find_emergencyroom_composable.BuildConfig
import com.example.find_emergencyroom_composable.emergencyRoomList
import com.example.find_emergencyroom_composable.model.EmergencyRoom
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.StringReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

fun emergencyRoomApi(query1: String, query2: String) {
    try {
        val urlBuilder =
            StringBuilder("https://apis.data.go.kr/B552657/ErmctInfoInqireService/getEmrrmRltmUsefulSckbdInfoInqire")
        urlBuilder.append(
            "?" + URLEncoder.encode(
                "serviceKey",
                "UTF-8"
            ) + BuildConfig.EMERGENCY_ROOM_API
        ) /*Service Key*/
        urlBuilder.append(
            "&" + URLEncoder.encode("STAGE1", "UTF-8") + "=" + URLEncoder.encode(
                query1,
                "UTF-8"
            )
        ) /*주소(시도)*/
        urlBuilder.append(
            "&" + URLEncoder.encode("STAGE2", "UTF-8") + "=" + URLEncoder.encode(
                query2,
                "UTF-8"
            )
        ) /*주소(시군구)*/
        urlBuilder.append(
            "&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(
                "",
                "UTF-8"
            )
        ) /*페이지 번호*/
        urlBuilder.append(
            "&" + URLEncoder.encode(
                "numOfRows",
                "UTF-8"
            ) + "=" + URLEncoder.encode("411", "UTF-8")
        ) /*목록 건수*/

        val url = URL(urlBuilder.toString())
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.setRequestProperty("Content-type", "application/json")

        val rd = BufferedReader(InputStreamReader(conn.inputStream))
        val sb = StringBuilder()
        var line: String?
        while (rd.readLine().also { line = it } != null) {
            sb.append(line)
        }
        rd.close()
        conn.disconnect()

        parseResponse(sb.toString())
    } catch (e: Exception) {
        Log.e("test", "Error", e)
    }
}

private fun parseResponse(xml: String) {
    try {
        val parser: XmlPullParser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(StringReader(xml))
        parser.nextTag()

        var eventType = parser.eventType
        var dutyName: String? = null
        var dutyTel: String? = null
        var phId: String? = null
        var roomCount: String? = null

        while (eventType != XmlPullParser.END_DOCUMENT) {
            val tagName = parser.name

            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (tagName) {
                        "item" -> {
                            dutyName = null
                            dutyTel = null
                            phId = null
                        }

                        "dutyName" -> dutyName = parser.nextText()
                        "dutyTel3" -> dutyTel = parser.nextText()
                        "hpid" -> phId = parser.nextText()
                        "hvec" -> roomCount = parser.nextText()
                    }
                }

                XmlPullParser.END_TAG -> {
                    if (tagName == "item") {
                        if ((roomCount?.toInt() ?: 0) > 0)
                            emergencyRoomList.add(
                                EmergencyRoom(
                                    phId.toString(),
                                    dutyName.toString(),
                                    dutyTel.toString(),
                                    roomCount.toString(),
                                )
                            )
                    }
                }
            }
            eventType = parser.next()
        }
    } catch (e: XmlPullParserException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}