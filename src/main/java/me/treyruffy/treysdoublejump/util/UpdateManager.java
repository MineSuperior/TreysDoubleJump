package me.treyruffy.treysdoublejump.util;

import me.treyruffy.treysdoublejump.TreysDoubleJump;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Created by TreyRuffy on 08/12/2018.
 */

public class UpdateManager {
    private static final LegacyComponentSerializer HEX_AMPERSAND = LegacyComponentSerializer.builder()
        .character(LegacyComponentSerializer.AMPERSAND_CHAR)
        .hexCharacter(LegacyComponentSerializer.HEX_CHAR)
        .useUnusualXRepeatedCharacterHexFormat().build();

    // Updates the config
    public void setup() {
        final String pluginVersion = TreysDoubleJump.getInstance().getDescription().getVersion();
        final String version = ConfigManager.getConfig().getString("Version");
        if (version == null) {
            ConfigManager.getConfig().set("Version", pluginVersion);
            ConfigManager.saveConfig();
            return;
        }

        final int lastVersion = parseVersion(version);
        if (lastVersion == parseVersion(pluginVersion))
            return;

        try {
            File source = new File(TreysDoubleJump.getInstance().getDataFolder() + File.separator + "config.yml");
            File dest = new File(TreysDoubleJump.getInstance().getDataFolder() + File.separator + "config.yml.old");
            if (!dest.exists())
                if (!dest.createNewFile()) {
                    return;
                }
            FileUtils.copyFile(source, dest);
        } catch (IOException e) {
            TreysDoubleJump.getInstance().getLogger().log(Level.WARNING, "Error when updating config", e);
        }

        if (lastVersion < 263) {
            ConfigManager.getConfig().set("Messages.PlayerNotFound", "&c[user] is not online.");
            ConfigManager.getConfig().set("Messages.ToggledOnOther", "&6You have &a&lenabled &6double jump for " +
                    "[user]!");
            ConfigManager.getConfig().set("Messages.ToggledOffOther", "&6You have &c&ldisabled &6double jump for " +
                    "[user]!");
            ConfigManager.getConfig().set("Messages.InvalidArgument", "&cInvalid argument: Please do /tdj " +
                    "[enable/disable]");
            ConfigManager.getConfig().set("Messages.InvalidArgumentConsole", "&cInvalid argument: Please do /tdj " +
                    "<username> [enable/disable]");
            ConfigManager.getConfig().set("Messages.InvalidArgumentWithOther", "&cInvalid argument: Please do " +
                    "/tdj [username] [enable/disable]");
            ConfigManager.getConfig().set("Messages.InvalidFlyArgument", "&cInvalid argument: Please do /fly " +
                    "[enable/disable]");
            ConfigManager.getConfig().set("Messages.InvalidFlyArgumentConsole", "&cInvalid argument: Please do /fly " +
                    "<username> [enable/disable]");
            ConfigManager.getConfig().set("Messages.InvalidFlyArgumentWithOther", "&cInvalid argument: Please do " +
                    "/fly [username] [enable/disable]");
            ConfigManager.getConfig().set("Messages.FlyToggledOnOther", "&6You have &a&lenabled &6flight for [user]!");
            ConfigManager.getConfig().set("Messages.FlyToggledOnOther", "&6You have &c&ldisabled &6flight for [user]!");
            ConfigManager.getConfig().set("Messages.FlightToggledOn", "&6Your flight has been &a&lenabled&6!");
            ConfigManager.getConfig().set("Messages.FlightToggledOn", "&6Your flight has been &c&ldisabled&6!");
            ConfigManager.getConfig().set("Messages.DoubleJumpToggledOn", "&6Your double jump has been &a&lenabled&6!");
            ConfigManager.getConfig().set("Messages.DoubleJumpToggledOff", "&6Your double jump has been " +
                    "&c&ldisabled&6!");
        }

        if (lastVersion < 272) {
            ConfigManager.getConfig().set("Sounds.Type", ConfigManager.bukkitToMinecraft(ConfigManager.getConfig()
                .getString("Sounds.Type"), "minecraft:entity.bat.takeoff"));
            ConfigManager.getConfig().set("Particles.Type", ConfigManager.bukkitToMinecraft(ConfigManager.getConfig()
                .getString("Particles.Type"), "minecraft:poof"));

            final ConfigurationSection messages = ConfigManager.getConfig().getConfigurationSection("Messages");
            assert messages != null;
            for (final String subKey : messages.getKeys(false)) {
                final String key = "Messages." + subKey;
                final String message = ConfigManager.getConfig().getString(key);
                if (message == null) continue; // I'd be surprised if this happened.

                ConfigManager.getConfig().set(key, MiniMessage.miniMessage().serialize(HEX_AMPERSAND.deserialize(message.replace("§", "&")))
                    .replace("\\<", "<"));
            }
        }

        ConfigManager.getConfig().set("Version", pluginVersion);
        ConfigManager.saveConfig();
    }

    private static int parseVersion(String s) {
        return Integer.parseInt(s.replace(".", "").replace("-SNAPSHOT", ""));
    }
}
