package cn.nukkit.item

import cn.nukkit.api.Since

/**
 * @author PetteriM1
 */
@Since("1.4.0.0-PN")
class ItemRecordPigstep @Since("1.4.0.0-PN") constructor(meta: Integer?, count: Int) : ItemRecord(RECORD_PIGSTEP, meta, count) {
    @Since("1.4.0.0-PN")
    constructor() : this(0, 1) {
    }

    @Since("1.4.0.0-PN")
    constructor(meta: Integer?) : this(meta, 1) {
    }

    @Override
    override fun getSoundId(): String {
        return "record.pigstep"
    }
}