package me.treyruffy.treysdoublejump.api;

import me.treyruffy.treysdoublejump.events.DoubleJump;
import me.treyruffy.treysdoublejump.commands.DoubleJumpCommand;
import me.treyruffy.treysdoublejump.commands.GroundPoundCommand;
import org.bukkit.entity.Player;

/* Created by TreyRuffy on 08/12/2018. */

/**
 * The DoubleJump api
 */
public class DoubleJumpAPI {

    /**
     * Gets the double jump cooldown time for the player.
     * Returns 0 if it is null.
     *
     * @param player the player
     * @return the cooldown time in seconds
     */
    // Accesses the cooldown timer for the player
    public static Integer getDoubleJumpTime(Player player) {
        final Integer cooldown = DoubleJump.getCooldown(player);
        return cooldown == null ? 0 : cooldown;
    }

    /**
     * Checks if double jumping is enabled for the player.
     * Returns true if the player can double jump.
     *
     * @param player the player
     * @return double jump enabled for the player
     */
    // Accesses whether the player can double jump or not
    public static Boolean isDoubleJumpEnabled(Player player) {
        return !DoubleJumpCommand.DISABLED_PLAYERS.contains(player.getUniqueId());
    }

    /**
     * Sets double jump.
     *
     * @param player  the player
     * @param enabled sets double jump to enabled if true
     */
    // Sets whether the player can double jump or not
    public static void setDoubleJump(Player player, Boolean enabled) {
        if (enabled) {
            DoubleJumpCommand.DISABLED_PLAYERS.remove(player.getUniqueId());
            return;
        }

        DoubleJumpCommand.DISABLED_PLAYERS.add(player.getUniqueId());
    }

    /**
     * Is ground pound enabled.
     *
     * @param player the player
     * @return ground pound enabled for the player
     */
    // Accesses whether the player has access to ground pound
    public static Boolean isGroundPoundEnabled(Player player) {
        return !GroundPoundCommand.GROUND_POUND_DISABLED.contains(player.getUniqueId());
    }

    /**
     * Can use ground pound now.
     * Returns true if the player can use ground pound now.
     *
     * @param player the player
     * @return can use ground pound now
     */
    // Accesses whether the player can use ground pound now
    public static Boolean canUseGroundPound(Player player) {
        return DoubleJump.GROUNDED.contains(player.getUniqueId());
    }

    /**
     * Sets ground pound.
     *
     * @param player  the player
     * @param enabled sets the ground pound to enabled if true
     */
    // Sets whether the player can or cannot use ground pound
    public static void setGroundPound(Player player, Boolean enabled) {
        if (enabled) {
            GroundPoundCommand.GROUND_POUND_DISABLED.remove(player.getUniqueId());
            return;
        }

        GroundPoundCommand.GROUND_POUND_DISABLED.add(player.getUniqueId());
    }
}
