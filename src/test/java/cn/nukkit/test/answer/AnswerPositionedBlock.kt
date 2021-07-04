package cn.nukkit.test.answer

import cn.nukkit.block.Block

/**
 * @author joserobjr
 */
@RequiredArgsConstructor
class AnswerPositionedBlock : Answer<Block?> {
    private val state: BlockState? = null
    @Override
    fun answer(invocationOnMock: InvocationOnMock): Block {
        val level: Level = invocationOnMock.getMock() as Level
        val arguments: Array<Object> = invocationOnMock.getArguments()
        val x: Int = arguments[0] as Integer
        val y: Int = arguments[1] as Integer
        val z: Int = arguments[2] as Integer
        val layer: Int = arguments[3] as Integer
        return state.getBlock(level, x, y, z, layer)
    }
}