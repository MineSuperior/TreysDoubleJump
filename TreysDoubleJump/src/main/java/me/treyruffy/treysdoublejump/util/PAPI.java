package me.treyruffy.treysdoublejump.util;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.treyruffy.treysdoublejump.TreysDoubleJump;
import me.treyruffy.treysdoublejump.api.DoubleJumpAPI;
import me.treyruffy.treysdoublejump.api.FlightAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Created by TreyRuffy on 08/12/2018.
 * Updated 01/26/2020.
 */

public class PAPI extends PlaceholderExpansion {

    private final TreysDoubleJump plugin;

    // Registers the tdj placeholder
    public PAPI(TreysDoubleJump plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    // Registers the placeholders
    public String onPlaceholderRequest(Player p, @NotNull String identifier) {
        if (p == null) {
            return "";
        }

        return switch (identifier.toLowerCase(Locale.ROOT)) {
            case "cooldown" -> DoubleJumpAPI.getDoubleJumpTime(p).toString();
            case "flightenabled" -> FlightAPI.isFlightEnabled(p).toString();
            case "doublejumpenabled" -> DoubleJumpAPI.isDoubleJumpEnabled(p).toString();
            case "groundpoundenabled" -> DoubleJumpAPI.isGroundPoundEnabled(p).toString();
            case "canusegroundpound" -> DoubleJumpAPI.canUseGroundPound(p).toString();
            default -> null;
        };
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "tdj";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

}
