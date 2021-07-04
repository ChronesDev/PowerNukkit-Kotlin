package cn.nukkit.level.particle

import cn.nukkit.block.Block

/**
 * @author xtypr
 * @since 2015/11/21
 */
class TerrainParticle(pos: Vector3, block: Block) : GenericParticle(pos, Particle.TYPE_TERRAIN, GlobalBlockPalette.getOrCreateRuntimeId(block.getId(), block.getDamage()))