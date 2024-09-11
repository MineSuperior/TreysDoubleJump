package me.treyruffy.treysdoublejump;

import com.destroystokyo.paper.ParticleBuilder;
import me.treyruffy.treysdoublejump.util.ConfigManager;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import java.util.List;

/**
 * Created by TreyRuffy on 01/26/2020.
 */

public class ParticleSender {
    public static void sendParticle(List<Player> players, String particle, Location loc, int amount, float r, float g, float b) {
        if (players == null && !ConfigManager.getConfig().getBoolean("Particles.AllPlayers")) return;
        final ParticleBuilder builder = new ParticleBuilder(Particle.valueOf(particle));
        builder.receivers(players);
        builder.location(loc);
        builder.count(amount);
        if (builder.particle() == Particle.DUST) {
            builder.color(Color.fromRGB((int) r * 100, (int) g * 100, (int) b * 100), 1);
        } else {
            builder.extra(1);
        }
        builder.spawn();
    }
}
