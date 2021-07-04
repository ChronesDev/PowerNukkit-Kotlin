package cn.nukkit.form.window

import cn.nukkit.form.element.ElementButton

class FormWindowSimple(title: String, content: String, buttons: List<ElementButton>) : FormWindow() {
    private val type = "form" //This variable is used for JSON import operations. Do NOT delete :) -- @Snake1999
    var title = ""
    var content = ""
    private val buttons: List<ElementButton>
    override var response: FormResponseSimple? = null
        set(data) {
            if (data.equals("null")) {
                this.closed = true
                return
            }
            val buttonID: Int
            buttonID = try {
                Integer.parseInt(data)
            } catch (e: Exception) {
                return
            }
            if (buttonID >= buttons.size()) {
                field = FormResponseSimple(buttonID, null)
                return
            }
            field = FormResponseSimple(buttonID, buttons[buttonID])
        }

    constructor(title: String, content: String) : this(title, content, ArrayList()) {}

    fun getButtons(): List<ElementButton> {
        return buttons
    }

    fun addButton(button: ElementButton?) {
        buttons.add(button)
    }

    init {
        this.title = title
        this.content = content
        this.buttons = buttons
    }
}