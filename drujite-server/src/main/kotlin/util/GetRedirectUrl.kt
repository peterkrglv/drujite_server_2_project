package ru.drujite.util

import java.net.HttpURLConnection
import java.net.URL

fun getRedirectedUrl(url: String): String? {
    try {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.instanceFollowRedirects = false // Отключаем автоматическое следование редиректам
        connection.connect()
        val redirectedUrl = connection.getHeaderField("Location") // Получаем заголовок Location
        connection.disconnect()
        return redirectedUrl
    }
    catch (e: Exception) {
        return url
    }
}

fun main() {
    val url = "qr.fm/bMDxkP" // Замените на ваш URL
    val redirectedUrl = getRedirectedUrl(url)
    println("Redirected URL: $redirectedUrl")
}