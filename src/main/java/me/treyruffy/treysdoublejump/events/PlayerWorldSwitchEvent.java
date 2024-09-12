package me.treyruffy.treysdoublejump.events;

import me.treyruffy.treysdoublejump.TreysDoubleJump;
import me.treyruffy.treysdoublejump.util.ConfigManager;
import net.kyori.adventure.util.TriState;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import java.util.UUID;

/**
 * Created by TreyRuffy on 01/03/2021.
 */

public class PlayerWorldSwitchEvent implements Listener {

    @EventHandler
    public void switchWorldEvent(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();
        final UUID uuid = p.getUniqueId();
        if (!p.hasPermission("tdj.use")
                || p.getGameMode() == GameMode.SPECTATOR
                || p.getGameMode() == GameMode.CREATIVE
                || TreysDoubleJump.DISABLED.contains(uuid)) {
            return;
        }

        if (ConfigManager.getEnabledWorlds().contains(p.getWorld().getName())) return;

        if (TreysDoubleJump.FLYING.contains(uuid)) {
            p.setFallDistance(0f);
            p.sendMessage(ConfigManager.getConfigMessage("FlyToggledOff"));
            TreysDoubleJump.FLYING.remove(uuid);
        }

        p.setFlying(false);
        p.setAllowFlight(false);

        if (!ConfigManager.getConfig().getBoolean("NoFall.Enabled"))
            p.setFlyingFallDamage(TriState.FALSE);
    }
}
