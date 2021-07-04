package cn.nukkit.level.format.updater

import cn.nukkit.blockstate.BlockState

@ExtendWith(PowerNukkitExtension::class)
internal class NewLeafUpdaterTest {
    val section: ChunkSection = ChunkSection(0)
    val x = 5
    val y = 6
    val z = 7
    val updater: NewLeafUpdater = NewLeafUpdater(section)
    val acacia: BlockState = BlockState.of(LEAVES2)
    val darkOak: BlockState = BlockState.of(LEAVES2, 1)
    var original: BlockState? = null
    fun setOriginal(original: BlockState?) {
        this.original = original
        section.setBlockState(x, y, z, original)
    }

    fun check(expected: BlockState?, change: Boolean) {
        assertEquals(change, updater.update(0, 0, 0, x, y, z, original))
        assertEquals(expected, section.getBlockState(x, y, z, 0))
    }

    @BeforeEach
    fun configure() {
        updater.setForceOldSystem(false)
        section.delayPaletteUpdates()
    }

    @Test
    fun simpleAcacia() {
        setOriginal(acacia)
        check(acacia, false)
    }

    @Test
    fun simpleDarkOak() {
        setOriginal(darkOak)
        check(darkOak, false)
    }

    @Test
    fun acaciaOldPersistent() {
        updater.setForceOldSystem(true)
        setOriginal(acacia.withData(4))
        check(acacia.withData(4), false)
    }

    @Test
    fun acaciaNewPersistent() {
        setOriginal(acacia.withData(4))
        check(acacia.withData(4), false)
    }

    @Test
    fun acaciaOldCheckDecay() {
        setOriginal(acacia.withData(2))
        check(acacia.withData(8), true)
    }

    @Test
    fun acaciaNewCheckDecay() {
        setOriginal(acacia.withData(8))
        check(acacia.withData(8), false)
    }

    @Test
    fun acaciaOldPersistentOldCheckDecay() {
        setOriginal(acacia.withData(6))
        check(acacia.withData(12), true)
    }

    @Test
    fun darkOakOldPersistentOldCheckDecay() {
        setOriginal(darkOak.withData(7))
        check(darkOak.withData(13), true)
    }

    @Test
    fun darkOakOldPersistent() {
        updater.setForceOldSystem(true)
        setOriginal(darkOak.withData(5))
        check(darkOak.withData(5), false)
    }

    @Test
    fun darkOakNewPersistent() {
        setOriginal(darkOak.withData(5))
        check(darkOak.withData(5), false)
    }

    @Test
    fun darkOakOldCheckDecay() {
        setOriginal(darkOak.withData(3))
        check(darkOak.withData(9), true)
    }

    @Test
    fun darkOakNewCheckDecay() {
        setOriginal(darkOak.withData(9))
        check(darkOak.withData(9), false)
    }

    @Test
    fun issue482() { // https://github.com/PowerNukkit/PowerNukkit/issues/482
        setOriginal(darkOak.withData(11))
        check(darkOak.withData(9), true)
    }
}