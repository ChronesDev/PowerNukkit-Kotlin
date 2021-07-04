package cn.nukkit.form.element

import com.google.gson.annotations.SerializedName

class ElementSlider(text: String, min: Float, max: Float, step: Int, defaultValue: Float) : Element() {
    private val type = "slider" //This variable is used for JSON import operations. Do NOT delete :) -- @Snake1999
    var text = ""
    var min = 0f
    var max = 100f
    var step = 0

    @SerializedName("default")
    var defaultValue = 0f

    constructor(text: String, min: Float, max: Float) : this(text, min, max, -1) {}
    constructor(text: String, min: Float, max: Float, step: Int) : this(text, min, max, step, -1f) {}

    init {
        this.text = text
        this.min = Math.max(min, 0f)
        this.max = Math.max(max, this.min)
        if (step.toFloat() != -1f && step > 0) this.step = step
        if (defaultValue != -1f) this.defaultValue = defaultValue
    }
}