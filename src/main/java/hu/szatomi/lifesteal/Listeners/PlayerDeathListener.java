package hu.szatomi.lifesteal.Listeners;

import hu.szatomi.lifesteal.Lifesteal;
import hu.szatomi.lifesteal.MessageManager;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    private final Lifesteal plugin;
    private final MessageManager messageManager;

    public PlayerDeathListener(Lifesteal plugin, MessageManager messageManager) {
        this.plugin = plugin;
        this.messageManager = messageManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        handleVictim(victim);

        Player killer = victim.getKiller();
        if (killer != null && !killer.equals(victim)) {
            handleKiller(killer);
        }
    }

    private void handleVictim(Player player) {
        AttributeInstance maxHealthAttribute = player.getAttribute(Attribute.MAX_HEALTH);

        if (maxHealthAttribute == null) return;
        if (maxHealthAttribute.getBaseValue() <= 2) return;

        double currentMaxHealth = maxHealthAttribute.getBaseValue();
        double newMaxHealth = currentMaxHealth - 2.0;

        maxHealthAttribute.setBaseValue(newMaxHealth);
        messageManager.sendMessage(player, "death_loss");
    }

    private void handleKiller(Player player) {
        AttributeInstance maxHealthAttribute = player.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealthAttribute == null) return;

        double maxHearts = plugin.getConfig().getDouble("max_hearts", 20.0);
        double maxHealthLimit = maxHearts * 2.0;
        double currentMaxHealth = maxHealthAttribute.getBaseValue();

        if (currentMaxHealth >= maxHealthLimit) return;

        double newMaxHealth = Math.min(currentMaxHealth + 2.0, maxHealthLimit);
        maxHealthAttribute.setBaseValue(newMaxHealth);
        messageManager.sendMessage(player, "death_gain");
    }
}
