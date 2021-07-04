package cn.nukkit.nbt.tag

import cn.nukkit.nbt.stream.NBTInputStream

class ListTag<T : Tag?> : Tag {
    private var list: List<T> = ArrayList()
    var type: Byte = 0

    constructor() : super("") {}
    constructor(name: String?) : super(name) {}

    @Override
    @Throws(IOException::class)
    override fun write(dos: NBTOutputStream) {
        if (list.size() > 0) type = list[0].getId() else type = 1
        dos.writeByte(type)
        dos.writeInt(list.size())
        for (aList in list) aList!!.write(dos)
    }

    @Override
    @SuppressWarnings("unchecked")
    @Throws(IOException::class)
    fun load(dis: NBTInputStream) {
        type = dis.readByte()
        val size: Int = dis.readInt()
        list = ArrayList(size)
        for (i in 0 until size) {
            val tag: Tag = Tag.newTag(type, null)
            tag.load(dis)
            tag.setName("")
            list.add(tag as T)
        }
    }

    override val id: Byte
        @Override get() = TAG_List

    @Override
    override fun toString(): String {
        val joiner = StringJoiner(",\n\t")
        list.forEach { tag -> joiner.add(tag.toString().replace("\n", "\n\t")) }
        return """ListTag '${this.getName().toString()}' (${list.size().toString()} entries of type ${Tag.getTagName(type).toString()}) {
	${joiner.toString().toString()}
}"""
    }

    fun print(prefix: String, out: PrintStream) {
        var prefix = prefix
        super.print(prefix, out)
        out.println("$prefix{")
        val orgPrefix = prefix
        prefix += "   "
        for (aList in list) aList!!.print(prefix, out)
        out.println("$orgPrefix}")
    }

    fun add(tag: T): ListTag<T> {
        type = tag.getId()
        tag!!.setName("")
        list.add(tag)
        return this
    }

    fun add(index: Int, tag: T): ListTag<T> {
        type = tag.getId()
        tag!!.setName("")
        if (index >= list.size()) {
            list.add(index, tag)
        } else {
            list.set(index, tag)
        }
        return this
    }

    @Override
    override fun parseValue(): List<Object> {
        val value: List<Object> = ArrayList(list.size())
        for (t in list) {
            value.add(t!!.parseValue())
        }
        return value
    }

    operator fun get(index: Int): T {
        return list[index]
    }

    var all: List<T>?
        get() = ArrayList(list)
        set(tags) {
            list = ArrayList(tags)
        }

    fun remove(tag: T) {
        list.remove(tag)
    }

    fun remove(index: Int) {
        list.remove(index)
    }

    fun removeAll(tags: Collection<T>?) {
        list.remove(tags)
    }

    fun size(): Int {
        return list.size()
    }

    @Override
    override fun copy(): Tag {
        val res = ListTag<T>(getName())
        res.type = type
        for (t in list) {
            @SuppressWarnings("unchecked") val copy = t!!.copy() as T
            res.list.add(copy)
        }
        return res
    }

    @SuppressWarnings("rawtypes")
    @Override
    override fun equals(obj: Object): Boolean {
        if (super.equals(obj)) {
            val o = obj as ListTag<*>
            if (type == o.type) {
                return list.equals(o.list)
            }
        }
        return false
    }

    @Override
    override fun hashCode(): Int {
        return Objects.hash(super.hashCode(), type, list)
    }
}