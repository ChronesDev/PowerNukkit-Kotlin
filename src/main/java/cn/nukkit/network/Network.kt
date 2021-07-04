package cn.nukkit.network

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
class Network(server: Server) {
    private var packetPool: Array<Class<out DataPacket?>?> = arrayOfNulls<Class>(256)
    private val server: Server
    private val interfaces: Set<SourceInterface> = HashSet()
    private val advancedInterfaces: Set<AdvancedSourceInterface> = HashSet()
    var upload = 0.0
        private set
    var download = 0.0
        private set
    private var name: String? = null
    var subName: String? = null
    fun addStatistics(upload: Double, download: Double) {
        this.upload += upload
        this.download += download
    }

    fun resetStatistics() {
        upload = 0.0
        download = 0.0
    }

    fun getInterfaces(): Set<SourceInterface> {
        return interfaces
    }

    fun processInterfaces() {
        for (interfaz in interfaces) {
            try {
                interfaz.process()
            } catch (e: Exception) {
                log.fatal(server.getLanguage().translateString("nukkit.server.networkError", interfaz.getClass().getName(), Utils.getExceptionMessage(e)), e)
                interfaz.emergencyShutdown()
                unregisterInterface(interfaz)
            }
        }
    }

    fun registerInterface(interfaz: SourceInterface) {
        interfaces.add(interfaz)
        if (interfaz is AdvancedSourceInterface) {
            advancedInterfaces.add(interfaz)
            interfaz.setNetwork(this)
        }
        interfaz.setName(name.toString() + "!@#" + subName)
    }

    fun unregisterInterface(sourceInterface: SourceInterface?) {
        interfaces.remove(sourceInterface)
        if (sourceInterface is AdvancedSourceInterface) {
            advancedInterfaces.remove(sourceInterface)
        }
    }

    fun setName(name: String?) {
        this.name = name
        updateName()
    }

    fun getName(): String? {
        return name
    }

    fun updateName() {
        for (interfaz in interfaces) {
            interfaz.setName(name.toString() + "!@#" + subName)
        }
    }

    fun registerPacket(id: Byte, clazz: Class<out DataPacket?>?) {
        packetPool[id and 0xff] = clazz
    }

    fun getServer(): Server {
        return server
    }

    fun processBatch(packet: BatchPacket, player: Player) {
        val packets: List<DataPacket> = ObjectArrayList()
        try {
            processBatch(packet.payload, packets)
        } catch (e: ProtocolException) {
            player.close("", e.getMessage())
            log.error("Unable to process player packets ", e)
        }
    }

    @Since("1.4.0.0-PN")
    @Throws(ProtocolException::class)
    fun processBatch(payload: ByteArray?, packets: Collection<DataPacket?>) {
        val data: ByteArray
        data = try {
            inflateRaw(payload)
        } catch (e: Exception) {
            log.debug("Exception while inflating batch packet", e)
            return
        }
        val stream = BinaryStream(data)
        try {
            var count = 0
            while (!stream.feof()) {
                count++
                if (count >= 1000) {
                    throw ProtocolException("Illegal batch with $count packets")
                }
                val buf: ByteArray = stream.getByteArray()
                val bais = ByteArrayInputStream(buf)
                val header = VarInt.readUnsignedVarInt(bais) as Int

                // | Client ID | Sender ID | Packet ID |
                // |   2 bits  |   2 bits  |  10 bits  |
                val packetId = header and 0x3ff
                val pk: DataPacket? = this.getPacket(packetId)
                if (pk != null) {
                    pk.setBuffer(buf, buf.size - bais.available())
                    try {
                        pk.decode()
                    } catch (e: Exception) {
                        if (log.isTraceEnabled()) {
                            log.trace("Dumping Packet\n{}", ByteBufUtil.prettyHexDump(Unpooled.wrappedBuffer(buf)))
                        }
                        log.error("Unable to decode packet", e)
                        throw IllegalStateException("Unable to decode " + pk.getClass().getSimpleName())
                    }
                    packets.add(pk)
                } else {
                    log.debug("Received unknown packet with ID: {}", Integer.toHexString(packetId))
                }
            }
        } catch (e: Exception) {
            log.debug("Error whilst processing {} batched packets", packets.size())
        }
    }

    /**
     * Process packets obtained from batch packets
     * Required to perform additional analyses and filter unnecessary packets
     *
     * @param packets
     */
    @PowerNukkitDifference(info = "Handles exception if on of the packets in the list fails")
    fun processPackets(player: Player, packets: List<DataPacket?>) {
        if (packets.isEmpty()) return
        packets.forEach { p ->
            try {
                player.handleDataPacket(p)
            } catch (e: Exception) {
                if (log.isWarnEnabled()) {
                    log.warn("Error whilst processing the packet {}:{} for {} (full data: {})",
                            p.pid(), p.getClass().getSimpleName(),
                            player.getName(), p.toString(),
                            e
                    )
                }
            }
        }
    }

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", by = "Cloudburst Nukkit", reason = "Changed the id to int without backward compatibility", replaceWith = "getPacket(int id)")
    fun getPacket(id: Byte): DataPacket? {
        return getPacket(id.toInt())
    }

    @Since("1.4.0.0-PN")
    fun getPacket(id: Int): DataPacket? {
        val clazz: Class<out DataPacket?>? = packetPool[id]
        if (clazz != null) {
            try {
                return clazz.newInstance()
            } catch (e: Exception) {
                log.error("Error while creating a class for the packet id {}", id, e)
            }
        }
        return null
    }

    fun sendPacket(socketAddress: InetSocketAddress?, payload: ByteBuf?) {
        for (sourceInterface in advancedInterfaces) {
            sourceInterface.sendRawPacket(socketAddress, payload)
        }
    }

    fun blockAddress(address: InetAddress?) {
        for (sourceInterface in advancedInterfaces) {
            sourceInterface.blockAddress(address)
        }
    }

    fun blockAddress(address: InetAddress?, timeout: Int) {
        for (sourceInterface in advancedInterfaces) {
            sourceInterface.blockAddress(address, timeout)
        }
    }

    fun unblockAddress(address: InetAddress?) {
        for (sourceInterface in advancedInterfaces) {
            sourceInterface.unblockAddress(address)
        }
    }

    private fun registerPackets() {
        packetPool = arrayOfNulls<Class>(256)
        registerPacket(ProtocolInfo.ADD_ENTITY_PACKET, AddEntityPacket::class.java)
        registerPacket(ProtocolInfo.ADD_ITEM_ENTITY_PACKET, AddItemEntityPacket::class.java)
        registerPacket(ProtocolInfo.ADD_PAINTING_PACKET, AddPaintingPacket::class.java)
        registerPacket(ProtocolInfo.ADD_PLAYER_PACKET, AddPlayerPacket::class.java)
        registerPacket(ProtocolInfo.ADVENTURE_SETTINGS_PACKET, AdventureSettingsPacket::class.java)
        registerPacket(ProtocolInfo.ANIMATE_PACKET, AnimatePacket::class.java)
        registerPacket(ProtocolInfo.ANVIL_DAMAGE_PACKET, AnvilDamagePacket::class.java)
        registerPacket(ProtocolInfo.AVAILABLE_COMMANDS_PACKET, AvailableCommandsPacket::class.java)
        registerPacket(ProtocolInfo.BATCH_PACKET, BatchPacket::class.java)
        registerPacket(ProtocolInfo.BLOCK_ENTITY_DATA_PACKET, BlockEntityDataPacket::class.java)
        registerPacket(ProtocolInfo.BLOCK_EVENT_PACKET, BlockEventPacket::class.java)
        registerPacket(ProtocolInfo.BLOCK_PICK_REQUEST_PACKET, BlockPickRequestPacket::class.java)
        registerPacket(ProtocolInfo.BOOK_EDIT_PACKET, BookEditPacket::class.java)
        registerPacket(ProtocolInfo.BOSS_EVENT_PACKET, BossEventPacket::class.java)
        registerPacket(ProtocolInfo.CHANGE_DIMENSION_PACKET, ChangeDimensionPacket::class.java)
        registerPacket(ProtocolInfo.CHUNK_RADIUS_UPDATED_PACKET, ChunkRadiusUpdatedPacket::class.java)
        registerPacket(ProtocolInfo.CLIENTBOUND_MAP_ITEM_DATA_PACKET, ClientboundMapItemDataPacket::class.java)
        registerPacket(ProtocolInfo.COMMAND_REQUEST_PACKET, CommandRequestPacket::class.java)
        registerPacket(ProtocolInfo.CONTAINER_CLOSE_PACKET, ContainerClosePacket::class.java)
        registerPacket(ProtocolInfo.CONTAINER_OPEN_PACKET, ContainerOpenPacket::class.java)
        registerPacket(ProtocolInfo.CONTAINER_SET_DATA_PACKET, ContainerSetDataPacket::class.java)
        registerPacket(ProtocolInfo.CRAFTING_DATA_PACKET, CraftingDataPacket::class.java)
        registerPacket(ProtocolInfo.CRAFTING_EVENT_PACKET, CraftingEventPacket::class.java)
        registerPacket(ProtocolInfo.DISCONNECT_PACKET, DisconnectPacket::class.java)
        registerPacket(ProtocolInfo.ENTITY_EVENT_PACKET, EntityEventPacket::class.java)
        registerPacket(ProtocolInfo.ENTITY_FALL_PACKET, EntityFallPacket::class.java)
        registerPacket(ProtocolInfo.FULL_CHUNK_DATA_PACKET, LevelChunkPacket::class.java)
        registerPacket(ProtocolInfo.GAME_RULES_CHANGED_PACKET, GameRulesChangedPacket::class.java)
        registerPacket(ProtocolInfo.HURT_ARMOR_PACKET, HurtArmorPacket::class.java)
        registerPacket(ProtocolInfo.INTERACT_PACKET, InteractPacket::class.java)
        registerPacket(ProtocolInfo.INVENTORY_CONTENT_PACKET, InventoryContentPacket::class.java)
        registerPacket(ProtocolInfo.INVENTORY_SLOT_PACKET, InventorySlotPacket::class.java)
        registerPacket(ProtocolInfo.INVENTORY_TRANSACTION_PACKET, InventoryTransactionPacket::class.java)
        registerPacket(ProtocolInfo.ITEM_FRAME_DROP_ITEM_PACKET, ItemFrameDropItemPacket::class.java)
        registerPacket(ProtocolInfo.LEVEL_EVENT_PACKET, LevelEventPacket::class.java)
        registerPacket(ProtocolInfo.LEVEL_SOUND_EVENT_PACKET_V1, LevelSoundEventPacketV1::class.java)
        registerPacket(ProtocolInfo.LOGIN_PACKET, LoginPacket::class.java)
        registerPacket(ProtocolInfo.MAP_INFO_REQUEST_PACKET, MapInfoRequestPacket::class.java)
        registerPacket(ProtocolInfo.MOB_ARMOR_EQUIPMENT_PACKET, MobArmorEquipmentPacket::class.java)
        registerPacket(ProtocolInfo.MOB_EQUIPMENT_PACKET, MobEquipmentPacket::class.java)
        registerPacket(ProtocolInfo.MODAL_FORM_REQUEST_PACKET, ModalFormRequestPacket::class.java)
        registerPacket(ProtocolInfo.MODAL_FORM_RESPONSE_PACKET, ModalFormResponsePacket::class.java)
        registerPacket(ProtocolInfo.MOVE_ENTITY_ABSOLUTE_PACKET, MoveEntityAbsolutePacket::class.java)
        registerPacket(ProtocolInfo.MOVE_PLAYER_PACKET, MovePlayerPacket::class.java)
        registerPacket(ProtocolInfo.PLAYER_ACTION_PACKET, PlayerActionPacket::class.java)
        registerPacket(ProtocolInfo.PLAYER_INPUT_PACKET, PlayerInputPacket::class.java)
        registerPacket(ProtocolInfo.PLAYER_LIST_PACKET, PlayerListPacket::class.java)
        registerPacket(ProtocolInfo.PLAYER_HOTBAR_PACKET, PlayerHotbarPacket::class.java)
        registerPacket(ProtocolInfo.PLAY_SOUND_PACKET, PlaySoundPacket::class.java)
        registerPacket(ProtocolInfo.PLAY_STATUS_PACKET, PlayStatusPacket::class.java)
        registerPacket(ProtocolInfo.REMOVE_ENTITY_PACKET, RemoveEntityPacket::class.java)
        registerPacket(ProtocolInfo.REQUEST_CHUNK_RADIUS_PACKET, RequestChunkRadiusPacket::class.java)
        registerPacket(ProtocolInfo.RESOURCE_PACKS_INFO_PACKET, ResourcePacksInfoPacket::class.java)
        registerPacket(ProtocolInfo.RESOURCE_PACK_STACK_PACKET, ResourcePackStackPacket::class.java)
        registerPacket(ProtocolInfo.RESOURCE_PACK_CLIENT_RESPONSE_PACKET, ResourcePackClientResponsePacket::class.java)
        registerPacket(ProtocolInfo.RESOURCE_PACK_DATA_INFO_PACKET, ResourcePackDataInfoPacket::class.java)
        registerPacket(ProtocolInfo.RESOURCE_PACK_CHUNK_DATA_PACKET, ResourcePackChunkDataPacket::class.java)
        registerPacket(ProtocolInfo.RESOURCE_PACK_CHUNK_REQUEST_PACKET, ResourcePackChunkRequestPacket::class.java)
        registerPacket(ProtocolInfo.PLAYER_SKIN_PACKET, PlayerSkinPacket::class.java)
        registerPacket(ProtocolInfo.RESPAWN_PACKET, RespawnPacket::class.java)
        registerPacket(ProtocolInfo.RIDER_JUMP_PACKET, RiderJumpPacket::class.java)
        registerPacket(ProtocolInfo.SET_COMMANDS_ENABLED_PACKET, SetCommandsEnabledPacket::class.java)
        registerPacket(ProtocolInfo.SET_DIFFICULTY_PACKET, SetDifficultyPacket::class.java)
        registerPacket(ProtocolInfo.SET_ENTITY_DATA_PACKET, SetEntityDataPacket::class.java)
        registerPacket(ProtocolInfo.SET_ENTITY_LINK_PACKET, SetEntityLinkPacket::class.java)
        registerPacket(ProtocolInfo.SET_ENTITY_MOTION_PACKET, SetEntityMotionPacket::class.java)
        registerPacket(ProtocolInfo.SET_HEALTH_PACKET, SetHealthPacket::class.java)
        registerPacket(ProtocolInfo.SET_PLAYER_GAME_TYPE_PACKET, SetPlayerGameTypePacket::class.java)
        registerPacket(ProtocolInfo.SET_SPAWN_POSITION_PACKET, SetSpawnPositionPacket::class.java)
        registerPacket(ProtocolInfo.SET_TITLE_PACKET, SetTitlePacket::class.java)
        registerPacket(ProtocolInfo.SET_TIME_PACKET, SetTimePacket::class.java)
        registerPacket(ProtocolInfo.SERVER_SETTINGS_REQUEST_PACKET, ServerSettingsRequestPacket::class.java)
        registerPacket(ProtocolInfo.SERVER_SETTINGS_RESPONSE_PACKET, ServerSettingsResponsePacket::class.java)
        registerPacket(ProtocolInfo.SHOW_CREDITS_PACKET, ShowCreditsPacket::class.java)
        registerPacket(ProtocolInfo.SPAWN_EXPERIENCE_ORB_PACKET, SpawnExperienceOrbPacket::class.java)
        registerPacket(ProtocolInfo.START_GAME_PACKET, StartGamePacket::class.java)
        registerPacket(ProtocolInfo.TAKE_ITEM_ENTITY_PACKET, TakeItemEntityPacket::class.java)
        registerPacket(ProtocolInfo.TEXT_PACKET, TextPacket::class.java)
        registerPacket(ProtocolInfo.UPDATE_ATTRIBUTES_PACKET, UpdateAttributesPacket::class.java)
        registerPacket(ProtocolInfo.UPDATE_BLOCK_PACKET, UpdateBlockPacket::class.java)
        registerPacket(ProtocolInfo.UPDATE_TRADE_PACKET, UpdateTradePacket::class.java)
        registerPacket(ProtocolInfo.MOVE_ENTITY_DELTA_PACKET, MoveEntityDeltaPacket::class.java)
        registerPacket(ProtocolInfo.SET_LOCAL_PLAYER_AS_INITIALIZED_PACKET, SetLocalPlayerAsInitializedPacket::class.java)
        registerPacket(ProtocolInfo.NETWORK_STACK_LATENCY_PACKET, NetworkStackLatencyPacket::class.java)
        registerPacket(ProtocolInfo.UPDATE_SOFT_ENUM_PACKET, UpdateSoftEnumPacket::class.java)
        registerPacket(ProtocolInfo.NETWORK_CHUNK_PUBLISHER_UPDATE_PACKET, NetworkChunkPublisherUpdatePacket::class.java)
        registerPacket(ProtocolInfo.AVAILABLE_ENTITY_IDENTIFIERS_PACKET, AvailableEntityIdentifiersPacket::class.java)
        registerPacket(ProtocolInfo.LEVEL_SOUND_EVENT_PACKET_V2, LevelSoundEventPacket::class.java)
        registerPacket(ProtocolInfo.SCRIPT_CUSTOM_EVENT_PACKET, ScriptCustomEventPacket::class.java)
        registerPacket(ProtocolInfo.SPAWN_PARTICLE_EFFECT_PACKET, SpawnParticleEffectPacket::class.java)
        registerPacket(ProtocolInfo.BIOME_DEFINITION_LIST_PACKET, BiomeDefinitionListPacket::class.java)
        registerPacket(ProtocolInfo.LEVEL_SOUND_EVENT_PACKET, LevelSoundEventPacket::class.java)
        registerPacket(ProtocolInfo.LEVEL_EVENT_GENERIC_PACKET, LevelEventGenericPacket::class.java)
        registerPacket(ProtocolInfo.LECTERN_UPDATE_PACKET, LecternUpdatePacket::class.java)
        registerPacket(ProtocolInfo.VIDEO_STREAM_CONNECT_PACKET, VideoStreamConnectPacket::class.java)
        registerPacket(ProtocolInfo.CLIENT_CACHE_STATUS_PACKET, ClientCacheStatusPacket::class.java)
        registerPacket(ProtocolInfo.MAP_CREATE_LOCKED_COPY_PACKET, MapCreateLockedCopyPacket::class.java)
        registerPacket(ProtocolInfo.EMOTE_PACKET, EmotePacket::class.java)
        registerPacket(ProtocolInfo.ON_SCREEN_TEXTURE_ANIMATION_PACKET, OnScreenTextureAnimationPacket::class.java)
        registerPacket(ProtocolInfo.COMPLETED_USING_ITEM_PACKET, CompletedUsingItemPacket::class.java)
        registerPacket(ProtocolInfo.CODE_BUILDER_PACKET, CodeBuilderPacket::class.java)
        registerPacket(ProtocolInfo.CREATIVE_CONTENT_PACKET, CreativeContentPacket::class.java)
        registerPacket(ProtocolInfo.DEBUG_INFO_PACKET, DebugInfoPacket::class.java)
        registerPacket(ProtocolInfo.EMOTE_LIST_PACKET, EmoteListPacket::class.java)
        registerPacket(ProtocolInfo.ITEM_STACK_REQUEST_PACKET, ItemStackRequestPacket::class.java)
        registerPacket(ProtocolInfo.ITEM_STACK_RESPONSE_PACKET, ItemStackResponsePacket::class.java)
        registerPacket(ProtocolInfo.PACKET_VIOLATION_WARNING_PACKET, PacketViolationWarningPacket::class.java)
        registerPacket(ProtocolInfo.PLAYER_ARMOR_DAMAGE_PACKET, PlayerArmorDamagePacket::class.java)
        registerPacket(ProtocolInfo.PLAYER_ENCHANT_OPTIONS_PACKET, PlayerEnchantOptionsPacket::class.java)
        registerPacket(ProtocolInfo.POS_TRACKING_CLIENT_REQUEST_PACKET, PositionTrackingDBClientRequestPacket::class.java)
        registerPacket(ProtocolInfo.POS_TRACKING_SERVER_BROADCAST_PACKET, PositionTrackingDBServerBroadcastPacket::class.java)
        registerPacket(ProtocolInfo.UPDATE_PLAYER_GAME_TYPE_PACKET, UpdatePlayerGameTypePacket::class.java)
        registerPacket(ProtocolInfo.FILTER_TEXT_PACKET, FilterTextPacket::class.java)
        registerPacket(ProtocolInfo.ITEM_COMPONENT_PACKET, ItemComponentPacket::class.java)
        registerPacket(ProtocolInfo.ADD_VOLUME_ENTITY, AddVolumeEntityPacket::class.java)
        registerPacket(ProtocolInfo.REMOVE_VOLUME_ENTITY, RemoveVolumeEntityPacket::class.java)
        registerPacket(ProtocolInfo.SYNC_ENTITY_PROPERTY, SyncEntityPropertyPacket::class.java)
    }

    companion object {
        private val INFLATER_RAW: ThreadLocal<Inflater> = ThreadLocal.withInitial { Inflater(true) }
        private val DEFLATER_RAW: ThreadLocal<Deflater> = ThreadLocal.withInitial { Deflater(7, true) }
        private val BUFFER: ThreadLocal<ByteArray> = ThreadLocal.withInitial { ByteArray(2 * 1024 * 1024) }
        const val CHANNEL_NONE: Byte = 0
        const val CHANNEL_PRIORITY: Byte = 1 //Priority channel, only to be used when it matters
        const val CHANNEL_WORLD_CHUNKS: Byte = 2 //Chunk sending
        const val CHANNEL_MOVEMENT: Byte = 3 //Movement sending
        const val CHANNEL_BLOCKS: Byte = 4 //Block updates or explosions
        const val CHANNEL_WORLD_EVENTS: Byte = 5 //Entity, level or blockentity entity events
        const val CHANNEL_ENTITY_SPAWNING: Byte = 6 //Entity spawn/despawn channel
        const val CHANNEL_TEXT: Byte = 7 //Chat and other text stuff
        const val CHANNEL_END: Byte = 31
        @Since("1.3.0.0-PN")
        @Throws(IOException::class, DataFormatException::class)
        fun inflateRaw(data: ByteArray?): ByteArray {
            val inflater: Inflater = INFLATER_RAW.get()
            return try {
                inflater.setInput(data)
                inflater.finished()
                val bos: FastByteArrayOutputStream = ThreadCache.fbaos.get()
                bos.reset()
                val buf: ByteArray = BUFFER.get()
                while (!inflater.finished()) {
                    val i: Int = inflater.inflate(buf)
                    if (i == 0) {
                        throw IOException("Could not decompress the data. Needs input: " + inflater.needsInput().toString() + ", Needs Dictionary: " + inflater.needsDictionary())
                    }
                    bos.write(buf, 0, i)
                }
                bos.toByteArray()
            } finally {
                inflater.reset()
            }
        }

        @Since("1.3.0.0-PN")
        @Throws(IOException::class)
        fun deflateRaw(data: ByteArray?, level: Int): ByteArray {
            val deflater: Deflater = DEFLATER_RAW.get()
            return try {
                deflater.setLevel(level)
                deflater.setInput(data)
                deflater.finish()
                val bos: FastByteArrayOutputStream = ThreadCache.fbaos.get()
                bos.reset()
                val buffer: ByteArray = BUFFER.get()
                while (!deflater.finished()) {
                    val i: Int = deflater.deflate(buffer)
                    bos.write(buffer, 0, i)
                }
                bos.toByteArray()
            } finally {
                deflater.reset()
            }
        }

        @Since("1.3.0.0-PN")
        @Throws(IOException::class)
        fun deflateRaw(datas: Array<ByteArray?>?, level: Int): ByteArray {
            val deflater: Deflater = DEFLATER_RAW.get()
            return try {
                deflater.setLevel(level)
                val bos: FastByteArrayOutputStream = ThreadCache.fbaos.get()
                bos.reset()
                val buffer: ByteArray = BUFFER.get()
                for (data in datas!!) {
                    deflater.setInput(data)
                    while (!deflater.needsInput()) {
                        val i: Int = deflater.deflate(buffer)
                        bos.write(buffer, 0, i)
                    }
                }
                deflater.finish()
                while (!deflater.finished()) {
                    val i: Int = deflater.deflate(buffer)
                    bos.write(buffer, 0, i)
                }
                bos.toByteArray()
            } finally {
                deflater.reset()
            }
        }
    }

    init {
        registerPackets()
        this.server = server
    }
}