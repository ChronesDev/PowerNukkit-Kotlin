package cn.nukkit.positiontracking

import cn.nukkit.Player

/**
 * A position tracking db service. It holds file resources that needs to be closed when not needed anymore.
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ParametersAreNonnullByDefault
@Log4j2
class PositionTrackingService @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(folder: File) : Closeable {
    private val storage: TreeMap<Integer, WeakReference<PositionTrackingStorage>> = TreeMap(Comparator.reverseOrder())
    private val closed: AtomicBoolean = AtomicBoolean(false)
    private val folder: File
    private val tracking: Map<Player, Map<PositionTrackingStorage, IntSet>> = MapMaker().weakKeys().makeMap()
    @Throws(IOException::class)
    private fun hasTrackingDevice(player: Player, @Nullable inventory: Inventory?, trackingHandler: Int): Boolean {
        if (inventory == null) {
            return false
        }
        val size: Int = inventory.getSize()
        for (i in 0 until size) {
            if (isTrackingDevice(player, inventory.getItem(i), trackingHandler)) {
                return true
            }
        }
        return false
    }

    @Throws(IOException::class)
    private fun isTrackingDevice(player: Player, @Nullable item: Item?, trackingHandler: Int): Boolean {
        if (!(item != null && item.getId() === ItemID.LODESTONE_COMPASS && item is ItemCompassLodestone)) {
            return false
        }
        val compassLodestone: ItemCompassLodestone = item as ItemCompassLodestone
        if (compassLodestone.getTrackingHandle() !== trackingHandler) {
            return false
        }
        val position: PositionTracking? = getPosition(trackingHandler)
        return position != null && position.getLevelName()!!.equals(player.getLevelName())
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Throws(IOException::class)
    fun hasTrackingDevice(player: Player, trackingHandler: Int): Boolean {
        for (inventory in inventories(player)) {
            if (hasTrackingDevice(player, inventory, trackingHandler)) {
                return true
            }
        }
        return false
    }

    private fun sendTrackingUpdate(player: Player, trackingHandler: Int, pos: PositionTracking) {
        if (player.getLevelName().equals(pos.getLevelName())) {
            val packet = PositionTrackingDBServerBroadcastPacket()
            packet.setAction(PositionTrackingDBServerBroadcastPacket.Action.UPDATE)
            packet.setPosition(pos)
            packet.setDimension(player.getLevel().getDimension())
            packet.setTrackingId(trackingHandler)
            packet.setStatus(0)
            player.dataPacket(packet)
        } else {
            sendTrackingDestroy(player, trackingHandler)
        }
    }

    private fun sendTrackingDestroy(player: Player, trackingHandler: Int) {
        val packet: PositionTrackingDBServerBroadcastPacket = destroyPacket(trackingHandler)
        player.dataPacket(packet)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    @Synchronized
    @Throws(IOException::class)
    fun startTracking(player: Player, trackingHandler: Int, validate: Boolean): PositionTracking? {
        Preconditions.checkArgument(trackingHandler >= 0, "Tracking handler must be positive")
        if (isTracking(player, trackingHandler, validate)) {
            val position: PositionTracking? = getPosition(trackingHandler)
            if (position != null) {
                sendTrackingUpdate(player, trackingHandler, position)
                return position
            }
            stopTracking(player, trackingHandler)
            return null
        }
        if (validate && !hasTrackingDevice(player, trackingHandler)) {
            return null
        }
        val storage: PositionTrackingStorage = getStorageForHandler(trackingHandler) ?: return null
        val position: PositionTracking = storage.getPosition(trackingHandler) ?: return null
        tracking.computeIfAbsent(player) { p -> HashMap() }.computeIfAbsent(storage) { s -> IntOpenHashSet(3) }.add(trackingHandler)
        return position
    }

    private fun destroyPacket(trackingHandler: Int): PositionTrackingDBServerBroadcastPacket {
        val packet = PositionTrackingDBServerBroadcastPacket()
        packet.setAction(PositionTrackingDBServerBroadcastPacket.Action.DESTROY)
        packet.setTrackingId(trackingHandler)
        packet.setDimension(0)
        packet.setPosition(0, 0, 0)
        packet.setStatus(2)
        return packet
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Synchronized
    fun stopTracking(player: Player): Boolean {
        val toRemove: Map<PositionTrackingStorage, IntSet> = tracking.remove(player)
        if (toRemove != null && player.isOnline()) {
            val packets: Array<DataPacket> = toRemove.values().stream()
                    .flatMapToInt { handlers -> IntStream.of(handlers.toIntArray()) }
                    .mapToObj { trackingHandler: Int -> destroyPacket(trackingHandler) }
                    .toArray { _Dummy_.__Array__() }
            player.getServer().batchPackets(arrayOf<Player>(player), packets)
        }
        return toRemove != null
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Synchronized
    fun stopTracking(player: Player, trackingHandler: Int): Boolean {
        val tracking: Map<Any, Any> = tracking[player] ?: return false
        for (entry in tracking.entrySet()) {
            if (entry.getValue().remove(trackingHandler)) {
                if (entry.getValue().isEmpty()) {
                    tracking.remove(entry.getKey())
                }
                player.dataPacket(destroyPacket(trackingHandler))
                return true
            }
        }
        return false
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Synchronized
    @Throws(IOException::class)
    fun isTracking(player: Player, trackingHandler: Int, validate: Boolean): Boolean {
        val tracking: Map<Any, Any> = tracking[player] ?: return false
        for (value in tracking.values()) {
            if (value.contains(trackingHandler)) {
                if (validate && !hasTrackingDevice(player, trackingHandler)) {
                    stopTracking(player, trackingHandler)
                    return false
                }
                return true
            }
        }
        return false
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Synchronized
    fun forceRecheckAllPlayers() {
        tracking.keySet().removeIf { p -> !p.isOnline() }
        val toRemove: Map<Player, IntList> = HashMap(2)
        for (entry in tracking.entrySet()) {
            val player: Player = entry.getKey()
            for (entry2 in entry.getValue().entrySet()) {
                entry2.getValue().forEach(IntConsumer { trackingHandler ->
                    try {
                        if (!hasTrackingDevice(player, trackingHandler)) {
                            toRemove.computeIfAbsent(player) { p -> IntArrayList(2) }.add(trackingHandler)
                        }
                    } catch (e: IOException) {
                        log.error("Failed to update the tracking handler {} for player {}", trackingHandler, player.getName(), e)
                    }
                } as IntConsumer?)
            }
        }
        toRemove.forEach { player, list -> list.forEach(IntConsumer { handler -> stopTracking(player, handler) } as IntConsumer?) }
        Server.getInstance().getOnlinePlayers().values().forEach { player: Player -> detectNeededUpdates(player) }
    }

    private fun inventories(player: Player): Iterable<Inventory> {
        return label@ Iterable<Inventory> {
            object : Iterator<Inventory?>() {
                var next = 0

                @Override
                override fun hasNext(): Boolean {
                    return@label next <= 4
                }

                @Override
                override fun next(): Inventory {
                    when (next++) {
                        0 -> return@label player.getInventory()
                        1 -> return@label player.getCursorInventory()
                        2 -> return@label player.getOffhandInventory()
                        3 -> return@label player.getCraftingGrid()
                        4 -> return@label player.getTopWindow().orElse(null)
                        else -> throw NoSuchElementException()
                    }
                }
            }
        }
    }

    private fun detectNeededUpdates(player: Player) {
        for (inventory in inventories(player)) {
            if (inventory == null) {
                continue
            }
            val size: Int = inventory.getSize()
            for (slot in 0 until size) {
                val item: Item = inventory.getItem(slot)
                if (item.getId() === ItemID.LODESTONE_COMPASS && item is ItemCompassLodestone) {
                    val compass: ItemCompassLodestone = item as ItemCompassLodestone
                    val trackingHandle: Int = compass.getTrackingHandle()
                    if (trackingHandle != 0) {
                        var pos: PositionTracking?
                        try {
                            pos = getPosition(trackingHandle)
                            if (pos != null && pos.getLevelName()!!.equals(player.getLevelName())) {
                                startTracking(player, trackingHandle, false)
                            }
                        } catch (e: IOException) {
                            log.error("Failed to get the position of the tracking handler {}", trackingHandle, e)
                        }
                    }
                }
            }
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun forceRecheck(player: Player) {
        val tracking: Map<PositionTrackingStorage, IntSet>? = tracking[player]
        if (tracking != null) {
            val toRemove: IntList = IntArrayList(2)
            for (entry2 in tracking.entrySet()) {
                entry2.getValue().forEach(IntConsumer { trackingHandler ->
                    try {
                        if (!hasTrackingDevice(player, trackingHandler)) {
                            toRemove.add(trackingHandler)
                        }
                    } catch (e: IOException) {
                        log.error("Failed to update the tracking handler {} for player {}", trackingHandler, player.getName(), e)
                    }
                } as IntConsumer?)
            }
            toRemove.forEach(IntConsumer { handler -> stopTracking(player, handler) } as IntConsumer?)
        }
        detectNeededUpdates(player)
    }

    @Nullable
    @Synchronized
    private fun findStorageForHandler(@Nonnull handler: Integer): Integer? {
        var best: Integer? = null
        for (startIndex in storage.keySet()) {
            val comp: Int = startIndex.compareTo(handler)
            if (comp == 0) {
                return startIndex
            }
            if (comp < 0 && (best == null || best.compareTo(startIndex) < 0)) {
                best = startIndex
            }
        }
        return best
    }

    @Nonnull
    @Synchronized
    @Throws(IOException::class)
    private fun loadStorage(@Nonnull startIndex: Integer): PositionTrackingStorage {
        val trackingStorage: PositionTrackingStorage = storage.get(startIndex).get()
        if (trackingStorage != null) {
            return trackingStorage
        }
        val positionTrackingStorage = PositionTrackingStorage(startIndex, File(folder, startIndex.toString() + ".pnt"))
        storage.put(startIndex, WeakReference(positionTrackingStorage))
        return positionTrackingStorage
    }

    @Nullable
    @Synchronized
    @Throws(IOException::class)
    private fun getStorageForHandler(@Nonnull trackingHandler: Integer): PositionTrackingStorage? {
        val startIndex: Integer = findStorageForHandler(trackingHandler) ?: return null
        val storage: PositionTrackingStorage = loadStorage(startIndex)
        return if (trackingHandler > storage.getMaxHandler()) {
            null
        } else storage
    }

    /**
     * Attempts to reuse an existing and enabled trackingHandler for the given position, if none is found than a new handler is created
     * if the limit was not exceeded.
     * @param position The position that needs a handler
     * @return The trackingHandler assigned to the position or an empty OptionalInt if none was found and this storage is full
     * @throws IOException If an error occurred while reading or writing the file
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Synchronized
    @Throws(IOException::class)
    fun addOrReusePosition(position: NamedPosition): Int {
        checkClosed()
        val trackingHandler: OptionalInt = findTrackingHandler(position)
        return if (trackingHandler.isPresent()) {
            trackingHandler.getAsInt()
        } else addNewPosition(position)
    }

    /**
     * Adds the given position as a new entry in this storage, even if the position is already registered and enabled.
     * @param position The position that needs a handler
     * @return The trackingHandler assigned to the position or an empty OptionalInt if none was found and this storage is full
     * @throws IOException If an error occurred while reading or writing the file
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Synchronized
    @Throws(IOException::class)
    fun addNewPosition(position: NamedPosition): Int {
        return addNewPosition(position, true)
    }

    /**
     * Adds the given position as a new entry in this storage, even if the position is already registered and enabled.
     * @param position The position that needs a handler
     * @param enabled If the position will be added as enabled or disabled
     * @return The trackingHandler assigned to the position or an empty OptionalInt if none was found and this storage is full
     * @throws IOException If an error occurred while reading or writing the file
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Synchronized
    @Throws(IOException::class)
    fun addNewPosition(position: NamedPosition, enabled: Boolean): Int {
        checkClosed()
        var next = 1
        if (!storage.isEmpty()) {
            val trackingStorage: PositionTrackingStorage = loadStorage(storage.firstKey())
            val handler: OptionalInt = trackingStorage.addNewPosition(position, enabled)
            if (handler.isPresent()) {
                return handler.getAsInt()
            }
            next = trackingStorage.getMaxHandler()
        }
        val trackingStorage = PositionTrackingStorage(next, File(folder, "$next.pnt"))
        storage.put(next, WeakReference(trackingStorage))
        return trackingStorage.addNewPosition(position, enabled).orElseThrow { InternalError() }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Throws(IOException::class)
    fun findTrackingHandler(position: NamedPosition): OptionalInt {
        val handlers: IntList = findTrackingHandlers(position, true, 1)
        return if (!handlers.isEmpty()) {
            OptionalInt.of(handlers.getInt(0))
        } else OptionalInt.empty()
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Synchronized
    @Throws(IOException::class)
    fun invalidateHandler(trackingHandler: Int): Boolean {
        checkClosed()
        val storage: PositionTrackingStorage = getStorageForHandler(trackingHandler) ?: return false
        if (!storage.hasPosition(trackingHandler, false)) {
            return false
        }
        storage.invalidateHandler(trackingHandler)
        handlerDisabled(trackingHandler)
        return true
    }

    private fun handlerDisabled(trackingHandler: Int) {
        val players: List<Player> = ArrayList()
        for (playerMapEntry in tracking.entrySet()) {
            for (value in playerMapEntry.getValue().values()) {
                if (value.contains(trackingHandler)) {
                    players.add(playerMapEntry.getKey())
                    break
                }
            }
        }
        if (!players.isEmpty()) {
            Server.getInstance().batchPackets(players.toArray(Player.EMPTY_ARRAY), arrayOf<DataPacket>(destroyPacket(trackingHandler)))
        }
    }

    @Throws(IOException::class)
    private fun handlerEnabled(trackingHandler: Int) {
        val server: Server = Server.getInstance()
        for (player in server.getOnlinePlayers().values()) {
            if (hasTrackingDevice(player, trackingHandler) && !isTracking(player, trackingHandler, false)) {
                startTracking(player, trackingHandler, false)
            }
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    @Throws(IOException::class)
    fun getPosition(trackingHandle: Int): PositionTracking? {
        return getPosition(trackingHandle, true)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    @Throws(IOException::class)
    fun getPosition(trackingHandle: Int, onlyEnabled: Boolean): PositionTracking? {
        checkClosed()
        val trackingStorage: PositionTrackingStorage = getStorageForHandler(trackingHandle) ?: return null
        return trackingStorage.getPosition(trackingHandle, onlyEnabled)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Synchronized
    @Throws(IOException::class)
    fun isEnabled(trackingHandler: Int): Boolean {
        checkClosed()
        val trackingStorage: PositionTrackingStorage? = getStorageForHandler(trackingHandler)
        return trackingStorage != null && trackingStorage.isEnabled(trackingHandler)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Synchronized
    @Throws(IOException::class)
    fun setEnabled(trackingHandler: Int, enabled: Boolean): Boolean {
        checkClosed()
        val trackingStorage: PositionTrackingStorage = getStorageForHandler(trackingHandler) ?: return false
        if (trackingStorage.setEnabled(trackingHandler, enabled)) {
            if (enabled) {
                handlerEnabled(trackingHandler)
            } else {
                handlerDisabled(trackingHandler)
            }
            return true
        }
        return false
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Synchronized
    @Throws(IOException::class)
    fun hasPosition(trackingHandler: Int): Boolean {
        return hasPosition(trackingHandler, true)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Synchronized
    @Throws(IOException::class)
    fun hasPosition(trackingHandler: Int, onlyEnabled: Boolean): Boolean {
        checkClosed()
        val startIndex: Integer = findStorageForHandler(trackingHandler) ?: return false
        return if (!storage.containsKey(startIndex)) {
            false
        } else loadStorage(startIndex).hasPosition(trackingHandler, onlyEnabled)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Synchronized
    @Throws(IOException::class)
    fun findTrackingHandlers(pos: NamedPosition): IntList {
        return findTrackingHandlers(pos, true)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Synchronized
    @Throws(IOException::class)
    fun findTrackingHandlers(pos: NamedPosition, onlyEnabled: Boolean): IntList {
        return findTrackingHandlers(pos, onlyEnabled, Integer.MAX_VALUE)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Synchronized
    @Throws(IOException::class)
    fun findTrackingHandlers(pos: NamedPosition, onlyEnabled: Boolean, limit: Int): IntList {
        checkClosed()
        val list: IntList = IntArrayList()
        for (startIndex in storage.descendingKeySet()) {
            list.addAll(loadStorage(startIndex).findTrackingHandlers(pos, onlyEnabled, limit - list.size()))
            if (list.size() >= limit) {
                break
            }
        }
        return list
    }

    /**
     * Close all active
     * @throws IOException If any resource failed to close properly.
     * The detailed exceptions will be in getCause() and and getSuppressed()
     */
    @Override
    @Synchronized
    @Throws(IOException::class)
    fun close() {
        closed.set(true)
        var exception: IOException? = null
        for (ref in storage.values()) {
            val positionTrackingStorage: PositionTrackingStorage = ref.get()
            if (positionTrackingStorage != null) {
                try {
                    positionTrackingStorage.close()
                } catch (e: Throwable) {
                    if (exception == null) {
                        exception = IOException(e)
                    } else {
                        exception.addSuppressed(e)
                    }
                }
            }
        }
        if (exception != null) {
            throw exception
        }
    }

    @Override
    @Throws(Throwable::class)
    protected fun finalize() {
        close()
    }

    @Throws(IOException::class)
    private fun checkClosed() {
        if (closed.get()) {
            throw IOException("The service is closed")
        }
    }

    companion object {
        private val FILENAME_PATTERN: Pattern = Pattern.compile("^\\d+\\.pnt$", Pattern.CASE_INSENSITIVE)
        private val FILENAME_FILTER: FilenameFilter = FilenameFilter { dir, name -> FILENAME_PATTERN.matcher(name).matches() && File(dir, name).isFile() }
    }

    /**
     * Creates position tracking db service. The service is ready to be used right after the creation.
     * @param folder The folder that will hold the position tracking db files
     * @throws FileNotFoundException If the folder does not exists and can't be created
     */
    init {
        if (!folder.isDirectory() && !folder.mkdirs()) {
            throw FileNotFoundException("Failed to create the folder $folder")
        }
        this.folder = folder
        val emptyRef: WeakReference<PositionTrackingStorage> = WeakReference(null)
        Arrays.stream(Optional.ofNullable(folder.list(FILENAME_FILTER)).orElseThrow { FileNotFoundException("Invalid folder: $folder") })
                .map { name -> Integer.parseInt(name.substring(0, name.length() - 4)) }
                .forEachOrdered { startIndex -> storage.put(startIndex, emptyRef) }
    }
}