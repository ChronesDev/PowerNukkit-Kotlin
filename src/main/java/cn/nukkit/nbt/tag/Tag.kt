package cn.nukkit.nbt.tag

import cn.nukkit.nbt.stream.NBTInputStream

abstract class Tag protected constructor(name: String?) {
    private var name: String? = null
    @Throws(IOException::class)
    abstract fun write(dos: NBTOutputStream?)
    @Throws(IOException::class)
    abstract fun load(dis: NBTInputStream?)
    abstract override fun toString(): String
    abstract val id: Byte

    @Override
    override fun equals(obj: Object): Boolean {
        if (obj !is Tag) {
            return false
        }
        val o = obj as Tag
        return id == o.id && !(name == null && o.name != null || name != null && o.name == null) && !(name != null && !name!!.equals(o.name))
    }

    @Override
    override fun hashCode(): Int {
        return Objects.hash(id, name)
    }

    fun print(out: PrintStream) {
        print("", out)
    }

    fun print(prefix: String?, out: PrintStream) {
        val name = getName()
        out.print(prefix)
        out.print(getTagName(id))
        if (name.length() > 0) {
            out.print("(\"$name\")")
        }
        out.print(": ")
        if (id != TAG_Compound && id != TAG_List) {
            out.println(parseValue())
        }
    }

    fun setName(name: String?): Tag {
        if (name == null) {
            this.name = ""
        } else {
            this.name = name
        }
        return this
    }

    @Nonnull
    fun getName(): String {
        return if (name == null) "" else name
    }

    abstract fun copy(): Tag
    abstract fun parseValue(): Object?

    companion object {
        const val TAG_End: Byte = 0
        const val TAG_Byte: Byte = 1
        const val TAG_Short: Byte = 2
        const val TAG_Int: Byte = 3
        const val TAG_Long: Byte = 4
        const val TAG_Float: Byte = 5
        const val TAG_Double: Byte = 6
        const val TAG_Byte_Array: Byte = 7
        const val TAG_String: Byte = 8
        const val TAG_List: Byte = 9
        const val TAG_Compound: Byte = 10
        const val TAG_Int_Array: Byte = 11
        @Throws(IOException::class)
        fun readNamedTag(dis: NBTInputStream): Tag {
            val type: Byte = dis.readByte()
            if (type.toInt() == 0) return EndTag()
            val name: String = dis.readUTF()
            val tag = newTag(type, name)
            tag.load(dis)
            return tag
        }

        @Throws(IOException::class)
        fun writeNamedTag(tag: Tag, dos: NBTOutputStream) {
            writeNamedTag(tag, tag.getName(), dos)
        }

        @Throws(IOException::class)
        fun writeNamedTag(tag: Tag, name: String?, dos: NBTOutputStream) {
            dos.writeByte(tag.id)
            if (tag.id == TAG_End) return
            dos.writeUTF(name)
            tag.write(dos)
        }

        fun newTag(type: Byte, name: String?): Tag {
            when (type) {
                TAG_End -> return EndTag()
                TAG_Byte -> return ByteTag(name)
                TAG_Short -> return ShortTag(name)
                TAG_Int -> return IntTag(name)
                TAG_Long -> return LongTag(name)
                TAG_Float -> return FloatTag(name)
                TAG_Double -> return DoubleTag(name)
                TAG_Byte_Array -> return ByteArrayTag(name)
                TAG_Int_Array -> return IntArrayTag(name)
                TAG_String -> return StringTag(name)
                TAG_List -> return ListTag(name)
                TAG_Compound -> return CompoundTag(name)
            }
            return EndTag()
        }

        fun getTagName(type: Byte): String {
            when (type) {
                TAG_End -> return "TAG_End"
                TAG_Byte -> return "TAG_Byte"
                TAG_Short -> return "TAG_Short"
                TAG_Int -> return "TAG_Int"
                TAG_Long -> return "TAG_Long"
                TAG_Float -> return "TAG_Float"
                TAG_Double -> return "TAG_Double"
                TAG_Byte_Array -> return "TAG_Byte_Array"
                TAG_Int_Array -> return "TAG_Int_Array"
                TAG_String -> return "TAG_String"
                TAG_List -> return "TAG_List"
                TAG_Compound -> return "TAG_Compound"
            }
            return "UNKNOWN"
        }
    }

    init {
        if (name == null) {
            this.name = ""
        } else {
            this.name = name
        }
    }
}