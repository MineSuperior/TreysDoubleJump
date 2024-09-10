package me.treyruffy.treysdoublejump.commands;

import me.treyruffy.treysdoublejump.TreysDoubleJump;
import me.treyruffy.treysdoublejump.util.ConfigManager;
import me.treyruffy.treysdoublejump.util.UpdateManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.logging.Level;

public class ReloadCommand extends Command {
    public ReloadCommand() {
        super("tdjreload", "Reload config", "/tdjreload", List.of("djreload", "doublejumpreload"));
        setPermission("tdj.reload");
    }

    @Override
    public boolean execute(@NotNull final CommandSender sender, @NotNull final String commandLabel, final @NotNull String[] args) {
        sender.sendMessage(
            Component.text("-=====[", NamedTextColor.BLUE)
                .append(Component.text("TDJ", NamedTextColor.AQUA, TextDecoration.BOLD))
                .append(Component.text("]=====-", NamedTextColor.BLUE))
        );
        sender.sendMessage(Component.text("Reloading the double jump YAML files...", NamedTextColor.GREEN));
        try {
            ConfigManager.reloadConfig();
            new UpdateManager().setup();
            sender.sendMessage(Component.text("Reloaded the double jump YAML files successfully!", NamedTextColor.GREEN));
        } catch (Exception e) {
            TreysDoubleJump.getInstance().getLogger().log(Level.WARNING, "Error occurred whilst reloading config", e);
            sender.sendMessage(Component.text("Could not reload the double jump YAML files.", NamedTextColor.RED));
        }
        sender.sendMessage(Component.text("-======================-", NamedTextColor.BLUE));
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull final CommandSender sender, @NotNull final String alias, final @NotNull String[] args) throws IllegalArgumentException {
        return List.of();
    }
}
