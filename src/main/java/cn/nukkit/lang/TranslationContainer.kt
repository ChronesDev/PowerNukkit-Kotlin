package cn.nukkit.lang

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class TranslationContainer : TextContainer, Cloneable {
    protected var params: Array<String>

    constructor(text: String) : this(text, *arrayOf<String>()) {}
    constructor(text: String, params: String) : super(text) {
        setParameters(arrayOf(params))
    }

    constructor(text: String, vararg params: String) : super(text) {
        setParameters(params)
    }

    fun getParameters(): Array<String> {
        return params
    }

    fun setParameters(params: Array<String>) {
        this.params = params
    }

    fun getParameter(i: Int): String? {
        return if (i >= 0 && i < params.size) params[i] else null
    }

    fun setParameter(i: Int, str: String) {
        if (i >= 0 && i < params.size) {
            params[i] = str
        }
    }

    @Override
    override fun clone(): TranslationContainer {
        return TranslationContainer(this.text, params.clone())
    }
}