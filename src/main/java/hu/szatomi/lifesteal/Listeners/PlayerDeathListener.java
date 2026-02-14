package hu.szatomi.lifesteal.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        AttributeInstance maxHealthAttribute = player.getAttribute(Attribute.MAX_HEALTH);

        if (maxHealthAttribute == null) return;
        if (maxHealthAttribute.getBaseValue() <= 2) return;

        double currentMaxHealth = maxHealthAttribute.getBaseValue();
        double newMaxHealth = currentMaxHealth - 2.0;

        maxHealthAttribute.setBaseValue(newMaxHealth);
        player.sendMessage(ChatColor.RED + "You have lost a heart!");
    }
}
