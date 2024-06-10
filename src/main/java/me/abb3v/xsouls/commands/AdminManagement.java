package me.abb3v.xsouls.commands;

import me.abb3v.xsouls.XSouls;
import me.abb3v.xsouls.services.Souls;
import me.abb3v.xsouls.utils.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AdminManagement implements CommandExecutor, TabCompleter {

    private final XSouls plugin;
    private final Souls souls;

    public AdminManagement(XSouls plugin, Souls souls) {
        this.plugin = plugin;
        this.souls = souls;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendVersion(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                handleReload(sender);
                break;
            case "souls":
                handleSoulsCommand(sender, args);
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Unknown command. Use /xsouls for help.");
                break;
        }
        return true;
    }

    private void sendVersion(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "XSouls version: " + plugin.getDescription().getVersion());
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("xsouls.admin.reload")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return;
        }
        ConfigManager.getInstance(plugin).reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "XSouls configuration reloaded.");
    }

    private void handleSoulsCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("xsouls.admin.souls")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return;
        }
        if (args.length < 3 || (args.length == 4 && args[1].equalsIgnoreCase("get"))) {
            sender.sendMessage(ChatColor.RED + "Usage: /xsouls souls <get:add:set:remove> <playername> <amount>");
            return;
        }

        String action = args[1].toLowerCase();
        String playerName = args[2];
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return;
        }

        switch (action) {
            case "get":
                sendSoulsAmount(sender, target);
                break;
            case "add":
            case "set":
            case "remove":
                handleAmountBasedCommands(sender, action, target, args);
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Usage: /xsouls souls <get:add:set:remove> <playername> <amount>");
                break;
        }
    }

    private void sendSoulsAmount(CommandSender sender, Player target) {
        int soulsAmount = souls.getSouls(target.getUniqueId());
        sender.sendMessage(ChatColor.GREEN + target.getName() + " has " + soulsAmount + " souls.");
    }

    private void handleAmountBasedCommands(CommandSender sender, String action, Player target, String[] args) {
        if (args.length != 4) {
            sender.sendMessage(ChatColor.RED + "Usage: /xsouls souls <get:add:set:remove> <playername> <amount>");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid number.");
            return;
        }

        switch (action) {
            case "add":
                addSouls(sender, target, amount);
                break;
            case "set":
                setSouls(sender, target, amount);
                break;
            case "remove":
                removeSouls(sender, target, amount);
                break;
        }
    }

    private void addSouls(CommandSender sender, Player target, int amount) {
        souls.addSouls(target.getUniqueId(), amount);
        sender.sendMessage(ChatColor.GREEN + "Added " + amount + " souls to " + target.getName());
    }

    private void setSouls(CommandSender sender, Player target, int amount) {
        souls.setSouls(target.getUniqueId(), amount);
        sender.sendMessage(ChatColor.GREEN + "Set " + target.getName() + "'s souls to " + amount);
    }

    private void removeSouls(CommandSender sender, Player target, int amount) {
        boolean success = souls.removeSouls(target.getUniqueId(), amount);
        if (success) {
            sender.sendMessage(ChatColor.GREEN + "Removed " + amount + " souls from " + target.getName());
        } else {
            sender.sendMessage(ChatColor.RED + "Not enough souls to remove.");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("xsouls.admin.reload")) {
                completions.add("reload");
            }
            if (sender.hasPermission("xsouls.admin.souls")) {
                completions.add("souls");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("souls") && sender.hasPermission("xsouls.admin.souls")) {
            completions.addAll(Arrays.asList("get", "add", "set", "remove"));
        } else if (args.length == 3 && args[0].equalsIgnoreCase("souls") && sender.hasPermission("xsouls.admin.souls")) {
            completions.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList()));
        } else if (args.length == 4 && args[0].equalsIgnoreCase("souls") && sender.hasPermission("xsouls.admin.souls")) {
            if (!args[1].equalsIgnoreCase("get")) {
                completions.add("<amount>");
            }
        }

        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
}