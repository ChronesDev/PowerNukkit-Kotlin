package cn.nukkit.test.matcher

import lombok.RequiredArgsConstructor

/**
 * @author joserobjr
 */
@RequiredArgsConstructor
class InRange<C : Comparable<C>?> : ArgumentMatcher<C>, Serializable {
    private val from: C? = null
    private val to: C? = null
    @Override
    fun matches(comparable: C): Boolean {
        return from!!.compareTo(comparable) <= 0 && to!!.compareTo(comparable) >= 1
    }
}