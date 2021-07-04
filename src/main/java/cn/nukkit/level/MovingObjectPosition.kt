package cn.nukkit.level

import cn.nukkit.api.DeprecationDetails

/**
 * @author MagicDroidX (Nukkit Project)
 */
class MovingObjectPosition {
    /**
     * 0 = block, 1 = entity
     */
    var typeOfHit = 0
    var blockX = 0
    var blockY = 0
    var blockZ = 0

    /**
     * Which side was hit. If its -1 then it went the full length of the ray trace.
     * Bottom = 0, Top = 1, East = 2, West = 3, North = 4, South = 5.
     */
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Magic numbers and not encapsulated", replaceWith = "getFaceHit(), setFaceHit(BlockFace)")
    var sideHit = 0
    var hitVector: Vector3? = null
    var entityHit: Entity? = null

    @get:SuppressWarnings("java:S1874")
    @get:Nullable
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:SuppressWarnings("java:S1874")
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var faceHit: BlockFace?
        get() = when (sideHit) {
            0 -> BlockFace.DOWN
            1 -> BlockFace.UP
            2 -> BlockFace.EAST
            3 -> BlockFace.WEST
            4 -> BlockFace.NORTH
            5 -> BlockFace.SOUTH
            else -> null
        }
        set(face) {
            if (face == null) {
                sideHit = -1
                return
            }
            sideHit = when (face) {
                DOWN -> 0
                UP -> 1
                NORTH -> 4
                SOUTH -> 5
                WEST -> 3
                EAST -> 2
                else -> -1
            }
        }

    @Override
    override fun toString(): String {
        return "MovingObjectPosition{" +
                "typeOfHit=" + typeOfHit +
                ", blockX=" + blockX +
                ", blockY=" + blockY +
                ", blockZ=" + blockZ +
                ", sideHit=" + sideHit +
                ", hitVector=" + hitVector +
                ", entityHit=" + entityHit +
                '}'
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun fromBlock(x: Int, y: Int, z: Int, face: BlockFace?, hitVector: Vector3): MovingObjectPosition {
            val objectPosition = MovingObjectPosition()
            objectPosition.typeOfHit = 0
            objectPosition.blockX = x
            objectPosition.blockY = y
            objectPosition.blockZ = z
            objectPosition.hitVector = Vector3(hitVector.x, hitVector.y, hitVector.z)
            objectPosition.faceHit = face
            return objectPosition
        }

        @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed: sideHit not being filled")
        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", reason = "Magic number in side param", replaceWith = "fromBlock(int,int,int,BlockFace,Vector3)")
        fun fromBlock(x: Int, y: Int, z: Int, side: Int, hitVector: Vector3): MovingObjectPosition {
            val objectPosition = MovingObjectPosition()
            objectPosition.typeOfHit = 0
            objectPosition.blockX = x
            objectPosition.blockY = y
            objectPosition.blockZ = z
            objectPosition.sideHit = side
            objectPosition.hitVector = Vector3(hitVector.x, hitVector.y, hitVector.z)
            return objectPosition
        }

        fun fromEntity(entity: Entity): MovingObjectPosition {
            val objectPosition = MovingObjectPosition()
            objectPosition.typeOfHit = 1
            objectPosition.entityHit = entity
            objectPosition.hitVector = Vector3(entity.x, entity.y, entity.z)
            return objectPosition
        }
    }
}