package me.treyruffy.treysdoublejump;

import me.treyruffy.treysdoublejump.commands.DoubleJumpCommand;
import me.treyruffy.treysdoublejump.commands.FlightCommand;
import me.treyruffy.treysdoublejump.commands.GroundPoundCommand;
import me.treyruffy.treysdoublejump.commands.ReloadCommand;
import me.treyruffy.treysdoublejump.events.DoubleJump;
import me.treyruffy.treysdoublejump.events.NoFallDamage;
import me.treyruffy.treysdoublejump.events.PlayerWorldSwitchEvent;
import me.treyruffy.treysdoublejump.util.ConfigManager;
import me.treyruffy.treysdoublejump.util.PAPI;
import me.treyruffy.treysdoublejump.util.UpdateManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by TreyRuffy on 08/12/2018.
 * Updated 01/03/2021
 */

public class TreysDoubleJump extends JavaPlugin {

    // Players in this cannot use double jump
    public static final Set<UUID> DISABLED = ConcurrentHashMap.newKeySet();
    // Players in this are flying
    public static final Set<UUID> FLYING = ConcurrentHashMap.newKeySet();
    // Players in this cannot ground pound
    public static final Set<UUID> DISABLED_GROUND_POUNDING = ConcurrentHashMap.newKeySet();

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

        getServer().getCommandMap().registerAll("tdj",
            List.of(
                new DoubleJumpCommand(),
                new FlightCommand(),
                new GroundPoundCommand(),
                new ReloadCommand()
            )
        );

        if (pm.getPlugin("PlaceholderAPI") == null) return;
        new PAPI().register();
    }
}
