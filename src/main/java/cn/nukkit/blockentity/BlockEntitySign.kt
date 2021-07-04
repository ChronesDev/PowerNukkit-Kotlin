package cn.nukkit.blockentity

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockEntitySign(chunk: FullChunk?, nbt: CompoundTag) : BlockEntitySpawnable(chunk, nbt) {
    var text: Array<String?>?
        private set

    @Override
    protected override fun initBlockEntity() {
        text = arrayOfNulls(4)
        if (!namedTag.contains("Text")) {
            for (i in 1..4) {
                val key = "Text$i"
                if (namedTag.contains(key)) {
                    val line: String = namedTag.getString(key)
                    text!![i - 1] = line
                    this.namedTag.remove(key)
                }
            }
        } else {
            val lines: Array<String> = namedTag.getString("Text").split("\n", 4)
            for (i in text.indices) {
                if (i < lines.size) text!![i] = lines[i] else text!![i] = ""
            }
        }

        // Check old text to sanitize
        if (text != null) {
            sanitizeText(text!!)
        }
        super.initBlockEntity()
    }

    @Override
    override fun saveNBT() {
        super.saveNBT()
        this.namedTag.remove("Creator")
    }

    @get:Override
    override val isBlockEntityValid: Boolean
        get() {
            val block: Block = getBlock()
            return block is BlockSignPost
        }

    fun setText(vararg lines: String?): Boolean {
        for (i in 0..3) {
            if (i < lines.size) text!![i] = lines[i] else text!![i] = ""
        }
        this.namedTag.putString("Text", String.join("\n", text))
        this.spawnToAll()
        if (this.chunk != null) {
            setDirty()
        }
        return true
    }

    @Override
    override fun updateCompoundTag(nbt: CompoundTag, player: Player): Boolean {
        if (!nbt.getString("id").equals(BlockEntity.SIGN)) {
            return false
        }
        val lines = arrayOfNulls<String>(4)
        Arrays.fill(lines, "")
        val splitLines: Array<String> = nbt.getString("Text").split("\n", 4)
        System.arraycopy(splitLines, 0, lines, 0, splitLines.size)
        sanitizeText(lines)
        val signChangeEvent = SignChangeEvent(this.getBlock(), player, lines)
        if (!this.namedTag.contains("Creator") || !Objects.equals(player.getUniqueId().toString(), this.namedTag.getString("Creator"))) {
            signChangeEvent.setCancelled()
        }
        if (player.getRemoveFormat()) {
            for (i in lines.indices) {
                lines[i] = TextFormat.clean(lines[i])
            }
        }
        this.server.getPluginManager().callEvent(signChangeEvent)
        if (!signChangeEvent.isCancelled()) {
            setText(signChangeEvent.getLines())
            return true
        }
        return false
    }

    @get:Override
    override val spawnCompound: CompoundTag
        get() = CompoundTag()
                .putString("id", BlockEntity.SIGN)
                .putString("Text", this.namedTag.getString("Text"))
                .putInt("x", this.x as Int)
                .putInt("y", this.y as Int)
                .putInt("z", this.z as Int)

    companion object {
        private fun sanitizeText(lines: Array<String?>) {
            for (i in lines.indices) {
                // Don't allow excessive text per line.
                if (lines[i] != null) {
                    lines[i] = lines[i].substring(0, Math.min(255, lines[i]!!.length()))
                }
            }
        }
    }
}