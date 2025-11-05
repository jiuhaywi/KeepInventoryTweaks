package com.Djwid.KInvTweaks;

import com.Djwid.KInvTweaks.data.PluginDataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class KeepInvCommand implements CommandExecutor, TabCompleter {

    private final KeepInventoryTweaks plugin;

    public KeepInvCommand(KeepInventoryTweaks plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        PluginDataManager dataManager = plugin.getDataManager();

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /keep_player_inventory <update/check/list> [args]");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {

            case "update": {
                if (!sender.hasPermission("keepinventory.update") && !sender.isOp()) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
                    return true;
                }

                if (args.length != 3) {
                    sender.sendMessage(ChatColor.RED + "Usage: /keep_player_inventory update <player> <true|false>");
                    return true;
                }

                String targetName = args[1];
                String toggleStr = args[2];

                boolean newState;
                if (toggleStr.equalsIgnoreCase("true")) newState = true;
                else if (toggleStr.equalsIgnoreCase("false")) newState = false;
                else {
                    sender.sendMessage(ChatColor.RED + "Third argument must be 'true' or 'false'.");
                    return true;
                }

                UUID targetUUID = Bukkit.getOfflinePlayer(targetName).getUniqueId();
                boolean previousState = dataManager.isKeepInventoryEnabled(targetUUID);

                if (previousState == newState) {
                    sender.sendMessage(ChatColor.YELLOW + targetName + "'s KeepInventory is already " + newState + ". No change made.");
                    return true;
                }

                dataManager.setKeepInventoryEnabled(targetUUID, newState);
                sender.sendMessage(ChatColor.GREEN + "KeepInventory for " + targetName +
                        " changed from " + previousState + " to " + newState);

                Player target = Bukkit.getPlayer(targetUUID);
                if (target != null) {
                    target.sendMessage(ChatColor.YELLOW + "Your KeepInventory has been " +
                            (newState ? "enabled" : "disabled") + " by " + sender.getName());
                }
                break;
            }

            case "check": {
                UUID targetUUID;
                String targetName;

                if (args.length == 1) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.RED + "Console must specify a player: /keep_player_inventory check <player>");
                        return true;
                    }
                    Player player = (Player) sender;
                    targetUUID = player.getUniqueId();
                    targetName = player.getName();
                } else {
                    targetName = args[1];
                    targetUUID = Bukkit.getOfflinePlayer(targetName).getUniqueId();

                    if (!sender.hasPermission("keepinventory.update") && !sender.isOp()) {
                        if (!(sender instanceof Player && ((Player) sender).getUniqueId().equals(targetUUID))) {
                            sender.sendMessage(ChatColor.RED + "You cannot check other players' KeepInventory.");
                            return true;
                        }
                    }
                }

                boolean state = dataManager.isKeepInventoryEnabled(targetUUID);
                sender.sendMessage(ChatColor.AQUA + "KeepInventory for player " + targetName + " is " +
                        (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"));
                break;
            }

            case "list": {
                if (!sender.hasPermission("keepinventory.update") && !sender.isOp()) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
                    return true;
                }

                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /keep_player_inventory list <all|enabled|disabled>");
                    return true;
                }

                String type = args[1].toLowerCase();
                Map<UUID, String> allPlayers = dataManager.getAllPlayers();
                List<String> output = new ArrayList<>();

                switch (type) {
                    case "all":
                        output.add(ChatColor.GOLD + "=== KeepInventory: All Players ===");
                        for (Map.Entry<UUID, String> entry : allPlayers.entrySet()) {
                            boolean enabled = dataManager.isKeepInventoryEnabled(entry.getKey());
                            output.add(ChatColor.GRAY + "- " + entry.getValue() + ": " +
                                    (enabled ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"));
                        }
                        break;

                    case "enabled":
                        output.add(ChatColor.GOLD + "=== KeepInventory: Enabled Players ===");
                        for (Map.Entry<UUID, String> entry : allPlayers.entrySet()) {
                            if (dataManager.isKeepInventoryEnabled(entry.getKey())) {
                                output.add(ChatColor.GREEN + "- " + entry.getValue());
                            }
                        }
                        break;

                    case "disabled":
                        output.add(ChatColor.GOLD + "=== KeepInventory: Disabled Players ===");
                        for (Map.Entry<UUID, String> entry : allPlayers.entrySet()) {
                            if (!dataManager.isKeepInventoryEnabled(entry.getKey())) {
                                output.add(ChatColor.RED + "- " + entry.getValue());
                            }
                        }
                        break;

                    default:
                        sender.sendMessage(ChatColor.RED + "Invalid type. Use: all, enabled, or disabled.");
                        return true;
                }

                if (output.size() == 1) {
                    sender.sendMessage(ChatColor.YELLOW + "No players found for that category.");
                } else {
                    output.forEach(sender::sendMessage);
                }
                break;
            }

            default:
                sender.sendMessage(ChatColor.RED + "Unknown subcommand. Use 'update', 'check', or 'list'.");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("update");
            completions.add("check");
            completions.add("list");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("update") || args[0].equalsIgnoreCase("check")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(p.getName());
                    }
                }
            } else if (args[0].equalsIgnoreCase("list")) {
                completions.add("all");
                completions.add("enabled");
                completions.add("disabled");
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("update")) {
            completions.add("true");
            completions.add("false");
        }

        return completions;
    }
}
