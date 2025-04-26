package me.treyruffy.treysdoublejump.util;

import me.treyruffy.treysdoublejump.TreysDoubleJump;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Created by TreyRuffy on 08/12/2018.
 */

public class ConfigManager {
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
        return TreysDoubleJump.getInstance().getConfig();
    }

    // Saves the config
    public static void saveConfig() {
        TreysDoubleJump.getInstance().saveConfig();
    }

    // Reloads the config
    public static void reloadConfig() {
        TreysDoubleJump.getInstance().reloadConfig();
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

    public static String bukkitToMinecraft(@Nullable String s, String def) {
        if (s == null) return def;

        final String lowerCase = s.toLowerCase(Locale.ROOT);
        return (!lowerCase.startsWith("minecraft:")
            ? "minecraft:" + lowerCase
            : lowerCase
        ).replace("_", "."); // Bukkit sounds were just the same as Mojang's, but with underscores.
    }
}
