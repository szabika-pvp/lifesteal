package hu.szatomi.lifesteal.Items;

import hu.szatomi.lifesteal.Colors;
import hu.szatomi.lifesteal.Lifesteal;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class HeartListener implements Listener {

    private final Lifesteal plugin;

    public HeartListener(Lifesteal plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || item.getItemMeta() == null) {
            return;
        }

        if (!item.getItemMeta().getPersistentDataContainer().has(HeartItem.HEART_KEY, PersistentDataType.BYTE)) {
            return;
        }

        event.setCancelled(true);
        Player player = event.getPlayer();

        if (player.hasCooldown(Material.NETHER_STAR)) {
            return;
        }

        AttributeInstance maxHealthAttr = player.getAttribute(Attribute.MAX_HEALTH);

        if (maxHealthAttr == null) return;

        int maxHeartsConfig = plugin.getConfig().getInt("max_hearts", 20);
        double maxHealthLimit = maxHeartsConfig * 2.0;

        if (maxHealthAttr.getBaseValue() >= maxHealthLimit) return;

        maxHealthAttr.setBaseValue(maxHealthAttr.getBaseValue() + 2.0);
        item.setAmount(item.getAmount() - 1);
        player.setCooldown(Material.NETHER_STAR, 20);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 1.0f, 1.0f);
        player.sendMessage(Component.text("+1 szív").color(Colors.GREEN));
    }
}
