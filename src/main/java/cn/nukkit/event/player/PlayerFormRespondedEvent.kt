package cn.nukkit.event.player

import cn.nukkit.Player

class PlayerFormRespondedEvent(player: Player?, formID: Int, window: FormWindow) : PlayerEvent() {
    var formID: Int
        protected set
    protected var window: FormWindow
    protected var closed = false
    fun getWindow(): FormWindow {
        return window
    }

    /**
     * Can be null if player closed the window instead of submitting it
     *
     * @return response
     */
    val response: FormResponse
        get() = window.getResponse()

    /**
     * Defines if player closed the window or submitted it
     *
     * @return form closed
     */
    fun wasClosed(): Boolean {
        return window.wasClosed()
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        player = player
        this.formID = formID
        this.window = window
    }
}