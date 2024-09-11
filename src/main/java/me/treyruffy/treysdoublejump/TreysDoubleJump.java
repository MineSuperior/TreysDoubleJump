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
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

        // Register our permissions (Better compatibility with LuckPerms web panel)
        List<Permission> permissions = new ArrayList<>();
        permissions.add(new Permission("tdj.use", "Use the double jump feature.", PermissionDefault.TRUE));
        permissions.add(new Permission("tdj.sounds", "Sounds whilst double jumping.", PermissionDefault.TRUE));
        permissions.add(new Permission("tdj.particles", "Particles whilst double jumping.", PermissionDefault.TRUE));
        permissions.add(new Permission("tdj.command", "Allows players to toggle their double jump.", PermissionDefault.TRUE));
        permissions.add(new Permission("tdj.nofall", "No fall when you have double jump on.", PermissionDefault.TRUE));
        permissions.add(new Permission("tdj.groundpound", "Allows players to ground pound.", PermissionDefault.TRUE));
        permissions.add(new Permission("tdj.groundpoundcommand", "Allows players to toggle their ground pounding.", PermissionDefault.TRUE));
        permissions.add(new Permission("tdj.infinitejump", "Sounds whilst double jumping.", PermissionDefault.TRUE));
        permissions.add(new Permission("tdj.fly", "Allows you to fly!", PermissionDefault.OP));
        permissions.add(new Permission("tdj.toggleothers", "Allows you to toggle other people's double jump.", PermissionDefault.OP));
        permissions.add(new Permission("tdj.fly.toggleothers", "Allows you to toggle other people's flight.", PermissionDefault.OP));
        permissions.add(new Permission("tdj.*", "Get granted all the double jump permissions.", PermissionDefault.OP,
            Map.of(
                "tdj.use", true,
                "tdj.sounds", true,
                "tdj.particles", true,
                "tdj.command", true,
                "tdj.nofall", true,
                "tdj.fly", true,
                "tdj.groundpound", true,
                "tdj.groundpoundcommand", true,
                "tdj.infinitejump", true
            )
        ));
        pm.addPermissions(permissions);

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
