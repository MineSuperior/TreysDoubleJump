package me.treyruffy.treysdoublejump.commands;

import me.treyruffy.treysdoublejump.TreysDoubleJump;
import me.treyruffy.treysdoublejump.events.DoubleJump;
import me.treyruffy.treysdoublejump.util.ConfigManager;
import me.treyruffy.treysdoublejump.util.PlayerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by TreyRuffy on 08/12/2018.
 */

public class FlightCommand extends Command {
    private static final List<String> OPTIONS = List.of("enable", "disable");

    public FlightCommand() {
        super("fly", "Toggle flight", "/fly <enable/disable>", List.of("flight", "togglefly", "toggleflight"));
        setPermission("tdj.fly");
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull final CommandSender sender, @NotNull final String alias, final @NotNull String[] args) throws IllegalArgumentException {
        if (sender.hasPermission("tdj.fly.toggleothers")) {
            if (args.length == 1) {
                final List<String> list = new ArrayList<>(OPTIONS);
                list.addAll(super.tabComplete(sender, alias, args));
                return list;
            }

            return args.length == 2 ? OPTIONS : List.of();
        }

        return args.length <= 1 ? OPTIONS : List.of();
    }

    @Override
    public boolean execute(@NotNull final CommandSender sender, @NotNull final String commandLabel, final @NotNull String[] args) {
        if (!ConfigManager.getConfig().getBoolean("Flight.Enabled")) {
            sender.sendMessage(ConfigManager.getConfigMessage("FlyCommandDisabled"));
            return true;
        }

        if (args.length > 0) {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "enable", "enabled", "on" -> {
                    if (!(sender instanceof Player p)) {
                        sender.sendMessage(ConfigManager.getConfigMessage("PlayersOnly"));
                        return true;
                    }

                    if (!ConfigManager.getEnabledWorlds().contains(p.getWorld().getName())) {
                        p.sendMessage(ConfigManager.getConfigMessage("NotInWorld"));
                        return true;
                    }

                    p.sendMessage(ConfigManager.getConfigMessage("FlyToggledOn"));
                    addEnabledFlightPlayer(p);
                    return true;
                }
                case "disable", "disabled", "off" -> {
                    if (!(sender instanceof Player p)) {
                        sender.sendMessage(ConfigManager.getConfigMessage("PlayersOnly"));
                        return true;
                    }

                    p.sendMessage(ConfigManager.getConfigMessage("FlyToggledOff"));
                    addDisabledFlightPlayer(p);
                    return true;
                }
                case "toggle", "toggled" -> {
                    if (!(sender instanceof Player p)) {
                        sender.sendMessage(ConfigManager.getConfigMessage("PlayersOnly"));
                        return true;
                    }

                    if (TreysDoubleJump.FLYING.contains(p.getUniqueId())) {
                        p.sendMessage(ConfigManager.getConfigMessage("FlyToggledOff"));
                        addDisabledFlightPlayer(p);
                    } else {
                        if (!ConfigManager.getEnabledWorlds().contains(p.getWorld().getName())) {
                            p.sendMessage(ConfigManager.getConfigMessage("NotInWorld"));
                            return true;
                        }

                        p.sendMessage(ConfigManager.getConfigMessage("FlyToggledOn"));
                        addEnabledFlightPlayer(p);
                    }

                    return true;
                }

                default -> {
                    if (!sender.hasPermission("tdj.fly.toggleothers")) {
                        sender.sendMessage(ConfigManager.getConfigMessage("InvalidFlyArgument"));
                        return true;
                    }

                    Player player = PlayerUtil.getPlayer(args[0]);
                    if (player == null) {
                        sender.sendMessage(ConfigManager.getConfigMessage("PlayerNotFound").replaceText(b -> b.matchLiteral("[user]").replacement(args[0])));
                        return true;
                    }

                    if (args.length > 1) {
                        switch (args[1].toLowerCase(Locale.ROOT)) {
                            case "enable", "enabled", "on" -> turnFlyOn(sender, player);
                            case "disable", "disabled", "off" -> turnFlyOff(sender, player);
                            case "toggle", "toggled" -> {
                                if (TreysDoubleJump.FLYING.contains(player.getUniqueId())) {
                                    turnFlyOff(sender, player);
                                } else {
                                    turnFlyOn(sender, player);
                                }
                            }
                            default -> {
                                if (sender instanceof Player) {
                                    sender.sendMessage(ConfigManager.getConfigMessage("InvalidFlyArgumentWithOther"));
                                } else {
                                    sender.sendMessage(ConfigManager.getConfigMessage("InvalidFlyArgumentConsole"));
                                }
                            }
                        }
                        return true;
                    }

                    if (TreysDoubleJump.FLYING.contains(player.getUniqueId())) {
                        turnFlyOff(sender, player);
                    } else {
                        turnFlyOn(sender, player);
                    }
                    return true;
                }
            }
        }

        if (!(sender instanceof Player p)) {
            sender.sendMessage(ConfigManager.getConfigMessage("PlayersOnly"));
            return true;
        }

        if (TreysDoubleJump.FLYING.contains(p.getUniqueId())) {
            p.sendMessage(ConfigManager.getConfigMessage("FlyToggledOff"));
            addDisabledFlightPlayer(p);
        } else {
            if (!ConfigManager.getEnabledWorlds().contains(p.getWorld().getName())) {
                p.sendMessage(ConfigManager.getConfigMessage("NotInWorld"));
                return true;
            }

            p.sendMessage(ConfigManager.getConfigMessage("FlyToggledOn"));
            addEnabledFlightPlayer(p);
        }

        return true;
    }

    private void turnFlyOn(@NotNull CommandSender sender, Player username) {
        sender.sendMessage(ConfigManager.getConfigMessage("FlyToggledOnOther").replaceText(b -> b.matchLiteral("[user]").replacement(username.getName())));
        addEnabledFlightPlayer(username);
        if (Component.IS_NOT_EMPTY.test(ConfigManager.getConfigMessage("FlightToggledOn")))
            username.sendMessage(ConfigManager.getConfigMessage("FlightToggledOn"));
    }

    private void turnFlyOff(@NotNull CommandSender sender, Player username) {
        sender.sendMessage(ConfigManager.getConfigMessage("FlyToggledOffOther").replaceText(b -> b.matchLiteral("[user]").replacement(username.getName())));
        addDisabledFlightPlayer(username);
        final Component toggledOff = ConfigManager.getConfigMessage("FlightToggledOff");
        if (Component.IS_NOT_EMPTY.test(toggledOff))
            username.sendMessage(toggledOff);
    }

    private void addDisabledFlightPlayer(Player player) {
        player.setFallDistance(0f);
        player.setAllowFlight(false);
        player.setFlying(false);
        if (!ConfigManager.getConfig().getBoolean("NoFall.Enabled"))
            player.setFlyingFallDamage(TriState.FALSE);
        TreysDoubleJump.FLYING.remove(player.getUniqueId());
    }

    private void addEnabledFlightPlayer(Player player) {
        player.setFallDistance(0f);
        DoubleJump.GROUNDED.remove(player.getUniqueId());
        player.setAllowFlight(true);
        if (!ConfigManager.getConfig().getBoolean("NoFall.Enabled"))
            player.setFlyingFallDamage(TriState.FALSE);
        player.setFlying(true);
        TreysDoubleJump.FLYING.add(player.getUniqueId());
    }
}
