package cn.nukkit.blockentity

import cn.nukkit.api.PowerNukkitDifference

class BlockEntityLectern(chunk: FullChunk?, nbt: CompoundTag) : BlockEntitySpawnable(chunk, nbt) {
    var totalPages = 0
        private set

    @Override
    protected override fun initBlockEntity() {
        if (this.namedTag.get("book") !is CompoundTag) {
            this.namedTag.remove("book")
        }
        if (this.namedTag.get("page") !is IntTag) {
            this.namedTag.remove("page")
        }
        updateTotalPages()
    }

    @get:Override
    override val spawnCompound: CompoundTag
        get() {
            val c: CompoundTag = CompoundTag()
                    .putString("id", BlockEntity.LECTERN)
                    .putInt("x", this.x as Int)
                    .putInt("y", this.y as Int)
                    .putInt("z", this.z as Int)
                    .putBoolean("isMovable", this.movable)
            val book: Item = book
            if (book.getId() !== Item.AIR) {
                c.putCompound("book", NBTIO.putItemHelper(book))
                c.putBoolean("hasBook", true)
                c.putInt("page", rawPage)
                c.putInt("totalPages", totalPages)
            } else {
                c.putBoolean("hasBook", false)
            }
            return c
        }

    @get:Override
    override val isBlockEntityValid: Boolean
        get() = getBlock().getId() === BlockID.LECTERN

    @Override
    override fun onBreak() {
        level.dropItem(this, book)
    }

    fun hasBook(): Boolean {
        return this.namedTag.contains("book") && this.namedTag.get("book") is CompoundTag
    }

    var book: Item
        get() = if (!hasBook()) {
            ItemBlock(BlockAir(), 0, 0)
        } else {
            NBTIO.getItemHelper(this.namedTag.getCompound("book"))
        }
        set(item) {
            if (item.getId() === Item.WRITTEN_BOOK || item.getId() === Item.BOOK_AND_QUILL) {
                this.namedTag.putCompound("book", NBTIO.putItemHelper(item))
            } else {
                this.namedTag.remove("book")
                this.namedTag.remove("page")
            }
            updateTotalPages()
        }
    var leftPage: Int
        get() = rawPage * 2 + 1
        set(newLeftPage) {
            rawPage = (newLeftPage - 1) / 2
        }
    var rightPage: Int
        get() = leftPage + 1
        set(newRightPage) {
            leftPage = newRightPage - 1
        }
    var rawPage: Int
        get() = this.namedTag.getInt("page")
        set(page) {
            this.namedTag.putInt("page", Math.min(page, totalPages))
            this.getLevel().updateAround(this)
        }

    @PowerNukkitDifference(info = "Use RedstoneComponent for redstone update.", since = "1.4.0.0-PN")
    private fun updateTotalPages() {
        val book: Item = book
        totalPages = if (book.getId() === Item.AIR || !book.hasCompoundTag()) {
            0
        } else {
            book.getNamedTag().getList("pages", CompoundTag::class.java).size()
        }
        RedstoneComponent.updateAroundRedstone(this)
    }
}