package com.Lino.lootMeteor.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import com.Lino.lootMeteor.LootMeteor;
import com.Lino.lootMeteor.gui.LootEditorGUI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LootMeteorCommand implements CommandExecutor, TabCompleter {

    private final LootMeteor plugin;

    public LootMeteorCommand(LootMeteor plugin) {
        this.plugin = plugin;
        plugin.getCommand("lootmeteor").setTabCompleter(this);
        plugin.getCommand("lm").setExecutor(this);
        plugin.getCommand("lm").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("lootmeteor.admin")) {
            sender.sendMessage(plugin.getMessageManager().getMessage("no-permission"));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "edit":
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    new LootEditorGUI(plugin, player).open();
                } else {
                    sender.sendMessage("This command can only be used by players!");
                }
                break;

            case "reload":
                plugin.getConfigManager().reload();
                plugin.getMessageManager().reload();
                plugin.getLootManager().loadLoot();
                sender.sendMessage(plugin.getMessageManager().getMessage("reload-success"));
                break;

            case "spawn":
                plugin.getMeteorManager().spawnMeteor();
                sender.sendMessage("Meteor spawned!");
                break;

            default:
                sendHelp(sender);
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("lootmeteor.admin")) {
            return new ArrayList<>();
        }

        if (args.length == 1) {
            List<String> completions = Arrays.asList("edit", "reload", "spawn");
            List<String> result = new ArrayList<>();
            for (String completion : completions) {
                if (completion.toLowerCase().startsWith(args[0].toLowerCase())) {
                    result.add(completion);
                }
            }
            return result;
        }

        return new ArrayList<>();
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6=== LootMeteor Commands ===");
        sender.sendMessage("§e/lootmeteor edit §7- Open loot editor GUI");
        sender.sendMessage("§e/lootmeteor reload §7- Reload configuration");
        sender.sendMessage("§e/lootmeteor spawn §7- Force spawn a meteor");
        sender.sendMessage("§e/lm §7- Alias for /lootmeteor");
    }
}