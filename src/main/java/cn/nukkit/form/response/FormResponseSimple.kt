package cn.nukkit.form.response

import cn.nukkit.form.element.ElementButton

class FormResponseSimple(val clickedButtonId: Int, clickedButton: ElementButton) : FormResponse() {
    private val clickedButton: ElementButton
    fun getClickedButton(): ElementButton {
        return clickedButton
    }

    init {
        this.clickedButton = clickedButton
    }
}