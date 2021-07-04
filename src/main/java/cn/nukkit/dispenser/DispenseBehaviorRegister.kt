package cn.nukkit.dispenser

import cn.nukkit.block.BlockID

/**
 * @author CreeperFace
 */
object DispenseBehaviorRegister {
    private val behaviors: Map<Integer, DispenseBehavior> = HashMap()
    private val defaultBehavior: DispenseBehavior = DefaultDispenseBehavior()
    fun registerBehavior(itemId: Int, behavior: DispenseBehavior?) {
        behaviors.put(itemId, behavior)
    }

    fun getBehavior(id: Int): DispenseBehavior {
        return behaviors.getOrDefault(id, defaultBehavior)
    }

    fun removeDispenseBehavior(id: Int) {
        behaviors.remove(id)
    }

    fun init() {
        registerBehavior(ItemID.BOAT, BoatDispenseBehavior())
        registerBehavior(ItemID.BUCKET, BucketDispenseBehavior())
        registerBehavior(ItemID.DYE, DyeDispenseBehavior())
        registerBehavior(ItemID.FIREWORKS, FireworksDispenseBehavior())
        registerBehavior(ItemID.FLINT_AND_STEEL, FlintAndSteelDispenseBehavior())
        registerBehavior(BlockID.SHULKER_BOX, ShulkerBoxDispenseBehavior())
        registerBehavior(BlockID.UNDYED_SHULKER_BOX, ShulkerBoxDispenseBehavior())
        registerBehavior(ItemID.SPAWN_EGG, SpawnEggDispenseBehavior())
        registerBehavior(BlockID.TNT, TNTDispenseBehavior())
        registerBehavior(ItemID.ARROW, object : ProjectileDispenseBehavior("Arrow") {
            @get:Override
            protected override val motion: Double
                protected get() = super.getMotion() * 1.5
        })
        //TODO: tipped arrow
        //TODO: spectral arrow
        registerBehavior(ItemID.EGG, ProjectileDispenseBehavior("Egg"))
        registerBehavior(ItemID.SNOWBALL, ProjectileDispenseBehavior("Snowball"))
        registerBehavior(ItemID.EXPERIENCE_BOTTLE, object : ProjectileDispenseBehavior("ThrownExpBottle") {
            @get:Override
            protected override val accuracy: Float
                protected get() = super.getAccuracy() * 0.5f

            @get:Override
            protected override val motion: Double
                protected get() = super.getMotion() * 1.25
        })
        registerBehavior(ItemID.SPLASH_POTION, object : ProjectileDispenseBehavior("ThrownPotion") {
            @get:Override
            protected override val accuracy: Float
                protected get() = super.getAccuracy() * 0.5f

            @get:Override
            protected override val motion: Double
                protected get() = super.getMotion() * 1.25
        })
        //        registerBehavior(ItemID.LINGERING_POTION, new ProjectileDispenseBehavior("LingeringPotion")); //TODO
        registerBehavior(ItemID.TRIDENT, object : ProjectileDispenseBehavior("ThrownTrident") {
            @get:Override
            protected override val accuracy: Float
                protected get() = super.getAccuracy() * 0.5f

            @get:Override
            protected override val motion: Double
                protected get() = super.getMotion() * 1.25
        })
    }
}