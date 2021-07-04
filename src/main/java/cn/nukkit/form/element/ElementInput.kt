package cn.nukkit.form.element

import com.google.gson.annotations.SerializedName

class ElementInput(text: String, placeholder: String, defaultText: String) : Element() {
    private val type = "input" //This variable is used for JSON import operations. Do NOT delete :) -- @Snake1999
    var text = ""
    var placeHolder = ""

    @SerializedName("default")
    var defaultText = ""

    constructor(text: String) : this(text, "") {}
    constructor(text: String, placeholder: String) : this(text, placeholder, "") {}

    init {
        this.text = text
        placeHolder = placeholder
        this.defaultText = defaultText
    }
}