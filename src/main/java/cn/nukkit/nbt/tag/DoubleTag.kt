package cn.nukkit.nbt.tag

import cn.nukkit.nbt.stream.NBTInputStream

class DoubleTag : NumberTag<Double?> {
    override var data = 0.0
    @Override
    fun getData(): Double {
        return data
    }

    @Override
    fun setData(data: Double?) {
        this.data = data ?: 0
    }

    constructor(name: String?) : super(name) {}
    constructor(name: String?, data: Double) : super(name) {
        this.data = data
    }

    @Override
    @Throws(IOException::class)
    override fun write(dos: NBTOutputStream) {
        dos.writeDouble(data)
    }

    @Override
    @Throws(IOException::class)
    fun load(dis: NBTInputStream) {
        data = dis.readDouble()
    }

    @Override
    override fun parseValue(): Double {
        return data
    }

    override val id: Byte
        @Override get() = TAG_Double

    @Override
    override fun toString(): String {
        return "DoubleTag " + this.getName().toString() + " (data: " + data.toString() + ")"
    }

    @Override
    override fun copy(): Tag {
        return DoubleTag(getName(), data)
    }

    @Override
    override fun equals(obj: Object): Boolean {
        if (super.equals(obj)) {
            val o = obj as DoubleTag
            return data == o.data
        }
        return false
    }
}