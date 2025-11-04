
package com.Djwid.KInvTweaks.data;

import com.Djwid.KInvTweaks.KeepInventoryTweaks;
import com.Djwid.KInvTweaks.util.JsonUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PluginDataManager {

    private final KeepInventoryTweaks plugin;
    private final File dataFolder;
    private final File pluginConfigFile;

    private Set<UUID> enabledPlayers;

    public PluginDataManager(KeepInventoryTweaks plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "players");
        this.pluginConfigFile = new File(plugin.getDataFolder(), "settings.json");

        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        if (!dataFolder.exists()) dataFolder.mkdirs();

        loadPluginSettings();
    }

    public void loadPluginSettings() {
        if (pluginConfigFile.exists()) {
            PluginSettings settings = JsonUtils.readJson(pluginConfigFile, PluginSettings.class);
            if (settings != null) {
                enabledPlayers = new HashSet<>(settings.enabledPlayers);
                return;
            }
        }
        enabledPlayers = new HashSet<>();
        savePluginSettings();
    }

    public void savePluginSettings() {
        PluginSettings settings = new PluginSettings(enabledPlayers);
        JsonUtils.writeJson(pluginConfigFile, settings);
    }

    public boolean isKeepInventoryEnabled(UUID uuid) {
        return enabledPlayers.contains(uuid);
    }

    public void setKeepInventoryEnabled(UUID uuid, boolean enabled) {
        if (enabled) enabledPlayers.add(uuid);
        else enabledPlayers.remove(uuid);
        savePluginSettings();
    }

    private static class PluginSettings {
        public Set<UUID> enabledPlayers;

        public PluginSettings() {}
        public PluginSettings(Set<UUID> enabledPlayers) {
            this.enabledPlayers = enabledPlayers;
        }
    }
}
