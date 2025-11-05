package com.Djwid.KInvTweaks.data;

import com.Djwid.KInvTweaks.KeepInventoryTweaks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class PluginDataManager {

    private final KeepInventoryTweaks plugin;
    private final File dataFolder;
    private final File playersFile;
    private final File disabledPlayersFile;

    private final Map<UUID, String> allPlayers = new HashMap<>();
    private final Set<UUID> disabledKeepInv = new HashSet<>();

    public PluginDataManager(KeepInventoryTweaks plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "data");
        if (!dataFolder.exists()) dataFolder.mkdirs();

        this.playersFile = new File(dataFolder, "players.json");
        this.disabledPlayersFile = new File(dataFolder, "disabled_players.json");

        loadAllData();

        // Register join listener for auto-tracking
        Bukkit.getPluginManager().registerEvents(new org.bukkit.event.Listener() {
            @org.bukkit.event.EventHandler
            public void onJoin(org.bukkit.event.player.PlayerJoinEvent event) {
                registerPlayer(event.getPlayer());
            }
        }, plugin);
    }

    private void loadAllData() {
        loadPlayers();
        loadDisabledPlayers();
    }

    private void loadPlayers() {
        if (!playersFile.exists()) return;
        try {
            String content = Files.readString(playersFile.toPath());
            if (!content.isEmpty()) {
                JSONObject obj = new JSONObject(content);
                for (String key : obj.keySet()) {
                    allPlayers.put(UUID.fromString(key), obj.getString(key));
                }
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to load players.json: " + e.getMessage());
        }
    }

    private void loadDisabledPlayers() {
        if (!disabledPlayersFile.exists()) return;
        try {
            String content = Files.readString(disabledPlayersFile.toPath());
            if (!content.isEmpty()) {
                JSONArray arr = new JSONArray(content);
                for (int i = 0; i < arr.length(); i++) {
                    disabledKeepInv.add(UUID.fromString(arr.getString(i)));
                }
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to load disabled_players.json: " + e.getMessage());
        }
    }

    public void registerPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        if (!allPlayers.containsKey(uuid)) {
            allPlayers.put(uuid, player.getName());
            savePlayers();
            plugin.getLogger().info("Registered new player: " + player.getName() + " (" + uuid + ")");
        }
    }

    public boolean isKeepInventoryEnabled(UUID uuid) {
        return !disabledKeepInv.contains(uuid);
    }

    public Map<UUID, String> getAllPlayers() {
    return Collections.unmodifiableMap(allPlayers);
    }


    public void setKeepInventoryEnabled(UUID uuid, boolean enabled) {
        if (enabled) {
            disabledKeepInv.remove(uuid);
        } else {
            disabledKeepInv.add(uuid);
        }
        saveDisabledPlayers();

        // Make sure player is tracked in players.json
        OfflinePlayer off = Bukkit.getOfflinePlayer(uuid);
        if (off != null && off.getName() != null) {
            allPlayers.putIfAbsent(uuid, off.getName());
            savePlayers();
        }
    }

    public void savePluginSettings() {
        savePlayers();
        saveDisabledPlayers();
    }

    private void savePlayers() {
        try {
            JSONObject obj = new JSONObject();
            for (Map.Entry<UUID, String> entry : allPlayers.entrySet()) {
                obj.put(entry.getKey().toString(), entry.getValue());
            }
            Files.writeString(playersFile.toPath(), obj.toString(4));
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save players.json: " + e.getMessage());
        }
    }

    private void saveDisabledPlayers() {
        try {
            JSONArray arr = new JSONArray();
            for (UUID uuid : disabledKeepInv) arr.put(uuid.toString());
            Files.writeString(disabledPlayersFile.toPath(), arr.toString(4));
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save disabled_players.json: " + e.getMessage());
        }
    }
}
