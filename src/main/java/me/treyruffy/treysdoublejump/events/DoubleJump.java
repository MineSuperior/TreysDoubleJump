package me.treyruffy.treysdoublejump.events;

import me.treyruffy.treysdoublejump.ParticleSender;
import me.treyruffy.treysdoublejump.TreysDoubleJump;
import me.treyruffy.treysdoublejump.api.DoubleJumpEvent;
import me.treyruffy.treysdoublejump.api.GroundPoundEvent;
import me.treyruffy.treysdoublejump.api.PreDoubleJumpEvent;
import me.treyruffy.treysdoublejump.util.ConfigManager;
import net.kyori.adventure.util.TriState;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by TreyRuffy on 08/12/2018.
 */

public class DoubleJump implements Listener {

    // Cooldown timer for each player stored in a hashmap
    private static final Map<UUID, Integer> COOLDOWN = new HashMap<>();

    // Adds if the player can ground pound
    public static final Set<UUID> GROUNDED = new HashSet<>();


    // Grabs the cooldown from config
    public static Integer getCooldown(Player p) {
        return COOLDOWN.get(p.getUniqueId());
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
                || !ConfigManager.getEnabledWorlds().contains(p.getWorld().getName())) {
            return;
        }
        final UUID uuid = p.getUniqueId();
        final Location location = p.getLocation();
        Material below = null, at = null;
        if (!ConfigManager.getDisabledBlocks().isEmpty()
            && (ConfigManager.getDisabledBlocks().contains(below = location.clone().add(0, -1, 0).getBlock().getType())
            || ConfigManager.getDisabledBlocks().contains(at = location.getBlock().getType()))) {
            GROUNDED.remove(uuid);
            return;
        }
        if (COOLDOWN.containsKey(uuid)
                || TreysDoubleJump.DISABLED.contains(uuid)
                || TreysDoubleJump.FLYING.contains(uuid)) {
            return;
        }
        if (!ConfigManager.getConfig().getBoolean("InfiniteJump.Enabled") || !p.hasPermission("tdj.infinitejump")) {
            if (!p.isOnGround() || ((below == null ? p.getWorld().getBlockAt(location.clone().add(0, -1, 0)).getType() : below) == Material.AIR && (at == null ? p.getWorld().getBlockAt(location).getType() : at) == Material.AIR)) {
                return;
            }

            p.getScheduler().runDelayed(TreysDoubleJump.getInstance(), task -> {
                if (!new PreDoubleJumpEvent(p, false).callEvent())
                    return;

                p.setAllowFlight(true);
                if (!ConfigManager.getConfig().getBoolean("NoFall.Enabled"))
                    p.setFlyingFallDamage(TriState.TRUE);
            }, () -> GROUNDED.remove(uuid), 1L);
        } else {
            if (!new PreDoubleJumpEvent(p, false).callEvent())
                return;

            p.setAllowFlight(true);
            if (!ConfigManager.getConfig().getBoolean("NoFall.Enabled"))
                p.setFlyingFallDamage(TriState.TRUE);
            GROUNDED.remove(uuid);
        }
    }

    // Checks if the player requested flight, without having access to it, so it can remove flight and set the player's velocity, particles, etc
    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent e) {
        final Player p = e.getPlayer();
        final UUID uuid = p.getUniqueId();
        if (TreysDoubleJump.FLYING.contains(uuid)
                || COOLDOWN.containsKey(uuid)
                || p.getGameMode() == GameMode.SPECTATOR
                || p.getGameMode() == GameMode.CREATIVE
                || !p.hasPermission("tdj.use")
                || !ConfigManager.getEnabledWorlds().contains(p.getWorld().getName())
                || TreysDoubleJump.DISABLED.contains(uuid)) {
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

        if (!TreysDoubleJump.DISABLED_GROUND_POUNDING.contains(uuid)) {
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

        if (!doubleJumpEvent.particlesEnabled()) return;

        if (doubleJumpEvent.isParticlesForEveryone()) {
            ParticleSender.sendParticle(p.getWorld().getPlayers(),doubleJumpEvent.getParticleType(), p.getLocation(),
                    doubleJumpEvent.getParticleAmount(), doubleJumpEvent.getParticleR(),
                    doubleJumpEvent.getParticleG(), doubleJumpEvent.getParticleB());
            return;
        }

        ParticleSender.sendParticle(List.of(p), doubleJumpEvent.getParticleType(),
            p.getLocation(), doubleJumpEvent.getParticleAmount(), doubleJumpEvent.getParticleR(),
            doubleJumpEvent.getParticleG(), doubleJumpEvent.getParticleB());
    }

    // Checks whether the player tries to sneak while double jumping, if they have permission to
    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        if (p.getGameMode() == GameMode.SPECTATOR
                || p.getGameMode() == GameMode.CREATIVE
                || !p.hasPermission("tdj.use")
                || !p.hasPermission("tdj.groundpound")
                || !ConfigManager.getEnabledWorlds().contains(p.getWorld().getName())
                || !GROUNDED.contains(p.getUniqueId())
                || TreysDoubleJump.FLYING.contains(p.getUniqueId())
                || TreysDoubleJump.DISABLED.contains(p.getUniqueId())) {
            return;
        }

        boolean isCancelled = !ConfigManager.getConfig().getBoolean("GroundPound.Enabled");
        double velocityDown = ConfigManager.getConfig().getDouble("GroundPound.VelocityDown");

        GroundPoundEvent groundPoundEvent = new GroundPoundEvent(p, isCancelled, velocityDown);
        if (!groundPoundEvent.callEvent() || TreysDoubleJump.DISABLED_GROUND_POUNDING.contains(p.getUniqueId())) {
            return;
        }

        p.setVelocity(new Vector(0, -groundPoundEvent.getVelocityDown(), 0));
    }

}
