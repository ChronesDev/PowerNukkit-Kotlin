package cn.nukkit.metrics

import cn.nukkit.api.PowerNukkitOnly

/**
 * bStats collects some data for plugin authors.
 *
 * Check out https://bStats.org/ to learn more about bStats!
 */
@Since("1.4.0.0-PN")
@Log4j2
class Metrics @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(// The name of the server software
        private val name: String?, // The uuid of the server
        private val serverUUID: String?, // Should failed requests be logged?
        private val logFailedRequests: Boolean) {
    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

    // A list with all custom charts
    private val charts: List<CustomChart> = ArrayList()

    /**
     * Creates a new instance and starts submitting immediately.
     *
     * @param name The bStats metrics identifier.
     * @param serverUUID The unique identifier of this server.
     * @param logFailedRequests If failed submissions should be logged.
     * @param logger The server main logger, ignored by PowerNukkit.
     */
    @Since("1.4.0.0-PN")
    constructor(name: String?, serverUUID: String?, logFailedRequests: Boolean, @SuppressWarnings("unused") logger: MainLogger?) : this(name, serverUUID, logFailedRequests) {
    }

    /**
     * Adds a custom chart.
     *
     * @param chart The chart to add.
     */
    @Since("1.4.0.0-PN")
    fun addCustomChart(chart: CustomChart?) {
        if (chart == null) {
            throw IllegalArgumentException("Chart cannot be null!")
        }
        charts.add(chart)
    }

    /**
     * Starts the Scheduler which submits our data every 30 minutes.
     */
    private fun startSubmitting() {
        val submitTask = Runnable { submitData() }

        // Many servers tend to restart at a fixed time at xx:00 which causes an uneven distribution of requests on the
        // bStats backend. To circumvent this problem, we introduce some randomness into the initial and second delay.
        // WARNING: You must not modify and part of this Metrics class, including the submit delay or frequency!
        // WARNING: Modifying this code will get your plugin banned on bStats. Just don't do it!
        val initialDelay = (1000 * 60 * (3 + Math.random() * 3)) as Long
        val secondDelay = (1000 * 60 * (Math.random() * 30)) as Long
        scheduler.schedule(submitTask, initialDelay, TimeUnit.MILLISECONDS)
        scheduler.scheduleAtFixedRate(submitTask, initialDelay + secondDelay, 1000 * 60 * 30L, TimeUnit.MILLISECONDS)
    }// If the chart is null, we skip it// Add the data of the custom charts// Append the name of the server software

    /**
     * Gets the plugin specific data.
     *
     * @return The plugin specific data.
     */
    private val pluginData: JSONObject
        private get() {
            val data = JSONObject()
            data.put("pluginName", name) // Append the name of the server software
            val customCharts = JSONArray()
            for (customChart in charts) {
                // Add the data of the custom charts
                val chart: JSONObject = customChart.getRequestJsonObject()
                        ?: // If the chart is null, we skip it
                        continue
                customCharts.add(chart)
            }
            data.put("customCharts", customCharts)
            return data
        }// OS specific data

    /**
     * Gets the server specific data.
     *
     * @return The server specific data.
     */
    private val serverData: JSONObject
        private get() {
            // OS specific data
            val osName: String = System.getProperty("os.name")
            val osArch: String = System.getProperty("os.arch")
            val osVersion: String = System.getProperty("os.version")
            val coreCount: Int = Runtime.getRuntime().availableProcessors()
            val data = JSONObject()
            data.put("serverUUID", serverUUID)
            data.put("osName", osName)
            data.put("osArch", osArch)
            data.put("osVersion", osVersion)
            data.put("coreCount", coreCount)
            return data
        }

    /**
     * Collects the data and sends it afterwards.
     */
    private fun submitData() {
        val data: JSONObject = serverData
        val pluginData = JSONArray()
        pluginData.add(pluginData)
        data.put("plugins", pluginData)
        try {
            // We are still in the Thread of the timer, so nothing get blocked :)
            sendData(data)
        } catch (e: Exception) {
            // Something went wrong! :(
            if (logFailedRequests) {
                log.warn("Could not submit stats of {}", name, e)
            }
        }
    }

    /**
     * Represents a custom chart.
     */
    @Since("1.4.0.0-PN")
    abstract class CustomChart internal constructor(chartId: String?) {
        // The id of the chart
        val chartId: String

        // If the data is null we don't send the chart.
        private val requestJsonObject: JSONObject?
            private get() {
                val chart = JSONObject()
                chart.put("chartId", chartId)
                try {
                    val data: JSONObject = chartData
                            ?: // If the data is null we don't send the chart.
                            return null
                    chart.put("data", data)
                } catch (t: Exception) {
                    return null
                }
                return chart
            }
        protected abstract val chartData: JSONObject?
            @SuppressWarnings("java:S112") @Since("1.4.0.0-PN") @Throws(Exception::class) protected get

        /**
         * Class constructor.
         *
         * @param chartId The id of the chart.
         */
        init {
            if (chartId == null || chartId.isEmpty()) {
                throw IllegalArgumentException("ChartId cannot be null or empty!")
            }
            this.chartId = chartId
        }
    }

    /**
     * Represents a custom simple pie.
     */
    @Since("1.4.0.0-PN")
    class SimplePie @Since("1.4.0.0-PN") constructor(chartId: String?, callable: Callable<String?>) : CustomChart(chartId) {
        private val callable: Callable<String>

        // Null = skip the chart
        override val chartData: JSONObject?
            @Override @Throws(Exception::class) protected get() {
                val data = JSONObject()
                val value: String = callable.call()
                if (value == null || value.isEmpty()) {
                    // Null = skip the chart
                    return null
                }
                data.put("value", value)
                return data
            }

        /**
         * Class constructor.
         *
         * @param chartId The id of the chart.
         * @param callable The callable which is used to request the chart data.
         */
        init {
            this.callable = callable
        }
    }

    /**
     * Represents a custom advanced pie.
     */
    @Since("1.4.0.0-PN")
    class AdvancedPie @Since("1.4.0.0-PN") constructor(chartId: String?, callable: Callable<Map<String?, Integer?>?>) : CustomChart(chartId) {
        private val callable: Callable<Map<String, Integer>>
        override val chartData: JSONObject?
            @Override @Throws(Exception::class) protected get() = createAdvancedChartData(callable)

        /**
         * Class constructor.
         *
         * @param chartId The id of the chart.
         * @param callable The callable which is used to request the chart data.
         */
        init {
            this.callable = callable
        }
    }

    /**
     * Represents a custom drill down pie.
     */
    @SuppressWarnings("SpellCheckingInspection")
    @Since("1.4.0.0-PN")
    class DrilldownPie @Since("1.4.0.0-PN") constructor(chartId: String?, callable: Callable<Map<String?, Map<String?, Integer?>?>?>) : CustomChart(chartId) {
        private val callable: Callable<Map<String, Map<String, Integer>>>// Null = skip the chart

        // Null = skip the chart
        override val chartData: JSONObject?
            @Override @Throws(Exception::class) get() {
                val data = JSONObject()
                val values = JSONObject()
                val map: Map<String, Map<String, Integer>> = callable.call()
                if (map == null || map.isEmpty()) {
                    // Null = skip the chart
                    return null
                }
                var reallyAllSkipped = true
                for (entryValues in map.entrySet()) {
                    val value = JSONObject()
                    var allSkipped = true
                    for (valueEntry in map[entryValues.getKey()].entrySet()) {
                        value.put(valueEntry.getKey(), valueEntry.getValue())
                        allSkipped = false
                    }
                    if (!allSkipped) {
                        reallyAllSkipped = false
                        values.put(entryValues.getKey(), value)
                    }
                }
                if (reallyAllSkipped) {
                    // Null = skip the chart
                    return null
                }
                data.put(VALUES, values)
                return data
            }

        /**
         * Class constructor.
         *
         * @param chartId The id of the chart.
         * @param callable The callable which is used to request the chart data.
         */
        init {
            this.callable = callable
        }
    }

    /**
     * Represents a custom single line chart.
     */
    @Since("1.4.0.0-PN")
    class SingleLineChart @Since("1.4.0.0-PN") constructor(chartId: String?, callable: Callable<Integer?>) : CustomChart(chartId) {
        private val callable: Callable<Integer>

        // Null = skip the chart
        override val chartData: JSONObject?
            @Override @Throws(Exception::class) protected get() {
                val data = JSONObject()
                val value: Int = callable.call()
                if (value == 0) {
                    // Null = skip the chart
                    return null
                }
                data.put("value", value)
                return data
            }

        /**
         * Class constructor.
         *
         * @param chartId The id of the chart.
         * @param callable The callable which is used to request the chart data.
         */
        init {
            this.callable = callable
        }
    }

    /**
     * Represents a custom multi line chart.
     */
    @Since("1.4.0.0-PN")
    class MultiLineChart @Since("1.4.0.0-PN") constructor(chartId: String?, callable: Callable<Map<String?, Integer?>?>) : CustomChart(chartId) {
        private val callable: Callable<Map<String, Integer>>
        override val chartData: JSONObject?
            @Override @Throws(Exception::class) protected get() = createAdvancedChartData(callable)

        /**
         * Class constructor.
         *
         * @param chartId The id of the chart.
         * @param callable The callable which is used to request the chart data.
         */
        init {
            this.callable = callable
        }
    }

    /**
     * Represents a custom simple bar chart.
     */
    @Since("1.4.0.0-PN")
    class SimpleBarChart @Since("1.4.0.0-PN") constructor(chartId: String?, callable: Callable<Map<String?, Integer?>?>) : CustomChart(chartId) {
        private val callable: Callable<Map<String, Integer>>

        // Null = skip the chart
        override val chartData: JSONObject?
            @Override @Throws(Exception::class) protected get() {
                val data = JSONObject()
                val values = JSONObject()
                val map: Map<String, Integer> = callable.call()
                if (map == null || map.isEmpty()) {
                    // Null = skip the chart
                    return null
                }
                for (entry in map.entrySet()) {
                    val categoryValues = JSONArray()
                    categoryValues.add(entry.getValue())
                    values.put(entry.getKey(), categoryValues)
                }
                data.put(VALUES, values)
                return data
            }

        /**
         * Class constructor.
         *
         * @param chartId The id of the chart.
         * @param callable The callable which is used to request the chart data.
         */
        init {
            this.callable = callable
        }
    }

    /**
     * Represents a custom advanced bar chart.
     */
    @Since("1.4.0.0-PN")
    class AdvancedBarChart @Since("1.4.0.0-PN") constructor(chartId: String?, callable: Callable<Map<String?, IntArray?>?>) : CustomChart(chartId) {
        private val callable: Callable<Map<String, IntArray>>// Null = skip the chart// Skip this invalid

        // Null = skip the chart
        override val chartData: JSONObject?
            @Override @Throws(Exception::class) protected get() {
                val data = JSONObject()
                val values = JSONObject()
                val map: Map<String, IntArray> = callable.call()
                if (map == null || map.isEmpty()) {
                    // Null = skip the chart
                    return null
                }
                var allSkipped = true
                for (entry in map.entrySet()) {
                    if (entry.getValue().length === 0) {
                        continue  // Skip this invalid
                    }
                    allSkipped = false
                    val categoryValues = JSONArray()
                    for (categoryValue in entry.getValue()) {
                        categoryValues.add(categoryValue)
                    }
                    values.put(entry.getKey(), categoryValues)
                }
                if (allSkipped) {
                    // Null = skip the chart
                    return null
                }
                data.put(VALUES, values)
                return data
            }

        /**
         * Class constructor.
         *
         * @param chartId The id of the chart.
         * @param callable The callable which is used to request the chart data.
         */
        init {
            this.callable = callable
        }
    }

    companion object {
        @Since("1.4.0.0-PN")
        val B_STATS_VERSION = 1
        private const val VALUES = "values"

        // The url to which the data is sent
        private const val URL = "https://bStats.org/submitData/server-implementation"

        /**
         * Sends the data to the bStats server.
         *
         * @param data The data to send.
         * @throws IOException If the request failed.
         */
        @Throws(IOException::class)
        private fun sendData(data: JSONObject?) {
            if (data == null) {
                throw IllegalArgumentException("Data cannot be null!")
            }
            val connection: HttpsURLConnection = URL(URL).openConnection() as HttpsURLConnection

            // Compress the data to save bandwidth
            val compressedData = compress(data.toString())

            // Add headers
            connection.setRequestMethod("POST")
            connection.addRequestProperty("Accept", "application/json")
            connection.addRequestProperty("Connection", "close")
            connection.addRequestProperty("Content-Encoding", "gzip") // We gzip our request
            connection.addRequestProperty("Content-Length", String.valueOf(compressedData.size))
            connection.setRequestProperty("Content-Type", "application/json") // We send our data in JSON format
            connection.setRequestProperty("User-Agent", "MC-Server/" + B_STATS_VERSION)

            // Send data
            connection.setDoOutput(true)
            val outputStream = DataOutputStream(connection.getOutputStream())
            outputStream.write(compressedData)
            outputStream.flush()
            outputStream.close()
            connection.getInputStream().close() // We don't care about the response - Just send our data :)
        }

        /**
         * GZIPs the given String.
         *
         * @param str The string to gzip.
         * @return The gzipped String.
         * @throws IOException If the compression failed.
         */
        @Throws(IOException::class)
        private fun compress(str: String?): ByteArray {
            if (str == null) {
                return EmptyArrays.EMPTY_BYTES
            }
            val outputStream = ByteArrayOutputStream()
            val gzip = GZIPOutputStream(outputStream)
            gzip.write(str.getBytes(StandardCharsets.UTF_8))
            gzip.close()
            return outputStream.toByteArray()
        }

        @Throws(Exception::class)
        private fun createAdvancedChartData(callable: Callable<Map<String, Integer>>): JSONObject? {
            val data = JSONObject()
            val values = JSONObject()
            val map: Map<String, Integer> = callable.call()
            if (map == null || map.isEmpty()) {
                // Null = skip the chart
                return null
            }
            var allSkipped = true
            for (entry in map.entrySet()) {
                if (entry.getValue() === 0) {
                    continue  // Skip this invalid
                }
                allSkipped = false
                values.put(entry.getKey(), entry.getValue())
            }
            if (allSkipped) {
                // Null = skip the chart
                return null
            }
            data.put(VALUES, values)
            return data
        }
    }

    /**
     * Creates a new instance and starts submitting immediately.
     *
     * @param name The bStats metrics identifier.
     * @param serverUUID The unique identifier of this server.
     * @param logFailedRequests If failed submissions should be logged.
     */
    init {

        // Start submitting the data
        startSubmitting()
    }
}