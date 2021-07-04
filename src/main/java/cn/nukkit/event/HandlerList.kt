package cn.nukkit.event

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author Nukkit Team.
 */
class HandlerList {
    @Volatile
    private var handlers: Array<RegisteredListener>? = null
    private val handlerslots: EnumMap<EventPriority, ArrayList<RegisteredListener>>
    @Synchronized
    fun register(listener: RegisteredListener) {
        if (handlerslots.get(listener.getPriority()).contains(listener)) throw IllegalStateException("This listener is already registered to priority " + listener.getPriority().toString())
        handlers = null
        handlerslots.get(listener.getPriority()).add(listener)
    }

    fun registerAll(listeners: Collection<RegisteredListener>) {
        for (listener in listeners) {
            register(listener)
        }
    }

    @Synchronized
    fun unregister(listener: RegisteredListener) {
        if (handlerslots.get(listener.getPriority()).remove(listener)) {
            handlers = null
        }
    }

    @Synchronized
    fun unregister(plugin: Plugin?) {
        var changed = false
        for (list in handlerslots.values()) {
            val i: ListIterator<RegisteredListener> = list.listIterator()
            while (i.hasNext()) {
                if (i.next().getPlugin().equals(plugin)) {
                    i.remove()
                    changed = true
                }
            }
        }
        if (changed) handlers = null
    }

    @Synchronized
    fun unregister(listener: Listener?) {
        var changed = false
        for (list in handlerslots.values()) {
            val i: ListIterator<RegisteredListener> = list.listIterator()
            while (i.hasNext()) {
                if (i.next().getListener().equals(listener)) {
                    i.remove()
                    changed = true
                }
            }
        }
        if (changed) handlers = null
    }

    @Synchronized
    fun bake() {
        if (handlers != null) return  // don't re-bake when still valid
        val entries: List<RegisteredListener> = ArrayList()
        for (entry in handlerslots.entrySet()) {
            entries.addAll(entry.getValue())
        }
        handlers = entries.toArray(RegisteredListener.EMPTY_ARRAY)
    }

    // This prevents fringe cases of returning null
    val registeredListeners: Array<Any>
        get() {
            var handlers: Array<RegisteredListener>
            while (this.handlers.also { handlers = it } == null) {
                bake()
            } // This prevents fringe cases of returning null
            return handlers
        }
    val isEmpty: Boolean
        @PowerNukkitOnly @Since("1.4.0.0-PN") get() {
            val handlers: Array<RegisteredListener>? = handlers
            return if (handlers != null) {
                handlers.size == 0
            } else registeredListeners.size == 0
        }

    companion object {
        private val allLists: ArrayList<HandlerList> = ArrayList()
        fun bakeAll() {
            synchronized(allLists) {
                for (h in allLists) {
                    h.bake()
                }
            }
        }

        fun unregisterAll() {
            synchronized(allLists) {
                for (h in allLists) {
                    synchronized(h) {
                        for (list in h.handlerslots.values()) {
                            list.clear()
                        }
                        h.handlers = null
                    }
                }
            }
        }

        fun unregisterAll(plugin: Plugin?) {
            synchronized(allLists) {
                for (h in allLists) {
                    h.unregister(plugin)
                }
            }
        }

        fun unregisterAll(listener: Listener?) {
            synchronized(allLists) {
                for (h in allLists) {
                    h.unregister(listener)
                }
            }
        }

        fun getRegisteredListeners(plugin: Plugin?): ArrayList<RegisteredListener> {
            val listeners: ArrayList<RegisteredListener> = ArrayList()
            synchronized(allLists) {
                for (h in allLists) {
                    synchronized(h) {
                        for (list in h.handlerslots.values()) {
                            for (listener in list) {
                                if (listener.getPlugin().equals(plugin)) {
                                    listeners.add(listener)
                                }
                            }
                        }
                    }
                }
            }
            return listeners
        }

        val handlerLists: ArrayList<HandlerList>
            get() {
                synchronized(allLists) { return ArrayList(allLists) }
            }
    }

    init {
        handlerslots = EnumMap(EventPriority::class.java)
        for (o in EventPriority.values()) {
            handlerslots.put(o, ArrayList())
        }
        synchronized(allLists) { allLists.add(this) }
    }
}