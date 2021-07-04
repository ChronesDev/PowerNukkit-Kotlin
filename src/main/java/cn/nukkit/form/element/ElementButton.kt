package cn.nukkit.form.element

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

class ElementButton {
    var text = ""
    private var image: ElementButtonImageData? = null

    constructor(text: String) {
        this.text = text
    }

    constructor(text: String, image: ElementButtonImageData) {
        this.text = text
        if (!image.getData().isEmpty() && !image.getType().isEmpty()) this.image = image
    }

    fun getImage(): ElementButtonImageData? {
        return image
    }

    fun addImage(image: ElementButtonImageData) {
        if (!image.getData().isEmpty() && !image.getType().isEmpty()) this.image = image
    }
}