package cn.nukkit.utils

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BinaryStream {
    var offset: Int
    private var buffer: ByteArray?
    var count: Int
        private set

    constructor() {
        buffer = ByteArray(32)
        offset = 0
        count = 0
    }

    @JvmOverloads
    constructor(buffer: ByteArray, offset: Int = 0) {
        this.buffer = buffer
        this.offset = offset
        count = buffer.size
    }

    fun reset(): BinaryStream {
        offset = 0
        count = 0
        return this
    }

    fun setBuffer(buffer: ByteArray?) {
        this.buffer = buffer
        count = buffer?.size ?: -1
    }

    fun setBuffer(buffer: ByteArray?, offset: Int) {
        this.setBuffer(buffer)
        this.offset = offset
    }

    fun getBuffer(): ByteArray {
        return Arrays.copyOf(buffer, count)
    }

    @JvmOverloads
    operator fun get(len: Int = count - offset): ByteArray {
        var len = len
        if (len < 0) {
            offset = count - 1
            return EmptyArrays.EMPTY_BYTES
        }
        len = Math.min(len, count - offset)
        offset += len
        return Arrays.copyOfRange(buffer, offset - len, offset)
    }

    fun put(bytes: ByteArray?) {
        if (bytes == null) {
            return
        }
        ensureCapacity(count + bytes.size)
        System.arraycopy(bytes, 0, buffer, count, bytes.size)
        count += bytes.size
    }

    val long: Long
        get() = Binary.readLong(this[8])

    fun putLong(l: Long) {
        put(Binary.writeLong(l))
    }

    val int: Int
        get() = Binary.readInt(this[4])

    fun putInt(i: Int) {
        put(Binary.writeInt(i))
    }

    val lLong: Long
        get() = Binary.readLLong(this[8])

    fun putLLong(l: Long) {
        put(Binary.writeLLong(l))
    }

    val lInt: Int
        get() = Binary.readLInt(this[4])

    fun putLInt(i: Int) {
        put(Binary.writeLInt(i))
    }

    val short: Int
        get() = Binary.readShort(this[2])

    fun putShort(s: Int) {
        put(Binary.writeShort(s))
    }

    val lShort: Int
        get() = Binary.readLShort(this[2])

    fun putLShort(s: Int) {
        put(Binary.writeLShort(s))
    }

    val float: Float
        get() = getFloat(-1)

    fun getFloat(accuracy: Int): Float {
        return Binary.readFloat(this[4], accuracy)
    }

    fun putFloat(v: Float) {
        put(Binary.writeFloat(v))
    }

    val lFloat: Float
        get() = getLFloat(-1)

    fun getLFloat(accuracy: Int): Float {
        return Binary.readLFloat(this[4], accuracy)
    }

    fun putLFloat(v: Float) {
        put(Binary.writeLFloat(v))
    }

    val triad: Int
        get() = Binary.readTriad(this[3])

    fun putTriad(triad: Int) {
        put(Binary.writeTriad(triad))
    }

    val lTriad: Int
        get() = Binary.readLTriad(this[3])

    fun putLTriad(triad: Int) {
        put(Binary.writeLTriad(triad))
    }

    val boolean: Boolean
        get() = byte == 0x01

    fun putBoolean(bool: Boolean) {
        putByte((if (bool) 1 else 0).toByte())
    }

    val byte: Int
        get() = buffer!![offset++] and 0xff

    fun putByte(b: Byte) {
        put(byteArrayOf(b))
    }

    /**
     * Reads a list of Attributes from the stream.
     *
     * @return Attribute[]
     */
    @get:Throws(Exception::class)
    val attributeList: Array<Any>
        get() {
            val list: List<Attribute> = ArrayList()
            val count = unsignedVarInt
            for (i in 0 until count) {
                val name = string
                val attr: Attribute = Attribute.getAttributeByName(name)
                if (attr != null) {
                    attr.setMinValue(lFloat)
                    attr.setValue(lFloat)
                    attr.setMaxValue(lFloat)
                    list.add(attr)
                } else {
                    throw Exception("Unknown attribute type \"$name\"")
                }
            }
            return list.toArray(Attribute.EMPTY_ARRAY)
        }

    /**
     * Writes a list of Attributes to the packet buffer using the standard format.
     */
    fun putAttributeList(attributes: Array<Attribute>) {
        putUnsignedVarInt(attributes.size.toLong())
        for (attribute in attributes) {
            putString(attribute.getName())
            putLFloat(attribute.getMinValue())
            putLFloat(attribute.getValue())
            putLFloat(attribute.getMaxValue())
        }
    }

    fun putUUID(uuid: UUID) {
        put(Binary.writeUUID(uuid))
    }

    val uUID: UUID
        get() = Binary.readUUID(this[16])

    fun putSkin(skin: Skin) {
        putString(skin.getSkinId())
        putString(skin.getPlayFabId())
        putString(skin.getSkinResourcePatch())
        putImage(skin.getSkinData())
        val animations: List<SkinAnimation> = skin.getAnimations()
        putLInt(animations.size())
        for (animation in animations) {
            putImage(animation.image)
            putLInt(animation.type)
            putLFloat(animation.frames)
            putLInt(animation.expression)
        }
        putImage(skin.getCapeData())
        putString(skin.getGeometryData())
        putString(skin.getAnimationData())
        putBoolean(skin.isPremium())
        putBoolean(skin.isPersona())
        putBoolean(skin.isCapeOnClassic())
        putString(skin.getCapeId())
        putString(skin.getFullSkinId())
        putString(skin.getArmSize())
        putString(skin.getSkinColor())
        val pieces: List<PersonaPiece> = skin.getPersonaPieces()
        putLInt(pieces.size())
        for (piece in pieces) {
            putString(piece.id)
            putString(piece.type)
            putString(piece.packId)
            putBoolean(piece.isDefault)
            putString(piece.productId)
        }
        val tints: List<PersonaPieceTint> = skin.getTintColors()
        putLInt(tints.size())
        for (tint in tints) {
            putString(tint.pieceType)
            val colors: List<String> = tint.colors
            putLInt(colors.size())
            for (color in colors) {
                putString(color)
            }
        }
    }

    // TODO: Full skin id
    val skin: Skin
        get() {
            val skin = Skin()
            skin.setSkinId(string)
            skin.setPlayFabId(string)
            skin.setSkinResourcePatch(string)
            skin.setSkinData(image)
            val animationCount = lInt
            for (i in 0 until animationCount) {
                val image: SerializedImage = image
                val type = lInt
                val frames = lFloat
                val expression = lInt
                skin.getAnimations().add(SkinAnimation(image, type, frames, expression))
            }
            skin.setCapeData(image)
            skin.setGeometryData(string)
            skin.setAnimationData(string)
            skin.setPremium(boolean)
            skin.setPersona(boolean)
            skin.setCapeOnClassic(boolean)
            skin.setCapeId(string)
            string // TODO: Full skin id
            skin.setArmSize(string)
            skin.setSkinColor(string)
            val piecesLength = lInt
            for (i in 0 until piecesLength) {
                val pieceId = string
                val pieceType = string
                val packId = string
                val isDefault = boolean
                val productId = string
                skin.getPersonaPieces().add(PersonaPiece(pieceId, pieceType, packId, isDefault, productId))
            }
            val tintsLength = lInt
            for (i in 0 until tintsLength) {
                val pieceType = string
                val colors: List<String> = ArrayList()
                val colorsLength = lInt
                for (i2 in 0 until colorsLength) {
                    colors.add(string)
                }
                skin.getTintColors().add(PersonaPieceTint(pieceType, colors))
            }
            return skin
        }

    fun putImage(image: SerializedImage) {
        putLInt(image.width)
        putLInt(image.height)
        putByteArray(image.data)
    }

    val image: cn.nukkit.utils.SerializedImage
        get() {
            val width = lInt
            val height = lInt
            val data = byteArray
            return SerializedImage(width, height, data)
        }

    // hasNetId
    // netId
    // blockRuntimeId
    val slot: Item
        get() {
            val networkId = varInt
            if (networkId == 0) {
                return Item.get(0, 0, 0)
            }
            val count = lShort
            var damage = unsignedVarInt.toInt()
            val fullId: Int = RuntimeItems.getRuntimeMapping().getLegacyFullId(networkId)
            val id: Int = RuntimeItems.getId(fullId)
            val hasData: Boolean = RuntimeItems.hasData(fullId)
            if (hasData) {
                damage = RuntimeItems.getData(fullId)
            }
            if (boolean) { // hasNetId
                varInt // netId
            }
            varInt // blockRuntimeId
            val bytes = byteArray
            val buf: ByteBuf = AbstractByteBufAllocator.DEFAULT.ioBuffer(bytes.size)
            buf.writeBytes(bytes)
            var nbt = ByteArray(0)
            var canPlace: Array<String?>
            var canBreak: Array<String?>
            try {
                LittleEndianByteBufInputStream(buf).use { stream ->
                    val nbtSize: Int = stream.readShort()
                    var compoundTag: CompoundTag? = null
                    if (nbtSize > 0) {
                        compoundTag = NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN)
                    } else if (nbtSize == -1) {
                        val tagCount: Int = stream.readUnsignedByte()
                        if (tagCount != 1) throw IllegalArgumentException("Expected 1 tag but got $tagCount")
                        compoundTag = NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN)
                    }
                    if (compoundTag != null && compoundTag.getAllTags().size() > 0) {
                        if (compoundTag.contains("Damage")) {
                            damage = compoundTag.getInt("Damage")
                            compoundTag.remove("Damage")
                        }
                        if (compoundTag.contains("__DamageConflict__")) {
                            compoundTag.put("Damage", compoundTag.removeAndGet("__DamageConflict__"))
                        }
                        if (!compoundTag.isEmpty()) {
                            nbt = NBTIO.write(compoundTag, ByteOrder.LITTLE_ENDIAN)
                        }
                    }
                    canPlace = arrayOfNulls(stream.readInt())
                    for (i in canPlace.indices) {
                        canPlace[i] = stream.readUTF()
                    }
                    canBreak = arrayOfNulls(stream.readInt())
                    for (i in canBreak.indices) {
                        canBreak[i] = stream.readUTF()
                    }
                    if (id == ItemID.SHIELD) {
                        stream.readLong()
                    }
                }
            } catch (e: IOException) {
                throw IllegalStateException("Unable to read item user data", e)
            } finally {
                buf.release()
            }
            val item: Item = Item.get(id, damage, count, nbt)
            if (canBreak.size > 0 || canPlace.size > 0) {
                var namedTag: CompoundTag? = item.getNamedTag()
                if (namedTag == null) {
                    namedTag = CompoundTag()
                }
                if (canBreak.size > 0) {
                    val listTag: ListTag<StringTag> = ListTag("CanDestroy")
                    for (blockName in canBreak) {
                        listTag.add(StringTag("", blockName))
                    }
                    namedTag.put("CanDestroy", listTag)
                }
                if (canPlace.size > 0) {
                    val listTag: ListTag<StringTag> = ListTag("CanPlaceOn")
                    for (blockName in canPlace) {
                        listTag.add(StringTag("", blockName))
                    }
                    namedTag.put("CanPlaceOn", listTag)
                }
                item.setNamedTag(namedTag)
            }
            return item
        }

    fun putSlot(item: Item?) {
        this.putSlot(item, false)
    }

    @Since("1.4.0.0-PN")
    fun putSlot(item: Item?, instanceItem: Boolean) {
        if (item == null || item.getId() === 0) {
            putByte(0.toByte())
            return
        }
        val networkFullId: Int = RuntimeItems.getRuntimeMapping().getNetworkFullId(item)
        val networkId: Int = RuntimeItems.getNetworkId(networkFullId)
        putVarInt(networkId)
        putLShort(item.getCount())
        var legacyData = 0
        if (item.getId() > 256) { // Not a block
            if (item is ItemDurable || !RuntimeItems.hasData(networkFullId)) {
                legacyData = item.getDamage()
            }
        }
        putUnsignedVarInt(legacyData.toLong())
        if (!instanceItem) {
            putBoolean(true) // hasNetId
            putVarInt(0) // netId
        }
        val block: Block = item.getBlockUnsafe()
        val blockRuntimeId = if (block == null) 0 else block.getRuntimeId()
        putVarInt(blockRuntimeId)
        var data = 0
        if (item is ItemDurable || item.getId() < 256) {
            data = item.getDamage()
        }
        val userDataBuf: ByteBuf = ByteBufAllocator.DEFAULT.ioBuffer()
        try {
            LittleEndianByteBufOutputStream(userDataBuf).use { stream ->
                if (data != 0) {
                    val nbt: ByteArray = item.getCompoundTag()
                    val tag: CompoundTag
                    if (nbt == null || nbt.size == 0) {
                        tag = CompoundTag()
                    } else {
                        tag = NBTIO.read(nbt, ByteOrder.LITTLE_ENDIAN)
                    }
                    if (tag.contains("Damage")) {
                        tag.put("__DamageConflict__", tag.removeAndGet("Damage"))
                    }
                    tag.putInt("Damage", data)
                    stream.writeShort(-1)
                    stream.writeByte(1) // Hardcoded in current version
                    stream.write(NBTIO.write(tag, ByteOrder.LITTLE_ENDIAN))
                } else if (item.hasCompoundTag()) {
                    stream.writeShort(-1)
                    stream.writeByte(1) // Hardcoded in current version
                    stream.write(item.getCompoundTag())
                } else {
                    userDataBuf.writeShortLE(0)
                }
                val canPlaceOn = extractStringList(item, "CanPlaceOn")
                stream.writeInt(canPlaceOn.size())
                for (string in canPlaceOn) {
                    stream.writeUTF(string)
                }
                val canDestroy = extractStringList(item, "CanDestroy")
                stream.writeInt(canDestroy.size())
                for (string in canDestroy) {
                    stream.writeUTF(string)
                }
                if (item.getId() === ItemID.SHIELD) {
                    stream.writeLong(0)
                }
                val bytes = ByteArray(userDataBuf.readableBytes())
                userDataBuf.readBytes(bytes)
                putByteArray(bytes)
            }
        } catch (e: IOException) {
            throw IllegalStateException("Unable to write item user data", e)
        } finally {
            userDataBuf.release()
        }
    }

    val recipeIngredient: Item
        get() {
            val networkId = varInt
            if (networkId == 0) {
                return Item.get(0, 0, 0)
            }
            val legacyFullId: Int = RuntimeItems.getRuntimeMapping().getLegacyFullId(networkId)
            val id: Int = RuntimeItems.getId(legacyFullId)
            val hasData: Boolean = RuntimeItems.hasData(legacyFullId)
            var damage = varInt
            if (hasData) {
                damage = RuntimeItems.getData(legacyFullId)
            } else if (damage == 0x7fff) {
                damage = -1
            }
            val count = varInt
            return Item.get(id, damage, count)
        }

    fun putRecipeIngredient(ingredient: Item?) {
        if (ingredient == null || ingredient.getId() === 0) {
            putVarInt(0)
            return
        }
        val networkFullId: Int = RuntimeItems.getRuntimeMapping().getNetworkFullId(ingredient)
        val networkId: Int = RuntimeItems.getNetworkId(networkFullId)
        var damage = if (ingredient.hasMeta()) ingredient.getDamage() else 0x7fff
        if (RuntimeItems.hasData(networkFullId)) {
            damage = 0
        }
        putVarInt(networkId)
        putVarInt(damage)
        putVarInt(ingredient.getCount())
    }

    private fun extractStringList(item: Item?, tagName: String): List<String> {
        val namedTag: CompoundTag = item.getNamedTag() ?: return Collections.emptyList()
        val listTag: ListTag<StringTag> = namedTag.getList(tagName, StringTag::class.java)
                ?: return Collections.emptyList()
        val size: Int = listTag.size()
        val values: List<String> = ArrayList(size)
        for (i in 0 until size) {
            val stringTag: StringTag = listTag.get(i)
            if (stringTag != null) {
                values.add(stringTag.data)
            }
        }
        return values
    }

    val byteArray: ByteArray
        get() = this[unsignedVarInt.toInt()]

    fun putByteArray(b: ByteArray) {
        putUnsignedVarInt(b.size.toLong())
        put(b)
    }

    val string: String
        get() = String(byteArray, StandardCharsets.UTF_8)

    fun putString(string: String) {
        val b: ByteArray = string.getBytes(StandardCharsets.UTF_8)
        putByteArray(b)
    }

    val unsignedVarInt: Long
        get() = VarInt.readUnsignedVarInt(this)

    fun putUnsignedVarInt(v: Long) {
        VarInt.writeUnsignedVarInt(this, v)
    }

    val varInt: Int
        get() = VarInt.readVarInt(this)

    fun putVarInt(v: Int) {
        VarInt.writeVarInt(this, v)
    }

    val varLong: Long
        get() = VarInt.readVarLong(this)

    fun putVarLong(v: Long) {
        VarInt.writeVarLong(this, v)
    }

    val unsignedVarLong: Long
        get() = VarInt.readUnsignedVarLong(this)

    fun putUnsignedVarLong(v: Long) {
        VarInt.writeUnsignedVarLong(this, v)
    }

    val blockVector3: BlockVector3
        get() = BlockVector3(varInt, unsignedVarInt.toInt(), varInt)
    val signedBlockPosition: BlockVector3
        get() = BlockVector3(varInt, varInt, varInt)

    fun putSignedBlockPosition(v: BlockVector3) {
        putVarInt(v.x)
        putVarInt(v.y)
        putVarInt(v.z)
    }

    fun putBlockVector3(v: BlockVector3) {
        this.putBlockVector3(v.x, v.y, v.z)
    }

    fun putBlockVector3(x: Int, y: Int, z: Int) {
        putVarInt(x)
        putUnsignedVarInt(y.toLong())
        putVarInt(z)
    }

    val vector3f: Vector3f
        get() = Vector3f(getLFloat(4), getLFloat(4), getLFloat(4))

    fun putVector3f(v: Vector3f) {
        this.putVector3f(v.x, v.y, v.z)
    }

    fun putVector3f(x: Float, y: Float, z: Float) {
        putLFloat(x)
        putLFloat(y)
        putLFloat(z)
    }

    fun putGameRules(gameRules: GameRules) {
        val rules: Map<GameRule, GameRules.Value> = gameRules.getGameRules()
        putUnsignedVarInt(rules.size().toLong())
        rules.forEach { gameRule, value ->
            putString(gameRule.getName().toLowerCase())
            value.write(this)
        }
    }

    /**
     * Reads and returns an EntityUniqueID
     *
     * @return int
     */
    val entityUniqueId: Long
        get() = varLong

    /**
     * Writes an EntityUniqueID
     */
    fun putEntityUniqueId(eid: Long) {
        putVarLong(eid)
    }

    /**
     * Reads and returns an EntityRuntimeID
     */
    val entityRuntimeId: Long
        get() = unsignedVarLong

    /**
     * Writes an EntityUniqueID
     */
    fun putEntityRuntimeId(eid: Long) {
        putUnsignedVarLong(eid)
    }

    val blockFace: BlockFace
        get() = BlockFace.fromIndex(varInt)

    fun putBlockFace(face: BlockFace) {
        putVarInt(face.getIndex())
    }

    fun putEntityLink(link: EntityLink) {
        putEntityUniqueId(link.fromEntityUniquieId)
        putEntityUniqueId(link.toEntityUniquieId)
        putByte(link.type)
        putBoolean(link.immediate)
        putBoolean(link.riderInitiated)
    }

    val entityLink: EntityLink
        get() = EntityLink(
                entityUniqueId,
                entityUniqueId,
                byte.toByte(),
                boolean,
                boolean
        )

    @SuppressWarnings("unchecked")
    fun <T> getArray(clazz: Class<T>?, function: Function<BinaryStream?, T>): Array<T> {
        val deque: ArrayDeque<T> = ArrayDeque()
        val count = unsignedVarInt.toInt()
        for (i in 0 until count) {
            deque.add(function.apply(this))
        }
        return deque.toArray(Array.newInstance(clazz, 0) as Array<T>?)
    }

    fun feof(): Boolean {
        return offset < 0 || offset >= buffer!!.size
    }

    @get:Since("1.5.0.0-PN")
    @get:PowerNukkitOnly
    @get:SneakyThrows(IOException::class)
    val tag: CompoundTag
        get() {
            val `is` = ByteArrayInputStream(buffer, offset, buffer!!.size)
            val initial: Int = `is`.available()
            return try {
                NBTIO.read(`is`)
            } finally {
                offset += `is`.available() - initial
            }
        }

    @SneakyThrows(IOException::class)
    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    fun putTag(tag: CompoundTag?) {
        put(NBTIO.write(tag))
    }

    private fun ensureCapacity(minCapacity: Int) {
        // overflow-conscious code
        if (minCapacity - buffer!!.size > 0) {
            grow(minCapacity)
        }
    }

    private fun grow(minCapacity: Int) {
        // overflow-conscious code
        val oldCapacity = buffer!!.size
        var newCapacity = oldCapacity shl 1
        if (newCapacity - minCapacity < 0) {
            newCapacity = minCapacity
        }
        if (newCapacity - MAX_ARRAY_SIZE > 0) {
            newCapacity = hugeCapacity(minCapacity)
        }
        buffer = Arrays.copyOf(buffer, newCapacity)
    }

    companion object {
        private val MAX_ARRAY_SIZE: Int = Integer.MAX_VALUE - 8
        private fun hugeCapacity(minCapacity: Int): Int {
            if (minCapacity < 0) { // overflow
                throw OutOfMemoryError()
            }
            return if (minCapacity > MAX_ARRAY_SIZE) Integer.MAX_VALUE else MAX_ARRAY_SIZE
        }
    }
}