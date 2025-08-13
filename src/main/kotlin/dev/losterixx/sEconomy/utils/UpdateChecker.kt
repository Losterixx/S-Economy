package dev.losterixx.sEconomy.utils

import java.net.HttpURLConnection
import java.net.URL

object UpdateChecker {

    fun getLatestGitHubRelease(user: String, repo: String): String? {
        return try {
            val url = URL("https://api.github.com/repos/$user/$repo/releases/latest")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("User-Agent", "Mozilla/5.0")

            if (conn.responseCode == 200) {
                val json = conn.inputStream.bufferedReader().readText()
                Regex("\"tag_name\":\"(.*?)\"").find(json)?.groupValues?.get(1)?.replace("v", "")
            } else null
        } catch (e: Exception) {
            null
        }
    }

}