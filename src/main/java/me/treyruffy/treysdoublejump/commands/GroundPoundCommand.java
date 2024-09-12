package me.treyruffy.treysdoublejump.commands;

import me.treyruffy.treysdoublejump.TreysDoubleJump;
import me.treyruffy.treysdoublejump.events.DoubleJump;
import me.treyruffy.treysdoublejump.util.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * Created by TreyRuffy on 08/12/2018.
 */

public class GroundPoundCommand extends Command {
    public GroundPoundCommand() {
        super("groundpound", "Toggle ground pound", "/groundpound", List.of("togglegroundpound"));
        setPermission("tdj.groundpoundcommand");
    }

    @Override
    public boolean execute(@NotNull final CommandSender sender, @NotNull final String commandLabel, final @NotNull String[] args) {
        if (!ConfigManager.getConfig().getBoolean("GroundPound.Enabled")) return true;

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ConfigManager.getConfigMessage("PlayersOnly"));
            return true;
        }

        if (!ConfigManager.getEnabledWorlds().contains(player.getWorld().getName())) {
            player.sendMessage(ConfigManager.getConfigMessage("NotInWorld"));
            return true;
        }

        final UUID uuid = player.getUniqueId();
        if (TreysDoubleJump.DISABLED_GROUND_POUNDING.contains(uuid)) {
            player.sendMessage(ConfigManager.getConfigMessage("GroundPoundToggledOn"));
            TreysDoubleJump.DISABLED_GROUND_POUNDING.remove(uuid);
            return true;
        }

        DoubleJump.GROUNDED.remove(uuid);
        player.sendMessage(ConfigManager.getConfigMessage("GroundPoundToggledOff"));
        TreysDoubleJump.DISABLED_GROUND_POUNDING.add(uuid);
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull final CommandSender sender, @NotNull final String alias, final @NotNull String[] args) throws IllegalArgumentException {
        return List.of();
    }
}
