package cn.nukkit.plugin

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.3.0.0-PN")
class PowerNukkitPlugin : PluginBase() {
    @Override
    override fun onEnable() {
        getServer().getPluginManager().registerEvents(HotFixes(), this)
    }

    /**
     * These are temporary workaround to issues with great impact that wasn't fixed the right way yet.
     */
    private inner class HotFixes : Listener { /*
         * Hotfix from MR.CLEAN: https://discordapp.com/channels/728280425255927879/728284727748067455/734488813774307329
         */
        /*@EventHandler
        public void onOpenEC(InventoryOpenEvent e) {
            if (!e.isCancelled() && e.getInventory() instanceof PlayerEnderChestInventory) {
                final Player p = e.getPlayer();

                Server.getInstance().getScheduler().scheduleDelayedTask(PowerNukkitPlugin.this, () -> {
                    if (p != null && p.isOnline() && p.getTopWindow().isPresent() && p.getTopWindow().get() instanceof PlayerEnderChestInventory) {
                        PlayerEnderChestInventory ec = (PlayerEnderChestInventory) p.getTopWindow().get();
                        ec.sendContents(p);
                    }
                }, 3);
            }
        }*/
    }

    companion object {
        private val INSTANCE = PowerNukkitPlugin()
        @PowerNukkitOnly
        @Since("1.3.0.0-PN")
        fun getInstance(): PowerNukkitPlugin {
            return INSTANCE
        }
    }
}