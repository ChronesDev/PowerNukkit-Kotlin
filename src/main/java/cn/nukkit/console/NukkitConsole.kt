package cn.nukkit.console

import cn.nukkit.Server

@RequiredArgsConstructor
class NukkitConsole : SimpleTerminalConsole() {
    private val server: Server? = null
    private val consoleQueue: BlockingQueue<String> = LinkedBlockingQueue()
    private val executingCommands: AtomicBoolean = AtomicBoolean(false)

    @get:Override
    protected val isRunning: Boolean
        protected get() = server.isRunning()

    @Override
    protected fun runCommand(command: String?) {
        if (executingCommands.get()) {
            Timings.serverCommandTimer.startTiming()
            val event = ServerCommandEvent(server.getConsoleSender(), command)
            if (server.getPluginManager() != null) {
                server.getPluginManager().callEvent(event)
            }
            if (!event.isCancelled()) {
                Server.getInstance().getScheduler().scheduleTask { server.dispatchCommand(event.getSender(), event.getCommand()) }
            }
            Timings.serverCommandTimer.stopTiming()
        } else {
            consoleQueue.add(command)
        }
    }

    fun readLine(): String {
        return try {
            consoleQueue.take()
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }

    @Override
    protected fun shutdown() {
        server.shutdown()
    }

    @Override
    protected fun buildReader(builder: LineReaderBuilder): LineReader {
        builder.completer(NukkitConsoleCompleter(server))
        builder.appName("Nukkit")
        builder.option(LineReader.Option.HISTORY_BEEP, false)
        builder.option(LineReader.Option.HISTORY_IGNORE_DUPS, true)
        builder.option(LineReader.Option.HISTORY_IGNORE_SPACE, true)
        return super.buildReader(builder)
    }

    fun isExecutingCommands(): Boolean {
        return executingCommands.get()
    }

    fun setExecutingCommands(executingCommands: Boolean) {
        if (this.executingCommands.compareAndSet(!executingCommands, executingCommands) && executingCommands) {
            consoleQueue.clear()
        }
    }
}