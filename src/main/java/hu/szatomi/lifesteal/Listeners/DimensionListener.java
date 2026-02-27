package hu.szatomi.lifesteal.Listeners;

import hu.szatomi.lifesteal.DimensionLockManager;
import hu.szatomi.lifesteal.MessageManager;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class DimensionListener implements Listener {

    private final DimensionLockManager lockManager;
    private final MessageManager messageManager;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public DimensionListener(DimensionLockManager lockManager, MessageManager messageManager) {
        this.lockManager = lockManager;
        this.messageManager = messageManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPortal(PlayerPortalEvent event) {
        World toWorld = event.getTo().getWorld();
        handleTeleport(event.getPlayer(), toWorld, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        World fromWorld = event.getFrom().getWorld();
        World toWorld = event.getTo().getWorld();
        
        if (fromWorld != null && fromWorld.equals(toWorld)) return;
        
        handleTeleport(event.getPlayer(), toWorld, event);
    }

    private void handleTeleport(Player player, World toWorld, org.bukkit.event.Cancellable event) {
        if (toWorld == null) return;
        
        if (lockManager.isLocked(toWorld)) {
            event.setCancelled(true);
            
            String key = "dimension_locked_title";
            LocalDateTime unlockTime = lockManager.getUnlockTime(toWorld.getEnvironment());
            
            if (toWorld.getEnvironment() == World.Environment.NETHER) {
                key = "dimension_locked_nether";
            } else if (toWorld.getEnvironment() == World.Environment.THE_END) {
                key = "dimension_locked_end";
            }
            
            String dateStr = unlockTime != null ? unlockTime.format(FORMATTER) : "Unknown";
            
            player.sendActionBar(messageManager.getMessage(player, key, Map.of("date", dateStr)));
        }
    }
}
