package hu.szatomi.lifesteal.Listeners;

import hu.szatomi.lifesteal.Colors;
import hu.szatomi.lifesteal.Lifesteal;
import net.kyori.adventure.text.Component;
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
    private final Map<UUID, BukkitTask> combatTasks = new HashMap<>();

    public CombatLogListener(Lifesteal plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;

        Player attacker = null;
        if (event.getDamager() instanceof Player p) {
            attacker = p;
        } else if (event.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player p) {
            attacker = p;
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
            combatTasks.get(uuid).cancel();
            combatTasks.remove(uuid);
            player.setHealth(0);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        UUID uuid = event.getEntity().getUniqueId();
        if (combatTasks.containsKey(uuid)) {
            combatTasks.get(uuid).cancel();
            combatTasks.remove(uuid);
        }
    }

    private void refreshCombat(Player player) {
        UUID uuid = player.getUniqueId();

        if (combatTasks.containsKey(uuid)) {
            combatTasks.get(uuid).cancel();
        } else {
            player.sendMessage(Component.text("Harcba kerültél!").color(Colors.RED));
        }

        BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            player.sendMessage(Component.text("Már nem vagy harcban!").color(Colors.GREEN));
            combatTasks.remove(uuid);
        }, 200L); // 10 seconds = 200 ticks

        combatTasks.put(uuid, task);
    }
}
