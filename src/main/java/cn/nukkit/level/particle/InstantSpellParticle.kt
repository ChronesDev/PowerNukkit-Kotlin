package cn.nukkit.level.particle

import cn.nukkit.math.Vector3

/**
 * @author xtypr
 * @since 2016/1/4
 */
class InstantSpellParticle : SpellParticle {
    protected override var data = 0

    constructor(pos: Vector3?) : this(pos, 0) {}
    constructor(pos: Vector3?, data: Int) : super(pos, data) {}
    constructor(pos: Vector3?, blockColor: BlockColor) : this(pos, blockColor.getRed(), blockColor.getGreen(), blockColor.getBlue()) {
        //alpha is ignored
    }

    constructor(pos: Vector3?, r: Int, g: Int, b: Int) : super(pos, r, g, b, 0x01) {
        //this 0x01 is the only difference between instant spell and non-instant one
    }
}