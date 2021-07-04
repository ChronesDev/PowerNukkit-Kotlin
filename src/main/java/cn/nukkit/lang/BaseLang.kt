package cn.nukkit.lang

import io.netty.util.internal.EmptyArrays

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
class BaseLang(lang: String, path: String?, fallback: String) {
    protected val langName: String
    protected var lang: Map<String, String> = HashMap()
    protected var fallbackLang: Map<String, String>? = HashMap()

    constructor(lang: String) : this(lang, null) {}
    constructor(lang: String, path: String?) : this(lang, path, FALLBACK_LANGUAGE) {}

    fun getLangMap(): Map<String, String> {
        return lang
    }

    fun getFallbackLangMap(): Map<String, String>? {
        return fallbackLang
    }

    fun getName(): String? {
        return this["language.name"]
    }

    fun getLang(): String {
        return langName
    }

    protected fun loadLang(path: String?): Map<String, String>? {
        try {
            val file = File(path)
            if (!file.exists() || file.isDirectory()) {
                throw FileNotFoundException()
            }
            FileInputStream(file).use { stream -> return parseLang(BufferedReader(InputStreamReader(stream, StandardCharsets.UTF_8))) }
        } catch (e: IOException) {
            log.fatal("Failed to load language at {}", path, e)
            return null
        }
    }

    protected fun loadLang(stream: InputStream?): Map<String, String>? {
        return try {
            parseLang(BufferedReader(InputStreamReader(stream, StandardCharsets.UTF_8)))
        } catch (e: IOException) {
            log.error("Failed to parse the language input stream", e)
            null
        }
    }

    @Throws(IOException::class)
    private fun parseLang(reader: BufferedReader): Map<String, String> {
        val d: Map<String, String> = HashMap()
        var line: String
        while (reader.readLine().also { line = it } != null) {
            line = line.trim()
            if (line.isEmpty() || line.charAt(0) === '#') {
                continue
            }
            val t: Array<String> = line.split("=", 2)
            if (t.size < 2) {
                continue
            }
            val key = t[0]
            var value = t[1]
            if (value.length() > 1 && value.charAt(0) === '"' && value.charAt(value.length() - 1) === '"') {
                value = value.substring(1, value.length() - 1).replace("\\\"", "\"").replace("\\\\", "\\")
            }
            if (value.isEmpty()) {
                continue
            }
            d.put(key, value)
        }
        return d
    }

    fun translateString(str: String): String? {
        return this.translateString(str, arrayOf(), null)
    }

    fun translateString(str: String, vararg params: String?): String? {
        return if (params != null) {
            this.translateString(str, params, null)
        } else this.translateString(str, EmptyArrays.EMPTY_STRINGS, null)
    }

    fun translateString(str: String, vararg params: Object?): String? {
        if (params != null) {
            val paramsToString = arrayOfNulls<String>(params.size)
            for (i in 0 until params.size) {
                paramsToString[i] = Objects.toString(params[i])
            }
            return this.translateString(str, paramsToString, null)
        }
        return this.translateString(str, EmptyArrays.EMPTY_STRINGS, null)
    }

    fun translateString(str: String, param: String?, onlyPrefix: String?): String? {
        return this.translateString(str, arrayOf(param), onlyPrefix)
    }

    fun translateString(str: String, params: Array<String?>, onlyPrefix: String?): String? {
        var baseText = this[str]
        baseText = this.parseTranslation(if (baseText != null && (onlyPrefix == null || str.indexOf(onlyPrefix) === 0)) baseText else str, onlyPrefix)
        for (i in params.indices) {
            baseText = baseText.replace("{%$i}", this.parseTranslation(String.valueOf(params[i])))
        }
        return baseText
    }

    fun translate(c: TextContainer): String? {
        var baseText: String? = this.parseTranslation(c.getText())
        if (c is TranslationContainer) {
            baseText = internalGet(c.getText())
            baseText = this.parseTranslation(baseText ?: c.getText())
            for (i in 0 until (c as TranslationContainer).getParameters().length) {
                baseText = baseText.replace("{%$i}", this.parseTranslation((c as TranslationContainer).getParameters().get(i)))
            }
        }
        return baseText
    }

    fun internalGet(id: String): String? {
        if (lang.containsKey(id)) {
            return lang[id]
        } else if (fallbackLang!!.containsKey(id)) {
            return fallbackLang!![id]
        }
        return null
    }

    operator fun get(id: String): String? {
        if (lang.containsKey(id)) {
            return lang[id]
        } else if (fallbackLang!!.containsKey(id)) {
            return fallbackLang!![id]
        }
        return id
    }

    protected fun parseTranslation(text: String): String {
        return this.parseTranslation(text, null)
    }

    protected fun parseTranslation(text: String, onlyPrefix: String?): String {
        var text = text
        val newString = StringBuilder()
        text = String.valueOf(text)
        var replaceString: String? = null
        val len: Int = text.length()
        for (i in 0 until len) {
            val c: Char = text.charAt(i)
            if (replaceString != null) {
                val ord = c.toInt()
                if (ord >= 0x30 && ord <= 0x39 // 0-9
                        || ord >= 0x41 && ord <= 0x5a // A-Z
                        || ord >= 0x61 && ord <= 0x7a || // a-z
                        c == '.' || c == '-') {
                    replaceString += String.valueOf(c)
                } else {
                    val t = internalGet(replaceString.substring(1))
                    if (t != null && (onlyPrefix == null || replaceString.indexOf(onlyPrefix) === 1)) {
                        newString.append(t)
                    } else {
                        newString.append(replaceString)
                    }
                    replaceString = null
                    if (c == '%') {
                        replaceString = String.valueOf(c)
                    } else {
                        newString.append(c)
                    }
                }
            } else if (c == '%') {
                replaceString = String.valueOf(c)
            } else {
                newString.append(c)
            }
        }
        if (replaceString != null) {
            val t = internalGet(replaceString.substring(1))
            if (t != null && (onlyPrefix == null || replaceString.indexOf(onlyPrefix) === 1)) {
                newString.append(t)
            } else {
                newString.append(replaceString)
            }
        }
        return newString.toString()
    }

    companion object {
        const val FALLBACK_LANGUAGE = "eng"
    }

    init {
        var path = path
        langName = lang.toLowerCase()
        val useFallback = !lang.equals(fallback)
        if (path == null) {
            path = "lang/"
            this.lang = this.loadLang(this.getClass().getClassLoader().getResourceAsStream(path + langName + "/lang.ini"))
            if (useFallback) fallbackLang = this.loadLang(this.getClass().getClassLoader().getResourceAsStream("$path$fallback/lang.ini"))
        } else {
            this.lang = this.loadLang(path + langName + "/lang.ini")
            if (useFallback) fallbackLang = this.loadLang("$path$fallback/lang.ini")
        }
        if (fallbackLang == null) fallbackLang = this.lang
    }
}