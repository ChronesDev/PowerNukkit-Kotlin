package cn.nukkit.entity

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EntityHuman(chunk: FullChunk?, nbt: CompoundTag?) : EntityHumanType(chunk, nbt) {
    protected var uuid: UUID? = null
    var rawUniqueId: ByteArray
        protected set

    @get:Override
    override val width: Float
        get() = 0.6f

    @get:Override
    override val length: Float
        get() = 0.6f

    @get:Override
    override val height: Float
        get() = 1.8f

    @get:Override
    override val eyeHeight: Float
        get() = 1.62f

    @get:Override
    protected override val baseOffset: Float
        protected get() = eyeHeight
    protected var skin: Skin? = null

    @get:Override
    override val networkId: Int
        get() = -1

    fun getSkin(): Skin? {
        return skin
    }

    val uniqueId: UUID?
        get() = uuid

    fun setSkin(skin: Skin?) {
        this.skin = skin
    }

    @Override
    protected override fun initEntity() {
        this.setDataFlag(DATA_PLAYER_FLAGS, DATA_PLAYER_FLAG_SLEEP, false)
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_GRAVITY)
        this.setDataProperty(IntPositionEntityData(DATA_PLAYER_BED_POSITION, 0, 0, 0), false)
        if (this !is Player) {
            if (this.namedTag.contains("NameTag")) {
                this.setNameTag(this.namedTag.getString("NameTag"))
            }
            if (this.namedTag.contains("Skin") && this.namedTag.get("Skin") is CompoundTag) {
                val skinTag: CompoundTag = this.namedTag.getCompound("Skin")
                if (!skinTag.contains("Transparent")) {
                    skinTag.putBoolean("Transparent", false)
                }
                val newSkin = Skin()
                if (skinTag.contains("ModelId")) {
                    newSkin.setSkinId(skinTag.getString("ModelId"))
                }
                if (skinTag.contains("PlayFabID")) {
                    newSkin.setPlayFabId(skinTag.getString("PlayFabID"))
                }
                if (skinTag.contains("Data")) {
                    val data: ByteArray = skinTag.getByteArray("Data")
                    if (skinTag.contains("SkinImageWidth") && skinTag.contains("SkinImageHeight")) {
                        val width: Int = skinTag.getInt("SkinImageWidth")
                        val height: Int = skinTag.getInt("SkinImageHeight")
                        newSkin.setSkinData(SerializedImage(width, height, data))
                    } else {
                        newSkin.setSkinData(data)
                    }
                }
                if (skinTag.contains("CapeId")) {
                    newSkin.setCapeId(skinTag.getString("CapeId"))
                }
                if (skinTag.contains("CapeData")) {
                    val data: ByteArray = skinTag.getByteArray("CapeData")
                    if (skinTag.contains("CapeImageWidth") && skinTag.contains("CapeImageHeight")) {
                        val width: Int = skinTag.getInt("CapeImageWidth")
                        val height: Int = skinTag.getInt("CapeImageHeight")
                        newSkin.setCapeData(SerializedImage(width, height, data))
                    } else {
                        newSkin.setCapeData(data)
                    }
                }
                if (skinTag.contains("GeometryName")) {
                    newSkin.setGeometryName(skinTag.getString("GeometryName"))
                }
                if (skinTag.contains("SkinResourcePatch")) {
                    newSkin.setSkinResourcePatch(String(skinTag.getByteArray("SkinResourcePatch"), StandardCharsets.UTF_8))
                }
                if (skinTag.contains("GeometryData")) {
                    newSkin.setGeometryData(String(skinTag.getByteArray("GeometryData"), StandardCharsets.UTF_8))
                }
                if (skinTag.contains("AnimationData")) {
                    newSkin.setAnimationData(String(skinTag.getByteArray("AnimationData"), StandardCharsets.UTF_8))
                }
                if (skinTag.contains("PremiumSkin")) {
                    newSkin.setPremium(skinTag.getBoolean("PremiumSkin"))
                }
                if (skinTag.contains("PersonaSkin")) {
                    newSkin.setPersona(skinTag.getBoolean("PersonaSkin"))
                }
                if (skinTag.contains("CapeOnClassicSkin")) {
                    newSkin.setCapeOnClassic(skinTag.getBoolean("CapeOnClassicSkin"))
                }
                if (skinTag.contains("AnimatedImageData")) {
                    val list: ListTag<CompoundTag> = skinTag.getList("AnimatedImageData", CompoundTag::class.java)
                    for (animationTag in list.getAll()) {
                        val frames: Float = animationTag.getFloat("Frames")
                        val type: Int = animationTag.getInt("Type")
                        val image: ByteArray = animationTag.getByteArray("Image")
                        val width: Int = animationTag.getInt("ImageWidth")
                        val height: Int = animationTag.getInt("ImageHeight")
                        val expression: Int = animationTag.getInt("AnimationExpression")
                        newSkin.getAnimations().add(SkinAnimation(SerializedImage(width, height, image), type, frames, expression))
                    }
                }
                if (skinTag.contains("ArmSize")) {
                    newSkin.setArmSize(skinTag.getString("ArmSize"))
                }
                if (skinTag.contains("SkinColor")) {
                    newSkin.setSkinColor(skinTag.getString("SkinColor"))
                }
                if (skinTag.contains("PersonaPieces")) {
                    val pieces: ListTag<CompoundTag> = skinTag.getList("PersonaPieces", CompoundTag::class.java)
                    for (piece in pieces.getAll()) {
                        newSkin.getPersonaPieces().add(PersonaPiece(
                                piece.getString("PieceId"),
                                piece.getString("PieceType"),
                                piece.getString("PackId"),
                                piece.getBoolean("IsDefault"),
                                piece.getString("ProductId")
                        ))
                    }
                }
                if (skinTag.contains("PieceTintColors")) {
                    val tintColors: ListTag<CompoundTag> = skinTag.getList("PieceTintColors", CompoundTag::class.java)
                    for (tintColor in tintColors.getAll()) {
                        newSkin.getTintColors().add(PersonaPieceTint(
                                tintColor.getString("PieceType"),
                                tintColor.getList("Colors", StringTag::class.java).getAll().stream()
                                        .map { stringTag -> stringTag.data }.collect(Collectors.toList())
                        ))
                    }
                }
                if (skinTag.contains("IsTrustedSkin")) {
                    newSkin.setTrusted(skinTag.getBoolean("IsTrustedSkin"))
                }
                setSkin(newSkin)
            }
            uuid = Utils.dataToUUID(String.valueOf(this.getId()).getBytes(StandardCharsets.UTF_8), getSkin()
                    .getSkinData().data, this.getNameTag().getBytes(StandardCharsets.UTF_8))
        }
        super.initEntity()
    }

    @get:Override
    override val name: String
        get() = this.getNameTag()

    @Override
    override fun saveNBT() {
        super.saveNBT()
        if (skin != null) {
            val skinTag: CompoundTag = CompoundTag()
                    .putByteArray("Data", getSkin().getSkinData().data)
                    .putInt("SkinImageWidth", getSkin().getSkinData().width)
                    .putInt("SkinImageHeight", getSkin().getSkinData().height)
                    .putString("ModelId", getSkin().getSkinId())
                    .putString("CapeId", getSkin().getCapeId())
                    .putByteArray("CapeData", getSkin().getCapeData().data)
                    .putInt("CapeImageWidth", getSkin().getCapeData().width)
                    .putInt("CapeImageHeight", getSkin().getCapeData().height)
                    .putByteArray("SkinResourcePatch", getSkin().getSkinResourcePatch().getBytes(StandardCharsets.UTF_8))
                    .putByteArray("GeometryData", getSkin().getGeometryData().getBytes(StandardCharsets.UTF_8))
                    .putByteArray("AnimationData", getSkin().getAnimationData().getBytes(StandardCharsets.UTF_8))
                    .putBoolean("PremiumSkin", getSkin().isPremium())
                    .putBoolean("PersonaSkin", getSkin().isPersona())
                    .putBoolean("CapeOnClassicSkin", getSkin().isCapeOnClassic())
                    .putString("ArmSize", getSkin().getArmSize())
                    .putString("SkinColor", getSkin().getSkinColor())
                    .putBoolean("IsTrustedSkin", getSkin().isTrusted())
            val animations: List<SkinAnimation> = getSkin().getAnimations()
            if (!animations.isEmpty()) {
                val animationsTag: ListTag<CompoundTag> = ListTag("AnimationImageData")
                for (animation in animations) {
                    animationsTag.add(CompoundTag()
                            .putFloat("Frames", animation.frames)
                            .putInt("Type", animation.type)
                            .putInt("ImageWidth", animation.image.width)
                            .putInt("ImageHeight", animation.image.height)
                            .putInt("AnimationExpression", animation.expression)
                            .putByteArray("Image", animation.image.data))
                }
                skinTag.putList(animationsTag)
            }
            val personaPieces: List<PersonaPiece> = getSkin().getPersonaPieces()
            if (!personaPieces.isEmpty()) {
                val piecesTag: ListTag<CompoundTag> = ListTag("PersonaPieces")
                for (piece in personaPieces) {
                    piecesTag.add(CompoundTag().putString("PieceId", piece.id)
                            .putString("PieceType", piece.type)
                            .putString("PackId", piece.packId)
                            .putBoolean("IsDefault", piece.isDefault)
                            .putString("ProductId", piece.productId))
                }
            }
            val tints: List<PersonaPieceTint> = getSkin().getTintColors()
            if (!tints.isEmpty()) {
                val tintsTag: ListTag<CompoundTag> = ListTag("PieceTintColors")
                for (tint in tints) {
                    val colors: ListTag<StringTag> = ListTag("Colors")
                    colors.setAll(tint.colors.stream().map { s -> StringTag("", s) }.collect(Collectors.toList()))
                    tintsTag.add(CompoundTag()
                            .putString("PieceType", tint.pieceType)
                            .putList(colors))
                }
            }
            if (!getSkin().getPlayFabId().isEmpty()) {
                skinTag.putString("PlayFabID", getSkin().getPlayFabId())
            }
            this.namedTag.putCompound("Skin", skinTag)
        }
    }

    @Override
    override fun addMovement(x: Double, y: Double, z: Double, yaw: Double, pitch: Double, headYaw: Double) {
        this.level.addPlayerMovement(this, x, y, z, yaw, pitch, headYaw)
    }

    @Override
    override fun spawnTo(player: Player) {
        if (this !== player && !this.hasSpawned.containsKey(player.getLoaderId())) {
            this.hasSpawned.put(player.getLoaderId(), player)
            if (!skin.isValid()) {
                throw IllegalStateException(this.getClass().getSimpleName().toString() + " must have a valid skin set")
            }
            (this as? Player)?.server?.updatePlayerListData(uniqueId, this.getId(), (this as Player).getDisplayName(), skin, (this as Player).getLoginChainData().getXUID(), arrayOf<Player>(player))
                    ?: this.server.updatePlayerListData(uniqueId, this.getId(), name, skin, arrayOf<Player>(player))
            val pk = AddPlayerPacket()
            pk.uuid = uniqueId
            pk.username = name
            pk.entityUniqueId = this.getId()
            pk.entityRuntimeId = this.getId()
            pk.x = this.x as Float
            pk.y = this.y as Float
            pk.z = this.z as Float
            pk.speedX = this.motionX as Float
            pk.speedY = this.motionY as Float
            pk.speedZ = this.motionZ as Float
            pk.yaw = this.yaw as Float
            pk.pitch = this.pitch as Float
            pk.item = this.getInventory().getItemInHand()
            pk.metadata = this.dataProperties
            player.dataPacket(pk)
            this.inventory.sendArmorContents(player)
            this.offhandInventory.sendContents(player)
            if (this.riding != null) {
                val pkk = SetEntityLinkPacket()
                pkk.vehicleUniqueId = this.riding.getId()
                pkk.riderUniqueId = this.getId()
                pkk.type = 1
                pkk.immediate = 1
                player.dataPacket(pkk)
            }
            if (this !is Player) {
                this.server.removePlayerListData(uniqueId, player)
            }
        }
    }

    @Override
    override fun despawnFrom(player: Player) {
        if (this.hasSpawned.containsKey(player.getLoaderId())) {
            val pk = RemoveEntityPacket()
            pk.eid = this.getId()
            player.dataPacket(pk)
            this.hasSpawned.remove(player.getLoaderId())
        }
    }

    @Override
    override fun close() {
        if (!this.closed) {
            if (inventory != null && (this !is Player || (this as Player).loggedIn)) {
                for (viewer in this.inventory.getViewers()) {
                    viewer.removeWindow(this.inventory)
                }
            }
            super.close()
        }
    }

    @Override
    protected override fun onBlock(entity: Entity?, animate: Boolean) {
        super.onBlock(entity, animate)
        var shield: Item = getInventory().getItemInHand()
        var shieldOffhand: Item = getOffhandInventory().getItem(0)
        if (shield.getId() === ItemID.SHIELD) {
            shield = damageArmor(shield, entity)
            getInventory().setItemInHand(shield)
        } else if (shieldOffhand.getId() === ItemID.SHIELD) {
            shieldOffhand = damageArmor(shieldOffhand, entity)
            getOffhandInventory().setItem(0, shieldOffhand)
        }
    }

    companion object {
        const val DATA_PLAYER_FLAG_SLEEP = 1
        const val DATA_PLAYER_FLAG_DEAD = 2
        const val DATA_PLAYER_FLAGS = 26
        const val DATA_PLAYER_BED_POSITION = 28
        const val DATA_PLAYER_BUTTON_TEXT = 40
    }
}