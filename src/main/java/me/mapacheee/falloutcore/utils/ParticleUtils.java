package me.mapacheee.falloutcore.utils;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Random;

public class ParticleUtils {

    public static void createRadiationHalo(Player player, Particle particle, int count, double extra, double radius, double density) {
        Location center = player.getLocation().add(0, 1.5, 0);
        Random random = new Random();

        int actualCount = (int) (count * density);
        if (actualCount < 1) actualCount = 1;

        for (int i = 0; i < actualCount; i++) {
            double theta = 2 * Math.PI * random.nextDouble();
            double phi = Math.acos(2 * random.nextDouble() - 1);
            double x = radius * Math.sin(phi) * Math.cos(theta);
            double y = radius * Math.sin(phi) * Math.sin(theta);
            double z = radius * Math.cos(phi);

            x += (random.nextDouble() - 0.5) * 0.3;
            y += (random.nextDouble() - 0.5) * 0.3;
            z += (random.nextDouble() - 0.5) * 0.3;

            player.getWorld().spawnParticle(
                    particle,
                    center.getX() + x,
                    center.getY() + y,
                    center.getZ() + z,
                    1,
                    0, 0, 0,
                    extra,
                    null,
                    true
            );
        }

        for (int i = 0; i < 8; i++) {
            double angle = (System.currentTimeMillis() / 1000.0) % (2 * Math.PI);
            double x = radius * Math.cos(angle + i * Math.PI/4);
            double z = radius * Math.sin(angle + i * Math.PI/4);

            player.getWorld().spawnParticle(
                    particle,
                    center.getX() + x,
                    center.getY() + 0.2,
                    center.getZ() + z,
                    1,
                    0, 0.05, 0,
                    extra,
                    null,
                    true
            );
        }
    }

    public static void createDistortionWave(Player player, Location center, Vector direction, double radius, int particles, double intensity) {
        Vector perpendicular = new Vector(-direction.getZ(), 0, direction.getX()).normalize();
        Random random = new Random();

        for (int i = 0; i < particles; i++) {
            double angle = 2 * Math.PI * i / particles;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);

            Vector point = perpendicular.clone().multiply(x).add(direction.clone().multiply(z));

            point.add(new Vector(
                    (random.nextDouble() - 0.5) * 0.3,
                    (random.nextDouble() - 0.5) * 0.3,
                    (random.nextDouble() - 0.5) * 0.3
            ));

            Location particleLoc = center.clone().add(point);

            player.getWorld().spawnParticle(
                    Particle.SQUID_INK,
                    particleLoc,
                    1,
                    0, 0, 0,
                    intensity,
                    null,
                    true
            );
        }
    }
}