package com.Djwid.KInvTweaks;

import com.Djwid.KInvTweaks.data.PluginDataManager;
import org.bukkit.plugin.java.JavaPlugin;

public class KeepInventoryTweaks extends JavaPlugin {

    private static KeepInventoryTweaks instance;
    private PluginDataManager dataManager;

    @Override
    public void onEnable() {
        instance = this;
        dataManager = new PluginDataManager(this);

        KeepInvCommand keepInvCommand = new KeepInvCommand(this);
        if (getCommand("keep_player_inventory") != null) {
            getCommand("keep_player_inventory").setExecutor(keepInvCommand);
            getCommand("keep_player_inventory").setTabCompleter(keepInvCommand);
        }

        getServer().getPluginManager().registerEvents(new PlayerDeathHandler(this), this);

        getLogger().info("KeepInventoryTweaks initialized and ready!");
    }

    @Override
    public void onDisable() {
        if (dataManager != null) dataManager.savePluginSettings();
        getLogger().info("KeepInventoryTweaks disabled, data saved.");
    }

    public static KeepInventoryTweaks getInstance() {
        return instance;
    }

    public PluginDataManager getDataManager() {
        return dataManager;
    }
}
