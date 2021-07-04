package cn.nukkit.blockentity

import cn.nukkit.block.Block

class BlockEntityBanner(chunk: FullChunk?, nbt: CompoundTag) : BlockEntitySpawnable(chunk, nbt) {
    var color = 0
    @Override
    protected override fun initBlockEntity() {
        if (!this.namedTag.contains("color")) {
            this.namedTag.putByte("color", 0)
        }
        color = this.namedTag.getByte("color")
        super.initBlockEntity()
    }

    @get:Override
    override val isBlockEntityValid: Boolean
        get() = this.getBlock().getId() === Block.WALL_BANNER || this.getBlock().getId() === Block.STANDING_BANNER

    @Override
    override fun saveNBT() {
        super.saveNBT()
        this.namedTag.putByte("color", color)
    }

    @get:Override
    override var name: String
        get() = "Banner"
        set(name) {
            super.name = name
        }
    var baseColor: Int
        get() = this.namedTag.getInt("Base")
        set(color) {
            this.namedTag.putInt("Base", color.getDyeData() and 0x0f)
        }
    var type: Int
        get() = this.namedTag.getInt("Type")
        set(type) {
            this.namedTag.putInt("Type", type)
        }

    fun addPattern(pattern: BannerPattern) {
        val patterns: ListTag<CompoundTag> = this.namedTag.getList("Patterns", CompoundTag::class.java)
        patterns.add(CompoundTag("").putInt("Color", pattern.getColor().getDyeData() and 0x0f).putString("Pattern", pattern.getType().getName()))
        this.namedTag.putList(patterns)
    }

    fun getPattern(index: Int): BannerPattern {
        return BannerPattern.fromCompoundTag(if (this.namedTag.getList("Patterns").size() > index && index >= 0) this.namedTag.getList("Patterns", CompoundTag::class.java).get(index) else CompoundTag())
    }

    fun removePattern(index: Int) {
        val patterns: ListTag<CompoundTag> = this.namedTag.getList("Patterns", CompoundTag::class.java)
        if (patterns.size() > index && index >= 0) {
            patterns.remove(index)
        }
    }

    val patternsSize: Int
        get() = this.namedTag.getList("Patterns").size()

    @get:Override
    override val spawnCompound: CompoundTag
        get() = getDefaultCompound(this, BANNER)
                .putInt("Base", baseColor)
                .putList(this.namedTag.getList("Patterns"))
                .putInt("Type", type)
                .putByte("color", color)
    val dyeColor: DyeColor
        get() = DyeColor.getByWoolData(color)
}