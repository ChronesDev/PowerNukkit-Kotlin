package cn.nukkit.lang

import lombok.extern.log4j.Log4j2

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
class TextContainer(protected var text: String) : Cloneable {
    fun setText(text: String) {
        this.text = text
    }

    fun getText(): String {
        return text
    }

    @Override
    override fun toString(): String {
        return getText()
    }

    @Override
    fun clone(): TextContainer? {
        try {
            return super.clone() as TextContainer?
        } catch (e: CloneNotSupportedException) {
            log.error("Failed to clone the text container {}", this.toString(), e)
        }
        return null
    }
}