package me.treyruffy.treysdoublejump.events;

import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;
import me.treyruffy.treysdoublejump.ParticleSender;
import me.treyruffy.treysdoublejump.TreysDoubleJump;
import me.treyruffy.treysdoublejump.api.DoubleJumpEvent;
import me.treyruffy.treysdoublejump.api.GroundPoundEvent;
import me.treyruffy.treysdoublejump.api.PreDoubleJumpEvent;
import me.treyruffy.treysdoublejump.commands.DoubleJumpCommand;
import me.treyruffy.treysdoublejump.commands.FlightCommand;
import me.treyruffy.treysdoublejump.commands.GroundPoundCommand;
import me.treyruffy.treysdoublejump.util.ConfigManager;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Created by TreyRuffy on 08/12/2018.
 */

public class DoubleJump implements Listener {

    // Cooldown timer for each player stored in a hashmap
    private static final Map<UUID, Integer> COOLDOWN = new HashMap<>();

    // Adds if the player is exempt from NCP, if it is enabled
    final Set<UUID> NCPPlayer = new HashSet<>();

    // Adds if the player can ground pound
    public static final Set<UUID> GROUNDED = new HashSet<>();


    // Grabs the cooldown from config
    public static Integer getCooldown(Player p) {
        return COOLDOWN.get(p.getUniqueId());
    }

    // Removes the exemption from NCP if the player leaves
    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (NCPPlayer.contains(p.getUniqueId())) {
            try {
                NCPExemptionManager.unexempt(p, CheckType.MOVING_SURVIVALFLY);
                NCPPlayer.remove(p.getUniqueId());
            } catch (Exception ignored) {}
        }
    }

    // Always checks whether the player can double jump again, and if so, it adds flight to the player
    @SuppressWarnings("deprecation")
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        final Player p = e.getPlayer();
        if (p.isFlying()
                || !p.hasPermission("tdj.use")
                || p.getGameMode() == GameMode.SPECTATOR
                || p.getGameMode() == GameMode.CREATIVE
                || !ConfigManager.getConfig().getStringList("EnabledWorlds").contains(p.getWorld().getName())) {
            return;
        }
        final UUID uuid = p.getUniqueId();
        if (!ConfigManager.getConfig().getStringList("DisabledBlocks").isEmpty()) {
            for (String blocks : ConfigManager.getConfig().getStringList("DisabledBlocks")) {
                try {
                    if (p.getWorld().getBlockAt(p.getLocation().add(0, -1, 0)).getType() == Material.valueOf(blocks.toUpperCase()) || p.getWorld().getBlockAt(p.getLocation()).getType() == Material.valueOf(blocks.toUpperCase())) {
                        GROUNDED.remove(uuid);
                        return;
                    }
                } catch (Exception ex) {
                    TreysDoubleJump.getInstance().getLogger().log(Level.WARNING, "Error occurred with DisabledBlocks list", ex);
                }
            }
        }
        if (COOLDOWN.containsKey(uuid)
                || DoubleJumpCommand.DISABLE_PLAYERS.contains(uuid)
                || FlightCommand.FLYING_PLAYERS.contains(uuid)) {
            return;
        }
        if (!ConfigManager.getConfig().getBoolean("InfiniteJump.Enabled") || !p.hasPermission("tdj.infinitejump")) {
            if (!p.isOnGround() || (p.getWorld().getBlockAt(p.getLocation().add(0, -1, 0)).getType() == Material.AIR && p.getWorld().getBlockAt(p.getLocation()).getType() == Material.AIR)) {
                return;
            }


            if (Bukkit.getPluginManager().getPlugin("NoCheatPlus") != null) {
                if (p.hasPermission("tdj.ncp")) {
                    if (NCPExemptionManager.isExempted(p, CheckType.MOVING_SURVIVALFLY)) {
                        PreDoubleJumpEvent preDoubleJumpEvent = new PreDoubleJumpEvent(p, false);

                        Bukkit.getPluginManager().callEvent(preDoubleJumpEvent);
                        if (preDoubleJumpEvent.isCancelled()) {
                            return;
                        }
                        p.setAllowFlight(true);
                        if (!ConfigManager.getConfig().getBoolean("NoFall.Enabled"))
                            p.setFlyingFallDamage(TriState.TRUE);
                        GROUNDED.remove(uuid);
                        return;
                    }
                    NCPExemptionManager.exemptPermanently(p, CheckType.MOVING_SURVIVALFLY);
                    PreDoubleJumpEvent preDoubleJumpEvent = new PreDoubleJumpEvent(p, false);

                    Bukkit.getPluginManager().callEvent(preDoubleJumpEvent);
                    if (preDoubleJumpEvent.isCancelled()) {
                        return;
                    }
                    p.setAllowFlight(true);
                    if (!ConfigManager.getConfig().getBoolean("NoFall.Enabled"))
                        p.setFlyingFallDamage(TriState.TRUE);
                    GROUNDED.remove(uuid);
                    NCP(p);
                    return;
                }
                return;
            }
            p.getScheduler().runDelayed(TreysDoubleJump.getInstance(), task -> {
                PreDoubleJumpEvent preDoubleJumpEvent = new PreDoubleJumpEvent(p, false);

                Bukkit.getPluginManager().callEvent(preDoubleJumpEvent);
                if (preDoubleJumpEvent.isCancelled()) {
                    return;
                }
                p.setAllowFlight(true);
                if (!ConfigManager.getConfig().getBoolean("NoFall.Enabled"))
                    p.setFlyingFallDamage(TriState.TRUE);
            }, () -> GROUNDED.remove(uuid), 1L);
        } else {
            if (Bukkit.getPluginManager().getPlugin("NoCheatPlus") != null) {
                if (p.hasPermission("tdj.ncp")) {
                    if (NCPExemptionManager.isExempted(p, CheckType.MOVING_SURVIVALFLY)) {
                        PreDoubleJumpEvent preDoubleJumpEvent = new PreDoubleJumpEvent(p, false);

                        Bukkit.getPluginManager().callEvent(preDoubleJumpEvent);
                        if (preDoubleJumpEvent.isCancelled()) {
                            return;
                        }
                        p.setAllowFlight(true);
                        if (!ConfigManager.getConfig().getBoolean("NoFall.Enabled"))
                            p.setFlyingFallDamage(TriState.TRUE);
                        GROUNDED.remove(uuid);
                        return;
                    }
                    NCPExemptionManager.exemptPermanently(p, CheckType.MOVING_SURVIVALFLY);
                    PreDoubleJumpEvent preDoubleJumpEvent = new PreDoubleJumpEvent(p, false);

                    Bukkit.getPluginManager().callEvent(preDoubleJumpEvent);
                    if (preDoubleJumpEvent.isCancelled()) {
                        return;
                    }
                    p.setAllowFlight(true);
                    if (!ConfigManager.getConfig().getBoolean("NoFall.Enabled"))
                        p.setFlyingFallDamage(TriState.TRUE);
                    GROUNDED.remove(uuid);
                    NCP(p);
                }
                return;
            }
            PreDoubleJumpEvent preDoubleJumpEvent = new PreDoubleJumpEvent(p, false);

            Bukkit.getPluginManager().callEvent(preDoubleJumpEvent);
            if (preDoubleJumpEvent.isCancelled()) {
                return;
            }
            p.setAllowFlight(true);
            if (!ConfigManager.getConfig().getBoolean("NoFall.Enabled"))
                p.setFlyingFallDamage(TriState.TRUE);
            GROUNDED.remove(uuid);
        }


    }

    private void NCP(Player p) {
        final UUID uuid = p.getUniqueId();
        NCPPlayer.add(uuid);
        p.getScheduler().runDelayed(TreysDoubleJump.getInstance(), task -> {
            try {
                NCPExemptionManager.unexempt(p, CheckType.MOVING_SURVIVALFLY);
                NCPPlayer.remove(uuid);
            } catch (Exception ignored) {}
        }, () -> NCPPlayer.remove(uuid), 60L);
    }

    // Checks if the player requested flight, without having access to it, so it can remove flight and set the player's velocity, particles, etc
    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent e) {
        final Player p = e.getPlayer();
        final UUID uuid = p.getUniqueId();
        if (FlightCommand.FLYING_PLAYERS.contains(uuid)
                || COOLDOWN.containsKey(uuid)
                || p.getGameMode() == GameMode.SPECTATOR
                || p.getGameMode() == GameMode.CREATIVE
                || !p.hasPermission("tdj.use")
                || !ConfigManager.getConfig().getStringList("EnabledWorlds").contains(p.getWorld().getName())
                || DoubleJumpCommand.DISABLE_PLAYERS.contains(uuid)) {
            return;
        }

        boolean cooldownEnabled = ConfigManager.getConfig().getBoolean("Cooldown.Enabled");
        int cooldownTime = ConfigManager.getConfig().getInt("Cooldown.Time");

        double velocityForward;
        double velocityUp;
        if (p.isSprinting()) {
            velocityForward = ConfigManager.getConfig().getDouble("Velocity.SprintingForward");
            velocityUp = ConfigManager.getConfig().getDouble("Velocity.SprintingUp");
        } else {
            velocityForward = ConfigManager.getConfig().getDouble("Velocity.Forward");
            velocityUp = ConfigManager.getConfig().getDouble("Velocity.Up");
        }

        boolean soundsEnabled = ((p.hasPermission("tdj.sounds")) && (ConfigManager.getConfig().getBoolean("Sounds" +
                ".Enabled")));

        Sound sound = Sound.valueOf(ConfigManager.getConfig().getString("Sounds.Type"));
        float volume = (float) ConfigManager.getConfig().getDouble("Sounds.Volume");
        float pitch = (float) ConfigManager.getConfig().getDouble("Sounds.Pitch");

        boolean particlesEnabled = ((p.hasPermission("tdj.particles")) && (ConfigManager.getConfig().getBoolean(
                "Particles.Enabled")));
        boolean particlesForEveryone = ConfigManager.getConfig().getBoolean("Particles.AllPlayers");
        String particleType = ConfigManager.getConfig().getString("Particles.Type");
        int particleAmount = ConfigManager.getConfig().getInt("Particles.Amount");
        float r = (float) ConfigManager.getConfig().getDouble("Particles.R");
        float g = (float) ConfigManager.getConfig().getDouble("Particles.G");
        float b = (float) ConfigManager.getConfig().getDouble("Particles.B");

        DoubleJumpEvent doubleJumpEvent = new DoubleJumpEvent(p, cooldownEnabled, cooldownTime, velocityForward,
                velocityUp, soundsEnabled, sound, volume, pitch, particlesEnabled, particlesForEveryone, particleType,
                particleAmount, r, g, b);
        doubleJumpEvent.callEvent();

        e.setCancelled(true);
        p.setAllowFlight(false);
        if (!ConfigManager.getConfig().getBoolean("NoFall.Enabled"))
            p.setFlyingFallDamage(TriState.FALSE);
        p.setFlying(false);


        if (doubleJumpEvent.isCancelled()) {
            return;
        }

        if (!GroundPoundCommand.GROUND_POUND_DISABLED.contains(uuid)) {
            GROUNDED.add(uuid);
        }

        if (doubleJumpEvent.isCooldownEnabled()) {
            COOLDOWN.put(uuid, doubleJumpEvent.getCooldownTime());
            p.getScheduler().runAtFixedRate(TreysDoubleJump.getInstance(), task -> {
                COOLDOWN.put(uuid, COOLDOWN.get(uuid) - 1);
                if (COOLDOWN.get(uuid) != 0) return;
                COOLDOWN.remove(uuid);
                task.cancel();
            }, () -> COOLDOWN.remove(uuid), 20L, 20L);
        }

        p.setVelocity(p.getLocation().getDirection().multiply(doubleJumpEvent.getVelocityForward()).setY(doubleJumpEvent.getVelocityUp()));

        if (doubleJumpEvent.soundsEnabled()) {
            p.playSound(p.getLocation(), doubleJumpEvent.getSound(), doubleJumpEvent.getVolume(), doubleJumpEvent.getPitch());
        }

        if (doubleJumpEvent.particlesEnabled()) {
            if (doubleJumpEvent.isParticlesForEveryone()) {
                for (Player players : Bukkit.getOnlinePlayers()) {
                    ParticleSender.sendParticle(players, doubleJumpEvent.getParticleType(), p.getLocation(),
                            doubleJumpEvent.getParticleAmount(), doubleJumpEvent.getParticleR(),
                            doubleJumpEvent.getParticleG(), doubleJumpEvent.getParticleB());
                }
            } else {
                ParticleSender.sendParticle(p, doubleJumpEvent.getParticleType(),
                        p.getLocation(), doubleJumpEvent.getParticleAmount(), doubleJumpEvent.getParticleR(),
                        doubleJumpEvent.getParticleG(), doubleJumpEvent.getParticleB());
            }
        }
    }

    // Checks whether the player tries to sneak while double jumping, if they have permission to
    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        if (p.getGameMode() == GameMode.SPECTATOR
                || p.getGameMode() == GameMode.CREATIVE
                || !p.hasPermission("tdj.use")
                || !p.hasPermission("tdj.groundpound")
                || !ConfigManager.getConfig().getStringList("EnabledWorlds").contains(p.getWorld().getName())
                || !GROUNDED.contains(p.getUniqueId())
                || FlightCommand.FLYING_PLAYERS.contains(p.getUniqueId())
                || DoubleJumpCommand.DISABLE_PLAYERS.contains(p.getUniqueId())) {
            return;
        }

        boolean isCancelled = !ConfigManager.getConfig().getBoolean("GroundPound.Enabled");
        double velocityDown = ConfigManager.getConfig().getDouble("GroundPound.VelocityDown");

        GroundPoundEvent groundPoundEvent = new GroundPoundEvent(p, isCancelled, velocityDown);

        Bukkit.getPluginManager().callEvent(groundPoundEvent);

        if (groundPoundEvent.isCancelled() || GroundPoundCommand.GROUND_POUND_DISABLED.contains(p.getUniqueId())) {
            return;
        }
        p.setVelocity(new Vector(0, -groundPoundEvent.getVelocityDown(), 0));
    }

}
