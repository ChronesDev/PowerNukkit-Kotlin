package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
interface BlockEntityHolder<E : BlockEntity?> {
    @get:Nullable
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val blockEntity: E?
        get() {
            val level: Level = level ?: throw LevelException("Undefined Level reference")
            val blockEntity: BlockEntity
            blockEntity = if (this is Vector3) {
                level.getBlockEntity(this as Vector3)
            } else if (this is BlockVector3) {
                level.getBlockEntity(this as BlockVector3)
            } else {
                level.getBlockEntity(BlockVector3(floorX, floorY, floorZ))
            }
            val blockEntityClass: Class<out E> = blockEntityClass
            return if (blockEntityClass.isInstance(blockEntity)) {
                blockEntityClass.cast(blockEntity)
            } else null
        }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun createBlockEntity(): E {
        return createBlockEntity(null)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun createBlockEntity(@Nullable initialData: CompoundTag?, @Nullable vararg args: Object?): E {
        var initialData: CompoundTag? = initialData
        val typeName = blockEntityType
        val chunk: FullChunk = chunk ?: throw LevelException("Undefined Level or chunk reference")
        if (initialData == null) {
            initialData = CompoundTag()
        } else {
            initialData = initialData.copy()
        }
        val created: BlockEntity = BlockEntity.createBlockEntity(typeName, chunk,
                initialData
                        .putString("id", typeName)
                        .putInt("x", floorX)
                        .putInt("y", floorY)
                        .putInt("z", floorZ),
                args)
        val entityClass: Class<out E> = blockEntityClass
        if (!entityClass.isInstance(created)) {
            val error = "Failed to create the block entity " + typeName + " of class " + entityClass + " at " + location + ", " +
                    "the created type is not an instance of the requested class. Created: " + created
            if (created != null) {
                created.close()
            }
            throw IllegalStateException(error)
        }
        return entityClass.cast(created)
    }

    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val orCreateBlockEntity: E
        get() {
            val blockEntity = blockEntity
            return blockEntity ?: createBlockEntity()
        }

    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val blockEntityClass: Class<out E>?

    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val blockEntityType: String

    @get:Nullable
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val chunk: FullChunk?

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val floorX: Int

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val floorY: Int

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val floorZ: Int

    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val location: Location

    @get:Getter
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val level: Level

    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    val block: cn.nukkit.block.Block
        get() = if (this is Position) {
            (this as Position).getLevelBlock()
        } else if (this is Vector3) {
            level.getBlock(this as Vector3)
        } else {
            level.getBlock(floorX, floorY, floorZ)
        }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        @Nullable
        fun <E : BlockEntity?, H : BlockEntityHolder<E>?> setBlockAndCreateEntity(@Nonnull holder: H): E {
            return setBlockAndCreateEntity(holder, true, true)
        }

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        @Nullable
        fun <E : BlockEntity?, H : BlockEntityHolder<E>?> setBlockAndCreateEntity(
                @Nonnull holder: H, direct: Boolean, update: Boolean): E {
            return setBlockAndCreateEntity(holder, direct, update, null)
        }

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        @Nullable
        fun <E : BlockEntity?, H : BlockEntityHolder<E>?> setBlockAndCreateEntity(
                @Nonnull holder: H, direct: Boolean, update: Boolean, @Nullable initialData: CompoundTag?,
                @Nullable vararg args: Object?): E? {
            val block: Block = holder!!.block
            val level: Level = block.getLevel()
            val layer0: Block = level.getBlock(block, 0)
            val layer1: Block = level.getBlock(block, 1)
            return if (level.setBlock(block, block, direct, update)) {
                try {
                    holder.createBlockEntity(initialData, *args)
                } catch (e: Exception) {
                    Loggers.logBlocKEntityHolder.warn("Failed to create block entity {} at {} at ", holder.blockEntityType, holder.location, e)
                    level.setBlock(layer0, 0, layer0, direct, update)
                    level.setBlock(layer1, 1, layer1, direct, update)
                    throw e
                }
            } else null
        }
    }
}