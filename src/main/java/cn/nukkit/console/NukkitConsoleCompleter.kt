package cn.nukkit.console

import cn.nukkit.Server

@RequiredArgsConstructor
class NukkitConsoleCompleter : Completer {
    private val server: Server? = null
    @Override
    fun complete(lineReader: LineReader?, parsedLine: ParsedLine, candidates: List<Candidate?>) {
        if (parsedLine.wordIndex() === 0) {
            if (parsedLine.word().isEmpty()) {
                addCandidates(Consumer<String> { s -> candidates.add(Candidate(s)) })
                return
            }
            val names: SortedSet<String> = TreeSet()
            addCandidates(names::add)
            for (match in names) {
                if (!match.toLowerCase().startsWith(parsedLine.word())) {
                    continue
                }
                candidates.add(Candidate(match))
            }
        } else if (parsedLine.wordIndex() > 0 && !parsedLine.word().isEmpty()) {
            val word: String = parsedLine.word()
            val names: SortedSet<String> = TreeSet()
            server.getOnlinePlayers().values().forEach { p -> names.add(p.getName()) }
            for (match in names) {
                if (!match.toLowerCase().startsWith(word.toLowerCase())) {
                    continue
                }
                candidates.add(Candidate(match))
            }
        }
    }

    private fun addCandidates(commandConsumer: Consumer<String>) {
        for (command in server.getCommandMap().getCommands().keySet()) {
            if (!command.contains(":")) {
                commandConsumer.accept(command)
            }
        }
    }
}