package hu.szatomi.lifesteal.Listeners;

import hu.szatomi.lifesteal.Lifesteal;
import hu.szatomi.lifesteal.MessageManager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatLogListener implements Listener {

    private final Lifesteal plugin;
    private final MessageManager messageManager;
    private final Map<UUID, BukkitTask> combatTasks = new HashMap<>();

    public CombatLogListener(Lifesteal plugin, MessageManager messageManager) {
        this.plugin = plugin;
        this.messageManager = messageManager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;

        Player attacker = null;
        if (event.getDamager() instanceof Player) {
            attacker = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof Player) {
                attacker = (Player) projectile.getShooter();
            }
        }

        if (attacker == null || attacker.equals(victim)) return;

        refreshCombat(victim);
        refreshCombat(attacker);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (combatTasks.containsKey(uuid)) {
            BukkitTask task = combatTasks.remove(uuid);
            if (task != null) task.cancel();
            
            // Combat log penalty: Kill player
            player.setHealth(0);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        UUID uuid = event.getEntity().getUniqueId();
        if (combatTasks.containsKey(uuid)) {
            BukkitTask task = combatTasks.remove(uuid);
            if (task != null) task.cancel();
        }
    }

    private void refreshCombat(Player player) {
        UUID uuid = player.getUniqueId();

        if (combatTasks.containsKey(uuid)) {
            BukkitTask task = combatTasks.get(uuid);
            if (task != null) task.cancel();
        } else {
            messageManager.sendMessage(player, "combat_start");
        }

        BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            combatTasks.remove(uuid);
            if (player.isOnline()) {
                messageManager.sendMessage(player, "combat_end");
            }
        }, 200L); // 10 seconds = 200 ticks

        combatTasks.put(uuid, task);
    }
}
