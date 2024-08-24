package me.treyruffy.treysdoublejump.events;

import me.treyruffy.treysdoublejump.util.ConfigManager;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TreyRuffy on 08/12/2018.
 */

public class FlightCommand implements CommandExecutor {

    // Players in this list can fly
    public static final List<String> FlyingPlayers = new ArrayList<>();

    // Sets all commands for /fly
    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("fly")) {
            return true;
        }
        if (!ConfigManager.getConfig().getBoolean("Flight.Enabled")) {
            sender.sendMessage(ConfigManager.getConfigMessage("FlyCommandDisabled"));
            return true;
        }

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("enable")) {
                if (sender instanceof Player p) {
                    if (p.hasPermission("tdj.fly")) {
                        p.sendMessage(ConfigManager.getConfigMessage("FlyToggledOn"));
                        addEnabledFlightPlayer(p);
                    } else {
                        p.sendMessage(ConfigManager.getConfigMessage("NoPermission"));
                    }
                }
            } else if (args[0].equalsIgnoreCase("disable")) {
                Player p = (Player) sender;
                if (p.hasPermission("tdj.fly")) {
                    p.sendMessage(ConfigManager.getConfigMessage("FlyToggledOff"));
                    addDisabledFlightPlayer(p);
                } else {
                    p.sendMessage(ConfigManager.getConfigMessage("NoPermission"));
                }
            } else if (Bukkit.getPlayer(args[0]) != null) {
                if (!sender.hasPermission("tdj.fly.toggleothers")) {
                    sender.sendMessage(ConfigManager.getConfigMessage("NoPermission"));
                    return true;
                }

                Player username = Bukkit.getPlayer(args[0]);
                assert username != null;

                if (args.length > 1) {
                    if (args[1].equalsIgnoreCase("enable")) {
                        turnFlyOn(sender, username);
                    } else if (args[1].equalsIgnoreCase("disable")) {
                        sender.sendMessage(ConfigManager.getConfigMessage("FlyToggledOffOther").replaceText(b -> b.matchLiteral("[user]").replacement(username.getName())));
                        addDisabledFlightPlayer(username);
                        if (!LegacyComponentSerializer.legacySection().serialize(ConfigManager.getConfigMessage("FlightToggledOff")).equalsIgnoreCase(""))
                            username.sendMessage(ConfigManager.getConfigMessage("FlightToggledOff"));
                    } else {
                        if (sender instanceof Player) {
                            sender.sendMessage(ConfigManager.getConfigMessage("InvalidFlyArgumentWithOther"));
                        } else {
                            sender.sendMessage(ConfigManager.getConfigMessage("InvalidFlyArgumentConsole"));
                        }
                    }
                    return true;
                }

                if (FlyingPlayers.contains(username.getUniqueId().toString())) {
                    sender.sendMessage(ConfigManager.getConfigMessage("FlyToggledOffOther").replaceText(b -> b.matchLiteral("[user]").replacement(username.getName())));
                    addDisabledFlightPlayer(username);

                    if (!LegacyComponentSerializer.legacySection().serialize(ConfigManager.getConfigMessage("FlightToggledOff")).equalsIgnoreCase("")) {
                        System.out.println(LegacyComponentSerializer.legacySection().serialize(ConfigManager.getConfigMessage("FlightToggledOff")));
                        username.sendMessage(ConfigManager.getConfigMessage("FlightToggledOff"));
                    }
                } else {
                    turnFlyOn(sender, username);
                }
            } else {
                if (sender.hasPermission("tdj.fly.toggleothers")) {
                    sender.sendMessage(ConfigManager.getConfigMessage("PlayerNotFound").replaceText(b -> b.matchLiteral("[user]").replacement(args[0])));
                } else if (sender.hasPermission("tdj.fly")) {
                    sender.sendMessage(ConfigManager.getConfigMessage("InvalidFlyArgument"));
                } else {
                    sender.sendMessage(ConfigManager.getConfigMessage("NoPermission"));
                }
                return true;
            }

        }

        // /fly
        else {
            if (sender instanceof Player p) {
                if (p.hasPermission("tdj.fly")) {
                    if (!checkIfInWorld(p)) {
                        p.sendMessage(ConfigManager.getConfigMessage("NotInWorld"));
                        return true;
                    }
                    if (FlyingPlayers.contains(p.getUniqueId().toString())) {
                        p.sendMessage(ConfigManager.getConfigMessage("FlyToggledOff"));
                        addDisabledFlightPlayer(p);
                    } else {
                        p.sendMessage(ConfigManager.getConfigMessage("FlyToggledOn"));
                        addEnabledFlightPlayer(p);
                    }
                } else {
                    p.sendMessage(ConfigManager.getConfigMessage("NoPermission"));
                }
                return true;
            }
            sender.sendMessage(ConfigManager.getConfigMessage("PlayersOnly"));
        }
        return true;
    }

    private void turnFlyOn(@NotNull CommandSender sender, Player username) {
        sender.sendMessage(ConfigManager.getConfigMessage("FlyToggledOnOther").replaceText(b -> b.matchLiteral("[user]").replacement(username.getName())));
        addEnabledFlightPlayer(username);
        if (!LegacyComponentSerializer.legacySection().serialize(ConfigManager.getConfigMessage("FlightToggledOn")).equalsIgnoreCase(""))
            username.sendMessage(ConfigManager.getConfigMessage("FlightToggledOn"));
    }

    private boolean checkIfInWorld(Player player) {
        return ConfigManager.getConfig().getStringList("EnabledWorlds").contains((player).getWorld().getName());
    }

    private void addDisabledFlightPlayer(Player player) {
        player.setFallDistance(0f);
        player.setAllowFlight(false);
        player.setFlying(false);
        try {
            if (!ConfigManager.getConfig().getBoolean("NoFall.Enabled"))
                player.setFlyingFallDamage(TriState.FALSE);
        } catch (NoSuchMethodError ignored) {}
        FlyingPlayers.remove(player.getUniqueId().toString());
    }

    private void addEnabledFlightPlayer(Player player) {
        player.setFallDistance(0f);
        DoubleJump.Grounded.remove(player.getUniqueId().toString());
        player.setAllowFlight(true);
        try {
            if (!ConfigManager.getConfig().getBoolean("NoFall.Enabled"))
                player.setFlyingFallDamage(TriState.FALSE);
        } catch (NoSuchMethodError ignored) {}
        player.setFlying(true);
        FlyingPlayers.add(player.getUniqueId().toString());
    }

}
