package me.abb3v.xsouls;

import me.abb3v.xsouls.commands.AdminManagement;
import me.abb3v.xsouls.listeners.EntityDeathListener;
import me.abb3v.xsouls.services.Souls;
import me.abb3v.xsouls.utils.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class XSouls extends JavaPlugin {

    private static XSouls instance;
    private Souls souls;

    @Override
    public void onEnable() {
        instance = this;
        getServer().getConsoleSender().sendMessage("§b╭───────────────────────────────────────╮");
        getServer().getConsoleSender().sendMessage("§b│§f          XSouls - Version 1.0         §b│");
        getServer().getConsoleSender().sendMessage("§b│§f           abbev 2024 © GPLv2          §b│");

        ConfigManager config = ConfigManager.getInstance(this);
        if (config.loadConfig()) {
            getServer().getConsoleSender().sendMessage("§b│§f             Config Loaded ⚡          §b│");
        } else {
            getServer().getConsoleSender().sendMessage("§b│§f                 Config Failed                  §b│");
        }

        getServer().getConsoleSender().sendMessage("§b│§f        Thanks for using XSouls!       §b│");
        getServer().getConsoleSender().sendMessage("§b╰───────────────────────────────────────╯");


        souls = new Souls(getLogger());
        AdminManagement adminCommand = new AdminManagement(this, souls);

        getCommand("xsouls").setExecutor(adminCommand);
        getCommand("xsouls").setTabCompleter(adminCommand);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(souls), this);

    }

    @Override
    public void onDisable() {
        getLogger().info("XSouls has been disabled.");
    }

    public static XSouls getInstance() {
        return instance;
    }
}
