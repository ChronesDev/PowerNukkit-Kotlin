package cn.nukkit.network.protocol

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author MagicDroidX &amp; iNevet (Nukkit Project)
 */
interface ProtocolInfo {
    companion object {
        /**
         * Actual Minecraft: PE protocol version
         */
        val CURRENT_PROTOCOL: Int = dynamic(440)
        val SUPPORTED_PROTOCOLS: List<Integer> = Ints.asList(CURRENT_PROTOCOL)
        val MINECRAFT_VERSION: String = dynamic("v1.17.0")
        val MINECRAFT_VERSION_NETWORK: String = dynamic("1.17.0")
        const val LOGIN_PACKET: Byte = 0x01
        const val PLAY_STATUS_PACKET: Byte = 0x02
        const val SERVER_TO_CLIENT_HANDSHAKE_PACKET: Byte = 0x03
        const val CLIENT_TO_SERVER_HANDSHAKE_PACKET: Byte = 0x04
        const val DISCONNECT_PACKET: Byte = 0x05
        const val RESOURCE_PACKS_INFO_PACKET: Byte = 0x06
        const val RESOURCE_PACK_STACK_PACKET: Byte = 0x07
        const val RESOURCE_PACK_CLIENT_RESPONSE_PACKET: Byte = 0x08
        const val TEXT_PACKET: Byte = 0x09
        const val SET_TIME_PACKET: Byte = 0x0a
        const val START_GAME_PACKET: Byte = 0x0b
        const val ADD_PLAYER_PACKET: Byte = 0x0c
        const val ADD_ENTITY_PACKET: Byte = 0x0d
        const val REMOVE_ENTITY_PACKET: Byte = 0x0e
        const val ADD_ITEM_ENTITY_PACKET: Byte = 0x0f
        const val TAKE_ITEM_ENTITY_PACKET: Byte = 0x11
        const val MOVE_ENTITY_ABSOLUTE_PACKET: Byte = 0x12
        const val MOVE_PLAYER_PACKET: Byte = 0x13
        const val RIDER_JUMP_PACKET: Byte = 0x14
        const val UPDATE_BLOCK_PACKET: Byte = 0x15
        const val ADD_PAINTING_PACKET: Byte = 0x16
        const val TICK_SYNC_PACKET: Byte = 0x17
        const val LEVEL_SOUND_EVENT_PACKET_V1: Byte = 0x18
        const val LEVEL_EVENT_PACKET: Byte = 0x19
        const val BLOCK_EVENT_PACKET: Byte = 0x1a
        const val ENTITY_EVENT_PACKET: Byte = 0x1b
        const val MOB_EFFECT_PACKET: Byte = 0x1c
        const val UPDATE_ATTRIBUTES_PACKET: Byte = 0x1d
        const val INVENTORY_TRANSACTION_PACKET: Byte = 0x1e
        const val MOB_EQUIPMENT_PACKET: Byte = 0x1f
        const val MOB_ARMOR_EQUIPMENT_PACKET: Byte = 0x20
        const val INTERACT_PACKET: Byte = 0x21
        const val BLOCK_PICK_REQUEST_PACKET: Byte = 0x22
        const val ENTITY_PICK_REQUEST_PACKET: Byte = 0x23
        const val PLAYER_ACTION_PACKET: Byte = 0x24
        const val ENTITY_FALL_PACKET: Byte = 0x25
        const val HURT_ARMOR_PACKET: Byte = 0x26
        const val SET_ENTITY_DATA_PACKET: Byte = 0x27
        const val SET_ENTITY_MOTION_PACKET: Byte = 0x28
        const val SET_ENTITY_LINK_PACKET: Byte = 0x29
        const val SET_HEALTH_PACKET: Byte = 0x2a
        const val SET_SPAWN_POSITION_PACKET: Byte = 0x2b
        const val ANIMATE_PACKET: Byte = 0x2c
        const val RESPAWN_PACKET: Byte = 0x2d
        const val CONTAINER_OPEN_PACKET: Byte = 0x2e
        const val CONTAINER_CLOSE_PACKET: Byte = 0x2f
        const val PLAYER_HOTBAR_PACKET: Byte = 0x30
        const val INVENTORY_CONTENT_PACKET: Byte = 0x31
        const val INVENTORY_SLOT_PACKET: Byte = 0x32
        const val CONTAINER_SET_DATA_PACKET: Byte = 0x33
        const val CRAFTING_DATA_PACKET: Byte = 0x34
        const val CRAFTING_EVENT_PACKET: Byte = 0x35
        const val GUI_DATA_PICK_ITEM_PACKET: Byte = 0x36
        const val ADVENTURE_SETTINGS_PACKET: Byte = 0x37
        const val BLOCK_ENTITY_DATA_PACKET: Byte = 0x38
        const val PLAYER_INPUT_PACKET: Byte = 0x39
        const val FULL_CHUNK_DATA_PACKET: Byte = 0x3a
        const val SET_COMMANDS_ENABLED_PACKET: Byte = 0x3b
        const val SET_DIFFICULTY_PACKET: Byte = 0x3c
        const val CHANGE_DIMENSION_PACKET: Byte = 0x3d
        const val SET_PLAYER_GAME_TYPE_PACKET: Byte = 0x3e
        const val PLAYER_LIST_PACKET: Byte = 0x3f
        const val SIMPLE_EVENT_PACKET: Byte = 0x40
        const val EVENT_PACKET: Byte = 0x41
        const val SPAWN_EXPERIENCE_ORB_PACKET: Byte = 0x42
        const val CLIENTBOUND_MAP_ITEM_DATA_PACKET: Byte = 0x43
        const val MAP_INFO_REQUEST_PACKET: Byte = 0x44
        const val REQUEST_CHUNK_RADIUS_PACKET: Byte = 0x45
        const val CHUNK_RADIUS_UPDATED_PACKET: Byte = 0x46
        const val ITEM_FRAME_DROP_ITEM_PACKET: Byte = 0x47
        const val GAME_RULES_CHANGED_PACKET: Byte = 0x48
        const val CAMERA_PACKET: Byte = 0x49
        const val BOSS_EVENT_PACKET: Byte = 0x4a
        const val SHOW_CREDITS_PACKET: Byte = 0x4b
        const val AVAILABLE_COMMANDS_PACKET: Byte = 0x4c
        const val COMMAND_REQUEST_PACKET: Byte = 0x4d
        const val COMMAND_BLOCK_UPDATE_PACKET: Byte = 0x4e
        const val COMMAND_OUTPUT_PACKET: Byte = 0x4f
        const val UPDATE_TRADE_PACKET: Byte = 0x50
        const val UPDATE_EQUIPMENT_PACKET: Byte = 0x51
        const val RESOURCE_PACK_DATA_INFO_PACKET: Byte = 0x52
        const val RESOURCE_PACK_CHUNK_DATA_PACKET: Byte = 0x53
        const val RESOURCE_PACK_CHUNK_REQUEST_PACKET: Byte = 0x54
        const val TRANSFER_PACKET: Byte = 0x55
        const val PLAY_SOUND_PACKET: Byte = 0x56
        const val STOP_SOUND_PACKET: Byte = 0x57
        const val SET_TITLE_PACKET: Byte = 0x58
        const val ADD_BEHAVIOR_TREE_PACKET: Byte = 0x59
        const val STRUCTURE_BLOCK_UPDATE_PACKET: Byte = 0x5a
        const val SHOW_STORE_OFFER_PACKET: Byte = 0x5b
        const val PURCHASE_RECEIPT_PACKET: Byte = 0x5c
        const val PLAYER_SKIN_PACKET: Byte = 0x5d
        const val SUB_CLIENT_LOGIN_PACKET: Byte = 0x5e
        const val INITIATE_WEB_SOCKET_CONNECTION_PACKET: Byte = 0x5f
        const val SET_LAST_HURT_BY_PACKET: Byte = 0x60
        const val BOOK_EDIT_PACKET: Byte = 0x61
        const val NPC_REQUEST_PACKET: Byte = 0x62
        const val PHOTO_TRANSFER_PACKET: Byte = 0x63
        const val MODAL_FORM_REQUEST_PACKET: Byte = 0x64
        const val MODAL_FORM_RESPONSE_PACKET: Byte = 0x65
        const val SERVER_SETTINGS_REQUEST_PACKET: Byte = 0x66
        const val SERVER_SETTINGS_RESPONSE_PACKET: Byte = 0x67
        const val SHOW_PROFILE_PACKET: Byte = 0x68
        const val SET_DEFAULT_GAME_TYPE_PACKET: Byte = 0x69
        const val MOVE_ENTITY_DELTA_PACKET: Byte = 0x6f
        const val SET_SCOREBOARD_IDENTITY_PACKET: Byte = 0x70
        const val SET_LOCAL_PLAYER_AS_INITIALIZED_PACKET: Byte = 0x71
        const val UPDATE_SOFT_ENUM_PACKET: Byte = 0x72
        const val NETWORK_STACK_LATENCY_PACKET: Byte = 0x73
        const val SCRIPT_CUSTOM_EVENT_PACKET: Byte = 0x75
        const val SPAWN_PARTICLE_EFFECT_PACKET: Byte = 0x76
        const val AVAILABLE_ENTITY_IDENTIFIERS_PACKET: Byte = 0x77
        const val LEVEL_SOUND_EVENT_PACKET_V2: Byte = 0x78
        const val NETWORK_CHUNK_PUBLISHER_UPDATE_PACKET: Byte = 0x79
        const val BIOME_DEFINITION_LIST_PACKET: Byte = 0x7a
        const val LEVEL_SOUND_EVENT_PACKET: Byte = 0x7b
        const val LEVEL_EVENT_GENERIC_PACKET: Byte = 0x7c
        const val LECTERN_UPDATE_PACKET: Byte = 0x7d
        const val VIDEO_STREAM_CONNECT_PACKET: Byte = 0x7e

        //byte ADD_ENTITY_PACKET = 0x7f;
        //byte REMOVE_ENTITY_PACKET = 0x80;
        const val CLIENT_CACHE_STATUS_PACKET = 0x81.toByte()
        const val ON_SCREEN_TEXTURE_ANIMATION_PACKET = 0x82.toByte()
        const val MAP_CREATE_LOCKED_COPY_PACKET = 0x83.toByte()
        const val STRUCTURE_TEMPLATE_DATA_EXPORT_REQUEST = 0x84.toByte()
        const val STRUCTURE_TEMPLATE_DATA_EXPORT_RESPONSE = 0x85.toByte()
        const val UPDATE_BLOCK_PROPERTIES = 0x86.toByte()
        const val CLIENT_CACHE_BLOB_STATUS_PACKET = 0x87.toByte()
        const val CLIENT_CACHE_MISS_RESPONSE_PACKET = 0x88.toByte()
        const val EDUCATION_SETTINGS_PACKET = 0x89.toByte()
        const val EMOTE_PACKET = 0x8a.toByte()
        const val MULTIPLAYER_SETTINGS_PACKET = 0x8b.toByte()
        const val SETTINGS_COMMAND_PACKET = 0x8c.toByte()
        const val ANVIL_DAMAGE_PACKET = 0x8d.toByte()
        const val COMPLETED_USING_ITEM_PACKET = 0x8e.toByte()
        const val NETWORK_SETTINGS_PACKET = 0x8f.toByte()
        const val PLAYER_AUTH_INPUT_PACKET = 0x90.toByte()

        @Since("1.3.0.0-PN")
        val CREATIVE_CONTENT_PACKET = 0x91.toByte()

        @Since("1.3.0.0-PN")
        val PLAYER_ENCHANT_OPTIONS_PACKET = 0x92.toByte()

        @Since("1.3.0.0-PN")
        val ITEM_STACK_REQUEST_PACKET = 0x93.toByte()

        @Since("1.3.0.0-PN")
        val ITEM_STACK_RESPONSE_PACKET = 0x94.toByte()

        @Since("1.3.0.0-PN")
        val PLAYER_ARMOR_DAMAGE_PACKET = 0x95.toByte()

        @Since("1.3.0.0-PN")
        val CODE_BUILDER_PACKET = 0x96.toByte()

        @Since("1.3.0.0-PN")
        val UPDATE_PLAYER_GAME_TYPE_PACKET = 0x97.toByte()

        @Since("1.3.0.0-PN")
        val EMOTE_LIST_PACKET = 0x98.toByte()

        @Since("1.3.0.0-PN")
        val POS_TRACKING_SERVER_BROADCAST_PACKET = 0x99.toByte()

        @Since("1.3.0.0-PN")
        val POS_TRACKING_CLIENT_REQUEST_PACKET = 0x9a.toByte()

        @Since("1.3.0.0-PN")
        val DEBUG_INFO_PACKET = 0x9b.toByte()

        @Since("1.3.0.0-PN")
        val PACKET_VIOLATION_WARNING_PACKET = 0x9c.toByte()

        @Since("1.4.0.0-PN")
        val MOTION_PREDICTION_HINTS_PACKET = 0x9d.toByte()

        @Since("1.4.0.0-PN")
        val ANIMATE_ENTITY_PACKET = 0x9e.toByte()

        @Since("1.4.0.0-PN")
        val CAMERA_SHAKE_PACKET = 0x9f.toByte()

        @Since("1.4.0.0-PN")
        val PLAYER_FOG_PACKET = 0xa0.toByte()

        @Since("1.4.0.0-PN")
        val CORRECT_PLAYER_MOVE_PREDICTION_PACKET = 0xa1.toByte()

        @Since("1.4.0.0-PN")
        val ITEM_COMPONENT_PACKET = 0xa2.toByte()

        @Since("1.4.0.0-PN")
        val FILTER_TEXT_PACKET = 0xa3.toByte()

        @Since("1.4.0.0-PN")
        val CLIENTBOUND_DEBUG_RENDERER_PACKET = 0xa4.toByte()

        @Since("1.5.0.0-PN")
        @PowerNukkitOnly
        val SYNC_ENTITY_PROPERTY = 0xa5.toByte()

        @Since("1.5.0.0-PN")
        @PowerNukkitOnly
        val ADD_VOLUME_ENTITY = 0xa6.toByte()

        @Since("1.5.0.0-PN")
        @PowerNukkitOnly
        val REMOVE_VOLUME_ENTITY = 0xa7.toByte()
        const val BATCH_PACKET = 0xff.toByte()
    }
}