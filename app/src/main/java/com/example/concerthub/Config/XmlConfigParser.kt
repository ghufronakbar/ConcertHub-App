package com.example.concerthub.Config

import android.content.Context
import com.example.concerthub.R
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException


class XmlConfigParser(private val context: Context) {

    fun getIpConfig(): String? {
        val parser = context.resources.getXml(R.xml.network_security_config)
        var eventType = parser.eventType
        var ip: String? = null

        try {
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.name == "domain") {
                    ip = parser.nextText()
                    break
                }
                eventType = parser.next()
            }
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            parser.close()
        }

        return ip
    }
}
