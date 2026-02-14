package hu.szatomi.lifesteal.Listeners;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BannedItemsListener implements Listener {

    @EventHandler
    public void cancelBed(PlayerBedEnterEvent event) {
        if (event.getPlayer().getWorld().getEnvironment() == World.Environment.NETHER || event.getPlayer().getWorld().getEnvironment() == World.Environment.THE_END) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onAnchorInteract(PlayerInteractEvent event) {
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
        if (event.getEntity() instanceof EnderCrystal) {
            event.blockList().clear();

            // Opcionális: Ha a robbanás hangját/effektjét is teljesen el akarod tüntetni:
            // event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCrystalDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof EnderCrystal) {
            event.setCancelled(true);
        }
    }
}
