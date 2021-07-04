package cn.nukkit.item.randomitem

import cn.nukkit.item.Item

/**
 * @author Snake1999
 * @since 2016/1/15
 */
class ConstantItemSelector(item: Item, parent: Selector?) : Selector(parent) {
    protected val item: Item

    constructor(id: Int, parent: Selector?) : this(id, 0, parent) {}
    constructor(id: Int, meta: Integer?, parent: Selector?) : this(id, meta, 1, parent) {}
    constructor(id: Int, meta: Integer?, count: Int, parent: Selector?) : this(Item.get(id, meta, count)!!, parent) {}

    fun getItem(): Item {
        return item
    }

    override fun select(): Object {
        return getItem()
    }

    init {
        this.item = item
    }
}