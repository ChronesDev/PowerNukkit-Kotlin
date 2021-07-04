package cn.nukkit.nbt.tag

import cn.nukkit.nbt.stream.NBTInputStream

class IntTag : NumberTag<Integer?> {
    override var data = 0
    @Override
    fun getData(): Integer {
        return data
    }

    @Override
    fun setData(data: Integer?) {
        this.data = if (data == null) 0 else data
    }

    constructor(name: String?) : super(name) {}
    constructor(name: String?, data: Int) : super(name) {
        this.data = data
    }

    @Override
    @Throws(IOException::class)
    override fun write(dos: NBTOutputStream) {
        dos.writeInt(data)
    }

    @Override
    @Throws(IOException::class)
    fun load(dis: NBTInputStream) {
        data = dis.readInt()
    }

    @Override
    override fun parseValue(): Integer {
        return data
    }

    override val id: Byte
        @Override get() = TAG_Int

    @Override
    override fun toString(): String {
        return "IntTag " + this.getName().toString() + "(data: " + data.toString() + ")"
    }

    @Override
    override fun copy(): Tag {
        return IntTag(getName(), data)
    }

    @Override
    override fun equals(obj: Object): Boolean {
        if (super.equals(obj)) {
            val o = obj as IntTag
            return data == o.data
        }
        return false
    }
}