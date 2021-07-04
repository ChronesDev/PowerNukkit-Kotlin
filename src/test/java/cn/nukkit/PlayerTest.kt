package cn.nukkit

import cn.nukkit.block.BlockID

@ExtendWith(PowerNukkitExtension::class)
internal class PlayerTest {
    private val clientId = 32L
    private val clientIp = "1.2.3.4"
    private val clientPort = 3232

    @MockLevel
    var level: Level? = null

    @Mock
    var sourceInterface: SourceInterface? = null
    var skin: Skin? = null
    var player: Player? = null
    @Test
    fun armorDamage() {
        player.attack(EntityDamageEvent(player, EntityDamageEvent.DamageCause.FALL, 1))
        val inventory: PlayerInventory = player.getInventory()

        ////// Block in armor content ////////
        inventory.setArmorContents(arrayOf<Item>(
                Item.getBlock(BlockID.WOOL),
                Item.getBlock(BlockID.WOOL, 1),
                Item.getBlock(BlockID.WOOL, 2),
                Item.getBlock(BlockID.WOOL, 3)
        ))
        for (i in 0..99) {
            player.setHealth(20)
            player.attack(EntityDamageEvent(player, EntityDamageEvent.DamageCause.FALL, 1))
            player.entityBaseTick(20)
        }
        assertEquals(Arrays.asList(
                Item.getBlock(BlockID.WOOL),
                Item.getBlock(BlockID.WOOL, 1),
                Item.getBlock(BlockID.WOOL, 2),
                Item.getBlock(BlockID.WOOL, 3)
        ), Arrays.asList(inventory.getArmorContents()))

        ////// Valid armor in armor content ///////
        inventory.setArmorContents(arrayOf<Item>(
                Item.get(ItemID.LEATHER_CAP),
                Item.get(ItemID.LEATHER_TUNIC),
                Item.get(ItemID.LEATHER_PANTS),
                Item.get(ItemID.LEATHER_BOOTS)
        ))
        for (i in 0..99) {
            player.setHealth(20)
            player.attack(EntityDamageEvent(player, EntityDamageEvent.DamageCause.FALL, 1))
            player.entityBaseTick(20)
        }
        assertEquals(Arrays.asList(
                Item.getBlock(BlockID.AIR),
                Item.getBlock(BlockID.AIR),
                Item.getBlock(BlockID.AIR),
                Item.getBlock(BlockID.AIR)
        ), Arrays.asList(inventory.getArmorContents()))

        ////// Unbreakable armor in armor content ///////
        val items: List<Item> = Arrays.asList(
                Item.get(ItemID.LEATHER_CAP),
                Item.get(ItemID.LEATHER_TUNIC),
                Item.get(ItemID.LEATHER_PANTS),
                Item.get(ItemID.LEATHER_BOOTS)
        )
        items.forEach { item -> item.setNamedTag(CompoundTag().putBoolean("Unbreakable", true)) }
        val array: Array<Item?> = arrayOfNulls<Item>(items.size())
        for (i in 0 until items.size()) {
            array[i] = items[i].clone()
        }
        inventory.setArmorContents(array)
        for (i in 0..99) {
            player.setHealth(20)
            player.attack(EntityDamageEvent(player, EntityDamageEvent.DamageCause.FALL, 1))
            player.entityBaseTick(20)
        }
        assertEquals(ItemID.LEATHER_CAP, items[0].getId())
        assertTrue(items[2].isUnbreakable())
        assertEquals(items, Arrays.asList(inventory.getArmorContents()))
    }

    @Test
    fun dupeCommand() {
        val stick: Item = Item.get(ItemID.STICK)
        val air: Item = Item.getBlock(BlockID.AIR, 0, 0)
        player.getInventory().addItem(stick)
        val actions: List<NetworkInventoryAction> = ArrayList()
        val remove = NetworkInventoryAction()
        remove.sourceType = NetworkInventoryAction.SOURCE_CONTAINER
        remove.windowId = 0
        remove.stackNetworkId = 1
        remove.inventorySlot = 0
        remove.oldItem = stick
        remove.newItem = air
        actions.add(remove)
        for (slot in 1..34) {
            if (slot > 1) {
                actions.add(remove)
            }
            val add = NetworkInventoryAction()
            add.sourceType = NetworkInventoryAction.SOURCE_CONTAINER
            add.windowId = 0
            add.stackNetworkId = 1
            add.inventorySlot = slot
            add.oldItem = air
            add.newItem = stick
            actions.add(add)
        }
        val packet = InventoryTransactionPacket()
        packet.actions = actions.toArray(NetworkInventoryAction.EMPTY_ARRAY)
        player.handleDataPacket(packet)
        val count = countItems(stick)
        assertEquals(1, count)
    }

    private fun countItems(item: Item): Int {
        var count = 0
        for (i in 0 until player.getInventory().getSize()) {
            val inv: Item = player.getInventory().getItem(i)
            if (item.equals(inv)) {
                count += inv.getCount()
            }
        }
        return count
    }

    @BeforeEach
    fun setUp() {
        /// Setup Level ///
        doReturn(Position(100, 64, 200, level)).`when`(level).getSafeSpawn()

        /// Setup Server ///
        doReturn(level).`when`(Server.getInstance()).getDefaultLevel()

        /// Setup skin ///
        skin = Skin()
        skin.setSkinId("test")
        skin.setSkinData(BufferedImage(64, 32, BufferedImage.TYPE_INT_BGR))
        assertTrue(skin.isValid())

        /// Make player login ///
        player = Player(sourceInterface, clientId, clientIp, clientPort)
        val loginPacket = LoginPacket()
        loginPacket.username = "TestPlayer"
        loginPacket.protocol = ProtocolInfo.CURRENT_PROTOCOL
        loginPacket.clientId = 2L
        loginPacket.clientUUID = UUID(3, 3)
        loginPacket.skin = skin
        loginPacket.putLInt(2)
        loginPacket.put("{}".getBytes())
        loginPacket.putLInt(0)
        player.handleDataPacket(loginPacket)
        player.completeLoginSequence()
        assertTrue(player.isOnline(), "Failed to make the fake player login")
        player.doFirstSpawn()
    }
}