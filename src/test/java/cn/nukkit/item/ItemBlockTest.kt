package cn.nukkit.item

import cn.nukkit.block.BlockID

/**
 * @author joserobjr
 */
@ExtendWith(PowerNukkitExtension::class)
internal class ItemBlockTest {
    var logLevelAdjuster: LogLevelAdjuster = LogLevelAdjuster()
    @Test
    fun badBlockData() {
        val stone: Item = Item.getBlock(BlockID.LEVER)
        assertThat(stone).isInstanceOf(ItemBlock::class.java)
        assertThat(stone.getBlock()).isInstanceOf(BlockLever::class.java)
        logLevelAdjuster.onlyNow(ItemBlock::class.java, Level.ERROR
        ) { stone.setDamage(1000) }
        assertThat(stone.getBlock()).isInstanceOf(BlockUnknown::class.java)
        stone.setDamage(0)
        assertThat(stone.getBlock()).isInstanceOf(BlockLever::class.java)
    }

    @AfterEach
    fun tearDown() {
        logLevelAdjuster.restoreLevels()
    }
}