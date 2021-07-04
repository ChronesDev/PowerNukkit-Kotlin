package cn.nukkit.network.protocol

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author MagicDroidX (Nukkit Project)
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Made the arg type constants dynamic because they can change in Minecraft updates")
@ToString
class AvailableCommandsPacket : DataPacket() {
    var commands: Map<String, CommandDataVersions>? = null
    val softEnums: Map<String, List<String>> = HashMap()

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        this.reset()
        val enumValuesSet: LinkedHashSet<String> = LinkedHashSet()
        val postFixesSet: LinkedHashSet<String> = LinkedHashSet()
        val enumsSet: LinkedHashSet<CommandEnum> = LinkedHashSet()
        commands.forEach { name, data ->
            val cmdData: CommandData = data.versions.get(0)
            if (cmdData.aliases != null) {
                enumsSet.add(cmdData.aliases)
                enumValuesSet.addAll(cmdData.aliases.getValues())
            }
            for (overload in cmdData.overloads.values()) {
                for (parameter in overload.input.parameters) {
                    if (parameter.enumData != null) {
                        enumsSet.add(parameter.enumData)
                        enumValuesSet.addAll(parameter.enumData.getValues())
                    }
                    if (parameter.postFix != null) {
                        postFixesSet.add(parameter.postFix)
                    }
                }
            }
        }
        val enumValues: List<String> = ArrayList(enumValuesSet)
        val enums: List<CommandEnum> = ArrayList(enumsSet)
        val postFixes: List<String> = ArrayList(postFixesSet)
        this.putUnsignedVarInt(enumValues.size())
        enumValues.forEach(this::putString)
        this.putUnsignedVarInt(postFixes.size())
        postFixes.forEach(this::putString)
        val indexWriter: ObjIntConsumer<BinaryStream>
        indexWriter = if (enumValues.size() < 256) {
            WRITE_BYTE
        } else if (enumValues.size() < 65536) {
            WRITE_SHORT
        } else {
            WRITE_INT
        }
        this.putUnsignedVarInt(enums.size())
        enums.forEach { cmdEnum ->
            putString(cmdEnum.getName())
            val values: List<String> = cmdEnum.getValues()
            putUnsignedVarInt(values.size())
            for (`val` in values) {
                val i = enumValues.indexOf(`val`)
                if (i < 0) {
                    throw IllegalStateException("Enum value '$`val`' not found")
                }
                indexWriter.accept(this, i)
            }
        }
        putUnsignedVarInt(commands!!.size())
        commands.forEach { name, cmdData ->
            val data: CommandData = cmdData.versions.get(0)
            putString(name)
            putString(data.description)
            putByte(data.flags as Byte)
            putByte(data.permission as Byte)
            putLInt(if (data.aliases == null) -1 else enums.indexOf(data.aliases))
            putUnsignedVarInt(data.overloads.size())
            for (overload in data.overloads.values()) {
                putUnsignedVarInt(overload.input.parameters.length)
                for (parameter in overload.input.parameters) {
                    putString(parameter.name)
                    var type = 0
                    if (parameter.postFix != null) {
                        val i = postFixes.indexOf(parameter.postFix)
                        if (i < 0) {
                            throw IllegalStateException("Postfix '" + parameter.postFix.toString() + "' isn't in postfix array")
                        }
                        type = ARG_FLAG_POSTFIX or i
                    } else {
                        type = type or ARG_FLAG_VALID
                        type = if (parameter.enumData != null) {
                            type or (ARG_FLAG_ENUM or enums.indexOf(parameter.enumData))
                        } else {
                            type or parameter.type.getId()
                        }
                    }
                    putLInt(type)
                    putBoolean(parameter.optional)
                    putByte(parameter.options) // TODO: 19/03/2019 Bit flags. Only first bit is used for GameRules.
                }
            }
        }
        this.putUnsignedVarInt(softEnums.size())
        softEnums.forEach { name, values ->
            this.putString(name)
            this.putUnsignedVarInt(values.size())
            values.forEach(this::putString)
        }
        this.putUnsignedVarInt(0)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.AVAILABLE_COMMANDS_PACKET
        private val WRITE_BYTE: ObjIntConsumer<BinaryStream> = ObjIntConsumer<BinaryStream> { s, v -> s.putByte(v as Byte) }
        private val WRITE_SHORT: ObjIntConsumer<BinaryStream> = BinaryStream::putLShort
        private val WRITE_INT: ObjIntConsumer<BinaryStream> = BinaryStream::putLInt
        const val ARG_FLAG_VALID = 0x100000
        const val ARG_FLAG_ENUM = 0x200000
        const val ARG_FLAG_POSTFIX = 0x1000000
        const val ARG_FLAG_SOFT_ENUM = 0x4000000
        val ARG_TYPE_INT: Int = dynamic(1)
        val ARG_TYPE_FLOAT: Int = dynamic(3)
        val ARG_TYPE_VALUE: Int = dynamic(4)
        val ARG_TYPE_WILDCARD_INT: Int = dynamic(5)
        val ARG_TYPE_OPERATOR: Int = dynamic(6)
        val ARG_TYPE_TARGET: Int = dynamic(7)
        val ARG_TYPE_WILDCARD_TARGET: Int = dynamic(8)
        val ARG_TYPE_FILE_PATH: Int = dynamic(16)
        val ARG_TYPE_STRING: Int = dynamic(32)
        val ARG_TYPE_BLOCK_POSITION: Int = dynamic(40)
        val ARG_TYPE_POSITION: Int = dynamic(41)
        val ARG_TYPE_MESSAGE: Int = dynamic(44)
        val ARG_TYPE_RAWTEXT: Int = dynamic(46)
        val ARG_TYPE_JSON: Int = dynamic(50)
        val ARG_TYPE_COMMAND: Int = dynamic(63)
    }
}