package me.treyruffy.treysdoublejump.commands;

import me.treyruffy.treysdoublejump.TreysDoubleJump;
import me.treyruffy.treysdoublejump.util.ConfigManager;
import me.treyruffy.treysdoublejump.util.UpdateManager;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Created by TreyRuffy on 08/12/2018.
 */

public class DoubleJumpCommand implements CommandExecutor {

    // Players in this list cannot use double jump
    public static final Set<UUID> DISABLE_PLAYERS = new HashSet<>();

    // All the commands for /tdj
    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("tdj")) {
            if (args.length >= 1) {
                // /tdj enable
                if (args[0].equalsIgnoreCase("enable")) {
                    if (sender instanceof Player p) {
                        if (checkWorldAndPerm(p))
                            return true;
                        if (addEnabledPlayer(p)) {
                            p.sendMessage(ConfigManager.getConfigMessage("ToggledOn"));
                        }
                    } else {
                        sender.sendMessage(ConfigManager.getConfigMessage("PlayersOnly"));
                        return true;
                    }
                }
                // /tdj disable
                else if (args[0].equalsIgnoreCase("disable")) {
                    if (sender instanceof Player p) {
                        if (checkWorldAndPerm(p))
                            return true;
                        if (addDisabledPlayer(p)) {
                            p.sendMessage(ConfigManager.getConfigMessage("ToggledOff"));
                        }
                    } else {
                        sender.sendMessage(ConfigManager.getConfigMessage("PlayersOnly"));
                        return true;
                    }
                }
                // /tdj <username>
                else if (Bukkit.getPlayer(args[0]) != null) {

                    if (!sender.hasPermission("tdj.toggleothers")) {
                        sender.sendMessage(ConfigManager.getConfigMessage(
                                "NoPermission"));
                        return true;
                    }

                    Player username = Bukkit.getPlayer(args[0]);
                    assert username != null;

                    // Player is not online
                    if (!username.isOnline()) {
                        sender.sendMessage(ConfigManager.getConfigMessage("PlayerNotFound").replaceText(b -> b.matchLiteral("[user]").replacement(args[0])));
                        return true;
                    } else if (args.length >= 2) {
                        // /tdj <username> enable
                        if (args[1].equalsIgnoreCase("enable")) {
                            if (addEnabledPlayer(username)) {
                                sender.sendMessage(ConfigManager.getConfigMessage("ToggledOnOther").replaceText(b -> b.matchLiteral("[user]").replacement(username.getName())));
                                if (!LegacyComponentSerializer.legacy(ChatColor.COLOR_CHAR).serialize(ConfigManager.getConfigMessage("DoubleJumpToggledOn")).equalsIgnoreCase(""))
                                    username.sendMessage(ConfigManager.getConfigMessage("DoubleJumpToggledOn"));
                            }
                        }
                        // /tdj <username> disable
                        else if (args[1].equalsIgnoreCase("disable")) {
                            turnDJOff(sender, username);
                        }
                        // invalid argument after /tdj <username>
                        else {
                            sender.sendMessage(ConfigManager.getConfigMessage("InvalidArgumentWithOther"));
                            return true;
                        }
                    }
                    // /tdj <username>
                    else {
                        if (DISABLE_PLAYERS.contains(username.getUniqueId()) || FlightCommand.FLYING_PLAYERS.contains(username.getUniqueId())) {
                            if (addEnabledPlayer(username)) {
                                sender.sendMessage(ConfigManager.getConfigMessage("ToggledOnOther").replaceText(b -> b.matchLiteral("[user]").replacement(username.getName())));
                                if (!LegacyComponentSerializer.legacy(ChatColor.COLOR_CHAR).serialize(ConfigManager.getConfigMessage("DoubleJumpToggledOff")).equalsIgnoreCase(""))
                                    username.sendMessage(ConfigManager.getConfigMessage("DoubleJumpToggledOn"));
                            }
                        } else {
                            turnDJOff(sender, username);
                        }
                    }
                }
                // invalid argument
                else {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(ConfigManager.getConfigMessage("PlayerNotFound").replaceText(b -> b.matchLiteral("[user]").replacement(args[0])));
                        return true;
                    }
                    if (!sender.hasPermission("tdj.toggleothers")) {
                        sender.sendMessage(ConfigManager.getConfigMessage("InvalidArgument"));
                        return true;
                    }
                    sender.sendMessage(ConfigManager.getConfigMessage("PlayerNotFound").replaceText(b -> b.matchLiteral("[user]").replacement(args[0])));
                    return true;
                }
                return true;
            } else if (sender instanceof Player p) {
                if (checkWorldAndPerm(p))
                    return true;
                if (DISABLE_PLAYERS.contains(p.getUniqueId()) || FlightCommand.FLYING_PLAYERS.contains(p.getUniqueId())) {
                    if (addEnabledPlayer(p))
                        p.sendMessage(ConfigManager.getConfigMessage("ToggledOn"));
                } else {
                    if (addDisabledPlayer(p)) {
                        p.sendMessage(ConfigManager.getConfigMessage("ToggledOff"));
                    }
                }
                return true;
            }
            sender.sendMessage(ConfigManager.getConfigMessage("InvalidArgumentConsole"));
            return true;
        } else if (cmd.getName().equalsIgnoreCase("djreload")) {
            if (sender.hasPermission("tdj.reload")) {
                sender.sendMessage(ChatColor.BLUE + "-=====[" + ChatColor.AQUA + ChatColor.BOLD + " TDJ " + ChatColor.BLUE + "]=====-");
                sender.sendMessage(ChatColor.GREEN + "Reloading the double jump YAML files...");
                try {
                    ConfigManager.reloadConfig();
                    new UpdateManager().setup();
                    sender.sendMessage(ChatColor.GREEN + "Reloaded the double jump YAML files successfully!");
                    sender.sendMessage(ChatColor.BLUE + "-======================-");
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "Could not reload the double jump YAML files.");
                    sender.sendMessage(ChatColor.BLUE + "-======================-");
                    TreysDoubleJump.getInstance().getLogger().log(Level.WARNING, "Error occurred whilst reloading config", e);
                }
            } else {
                sender.sendMessage(ConfigManager.getConfigMessage("NoPermission"));
            }
            return true;
        }
        return true;
    }

    private void turnDJOff(@NotNull CommandSender sender, Player username) {
        if (addDisabledPlayer(username)) {
            sender.sendMessage(ConfigManager.getConfigMessage("ToggledOffOther").replaceText(b -> b.matchLiteral("[user]").replacement(username.getName())));
            if (!LegacyComponentSerializer.legacySection().serialize(ConfigManager.getConfigMessage("DoubleJumpToggledOff")).equalsIgnoreCase(""))
                username.sendMessage(ConfigManager.getConfigMessage("DoubleJumpToggledOff"));
        }
    }

    private boolean addDisabledPlayer(Player player) {
        DISABLE_PLAYERS.add(player.getUniqueId());
        if (player.getGameMode() == GameMode.CREATIVE
                || player.getGameMode() == GameMode.SPECTATOR
                || !ConfigManager.getConfig().getStringList("EnabledWorlds").contains((player).getWorld().getName())) {
            return true;
        }
        if (!FlightCommand.FLYING_PLAYERS.contains(player.getUniqueId())) {
            player.setAllowFlight(false);
            try {
                if (!ConfigManager.getConfig().getBoolean("NoFall.Enabled"))
                    player.setFlyingFallDamage(TriState.FALSE);
            } catch (NoSuchMethodError ignored) {}
            player.setFlying(false);
        }
        return true;
    }

    private boolean addEnabledPlayer(Player player) {
        FlightCommand.FLYING_PLAYERS.remove(player.getUniqueId());
        if (!DISABLE_PLAYERS.contains(player.getUniqueId())) {
            return true;
        }
        DISABLE_PLAYERS.remove(player.getUniqueId());
        if (player.getGameMode() == GameMode.CREATIVE
                || player.getGameMode() == GameMode.SPECTATOR
                || !ConfigManager.getConfig().getStringList("EnabledWorlds").contains((player).getWorld().getName())) {
            return true;
        }
        player.setAllowFlight(false);
        player.setFlying(false);
        return true;
    }

    // Returns false if the player is not in the correct world or doesn't have permissions
    private boolean checkWorldAndPerm(Player player) {
        if (!player.hasPermission("tdj.command")) {
            player.sendMessage(ConfigManager.getConfigMessage("NoPermission"));
            return true;
        }
        if (!ConfigManager.getConfig().getStringList("EnabledWorlds").contains((player).getWorld().getName())) {
            player.sendMessage(ConfigManager.getConfigMessage("NotInWorld"));
            return true;
        }
        return false;
    }

}
