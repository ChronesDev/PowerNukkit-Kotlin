package cn.nukkit.nbt.tag

import cn.nukkit.api.PowerNukkitDifference

class StringTag : Tag {
    var data: String? = null

    constructor(name: String?) : super(name) {}

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Throws NullPointerException instead of IllegalArgumentException if data is null")
    constructor(name: String?, @Nonnull data: String?) : super(name) {
        this.data = Preconditions.checkNotNull(data, "Empty string not allowed")
    }

    @Override
    @Throws(IOException::class)
    override fun write(dos: NBTOutputStream) {
        dos.writeUTF(data)
    }

    @Override
    @Throws(IOException::class)
    override fun load(dis: NBTInputStream) {
        data = dis.readUTF()
    }

    @Override
    override fun parseValue(): String? {
        return data
    }

    override val id: Byte
        @Override get() = TAG_String

    @Override
    override fun toString(): String {
        return "StringTag " + this.getName().toString() + " (data: " + data.toString() + ")"
    }

    @Override
    override fun copy(): Tag {
        return StringTag(getName(), data)
    }

    @Override
    override fun equals(obj: Object): Boolean {
        if (super.equals(obj)) {
            val o = obj as StringTag
            return data == null && o.data == null || data != null && data!!.equals(o.data)
        }
        return false
    }

    @Override
    override fun hashCode(): Int {
        return Objects.hash(super.hashCode(), data)
    }
}