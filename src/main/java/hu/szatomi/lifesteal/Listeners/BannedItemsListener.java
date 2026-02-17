package hu.szatomi.lifesteal.Listeners;

import hu.szatomi.lifesteal.Lifesteal;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BannedItemsListener implements Listener {

    private final Lifesteal plugin;

    public BannedItemsListener(Lifesteal plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {

        // Ender Crystal damage limit
        if (plugin.getConfig().getBoolean("restrictions.block-crystal-explosions", true) && event.getDamager() instanceof EnderCrystal) {
            event.setCancelled(true);
            return;
        }

        // Mace limit
        if (event.getDamager() instanceof Player player) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand.getType() == Material.MACE) {
                double maxDamage = plugin.getConfig().getDouble("restrictions.mace-max-damage", 16.0);
                if (event.getDamage() > maxDamage) {
                    event.setDamage(maxDamage);
                }
            }
        }

        // TNT Minecart limit
        if (event.getDamager() instanceof ExplosiveMinecart) {
            double maxDamage = plugin.getConfig().getDouble("restrictions.tnt-minecart-max-damage", 18.0);
            if (event.getDamage() > maxDamage) {
                event.setDamage(maxDamage);
            }
        }
    }

    @EventHandler
    public void cancelBed(PlayerBedEnterEvent event) {
        if (!plugin.getConfig().getBoolean("restrictions.block-bed-explosions", true)) return;

        if (event.getPlayer().getWorld().getEnvironment() != World.Environment.NORMAL) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onAnchorInteract(PlayerInteractEvent event) {
        if (!plugin.getConfig().getBoolean("restrictions.block-anchor-explosions", true)) return;

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.RESPAWN_ANCHOR)
            return;

        if (event.getPlayer().getWorld().getEnvironment() == World.Environment.NETHER)
            return;

        ItemStack itemInHand = event.getItem();
        boolean isBlockInHand = (itemInHand != null && itemInHand.getType().isBlock() && itemInHand.getType() != Material.GLOWSTONE);

        if (isBlockInHand) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onCrystalExplode(EntityExplodeEvent event) {
        if (!plugin.getConfig().getBoolean("restrictions.block-crystal-explosions", true)) return;

        if (event.getEntity() instanceof EnderCrystal) {
            event.blockList().clear();

            // Opcionális: Ha a robbanás hangját/effektjét is teljesen el akarod tüntetni:
            // event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTotemUse(EntityResurrectEvent event) {
        if (!plugin.getConfig().getBoolean("restrictions.disable-totems", true)) return;

        if (event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }
}
