package me.treyruffy.treysdoublejump.api;

import me.treyruffy.treysdoublejump.TreysDoubleJump;
import org.bukkit.entity.Player;

/* Created by TreyRuffy on 08/12/2018. */

/**
 * The Flight api
 */
public class FlightAPI {

    /**
     * Is flight enabled for the player.
     *
     * @param player the player
     * @return flight enabled for the player
     */
    public static Boolean isFlightEnabled(Player player) {
        return TreysDoubleJump.FLYING.contains(player.getUniqueId());
    }

    /**
     * Sets flight for the player.
     *
     * @param player  the player
     * @param enabled sets flight to enabled if true
     */
    public static void setFlight(Player player, Boolean enabled) {
        if (enabled) {
            TreysDoubleJump.FLYING.remove(player.getUniqueId());
            return;
        }

        TreysDoubleJump.FLYING.add(player.getUniqueId());
    }
}
