package cn.nukkit.utils.functional

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@FunctionalInterface
interface BlockPositionConsumer {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun accept(x: Int, y: Int, z: Int)

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun validate(fromX: Int, fromY: Int, fromZ: Int, toX: Int, toY: Int, toZ: Int, xInc: Int, yInc: Int, zInc: Int) {
            if (fromX <= toX) {
                Preconditions.checkArgument(xInc > 0, "Invalid xInc")
            } else {
                Preconditions.checkArgument(xInc < 0, "Invalid xInc")
            }
            if (fromY <= toY) {
                Preconditions.checkArgument(yInc > 0, "Invalid yInc")
            } else {
                Preconditions.checkArgument(yInc < 0, "Invalid yInc")
            }
            if (fromZ <= toZ) {
                Preconditions.checkArgument(zInc > 0, "Invalid zInc")
            } else {
                Preconditions.checkArgument(zInc < 0, "Invalid zInc")
            }
        }

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun xzy(fromX: Int, fromY: Int, fromZ: Int, toX: Int, toY: Int, toZ: Int, xInc: Int, yInc: Int, zInc: Int, iterator: BlockPositionConsumer) {
            validate(fromX, fromY, fromZ, toX, toY, toZ, xInc, yInc, zInc)
            val xStream: IntStream = IntIncrementSupplier(fromX, xInc).stream().limit(NukkitMath.floorFloat((toX - fromX) / xInc.toFloat()))
            val yStream: IntStream = IntIncrementSupplier(fromY, yInc).stream().limit(NukkitMath.floorFloat((toY - fromY) / yInc.toFloat()))
            val zStream: IntStream = IntIncrementSupplier(fromZ, zInc).stream().limit(NukkitMath.floorFloat((toZ - fromZ) / zInc.toFloat()))
            xStream.forEachOrdered { x -> zStream.forEachOrdered { z -> yStream.forEachOrdered { y -> iterator.accept(x, y, z) } } }
        }

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun xzy(fromX: Int, fromY: Int, fromZ: Int, toX: Int, toY: Int, toZ: Int, iterator: BlockPositionConsumer) {
            xzy(fromX, fromY, fromZ, toX, toY, toZ, if (fromX <= toX) 1 else -1, if (fromY <= toY) 1 else -1, if (fromZ <= toZ) 1 else -1, iterator)
        }

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun xzy(toX: Int, toY: Int, toZ: Int, iterator: BlockPositionConsumer) {
            xzy(0, 0, 0, toX, toY, toZ, iterator)
        }
    }
}