package com.example.concerthub

import com.example.concerthub.Config.XmlConfigParser


class BaseUrl {
    companion object {
        private val context = MainActivity.applicationContext()
        val xmlConfigParser = XmlConfigParser(context)
        var IpConfig = xmlConfigParser.getIpConfig()
        var URL: String = "http://$IpConfig:2001"

    }
}
