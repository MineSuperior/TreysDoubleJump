package me.treyruffy.treysdoublejump.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.UUID;

public class PlayerUtil {
    public static Player getPlayer(String s) {
        UUID uuid;
        try {
            int length = s.length();
            if (length <= 16 || (length == 17 && s.startsWith(ConfigManager.getConfig().getString("BedrockPlayerPrefix", ".")))) return Bukkit.getPlayer(s);
            uuid = length == 32 ? fromTrimmed(s) : length == 36 ? UUID.fromString(s) : null;
        } catch (Exception e) {
            return null;
        }
        return uuid == null ? null : Bukkit.getPlayer(uuid);
    }

    public static UUID fromTrimmed(String trimmedUUID) throws IllegalArgumentException {
        if (trimmedUUID == null) throw new IllegalArgumentException();
        StringBuilder builder = new StringBuilder(trimmedUUID.trim());
        try {
            builder.insert(20, "-");
            builder.insert(16, "-");
            builder.insert(12, "-");
            builder.insert(8, "-");
        } catch (StringIndexOutOfBoundsException e){
            throw new IllegalArgumentException();
        }

        return UUID.fromString(builder.toString());
    }
}
