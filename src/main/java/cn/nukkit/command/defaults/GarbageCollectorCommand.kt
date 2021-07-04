package cn.nukkit.command.defaults

import cn.nukkit.command.CommandSender

/**
 * @author xtypr
 * @since 2015/11/11
 */
class GarbageCollectorCommand(name: String?) : VanillaCommand(name, "%nukkit.command.gc.description", "%nukkit.command.gc.usage") {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String?>?): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        var chunksCollected = 0
        var entitiesCollected = 0
        var tilesCollected = 0
        val memory: Long = Runtime.getRuntime().freeMemory()
        for (level in sender.getServer().getLevels().values()) {
            val chunksCount: Int = level.getChunks().size()
            val entitiesCount: Int = level.getEntities().length
            val tilesCount: Int = level.getBlockEntities().size()
            level.doChunkGarbageCollection()
            level.unloadChunks(true)
            chunksCollected += chunksCount - level.getChunks().size()
            entitiesCollected += entitiesCount - level.getEntities().length
            tilesCollected += tilesCount - level.getBlockEntities().size()
        }
        ThreadCache.clean()
        System.gc()
        val freedMemory: Long = Runtime.getRuntime().freeMemory() - memory
        sender.sendMessage(TextFormat.GREEN.toString() + "---- " + TextFormat.WHITE + "Garbage collection result" + TextFormat.GREEN + " ----")
        sender.sendMessage(TextFormat.GOLD.toString() + "Chunks: " + TextFormat.RED + chunksCollected)
        sender.sendMessage(TextFormat.GOLD.toString() + "Entities: " + TextFormat.RED + entitiesCollected)
        sender.sendMessage(TextFormat.GOLD.toString() + "Block Entities: " + TextFormat.RED + tilesCollected)
        sender.sendMessage(TextFormat.GOLD.toString() + "Memory freed: " + TextFormat.RED + NukkitMath.round(freedMemory / 1024.0 / 1024.0, 2) + " MB")
        return true
    }

    init {
        this.setPermission("nukkit.command.gc")
        this.commandParameters.clear()
    }
}