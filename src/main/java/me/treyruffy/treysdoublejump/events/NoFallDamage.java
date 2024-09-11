package me.treyruffy.treysdoublejump.events;

import me.treyruffy.treysdoublejump.TreysDoubleJump;
import me.treyruffy.treysdoublejump.util.ConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * Created by TreyRuffy on 08/12/2018.
 */

public class NoFallDamage implements Listener {

    // Cancels damage if no fall is enabled
    @EventHandler(priority = EventPriority.HIGH)
    public void noFall(EntityDamageEvent e) {
        if (e.getCause() != DamageCause.FALL) {
            return;
        }
        if (!ConfigManager.getConfig().getBoolean("NoFall.Enabled")) {
            e.setCancelled(false);
            return;
        }
        if (!(e.getEntity() instanceof Player p)) {
            return;
        }
        if (!p.hasPermission("tdj.nofall")) {
            e.setCancelled(false);
            return;
        }
        if (TreysDoubleJump.DISABLED.contains(p.getUniqueId())) {
            e.setCancelled(false);
        }
        if (ConfigManager.getConfig().getStringList("EnabledWorlds").contains(p.getWorld().getName())) {
            e.setCancelled(true);
            return;
        }
        e.setCancelled(false);
    }
}
