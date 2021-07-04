package cn.nukkit.form.element

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

class ElementButtonImageData(type: String, data: String) {
    var type: String
    var data: String

    companion object {
        const val IMAGE_DATA_TYPE_PATH = "path"
        const val IMAGE_DATA_TYPE_URL = "url"
    }

    init {
        if (!type.equals(IMAGE_DATA_TYPE_URL) && !type.equals(IMAGE_DATA_TYPE_PATH)) return
        this.type = type
        this.data = data
    }
}