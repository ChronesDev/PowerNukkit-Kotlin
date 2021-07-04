package cn.nukkit.utils.bugreport

import cn.nukkit.Nukkit

/**
 * Project nukkit
 */
@Log4j2
class BugReportGenerator internal constructor(private val throwable: Throwable) : Thread() {
    @Override
    fun run() {
        val baseLang: BaseLang = Server.getInstance().getLanguage()
        try {
            log.info("[BugReport] " + baseLang.translateString("nukkit.bugreport.create"))
            val path = generate()
            log.info("[BugReport] " + baseLang.translateString("nukkit.bugreport.archive", path))
        } catch (e: Exception) {
            log.info("[BugReport] " + baseLang.translateString("nukkit.bugreport.error", e.getMessage()), e)
        }
    }

    @Throws(IOException::class)
    private fun generate(): String {
        val reports = File(Nukkit.DATA_PATH, "logs/bug_reports")
        if (!reports.isDirectory()) {
            reports.mkdirs()
        }
        val simpleDateFormat = SimpleDateFormat("yyyyMMddHHmmss")
        val date: String = simpleDateFormat.format(Date())
        val model = StringBuilder()
        var totalDiskSpace: Long = 0
        var diskNum = 0
        for (root in FileSystems.getDefault().getRootDirectories()) {
            try {
                val store: FileStore = Files.getFileStore(root)
                model.append("Disk ").append(diskNum++).append(":(avail=").append(getCount(store.getUsableSpace(), true))
                        .append(", total=").append(getCount(store.getTotalSpace(), true))
                        .append(") ")
                totalDiskSpace += store.getTotalSpace()
            } catch (e: IOException) {
                //
            }
        }
        val stringWriter = StringWriter()
        throwable.printStackTrace(PrintWriter(stringWriter))
        val stackTrace: Array<StackTraceElement> = throwable.getStackTrace()
        var pluginError = false
        if (stackTrace.size > 0) {
            pluginError = !throwable.getStackTrace().get(0).getClassName().startsWith("cn.nukkit")
        }
        val mdReport = File(reports, date + "_" + throwable.getClass().getSimpleName() + ".md")
        mdReport.createNewFile()
        var content: String = Utils.readFile(this.getClass().getClassLoader().getResourceAsStream("report_template.md"))
        val cpuType: String = System.getenv("PROCESSOR_IDENTIFIER")
        val osMXBean: OperatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean() as OperatingSystemMXBean
        content = content.replace("\${NUKKIT_VERSION}", Nukkit.VERSION)
        content = content.replace("\${NUKKIT_COMMIT}", Nukkit.GIT_COMMIT)
        content = content.replace("\${JAVA_VERSION}", System.getProperty("java.vm.name").toString() + " (" + System.getProperty("java.runtime.version") + ")")
        content = content.replace("\${HOSTOS}", osMXBean.getName().toString() + "-" + osMXBean.getArch() + " [" + osMXBean.getVersion() + "]")
        content = content.replace("\${MEMORY}", getCount(osMXBean.getTotalPhysicalMemorySize(), true))
        content = content.replace("\${STORAGE_SIZE}", getCount(totalDiskSpace, true))
        content = content.replace("\${CPU_TYPE}", cpuType ?: "UNKNOWN")
        content = content.replace("\${AVAILABLE_CORE}", String.valueOf(osMXBean.getAvailableProcessors()))
        content = content.replace("\${STACKTRACE}", stringWriter.toString())
        content = content.replace("\${PLUGIN_ERROR}", toString(pluginError).toUpperCase())
        content = content.replace("\${STORAGE_TYPE}", model.toString())
        Utils.writeFile(mdReport, content)
        return mdReport.getAbsolutePath()
    }

    companion object {
        //Code section from SOF
        fun getCount(bytes: Long, si: Boolean): String {
            val unit = if (si) 1000 else 1024
            if (bytes < unit) return "$bytes B"
            val exp = (Math.log(bytes) / Math.log(unit)) as Int
            val pre: String = (if (si) "kMGTPE" else "KMGTPE").charAt(exp - 1).toString() + if (si) "" else "i"
            return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre)
        }
    }
}