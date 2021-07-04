package cn.nukkit.utils

import java.io.*

object HastebinUtility {
    const val BIN_URL = "https://hastebin.com/documents"
    const val USER_AGENT = "Mozilla/5.0"
    val PATTERN: Pattern = Pattern.compile("\\{\"key\":\"([\\S\\s]*)\"}")
    @Throws(IOException::class)
    fun upload(string: String): String {
        val url = URL(BIN_URL)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.setRequestMethod("POST")
        connection.setRequestProperty("User-Agent", USER_AGENT)
        connection.setDoOutput(true)
        DataOutputStream(connection.getOutputStream()).use { outputStream ->
            outputStream.write(string.getBytes())
            outputStream.flush()
        }
        var response: StringBuilder
        BufferedReader(InputStreamReader(connection.getInputStream())).use { `in` ->
            response = StringBuilder()
            var inputLine: String?
            while (`in`.readLine().also { inputLine = it } != null) {
                response.append(inputLine)
            }
        }
        val matcher: Matcher = PATTERN.matcher(response.toString())
        return if (matcher.matches()) {
            "https://hastebin.com/" + matcher.group(1)
        } else {
            throw RuntimeException("Couldn't read response!")
        }
    }

    @Throws(IOException::class)
    fun upload(file: File?): String {
        val content = StringBuilder()
        val lines: List<String> = ArrayList()
        BufferedReader(FileReader(file)).use { reader ->
            var line: String
            val i = 0
            while (reader.readLine().also { line = it } != null) {
                if (!line.contains("rcon.password=")) {
                    lines.add(line)
                }
            }
        }
        for (i in Math.max(0, lines.size() - 1000) until lines.size()) {
            content.append(lines[i]).append("\n")
        }
        return upload(content.toString())
    }
}