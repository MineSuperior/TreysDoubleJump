package me.treyruffy.treysdoublejump;

import me.treyruffy.treysdoublejump.commands.DoubleJumpCommand;
import me.treyruffy.treysdoublejump.commands.FlightCommand;
import me.treyruffy.treysdoublejump.commands.GroundPoundCommand;
import me.treyruffy.treysdoublejump.events.DoubleJump;
import me.treyruffy.treysdoublejump.events.NoFallDamage;
import me.treyruffy.treysdoublejump.events.PlayerWorldSwitchEvent;
import me.treyruffy.treysdoublejump.util.ConfigManager;
import me.treyruffy.treysdoublejump.util.PAPI;
import me.treyruffy.treysdoublejump.util.UpdateManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

/**
 * Created by TreyRuffy on 08/12/2018.
 * Updated 01/03/2021
 */

public class TreysDoubleJump extends JavaPlugin {

    private static TreysDoubleJump instance;

    public static TreysDoubleJump getInstance() {
        return instance;
    }

    public TreysDoubleJump() {
        instance = this;
    }

    // Sets up everything
    @Override
    public void onEnable() {
        ConfigManager.reloadConfig();
        new UpdateManager().setup();
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new DoubleJump(), this);
        pm.registerEvents(new NoFallDamage(), this);
        pm.registerEvents(new PlayerWorldSwitchEvent(), this);

        Objects.requireNonNull(getCommand("fly")).setExecutor(new FlightCommand());
        Objects.requireNonNull(getCommand("tdj")).setExecutor(new DoubleJumpCommand());
        Objects.requireNonNull(getCommand("djreload")).setExecutor(new DoubleJumpCommand());
        Objects.requireNonNull(getCommand("groundpound")).setExecutor(new GroundPoundCommand());

        if (pm.getPlugin("PlacehodlerAPI") == null) return;
        new PAPI().register();
    }
}
