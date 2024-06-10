package me.abb3v.xsouls.listeners;

import me.abb3v.xsouls.services.Effects;
import me.abb3v.xsouls.services.Souls;
import me.abb3v.xsouls.utils.ConfigManager;
import me.abb3v.xsouls.utils.MessageUtils;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Random;

public class EntityDeathListener implements Listener {

    private final Souls souls;
    private final Random random = new Random();

    public EntityDeathListener(Souls souls) {
        this.souls = souls;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() instanceof Player) {
            Player player = event.getEntity().getKiller();
            EntityType entityType = event.getEntity().getType();
            String entityName = entityType.name();

            String rewardPath = "soul_rewards." + entityName + ".reward";
            String dropChancePath = "soul_rewards." + entityName + ".drop_chance";

            if (!ConfigManager.hasKey(rewardPath) || !ConfigManager.hasKey(dropChancePath)) {
                // Entity is not configured, so ignore it
                return;
            }

            int reward = ConfigManager.getInt(rewardPath);
            double dropChance = ConfigManager.getDouble(dropChancePath);

            if (random.nextDouble() <= dropChance) {
                souls.addSouls(player.getUniqueId(), reward);
                String msg = MessageUtils.formatMessage("§aYou have been awarded " + reward + " soul(s) for killing a " + entityName.toLowerCase() + "!");
                player.sendMessage(msg);
                Effects.showSoulEffect(player);
            }
        }
    }
}
