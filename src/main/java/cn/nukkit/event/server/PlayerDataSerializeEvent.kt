package cn.nukkit.event.server

import cn.nukkit.event.HandlerList

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
class PlayerDataSerializeEvent(name: String?, serializer: PlayerDataSerializer?) : ServerEvent() {
    private val name: Optional<String>
    private val uuid: Optional<UUID>
    private var serializer: PlayerDataSerializer
    fun getName(): Optional<String> {
        return name
    }

    fun getUuid(): Optional<UUID> {
        return uuid
    }

    fun getSerializer(): PlayerDataSerializer {
        return serializer
    }

    fun setSerializer(serializer: PlayerDataSerializer?) {
        this.serializer = Preconditions.checkNotNull(serializer, "serializer")
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        Preconditions.checkNotNull(name)
        this.serializer = Preconditions.checkNotNull(serializer)
        var uuid: UUID? = null
        try {
            uuid = UUID.fromString(name)
        } catch (e: Exception) {
            // ignore
        }
        this.uuid = Optional.ofNullable(uuid)
        this.name = if (this.uuid.isPresent()) Optional.empty() else Optional.of(name)
    }
}