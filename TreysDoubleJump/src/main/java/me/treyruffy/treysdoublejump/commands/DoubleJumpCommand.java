package me.treyruffy.treysdoublejump.commands;

import me.treyruffy.treysdoublejump.TreysDoubleJump;
import me.treyruffy.treysdoublejump.util.ConfigManager;
import me.treyruffy.treysdoublejump.util.PlayerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import org.bukkit.GameMode;
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

public class DoubleJumpCommand extends Command {
    private static final List<String> OPTIONS = List.of("enable", "disable");

    public DoubleJumpCommand() {
        super("dj", "Toggle double jump", "/dj <enable/disable>", List.of("tdj", "doublejump", "treysdoublejump"));
        setPermission("tdj.command");
    }

    @Override
    public boolean execute(@NotNull final CommandSender sender, @NotNull final String commandLabel, final @NotNull String[] args) {
        if (args.length >= 1) {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "enable", "enabled", "on" -> {
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(ConfigManager.getConfigMessage("PlayersOnly"));
                        return true;
                    }

                    if (checkWorldAndPerm(player))
                        return true;

                    if (addEnabledPlayer(player))
                        player.sendMessage(ConfigManager.getConfigMessage("ToggledOn"));

                    return true;
                }

                case "disable", "disabled", "off" -> {
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(ConfigManager.getConfigMessage("PlayersOnly"));
                        return true;
                    }

                    if (addDisabledPlayer(player))
                        player.sendMessage(ConfigManager.getConfigMessage("ToggledOff"));

                    return true;
                }

                case "toggle" -> {
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(ConfigManager.getConfigMessage("PlayersOnly"));
                        return true;
                    }

                    if (TreysDoubleJump.DISABLED.contains(player.getUniqueId()) || TreysDoubleJump.FLYING.contains(player.getUniqueId())) {
                        if (checkWorldAndPerm(player))
                            return true;

                        if (addEnabledPlayer(player))
                            player.sendMessage(ConfigManager.getConfigMessage("ToggledOn"));

                        return true;
                    }

                    if (addDisabledPlayer(player))
                        player.sendMessage(ConfigManager.getConfigMessage("ToggledOff"));

                    return true;
                }

                default -> {
                    if (!sender.hasPermission("tdj.toggleothers")) {
                        sender.sendMessage(ConfigManager.getConfigMessage("InvalidArgument"));
                        return true;
                    }

                    final Player player = PlayerUtil.getPlayer(args[0]);
                    if (player == null || !player.isOnline()) {
                        sender.sendMessage(ConfigManager.getConfigMessage("PlayerNotFound").replaceText(b -> b.matchLiteral("[user]").replacement(args[0])));
                        return true;
                    }

                    if (args.length >= 2) {
                        switch (args[1].toLowerCase(Locale.ROOT)) {
                            case "enable", "enabled", "on" -> {
                                if (!addEnabledPlayer(player)) return true;

                                sender.sendMessage(ConfigManager.getConfigMessage("ToggledOnOther").replaceText(b -> b.matchLiteral("[user]").replacement(player.getName())));
                                final Component toggledOn = ConfigManager.getConfigMessage("DoubleJumpToggledOn");
                                if (Component.IS_NOT_EMPTY.test(toggledOn))
                                    player.sendMessage(toggledOn);

                                return true;
                            }
                            case "disable", "disabled", "off" -> {
                                turnDJOff(sender, player);
                                return true;
                            }
                            case "toggle", "toggled" -> {
                                if (TreysDoubleJump.DISABLED.contains(player.getUniqueId()) || TreysDoubleJump.FLYING.contains(player.getUniqueId())) {
                                    if (!addEnabledPlayer(player)) return true;

                                    sender.sendMessage(ConfigManager.getConfigMessage("ToggledOnOther").replaceText(b -> b.matchLiteral("[user]").replacement(player.getName())));
                                    final Component toggledOn = ConfigManager.getConfigMessage("DoubleJumpToggledOn");
                                    if (Component.IS_NOT_EMPTY.test(toggledOn))
                                        player.sendMessage(toggledOn);

                                    return true;
                                }

                                turnDJOff(sender, player);
                                return true;
                            }
                            default -> {
                                sender.sendMessage(ConfigManager.getConfigMessage("InvalidArgumentWithOther"));
                                return true;
                            }
                        }
                    }

                    if (TreysDoubleJump.DISABLED.contains(player.getUniqueId()) || TreysDoubleJump.FLYING.contains(player.getUniqueId())) {
                        if (!addEnabledPlayer(player)) return true;

                        sender.sendMessage(ConfigManager.getConfigMessage("ToggledOnOther").replaceText(b -> b.matchLiteral("[user]").replacement(player.getName())));
                        final Component toggledOn = ConfigManager.getConfigMessage("DoubleJumpToggledOn");
                        if (Component.IS_NOT_EMPTY.test(toggledOn))
                            player.sendMessage(toggledOn);

                        return true;
                    }

                    turnDJOff(sender, player);
                    return true;
                }
            }
        }

        sender.sendMessage(sender instanceof Player ?
            ConfigManager.getConfigMessage("InvalidArgument") :
            ConfigManager.getConfigMessage("InvalidArgumentConsole"));
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull final CommandSender sender, @NotNull final String alias, final @NotNull String[] args) throws IllegalArgumentException {
        if (sender.hasPermission("tdj.toggleothers")) {
            if (args.length == 1) {
                final List<String> list = new ArrayList<>(OPTIONS);
                list.addAll(super.tabComplete(sender, alias, args));
                return list;
            }

            return args.length == 2 ? OPTIONS : List.of();
        }

        return args.length <= 1 ? OPTIONS : List.of();
    }

    private void turnDJOff(@NotNull CommandSender sender, Player username) {
        if (!addDisabledPlayer(username)) return;

        sender.sendMessage(ConfigManager.getConfigMessage("ToggledOffOther").replaceText(b -> b.matchLiteral("[user]").replacement(username.getName())));
        final Component toggledOff = ConfigManager.getConfigMessage("DoubleJumpToggledOff");
        if (!Component.IS_NOT_EMPTY.test(toggledOff))
            username.sendMessage(toggledOff);
    }

    private boolean addDisabledPlayer(Player player) {
        TreysDoubleJump.DISABLED.add(player.getUniqueId());
        if (player.getGameMode() == GameMode.CREATIVE
                || player.getGameMode() == GameMode.SPECTATOR
                || !ConfigManager.getConfig().getStringList("EnabledWorlds").contains((player).getWorld().getName())) {
            return true;
        }

        if (TreysDoubleJump.FLYING.contains(player.getUniqueId())) return true;

        player.setAllowFlight(false);
        if (!ConfigManager.getConfig().getBoolean("NoFall.Enabled"))
            player.setFlyingFallDamage(TriState.FALSE);
        player.setFlying(false);

        return true;
    }

    private boolean addEnabledPlayer(Player player) {
        TreysDoubleJump.FLYING.remove(player.getUniqueId());
        if (!TreysDoubleJump.DISABLED.contains(player.getUniqueId())) {
            return true;
        }

        TreysDoubleJump.DISABLED.remove(player.getUniqueId());
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
        if (!ConfigManager.getConfig().getStringList("EnabledWorlds").contains((player).getWorld().getName())) {
            player.sendMessage(ConfigManager.getConfigMessage("NotInWorld"));
            return true;
        }

        return false;
    }
}
