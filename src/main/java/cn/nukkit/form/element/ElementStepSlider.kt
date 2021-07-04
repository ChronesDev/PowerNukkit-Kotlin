package cn.nukkit.form.element

import com.google.gson.annotations.SerializedName

class ElementStepSlider(text: String, steps: List<String?>, defaultStep: Int) : Element() {
    private val type = "step_slider" //This variable is used for JSON import operations. Do NOT delete :) -- @Snake1999
    var text = ""
    val steps: List<String?>

    @SerializedName("default")
    var defaultStepIndex = 0
        private set

    constructor(text: String) : this(text, ArrayList()) {}
    constructor(text: String, steps: List<String?>) : this(text, steps, 0) {}

    fun setDefaultOptionIndex(index: Int) {
        if (index >= steps.size()) return
        defaultStepIndex = index
    }

    fun addStep(step: String?) {
        addStep(step, false)
    }

    fun addStep(step: String?, isDefault: Boolean) {
        steps.add(step)
        if (isDefault) defaultStepIndex = steps.size() - 1
    }

    init {
        this.text = text
        this.steps = steps
        defaultStepIndex = defaultStep
    }
}