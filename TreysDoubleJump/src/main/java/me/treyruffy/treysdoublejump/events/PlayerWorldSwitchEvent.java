package me.treyruffy.treysdoublejump.events;

import me.treyruffy.treysdoublejump.commands.DoubleJumpCommand;
import me.treyruffy.treysdoublejump.commands.FlightCommand;
import me.treyruffy.treysdoublejump.util.ConfigManager;
import net.kyori.adventure.util.TriState;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

/**
 * Created by TreyRuffy on 01/03/2021.
 */

public class PlayerWorldSwitchEvent implements Listener {

    @EventHandler
    public void switchWorldEvent(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPermission("tdj.use")
                || p.getGameMode() == GameMode.SPECTATOR
                || p.getGameMode() == GameMode.CREATIVE
                || DoubleJumpCommand.DISABLE_PLAYERS.contains(p.getUniqueId())) {
            return;
        }
        if (!ConfigManager.getConfig().getStringList("EnabledWorlds").contains(p.getWorld().getName())) {
            if (FlightCommand.FLYING_PLAYERS.contains(p.getUniqueId())) {
                p.setFallDistance(0f);
                p.sendMessage(ConfigManager.getConfigMessage("FlyToggledOff"));
                FlightCommand.FLYING_PLAYERS.remove(p.getUniqueId());
            }
            p.setFlying(false);
            p.setAllowFlight(false);
            try {
                if (!ConfigManager.getConfig().getBoolean("NoFall.Enabled"))
                    p.setFlyingFallDamage(TriState.FALSE);
            } catch (NoSuchMethodError ignored) {
            }
        }
    }
}
