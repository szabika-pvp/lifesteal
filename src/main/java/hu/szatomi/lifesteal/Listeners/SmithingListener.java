package hu.szatomi.lifesteal.Listeners;

import hu.szatomi.lifesteal.Lifesteal;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.ItemStack;

public class SmithingListener implements Listener {

    private final Lifesteal plugin;

    public SmithingListener(Lifesteal plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepareSmithing(PrepareSmithingEvent event) {
        if (!plugin.getConfig().getBoolean("restrictions.ban-netherite-armor", true)) return;

        ItemStack result = event.getResult();
        if (result == null) return;

        if (isNetheriteArmor(result.getType())) {
            event.setResult(null);
        }
    }

    @EventHandler
    public void onSmithItem(SmithItemEvent event) {
        if (!plugin.getConfig().getBoolean("restrictions.ban-netherite-armor", true)) return;

        ItemStack result = event.getCurrentItem();
        if (result == null) return;

        if (isNetheriteArmor(result.getType())) {
            event.setCancelled(true);
        }
    }

    private boolean isNetheriteArmor(Material material) {
        return material == Material.NETHERITE_HELMET ||
               material == Material.NETHERITE_CHESTPLATE ||
               material == Material.NETHERITE_LEGGINGS ||
               material == Material.NETHERITE_BOOTS;
    }
}
