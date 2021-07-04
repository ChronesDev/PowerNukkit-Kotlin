package cn.nukkit.plugin

import cn.nukkit.event.Event

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
class MethodEventExecutor(method: Method) : EventExecutor {
    private val method: Method

    @SuppressWarnings("unchecked")
    @Override
    @Throws(EventException::class)
    override fun execute(listener: Listener?, event: Event) {
        try {
            for (param in method.getParameterTypes()) {
                if (param.isAssignableFrom(event.getClass())) {
                    method.invoke(listener, event)
                    break
                }
            }
        } catch (ex: InvocationTargetException) {
            throw EventException(if (ex.getCause() != null) ex.getCause() else ex)
        } catch (ex: ClassCastException) {
            log.debug("Ignoring a ClassCastException", ex)
            // We are going to ignore ClassCastException because EntityDamageEvent can't be cast to EntityDamageByEntityEvent
        } catch (t: Throwable) {
            throw EventException(t)
        }
    }

    fun getMethod(): Method {
        return method
    }

    init {
        this.method = method
    }
}