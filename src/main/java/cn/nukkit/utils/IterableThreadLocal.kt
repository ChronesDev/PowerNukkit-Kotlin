package cn.nukkit.utils

import java.lang.ref.Reference

abstract class IterableThreadLocal<T> : ThreadLocal<T>(), Iterable<T> {
    private val flag: ThreadLocal<T>? = null
    private val allValues: ConcurrentLinkedDeque<T> = ConcurrentLinkedDeque()
    @Override
    protected fun initialValue(): T? {
        val value = init()
        if (value != null) {
            allValues.add(value)
        }
        return value
    }

    @Override
    override fun iterator(): Iterator<T> {
        return all.iterator()
    }

    fun init(): T? {
        return null
    }

    fun clean() {
        clean(this)
    }

    val all: Collection<T>
        get() = Collections.unmodifiableCollection(allValues)

    @Override
    @Throws(Throwable::class)
    protected fun finalize() {
        clean(this)
        super.finalize()
    }

    companion object {
        fun clean(instance: ThreadLocal?) {
            try {
                var rootGroup: ThreadGroup = Thread.currentThread().getThreadGroup()
                var parentGroup: ThreadGroup?
                while (rootGroup.getParent().also { parentGroup = it } != null) {
                    rootGroup = parentGroup
                }
                var threads: Array<Thread?> = arrayOfNulls<Thread>(rootGroup.activeCount())
                if (threads.size != 0) {
                    while (rootGroup.enumerate(threads, true) === threads.size) {
                        threads = arrayOfNulls<Thread>(threads.size * 2)
                    }
                }
                val tl: Field = Thread::class.java.getDeclaredField("threadLocals")
                tl.setAccessible(true)
                var methodRemove: Method? = null
                for (thread in threads) {
                    if (thread != null) {
                        val tlm: Object = tl.get(thread)
                        if (tlm != null) {
                            if (methodRemove == null) {
                                methodRemove = tlm.getClass().getDeclaredMethod("remove", ThreadLocal::class.java)
                                methodRemove.setAccessible(true)
                            }
                            if (methodRemove != null) {
                                try {
                                    methodRemove.invoke(tlm, instance)
                                } catch (ignore: Throwable) {
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun cleanAll() {
            try {
                // Get a reference to the thread locals table of the current thread
                val thread: Thread = Thread.currentThread()
                val threadLocalsField: Field = Thread::class.java.getDeclaredField("threadLocals")
                threadLocalsField.setAccessible(true)
                val threadLocalTable: Object = threadLocalsField.get(thread)

                // Get a reference to the array holding the thread local variables inside the
                // ThreadLocalMap of the current thread
                val threadLocalMapClass: Class = Class.forName("java.lang.ThreadLocal\$ThreadLocalMap")
                val tableField: Field = threadLocalMapClass.getDeclaredField("table")
                tableField.setAccessible(true)
                val table: Object = tableField.get(threadLocalTable)

                // The key to the ThreadLocalMap is a WeakReference object. The referent field of this object
                // is a reference to the actual ThreadLocal variable
                val referentField: Field = Reference::class.java.getDeclaredField("referent")
                referentField.setAccessible(true)
                for (i in 0 until Array.getLength(table)) {
                    // Each entry in the table array of ThreadLocalMap is an Entry object
                    // representing the thread local reference and its value
                    val entry: Object = Array.get(table, i)
                    if (entry != null) {
                        // Get a reference to the thread local object and remove it from the table
                        val threadLocal: ThreadLocal = referentField.get(entry) as ThreadLocal
                        clean(threadLocal)
                    }
                }
            } catch (e: Exception) {
                // We will tolerate an exception here and just log it
                throw IllegalStateException(e)
            }
        }
    }
}