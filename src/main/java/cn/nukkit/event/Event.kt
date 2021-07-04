package cn.nukkit.event

import cn.nukkit.utils.EventException

/**
 * 描述服务器中可能发生的事情的类。<br></br>
 * Describes things that happens in the server.
 *
 *
 * 服务器中可能发生的事情称作**事件**。定义一个需要它在一个事件发生时被运行的过程，这个过程称作**监听器**。<br></br>
 * Things that happens in the server is called a **event**. Define a procedure that should be executed
 * when a event happens, this procedure is called a **listener**.
 *
 *
 * Nukkit调用事件的处理器时，会通过参数的类型判断需要被监听的事件。<br></br>
 * When Nukkit is calling a handler, the event needed to listen is judged by the type of the parameter.
 *
 *
 * 关于监听器的实现，参阅：[Listener] <br></br>
 * For the way to implement a listener, see: [cn.nukkit.event.Listener]
 *
 * @author Unknown(code) @ Nukkit Project
 * @author 粉鞋大妈(javadoc) @ Nukkit Project
 * @see cn.nukkit.event.EventHandler
 *
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
abstract class Event {
    protected var eventName: String? = null
    var isCancelled = false
        get() {
            if (this !is Cancellable) {
                throw EventException("Event is not Cancellable")
            }
            return field
        }
        set(value) {
            if (this !is Cancellable) {
                throw EventException("Event is not Cancellable")
            }
            field = value
        }

    fun getEventName(): String {
        return if (eventName == null) getClass().getName() else eventName!!
    }

    fun setCancelled() {
        isCancelled = true
    }
}