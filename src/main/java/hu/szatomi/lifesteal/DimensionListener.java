package hu.szatomi.lifesteal;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class DimensionListener implements Listener {

    private final DimensionLockManager lockManager;

    public DimensionListener(DimensionLockManager lockManager) {
        this.lockManager = lockManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPortal(PlayerPortalEvent event) {
        if (event.getTo() == null) return;
        
        World targetWorld = event.getTo().getWorld();
        if (lockManager.isLocked(targetWorld)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(lockManager.getLockedMessage(targetWorld));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getTo() == null) return;
        
        World fromWorld = event.getFrom().getWorld();
        World toWorld = event.getTo().getWorld();
        
        // Only check if they are actually changing worlds
        if (fromWorld != null && fromWorld.equals(toWorld)) return;

        if (lockManager.isLocked(toWorld)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(lockManager.getLockedMessage(toWorld));
        }
    }
}
