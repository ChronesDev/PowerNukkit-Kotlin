package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.3.0.0-PN")
interface BlockConnectable {
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    fun getSideAtLayer(layer: Int, face: BlockFace?): Block?

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    fun canConnect(block: Block?): Boolean

    @get:Since("1.3.0.0-PN")
    @get:PowerNukkitOnly
    val isStraight: Boolean
        get() {
            val connections: Set<BlockFace> = connections
            if (connections.size() !== 2) {
                return false
            }
            val iterator: Iterator<BlockFace> = connections.iterator()
            val a: BlockFace = iterator.next()
            val b: BlockFace = iterator.next()
            return a.getOpposite() === b
        }

    @get:Since("1.3.0.0-PN")
    @get:PowerNukkitOnly
    val connections: Set<Any>
        get() {
            val connections: EnumSet<BlockFace> = EnumSet.noneOf(BlockFace::class.java)
            for (blockFace in BlockFace.Plane.HORIZONTAL) {
                if (isConnected(blockFace)) {
                    connections.add(blockFace)
                }
            }
            return connections
        }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    fun isConnected(face: BlockFace?): Boolean {
        return canConnect(getSideAtLayer(0, face))
    }
}