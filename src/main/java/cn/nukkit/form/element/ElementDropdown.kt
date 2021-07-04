package cn.nukkit.form.element

import com.google.gson.annotations.SerializedName

class ElementDropdown(text: String, options: List<String?>, defaultOption: Int) : Element() {
    private val type = "dropdown" //This variable is used for JSON import operations. Do NOT delete :) -- @Snake1999
    var text = ""
    val options: List<String?>

    @SerializedName("default")
    private var defaultOptionIndex = 0

    constructor(text: String) : this(text, ArrayList()) {}
    constructor(text: String, options: List<String?>) : this(text, options, 0) {}

    fun getDefaultOptionIndex(): Int {
        return defaultOptionIndex
    }

    fun setDefaultOptionIndex(index: Int) {
        if (index >= options.size()) return
        defaultOptionIndex = index
    }

    fun addOption(option: String?) {
        addOption(option, false)
    }

    fun addOption(option: String?, isDefault: Boolean) {
        options.add(option)
        if (isDefault) defaultOptionIndex = options.size() - 1
    }

    init {
        this.text = text
        this.options = options
        defaultOptionIndex = defaultOption
    }
}