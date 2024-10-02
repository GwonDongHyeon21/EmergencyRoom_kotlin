package com.example.find_emergencyroom_composable.api

import android.util.Log
import android.util.Xml
import com.example.find_emergencyroom_composable.location
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.StringReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

fun findLocation(phId: String) {
    try {
        val urlBuilder =
            StringBuilder("https://apis.data.go.kr/B552657/ErmctInfoInqireService/getEgytBassInfoInqire")
        urlBuilder.append(
            "?" + URLEncoder.encode(
                "serviceKey",
                "UTF-8"
            ) + "=***REMOVED***"
        ) /*Service Key*/
        urlBuilder.append(
            "&" + URLEncoder.encode(
                "HPID",
                "UTF-8"
            ) + "=" + URLEncoder.encode(phId, "UTF-8")
        ) /*기관 ID*/
        urlBuilder.append(
            "&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(
                "1",
                "UTF-8"
            )
        ) /*페이지 번호*/
        urlBuilder.append(
            "&" + URLEncoder.encode(
                "numOfRows",
                "UTF-8"
            ) + "=" + URLEncoder.encode("10", "UTF-8")
        ) /*목록 건수*/

        val url = URL(urlBuilder.toString())
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.setRequestProperty("Content-type", "application/json")

        val rd: BufferedReader = if (conn.responseCode in 200..300) {
            BufferedReader(InputStreamReader(conn.inputStream))
        } else {
            BufferedReader(InputStreamReader(conn.errorStream))
        }

        val sb = StringBuilder()
        var line: String?
        while (rd.readLine().also { line = it } != null) {
            sb.append(line)
        }
        rd.close()
        conn.disconnect()

        parseResponse2(sb.toString())
    } catch (e: Exception) {
        Log.e("test", "Error", e)
    }
}

private fun parseResponse2(xml: String) {
    try {
        val parser: XmlPullParser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(StringReader(xml))
        parser.nextTag()

        var eventType = parser.eventType
        var wgs84Lon: String? = null
        var wgs84Lat: String? = null
        var dutyAddress: String? = null

        while (eventType != XmlPullParser.END_DOCUMENT) {
            val tagName = parser.name

            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (tagName) {
                        "item" -> {
                            wgs84Lon = null
                            wgs84Lat = null
                            dutyAddress = null
                        }

                        "wgs84Lon" -> wgs84Lon = parser.nextText()
                        "wgs84Lat" -> wgs84Lat = parser.nextText()
                        "dutyAddr" -> dutyAddress = parser.nextText()
                    }
                }

                XmlPullParser.END_TAG -> {
                    if (tagName == "item") {
                        location.clear()
                        Log.d("test", "경도: $wgs84Lon, 위도: $wgs84Lat, 주소: $dutyAddress")
                        location.add(wgs84Lon.toString())
                        location.add(wgs84Lat.toString())
                        location.add(dutyAddress.toString())
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