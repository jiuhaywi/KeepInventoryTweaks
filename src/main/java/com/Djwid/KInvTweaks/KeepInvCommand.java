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
            sender.sendMessage(ChatColor.RED + "Usage: /keepinventory <update/check> <player> [true|false]");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("update")) {

            if (!sender.hasPermission("keepinventory.update") && !sender.isOp()) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
                return true;
            }

            if (args.length != 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /keepinventory update <player> <true|false>");
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

        } else if (subCommand.equals("check")) {

            UUID targetUUID;
            String targetName;

            // Check your own state if no arguments or if only one arg
            if (args.length == 1) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Console must specify a player: /keepinventory check <player>");
                    return true;
                }
                Player player = (Player) sender;
                targetUUID = player.getUniqueId();
                targetName = player.getName();
            } else {
                // Player specified
                targetName = args[1];
                targetUUID = Bukkit.getOfflinePlayer(targetName).getUniqueId();

                // Only allow checking others if sender is OP or has permission
                if (!sender.hasPermission("keepinventory.update") && !sender.isOp()) {
                    // If the target is yourself, allow
                    if (!(sender instanceof Player && ((Player) sender).getUniqueId().equals(targetUUID))) {
                        sender.sendMessage(ChatColor.RED + "You cannot check other players' KeepInventory.");
                        return true;
                    }
                }
            }

            boolean state = dataManager.isKeepInventoryEnabled(targetUUID);
            sender.sendMessage(ChatColor.AQUA + "KeepInventory for player " + targetName + " is " + state);

        } else {
            sender.sendMessage(ChatColor.RED + "Unknown subcommand. Use 'update' or 'check'.");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("update");
            completions.add("check");
        } else if (args.length == 2) {
            // Suggest online players
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(p.getName());
                }
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("update")) {
            completions.add("true");
            completions.add("false");
        }

        return completions;
    }
}
