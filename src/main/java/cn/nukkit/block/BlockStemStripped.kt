package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
@Since("1.4.0.0-PN")
abstract class BlockStemStripped @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockStem(meta) {
    @get:Override
    protected override val strippedState: BlockState
        protected get() = getCurrentState()

    @Override
    override fun canBeActivated(): Boolean {
        return false
    }

    @Override
    override fun onActivate(@Nonnull item: Item?, player: Player?): Boolean {
        return false
    }
}