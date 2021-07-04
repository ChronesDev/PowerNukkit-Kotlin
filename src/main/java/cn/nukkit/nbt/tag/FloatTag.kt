package cn.nukkit.nbt.tag

import cn.nukkit.nbt.stream.NBTInputStream

class FloatTag : NumberTag<Float?> {
    override var data = 0f
    @Override
    fun getData(): Float {
        return data
    }

    @Override
    fun setData(data: Float?) {
        this.data = data ?: 0
    }

    constructor(name: String?) : super(name) {}
    constructor(name: String?, data: Float) : super(name) {
        this.data = data
    }

    @Override
    @Throws(IOException::class)
    override fun write(dos: NBTOutputStream) {
        dos.writeFloat(data)
    }

    @Override
    @Throws(IOException::class)
    fun load(dis: NBTInputStream) {
        data = dis.readFloat()
    }

    @Override
    override fun parseValue(): Float {
        return data
    }

    override val id: Byte
        @Override get() = TAG_Float

    @Override
    override fun toString(): String {
        return "FloatTag " + this.getName().toString() + " (data: " + data.toString() + ")"
    }

    @Override
    override fun copy(): Tag {
        return FloatTag(getName(), data)
    }

    @Override
    override fun equals(obj: Object): Boolean {
        if (super.equals(obj)) {
            val o = obj as FloatTag
            return data == o.data
        }
        return false
    }
}