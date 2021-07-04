package cn.nukkit.nbt.tag

import cn.nukkit.nbt.stream.NBTInputStream

class LongTag : NumberTag<Long?> {
    override var data: Long = 0
    @Override
    fun getData(): Long {
        return data
    }

    @Override
    fun setData(data: Long?) {
        this.data = data ?: 0
    }

    constructor(name: String?) : super(name) {}
    constructor(name: String?, data: Long) : super(name) {
        this.data = data
    }

    @Override
    @Throws(IOException::class)
    override fun write(dos: NBTOutputStream) {
        dos.writeLong(data)
    }

    @Override
    @Throws(IOException::class)
    fun load(dis: NBTInputStream) {
        data = dis.readLong()
    }

    @Override
    override fun parseValue(): Long {
        return data
    }

    override val id: Byte
        @Override get() = TAG_Long

    @Override
    override fun toString(): String {
        return "LongTag " + this.getName().toString() + " (data:" + data.toString() + ")"
    }

    @Override
    override fun copy(): Tag {
        return LongTag(getName(), data)
    }

    @Override
    override fun equals(obj: Object): Boolean {
        if (super.equals(obj)) {
            val o = obj as LongTag
            return data == o.data
        }
        return false
    }
}