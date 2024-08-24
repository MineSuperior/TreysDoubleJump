package me.treyruffy.treysdoublejump.events;

import me.treyruffy.treysdoublejump.TreysDoubleJump;
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
		if (!p.hasPermission("tdj.use")) {
			return;
		}
		if (p.getGameMode() == GameMode.SPECTATOR) {
			return;
		}
		if (p.getGameMode() == GameMode.CREATIVE) {
			return;
		}
		if (DoubleJumpCommand.DisablePlayers.contains(p.getUniqueId().toString())) {
			return;
		}
		if (!ConfigManager.getConfig().getStringList("EnabledWorlds").contains(p.getWorld().getName())){
			if (FlightCommand.FlyingPlayers.contains(p.getUniqueId().toString())) {
				p.setFallDistance(0f);
				p.sendMessage(ConfigManager.getConfigMessage(
						"FlyToggledOff"));
				FlightCommand.FlyingPlayers.remove(p.getUniqueId().toString());
			}
			p.setFlying(false);
			p.setAllowFlight(false);
			try {
				if (!ConfigManager.getConfig().getBoolean("NoFall.Enabled"))
					p.setFlyingFallDamage(TriState.FALSE);
			} catch (NoSuchMethodError ignored) {}
		}
	}
}
