package me.treyruffy.treysdoublejump.commands;

import me.treyruffy.treysdoublejump.events.DoubleJump;
import me.treyruffy.treysdoublejump.util.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by TreyRuffy on 08/12/2018.
 */

public class GroundPoundCommand implements CommandExecutor {

    // Players in this list cannot ground pound
    public static final Set<UUID> GROUND_POUND_DISABLED = new HashSet<>();

    // Sets all the /groundpound commands
    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("groundpound")) {
            if (ConfigManager.getConfig().getBoolean("GroundPound.Enabled")) {
                if (sender instanceof Player p) {
                    if (p.hasPermission("tdj.groundpoundcommand")) {
                        if (!ConfigManager.getConfig().getStringList("EnabledWorlds").contains((p).getWorld().getName())) {
                            p.sendMessage(ConfigManager.getConfigMessage("NotInWorld"));
                            return true;
                        }
                        if (GROUND_POUND_DISABLED.contains(p.getUniqueId())) {
                            p.sendMessage(ConfigManager.getConfigMessage("GroundPoundToggledOn"));
                            GROUND_POUND_DISABLED.remove(p.getUniqueId());
                        } else {
                            DoubleJump.GROUNDED.remove(p.getUniqueId());
                            p.sendMessage(ConfigManager.getConfigMessage("GroundPoundToggledOff"));
                            GROUND_POUND_DISABLED.add(p.getUniqueId());
                        }
                    } else {
                        p.sendMessage(ConfigManager.getConfigMessage("NoPermission"));
                    }
                    return true;
                }
                sender.sendMessage(ConfigManager.getConfigMessage("PlayersOnly"));
                return true;
            }
        }
        return true;
    }
}
