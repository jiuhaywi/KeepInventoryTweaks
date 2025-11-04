package com.Djwid.KInvTweaks;

import com.Djwid.KInvTweaks.data.PluginDataManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDeathHandler implements Listener {

    private final KeepInventoryTweaks plugin;

    public PlayerDeathHandler(KeepInventoryTweaks plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        PluginDataManager dataManager = plugin.getDataManager();

        boolean keepInv = dataManager.isKeepInventoryEnabled(player.getUniqueId());

        if (!keepInv) {
            Location deathLoc = player.getLocation();


            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null) deathLoc.getWorld().dropItemNaturally(deathLoc, item);
            }

            for (ItemStack item : player.getInventory().getArmorContents()) {
                if (item != null) deathLoc.getWorld().dropItemNaturally(deathLoc, item);
            }

            for (ItemStack item : player.getInventory().getExtraContents()) {
                if (item != null) deathLoc.getWorld().dropItemNaturally(deathLoc, item);
            }

            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.getInventory().setExtraContents(null);

            event.getDrops().clear();
            player.sendMessage("Your KeepInventory is disabled: all items have been dropped at your death location.");
        }
    }
}
