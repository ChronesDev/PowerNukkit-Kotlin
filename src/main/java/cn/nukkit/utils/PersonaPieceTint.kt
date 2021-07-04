package cn.nukkit.utils

import com.google.common.collect.ImmutableList

@ToString
class PersonaPieceTint(val pieceType: String, colors: List<String?>?) {
    val colors: ImmutableList<String>

    init {
        this.colors = ImmutableList.copyOf(colors)
    }
}