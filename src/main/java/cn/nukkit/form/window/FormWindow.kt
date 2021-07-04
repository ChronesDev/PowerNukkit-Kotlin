package cn.nukkit.form.window

import cn.nukkit.form.response.FormResponse

abstract class FormWindow {
    protected var closed = false
    val jSONData: String
        get() = GSON.toJson(this)
    abstract var response: cn.nukkit.form.response.FormResponse?
    fun wasClosed(): Boolean {
        return closed
    }

    companion object {
        private val GSON: Gson = Gson()
    }
}