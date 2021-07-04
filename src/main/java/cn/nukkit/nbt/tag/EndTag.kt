package cn.nukkit.nbt.tag

import cn.nukkit.nbt.stream.NBTInputStream

class EndTag : Tag(null) {
    @Override
    @Throws(IOException::class)
    override fun load(dis: NBTInputStream?) {
    }

    @Override
    @Throws(IOException::class)
    override fun write(dos: NBTOutputStream?) {
    }

    override val id: Byte
        @Override get() = TAG_End

    @Override
    override fun toString(): String {
        return "EndTag"
    }

    @Override
    override fun copy(): Tag {
        return EndTag()
    }

    @Override
    override fun parseValue(): Object? {
        return null
    }
}