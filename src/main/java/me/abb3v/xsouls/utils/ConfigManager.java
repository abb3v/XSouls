package me.abb3v.xsouls.utils;

import org.bukkit.plugin.java.JavaPlugin;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ConfigManager {
    private static ConfigManager instance;
    private TomlParseResult config;
    private final JavaPlugin plugin;
    private final Logger logger;
    private final Map<String, Object> cache = new HashMap<>();

    private ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        loadConfig();
    }

    public static synchronized ConfigManager getInstance(JavaPlugin plugin) {
        if (instance == null) {
            if (plugin == null) {
                throw new IllegalStateException("Config has not been initialized. Call getInstance with a plugin instance first.");
            }
            instance = new ConfigManager(plugin);
        }
        return instance;
    }

    public boolean loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.toml");

        if (!configFile.exists()) {
            plugin.saveResource("config.toml", false);
        }

        try (InputStream input = new FileInputStream(configFile)) {
            config = Toml.parse(input);
            if (config.hasErrors()) {
                config.errors().forEach(error -> logger.severe("TOML Error: " + error.toString()));
                return false;
            }
            cache.clear(); // Clear cache on reload
        } catch (IOException e) {
            logger.severe("Could not load config.toml: " + e.getMessage());
            return false;
        }
        return true;
    }

    public void saveConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.toml");

        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write(config.toString());
        } catch (IOException e) {
            logger.severe("Could not save config.toml: " + e.getMessage());
        }
    }

    public void reloadConfig() {
        loadConfig();
    }

    public static boolean hasKey(String path) {
        return getInstance(null).config.get(path) != null;
    }

    public static String getString(String path) {
        return (String) getInstance(null).cache.computeIfAbsent(path, p -> getInstance(null).config.getString(p));
    }

    public static int getInt(String path) {
        return getInstance(null).config.getLong(path).intValue();
    }

    public static double getDouble(String path) {
        return getInstance(null).config.getDouble(path);
    }
}
