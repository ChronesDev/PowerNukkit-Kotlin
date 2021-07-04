package cn.nukkit.item

import cn.nukkit.Player

/**
 * @author CreeperFace
 * @since 18.3.2017
 */
@Log4j2
class ItemMap @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(MAP, meta, count, "Map") {
    // not very pretty but definitely better than before.
    private var image: BufferedImage? = null

    constructor(meta: Integer?) : this(meta, 1) {}

    @Throws(IOException::class)
    fun setImage(file: File?) {
        setImage(ImageIO.read(file))
    }

    fun setImage(image: BufferedImage) {
        try {
            if (image.getHeight() !== 128 || image.getWidth() !== 128) { //resize
                this.image = BufferedImage(128, 128, image.getType())
                val g: Graphics2D = this.image.createGraphics()
                g.drawImage(image, 0, 0, 128, 128, null)
                g.dispose()
            } else {
                this.image = image
            }
            val baos = ByteArrayOutputStream()
            ImageIO.write(this.image, "png", baos)
            this.getNamedTag().putByteArray("Colors", baos.toByteArray())
        } catch (e: IOException) {
            log.error("Error while adding an image to an ItemMap", e)
        }
    }

    protected fun loadImageFromNBT(): BufferedImage? {
        try {
            val data: ByteArray = getNamedTag().getByteArray("Colors")
            image = ImageIO.read(ByteArrayInputStream(data))
            return image
        } catch (e: IOException) {
            log.error("Error while loading an image of an ItemMap from NBT", e)
        }
        return null
    }

    fun getMapId(): Long {
        return getNamedTag().getLong("map_uuid")
    }

    fun sendImage(p: Player) {
        // don't load the image from NBT if it has been done before.
        val image: BufferedImage = if (image != null) image else loadImageFromNBT()
        val pk = ClientboundMapItemDataPacket()
        pk.mapId = getMapId()
        pk.update = 2
        pk.scale = 0
        pk.width = 128
        pk.height = 128
        pk.offsetX = 0
        pk.offsetZ = 0
        pk.image = image
        p.dataPacket(pk)
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun getMaxStackSize(): Int {
        return 1
    }

    companion object {
        var mapCount = 0
    }

    init {
        when (meta) {
            3 -> this.name = "Ocean Explorer Map"
            4 -> this.name = "Woodland Explorer Map"
            5 -> this.name = "Treasure Map"
        }
        if (!hasCompoundTag() || !getNamedTag().contains("map_uuid")) {
            val tag = CompoundTag()
            tag.putLong("map_uuid", mapCount++)
            this.setNamedTag(tag)
        }
    }
}