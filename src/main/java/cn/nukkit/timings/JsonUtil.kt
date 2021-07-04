package cn.nukkit.timings

import com.google.gson.Gson

/**
 * @author Tee7even
 *
 *
 * Various methods for more compact JSON object constructing
 */
@SuppressWarnings("unchecked")
object JsonUtil {
    private val GSON: Gson = GsonBuilder().setPrettyPrinting().create()
    fun toArray(vararg objects: Object?): JsonArray {
        val array: List = ArrayList()
        Collections.addAll(array, objects)
        return GSON.toJsonTree(array).getAsJsonArray()
    }

    fun toObject(`object`: Object?): JsonObject {
        return GSON.toJsonTree(`object`).getAsJsonObject()
    }

    fun <E> mapToObject(collection: Iterable<E>, mapper: Function<E, JSONPair?>): JsonObject {
        val `object`: Map = LinkedHashMap()
        for (e in collection) {
            val pair: JSONPair = mapper.apply(e)
            if (pair != null) {
                `object`.put(pair.key, pair.value)
            }
        }
        return GSON.toJsonTree(`object`).getAsJsonObject()
    }

    fun <E> mapToArray(elements: Array<E>?, mapper: Function<E, Object?>?): JsonArray {
        val array = ArrayList()
        Collections.addAll(array, elements)
        return mapToArray<Any>(array, mapper)
    }

    fun <E> mapToArray(collection: Iterable<E>, mapper: Function<E, Object?>): JsonArray {
        val array: List = ArrayList()
        for (e in collection) {
            val obj: Object = mapper.apply(e)
            if (obj != null) {
                array.add(obj)
            }
        }
        return GSON.toJsonTree(array).getAsJsonArray()
    }

    class JSONPair {
        val key: String
        val value: Object

        constructor(key: String, value: Object) {
            this.key = key
            this.value = value
        }

        constructor(key: Int, value: Object) {
            this.key = String.valueOf(key)
            this.value = value
        }
    }
}