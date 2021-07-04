package cn.nukkit.blockentity

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockEntityChest(chunk: FullChunk?, nbt: CompoundTag) : BlockEntitySpawnableContainer(chunk, nbt), BlockEntityNameable {
    protected var doubleInventory: DoubleChestInventory? = null
    @Override
    protected override fun initBlockEntity() {
        inventory = ChestInventory(this)
        super.initBlockEntity()
    }

    @Override
    override fun close() {
        if (!closed) {
            unpair()
            for (player in HashSet(inventory.getViewers())) {
                player.removeWindow(inventory)
            }
            for (player in HashSet(inventory.getViewers())) {
                player.removeWindow(realInventory)
            }
            super.close()
        }
    }

    @get:Override
    override val isBlockEntityValid: Boolean
        get() {
            val blockID: Int = this.getBlock().getId()
            return blockID == Block.CHEST || blockID == Block.TRAPPED_CHEST
        }

    @get:Override
    override val size: Int
        get() = 27

    @get:Override
    override var inventory: BaseInventory?
        get() {
            if (doubleInventory == null && isPaired) {
                checkPairing()
            }
            return if (doubleInventory != null) doubleInventory else field
        }
        set(inventory) {
            super.inventory = inventory
        }
    val realInventory: ChestInventory
        get() = inventory as ChestInventory

    protected fun checkPairing() {
        val pair = pair
        if (pair != null) {
            if (!pair.isPaired) {
                pair.createPair(this)
                pair.checkPairing()
            }
            if (pair.doubleInventory != null) {
                doubleInventory = pair.doubleInventory
            } else if (doubleInventory == null) {
                if (pair.x + (pair.z as Int shl 15) > this.x + (this.z as Int shl 15)) { //Order them correctly
                    doubleInventory = DoubleChestInventory(pair, this)
                } else {
                    doubleInventory = DoubleChestInventory(this, pair)
                }
            }
        } else {
            if (level.isChunkLoaded(this.namedTag.getInt("pairx") shr 4, this.namedTag.getInt("pairz") shr 4)) {
                doubleInventory = null
                this.namedTag.remove("pairx")
                this.namedTag.remove("pairz")
            }
        }
    }

    val isPaired: Boolean
        get() = this.namedTag.contains("pairx") && this.namedTag.contains("pairz")
    val pair: BlockEntityChest?
        get() {
            if (isPaired) {
                val blockEntity: BlockEntity = this.getLevel().getBlockEntityIfLoaded(Vector3(this.namedTag.getInt("pairx"), this.y, this.namedTag.getInt("pairz")))
                if (blockEntity is BlockEntityChest) {
                    return blockEntity
                }
            }
            return null
        }

    fun pairWith(chest: BlockEntityChest): Boolean {
        if (isPaired || chest.isPaired || this.getBlock().getId() !== chest.getBlock().getId()) {
            return false
        }
        createPair(chest)
        chest.spawnToAll()
        this.spawnToAll()
        checkPairing()
        return true
    }

    fun createPair(chest: BlockEntityChest) {
        this.namedTag.putInt("pairx", chest.x as Int)
        this.namedTag.putInt("pairz", chest.z as Int)
        chest.namedTag.putInt("pairx", this.x as Int)
        chest.namedTag.putInt("pairz", this.z as Int)
    }

    fun unpair(): Boolean {
        if (!isPaired) {
            return false
        }
        val chest = pair
        doubleInventory = null
        this.namedTag.remove("pairx")
        this.namedTag.remove("pairz")
        this.spawnToAll()
        if (chest != null) {
            chest.namedTag.remove("pairx")
            chest.namedTag.remove("pairz")
            chest.doubleInventory = null
            chest.checkPairing()
            chest.spawnToAll()
        }
        checkPairing()
        return true
    }

    @get:Override
    override val spawnCompound: CompoundTag
        get() {
            val c: CompoundTag
            c = if (isPaired) {
                CompoundTag()
                        .putString("id", BlockEntity.CHEST)
                        .putInt("x", this.x as Int)
                        .putInt("y", this.y as Int)
                        .putInt("z", this.z as Int)
                        .putInt("pairx", this.namedTag.getInt("pairx"))
                        .putInt("pairz", this.namedTag.getInt("pairz"))
            } else {
                CompoundTag()
                        .putString("id", BlockEntity.CHEST)
                        .putInt("x", this.x as Int)
                        .putInt("y", this.y as Int)
                        .putInt("z", this.z as Int)
            }
            if (hasName()) {
                c.put("CustomName", this.namedTag.get("CustomName"))
            }
            return c
        }

    @get:Override
    @set:Override
    override var name: String
        get() = if (hasName()) this.namedTag.getString("CustomName") else "Chest"
        set(name) {
            if (name == null || name.equals("")) {
                this.namedTag.remove("CustomName")
                return
            }
            this.namedTag.putString("CustomName", name)
        }

    @Override
    override fun hasName(): Boolean {
        return this.namedTag.contains("CustomName")
    }

    @Override
    override fun onBreak() {
        unpair()
        super.onBreak()
    }
}