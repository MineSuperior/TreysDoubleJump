package me.treyruffy.treysdoublejump.util;

import me.treyruffy.treysdoublejump.TreysDoubleJump;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by TreyRuffy on 08/12/2018.
 */

public class ConfigManager {

    // Accesses the main class
    private static final TreysDoubleJump plugin = TreysDoubleJump.getInstance();

    // Accesses the configuration
    public static FileConfiguration MainConfig;

    // Accesses the configuration file
    public static File MainConfigFile;

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
        MainConfigFile = new File(plugin.getDataFolder(), "config.yml");
        if (!MainConfigFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        MainConfig = YamlConfiguration.loadConfiguration(MainConfigFile);
        InputStream configData = plugin.getResource("config.yml");
        if (configData != null) {
            MainConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(configData)));
        }
    }

    public static Component getConfigMessage(String message) {
        String oldConfigMessage = getOldConfigMessage(message);
        return MiniMessage.miniMessage().deserialize(oldConfigMessage);
    }

    private static String getOldConfigMessage(String message) {
        String messageFromConfig = getConfig().getString("Messages." + message);
        if (messageFromConfig == null) {
            return ChatColor.RED + "Messages. " + message + " is not set in the config.";
        }
        return ChatColor.translateAlternateColorCodes('&', translateHexCodes(messageFromConfig));
    }

    static final Pattern hexPattern = Pattern.compile("#([A-Fa-f0-9]{6})");
    static final char COLOR_CHAR = ChatColor.COLOR_CHAR;

    // Thank you https://www.spigotmc.org/threads/hex-color-code-translate.449748/#post-3867804
    public static String translateHexCodes(String text) {
        Matcher matcher = hexPattern.matcher(text);
        StringBuilder buffer = new StringBuilder(text.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        return matcher.appendTail(buffer).toString();
    }
}
