package cn.nukkit.item

import cn.nukkit.api.DeprecationDetails

/**
 * @author PetteriM1
 */
class ItemBanner(meta: Integer?, count: Int) : Item(BANNER, meta, count, "Banner") {
    @JvmOverloads
    constructor(meta: Integer? = 0) : this(meta, 1) {
    }

    @Override
    override fun getMaxStackSize(): Int {
        return 16
    }

    fun getBaseColor(): Int {
        return this.getDamage() and 0x0f
    }

    fun setBaseColor(color: DyeColor) {
        this.setDamage(color.getDyeData() and 0x0f)
    }

    fun getType(): Int {
        return this.getNamedTag().getInt("Type")
    }

    fun setType(type: Int) {
        val tag: CompoundTag = if (this.hasCompoundTag()) this.getNamedTag() else CompoundTag()
        tag.putInt("Type", type)
        this.setNamedTag(tag)
    }

    fun addPattern(pattern: BannerPattern) {
        val tag: CompoundTag = if (this.hasCompoundTag()) this.getNamedTag() else CompoundTag()
        val patterns: ListTag<CompoundTag> = tag.getList("Patterns", CompoundTag::class.java)
        patterns.add(CompoundTag("").putInt("Color", pattern.getColor().getDyeData() and 0x0f).putString("Pattern", pattern.getType().getName()))
        tag.putList(patterns)
        this.setNamedTag(tag)
    }

    fun getPattern(index: Int): BannerPattern {
        val tag: CompoundTag = if (this.hasCompoundTag()) this.getNamedTag() else CompoundTag()
        return BannerPattern.fromCompoundTag(if (tag.getList("Patterns").size() > index && index >= 0) tag.getList("Patterns", CompoundTag::class.java).get(index) else CompoundTag())
    }

    fun removePattern(index: Int) {
        val tag: CompoundTag = if (this.hasCompoundTag()) this.getNamedTag() else CompoundTag()
        val patterns: ListTag<CompoundTag> = tag.getList("Patterns", CompoundTag::class.java)
        if (patterns.size() > index && index >= 0) {
            patterns.remove(index)
        }
        this.setNamedTag(tag)
    }

    fun getPatternsSize(): Int {
        return (if (this.hasCompoundTag()) this.getNamedTag() else CompoundTag()).getList("Patterns").size()
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun hasPattern(): Boolean {
        return (if (this.hasCompoundTag()) this.getNamedTag() else CompoundTag()).contains("Patterns")
    }

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Does nothing, used to do a backward compatibility but the content and usage were removed by Cloudburst")
    fun correctNBT() {
    }

    init {
        this.block = Block.get(Block.STANDING_BANNER)
    }
}