package me.treyruffy.treysdoublejump.util;

import me.treyruffy.treysdoublejump.TreysDoubleJump;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;

/**
 * Created by TreyRuffy on 08/12/2018.
 */

public class ConfigManager {
    // Accesses the configuration
    public static FileConfiguration MainConfig;

    // Accesses the configuration file
    public static File MainConfigFile;

    private static final Set<String> enabledWorlds = new HashSet<>();
    private static final Set<Material> disabledBlocks = new HashSet<>();

    public static Set<String> getEnabledWorlds() {
        return enabledWorlds;
    }

    public static Set<Material> getDisabledBlocks() {
        return disabledBlocks;
    }

    // Gets the config
    public static FileConfiguration getConfig() {
        if (MainConfig == null) {
            reloadConfig();
        }
        return MainConfig;
    }

    // Saves the config
    public static void saveConfig() {
        if (MainConfig == null) {
            throw new NullPointerException("Cannot save a non-existent file!");
        }
        try {
            MainConfig.save(MainConfigFile);
        } catch (IOException e) {
            TreysDoubleJump.getInstance().getLogger().log(Level.SEVERE, "Could not save " + MainConfigFile + ".", e);
        }
    }

    // Reloads the config
    public static void reloadConfig() {
        MainConfigFile = new File(TreysDoubleJump.getInstance().getDataFolder(), "config.yml");
        if (!MainConfigFile.exists()) {
            TreysDoubleJump.getInstance().saveResource("config.yml", false);
        }
        MainConfig = YamlConfiguration.loadConfiguration(MainConfigFile);
        InputStream configData = TreysDoubleJump.getInstance().getResource("config.yml");
        if (configData != null) {
            MainConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(configData)));
        }
        enabledWorlds.clear();
        enabledWorlds.addAll(getConfig().getStringList("EnabledWorlds"));
        disabledBlocks.clear();
        disabledBlocks.addAll(getConfig().getStringList("DisabledBlocks").stream().map(Material::matchMaterial).filter(Objects::nonNull).toList());
    }

    public static Component getConfigMessage(String message) {
        String oldConfigMessage = getConfig().getString("Messages." + message);
        if (oldConfigMessage == null) {
            return Component.text("Messages." + message + " is not set in the config.", NamedTextColor.RED);
        }
        return MiniMessage.miniMessage().deserialize(oldConfigMessage);
    }
}
