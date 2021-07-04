package cn.nukkit.entity.passive

import cn.nukkit.Player

/**
 * @author BeYkeRYkt (Nukkit Project)
 */
class EntitySheep(chunk: FullChunk?, nbt: CompoundTag?) : EntityAnimal(chunk, nbt) {
    var sheared = false
    var color = 0

    @get:Override
    val width: Float
        get() = if (this.isBaby()) {
            0.45f
        } else 0.9f

    @get:Override
    val height: Float
        get() = if (isBaby()) {
            0.65f
        } else 1.3f

    @get:Override
    val name: String
        get() = "Sheep"

    @Override
    fun initEntity() {
        this.setMaxHealth(8)
        if (!this.namedTag.contains("Color")) {
            setColor(randomColor())
        } else {
            setColor(this.namedTag.getByte("Color"))
        }
        if (!this.namedTag.contains("Sheared")) {
            this.namedTag.putByte("Sheared", 0)
        } else {
            sheared = this.namedTag.getBoolean("Sheared")
        }
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_SHEARED, sheared)
    }

    @Override
    fun saveNBT() {
        super.saveNBT()
        this.namedTag.putByte("Color", color)
        this.namedTag.putBoolean("Sheared", sheared)
    }

    @Override
    fun onInteract(player: Player?, item: Item, clickedPos: Vector3?): Boolean {
        if (super.onInteract(player, item, clickedPos)) {
            return true
        }
        if (item is ItemDye) {
            setColor((item as ItemDye).getDyeColor().getWoolData())
            return true
        }
        return item.getId() === Item.SHEARS && shear()
    }

    fun shear(): Boolean {
        if (sheared) {
            return false
        }
        sheared = true
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_SHEARED, true)
        this.level.dropItem(this, Item.get(Item.WOOL, getColor(), ThreadLocalRandom.current().nextInt(2) + 1))
        return true
    }

    @get:Override
    val drops: Array<Any>
        get() = if (this.lastDamageCause is EntityDamageByEntityEvent) {
            arrayOf(Item.get(if (this.isOnFire()) Item.COOKED_MUTTON else Item.RAW_MUTTON), Item.get(Item.WOOL, getColor(), 1))
        } else Item.EMPTY_ARRAY

    fun setColor(color: Int) {
        this.color = color
        this.setDataProperty(ByteEntityData(DATA_COLOUR, color))
        this.namedTag.putByte("Color", this.color)
    }

    fun getColor(): Int {
        return namedTag.getByte("Color")
    }

    private fun randomColor(): Int {
        val random: ThreadLocalRandom = ThreadLocalRandom.current()
        val rand: Double = random.nextDouble(1, 100)
        if (rand <= 0.164) {
            return DyeColor.PINK.getWoolData()
        }
        return if (rand <= 15) {
            if (random.nextBoolean()) DyeColor.BLACK.getWoolData() else if (random.nextBoolean()) DyeColor.GRAY.getWoolData() else DyeColor.LIGHT_GRAY.getWoolData()
        } else DyeColor.WHITE.getWoolData()
    }

    companion object {
        @get:Override
        val networkId = 13
            get() = Companion.field
    }
}