package me.abb3v.xsouls.services;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Effects {

    public static void showSoulEffect(Player player) {
        // Get the player's eye location
        Location eyeLocation = player.getEyeLocation();
        // Calculate the location 2 blocks in front of the player at eye level
        Location inFront = eyeLocation.add(eyeLocation.getDirection().multiply(2));
        player.getWorld().spawnParticle(Particle.SOUL, inFront, 50, 0.5, 0.5, 0.5, 0.05);
        //Play a "DING" sound.
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }
}

