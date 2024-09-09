package me.treyruffy.treysdoublejump.api;

import me.treyruffy.treysdoublejump.commands.FlightCommand;
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
        return FlightCommand.FLYING_PLAYERS.contains(player.getUniqueId());
    }

    /**
     * Sets flight for the player.
     *
     * @param player  the player
     * @param enabled sets flight to enabled if true
     */
    public static void setFlight(Player player, Boolean enabled) {
        if (enabled) {
            FlightCommand.FLYING_PLAYERS.remove(player.getUniqueId());
            return;
        }

        FlightCommand.FLYING_PLAYERS.add(player.getUniqueId());
    }
}
