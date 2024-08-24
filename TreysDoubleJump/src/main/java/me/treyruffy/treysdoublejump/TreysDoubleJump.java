package me.treyruffy.treysdoublejump;

import me.treyruffy.treysdoublejump.events.*;
import me.treyruffy.treysdoublejump.util.ConfigManager;
import me.treyruffy.treysdoublejump.util.PAPI;
import me.treyruffy.treysdoublejump.util.UpdateManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

/**
 * Created by TreyRuffy on 08/12/2018.
 * Updated 01/03/2021
 */

public class TreysDoubleJump extends JavaPlugin implements Listener {

	private static TreysDoubleJump instance;

	public static TreysDoubleJump getInstance() {
		return instance;
	}

	public static File dataFolder;

	// Sets up everything
	@Override
	public void onEnable() {
		instance = this;
		ConfigManager.reloadConfig();
		dataFolder = getDataFolder();
		new UpdateManager().setup();
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(this, this);
		pm.registerEvents(new DoubleJump(), this);
		pm.registerEvents(new NoFallDamage(), this);
		pm.registerEvents(new PlayerWorldSwitchEvent(), this);

		Objects.requireNonNull(getCommand("fly")).setExecutor(new FlightCommand());
		Objects.requireNonNull(getCommand("tdj")).setExecutor(new DoubleJumpCommand());
		Objects.requireNonNull(getCommand("djreload")).setExecutor(new DoubleJumpCommand());
		Objects.requireNonNull(getCommand("groundpound")).setExecutor(new GroundPoundCommand());

		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
			new PAPI(this).register();
		}
	}
}