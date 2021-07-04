package cn.nukkit.block

import kotlin.jvm.JvmOverloads
import kotlin.Throws
import cn.nukkit.block.BlockRedstoneComparator.Mode

class BlockBlueIce : BlockIcePacked() {
    @get:Override
    override val id: Int
        get() = BLUE_ICE

    @get:Override
    override val name: String
        get() = "Blue Ice"

    @get:Override
    override val frictionFactor: Double
        get() = 0.989

    @get:Override
    override val hardness: Double
        get() = 2.8

    @get:Override
    override val resistance: Double
        get() = 14

    @get:Override
    override val isTransparent: Boolean
        get() = false

    @get:Override
    override val lightLevel: Int
        get() = 4
}