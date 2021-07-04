package cn.nukkit.blockentity

import cn.nukkit.Player

/**
 * @author Rover656
 */
class BlockEntityBeacon(chunk: FullChunk?, nbt: CompoundTag) : BlockEntitySpawnable(chunk, nbt) {
    @Override
    protected override fun initBlockEntity() {
        if (!namedTag.contains("Lock")) {
            namedTag.putString("Lock", "")
        }
        if (!namedTag.contains("Levels")) {
            namedTag.putInt("Levels", 0)
        }
        if (!namedTag.contains("Primary")) {
            namedTag.putInt("Primary", 0)
        }
        if (!namedTag.contains("Secondary")) {
            namedTag.putInt("Secondary", 0)
        }
        scheduleUpdate()
        super.initBlockEntity()
    }

    @get:Override
    override val isBlockEntityValid: Boolean
        get() {
            val blockID: Int = getBlock().getId()
            return blockID == Block.BEACON
        }

    @get:Override
    override val spawnCompound: CompoundTag
        get() = CompoundTag()
                .putString("id", BlockEntity.BEACON)
                .putInt("x", this.x as Int)
                .putInt("y", this.y as Int)
                .putInt("z", this.z as Int)
                .putString("Lock", this.namedTag.getString("Lock"))
                .putInt("Levels", this.namedTag.getInt("Levels"))
                .putInt("primary", this.namedTag.getInt("Primary"))
                .putInt("secondary", this.namedTag.getInt("Secondary"))
    private var currentTick: Long = 0

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    @Override
    override fun onUpdate(): Boolean {
        //Only apply effects every 4 secs
        if (currentTick++ % 80 != 0L) {
            return true
        }
        val oldPowerLevel = powerLevel
        //Get the power level based on the pyramid
        powerLevel = calculatePowerLevel()
        val newPowerLevel = powerLevel

        //Skip beacons that do not have a pyramid or sky access
        if (newPowerLevel < 1 || !hasSkyAccess()) {
            if (oldPowerLevel > 0) {
                this.getLevel().addSound(this, Sound.BEACON_DEACTIVATE)
            }
            return true
        } else if (oldPowerLevel < 1) {
            this.getLevel().addSound(this, Sound.BEACON_ACTIVATE)
        } else {
            this.getLevel().addSound(this, Sound.BEACON_AMBIENT)
        }

        //Get all players in game
        val players: Map<Long, Player> = this.level.getPlayers()

        //Calculate vars for beacon power
        val range = 10 + powerLevel * 10
        val duration = 9 + powerLevel * 2
        if (!isPrimaryAllowed(primaryPower, powerLevel)) {
            return true
        }
        for (entry in players.entrySet()) {
            val p: Player = entry.getValue()

            //If the player is in range
            if (p.distance(this) < range) {
                var e: Effect
                if (primaryPower != 0) {
                    //Apply the primary power
                    e = Effect.getEffect(primaryPower)

                    //Set duration
                    e.setDuration(duration * 20)

                    //If secondary is selected as the primary too, apply 2 amplification
                    if (powerLevel == POWER_LEVEL_MAX && secondaryPower == primaryPower) {
                        e.setAmplifier(1)
                    }

                    //Add the effect
                    p.addEffect(e)
                }

                //If we have a secondary power as regen, apply it
                if (powerLevel == POWER_LEVEL_MAX && secondaryPower == Effect.REGENERATION) {
                    //Get the regen effect
                    e = Effect.getEffect(Effect.REGENERATION)

                    //Set duration
                    e.setDuration(duration * 20)

                    //Add effect
                    p.addEffect(e)
                }
            }
        }
        return true
    }

    private fun hasSkyAccess(): Boolean {
        val tileX: Int = getFloorX()
        val tileY: Int = getFloorY()
        val tileZ: Int = getFloorZ()

        //Check every block from our y coord to the top of the world
        for (y in tileY + 1..255) {
            val testBlockId: Int = level.getBlockIdAt(tileX, y, tileZ)
            if (!Block.transparent.get(testBlockId)) {
                //There is no sky access
                return false
            }
        }
        return true
    }

    private fun calculatePowerLevel(): Int {
        val tileX: Int = getFloorX()
        val tileY: Int = getFloorY()
        val tileZ: Int = getFloorZ()

        //The power level that we're testing for
        for (powerLevel in 1..POWER_LEVEL_MAX) {
            val queryY = tileY - powerLevel //Layer below the beacon block
            for (queryX in tileX - powerLevel..tileX + powerLevel) {
                for (queryZ in tileZ - powerLevel..tileZ + powerLevel) {
                    val testBlockId: Int = level.getBlockIdAt(queryX, queryY, queryZ)
                    if (testBlockId != Block.IRON_BLOCK && testBlockId != Block.GOLD_BLOCK && testBlockId != Block.EMERALD_BLOCK && testBlockId != Block.DIAMOND_BLOCK && testBlockId != Block.NETHERITE_BLOCK) {
                        return powerLevel - 1
                    }
                }
            }
        }
        return POWER_LEVEL_MAX
    }

    var powerLevel: Int
        get() = namedTag.getInt("Levels")
        set(level) {
            val currentLevel = powerLevel
            if (level != currentLevel) {
                namedTag.putInt("Levels", level)
                setDirty()
                this.spawnToAll()
            }
        }
    var primaryPower: Int
        get() = namedTag.getInt("Primary")
        set(power) {
            val currentPower = primaryPower
            if (power != currentPower) {
                namedTag.putInt("Primary", power)
                setDirty()
                this.spawnToAll()
            }
        }
    var secondaryPower: Int
        get() = namedTag.getInt("Secondary")
        set(power) {
            val currentPower = secondaryPower
            if (power != currentPower) {
                namedTag.putInt("Secondary", power)
                setDirty()
                this.spawnToAll()
            }
        }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    @Override
    override fun updateCompoundTag(nbt: CompoundTag, player: Player): Boolean {
        if (!nbt.getString("id").equals(BlockEntity.BEACON)) {
            return false
        }
        val primary: Int = nbt.getInt("primary")
        if (!isPrimaryAllowed(primary, powerLevel)) {
            return false
        }
        val secondary: Int = nbt.getInt("secondary")
        if (secondary != 0 && secondary != primary && secondary != Effect.REGENERATION) {
            return false
        }
        primaryPower = primary
        secondaryPower = secondary
        this.getLevel().addSound(this, Sound.BEACON_POWER)
        val inv: BeaconInventory = player.getWindowById(Player.BEACON_WINDOW_ID) as BeaconInventory
        inv.setItem(0, ItemBlock(Block.get(BlockID.AIR), 0, 0))
        return true
    }

    companion object {
        private const val POWER_LEVEL_MAX = 4
        private fun isPrimaryAllowed(primary: Int, powerLevel: Int): Boolean {
            return (primary == Effect.SPEED || primary == Effect.HASTE) && powerLevel >= 1 ||
                    (primary == Effect.DAMAGE_RESISTANCE || primary == Effect.JUMP) && powerLevel >= 2 ||
                    primary == Effect.STRENGTH && powerLevel >= 3
        }
    }
}